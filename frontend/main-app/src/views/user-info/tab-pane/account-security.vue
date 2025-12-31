<template>
  <el-table :data="state.tableData"
            :stripe="true"
            :header-cell-style="{backgroundColor:'#F5F5F5',color:'#333333',height:'48px'}"
            :cell-style="{height:'48px'}"
            style="width: 100%;height: 100%"
            border>
    <el-table-column prop="name" :label="$t('message.label.name')" width="100"></el-table-column>
    <el-table-column prop="content" :label="$t('message.label.content')"></el-table-column>
    <el-table-column prop="status" :label="$t('message.label.status')" width="70"></el-table-column>
    <el-table-column :label="$t('message.label.operation')" width="60">
      <template #default="scope">
        <el-link type="primary" @click="setup(scope.row)">{{ scope.row.operation }}</el-link>
      </template>
    </el-table-column>
  </el-table>
  <div>
    <UpdatePassword ref="UpdatePasswordRef"></UpdatePassword>

  </div>
</template>

<script setup lang="ts" name="userInfo">
import {ref, onMounted, reactive, defineAsyncComponent} from 'vue';
import {useI18n} from 'vue-i18n';
import {useUserInfo} from "/@/stores/userInfo";
import {storeToRefs} from "pinia";
import {ElMessageBox} from "element-plus";

const UpdatePassword = defineAsyncComponent(() => import('/@/views/user-info/dialog/update-password.vue'));


// 定义变量内容
const {t} = useI18n();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const UpdatePasswordRef = ref();
const state = reactive({
  user: userInfos.value,
  tableData: [] as object
});

// 页面加载时
onMounted(() => {
  initTable()
});

const initTable = () => {
  state.tableData = [{
    name: t('message.label.loginPassword'),
    content: t('message.label.passwordContent'),
    status: userInfos.value.password ? t('message.label.alreadySet') : t('message.label.notSet'),
    operation: userInfos.value.password ? t('message.operation.update') : t('message.operation.setup'),
  }]
}

const setup = (row: any) => {
  switch (row.name) {
    case t('message.label.loginPassword'):
      UpdatePasswordRef.value.openDialog();
      break;
  }
};

// 暴露变量
defineExpose({});

</script>

<style scoped lang="scss">

</style>
