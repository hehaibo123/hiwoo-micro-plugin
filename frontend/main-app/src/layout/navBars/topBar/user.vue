<template>
  <div class="layout-navbars-breadcrumb-user pr15">
    <el-dropdown :show-timeout="70" :hide-timeout="50" trigger="click" @command="onLanguageChange">
      <div class="layout-navbars-breadcrumb-user-icon">
        <i
            class="iconfont"
            :class="state.disabledI18n === 'en' ? 'icon-fuhao-yingwen' : 'icon-fuhao-zhongwen'"
            :title="$t('message.user.title1')"
        ></i>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="zh-cn" :disabled="state.disabledI18n === 'zh-cn'">简体中文</el-dropdown-item>
          <el-dropdown-item command="en" :disabled="state.disabledI18n === 'en'">English</el-dropdown-item>
          <!--          <el-dropdown-item command="zh-tw" :disabled="state.disabledI18n === 'zh-tw'">繁體中文</el-dropdown-item>-->
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <!--    <div class="layout-navbars-breadcrumb-user-icon" @click="onSearchClick">-->
    <!--      <el-icon :title="$t('message.user.title2')">-->
    <!--        <ele-Search/>-->
    <!--      </el-icon>-->
    <!--    </div>-->
    <!--    <div class="layout-navbars-breadcrumb-user-icon" @click="onLayoutSetingClick">-->
    <!--      <i class="icon-skin iconfont" :title="$t('message.user.title3')"></i>-->
    <!--    </div>-->

    <!--    <el-popover-->
    <!--        ref="userNewsRef"-->
    <!--        :virtual-ref="userNewsBadgeRef"-->
    <!--        placement="bottom"-->
    <!--        trigger="click"-->
    <!--        transition="el-zoom-in-top"-->
    <!--        virtual-triggering-->
    <!--        :width="300"-->
    <!--        :persistent="false"-->
    <!--    >-->
    <!--            <UserNews/>-->
    <!--    </el-popover>-->
    <div class="layout-navbars-breadcrumb-user-icon" @click="onScreenfullClick">
      <i
          class="iconfont"
          :title="state.isScreenfull ? $t('message.user.title6') : $t('message.user.title5')"
          :class="!state.isScreenfull ? 'icon-fullscreen' : 'icon-tuichuquanping'"
      ></i>
    </div>
    <div class="layout-navbars-breadcrumb-user-icon mr20" ref="userNewsBadgeRef" @click="onUserNewsClick">
      <el-badge :hidden="state.messageCount == 0" :value="state.messageCount" :max="99">
        <el-icon :title="$t('message.user.title4')">
          <ele-Bell/>
        </el-icon>
        <el-tag v-if="!state.readFlag" color="#FFB8BB">NEW</el-tag>
      </el-badge>
    </div>
    <el-dropdown :show-timeout="70" :hide-timeout="50" @command="onHandleCommandClick">
			<span class="layout-navbars-breadcrumb-user-link">
				<img :src="userInfos.headSculpture ? userInfos.headSculpture : defaultLogo"
             class="layout-navbars-breadcrumb-user-link-photo mr5"/>
				<el-icon class="el-icon--right">
					<ele-ArrowDown/>
				</el-icon>
			</span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item :icon="SwitchButton" divided command="logOut">{{
              $t('message.user.dropdown5')
            }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <!--    <Search ref="searchRef"/>-->
  </div>
  <Drawer ref="DrawerRef"></Drawer>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, unref, computed, reactive, onMounted, watch, onBeforeUnmount} from 'vue';

