import { ref, reactive, onMounted } from "vue";
import {
  addMaterial,
  updateMaterial,
  getMaterialList,
  delMaterial,
  updateStatus
} from "@/api/pier/material";
import { delObjectProperty } from "@pureadmin/utils";
import { ElMessage } from "element-plus";
import { transformI18n } from "@/plugins/i18n";
import { listData } from "@/api/system/dict/data";

export function useColumns() {
  const editMap = reactive({}) as any; // 追踪编辑状态
  const dataList = ref([]);
  const loading = ref(false);
  const form = ref({
    materialName: null,
    materialStatus: null,
    remark01: null
  });

  const loadingTypeOptions = ref([]);
  onMounted(async () => {
    const res: any = await listData({ dictType: "loading_type", pageSize: 50 });
    if (res && res.code === 200) {
      loadingTypeOptions.value = res.rows.map((item: any) => ({
        label: item.dictLabel,
        value: item.dictValue
      }));
    }
  });

  const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
  });

  const isAcquisitionData = [
    {label: '是|yes',value: '1'},
    {label: '否|no',value: '0'},
  ]

  const columns: any[] = [
    {
      label: transformI18n("imip.page4.obj29"),
      prop: "sort",
      sortable: true,
      width: 180,
      cellRenderer: ({ row, index }) =>
        editMap[index]?.editable ? (
          <el-input-number
            v-model={editMap[index].sort}
            controls-position="right"
          />
        ) : (
          <p>{row.sort}</p>
        )
    },
    {
      label: transformI18n("imip.page1.obj3"),
      sortable: true,
      prop: "materialName",
      cellRenderer: ({ row, index }) =>
        editMap[index]?.editable ? (
          <el-input v-model={editMap[index].materialName} />
        ) : (
          <p>{row.materialName}</p>
        )
    },
    {
      label: transformI18n("imip.page1.loadingType"),
      prop: "remark01",
      cellRenderer: ({ row, index }) =>
        editMap[index]?.editable ? (
          <el-select
            v-model={editMap[index].remark01}
            placeholder={transformI18n("imip.page1.loadingType")}
          >
            {loadingTypeOptions.value.map(item => (
              <el-option
                label={item.label}
                value={item.value}
                key={item.value}
              />
            ))}
          </el-select>
        ) : (
          <span>
            {(
              loadingTypeOptions.value.find(
                (item: any) => item.value === row.remark01
              ) || {}
            ).label || row.remark01}
          </span>
        )
    },
    {
      label: transformI18n("是否关联皮带秤"),
      prop: "remark",
      cellRenderer: ({ row, index }) =>
        editMap[index]?.editable ? (
          <el-select
            v-model={editMap[index].remark}
            placeholder={transformI18n("是否关联皮带秤")}
          >
            {isAcquisitionData.map(item => (
              <el-option
                label={item.label}
                value={item.value}
                key={item.value}
              />
            ))}
          </el-select>
        ) : (
          <span>
            {(
              isAcquisitionData.find(
                (item: any) => item.value === row.remark
              ) || {}
            ).label || row.remark}
          </span>
        )
    },
    {
      label: transformI18n("imip.page1.obj2"),
      prop: "materialStatus",
      cellRenderer: ({ row }) => (
        <el-switch
          v-model={row.materialStatus}
          inline-prompt
          style="--el-switch-on-color: #13ce66"
          active-value={"0"}
          inactive-value={"1"}
          active-text={transformI18n("imip.page4.obj22")}
          inactive-text={transformI18n("imip.page4.obj23")}
          onChange={() => onStatusChange(row as any)}
        />
      )
    },
    {
      label: transformI18n("imip.page1.obj38"),
      slot: "operation"
    }
  ];

  /** 获取列表数据 */
  async function fetchData() {
    loading.value = true;
    try {
      const { rows, total } = (await getMaterialList({
        ...form.value,
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      })) as any;
      dataList.value = rows || [];
      pagination.total = total;
    } catch (error) {
      ElMessage.error("获取数据失败", error);
    }
    loading.value = false;
  }

  function handleCurrentChange(page) {
    pagination.pageNum = page;
    fetchData();
  }
  function handleSizeChange(size) {
    pagination.pageSize = size;
    pagination.pageNum = 1;
    fetchData();
  }

  /** 添加新物资 */
  async function onAdd() {
    // 检查是否有正在编辑的数据
    if (Object.keys(editMap).length > 0) {
      return ElMessage.warning("请先保存或取消当前正在编辑的物资");
    }

    const newItem = {
      id: null,
      materialName: "",
      materialStatus: "0",
      remark01: null,
      sort: 1 // 新增的排第一
    };

    // 在数组最前面插入新数据
    dataList.value.unshift(newItem);

    // 确保 editMap 只包含当前新增的数据
    editMap[0] = { ...newItem, editable: true };
  }

  /** 编辑行 */
  function onEdit(row, index) {
    // 检查是否有未保存的新增数据
    if (dataList.value.some(item => item.id === null)) {
      return ElMessage.warning("请先保存或取消当前新增的物资");
    }

    // 允许编辑
    editMap[index] = { ...row, editable: true };
  }

  /** 保存（新增或更新） */
  async function onSave(index) {
    const row = editMap[index];

    // **确保 row 存在，并且 materialName 不是空**
    if (!row || !row.materialName.trim()) {
      return ElMessage.warning("物资名称不能为空");
    }

    try {
      if (row.id) {
        // **更新**
        await updateMaterial({
          id: row.id,
          materialName: row.materialName,
          materialStatus: row.materialStatus,
          remark: row.remark,
          remark01: row.remark01,
          sort: row.sort
        });
        ElMessage.success("修改成功");
      } else {
        // **新增**
        const { data } = (await addMaterial({
          materialName: row.materialName,
          materialStatus: row.materialStatus,
          remark01: row.remark01,
          remark: row.remark,
          sort: row.sort
        })) as any;
        row.id = data?.id; // **获取新 id**
        ElMessage.success("新增成功");
      }

      dataList.value[index] = delObjectProperty(row, "editable");
      // eslint-disable-next-line @typescript-eslint/no-dynamic-delete
      delete editMap[index];
    } catch (error) {
      ElMessage.error("操作失败", error);
    }

    fetchData();
  }

  /** 取消编辑 */
  function onCancel(index) {
    if (dataList.value[index]?.id === null) {
      dataList.value.splice(index, 1);
    }
    // eslint-disable-next-line @typescript-eslint/no-dynamic-delete
    delete editMap[index];
  }

  async function onStatusChange(row) {
    try {
      await updateStatus(row.id, row.materialStatus);
      ElMessage.success(
        `物资状态更新成功: ${row.materialStatus === "0" ? "启用" : "禁用"}`
      );
    } catch (error) {
      ElMessage.error("状态更新失败", error);
      row.materialStatus = row.materialStatus === "0" ? "1" : "0";
    }
  }

  /** 删除物资 */
  async function onDel(row) {
    try {
      await delMaterial(row.id);
      ElMessage.success("删除成功");
      fetchData();
    } catch (error) {
      ElMessage.error("删除失败", error);
    }
  }

  function resetForm() {
    form.value.materialName = null;
    form.value.materialStatus = null;
    form.value.remark01 = null;
    fetchData();
  }

  onMounted(fetchData); // 组件挂载时加载数据

  return {
    form,
    fetchData,
    loading,
    editMap,
    columns,
    dataList,
    onEdit,
    onSave,
    onCancel,
    onAdd,
    onDel,
    resetForm,
    handleCurrentChange,
    handleSizeChange,
    pagination,
    loadingTypeOptions
  };
}
