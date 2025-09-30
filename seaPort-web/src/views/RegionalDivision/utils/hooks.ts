import { ref, reactive, h } from "vue";
import { ElMessage } from "element-plus";
import { addDialog } from "@/components/ReDialog/index";
import editForm from "../form.vue";
import { delHarbor, getHarborList, addHarbor, updateHarbor } from "@/api/pier";
import { transformI18n } from "@/plugins/i18n";
export const useAreaManagement = () => {
  const form = reactive({
    berthStatus: "",
    name: "",
    pierType: ""
  });

  const loading = ref(false);
  const treeData = ref([]);
  const expandedKeys = ref([]);
  const pagination = reactive({
    pageSize: 10,
    currentPage: 1,
    total: 0
  });

  const columns: any = [
    { label: transformI18n("imip.page3.obj29"), prop: "name" },
    {
      label: transformI18n("imip.page3.obj37"),
      cellRenderer: ({ row }) => {
        if (row.type === "dock") {
          return row.pierCode;
        } else {
          return row.berthCode;
        }
      }
    },
    {
      label: transformI18n("imip.page3.obj31"),
      slot: "pierType"
    },
    { label: transformI18n("imip.page3.obj30"), slot: "berthStatus" },
    {
      label: transformI18n("imip.page1.obj37"),
      slot: "remark"
    },
    { label: transformI18n("imip.page1.obj38"), fixed: "right", slot: "operation", minWidth: 130 }
  ];

  const onSearch = async () => {
    loading.value = true;
    // 保存当前已展开的节点ID
    const currentExpandedKeys = [...expandedKeys.value];
    
    try {
      const result = await getHarborList(form);
      const rows = result?.rows || [];

      // 处理树结构
      treeData.value = rows.map(node => processNode(node));
      
      // 保持原来已展开的节点状态
      if (currentExpandedKeys.length > 0) {
        expandedKeys.value = currentExpandedKeys;
      }
    } catch (error) {
      console.error("获取码头列表失败:", error);
      ElMessage.error("获取码头列表失败");
    } finally {
      loading.value = false;
    }
  };

  const processNode = node => {
    // 动态生成唯一标识
    node.id = node.pierId ? `pier-${node.pierId}` : `berth-${node.berthId}`;
    node.name = node.pierName || node.berthName || "";

    // 统一 geoJson 字段并解析
    const geoJsonStr = node.pierGeoJson || node.berthGeoJson;
    if (geoJsonStr) {
      try {
        node.geoJson = JSON.parse(geoJsonStr);
      } catch (error) {
        console.warn(`解析 geoJson 失败: ${error}`);
        node.geoJson = null;
      }
    }

    // 递归处理子节点
    if (node.children && node.children.length) {
      node.children = node.children.map(child => processNode(child));
    }

    return node;
  };

  const resetForm = formRef => {
    formRef.resetFields();
    onSearch();
  };

  const formRef = ref();

  const openDialog = (action = "imip.button.obj3", row = null, type = "dock") => {
    const getMapFeatures = (features = []) => {
      if (!Array.isArray(features) || features.length === 0) {
        return [];
      }

      return features.map(feature => {
        const geoJsonFeature = {
          ...feature.geoJson,
          pierName: feature.pierName,
          pierId: feature.pierId,
          berthHpId: feature.berthHpId,
          berthId: feature.berthId,
          properties: {
            ...feature.geoJson?.properties,
            color: feature.id === row?.id ? "blue" : "gray"
          }
        };

        if (Array.isArray(feature.children) && feature.children.length > 0) {
          geoJsonFeature.children = getMapFeatures(feature.children);
        }

        return geoJsonFeature;
      });
    };

    const mapFeatures =
      treeData.value && treeData.value.length > 0
        ? getMapFeatures(treeData.value)
        : [];

    addDialog({
      title: `${transformI18n(action)}${transformI18n(type === "dock" ? "imip.page1.obj6" : "imip.page1.obj4")}`,
      props: {
        formInline: {
          ...row,
          mapFeatures,
          type,
          action
        }
      },
      width: "80%",
      top: "2vh",
      draggable: true,
      fullscreenIcon: true,
      closeOnClickModal: true,
      contentRenderer: () => h(editForm, { formInline: { ...row, mapFeatures, type, action }, ref: formRef }),
      beforeSure: (done) => {
        const { submitForm } = formRef.value;
        submitForm(formData => {
          const apiFunction = action === "imip.button.obj3" ? addHarbor : updateHarbor;

          return apiFunction(formData)
            .then(() => {
              ElMessage.success(`${transformI18n(action)+transformI18n("imip.page4.obj33")}`);
              done();
              
              // 如果是新增操作，当前操作的节点是码头，且有父节点，则将父节点ID添加到展开列表中
              if (action === "imip.button.obj3" && type === "berth" && row) {
                const parentId = row.id;
                if (!expandedKeys.value.includes(parentId)) {
                  expandedKeys.value.push(parentId);
                }
              } 
              // 如果是修改操作，保存当前展开状态
              else if (action === "imip.button.obj1" && row) {
                // 确保被修改的节点的ID存在于展开列表中
                if (
                  row.type === "dock" &&
                  !expandedKeys.value.includes(row.id)
                ) {
                  expandedKeys.value.push(row.id);
                }
              }
              
              onSearch();
            })
            .catch(err => {
              ElMessage.error(`${transformI18n(action)+transformI18n("imip.page4.obj34")}：${err.message}`);
            });
        }, type);
      }
    });
  };

  const handleDelete = async row => {
    const idKey = row.type === "dock" ? "pierId" : "berthId";
    try {
      await delHarbor({
        type: row.type,
        [idKey]: row[idKey]
      });
      ElMessage.success(`成功删除: ${row.name}`);
    } catch (error) {
      console.error("删除失败:", error);
      ElMessage.error(`删除失败: ${error.message || "请稍后重试"}`);
    }
    onSearch();
  };

  const handleSizeChange = size => {
    pagination.pageSize = size;
    onSearch();
  };

  const handleCurrentChange = page => {
    pagination.currentPage = page;
    onSearch();
  };

  const handleSelectionChange = selection => {
    // Handle selection change
  };

  const handleStatusChange = row => {
    ElMessage.success(`状态已更改为: ${row.status === 0 ? "启用" : "停用"}`);
  };

  // 添加处理节点展开状态的方法
  const handleNodeExpand = (row) => {
    const rowId = row.id;
    if (!expandedKeys.value.includes(rowId)) {
      expandedKeys.value.push(rowId);
    }
  };

  const handleNodeCollapse = (row) => {
    const rowId = row.id;
    const index = expandedKeys.value.indexOf(rowId);
    if (index !== -1) {
      expandedKeys.value.splice(index, 1);
    }
  };

  return {
    form,
    loading,
    columns,
    treeData,
    expandedKeys,
    onSearch,
    resetForm,
    pagination,
    openDialog,
    handleDelete,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange,
    handleStatusChange,
    handleNodeExpand,
    handleNodeCollapse
  };
};
