<template>
  <div class="layout-iframe">
    <div class="layout-padding-auto layout-padding-view">
      <div class="w100 iframe-box" v-for="v in setIframeList" :key="v.path" v-loading="v.loading"
           :style="{height: getRoutePath === v.path ? '100%' : '0'}"
           element-loading-background="white">
        <transition-group :name="name">
          <iframe
              :src="v.meta.isLink"
              :key="v.path"
              frameborder="0"
              height="100%"
              width="100%"
              :data-url="v.path"
              v-if="getRoutePath === v.path"
              ref="iframeRef"
          />
        </transition-group>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="layoutIframeView">
import {computed, watch, ref, nextTick} from 'vue';
import {useRoute} from 'vue-router';
import NProgress from "nprogress";
import {NextLoading} from "/@/utils/loading";

// 定义父组件传过来的值
const props = defineProps({
  // 刷新 iframe
  refreshKey: {
    type: String,
    default: () => '',
  },
  // 过渡动画 name
  name: {
    type: String,
    default: () => 'slide-right',
  },
  // iframe 列表
  list: {
    type: Array,
    default: () => [],
  },
});

// 定义变量内容
const iframeRef = ref();
const route = useRoute();

// 处理 list 列表，当打开时，才进行加载
const setIframeList = computed(() => {
  return (<RouteItems>props.list).filter((v: RouteItem) => v.meta.isIframe);
});
// 获取 iframe 当前路由 path
const getRoutePath = computed(() => {
  return route.path;
});
// 关闭 iframe loading
const closeIframeLoading = (val: string, item: RouteItem) => {
  nextTick(() => {
    if (!iframeRef.value) return false;
    iframeRef.value.forEach((v: HTMLElement) => {
      if (v.dataset.url === val) {
        v.onload = () => {
          if (item.meta?.isIframeOpen && item.loading) item.loading = false;
        };
      }
    });
  });
};
// 监听路由变化，初始化 iframe 数据，防止多个 iframe 时，切换不生效
watch(
    () => route.fullPath,
    (val) => {
      const item: any = props.list.find((v: any) => v.path === val);
      if (!item) return false;
      if (!item.meta.isIframeOpen) item.meta.isIframeOpen = true;
      closeIframeLoading(val, item);
      NextLoading.done();
    },
    {
      immediate: true,
    }
);
// 监听 iframe refreshKey 变化，用于 tagsview 右键菜单刷新
watch(
    () => props.refreshKey,
    () => {
      const item: any = props.list.find((v: any) => v.path === route.path);
      if (!item) return false;
      if (item.meta.isIframeOpen) item.meta.isIframeOpen = false;
      setTimeout(() => {
        item.meta.isIframeOpen = true;
        item.loading = true;
        closeIframeLoading(route.fullPath, item);
      });
    },
    {
      deep: true,
    }
);
</script>
<style scoped lang="scss">
.layout-iframe {
  width: calc(100% - 32px);
  height: 100%;
  position: inherit;
}

</style>