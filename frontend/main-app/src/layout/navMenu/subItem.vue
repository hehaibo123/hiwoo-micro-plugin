<template>
  <template v-for="val in chils">
    <el-sub-menu :index="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                 :key="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                 v-if="val.children && val.children.length > 0">
      <template #title>
        <SvgIcon :name="val.icon"/>
        <span>{{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn }}</span>
      </template>
      <sub-item :chil="val.children"/>
    </el-sub-menu>
    <template v-else>
      <el-menu-item :index="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                    :key="val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path)"
                    :class="route.fullPath===(val.iframe ? '/iframe/' + val.id:(val.activeRule + val.path))?'is-active':''">
        <template v-if="val.link && val.iframe">
          <SvgIcon :name="val.icon"/>
          <span style="margin-left: 8px">{{ themeConfig.globalI18n == 'en' ? val.en : val.zhCn }}</span>
        </template>
        <template v-else>
          <SvgIcon :name="val.icon"/>
          <a class="w100" style="margin-left: 8px" @click.prevent="onALinkClick(val)">{{
              themeConfig.globalI18n == 'en' ? val.en : val.zhCn
            }}</a>
        </template>
      </el-menu-item>
    </template>
  </template>
</template>

<script setup lang="ts" name="navMenuSubItem">
import {computed} from 'vue';
import {RouteRecordRaw, useRoute} from 'vue-router';
import other from '/@/utils/other';
import {useThemeConfig} from "/@/stores/themeConfig";
import {storeToRefs} from "pinia";

const storesThemeConfig = useThemeConfig();
const {themeConfig} = storeToRefs(storesThemeConfig);
let route = useRoute();

// 定义父组件传过来的值
const props = defineProps({
  // 菜单列表
  chil: {
    type: Array <RouteRecordRaw>,
    default: () => [],
  },
});

// 获取父级菜单数据
const chils = computed(() => {
  return <RouteItems>props.chil;
});
// 打开外部链接
const onALinkClick = (val: RouteItem) => {
  other.handleOpenLink(val);
};
</script>

<style scoped lang="scss">

</style>
