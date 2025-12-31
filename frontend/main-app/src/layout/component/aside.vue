<template>
  <div class="h100" v-show="!isTagsViewCurrenFull">
    <el-aside class="layout-aside" :class="setCollapseStyle">
      <Logo v-if="setShowLogo"/>
      <el-scrollbar class="flex-auto" ref="layoutAsideScrollbarRef" @mouseenter="onAsideEnterLeave(true)"
                    @mouseleave="onAsideEnterLeave(false)">
        <Vertical :menuList="state.menuList"/>


      </el-scrollbar>
    </el-aside>
    <el-dialog
        v-model="state.dialogVisible"
        title="远程访问"
        width="500px"
        :close-on-click-modal="false"
    >
      <div class="remote-access-content">
        <el-input v-model="remoteAccessUrl" readonly>
          <template #append>
            <el-button
                type="primary"
                @click="copyUrl"
                :icon="DocumentCopy"
            >复制
            </el-button>
          </template>
        </el-input>
        <div class="tip">
          <el-icon>
            <Warning/>
          </el-icon>
          <span>请确保该地址可通过网络访问</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="state.dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="openRemote">直接打开</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="layoutAside">
import {defineAsyncComponent, reactive, computed, watch, onBeforeMount, ref, onMounted} from 'vue';
import {storeToRefs} from 'pinia';
import {useRoutesList} from '/@/stores/routesList';
import {useThemeConfig} from '/@/stores/themeConfig';
import {useTagsViewRoutes} from '/@/stores/tagsViewRoutes';
import mittBus from '/@/utils/mitt';
import demo from '/@/assets/user/demo.png';
import {useUserInfo} from "/@/stores/userInfo";
import {Session} from "/@/utils/storage";

// 引入组件
const Logo = defineAsyncComponent(() => import('/@/layout/logo/index.vue'));
const Vertical = defineAsyncComponent(() => import('/@/layout/navMenu/vertical.vue'));
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
// 定义变量内容
const layoutAsideScrollbarRef = ref();
const stores = useRoutesList();
const storesThemeConfig = useThemeConfig();
const storesTagsViewRoutes = useTagsViewRoutes();
const {routesList} = storeToRefs(stores);
const {themeConfig} = storeToRefs(storesThemeConfig);
import {config} from '/public/config.js'
import {DocumentCopy, Paperclip} from '@element-plus/icons-vue';
import os from 'os';
import {ElMessage} from 'element-plus';
import commonFunction from '/@/utils/commonFunction';

const {isTagsViewCurrenFull} = storeToRefs(storesTagsViewRoutes);
const state = reactive({
  menuList: [],
  clientWidth: 0,
  data: {},
  ip: '',
  dialogVisible: false,
  remoteAccessUrl: '',
});
onMounted(() => {

});


const showRemoteDialog = () => {
  if (!state.ip || state.ip === '127.0.0.1') {
    ElMessage.warning(' 远程访问地址未正确获取');
    return;
  }
  state.dialogVisible = true;
};
const {copyText} = commonFunction();

const copyUrl = () => {

  copyText(remoteAccessUrl.value)

};

const openRemote = () => {
  if (window.electron) {
    window.electron.ipcRenderer.invoke('openWeb', remoteAccessUrl.value)
  } else {
    window.open(remoteAccessUrl.value, '_blank', 'noopener,noreferrer');
  }
  state.dialogVisible = false;
};
const remoteAccessUrl = computed(() => {
  const port = window.location.port ? `:${window.location.port}` : '';
  return window.location.protocol + '//' + `${state.ip}${port}`;
});

