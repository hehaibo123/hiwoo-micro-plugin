<template>
  <div class="update-password-dialog">
    <el-dialog :title="state.dialog.title" v-model="state.dialog.isShowDialog" width="450px">
      <el-form ref="passwordFormRef" :model="state.form" :rules="state.rules" label-width="80px">
        <el-form-item prop="oldPassword" :label="$t('message.label.originalPassword')">
          <el-input type="password" v-model="state.form.oldPassword"
                    :placeholder="$t('message.placeholder.originalPassword')"
                    clearable></el-input>
        </el-form-item>
        <el-form-item prop="newPassword" :label="$t('message.label.newPassword')">
          <el-input type="password" v-model="state.form.newPassword"
                    :placeholder="$t('message.placeholder.newPassword')"
                    clearable></el-input>
        </el-form-item>
        <el-form-item prop="doubleConfirmation" :label="$t('message.label.doubleConfirmation')">
          <el-input type="password" v-model="state.form.doubleConfirmation"
                    :placeholder="$t('message.placeholder.doubleConfirmation')"
                    clearable></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="onCancel">{{ $t('message.formI18nOperation.cancel') }}</el-button>
					<el-button type="primary" @click="onSubmit(passwordFormRef)" :loading="state.submitLoading">
            {{ $t('message.formI18nOperation.confirm') }}</el-button>
				</span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from 'vue';
import {ElMessage, FormInstance} from 'element-plus';
import {useI18n} from 'vue-i18n';
import {useLoginApi} from "/@/api/login";
import {JSEncrypt} from "jsencrypt";

const {t} = useI18n();

// 定义变量内容
const passwordFormRef = ref();
const state = reactive({
  form: {
    oldPassword: '',
    newPassword: '',
    doubleConfirmation: '',
  },
  dialog: {
    isShowDialog: false,
    type: '',
    title: '',
    submitTxt: '',
  },
  submitLoading: false,
  rules: {
    oldPassword: [
      {
        required: true,
        message: '请填写密码',
        trigger: 'blur',
      }
    ],
    newPassword: [
      {
        required: true,
        message: '请填写密码',
        trigger: 'blur',
      },
      {
        pattern: /^[0-9A-Za-z!@#$%^&*()_+=\-[\]{};':"\\|,.<>/?]{6,12}$/,
        message: '请填写由字母、数字、常见特殊字符组成的 6-12 位的密码',
        trigger: 'change',
      }
    ],
    doubleConfirmation: [
      {
        required: true,
        message: '请填写密码',
        trigger: 'blur',
      },
      {
        pattern: /^[0-9A-Za-z!@#$%^&*()_+=\-[\]{};':"\\|,.<>/?]{6,12}$/,
        message: '请填写由字母、数字、常见特殊字符组成的 6-12 位的密码',
        trigger: 'change',
      }
    ],
  }
});

// 打开弹窗
const openDialog = () => {
  state.form = {
    oldPassword: '',
    newPassword: '',
    doubleConfirmation: '',
  }
  state.dialog.title = t('message.title.updatePassword');
  state.dialog.submitTxt = t('message.operation.confirm');
  state.dialog.isShowDialog = true;
};

// 关闭弹窗
const closeDialog = () => {
  state.dialog.isShowDialog = false;
};
// 取消
const onCancel = () => {
  closeDialog();
};
// 提交
const onSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    const req = {
      oldPassword: <string>encrypt(state.form.oldPassword),
      newPassword: <string>encrypt(state.form.newPassword),
      confirmPassword: <string>encrypt(state.form.newPassword)
    }
    useLoginApi().changePassword(req).then(response => {
      if (response.code == 200) {
        ElMessage.success(t('message.tip.requestSuccess'))
        closeDialog();
        state.form.oldPassword = ""
        state.form.newPassword = ""
        state.form.doubleConfirmation = ""
      } else {
        ElMessage.warning(response.msg)
      }
    })
  })
};

const encrypt = (password: string) => {
  // 公钥加密
  const encrypt = new JSEncrypt()
  const publicKey = import.meta.env.VITE_PublicKey;
  encrypt.setPublicKey(publicKey)
  return encrypt.encrypt(password)
}

// 暴露变量
defineExpose({
  openDialog
});
</script>
