<template>
  <div class="layout-pd user-details">
    <el-card shadow="hover">
      <el-row :gutter="35">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="mb20">
          <header>
            <div class="personal-info">
              <el-avatar :size="88"
                         :src="state.user.headSculpture ? state.origin + state.user.headSculpture : defaultLogo"/>
              <div style=" margin: 4px 32px;">
                <div class="personal-name">
                  <span style="font-size: 18px;font-weight: bold">{{ state.user.name }}</span>
                </div>
                <div class="personal-account">
                  {{ $t('message.label.account') }}：{{ state.user.account }}
                </div>
              </div>
            </div>
            <div class="personal-operation" v-if="state.activeName == 'basic-info'">
              <el-button v-show="!state.disabled" type="primary" @click="submit" plain>
                {{ $t('message.formI18nOperation.save') }}
              </el-button>
              <el-button v-show="state.disabled" type="primary" @click="state.disabled = false" plain>
                {{ $t('message.formI18nOperation.editInfo') }}
              </el-button>
              <el-button type="primary" plain @click="resetPassword">{{
                  $t('message.formI18nOperation.resetPassword')
                }}
              </el-button>
            </div>
          </header>
          <section>
            <el-tabs v-model="state.activeName" class="style-tabs" @tab-click="handleClick">
              <el-tab-pane :label="$t('message.label.basicInfo')" name="basic-info">
                <InfoConfig v-if="state.activeName=='basic-info'" ref="InfoConfigRef" :user="state.user"
                            :disabled="state.disabled"
                            @refresh="update"></InfoConfig>
              </el-tab-pane>
              <el-tab-pane :label="$t('message.label.accountSecurity')" name="account-security">
                <SecurityConfig v-if="state.activeName=='account-security'" ref="SecurityConfigRef"></SecurityConfig>
              </el-tab-pane>
            </el-tabs>
          </section>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount} from 'vue';
import {ElMessageBox, ElMessage, TabsPaneContext, UploadProps, ElForm} from 'element-plus';
import {useRoute} from 'vue-router';
import {useUserApi} from "/@/api/hiwoo-base";
import {useI18n} from 'vue-i18n';
import defaultLogo from '/@/assets/user/default.png';
import {useUserInfo} from "/@/stores/userInfo";
import {storeToRefs} from "pinia";
import {Session} from "/@/utils/storage";
import NProgress from "nprogress";
import {NextLoading} from "/@/utils/loading";

// 引入组件
const InfoConfig = defineAsyncComponent(() => import('/@/views/user-info/tab-pane/basic-info.vue'));
const SecurityConfig = defineAsyncComponent(() => import('/@/views/user-info/tab-pane/account-security.vue'));

// 定义变量内容
const {t} = useI18n();
const route = useRoute();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const InfoConfigRef = ref();
const SecurityConfigRef = ref();
const baseUploadUrl = import.meta.env.VITE_API_SYSTEM_URL;

const state = reactive({
  uploadUrl: baseUploadUrl + "/system/oss/upload",
  activeName: "basic-info",
  user: userInfos.value,
  disabled: true,
  headSculptureUrl: '',
  origin: window.location.origin
});


// 页面加载前
onBeforeMount(() => {
});
// 页面加载时
onMounted(() => {
  NextLoading.done();
});

const resetPassword = () => {
  ElMessageBox.confirm('确定要重置密码吗？', t('message.tip.hint'), {
    confirmButtonText: t('message.tip.confirm'),
    cancelButtonText: t('message.tip.cancel'),
    type: 'warning',
  }).then(() => {
    useUserApi().resetPassword(state.user.id).then(response => {
      ElMessage.success("重置成功,密码初始化为:123456");
    })
  }).catch(() => {
  });
}

const handleClick = (tab: TabsPaneContext, event: Event) => {
  state.disabled = true;
}

const submit = () => {
  InfoConfigRef.value.saveUserInfo()
}

const update = () => {
  state.disabled = !state.disabled;
  state.user = userInfos.value;
}

</script>

<style scoped lang="scss">

.is-disabled {
  color: #c0c4cc;
  cursor: not-allowed;
  background-image: none;
  background-color: #f5f7fa;
  border-color: #dcdfe6;
}

.el-form {
  max-width: 400px;
  margin: 0px 0px;

  .el-form-item {
    margin-bottom: 24px;
  }

  .el-button {
    width: 100%;
  }

  .dialog-footer {
    text-align: right;
  }
}


.user-details {
  height: 100% !important;

  .el-card, {
    height: 100%;

    .el-row {
      height: 100%;
    }
  }

  header {
    background: rgba(24, 144, 255, 0.03);
    border-radius: 8px 8px 8px 8px;
    height: 120px;
    display: flex;
    justify-content: space-between;
    padding: 16px;
    margin-bottom: 8px;

    .personal-info {
      display: flex;

      .personal-name {
        display: flex;
        padding: 12px 0px;
      }

      .personal-account {
        padding: 12px 0px;
      }
    }
  }

  section {
    height: calc(100% - 115px);
    margin-top: 12px;
  }
}
</style>