import {useRouter} from 'vue-router';
import {ElMessageBox, ElMessage, ClickOutside as vClickOutside} from 'element-plus';
import screenfull from 'screenfull';
import {useI18n} from 'vue-i18n';
import {storeToRefs} from 'pinia';
import {useUserInfo} from '/@/stores/userInfo';
import {useThemeConfig} from '/@/stores/themeConfig';
import other from '/@/utils/other';
import mittBus from '/@/utils/mitt';
import {Session, Local} from '/@/utils/storage';
import defaultLogo from '/@/assets/user/default.png';
import {useLoginApi} from '/@/api/login';
import {useSystemApi} from '/src/api/hiwoo-base';
import {User, Operation, Setting, SwitchButton} from '@element-plus/icons-vue'
import {useRoutesList} from "/@/stores/routesList";
// 引入组件
// const UserNews = defineAsyncComponent(() => import('/@/layout/navBars/topBar/userNews.vue'));
const Search = defineAsyncComponent(() => import('/@/layout/navBars/topBar/search.vue'));
const Drawer = defineAsyncComponent(() => import('/@/layout/navBars/topBar/drawer.vue'));

// 定义变量内容
const userNewsRef = ref();
const userNewsBadgeRef = ref();
const DrawerRef = ref();
const {locale, t} = useI18n();
const router = useRouter();
const routeStores = useRoutesList();
const {routesList} = storeToRefs(routeStores);
const stores = useUserInfo();
const storesThemeConfig = useThemeConfig();
const {userInfos} = storeToRefs(stores);
const {themeConfig} = storeToRefs(storesThemeConfig);
const searchRef = ref();
const state = reactive({
  loading: false,
  time: 2000,
  status: 0,
  timer: null as ReturnType<typeof setInterval> | null,
  syncTimer: null as ReturnType<typeof setInterval> | null,
  user: {
    account: '',
    client: 'PC',
    ip: '',
    address: '',
  },
  query: {
    keyword: '',
    userId: userInfos.value.id,
    type: 1,
    readStatus: 0,
  },
  expiredDialog: false,
  isScreenfull: false,
  disabledI18n: 'zh-cn',
  disabledSize: 'default',
  messageCount: 0,
  readFlag: true,
});

// 设置分割样式
const layoutUserFlexNum = computed(() => {
  let num: string | number = '';
  const {layout, isClassicSplitMenu} = themeConfig.value;
  const layoutArr: string[] = ['defaults', 'columns'];
  if (layoutArr.includes(layout) || (layout === 'classic' && !isClassicSplitMenu)) num = '1';
  else num = '';
  return num;
});
// 全屏点击时
const onScreenfullClick = () => {
  if (!screenfull.isEnabled) {
    ElMessage.warning('暂不不支持全屏');
    return false;
  }
  screenfull.toggle();
  screenfull.on('change', () => {
    if (screenfull.isFullscreen) state.isScreenfull = true;
    else state.isScreenfull = false;
  });
};
// 消息通知点击时
const onUserNewsClick = () => {
  // unref(userNewsRef).popperRef?.delayHide?.();
  DrawerRef.value.changeDrawer();
};
const getMessages = () => {
  useLoginApi().getMessages(state.query.readStatus).then((response) => {
    if (response.code == 200) {
      state.messageCount = response.data.length;
    }
  });
};

