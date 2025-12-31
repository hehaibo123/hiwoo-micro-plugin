<template>
  <!--  <div v-if="isShowBreadcrumb" class="layout-navbars-breadcrumb">-->
  <div class="layout-navbars-breadcrumb">
    <SvgIcon
        class="layout-navbars-breadcrumb-icon"
        :name="themeConfig.isCollapse ? 'ele-Expand' : 'ele-Fold'"
        :size="16"
        @click="onThemeConfigChange"
    />
    <div>
      <el-button type="primary" plain v-for="menu in menus" :key="menu.id"
                 :style="{height: '50px',marginLeft: 0,borderRadius: 0,background:'white',margin:'0 14px'}"
                 @click="onSystemChange(menu)">
        {{ menu.zhCn }}
      </el-button>
    </div>
    <el-breadcrumb class="layout-navbars-breadcrumb-hide">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item v-for="(v, k) in state.breadcrumbList"
                            :key="!v.tagsViewName ? v.title : v.tagsViewName">
					<span v-if="k === state.breadcrumbList.length - 1" class="layout-navbars-breadcrumb-span">
						<SvgIcon :name="v.icon" class="layout-navbars-breadcrumb-iconfont"
                     v-if="themeConfig.isBreadcrumbIcon"/>
						<div v-if="!v.tagsViewName">{{ $t(v.title) }}</div>
						<div v-else>{{ v.tagsViewName }}</div>
					</span>
          <!--          <a v-else @click.prevent="onBreadcrumbClick(v)">-->
          <!--            <SvgIcon :name="v.icon" class="layout-navbars-breadcrumb-iconfont"-->
          <!--                     v-if="themeConfig.isBreadcrumbIcon"/>-->
          <!--            {{ $t(v.title) }}-->
          <!--          </a>-->
          <a v-else>
            <SvgIcon :name="v.icon" class="layout-navbars-breadcrumb-iconfont"
                     v-if="themeConfig.isBreadcrumbIcon"/>
            {{ $t(v.title) }}
          </a>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts" name="layoutBreadcrumb">
import {reactive, computed, onMounted, ref} from 'vue';
import {onBeforeRouteUpdate, useRoute, useRouter} from 'vue-router';
import {Local} from '/@/utils/storage';
import other from '/@/utils/other';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import {useRoutesList} from '/@/stores/routesList';
import {useTagsViewRoutes} from '/@/stores/tagsViewRoutes';
import {useUserInfo} from "/@/stores/userInfo";
import {useMenuApi} from '/@/api/menu/index';

// 定义变量内容
const stores = useRoutesList();
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
const {routesList} = storeToRefs(stores);
let route = useRoute();
const router = useRouter();
const storesTagsViewRoutes = useTagsViewRoutes();
const {tagsViewRoutes} = storeToRefs(storesTagsViewRoutes);
const state = reactive<BreadcrumbState>({
  breadcrumbList: [],
  routeSplit: [],
  routeSplitFirst: '',
  routeSplitIndex: 1,
});
const menus = ref([]);
const systemType = ref('cloud')
systemType.value = sessionStorage.getItem("systemType")
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);

const getTopLeftMenus = () => {
  menus.value = <RouteItems>routesList.value.filter((item: T) => item.layout === 'topLeft' && !item.hide);
}
const onSystemChange = (menu: any) => {
  let userInfo = userInfos.value;
  delete userInfo['password'];
  delete userInfo['authBtnList'];
  window.open(menu.path + "?token=" + btoa(window.encodeURIComponent(JSON.stringify(userInfo))), 'target');
};

// 动态设置经典、横向布局不显示
const isShowBreadcrumb = computed(() => {
  if (!route) return false
  initRouteSplit(route.path);
  const {layout, isBreadcrumb} = themeConfig.value;
  if (layout === 'classic' || layout === 'transverse') return false;
  else return isBreadcrumb ? true : false;
});
// 面包屑点击时
const onBreadcrumbClick = (v: RouteItem) => {
  const {redirect, path, activeRule} = v;
  if (redirect) router.push(redirect);
  else router.push(activeRule + path);
};
// 展开/收起左侧菜单点击
const onThemeConfigChange = () => {
  themeConfig.value.isCollapse = !themeConfig.value.isCollapse;
  setLocalThemeConfig();
};
// 存储布局配置
const setLocalThemeConfig = () => {
  Local.remove('themeConfig');
  Local.set('themeConfig', themeConfig.value);
};
// 处理面包屑数据
const getBreadcrumbList = (arr: RouteItems) => {
  arr.forEach((item: RouteItem) => {
    state.routeSplit.forEach((v: string, k: number, arrs: string[]) => {
      if (state.routeSplitFirst === item.path) {
        state.routeSplitFirst += `/${arrs[state.routeSplitIndex]}`;
        state.breadcrumbList.push(item);
        state.routeSplitIndex++;
        if (item.children) getBreadcrumbList(item.children);
      }
    });
  });
};
// 当前路由字符串切割成数组，并删除第一项空内容
const initRouteSplit = (path: string) => {
  if (!themeConfig.value.isBreadcrumb) return false;
  state.breadcrumbList = [routesList.value[0]];
  state.routeSplit = path.split('/');
  state.routeSplit.shift();
  state.routeSplitFirst = `/${state.routeSplit[0]}`;
  state.routeSplitIndex = 1;
  getBreadcrumbList(routesList.value);

  // if (route.name === 'home' || (route.name === 'notFound' && state.breadcrumbList.length > 1)) state.breadcrumbList.shift();
  state.breadcrumbList.shift();
  // if (state.breadcrumbList.length > 0)
  //   state.breadcrumbList[state.breadcrumbList.length - 1].tagsViewName = other.setTagsViewNameI18n(<RouteToFrom>route);
};
// 页面加载时
onMounted(() => {
  getTopLeftMenus();
  // route = tagsViewRoutes.value.find((v: RouteItem) => (v.activeRule + v.path) === route.path);
  route = tagsViewRoutes.value.find((v: RouteItem) => route.path.indexOf((v.activeRule + v.path)) >= 0 && !v.children);

  if (!route) return false
  initRouteSplit(route.path);
});
// 路由更新时
onBeforeRouteUpdate((to) => {
  // route = tagsViewRoutes.value.find((v: RouteItem) => (v.activeRule + v.path) === to.path);
  route = tagsViewRoutes.value.find((v: RouteItem) => to.path.indexOf((v.activeRule + v.path)) >= 0 && !v.children);
  initRouteSplit(to.path);
});
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb {
  flex: 1;
  height: inherit;
  display: flex;
  align-items: center;
  margin: 0 !important;

  .layout-navbars-breadcrumb-icon {
    cursor: pointer;
    font-size: 18px;
    color: var(--next-bg-topBarColor);
    height: 100%;
    width: 40px;
    opacity: 0.8;

    &:hover {
      opacity: 1;
    }
  }

  .layout-navbars-breadcrumb-span {
    display: flex;
    opacity: 0.7;
    color: var(--next-bg-topBarColor);
  }

  .layout-navbars-breadcrumb-iconfont {
    font-size: 14px;
    margin-right: 5px;
  }

  :deep(.el-breadcrumb__separator) {
    opacity: 0.7;
    color: var(--next-bg-topBarColor);
  }

  :deep(.el-breadcrumb__inner a, .el-breadcrumb__inner.is-link) {
    font-weight: unset !important;
    color: var(--next-bg-topBarColor);

    &:hover {
      color: var(--el-color-primary) !important;
    }
  }
}
</style>
