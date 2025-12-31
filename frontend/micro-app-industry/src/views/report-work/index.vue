<template>
  <div class="layout-pd">
    <el-card shadow="hover">
      <h1>待开发...</h1>
      <section>
        <div style="width: 50%">
          <h3>开发说明：</h3>
          <p>根据业务需求自定义报工功能</p>
        </div>
        <div style="width: 50%">
          <h3>开发示例：</h3>
          <el-image :src="example1" alt=""></el-image>
          <el-image :src="example2" alt=""></el-image>
        </div>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount} from 'vue';
import {useI18n} from 'vue-i18n';
import example1 from '/@/assets/img/work/example-1.png'
import example2 from '/@/assets/img/work/example-2.png'


//引入组件


// 定义变量内容
const {t} = useI18n();

const state = reactive({
  name: '',
  user: {} as User,
});

// 页面加载前
onBeforeMount(() => {
});

// 页面加载时
onMounted(() => {

});


</script>

<style scoped lang="scss">
.layout-pd {
  .el-card {
    height: 100%;

    section {
      display: flex;
    }

    h3 {
      margin-top: 30px;
    }

    p {
      margin-top: 10px;
    }

    .el-image {
      margin-top: 20px;
      display: inline-block;
      height: 300px;
    }
  }
}

</style>
