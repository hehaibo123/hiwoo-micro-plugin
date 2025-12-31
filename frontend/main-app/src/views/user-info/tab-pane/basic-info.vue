<template>
  <div class="user-info" style="margin-top: 4px">
    <el-form ref="userInfoFormRef" :model="state.form" :rules="state.rules" label-width="68px" style="width: 500px">
      <el-form-item :label="$t('message.label.avatar')">
        <el-upload
            class="avatar-uploader"
            :action="url"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :disabled="props.disabled"
        >
          <img v-if="state.form.headSculpture" :src="state.origin + state.form.headSculpture" class="avatar"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>

      </el-form-item>
      <el-form-item prop="name" :label="$t('message.label.userName')">
        <el-input v-model="state.form.name" @keydown="onDown('name')"
                  :placeholder="$t('message.login.placeholder.inputName')"
                  clearable :disabled="props.disabled"></el-input>
      </el-form-item>
      <el-form-item :label="$t('message.label.account')">
        <el-input v-model="state.form.account" disabled clearable></el-input>
      </el-form-item>
      <el-form-item :label="$t('message.label.personalSignature')">
        <el-input type="textarea" v-model="state.form.notes" @keydown="onDown('notes')"
                  :placeholder="$t('message.login.placeholder.inputUserDescription')"
                  clearable :disabled="props.disabled"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts" name="userInfo">
import {ref, onMounted, reactive} from 'vue';
import {ElMessageBox, ElMessage, FormInstance, UploadProps} from 'element-plus';
import {useUserApi} from "/@/api/hiwoo-base";
import {useI18n} from 'vue-i18n';
import {Plus} from "@element-plus/icons-vue";
import {useUserInfo} from "/@/stores/userInfo";
import {storeToRefs} from "pinia";
import {Session} from "/@/utils/storage";

// 定义子组件向父组件传值/事件
const emit = defineEmits(['refresh']);

// 定义父组件传过来的值
const props = defineProps({
  user: {
    type: Object,
    default: () => {
    }
  },
  disabled: {
    type: Boolean,
    default: () => true
  }
});

// 定义变量内容
const {t} = useI18n();
const userInfoFormRef = ref();
const userInfo = useUserInfo();
const {userInfos} = storeToRefs(userInfo);
const url = import.meta.env.VITE_API_URL + '/system/oss/upload/' + userInfos.value.enterpriseId;
const state = reactive({
  form: JSON.parse(JSON.stringify(userInfos.value)),
  rules: {
    name: [
      {
        required: true,
        message: '请填写长度为2-12的名称',
        trigger: 'blur',
      },
      {
        pattern: '[^ \x22]+',
        min: 2, max: 12,
        message: '请填写长度为2-12的名称',
        trigger: 'blur',
      },
    ]
  },
  tmp: {},
  origin: window.location.origin

});

// 页面加载时
onMounted(() => {
});

const update = (column) => {
  ElMessageBox.confirm('确认要修改此用户信息吗?',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      })
      .then(() => {
        useUserApi().editUser(state.form).then(response => {
          userInfos.value = state.form;
          Session.set('userInfo', state.form);
          ElMessage.success(t('message.tip.requestSuccess'));
        })
      })
      .catch(() => {
        state.form[column] = state.tmp[column];
      })
}

const onDown = (column) => {
  state.tmp[column] = state.form[column];
}

const handleAvatarSuccess = (res: any) => {
  if (res.code === 200) {
    state.form.headSculpture = res.data.url;

    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/png' && rawFile.type !== 'image/gif' && rawFile.type !== 'image/jpg' && rawFile.type !== 'image/jpeg') {
    ElMessage.warning(t('message.system.tip.imgFormatTip'))
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.warning(t('message.system.tip.imgSize2Tip'))
    return false
  }
  return true
}

const saveUserInfo = async (formEl = userInfoFormRef.value) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      useUserApi().editUser(state.form).then(response => {
        userInfos.value = state.form;
        Session.set('userInfo', state.form);
        ElMessage.success(t('message.tip.requestSuccess'))
        emit('refresh');
      })
    } else {
    }
  })
}

// 暴露变量
defineExpose({
  saveUserInfo,
});

</script>

<style scoped lang="scss">
::v-deep(.el-upload) {
  width: 150px;
  height: 150px;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  .avatar {
    width: 100%;
  }
}
</style>
