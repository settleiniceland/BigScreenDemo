<template>
  <el-form ref="pwdRef" :model="user" :rules="rules" :label-width="width">
    <el-form-item :label="t('person.oldPassword')" prop="oldPassword">
      <el-input
        v-model="user.oldPassword"
        :placeholder="t('person.oldPasswordPlaceholder')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item :label="t('person.newPassword')" prop="newPassword">
      <el-input
        v-model="user.newPassword"
        :placeholder="t('person.newPasswordPlaceholder')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item :label="t('person.confirmPassword')" prop="confirmPassword">
      <el-input
        v-model="user.confirmPassword"
        :placeholder="t('person.confirmPasswordPlaceholder')"
        type="password"
        show-password
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { updateUserPwd } from "@/api/system/user";
import { useI18n } from "vue-i18n";
import { computed, getCurrentInstance, reactive, ref } from "vue";
const { proxy } = getCurrentInstance();

const user = reactive({
  oldPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined
});

const { locale, t } = useI18n();
const width = computed(() => {
  return locale.value === "zh" ? "80px" : "auto";
});

const equalToPassword = (rule, value, callback) => {
  if (user.newPassword !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = ref({
  oldPassword: [{ required: true, message: "旧密码不能为空", trigger: "blur" }],
  newPassword: [
    { required: true, message: "新密码不能为空", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
    {
      pattern: /^[^<>"'|\\]+$/,
      message: "不能包含非法字符：< > \" ' \\\ |",
      trigger: "blur"
    }
  ],
  confirmPassword: [
    { required: true, message: "确认密码不能为空", trigger: "blur" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ]
});

/** 提交按钮 */
function submit() {
  proxy.$refs.pwdRef.validate(valid => {
    if (valid) {
      updateUserPwd(user.oldPassword, user.newPassword).then(response => {
        ElMessage.success("修改成功");
      });
    }
  });
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage();
}
</script>
