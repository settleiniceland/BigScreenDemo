<script setup lang="ts">
import { ref, onMounted, nextTick } from "vue";
import { formRules } from "./utils/rule";
import { FormProps } from "./utils/types";
import {
  addRole,
  changeRoleStatus,
  dataScope,
  delRole,
  getRole,
  listRole,
  updateRole,
  deptTreeSelectByRoleId
} from "@/api/system/role";
import {
  roleMenuTreeselect,
  treeselect as menuTreeselect
} from "@/api/system/menu";
import { transformI18n } from "@/plugins/i18n";
const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    code: "",
    remark: ""
  })
});

const roleRef = ref();
const form: any = ref(props.formInline);

function getRef() {
  return roleRef.value;
}
const sys_normal_disable = [
  { label: "启用", value: "0" },
  { label: "停用", value: "1" }
];
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref<any>([]);
const menuOptions = ref<any[]>([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const deptExpand = ref(true);
const deptNodeAll = ref(false);
const deptOptions = ref<any[]>([]);
const openDataScope = ref(false);
const menuRef = ref<any>(null);
const deptRef = ref<any>(null);

/** 添加角色 */
function handleAdd() {
  reset();
  getMenuTreeselect();
}
/** 修改角色 */
function handleUpdate(row: any) {
  reset();
  const roleId = row.roleId;
  const roleMenu = getRoleMenuTreeselect(roleId);
  getRole(roleId).then((response: any) => {
    form.value = response.data;
    form.value.roleSort = Number(form.value.roleSort);
    nextTick(() => {
      roleMenu.then((res: any) => {
        let checkedKeys = res.checkedKeys;
        checkedKeys.forEach((v: any) => {
          nextTick(() => {
            menuRef.value.setChecked(v, true, false);
          });
        });
      });
    });
    title.value = "修改角色";
  });
}
/** 重置新增的表单以及其他数据  */
function reset() {
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([]);
  }
  menuExpand.value = false;
  menuNodeAll.value = false;
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
  roleRef.value.resetFields();
}
// 初始化页面
onMounted(() => {
  // 判断form是否有id，有则为修改，无则为添加
  if (form.value.roleId) {
    handleUpdate(form.value);
  } else {
    handleAdd();
  }
});
/** 根据角色ID查询菜单树结构 */
function getRoleMenuTreeselect(roleId: any) {
  return roleMenuTreeselect(roleId).then((response: any) => {
    menuOptions.value =translateTree(response.menus);
    return response;
  });
}
/** 根据角色ID查询部门树结构 */
function getDeptTree(roleId: any) {
  return deptTreeSelectByRoleId(roleId).then((response: any) => {
    deptOptions.value = response.depts;
    return response;
  });
}
/** 树权限（展开/折叠）*/
function handleCheckedTreeExpand(value: any, type: any) {
  if (type === "menu") {
    let treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  } else if (type === "dept") {
    let treeList = deptOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
}
/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value: any, type: string) {
  if (type === "menu") {
    menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  } else if (type === "dept") {
    deptRef.value.setCheckedNodes(value ? deptOptions.value : []);
  }
}
/** 树权限（父子联动） */
function handleCheckedTreeConnect(value: any, type: string) {
  if (type === "menu") {
    form.value.menuCheckStrictly = value ? true : false;
  } else if (type === "dept") {
    form.value.deptCheckStrictly = value ? true : false;
  }
}

/** 查询菜单树结构 */
function getMenuTreeselect() {
  menuTreeselect().then((response: any) => {
    menuOptions.value = translateTree(response.data);
  });
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
/** 所有菜单节点数据 */
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}
/** 提交按钮 */
function submitForm(cb) {
  roleRef.value.validate((valid: any) => {
    if (valid) {
      if (form.value.roleId !== undefined) {
        form.value.menuIds = getMenuAllCheckedKeys();
        updateRole(form.value).then(response => {
          if (cb) {
            cb();
          }
        });
      } else {
        form.value.menuIds = getMenuAllCheckedKeys();
        addRole(form.value).then(response => {
          if (cb) {
            cb();
          }
        });
      }
    }
  });
}
function translateTree(tree) {
  return tree.map(node => {
    const translatedNode = {
      ...node,
      label: transformI18n(node.label)
    };
    if (node.children && node.children.length > 0) {
      translatedNode.children = translateTree(node.children);
    }
    return translatedNode;
  });
}
defineExpose({ getRef, submitForm, form });
</script>

<template>
  <el-form ref="roleRef" :model="form" :rules="formRules" label-width="90px">
    <el-form-item label="角色名称" prop="roleName">
      <el-input v-model="form.roleName" placeholder="请输入角色名称" />
    </el-form-item>
    <el-form-item prop="roleKey">
      <template #label>
        <span> 权限字符 </span>
      </template>
      <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
    </el-form-item>
    <el-form-item label="角色顺序" prop="roleSort">
      <el-input-number
        v-model="form.roleSort"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="状态">
      <el-radio-group v-model="form.status">
        <el-radio
          v-for="dict in sys_normal_disable"
          :key="dict.value"
          :label="dict.value"
          >{{ dict.label }}</el-radio
        >
      </el-radio-group>
    </el-form-item>
    <el-form-item label="菜单权限">
      <el-checkbox
        v-model="menuExpand"
        @change="handleCheckedTreeExpand($event, 'menu')"
        >展开/折叠</el-checkbox
      >
      <el-checkbox
        v-model="menuNodeAll"
        @change="handleCheckedTreeNodeAll($event, 'menu')"
        >全选/全不选</el-checkbox
      >
      <el-checkbox
        v-model="form.menuCheckStrictly"
        @change="handleCheckedTreeConnect($event, 'menu')"
        >父子联动</el-checkbox
      >
      <el-tree
        ref="menuRef"
        class="tree-border"
        :data="menuOptions"
        show-checkbox
        node-key="id"
        :check-strictly="!form.menuCheckStrictly"
        empty-text="加载中，请稍候"
        :props="{ label: 'label', children: 'children' }"
      />
    </el-form-item>
    <el-form-item label="备注">
      <el-input
        v-model="form.remark"
        type="textarea"
        placeholder="请输入内容"
      />
    </el-form-item>
  </el-form>
</template>
<style lang="scss">
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #ffffff none;
  border-radius: 4px;
  width: 100%;
  max-height: 500px;
  overflow-y: auto;
}
</style>
