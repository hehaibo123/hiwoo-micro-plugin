<template>
  <div class="layout-logo" v-if="setShowLogo" @click="onThemeConfigChange">
<!--    <img :src="themeConfig.smallLogo || logoMini"  @error="handleError" class="layout-logo-medium-img" />-->
    <span>{{ themeConfig.globalViceTitle }}</span>
  </div>
  <div class="layout-logo-size" v-else @click="onThemeConfigChange">
    <img :src="getLogo" class="layout-logo-size-img" />
  </div>
</template>

<script setup lang="ts" name="layoutLogo">
import {computed, onMounted, watch} from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import logoMini from '/@/assets/logo-mini.svg';
import logo from '/@/assets/engine-logo.png';

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);

// 设置 logo 的显示。classic 经典布局默认显示 logo
const setShowLogo = computed(() => {
  let { isCollapse, layout } = themeConfig.value;
  return !isCollapse || layout === 'classic' || document.body.clientWidth < 1000;
});
// logo 点击实现菜单展开/收起
const onThemeConfigChange = () => {
  if (themeConfig.value.layout === 'transverse') return false;
  themeConfig.value.isCollapse = !themeConfig.value.isCollapse;
};
const getLogo = computed(() => {
  console.log('themeConfig.value.smallLogo', themeConfig.value)

  return   themeConfig.value.smallLogo || logoMini ;
});


// 监听smallLogo的变化，并在必要时更新DOM
// watch(() => themeConfig.value.smallLogo, (newVal) => {
//   console.log('smallLogo changed to:', newVal);
// });

onMounted(() => {
      const faviconElement = document.querySelector('link[rel="icon"]');
      if (faviconElement && themeConfig.value.smallLogo) {
        // 获取当前图标地址
        const currentIconHref = faviconElement.getAttribute('href');
        const newIconHref = themeConfig.value.smallLogo;
        // 替换图标地址
        faviconElement.setAttribute('href', newIconHref);
        // console.log(`图标地址已从 ${currentIconHref} 替换为 ${newIconHref}`);
      }
    }
)
</script>

<style scoped lang="scss">
.layout-logo {
  width: 192px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: rgb(0 21 41 / 2%) 0px 1px 4px;
  color: var(--el-color-primary);
  font-size: 16px;
  cursor: pointer;
  animation: logoAnimation 0.3s ease-in-out;
  span {
    white-space: nowrap;
    display: inline-block;
  }
  &:hover {
    span {
      color: var(--color-primary-light-2);
    }
  }
  &-medium-img {
    width: 32px;
    margin-right: 5px;
  }
}
.layout-logo-size {
  width: 100%;
  height: 50px;
  display: flex;
  cursor: pointer;
  animation: logoAnimation 0.3s ease-in-out;
  &-img {
    width: 20px;
    margin: auto;
  }
  &:hover {
    img {
      animation: logoAnimation 0.3s ease-in-out;
    }
  }
}
</style>
