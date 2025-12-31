<template>
  <el-menu
      router
      :default-active="state.defaultActive"
      :default-openeds="state.defaultOpened"
      background-color="transparent"
      :collapse="state.isCollapse"
      :unique-opened="getThemeConfig.isUniqueOpened"
      :collapse-transition="true"
  >
    <template v-for="val in menuLists">
      <template v-if="!val.hide">
        <el-sub-menu :index="val.iframe ? val.id:(val.activeRule+val.path)"
                     v-if="val.menuType == 'M'"
                     :key="val.iframe ? val.id:(val.activeRule+val.path)">
          <template #title>
            <component :is="val.icon" :width="16" :height="16"/>
            <span style="margin-left: 8px">{{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn }}</span>
          </template>
          <SubItem :chil="val.children"/>
        </el-sub-menu>
        <template v-else>
          <el-menu-item :index="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                        :key="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                        :class="route.fullPath===(val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path))?'is-active':''">
            <template v-if="val.link && val.iframe">
              <component :is="val.icon" :width="16" :height="16"/>
              <a class="w100" style="margin-left: 8px" @click.prevent="onALinkClick(val)">
                {{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn }}
              </a>
            </template>
            <template v-else>
              <component :is="val.icon" :width="16" :height="16"/>
              <a class="w100" style="margin-left: 8px" @click.prevent="onALinkClick(val)">
                {{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn }}
              </a>
            </template>
          </el-menu-item>

          <!--        <el-menu-item :index="val.activeRule + val.path" :key="val.activeRule + val.path">
                    &lt;!&ndash;          <component :is="val.icon" :width="20" :height="20"/>&ndash;&gt;
                    <template #title v-if="!val.isLink || (val.isLink && val.isIframe)">
                      <span style="margin-left: 8px">{{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn}}999</span>
                    </template>
                    <template #title v-else>
                      <a class="w100" style="margin-left: 8px" @click.prevent="onALinkClick(val)">{{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn}}</a>
                    </template>
                  </el-menu-item>-->
        </template>
      </template>
    </template>
  </el-menu>
</template>

<script setup lang="ts" name="navMenuVertical">
import {defineAsyncComponent, reactive, computed, onMounted, watch, onBeforeMount} from 'vue';
import {useRoute, onBeforeRouteUpdate, RouteRecordRaw} from 'vue-router';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import other from '/@/utils/other';
import {useUserInfo} from "/@/stores/userInfo";

// 引入组件
const SubItem = defineAsyncComponent(() => import('/@/layout/navMenu/subItem.vue'));
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
// 定义父组件传过来的值
const props = defineProps({
  // 菜单列表
  menuList: {
    type: Array <RouteRecordRaw>,
    default: () => [],
  },
});

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
const route = useRoute();
const state = reactive({
  // 修复：https://gitee.com/lyt-top/vue-next-admin/issues/I3YX6G
  defaultActive: route.isDynamic ? route.isDynamicPath : route.path,
  defaultOpened: ['/operate/operate/'],
  isCollapse: false,
});

// 获取父级菜单数据
const menuLists = computed(() => {
  const menus = <RouteItems>props.menuList.filter((item: T) => item.layout === 'left');
  if (userInfos.value.roleId != 1) {
    return filterMenus(menus);
  } else {
    return menus;

  }

});
const filterMenus = (menus) => {
  return menus.filter(menu => {
    // 递归处理子菜单
    if (menu.children && menu.children.length > 0) {
      menu.children = filterMenus(menu.children);
    }

    return true;
  });
};


// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfig.value;
});
// 菜单高亮（详情时，父级高亮）
const setParentHighlight = (currentRoute: RouteToFrom) => {
  const {path, meta} = currentRoute;
  return meta?.isDynamic ? meta.isDynamicPath! : path!;
};
// 打开外部链接
const onALinkClick = (val: RouteItem) => {
  other.handleOpenLink(val);
};
onBeforeMount(() => {
  state.defaultOpened = [setParentHighlight(route) + "/"];
})
// 页面加载时
// 页面加载时
onMounted(() => {
  state.defaultActive = setParentHighlight(route);
});

// 路由更新时
onBeforeRouteUpdate((to) => {
  // 修复：https://gitee.com/lyt-top/vue-next-admin/issues/I3YX6G
  state.defaultActive = setParentHighlight(to);
  const clientWidth = document.body.clientWidth;
  if (clientWidth < 1000) themeConfig.value.isCollapse = false;
});
// 设置菜单的收起/展开
watch(
    () => themeConfig.value.isCollapse,
    (isCollapse) => {
      document.body.clientWidth <= 1000 ? (state.isCollapse = false) : (state.isCollapse = isCollapse);
    },
    {
      immediate: true,
    }
);
</script>