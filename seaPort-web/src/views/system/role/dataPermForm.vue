<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import {FormProps} from "./utils/types";
import {dataScope, deptTreeSelectByRoleId, getRole} from "@/api/system/role";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    code: "",
    remark: ""
  })
});

const formRef = ref();
const form: any = ref(props.formInline);

function getRef() {
  return formRef.value;
}
const sys_normal_disable = [
  { label: "正常", value: "0" },
  { label: "停用", value: "1" }
];
const dateRange = ref<any>([]);
const deptExpand = ref(true);
const deptNodeAll = ref(false);
const deptOptions = ref<any[]>([]);
const deptRef = ref<any>(null);

/** 数据范围选项*/
const dataScopeOptions = ref([
  { value: "1", label: "全部数据权限" },
  { value: "2", label: "自定数据权限" },
  { value: "3", label: "本部门数据权限" },
  { value: "4", label: "本部门及以下数据权限" },
  { value: "5", label: "仅本人数据权限" },
  { value: "6", label: "自定义部门及以下经理数据权限" },
  { value: "7", label: "公司/事业部级数据权限" }
]);

/** 根据角色ID查询部门树结构 */
function getDeptTree(roleId: any) {
  return deptTreeSelectByRoleId(roleId).then((response: any) => {
    deptOptions.value = response.depts;
    return response;
  });
}
/** 树权限（展开/折叠）*/
function handleCheckedTreeExpand(value: any, type: any) {
  let treeList = deptOptions.value;
  for (let i = 0; i < treeList.length; i++) {
    deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
  }
}
/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value: any, type: string) {
  deptRef.value.setCheckedNodes(value ? deptOptions.value : []);
}
/** 树权限（父子联动） */
function handleCheckedTreeConnect(value: any, type: string) {
  if (type === "menu") {
    form.value.menuCheckStrictly = value ? true : false;
  } else if (type === "dept") {
    form.value.deptCheckStrictly = value ? true : false;
  }
}
/** 重置新增的表单以及其他数据  */
function reset() {
  deptExpand.value = true;
  deptNodeAll.value = false;
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: "0",
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    remark: undefined
  };
  formRef.value.resetFields();
}
/** 所有部门节点数据 */
function getDeptAllCheckedKeys() {
  // 目前被选中的部门节点
  let checkedKeys = deptRef.value.getCheckedKeys();
  // 半选中的部门节点
  let halfCheckedKeys = deptRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}
/** 选择角色权限范围触发 */
function dataScopeSelectChange(value: any) {
  if (value !== "2") {
    deptRef.value.setCheckedKeys([]);
  }
}
/** 分配数据权限操作 */
function handleDataScope(row: any) {
  reset();
  const deptTreeSelectByRoleId = getDeptTree(row.roleId);
  getRole(row.roleId).then((response: any) => {
    form.value = response.data;
    nextTick(() => {
      deptTreeSelectByRoleId.then(res => {
        nextTick(() => {
          if (deptRef.value) {
            deptRef.value.setCheckedKeys(res.checkedKeys);
          }
        });
      });
    });
  });
}
/** 提交按钮（数据权限） */
function submitDataScope(cb) {
  if (form.value.roleId) {
    form.value.deptIds = getDeptAllCheckedKeys();
    dataScope(form.value).then(response => {
      if (cb) {
        cb();
      }
    });
  }
}
onMounted(() => {
  handleDataScope(form.value);
});

defineExpose({ getRef, submitDataScope });
</script>

<template>
  <el-form ref="formRef" :model="form" label-width="100px">
    <el-form-item label="角色名称" prop="roleName">
      <el-input v-model="form.roleName" placeholder="请输入角色名称" />
    </el-form-item>
    <el-form-item prop="roleKey">
      <template #label>
        <span>
          <el-tooltip
            content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasRole('admin')`)"
            placement="top"
          >
            <el-icon><question-filled /></el-icon>
          </el-tooltip>
          权限字符
        </span>
      </template>
      <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
    </el-form-item>
    <el-form-item label="权限范围">
      <el-select v-model="form.dataScope" @change="dataScopeSelectChange">
        <el-option
          v-for="item in dataScopeOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-show="form.dataScope == 2" label="数据权限">
      <el-checkbox
        v-model="deptExpand"
        @change="handleCheckedTreeExpand($event, 'dept')"
      >展开/折叠</el-checkbox
      >
      <el-checkbox
        v-model="deptNodeAll"
        @change="handleCheckedTreeNodeAll($event, 'dept')"
      >全选/全不选</el-checkbox
      >
      <el-checkbox
        v-model="form.deptCheckStrictly"
        @change="handleCheckedTreeConnect($event, 'dept')"
      >父子联动</el-checkbox
      >
      <el-tree
        ref="deptRef"
        class="tree-border"
        :data="deptOptions"
        show-checkbox
        default-expand-all
        node-key="id"
        :check-strictly="!form.deptCheckStrictly"
        empty-text="加载中，请稍候"
        :props="{ label: 'label', children: 'children' }"
      ></el-tree>
    </el-form-item>
  </el-form>
</template>
<style lang="scss">
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF none;
  border-radius:4px;
  width: 100%;
  max-height: 500px;
  overflow-y: auto;
}
</style>
