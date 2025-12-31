<template>
  <div class="layout-pd">
    <el-card shadow="hover">
      <section>
        <div class="demo-box">
          <h3>开发示例</h3>
          <el-image :src="example1" :zoom-rate="1.2"
                    :max-scale="7"
                    :min-scale="0.2"
                    :preview-src-list="[example1]"
                    show-progress
                    :initial-index="4"
                    fit="cover"></el-image>
        </div>
        <el-divider direction="vertical"/>
        <div class="note-box">

        </div>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount} from 'vue';
import {useI18n} from 'vue-i18n';
import Process from "/@/assets/img/production-line/process.png";
import example1 from '/@/assets/img/production-line/example-1.png'


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
      height: 100%;
      display: flex;

      h3 {
        margin-bottom: 16px;
      }

      .el-divider {
        height: 100%;
        margin: 0 8px;
      }

      .demo-box {
        width: calc(100% - 720px);

        .el-image {
          display: inline-block;
          height: 300px;
        }
      }

      .note-box {
        width: 400px;

        h4 {
          margin: 16px 0;
        }
      }
    }
  }
}

</style>
