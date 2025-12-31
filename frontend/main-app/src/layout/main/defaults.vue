<template>
  <el-watermark
      :content="state.showWatermark ? watermarkConnect : []"
      :font="{ fontSize: 16, color: 'rgba(0, 0, 0, 0.2)', fontFamily: 'Courier New' }"
      :gap="[200, 200]"
      :z-index="99999"
      style="height: 100%"
  >
    <el-container class="layout-container">
      <LayoutAside/>
      <el-container class="layout-container-view h100">
        <el-scrollbar ref="layoutScrollbarRef" class="layout-backtop">
          <LayoutHeader/>
          <LayoutMain ref="layoutMainRef"/>
        </el-scrollbar>
      </el-container>
    </el-container>
  </el-watermark>
</template>

<script setup lang="ts" name="layoutDefaults">
import {defineAsyncComponent, watch, onMounted, nextTick, ref, reactive, computed} from 'vue';
import {useRoute} from 'vue-router';
import {storeToRefs} from 'pinia';
import {useThemeConfig} from '/@/stores/themeConfig';
import {NextLoading} from '/@/utils/loading';

// 引入组件
const LayoutAside = defineAsyncComponent(() => import('/@/layout/component/aside.vue'));
const LayoutHeader = defineAsyncComponent(() => import('/@/layout/component/header.vue'));
const LayoutMain = defineAsyncComponent(() => import('/@/layout/component/main.vue'));

// 定义变量内容
const layoutScrollbarRef = ref<RefType>('');
const layoutMainRef = ref<InstanceType<typeof LayoutMain>>();
const route = useRoute();
const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
import {useUserInfo} from "/@/stores/userInfo";
import axios from 'axios';
import {Session} from "/@/utils/storage";

const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const state = reactive({
  communityMode: false,
  showWatermark: false, // 控制是否显示水印
})

// 重置滚动条高度
const updateScrollbar = () => {
  layoutScrollbarRef.value.update();
  layoutMainRef.value!.layoutMainScrollbarRef.update();
};

// 重置滚动条高度，由于组件是异步引入的
const initScrollBarHeight = () => {
  nextTick(() => {
    setTimeout(() => {
      updateScrollbar();
      layoutScrollbarRef.value.wrapRef.scrollTop = 0;
      layoutMainRef.value!.layoutMainScrollbarRef.wrapRef.scrollTop = 0;
    }, 500);
  });
};

const watermarkConnect = computed(() => {
  return ['示例工程'];
});



// 页面加载时
onMounted(async () => {
  // 获取项目类型
  state.showWatermark = false;
  initScrollBarHeight();
  // NextLoading.done(600);
});

// 监听路由的变化，切换界面时，滚动条置顶
watch(
    () => route.path,
    () => {
      initScrollBarHeight();
    }
);

// 监听 themeConfig 配置文件的变化，更新菜单 el-scrollbar 的高度
watch(
    () => [themeConfig.value.isTagsview, themeConfig.value.isFixedHeader],
    () => {
      nextTick(() => {
        updateScrollbar();
      });
    },
    {
      deep: true,
    }
);
</script>