// 布局配置 icon 点击时
const onLayoutSetingClick = () => {
  mittBus.emit('openSetingsDrawer');
};
// 下拉菜单点击时
const onHandleCommandClick = (path: string) => {
  if (path === 'logOut') {
    ElMessageBox({
      closeOnClickModal: false,
      closeOnPressEscape: false,
      title: t('message.user.logOutTitle'),
      message: t('message.user.logOutMessage'),
      confirmButtonText: t('message.user.logOutConfirm'),
      cancelButtonText: t('message.user.logOutCancel'),
      buttonSize: 'default',
      beforeClose: (action, instance, done) => {
        if (action === 'confirm') {
          state.user.account = userInfos.value.account;
          useLoginApi()
              .signOut(state.user)
              .then((response) => {
                instance.confirmButtonLoading = true;
                instance.confirmButtonText = t('message.user.logOutExit');
                setTimeout(() => {
                  done();
                  setTimeout(() => {
                    instance.confirmButtonLoading = false;
                  }, 300);
                }, 700);
              });
        } else {
          done();
        }
      },
    }).then(async () => {
      // 清除缓存/token等
      Session.clear();
      Local.clear();
      // 使用 reload 时，不需要调用 resetRoute() 重置路由
      window.location.reload();
    })
        .catch(() => {
        });
  } else {
    router.push(path);
  }
};
// 菜单搜索点击
const onSearchClick = () => {
  searchRef.value.openSearch();
};
// 组件大小改变
const onComponentSizeChange = (size: string) => {
  Local.remove('themeConfig');
  themeConfig.value.globalComponentSize = size;
  Local.set('themeConfig', themeConfig.value);
  initI18nOrSize('globalComponentSize', 'disabledSize');
  window.location.reload();
};
// 语言切换
const onLanguageChange = (lang: string) => {
  Local.remove('themeConfig');
  themeConfig.value.globalI18n = lang;
  Local.set('themeConfig', themeConfig.value);
  locale.value = lang;
  other.useTitle();
  initI18nOrSize('globalI18n', 'disabledI18n');
  window.location.reload();
};
// 初始化组件大小/i18n
const initI18nOrSize = (value: string, attr: string) => {
  (<any>state)[attr] = Local.get('themeConfig')[value];
};
// 页面加载时
onMounted(() => {
  if (Local.get('themeConfig')) {
    initI18nOrSize('globalComponentSize', 'disabledSize');
    initI18nOrSize('globalI18n', 'disabledI18n');
  }
  getMessages();

});

const lastServerTime = ref(0);
const countdown = () => {
  stopCountdownTimer();
  stopSyncTimer();
  // 1. 初始化获取服务器时间
  fetchServerTime();

  // 2. 建立本地计时器（仅作显示用）
  state.timer = window.setInterval(() => {
    if (state.time > 0) {
      state.time--;
    } else {
      stopCountdownTimer();
    }
  }, 1000);

  // 4. 每5分钟强制同步服务器时间
  state.syncTimer = window.setInterval(fetchServerTime, 5 * 60 * 1000);
};

// 获取服务器时间（独立函数）
const fetchServerTime = async () => {
  try {
    const response = await useSystemApi().getExpireTime();
    lastServerTime.value = Date.now();
    state.time = response.data;
    if (response.data === -2) state.status = 1;
  } catch (error) {
    console.error(' 同步服务器时间失败:', error);
  }
};
const stopCountdownTimer = () => {
  if (state.timer) {
    clearInterval(state.timer);
    state.timer = null;
  }
};
const stopSyncTimer = () => {
  if (state.syncTimer) {
    clearInterval(state.syncTimer);
    state.syncTimer = null;
  }
};

watch(
    () => state.time,
    (newValue) => {
      if (newValue === 0) {
      } else if (newValue === -1) {
        state.expiredDialog = false;
      }
    },
    {immediate: false}
);
onBeforeUnmount(() => {
  stopCountdownTimer();
  stopSyncTimer();
});
const formatTime = (time: number) => {
  const hours = Math.floor(time / 3600);
  const minutes = Math.floor((time % 3600) / 60);
  const seconds = time % 60;
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-user {
  display: flex;
  align-items: center;
  justify-content: flex-end;

  &-link {
    height: 100%;
    display: flex;
    align-items: center;
    white-space: nowrap;

    &-photo {
      width: 25px;
      height: 25px;
      border-radius: 100%;
    }
  }

  &-icon {
    padding: 0 10px;
    cursor: pointer;
    color: var(--next-bg-topBarColor);
    height: 50px;
    line-height: 50px;
    display: flex;
    align-items: center;

    &:hover {
      background: var(--next-color-user-hover);

      i {
        display: inline-block;
        animation: logoAnimation 0.3s ease-in-out;
      }
    }
  }

  :deep(.el-dropdown) {
    color: var(--next-bg-topBarColor);
  }

  :deep(.el-badge) {
    height: 40px;
    line-height: 40px;
    display: flex;
    align-items: center;
  }

  :deep(.el-badge__content.is-fixed) {
    top: 12px;
  }
}
</style>
