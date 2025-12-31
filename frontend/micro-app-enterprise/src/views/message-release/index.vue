<template>
  <div class="layout-pd">
    <el-card shadow="hover">
      <section>
        <h3>系统公告</h3>
        <el-form style="width: 500px;" label-width="100px">
          <el-form-item label="公告标题:">
            <el-input v-model="state.announcement.title" placeholder="请输入内容"></el-input>
          </el-form-item>
          <el-form-item label="公告类型:">
            <el-select v-model="state.announcement.type" placeholder="请选择">
              <el-option label="平台公告" value="platform"></el-option>
              <el-option label="升级公告" value="upgrade"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="公告内容:">
            <el-input type="textarea" v-model="state.announcement.content" placeholder="请输入内容"></el-input>
          </el-form-item>
          <el-form-item label="发布时间:">
            <el-date-picker
                v-model="state.announcement.releaseTime"
                type="datetime"
                placeholder="请选择"
                format="YYYY-MM-DD HH:mm:ss"
                date-format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="失效时间:">
            <el-date-picker
                v-model="state.announcement.expiredTime"
                type="datetime"
                placeholder="请选择"
                format="YYYY-MM-DD HH:mm:ss"
                date-format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="createAnnouncement">发布</el-button>
          </el-form-item>
        </el-form>
      </section>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {defineAsyncComponent, ref, onMounted, reactive, onBeforeMount} from 'vue';
import {useI18n} from 'vue-i18n';
import example1 from '/@/assets/img/price/example-1.png'
import {useBaseApi} from "/@/api";
import {ElMessage} from "element-plus";


//引入组件


// 定义变量内容
const {t} = useI18n();

const state = reactive({
  name: '',
  user: {} as User,
  announcement: {
    title: '',
    content: '',
    type: 'platform',
    releaseTime: '',
    expiredTime: '',
    receiverType: 'all',
    channel: 'platform'
  }
});

// 页面加载前
onBeforeMount(() => {
});

// 页面加载时
onMounted(() => {

});

const createAnnouncement = () => {
  useBaseApi().createAnnouncement(state.announcement).then(res => {
    if (res.code === 200) {
      ElMessage.success(res.msg);
    }
  });
}

</script>

<style scoped lang="scss">
.layout-pd {
  .el-card {
    height: 100%;

    section {
    }

    h3 {
      margin-bottom: 16px;
    }
  }
}

</style>