// 设置菜单展开/收起时的宽度
const setCollapseStyle = computed(() => {
  const {layout, isCollapse, menuBar} = themeConfig.value;
  const asideBrTheme = ['#FFFFFF', '#FFF', '#fff', '#ffffff'];
  const asideBrColor = asideBrTheme.includes(menuBar) ? 'layout-el-aside-br-color' : '';
  // 判断是否是手机端
  if (state.clientWidth <= 1000) {
    if (isCollapse) {
      document.body.setAttribute('class', 'el-popup-parent--hidden');
      const asideEle = document.querySelector('.layout-container') as HTMLElement;
      const modeDivs = document.createElement('div');
      modeDivs.setAttribute('class', 'layout-aside-mobile-mode');
      asideEle.appendChild(modeDivs);
      modeDivs.addEventListener('click', closeLayoutAsideMobileMode);
      return [asideBrColor, 'layout-aside-mobile', 'layout-aside-mobile-open'];
    } else {
      // 关闭弹窗
      closeLayoutAsideMobileMode();
      return [asideBrColor, 'layout-aside-mobile', 'layout-aside-mobile-close'];
    }
  } else {
    if (layout === 'columns' || layout === 'classic') {
      // 分栏布局、经典布局，菜单收起时宽度给 1px，防止切换动画消失
      if (isCollapse) return [asideBrColor, 'layout-aside-pc-1'];
      else return [asideBrColor, 'layout-aside-pc-192'];
    } else {
      // 其它布局给 64px
      if (isCollapse) return [asideBrColor, 'layout-aside-pc-64'];
      else return [asideBrColor, 'layout-aside-pc-192'];
    }
  }
});
// 设置显示/隐藏 logo
const setShowLogo = computed(() => {
  let {layout, isShowLogo} = themeConfig.value;
  return (isShowLogo && layout === 'defaults') || (isShowLogo && layout === 'columns');
});
// 关闭移动端蒙版
const closeLayoutAsideMobileMode = () => {
  const el = document.querySelector('.layout-aside-mobile-mode');
  el?.setAttribute('style', 'animation: error-img-two 0.3s');
  setTimeout(() => {
    el?.parentNode?.removeChild(el);
  }, 300);
  const clientWidth = document.body.clientWidth;
  if (clientWidth < 1000) themeConfig.value.isCollapse = false;
  document.body.setAttribute('class', '');
};
// 设置/过滤路由（非静态路由/是否显示在菜单中）
const setFilterRoutes = () => {
  if (themeConfig.value.layout === 'columns') return false;
  state.menuList = filterRoutesFun(routesList.value);
};
// 路由过滤递归函数
const filterRoutesFun = <T extends RouteItem>(arr: T[]): T[] => {
  return arr
      .filter((item: T) => !item.hide)
      .map((item: T) => {
        item = Object.assign({}, item);
        if (item.children) item.children = filterRoutesFun(item.children);
        return item;
      });
};
// 设置菜单导航是否固定（移动端）
const initMenuFixed = (clientWidth: number) => {
  state.clientWidth = clientWidth;
};
// 鼠标移入、移出
const onAsideEnterLeave = (bool: Boolean) => {
  let {layout} = themeConfig.value;
  if (layout !== 'columns') return false;
  if (!bool) mittBus.emit('restoreDefault');
  // 开启 `分栏菜单鼠标悬停预加载` 才设置，防止 columnsAside.vue 监听 pinia.state
  if (themeConfig.value.isColumnsMenuHoverPreload) stores.setColumnsMenuHover(bool);
};
// 页面加载前
onBeforeMount(() => {
  initMenuFixed(document.body.clientWidth);
  setFilterRoutes();
  // 此界面不需要取消监听(mittBus.off('setSendColumnsChildren))
  // 因为切换布局时有的监听需要使用，取消了监听，某些操作将不生效
  mittBus.on('setSendColumnsChildren', (res: MittMenu) => {
    state.menuList = res.children;
  });
  // 开启经典布局分割菜单时，设置菜单数据
  mittBus.on('setSendClassicChildren', (res: MittMenu) => {
    let {layout, isClassicSplitMenu} = themeConfig.value;
    if (layout === 'classic' && isClassicSplitMenu) {
      // 经典布局分割菜单只要一项子级时，收起左侧导航菜单
      res.children.length <= 1 ? (themeConfig.value.isCollapse = true) : (themeConfig.value.isCollapse = false);
      state.menuList = [];
      state.menuList = res.children;
    }
  });
  // 开启经典布局分割菜单时，重新处理菜单数据
  mittBus.on('getBreadcrumbIndexSetFilterRoutes', () => {
    setFilterRoutes();
  });
  // 监听窗口大小改变时(适配移动端)
  mittBus.on('layoutMobileResize', (res: LayoutMobileResize) => {
    initMenuFixed(res.clientWidth);
    closeLayoutAsideMobileMode();
  });
});
// 监听 themeConfig 配置文件的变化，更新菜单 el-scrollbar 的高度
watch(
    () => [themeConfig.value.isShowLogoChange, themeConfig.value.isShowLogo, themeConfig.value.layout, themeConfig.value.isClassicSplitMenu],
    ([isShowLogoChange, isShowLogo, layout, isClassicSplitMenu]) => {
      if (isShowLogoChange !== isShowLogo) {
        if (layoutAsideScrollbarRef.value) layoutAsideScrollbarRef.value.update();
      }
      if (layout === 'classic' && isClassicSplitMenu) return false;
    }
);
// 监听用户权限切换，用于演示 `权限管理 -> 前端控制 -> 页面权限` 权限切换不生效
watch(
    () => routesList.value,
    () => {
      setFilterRoutes();
    }
);
// 通用的打开系统函数
const openSystem = (url: string) => {
  // 获取存储的 Token 和 用户信息
  const token = Session.get('satoken');
  const userInfo = Session.get('userInfo');

  // 序列化用户信息为 JSON 字符串
  const serializedUserInfo = encodeURIComponent(JSON.stringify(userInfo));

  // 构建 URL 查询参数
  const fullUrl = new URL(url);
  fullUrl.searchParams.append('userInfo', serializedUserInfo || '');
  fullUrl.searchParams.append('token', token || '');

  // 打开新窗口
  window.open(fullUrl.toString(), '_blank');
};

</script>

<style lang="scss" scoped>
/* 设置整个行的背景色和光标样式 */
.hoverable {
  margin: 12px 8px 12px;
  padding: 10px; /* 添加内边距确保背景色覆盖内容 */
  display: flex;
  align-items: center;
  justify-content: space-between; /* 在左右两侧对齐内容 */
  cursor: pointer; /* 鼠标悬停时显示手型光标 */
  position: relative; /* 用于伪元素定位 */
  border-radius: 4px; /* 可选：添加圆角效果 */
  transition: background-color 0.3s; /* 平滑过渡效果 */
}

.left-content {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
}

.icon, .text, .arrow {
  color: #1890FF; /* 默认颜色 */
}

.hoverable:hover {
  background-color: rgba(24, 144, 255, 0.05); /* 鼠标悬停时的背景色（包含透明度） */
}

.hoverable:hover .icon, .hoverable:hover .text, .hoverable:hover .arrow {
  color: #1890FF; /* 鼠标悬停时的颜色 */
}

.remote-access-content {
  padding: 0 20px;

  .tip {
    margin-top: 15px;
    color: var(--el-color-warning);
    display: flex;
    align-items: center;

    .el-icon {
      margin-right: 5px;
    }
  }
}
</style>
