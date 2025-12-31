<template>
  <div class="upgrade-dialog">
    <el-dialog
        v-model="state.isUpgrade"
        width="500"
        destroy-on-close
        :show-close="false"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
      <div class="upgrade-title">
        <div class="upgrade-title-warp">
          <span class="upgrade-title-warp-txt"> {{ state.data.title }}</span>
          <!--          <span class="upgrade-title-warp-version">v{{ state.version }}</span>-->
        </div>
      </div>
      <div class="upgrade-content">
        <!--        {{ getThemeConfig.globalTitle }} {{ state.data.title }}-->
        <div class="upgrade-content-desc mt5 notice-content" v-html="state.data.content"></div>
      </div>
      <div class="upgrade-btn">
        <el-button type="primary" round size="default" @click="onUpgrade" :loading="state.isLoading">已读
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="layoutUpgrade">
import {reactive, computed, onMounted, watch} from 'vue';
import {useI18n} from 'vue-i18n';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import {Local} from '/@/utils/storage';
import {useMenuApi} from "/@/api/menu";
import {useUserInfo} from '/@/stores/userInfo';
import router from "/@/router";
import {useRoute} from "vue-router";

const route = useRoute();

// 定义变量内容
const {t} = useI18n();
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);

const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  isUpgrade: false,
  // @ts-ignore
  version: __NEXT_VERSION__,
  isLoading: false,
  btnTxt: '',
  isFirstOpen: true,
  //公告信息
  data: {}
});

// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfig.value;
});
// 残忍拒绝
const onCancel = () => {
  state.isUpgrade = false;
};
// 马上更新
const onUpgrade = () => {
  //公告已读接口
  useMenuApi().readAnnouncement({userId: userInfos.value.id, announcementId: state.data.id}).then(res => {
    console.log('公告已读res', res);
    state.isUpgrade = false;
  });
};
// 延迟显示，防止刷新时界面显示太快
const delayShow = () => {
  setTimeout(() => {
    if (state.isFirstOpen) {
      state.isUpgrade = true;
    }
  }, 2000);
};

watch(() => router.currentRoute.value.path, (newVal, oldVal) => {
  console.log("newVal:" + newVal)

  if (newVal != '/login' && oldVal === '/login') {

    setTimeout(() => {
      state.btnTxt = t('message.upgrade.btnTwo');
    }, 200);
    //查询是否展示公告
    useMenuApi().getAnnouncement().then( // 获取公告
        res => {
          console.log('获取公告res', res);
          if (res.data) {
            state.data = res.data;
            if ((state.data.receiverType == 'tenant' && userInfos.value.roleId == 1) || state.data.receiverType != 'tenant') {
              delayShow();
            }
          }
        });
  }

});
/**
 * 如果是未认证用户首次登录不打开通知页面
 */
const firstInPage = () => {
  state.isFirstOpen = false;
}

// 页面加载时
onMounted(() => {
});
/**
 * 暴漏方法
 */
defineExpose({
  firstInPage,
});
</script>

<style scoped lang="scss">
.upgrade-dialog {
  :deep(.el-dialog) {
    .el-dialog__body {
      padding: 0 !important;
    }

    .el-dialog__header {
      display: none !important;
    }

    .upgrade-title {
      text-align: center;
      height: 130px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        background-color: var(--el-color-primary-light-1);
        width: 130%;
        height: 130px;
        border-bottom-left-radius: 100%;
        border-bottom-right-radius: 100%;
      }

      .upgrade-title-warp {
        z-index: 1;
        position: relative;

        .upgrade-title-warp-txt {
          color: var(--next-color-white);
          font-size: 22px;
          letter-spacing: 3px;
        }

        .upgrade-title-warp-version {
          color: var(--next-color-white);
          background-color: var(--el-color-primary-light-4);
          font-size: 12px;
          position: absolute;
          display: flex;
          top: -2px;
          right: -50px;
          padding: 2px 4px;
          border-radius: 2px;
        }
      }
    }

    .upgrade-content {
      padding: 20px;
      line-height: 22px;

      .upgrade-content-desc {
        color: var(--el-color-info-light-4);
        font-size: 12px;

        img {
          max-width: 455px;
        }
      }
    }

    .upgrade-btn {
      border-top: 1px solid var(--el-border-color-lighter, #ebeef5);
      display: flex;
      justify-content: space-around;
      padding: 15px 20px;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>
