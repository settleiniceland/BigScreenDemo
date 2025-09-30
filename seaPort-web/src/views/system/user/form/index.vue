<script setup lang="ts">
import { onMounted, ref, toRaw } from "vue";
import ReCol from "@/components/ReCol";
import { formRules } from "../utils/rule";
import { FormProps } from "../utils/types";
import { usePublicHooks } from "../../hooks";
import { deptTreeSelect, getUser } from "@/api/system/user";
import { listRole } from "@/api/system/role";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    title: "新增",
    higherDeptOptions: [],
    parentId: 0,
    nickName: "",
    userName: "",
    password: "",
    phonenumber: "",
    email: "",
    sex: "0",
    status: 1,
    remark: "",
    roleIds: [],
    bindIpStatus: "0"
  })
});
const deptOptions = ref(undefined);
const roleOptions = ref<any[]>([]);
/** 查询部门下拉树结构 */
function getDeptTree() {
  deptTreeSelect().then((response: any) => {
    deptOptions.value = response.data;
  });
}

async function getRoles() {
  const data: any = await listRole({
    pageSize: 200,
    pageNum: 1
  });
  roleOptions.value = data.rows;
}

const sexOptions = [
  {
    value: "0",
    label: "男"
  },
  {
    value: "1",
    label: "女"
  }
];
const ruleFormRef = ref();
const { switchStyle } = usePublicHooks();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}

onMounted(() => {
  if (props.formInline.title === "新增") {
    props.formInline.password = "123456";
  } else {
    handleUpdate(props.formInline);
  }
  getDeptTree();
  getRoles();
});

async function handleUpdate(row: any) {
  const userId = row.userId || ids.value;
  const response = await getUser(userId);
  newFormInline.value.roleIds = response.roleIds;
}

defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="82px"
  >
    <el-row :gutter="30">
      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="用户昵称" prop="nickName">
          <el-input
            v-model="newFormInline.nickName"
            clearable
            placeholder="请输入用户昵称"
          />
        </el-form-item>
      </re-col>
      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="用户工号" prop="userName">
          <el-input
            v-model="newFormInline.userName"
            clearable
            placeholder="请输入用户工号"
          />
        </el-form-item>
      </re-col>

      <re-col
        v-if="newFormInline.title === '新增'"
        :value="12"
        :xs="24"
        :sm="24"
      >
        <el-form-item label="用户密码" prop="password">
          <el-input
            v-model="newFormInline.password"
            type="password"
            clearable
            placeholder="请输入用户密码"
            show-password
          />
        </el-form-item>
      </re-col>
      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="手机号" prop="phonenumber">
          <el-input
            v-model="newFormInline.phonenumber"
            clearable
            placeholder="请输入手机号"
          />
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="newFormInline.email"
            clearable
            placeholder="请输入邮箱"
          />
        </el-form-item>
      </re-col>
      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="用户性别">
          <el-select
            v-model="newFormInline.sex"
            placeholder="请选择用户性别"
            class="w-full"
            clearable
          >
            <el-option
              v-for="(item, index) in sexOptions"
              :key="index"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="归属部门" prop="deptId">
          <el-tree-select
            v-model="newFormInline.deptId"
            filterable
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            placeholder="请选择归属部门"
            check-strictly
          />
        </el-form-item>
      </re-col>
      <re-col
        v-if="newFormInline.title === '新增'"
        :value="12"
        :xs="24"
        :sm="24"
      >
        <el-form-item label="用户状态">
          <el-switch
            v-model="newFormInline.status"
            inline-prompt
            :active-value="0"
            :inactive-value="1"
            active-text="启用"
            inactive-text="停用"
            :style="switchStyle"
          />
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="角色">
          <el-select
            v-model="newFormInline.roleIds"
            multiple
            placeholder="请选择"
          >
            <el-option
              v-for="item in roleOptions"
              :key="item.roleId"
              :label="item.roleName"
              :value="item.roleId"
            />
          </el-select>
        </el-form-item>
      </re-col>
      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="绑定IP地址">
          <el-switch
            v-model="newFormInline.bindIpStatus"
            inline-prompt
            :active-value="'1'"
            :inactive-value="'0'"
            active-text="绑定"
            inactive-text="不绑定"
            :style="switchStyle"
          />
        </el-form-item>
      </re-col>
      <re-col>
        <el-form-item label="备注">
          <el-input
            v-model="newFormInline.remark"
            placeholder="请输入备注信息"
            type="textarea"
          />
        </el-form-item>
      </re-col>
    </el-row>
  </el-form>
</template>
