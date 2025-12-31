<template>
  <div class="process-page">

  </div>
</template>

<script setup lang="ts">
import {reactive, onMounted, ref, watch, nextTick, onActivated, markRaw, defineAsyncComponent} from 'vue';
import {useUserInfo} from "/@/stores/userInfo";
import {storeToRefs} from "pinia";
import {NextLoading} from "/@/utils/loading";

// 引入组件

const stores = useUserInfo();
const {userInfos} = storeToRefs(stores);

const props = defineProps({

});

// 定义变量内容
const state = reactive({

});


// 页面加载时
onMounted(() => {
  NextLoading.done();

});

// 由于页面缓存原因，keep-alive
onActivated(() => {
});

</script>

<style scoped lang="scss">
.process-page {
  height: 100vh;
  width: 100vw;
}
</style>
