import dayjs from "dayjs";
import editForm from "../form.vue";
import dataPermForm from "../dataPermForm.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
import { ElMessageBox } from "element-plus";
import { usePublicHooks } from "../../hooks";
import { transformI18n } from "@/plugins/i18n";
import { addDialog } from "@/components/ReDialog";
import type { FormItemProps } from "../utils/types";
import type { PaginationProps } from "@pureadmin/table";
import { getKeyList, deviceDetection } from "@pureadmin/utils";
// import { getRoleList, getRoleMenu, getRoleMenuIds } from "@/api/system";
import {
  type Ref,
  reactive,
  ref,
  onMounted,
  h,
  toRaw,
  watch,
  getCurrentInstance,
  ComponentInternalInstance,
  computed
} from "vue";
import {
  addRole,
  changeRoleStatus,
  dataScope,
  delRole,
  getRole,
  listRole,
  updateRole
} from "@/api/system/role";
import {
  roleMenuTreeselect,
  treeselect as menuTreeselect
} from "@/api/system/menu";
// import { parseTime } from "@/utils/ruoyi";
import { useRouter } from "vue-router";

// const { proxy } = getCurrentInstance() as ComponentInternalInstance;
export function useRole(treeRef: Ref) {
  const router = useRouter();
  const form = reactive({
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleKey: undefined,
    status: ""
  });
  const buttonClass = computed(() => {
    return [
      "!h-[20px]",
      "reset-margin",
      "!text-gray-500",
      "dark:!text-white",
      "dark:hover:!text-primary"
    ];
  });
  const ids = ref<number[]>([]);

  const curRow = ref();
  const formRef = ref();
  const dataPermFormRef = ref();
  const dataList = ref([]);
  const treeData = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const treeSearchValue = ref();
  const switchLoadMap = ref({});
  const isExpandAll = ref(false);
  const isSelectAll = ref(false);
  const { switchStyle } = usePublicHooks();
  const treeProps = {
    value: "id",
    label: "title",
    children: "children"
  };
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const columns: TableColumnList = [
    {
      label: "角色编号",
      prop: "roleId"
    },
    {
      label: "角色名称",
      prop: "roleName"
    },
    {
      label: "权限字符",
      prop: "roleKey"
    },
    {
      label: "状态",
      cellRenderer: scope => (
        <el-switch
          size={scope.props.size === "small" ? "small" : "default"}
          loading={switchLoadMap.value[scope.index]?.loading}
          v-model={scope.row.status}
          active-value={"0"}
          inactive-value={"1"}
          active-text="已启用"
          inactive-text="已停用"
          disabled={scope.row.roleKey === "admin"}
          inline-prompt
          style={switchStyle.value}
          onChange={() => onChange(scope as any)}
        />
      ),
      minWidth: 90
    },
    {
      label: "备注",
      prop: "remark",
      minWidth: 160
    },
    {
      label: "创建时间",
      prop: "createTime",
      minWidth: 160,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      label: "操作",
      fixed: "right",
      width: 210,
      slot: "operation"
    }
  ];

  function onChange({ row, index }) {
    switchLoadMap.value[index] = Object.assign({}, switchLoadMap.value[index], {
      loading: true
    });
    ElMessageBox.confirm(
      `确认要<strong>${
        row.status === "0" ? "启用" : "停用"
      }</strong><strong style='color:var(--el-color-primary)'>${
        row.roleName
      }</strong>吗?`,
      "系统提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    )
      .then(() => {
        changeRoleStatus(row.roleId, row.status).then(() => {
          message(`已${row.status === "0" ? "启用" : "停用"}${row.roleName}`, {
            type: "success"
          });
        });
      })
      .catch(() => {
        row.status === "0" ? (row.status = "1") : (row.status = "0");
      })
      .finally(() => {
        switchLoadMap.value[index] = Object.assign(
          {},
          switchLoadMap.value[index],
          {
            loading: false
          }
        );
      });
  }

  function handleDelete(row) {
    const roleIds = row.roleId || ids.value;
    ElMessageBox.confirm(
      `确认要<strong>删除角色</strong><strong style='color:var(--el-color-primary)'>${row.roleName}</strong>吗?`,
      "系统提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    )
      .then(() => {
        return delRole(roleIds);
      })
      .then(() => {
        onSearch();
        message(`您删除了角色名称为${row.roleName}的这条数据`, {
          type: "success"
        });
      })
      .catch(() => {});
    onSearch();
  }

  function handleSizeChange(val: number) {
    pagination.pageSize = val;
    form.pageSize = val;
    onSearch();
  }

  function handleCurrentChange(val: number) {
    console.log(`current page: ${val}`);
    pagination.currentPage = val;
    form.pageNum = val;
    onSearch();
  }

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  async function onSearch() {
    loading.value = true;
    const data: any = await listRole(toRaw(form));
    dataList.value = data.rows;
    pagination.total = data.total;
    // pagination.pageSize = data.pageSize;
    // pagination.currentPage = 1;

    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  const resetForm = formEl => {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  };
  /** 分配用户 */
  function handleAuthUser(row: any) {
    router.push({ path: "/system/role_auth", query: { roleId: row.roleId } });
  }

  function openDialog(title = "新增", row?: FormItemProps) {
    addDialog({
      title: `${title}角色`,
      props: {
        formInline: row
      },
      width: "40%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      beforeSure: (done, { options }) => {
        // const FormRef = formRef.value.getRef();
        const { getRef, submitForm, form } = formRef.value;
        // const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(`您${title}了角色名称为${form?.roleName}的这条数据`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        submitForm(valid => {
          // 表单规则校验通过
          if (title === "新增") {
            // 实际开发先调用新增接口，再进行下面操作
            chores();
          } else {
            // 实际开发先调用修改接口，再进行下面操作
            chores();
          }
        });
      }
    });
  }
  function handleDataScope(title = "分配数据权限", row?: FormItemProps) {
    addDialog({
      title: `${title}`,
      props: {
        formInline: row
      },
      width: "40%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(dataPermForm, { ref: dataPermFormRef }),
      beforeSure: (done, { options }) => {
        const { getRef, submitDataScope } = dataPermFormRef.value;
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(`修改成功`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        submitDataScope(valid => {
          // 表单规则校验通过
          if (title === "新增") {
            // 实际开发先调用新增接口，再进行下面操作
            chores();
          } else {
            // 实际开发先调用修改接口，再进行下面操作
            chores();
          }
        });
      }
    });
  }

  /** 高亮当前权限选中行 */
  function rowStyle({ row: { id } }) {
    return {
      cursor: "pointer",
      background: id === curRow.value?.id ? "var(--el-fill-color-light)" : ""
    };
  }

  const filterMethod = (query: string, node) => {
    return transformI18n(node.title)!.includes(query);
  };

  onMounted(async () => {
    onSearch();
  });

  return {
    form,
    isShow,
    curRow,
    loading,
    columns,
    rowStyle,
    dataList,
    treeData,
    treeProps,
    isLinkage,
    pagination,
    isExpandAll,
    isSelectAll,
    treeSearchValue,
    buttonClass,
    handleDataScope,
    handleAuthUser,
    onSearch,
    resetForm,
    openDialog,
    handleDelete,
    filterMethod,
    transformI18n,
    // handleDatabase,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange
  };
}
