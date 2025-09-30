<script setup lang="ts">
import { addMenu, getMenu, listMenu, updateMenu } from "@/api/system/menu";
import { IconSelect } from "@/components/ReIcon";
import { handleTree } from "@/utils/tree";
import QuestionFilled from "@iconify-icons/ep/question-filled";
import { nextTick, onMounted, ref } from "vue";
import { formRules } from "./utils/rule";
import { FormProps } from "./utils/types";
import { transformI18n } from "@/plugins/i18n";
const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    menuNameEn: undefined,
    menuNameInd: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: undefined,
    isFrame: "1",
    isCache: "0",
    visible: "0",
    status: "0",
    path: "",
    component: "",
    perms: "",
    query: ""
  })
});

const menuRef = ref();
const form = ref(props.formInline);

const menuList = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const title = ref("");
const menuOptions = ref<any[]>([]);
const isExpandAll = ref(false);
const refreshTable = ref(true);
const showChooseIcon = ref(false);
const iconSelectRef = ref<any>(null);
const sys_show_hide = [
  { label: "显示", value: "0" },
  { label: "隐藏", value: "1" }
];
const sys_normal_disable = [
  { label: "正常", value: "0" },
  { label: "停用", value: "1" }
];
/** 表单重置 */
function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    menuNameEn: undefined,
    menuNameInd: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: undefined,
    isFrame: "1",
    isCache: "0",
    visible: "0",
    status: "0",
    path: "",
    component: "",
    perms: "",
    query: ""
  };
  menuRef.value?.resetFields();
}
const transformMenuOptions = (menus) => {
  return menus.map(item => ({
    ...item,
    menuName: transformI18n(item.menuName),
    children: item.children ? transformMenuOptions(item.children) : []
  }))
}
/** 查询菜单下拉树结构 */
function getTreeselect() {
  menuOptions.value = [];
  listMenu().then((response: any) => {
    const menu: any = { menuId: 0, menuName: "主类目", children: [] };
    menu.children = transformMenuOptions(handleTree(response.data, "menuId"));
    menuOptions.value.push(menu);
  });
}
/** 新增按钮操作 */
function handleAdd(row: any) {
  reset();
  getTreeselect();
  if (row != null && row.addMenuId) {
    form.value.parentId = row.addMenuId;
    form.value.menuId = undefined;
  } else {
    form.value.parentId = 0;
  }
  title.value = "添加菜单";
}
/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}
/** 修改按钮操作 */
async function handleUpdate(row: any) {
  reset();
  await getTreeselect();
  getMenu(row.menuId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改菜单";
  });
}

// 初始化页面
onMounted(() => {
  // 判断form是否有id，有则为修改，无则为添加
  if (
    !form.value ||
    (form.value != null &&
      (form.value?.addMenuId || form.value?.menuId === undefined))
  ) {
    handleAdd(form.value);
  } else {
    handleUpdate(form.value);
  }
});

/** 提交按钮 */
function submitForm(cb) {
  menuRef.value.validate((valid: any) => {
    if (valid) {
      if (form.value.menuId !== undefined) {
        updateMenu(form.value).then(response => {
          if (cb) {
            cb();
          }
        });
      } else {
        addMenu(form.value).then(response => {
          if (cb) {
            cb();
          }
        });
      }
    }
  });
}

function getRef() {
  return menuRef.value;
}

defineExpose({ getRef, submitForm });
</script>

<template>
  <el-form ref="menuRef" :model="form" :rules="formRules" label-width="100px">
    <el-row>
      <el-col :span="24">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions ?? []"
            :props="{
              value: 'menuId',
              label: 'menuName',
              children: 'children'
            }"
            value-key="menuId"
            placeholder="选择上级菜单"
            check-strictly
          />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-show="form.menuType !== 'F'" :span="24">
        <el-form-item label="菜单图标">
          <IconSelect v-model="form.icon" class="w-full" />
          <!--          <el-input v-model="form.icon" placeholder="请输入菜单名称" />-->
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number
            v-model="form.orderNum"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType != 'F'" :span="12">
        <el-form-item>
          <template #label>
            <span>
              <el-tooltip
                content="选择是外链则路由地址需要以`http(s)://`开头"
                placement="top"
              >
                <el-icon><question-filled /></el-icon> </el-tooltip
              >是否外链
            </span>
          </template>
          <el-radio-group v-model="form.isFrame">
            <el-radio label="0">是</el-radio>
            <el-radio label="1">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType != 'F'" :span="12">
        <el-form-item prop="path">
          <template #label>
            <span>
              <el-tooltip
                content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              路由地址
            </span>
          </template>
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType == 'C'" :span="12">
        <el-form-item prop="component">
          <template #label>
            <span>
              <el-tooltip
                content="访问的组件路径，如：`system/user/index`，默认在`views`目录下"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              组件路径
            </span>
          </template>
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType != 'M'" :span="12">
        <el-form-item>
          <el-input
            v-model="form.perms"
            placeholder="请输入权限标识"
            maxlength="100"
          />
          <template #label>
            <span>
              <el-tooltip
                content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              权限字符
            </span>
          </template>
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType == 'C'" :span="12">
        <el-form-item>
          <el-input
            v-model="form.query"
            placeholder="请输入路由参数"
            maxlength="255"
          />
          <template #label>
            <span>
              <el-tooltip
                content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`'
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              路由参数
            </span>
          </template>
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType == 'C'" :span="12">
        <el-form-item>
          <template #label>
            <span>
              <el-tooltip
                content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              是否缓存
            </span>
          </template>
          <el-radio-group v-model="form.isCache">
            <el-radio label="0">缓存</el-radio>
            <el-radio label="1">不缓存</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType != 'F'" :span="12">
        <el-form-item>
          <template #label>
            <span>
              <el-tooltip
                content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              显示状态
            </span>
          </template>
          <el-radio-group v-model="form.visible">
            <el-radio
              v-for="dict in sys_show_hide"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="form.menuType != 'F'" :span="12">
        <el-form-item>
          <template #label>
            <span>
              <el-tooltip
                content="选择停用则路由将不会出现在侧边栏，也不能被访问"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              菜单状态
            </span>
          </template>
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
