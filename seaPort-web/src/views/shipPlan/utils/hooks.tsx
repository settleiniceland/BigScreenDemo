import { h, ref } from "vue";
import type { PlanListParams } from "./type";
import {
  addHarborPlan,
  archiveHarborPlan,
  deleteHarborPlan,
  downloadPlanImportTemplate,
  exportHarborPlan,
  getPlanList,
  importHarborPlan,
  moveHarborPlan,
  updateHarborPlan,
  updateArchivedPlan,
  downloadPlanRateTemplate,
  exportUnloadWeight,
  updateHarborPlanScreen
} from "@/api/plan/planOrder";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { getHarborBerthList } from "@/api/pier";
import { listData } from "@/api/system/dict/data";
import { downloadFun } from "@/utils/downloadBlob";
import { getMaterialStatusList } from "@/api/pier/material";
import { addDialog } from "@/components/ReDialog";
import MoveForm from "../components/OperationForm/index.vue";
import ChangeScreenStatus from "../components/OperationForm/ChangeScreenStatus.vue";
import { useRoute } from "vue-router";
import { cloneDeep } from "lodash";
import { ExportPlanType } from "@/contans";
import { transformI18n } from "@/plugins/i18n";
export const useShipPlan = () => {
  const router = useRouter();
  const submitPlanLoading = ref(false);
  const path = useRoute().path;
  const columns: any[] = [
    {
      type: "selection",
      align: "left"
    },
    {
      label: transformI18n("imip.page1.obj42"),
      prop: "shipRade",
      minWidth: 100
    },
    // {
    //   label: transformI18n("imip.page1.obj5"),
    //   slot: "planType"
    // },
    {
      label: transformI18n("imip.page1.obj1"),
      prop: "shipName",
      minWidth: 140,
      fixed: "left",
      cellRenderer: ({ row }) => {
        const { shipName, screenStatus } = row;
        if (screenStatus === "0") {
          return shipName;
        } else {
          return (
            <span style="color:rgb(230, 151, 32);font-weight: bold">
              {shipName}
            </span>
          );
        }
      }
    },
    {
      label: transformI18n("imip.items.item5"),
      prop: "remark01",
      minWidth: 200
    },
    {
      label: transformI18n("imip.page1.obj29"),
      prop: "contractFee"
    },
    {
      label: transformI18n("imip.page1.obj44"),
      prop: "params.collectFee"
    },
    {
      label: transformI18n("imip.page1.obj4"),
      prop: "hbName",
      minWidth: 80
    },
    {
      label: transformI18n("imip.page1.obj6"),
      slot: "pierType",
      minWidth: 100
    },
    // { label: transformI18n("imip.page1.obj10"), prop: "imo", minWidth: 110 },
    { label: transformI18n("imip.page1.obj11"), prop: "shipLength", minWidth: 100 },
    {
      label: transformI18n("imip.items.item8"),
      prop: "planTonnage",
      minWidth: 80
    },
    {
      label: transformI18n("imip.page1.obj12"),
      prop: "usageUnit",
      minWidth: 110
    },
    {
      label: transformI18n("imip.page1.obj3"),
      prop: "materialName",
      minWidth: 100
    },
    {
      label: transformI18n("imip.page1.obj13"),
      prop: "unloadWeight",
      minWidth: 90
    },
    { 
      label: transformI18n("imip.page1.obj14"), 
      prop: "tonnage", 
      minWidth: 100 
    },
    {
      label: transformI18n("单位"), 
      prop: "packageNum", 
      minWidth: 100,
      cellRenderer: ({ row }) => {
        const p = row?.packageNum;
        if (p===2) {
          return "按件计";
        }else{
          return "按吨计";
        }
      }
    },
    {
      label: transformI18n("其余物资"), 
      prop: "remark02", 
      minWidth: 55,
    },
    {
      type: "expand",
      slot: "expand"
    },
    // {
    //   label: transformI18n("imip.page1.obj15"),
    //   prop: "cardCount",
    //   minWidth: 80
    // },
    {
      label: transformI18n("imip.page1.obj2"),
      slot: "status",
      fixed: "left",
      minWidth: 85
    },
    {
      label: transformI18n("imip.page1.obj18"),
      prop: "arrivalTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj19"),
      prop: "planDockingTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj20"),
      prop: "dockingTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj21"),
      prop: "operationTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj7"),
      prop: "endTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj23"),
      prop: "outBerthTime",
      minWidth: 160
    },
    {
      label: transformI18n("imip.page1.obj24"),
      prop: "leaveTime",
      minWidth: 160
    },
    // {
    //   label: transformI18n("imip.page1.obj25"),
    //   prop: "moveBerthTime",
    //   minWidth: 160
    // },
    // {
    //   label: transformI18n("imip.page1.obj26"),
    //   prop: "effectiveTime",
    //   minWidth: 100
    // },
    {
      label: transformI18n("imip.page1.obj27"),
      prop: "effectiveRate",
      minWidth: 100,
      cellRenderer: ({ row }) => {
        const rate = row?.effectiveRate;
        if (!rate) {
          return "-";
        }
        return (
          <span style="color:rgb(108, 223, 142);font-weight: bold">{rate}</span>
        );
      }
    },
    {
      label: transformI18n("imip.page1.obj28"),
      prop: "avgDischargeRate",
      minWidth: 100,
      cellRenderer: ({ row }) => {
        const rate = row?.avgDischargeRate;
        if (!rate) {
          return "-";
        }
        return <span style="color: #626aef;font-weight: bold">{rate}</span>;
      }
    },
    // {
    //   label: transformI18n("imip.page1.obj29"),
    //   prop: "demurrageFee",
    //   minWidth: 100,
    //   cellRenderer: ({ row }) => {
    //     const demurrageFee = row?.demurrageFee;
    //     if (!demurrageFee) {
    //       return "-";
    //     }
    //     if (Number(demurrageFee) > 0) {
    //       return (
    //         <span style="color:rgb(61, 128, 214);font-weight: bold">
    //           {demurrageFee}
    //         </span>
    //       );
    //     }
    //     return (
    //       <span style="color:rgb(223, 89, 49);font-weight: bold">
    //         {demurrageFee}
    //       </span>
    //     );
    //   }
    // },
    // {
    //   label: transformI18n("imip.page1.obj30"),
    //   prop: "contractRate",
    //   minWidth: 100
    // },
    // {
    //   label: transformI18n("imip.page1.obj31"),
    //   prop: "contractFee",
    //   minWidth: 100
    // },
    // { label: transformI18n("imip.page1.obj32"), prop: "lastPort" },
    // { label: transformI18n("imip.page1.obj33"), prop: "nextPort" },
    // {
    //   label: transformI18n("imip.page1.obj34"),
    //   prop: "shipAgency",
    //   minWidth: 130
    // },
    // { label: transformI18n("imip.page1.obj35"), prop: "draft", minWidth: 100 },
    {
      label: transformI18n("imip.page1.obj36"),
      prop: "createTime",
      minWidth: 100
    },
    {
      label: transformI18n("imip.page1.obj37"),
      prop: "remark",
      minWidth: 100
    },
    {
      label: transformI18n("imip.page1.obj38"),
      slot: "operation",
      fixed: "right",
      minWidth: path == "/archivePlan" ? 145 : 190
    }
  ];
  const planData = ref([]);
  const total = ref(0);
  const loading = ref(false);
  const searchParams: PlanListParams & Record<string, any> = ref({
    pageSize: 50,
    pageNum: 1
  });
  const berthListOptions = ref([]);
  const formRef = ref();
  const summaryData = ref({});

  const getPlanData = async () => {
    loading.value = true;
    try {
      const time = searchParams.value.endTime;
      const newData = Object.fromEntries(
        Object.entries(searchParams.value).filter(([key]) => key !== "endTime")
      );
      if (time) {
        newData.endStartTime = time?.[0];
        newData.endEndTime = time?.[1];
      }
      if (path == "/archivePlan") {
        newData.isArchived = 1;
      } else {
        newData.isArchived = 0;
      }
      const res: any = await getPlanList(newData);
      if (res.code === 200) {
        planData.value = res.rows;
        planData.value.forEach((item) => {
          if(item.remark02===null){
            item.remark02='0';
          }
        })
        summaryData.value = res.mapParams;
        total.value = res.total;
        loading.value = false;
      } else {
        loading.value = false;
        ElMessage({
          message: res.msg,
          type: "error"
        });
      }
    } catch (error) {
      loading.value = false;
      console.error("获取计划单列表失败", error);
    }
  };
  const updatePlanOrder = async (values, id) => {
    submitPlanLoading.value = true;
    let data: any = {};
    if (id) {
      const res =
        path == "/checkPlan"
          ? await updateArchivedPlan(values)
          : await updateHarborPlan(values);
      data = res;
    } else {
      const res = await addHarborPlan(values);
      data = res;
    }
    if (data.code === 200) {
      ElMessage({
        message: `${id ? "修改" : "新增"}计划单成功`,
        type: "success"
      });
      submitPlanLoading.value = false;
      if (path == "/editPlan") {
        router.push({
          path: "/shipPlan"
        });
      } else {
        router.push({
          path: "/archivePlan"
        });
      }
    } else {
      submitPlanLoading.value = false;
      ElMessage({
        message: data.msg,
        type: "error"
      });
    }
  };
  const deletePlanOrder = async id => {
    const res: any = await deleteHarborPlan(id);
    if (res.code === 200) {
      ElMessage({
        message: "删除计划单成功",
        type: "success"
      });
      getPlanData();
    } else {
      ElMessage({
        message: res.msg,
        type: "error"
      });
    }
  };
  const getBerthList = async () => {
    const res: any = await getHarborBerthList();
    if (res.code === 200) {
      berthListOptions.value =
        res.rows?.map(item => ({
          label: item.berthName, // 显示的名称
          value: item.berthId // 绑定的值
        })) ?? [];
      return berthListOptions.value;
    }
    ElMessage({
      message: res.msg ?? " 未知错误",
      type: "error"
    });
    return [];
  };
  const downloadPlanTemplate = async () => {
    try {
      const res: any = await downloadPlanImportTemplate();
      // 尝试解析 Blob 内容
      const isJson = res.type.includes("application/json");
      if (isJson) {
        ElMessage({
          message: "下载失败，请重试",
          type: "error"
        });
        return;
      }
      downloadFun(res, "计划单模版");
      ElMessage({
        message: "下载成功",
        type: "success"
      });
    } catch (error) {
      console.error("下载失败", error);
    }
  };
  const importPlanOrders = async () => {
    const res: any = await importHarborPlan({});
    if (res.code === 200) {
      ElMessage({
        message: "导入成功",
        type: "success"
      });
      getPlanData();
    } else {
      ElMessage({
        message: res.msg,
        type: "error"
      });
    }
  };
  const exportPlanOrders = async (
    ids: string | string[],
    type: ExportPlanType
  ) => {
    const filteredParams = cloneDeep(searchParams.value);
    delete filteredParams.pageNum;
    delete filteredParams.pageSize;
    const params = { ids, ...filteredParams };
    try {
      const res: any =
        type === ExportPlanType.Plan
          ? await exportHarborPlan(params)
          : type === ExportPlanType.PlanRate
            ? await downloadPlanRateTemplate(params)
            : await exportUnloadWeight(params);
      // 尝试解析 Blob 内容
      const isJson = res.type.includes("application/json");
      // 尝试解析 Blob 内容
      if (isJson) {
        const text = await res.text();
        try {
          const json = JSON.parse(text);
          if (json.code !== 200) {
            ElMessage({
              message: json.msg,
              type: "error"
            });
            throw new Error(json.msg || "导出失败");
          }
        } catch {
          // 如果解析失败，说明可能是正常的二进制数据
        }
        return;
      }
      const title = transformI18n(type);
      downloadFun(res, title);
      ElMessage({
        message: "导出成功",
        type: "success"
      });
    } catch (error) {
      console.error("导出失败", error);
    }
  };
  let key = ref(0);
  // 获取某个字典（例如 "shipStatus"）
  const getDictOptions = async (type: string) => {
    const res: any = await listData({
      dictType: type,
      pageSize: 50
    });
    if (res.code === 200) {
      return res.rows?.map((item: any) => {
        return {
          label: item.dictLabel,
          value: item.dictValue
        };
      });
    }
  };
  /** 获取列表数据 */
  async function fetchMaterialName() {
    try {
      const { rows } = (await getMaterialStatusList()) as any;
      return rows?.map(item => {
        return {
          label: item.materialName,
          value: item.materialName,
          remark01: item.remark01
        };
      });
    } catch (error) {
      ElMessage.error("获取数据失败", error);
      return [];
    }
  }
  const handlePlanOperation = async (info?: any, type?: string) => {
    const loading = ref(false);
    const label = type === "archive" ? "计划单归档" : "移泊计划单";
    const props =
      type === "archive"
        ? { ids: info, type }
        : {
            moveBerthTime: info?.status === "8" ? info?.moveBerthTime : null,
            berthListOptions
          };
    addDialog({
      width: "25%",
      top: "15%",
      title: label,
      sureBtnLoading: loading.value,
      contentRenderer: () => h(MoveForm, { ref: formRef }),
      props,
      beforeSure: done => {
        const { submitForm } = formRef.value;
        loading.value = true;
        submitForm(formData => {
          let data: any = {};
          const { monthQuery, ...rest } = formData;
          if (type === "archive") {
            data.ids = info;
            data.monthQuery = monthQuery;
          } else {
            data = {
              ...rest,
              id: info?.id
            };
          }
          const res =
            type === "archive" ? archiveHarborPlan(data) : moveHarborPlan(data);
          return res
            .then(() => {
              loading.value = false;
              ElMessage.success(`${label}成功`);
              done();
              getPlanData();
            })
            .catch(err => {
              loading.value = false;
              ElMessage.error(`${label}失败：${err.message}`);
            });
        });
      }
    });
  };

  const changeScreenStatus = async (id: string, screenStatus: string) => {
    loading.value = false;
    addDialog({
      width: "25%",
      top: "15%",
      title: "大屏统计显示",
      sureBtnLoading: loading.value,
      contentRenderer: () => h(ChangeScreenStatus, { ref: formRef }),
      props: {
        screenStatus
      },
      beforeSure: done => {
        const { submitForm } = formRef.value;
        loading.value = true;
        submitForm(formData => {
          const res = updateHarborPlanScreen({
            id,
            screenStatus: formData.screenStatus
          });
          return res
            .then(() => {
              loading.value = false;
              ElMessage.success(`修改成功`);
              done();
              getPlanData();
            })
            .catch(err => {
              loading.value = false;
              ElMessage.error(`修改成功：${err.message}`);
            });
        });
      }
    });
  };

  return {
    columns,
    loading,
    getPlanData,
    searchParams,
    planData,
    total,
    updatePlanOrder,
    deletePlanOrder,
    submitPlanLoading,
    getBerthList,
    berthListOptions,
    downloadPlanTemplate,
    importPlanOrders,
    exportPlanOrders,
    getDictOptions,
    key,
    fetchMaterialName,
    handlePlanOperation,
    summaryData,
    changeScreenStatus
  };
};
