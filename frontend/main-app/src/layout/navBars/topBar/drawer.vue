<template>
  <div class="message-box-drawer">
    <el-drawer v-model="state.drawer" :show-close="false" size="30%" style="min-width: 576px">
      <template #header="{ close }">
        <div style="padding: 24px 9px;display: flex;justify-content: space-between;">
          <h2>系统消息</h2>
          <el-icon size="20" color="#999999" style="cursor: pointer" @click="cancel">
            <Close/>
          </el-icon>
        </div>
      </template>
      <template #default>
        <div>
          <header>
            <el-form-item label="消息类型:">
              <el-select text v-model="state.query.type" style="width: 130px;" @change="changeType">
                <el-option v-for="item in state.messageType" :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
            <el-form-item label="状态:" style="margin-left: 16px">
              <el-select text v-model="state.query.readStatus" style="width: 130px" @change="changeStatus">
                <el-option v-for="item in state.readStatus" :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
          </header>
          <ul v-if="state.messages && state.messages.length > 0">
            <li v-for="item in state.messages">
              <div style="display:flex; justify-content: space-between;margin-top: 24px">
                <div style="display: flex;">
                  <h3 style="margin-left: 0px">{{ item.title }}</h3>
                  <el-tag v-if="item.type == 0" class="mr1" style="margin-left: 12px">通知消息</el-tag>
                  <el-tag v-if="item.type == 1" class="mr1" style="margin-left: 12px">公告消息</el-tag>
                </div>
                <el-button v-if="state.query.readStatus == 0" type="primary" @click="readOne(item)">标记已读</el-button>
              </div>
              <p @click="toggleContent(item.id)" style="cursor: pointer">
                <div v-html="getContentDisplay(item)"></div>
                <p><span class="time">{{ item.releaseTime }}</span></p>
              </p>
            </li>
          </ul>
          <el-empty v-else description="暂无消息" style="height: calc(100% - 200px);"/>
        </div>
      </template>
      <template #footer>
        <div style="flex: auto;">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="allRead">全部已读</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="userManage">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount} from 'vue';
import {ElMessageBox, ElMessage, TabsPaneContext} from 'element-plus';
import {Search, Download, Plus, MessageBox, Close, Message} from '@element-plus/icons-vue';
import {useRouter} from 'vue-router';
import {useUserInfo} from "/@/stores/userInfo";
import {storeToRefs} from "pinia";
import {useI18n} from 'vue-i18n';
import {useLoginApi} from "/@/api/login";
import {useMenuApi} from "/@/api/menu";

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);
const {t} = useI18n();

// 引入组件

// 定义变量内容
const router = useRouter();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  drawer: false,
  query: {
    keyword: '',
    userId: userInfos.value.id,
    type: 0,
    readStatus: 0,
  },
  readStatus: [
    {
      id: 1,
      name: "已读"
    },
    {
      id: 0,
      name: "未读"
    },
  ],
  messageType: [
    {
      id: 0,
      name: "系统通知"
    },
    {
      id: 1,
      name: "系统公告"
    },
  ],
  message: {} as any,
  messages: [] as any,
  contentExpanded: {} as { [key: string]: boolean }, // 用于记录每个消息内容的展开状态
});

const MAX_CONTENT_LENGTH = 200; // 定义最大显示长度，可按需调整

// 获取要显示的内容（缩略或完整）
const getContentDisplay = (item: any) => {
  if (state.contentExpanded[item.id]) {
    return item.content;
  }
  const truncatedContent = item.content.length > MAX_CONTENT_LENGTH ? item.content.slice(0, MAX_CONTENT_LENGTH) + '...' : item.content;
  return truncatedContent;
};

// 切换消息内容展开与收起状态
const toggleContent = (id: string) => {
  state.contentExpanded[id] = !state.contentExpanded[id];
};

// 页面加载前
onBeforeMount(() => {
});
// 页面加载时
onMounted(() => {
});


const handleClick = (tab: TabsPaneContext, event: Event) => {
  // console.log(tab, event)
}
const changeStatus = () => {
  changeType();
}

const changeType = () => {
  switch (state.query.type) {
    case 0:
      getMessages();
      break;
    case 1:
      getAnnouncements();
      break;
  }
}

const changeDrawer = () => {
  state.drawer = !state.drawer;
  if (state.drawer) {
    getMessages();
  }
}
const getMessages = () => {
  useLoginApi().getMessages(state.query.readStatus).then(response => {
    if (response.code == 200) {
      state.messages = response.data || [];
      // 初始化所有消息内容为收起状态
      state.messages.forEach(message => {
        state.contentExpanded[message.id] = false;
      });
    } else {
      ElMessage.warning(response.msg);
    }
  })
}

const getAnnouncements = () => {
  useMenuApi().getAnnouncements(state.query.readStatus).then(response => {
    if (response.code == 200) {
      state.messages = response.data || [];
      // 初始化所有消息内容为收起状态
      state.messages.forEach(message => {
        state.contentExpanded[message.id] = false;
      });
    } else {
      ElMessage.warning(response.msg);
    }
  })
}

const allRead = async () => {
  switch (state.query.type) {
    case 0:
      const response = useLoginApi().allRead();
      if (response.code === 200) {
        getMessages();
      } else {
        ElMessage.warning(response.msg);
      }
      break;
    case 1:
      useMenuApi().allRead().then(response => {
        if (response.code == 200) {
          getAnnouncements();
        } else {
          ElMessage.warning(response.msg);
        }
      })
      break;
  }
};
const readOne = (item: any) => {
  switch (state.query.type) {
    case 0:
      useLoginApi().readOne({
        userId: userInfos.value.id,
        ids: item.id
      }).then(response => {
        if (response.code == 200) {
          getMessages();
        } else {
          ElMessage.warning(response.msg);
        }
      })
      break;
    case 1:
      useMenuApi().readAnnouncement({
        userId: userInfos.value.id,
        announcementId: item.id
      }).then(response => {
        if (response.code == 200) {
          getAnnouncements();
        } else {
          ElMessage.warning(response.msg);
        }
      })
      break;
  }
}

const cancel = () => {
  state.drawer = false;
}

// 暴露变量
defineExpose({
  changeDrawer
});

</script>

<style scoped lang="scss">
.message-box-drawer {
  header {
    display: flex;
  }

  ul {
    height: calc(100% - 50px);
    overflow-y: auto;
    margin-top: -16px;

    li {
      list-style: none;
      border-bottom: 1px #E5E5E5 dotted;
      position: relative;

      h3 {
        margin-left: 8px;
        line-height: 25px;
      }

      .time {
        position: absolute; /* 添加这行来确保 right 属性有效 */
        width: auto; /* 修改或确认宽度设置为 auto 或具体值 */
        right: 20px;
        color: #666666;
      }

      p {
        margin-top: 17px;
        color: #666666;
        margin-bottom: 48px;
        line-height: 21px;
        width: 97%;
      }
    }
  }

  .style-tabs {
    margin-top: -12px;
  }

}

</style>