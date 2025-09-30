<template>
  <el-form ref="userRef" :model="form" :rules="rules" :label-width="width">
    <el-form-item  label="用户昵称" prop="nickName">
      <el-input disabled v-model="form.nickName" maxlength="30" />
    </el-form-item>
    <el-form-item label="手机号码" prop="phonenumber">
      <el-input v-model="form.phonenumber" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio value="0">男</el-radio>
        <el-radio value="1">女</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { updateUserProfile } from "@/api/system/user";
import { useUserStoreHook } from "@/store/modules/user";
import { useI18n } from "vue-i18n";
import { deviceDetection } from "@pureadmin/utils";
import { addDialog } from "@/components/ReDialog";
import { computed, getCurrentInstance, h, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SelectUserTable from "@/views/system/user/index.vue";

const props = defineProps({
  user: {
    type: Object
  }
});
const userStore = useUserStoreHook();
const { locale, t } = useI18n();
const $t = t;
const width = computed(() => {
  return locale.value === "zh" ? "100px" : "auto";
});

const { proxy } = getCurrentInstance();

const form: any = ref({
  sysUserCandidates: [],
  sysUserCandidatesLongTime: []
});
const rules: any = ref({
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  email: [
    { required: true, message: "邮箱地址不能为空", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"]
    }
  ],
  phonenumber: [
    { required: true, message: "手机号码不能为空", trigger: "blur" },
    {
      // pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入手机号码",
      trigger: "blur"
    }
  ],
  sysUserCandidates: [
    {
      required: true,
      message: "请选择候补人",
      trigger: "change",
      type: "array"
    }
  ],
  sysUserCandidatesLongTime: [
    {
      required: true,
      message: "请选择候补人",
      trigger: "change",
      type: "array"
    }
  ]
});

/** 提交按钮 */
function submit() {
  (proxy.$refs.userRef as any).validate(valid => {
    if (valid) {
      updateUserProfile(form.value).then(response => {
        ElMessage.success("修改成功");
        props.user.phonenumber = form.value.phonenumber;
        props.user.email = form.value.email;
        userStore.SET_NICKNAME(form.value.nickName);
      });
    }
  });
}

/** 关闭按钮 */
// function close() {
//   proxy.$tab.closePage();
// }

// 回显当前登录用户信息
// 是否休假
const sys_user_leave = [
  { label: "否", value: "0" },
  { label: "是", value: "1" }
];

// 是否长期授权
const sys_user_auth = [
  { label: "否", value: "0" },
  { label: "是", value: "1" }
];

const selectUserRef = ref();

const sysUserCandidates: any = ref([]);
watch(
  () => props.user,
  user => {
    if (user) {
      form.value = {
        nickName: user.nickName,
        phonenumber: user.phonenumber,
        email: user.email,
        sex: user.sex,
        takeVacation: user.takeVacation,
        sysUserCandidates: user.sysUserCandidates,
        isAuth: user.isAuth,
        sysUserCandidatesLongTime: user.sysUserCandidatesLongTime
      };
    }
  },
  { immediate: true }
);
const generateUniqueId = () => `signatory-checkbox-${Date.now()}`;
const beforeClose = (action: string, instance, done, uniqueId) => {
  if (action === "confirm") {
    // 判断是否勾选了按钮 - 逻辑
    const checkbox = document.getElementById(uniqueId) as HTMLInputElement;
    if (checkbox && !checkbox.checked) {
      ElMessage.warning("请勾选按钮同意");
      return false; // 阻止弹窗关闭
    }
  }
  done();
  return true;
};
async function openDialog(title, type: any) {
  addDialog({
    title: `${title}选择`,
    props: {
      cb: (data: any) => {
        console.log(data);
      },
      isSelect: true,
      isSingleChoice: false
    },
    width: "55%",
    top: "5vh",
    draggable: true,
    fullscreen: deviceDetection(),
    fullscreenIcon: true,
    closeOnClickModal: false,
    contentRenderer: () => h(SelectUserTable, { ref: selectUserRef }),
    beforeSure: async (done, { options }) => {
      const selectRow = selectUserRef.value.getCurrentSelectRow();
      if (selectRow && selectRow[0]) {
        const uniqueId = generateUniqueId(); // 生成唯一ID
        ElMessageBox.confirm(
          `${`<div><label><input type="checkbox" id="${uniqueId}" /> <strong>授权代签人</strong><span style="font-size: 12px;font-weight: 400;">应经过所属安环科作业许可培训，了解有关作业的步骤、危害、存在的风险及其预防控制措施，熟知作业票有关作业的管理程序，现场确认签发，</span><strong>被授权人与授权人承担同等责任</strong>。</label></div><br />`}`,
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            dangerouslyUseHTMLString: true,
            type: "warning",
            beforeClose: (action, instance, done) =>
              beforeClose(action, instance, done, uniqueId)
          }
        ).then(async () => {
          sysUserCandidates.value = [];
          selectRow.forEach((item: any) => {
            sysUserCandidates.value.push({
              userId: item.userId,
              deptId: item.deptId,
              userName: item.userName,
              nickName: item.nickName,
              ucUserId: userStore.userId
            });
          });
          if (type === 1) {
            form.value.sysUserCandidates = sysUserCandidates.value;
          } else if (type === 2) {
            form.value.sysUserCandidatesLongTime = sysUserCandidates.value;
          }
          done();
        });
      }
    }
  });
  // }
}

const tagClose = (tag, index, type) => {
  if (type === 1) {
    form.value.sysUserCandidates.splice(index, 1);
  } else {
    form.value.sysUserCandidatesLongTime.splice(index, 1);
  }
};
</script>
