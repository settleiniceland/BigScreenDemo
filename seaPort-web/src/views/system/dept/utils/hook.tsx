import dayjs from "dayjs";
import editForm from "../form.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
import { usePublicHooks } from "../../hooks";
import { addDialog } from "@/components/ReDialog";
import { reactive, ref, onMounted, h } from "vue";
import type { FormProps } from "../utils/types";
import { cloneDeep, deviceDetection } from "@pureadmin/utils";
import { addDept, listDept, updateDept, delDept } from "@/api/system/dept";
import { ElMessage } from "element-plus";

export function useDept() {
  const form = reactive({
    deptName: "",
    status: null
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);
  const { tagStyle } = usePublicHooks();

  const columns: TableColumnList = [
    {
      label: "部门名称",
      prop: "deptName",
      minWidth: 180,
      align: "left"
    },
    {
      label: "排序",
      prop: "orderNum",
      minWidth: 70
    },
    {
      label: "状态",
      prop: "status",
      minWidth: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag size={props.size} style={tagStyle.value(row.status)}>
          {row.status === "0" ? "正常" : "停用"}
        </el-tag>
      )
    },
    {
      label: "负责人",
      prop: "leader"
    },
    {
      label: "创建时间",
      minWidth: 200,
      prop: "createTime",
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    // {
    //   label: "备注",
    //   prop: "remark",
    //   minWidth: 320
    // },
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
    const res = (await listDept(form)) as any; // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    if (res.code === 200) {
      let newData = res.data;
      dataList.value = handleTree(newData, "deptId"); // 处理成树结构
      console.log(dataList.value);
    } else {
      ElMessage.error(res.msg);
    }
    loading.value = false;
  }

  function formatHigherDeptOptions(treeList) {
    // 根据返回数据的status字段值判断追加是否禁用disabled字段，返回处理后的树结构，用于上级部门级联选择器的展示（实际开发中也是如此，不可能前端需要的每个字段后端都会返回，这时需要前端自行根据后端返回的某些字段做逻辑处理）
    if (!treeList || !treeList.length) return;
    const newTreeList = [];
    for (let i = 0; i < treeList.length; i++) {
      treeList[i].disabled = treeList[i].status === 0 ? true : false;
      formatHigherDeptOptions(treeList[i].children);
      newTreeList.push(treeList[i]);
    }
    return newTreeList;
  }

  function openDialog(title = "新增", row?: FormProps) {
    console.log(row);
    addDialog({
      title: `${title}部门`,
      props: {
        formInline: {
          higherDeptOptions: formatHigherDeptOptions(cloneDeep(dataList.value)),
          parentId: "",
          deptName: "",
          deptId: "",
          orderNum: "",
          leader: "",
          phone: "",
          email: "",
          status: "0",
          parentName: "",
          ...row
        }
      },
      width: "40%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef, formInline: null }),
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormProps;
        delete curData.higherDeptOptions;
        function chores() {
          message(`您${title}了部门名称为${curData.deptName}的这条数据`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        FormRef.validate(valid => {
          if (valid) {
            console.log("curData", curData);
            // 表单规则校验通过
            if (title === "新增") {
              // 实际开发先调用新增接口，再进行下面操作
              addDept(curData).then(r => chores());
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              updateDept(curData).then(r => chores());
            }
          }
        });
      }
    });
  }

  async function handleDelete(row) {
    const res: any = await delDept(row.deptId);
    console.log(res);
    if (res.code === 200) {
      ElMessage({
        message: `成功删除了部门名称为${row.deptName}的这条数据`,
        type: "success"
      });
      onSearch();
    } else {
      ElMessage.error(res.msg);
    }
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
    /** 新增、修改部门 */
    openDialog,
    /** 删除部门 */
    handleDelete,
    handleSelectionChange
  };
}
