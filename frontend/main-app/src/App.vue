<template>
  <el-config-provider :size="getGlobalComponentSize" :locale="getGlobalI18n">
    <router-view v-show="setLockScreen"/>
    <LockScreen v-if="themeConfig.isLockScreen"/>
    <Setings ref="setingsRef" v-show="setLockScreen"/>
    <CloseFull v-if="!themeConfig.isLockScreen"/>
    <Upgrade ref="upgradeRef"/>
    <!--    		<Sponsors />-->
  </el-config-provider>
</template>

<script setup lang="ts" name="main-app">
import {
  defineAsyncComponent,
  computed,
  ref,
  onBeforeMount,
  onMounted,
  onUnmounted,
  nextTick,
  watch,
  reactive
} from 'vue';
import {useRoute} from 'vue-router';
import {useI18n} from 'vue-i18n';
import {storeToRefs} from 'pinia';
import {useTagsViewRoutes} from '/@/stores/tagsViewRoutes';
import {useThemeConfig} from '/@/stores/themeConfig';
import other from '/@/utils/other';
import {Local, Session} from '/@/utils/storage';
import mittBus from '/@/utils/mitt';
import setIntroduction from '/@/utils/setIconfont';
import {useChangeColor} from '/@/utils/theme';
import {useUserInfo} from "/@/stores/userInfo";
import {useLoginApi} from "/@/api/login";

const {getLightColor, getDarkColor} = useChangeColor();

// 引入组件
const LockScreen = defineAsyncComponent(() => import('/@/layout/lockScreen/index.vue'));
const Setings = defineAsyncComponent(() => import('/@/layout/navBars/topBar/setings.vue'));
const CloseFull = defineAsyncComponent(() => import('/@/layout/navBars/topBar/closeFull.vue'));
const Upgrade = defineAsyncComponent(() => import('/@/layout/upgrade/index.vue'));
const Sponsors = defineAsyncComponent(() => import('/@/layout/sponsors/index.vue'));

// 定义变量内容
const {messages, locale} = useI18n();
const setingsRef = ref();
const upgradeRef = ref();
const route = useRoute();
const stores = useTagsViewRoutes();
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  status: false,
})
// 设置锁屏时组件显示隐藏
const setLockScreen = computed(() => {
  // 防止锁屏后，刷新出现不相关界面
  // https://gitee.com/lyt-top/vue-next-admin/issues/I6AF8P
  return themeConfig.value.isLockScreen ? themeConfig.value.lockScreenTime > 1 : themeConfig.value.lockScreenTime >= 0;
});
// 获取版本号
const getVersion = computed(() => {
  let isVersion = false;
  if (route.path !== '/login') {
    // @ts-ignore
    if ((Local.get('version') && Local.get('version') !== __NEXT_VERSION__) || !Local.get('version')) isVersion = true;
  }
  return isVersion;
});

//获取公告信息 查询是否能够获取到
const haveAnnouncement = ref(false);


// 获取全局组件大小
const getGlobalComponentSize = computed(() => {
  return other.globalComponentSize();
});
// 获取全局 i18n
const getGlobalI18n = computed(() => {
  return messages.value[locale.value];
});
// 设置初始化，防止刷新时恢复默认
onBeforeMount(() => {
  // 设置批量第三方 icon 图标
  setIntroduction.cssCdn();
  // 设置批量第三方 js
  setIntroduction.jsCdn();
});
// 页面加载时
onMounted(() => {
  nextTick(() => {
    // 监听布局配'置弹窗点击打开
    mittBus.on('openSetingsDrawer', () => {
      setingsRef.value.openDrawer();
    });
    // 获取缓存中的布局配置
    if (Local.get('themeConfig')) {
      storesThemeConfig.setThemeConfig({themeConfig: Local.get('themeConfig')});
      document.documentElement.style.cssText = Local.get('themeConfigStyle');
    }
    // 获取缓存中的全屏配置
    if (Session.get('isTagsViewCurrenFull')) {
      stores.setCurrenFullscreen(Session.get('isTagsViewCurrenFull'));
    }
    //首先查询是否登录
    /*    useMenuApi().checkLogin().then(
            res => {
              //判断当前路由是否为 /login 如果是则不展示公告
              if (route.path === '/login' || route.path === '/') {
                haveAnnouncement.value = false;
              } else if (res.msg === 'true') {
                //查询是否展示公告
                useMenuApi().getAnnouncement().then( // 获取公告
                    res => {
                      console.log('获取公告res', res);
                      haveAnnouncement.value = (res.data != "");
                    });
              }//清楚全部缓存
              else {
                Local.clear();
                Session.clear();
              }
            });*/
  });
});

//首次进入界面如何是未认证用户不展示通知消息
const noticeCloseFun = () => {
  //页面通知弹框不打开
  upgradeRef.value.firstInPage();
}
// 页面销毁时，关闭监听布局配置/i18n监听
onUnmounted(() => {
  mittBus.off('openSetingsDrawer', () => {
  });
});
// 监听路由的变化，设置网站标题
watch(
    () => route.path,
    () => {
      other.useTitle();
    },
    {
      deep: true,
    }
);
</script>
