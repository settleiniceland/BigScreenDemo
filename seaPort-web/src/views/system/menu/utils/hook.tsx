import editForm from "../form.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
// import { getMenuList } from "@/api/system";
import { delMenu, listMenu } from "@/api/system/menu";

import { transformI18n } from "@/plugins/i18n";
import { addDialog } from "@/components/ReDialog";
import { reactive, ref, onMounted, h } from "vue";
import type { FormItemProps } from "../utils/types";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { cloneDeep, isAllEmpty, deviceDetection } from "@pureadmin/utils";
import { status } from "nprogress";

export function useMenu() {
  const form = reactive({
    menuName: null
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);

  const getMenuType = (type, text = false) => {
    switch (type) {
      case "M":
        return text ? "目录" : "primary";
      case "C":
        return text ? "菜单" : "warning";
      case "F":
        return text ? "按钮" : "info";
    }
  };

  const columns: TableColumnList = [
    {
      label: "菜单名称",
      prop: "menuName",
      align: "left",
      width: 300,
      cellRenderer: ({ row }) => (
        <>
          <span class="inline-block mr-1">
            {h(useRenderIcon(row.icon), {
              style: { paddingTop: "1px" }
            })}
          </span>
          <span>{transformI18n(row.menuName)}</span>
        </>
      )
    },
    {
      label: "排序",
      prop: "orderNum",
      width: 100
    },
    {
      label: "权限标识",
      prop: "perms",
      showOverflowTooltip: true
    },
    {
      label: "组件路径",
      prop: "component",
      formatter: ({ path, component }) =>
        isAllEmpty(component) ? path : component
    },
    {
      label: "菜单类型",
      prop: "menuType",
      width: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={getMenuType(row.menuType)}
          effect="plain"
        >
          {getMenuType(row.menuType, true)}
        </el-tag>
      )
    },
    {
      label: "状态",
      prop: "status",
      width: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={row.status === "0" ? "primary" : "warning"}
          effect="plain"
        >
          {row.status === "0" ? "启用" : "停用"}
        </el-tag>
      )
    },
    // {
    //   label: "路由路径",
    //   prop: "path"
    // },
    {
      label: "隐藏",
      prop: "visible",
      formatter: ({ visible }) => (visible === "0" ? "否" : "是"),
      width: 100
    },
    {
      label: "操作",
      fixed: "right",
      width: 210,
      slot: "operation"
    }
  ];

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  function resetForm(formEl) {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  }

  async function onSearch() {
    loading.value = true;
    console.log(form);
    const { data } = (await listMenu(form)) as any; // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    dataList.value = handleTree(data, "menuId"); // 处理成树结构
    loading.value = false;
  }

  function formatHigherMenuOptions(treeList) {
    if (!treeList || !treeList.length) return;
    const newTreeList = [];
    for (let i = 0; i < treeList.length; i++) {
      treeList[i].title = transformI18n(treeList[i].title);
      formatHigherMenuOptions(treeList[i].children);
      newTreeList.push(treeList[i]);
    }
    return newTreeList;
  }

  function openDialog(title = "新增", row?: FormItemProps) {
    // console.log("row", row);
    addDialog({
      title: `${title}菜单`,
      props: {
        formInline: {
          menuType: row?.menuType ?? "M",
          menuId: row?.menuId ?? undefined,
          addMenuId: row?.addMenuId ?? undefined,
          parentId: row?.parentId ?? 0,
          menuName: row?.menuName ?? "",
          icon: row?.icon ?? "",
          orderNum: row?.orderNum ?? undefined,
          isFrame: row?.isFrame ?? "1",
          isCache: row?.isCache ?? "0",
          visible: row?.visible ?? "0",
          status: row?.status ?? "0"
        }
      },
      width: "45%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      beforeSure: (done, { options }) => {
        const { getRef, submitForm } = formRef.value;
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(
            `您“${title}”了菜单名称为“${transformI18n(curData.menuName)}”的这条数据`,
            {
              type: "success"
            }
          );
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

  function handleDelete(row) {
    delMenu(row.menuId).then(() => {
      message(`您删除了菜单名称为${transformI18n(row.menuName)}的这条数据`, {
        type: "success"
      });
      onSearch();
    });
  }

  onMounted(() => {
    onSearch();
  });

  return {
    form,
    loading,
    columns,
    dataList,
    /** 搜索 */
    onSearch,
    /** 重置 */
    resetForm,
    /** 新增、修改菜单 */
    openDialog,
    /** 删除菜单 */
    handleDelete,
    handleSelectionChange
  };
}
