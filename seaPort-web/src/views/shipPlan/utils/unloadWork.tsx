import { addDialog } from "@/components/ReDialog";
import { ElMessage } from "element-plus";
import type { PlusColumn } from "plus-pro-components";
import { h, ref } from "vue";
import UnloadOverForm from "../components/UnloadStatus/UnloadOverForm.vue";
import UnloadStatusForm from "../components/UnloadStatus/UnloadStatusForm.vue";
import { listData } from "@/api/system/dict/data";
import {
  endHarborUnload,
  exportHarborUnload,
  exportHarborUnloadLog,
  pauseHarborUnload,
  recoverHarborUnload,
  updateHarborUnloadLog
} from "@/api/plan/unloadingOrder";
import { downloadFun } from "@/utils/downloadBlob";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { CaretRight, SwitchButton } from "@element-plus/icons-vue";
import Pause from "@iconify-icons/ri/pause-circle-line";
import UpdateForm from "../components/LogTable/UpdateForm.vue";
import { UnloadWorkTypeOptions } from "@/contans";
import { transformI18n } from "@/plugins/i18n";
export const useUnloadWork = () => {
  const formRef = ref();
  const moreBtn = ref(null);
  moreBtn.value = [
    {
      key: "0",
      label: transformI18n("imip.page3.obj19"),
      value: "recover",
      icon: CaretRight,
      color: "#67C23A"
    },
    {
      key: "1",
      value: "stop",
      label: transformI18n("imip.page3.obj20"),
      icon: useRenderIcon(Pause),
      color: "#E6A23C"
    },
    {
      key: "2",
      label: transformI18n("imip.page3.obj21"),
      value: "over",
      icon: SwitchButton,
      color: "#909399"
    }
  ];
  const handleMenu = (workType: string) => {
    return moreBtn.value.filter(item => item.key !== workType);
  };

  const unloadColums: any[] = [
    {
      type: "expand",
      label: "",
      slot: "expand",
      minWidth: 60
    },
    {
      type: "selection",
      align: "left",
      width: 55
    },

    {
      label: transformI18n("imip.page2.obj36"),
      prop: "classTime",
      minWidth: 120,
      sortable: true
    },
    {
      label: transformI18n("imip.page1.obj3"),
      prop: "params.MaterialName",
      minWidth: 100,
    },
    {
      label: transformI18n("imip.page1.obj12"),
      prop: "params.UsageUnit",
      minWidth: 100,
    },
    { label: transformI18n("imip.page2.obj37"), prop: "classes", minWidth: 100 },
    {
      label: transformI18n("imip.page3.obj3"),
      prop: "leader",
      minWidth: 100
    },
    { label: transformI18n("imip.page3.obj4"), prop: "workEquipment", minWidth: 100 },
    {
      label: transformI18n("imip.page3.obj1"),
      slot: "workType",
      minWidth: 100
    },

    {
      label: transformI18n("imip.page3.obj5"),
      prop: "totalUnloadWeight",
      minWidth: 90
    },
    {
      label: transformI18n("imip.page3.obj6"),
      prop: "unloadNum",
      minWidth: 80
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
    {
      label: transformI18n("imip.page1.obj27"),
      prop: "effectiveRate",
      minWidth: 100,
      cellRenderer: ({ row }) => {
        const rate = row?.effectiveRate;
        if (!rate) {
          return "-";
        }
        return <span style="color: #67C23A;font-weight: bold">{rate}</span>;
      }
    },
    {
      label: transformI18n("imip.page3.obj7"),
      prop: "startTime",
      minWidth: 160,
      cellRenderer: ({ row }) => {
        const time = row?.startTime;
        if (!time) {
          return "-";
        }
        return <span style="color: #409EFF">{time}</span>;
      }
    },
    {
      label: transformI18n("imip.page3.obj8"),
      prop: "endTime",
      minWidth: 160,
      cellRenderer: ({ row }) => {
        const time = row?.endTime;
        if (!time) {
          return "-";
        }
        return <span style="color: #67C23A">{time}</span>;
      }
    },
    {
      label: transformI18n("imip.page1.obj26"),
      prop: "effectiveTime",
      minWidth: 130,
      cellRenderer: ({ row }) => {
        const rate = row?.effectiveTime;
        if (!rate) {
          return "-";
        }
        return <span style="color: #67C23A">{rate}</span>;
      }
    },
    {
      label: transformI18n("imip.page3.obj9"),
      prop: "stopCount",
      width: 100
    },
    {
      label: transformI18n("imip.page3.obj10"),
      prop: "pauseInterval",
      minWidth: 100,
      cellRenderer: ({ row }) => {
        const rate = row?.pauseInterval;
        if (!rate) {
          return "-";
        }
        return <span style="color:#E6A23C">{rate}</span>;
      }
    },

    {
      label: transformI18n("imip.page1.obj37"),
      prop: "remark",
      width: 150,
      showOverflowTooltip: true
    },
    {
      label: transformI18n("imip.page1.obj36"),
      minWidth: 120,
      prop: "createTime",
      sortable: true
    },
    { label: transformI18n("imip.page1.obj38"), slot: "operation", fixed: "right", minWidth: 200 }
  ];
  const formColumns: PlusColumn[] = [
    {
      label: transformI18n("imip.page2.obj36"),
      prop: "classTime",
      valueType: "date-picker",
      fieldProps: {
        type: "date",
        valueFormat: "YYYY-MM-DD"
      }
    },

    {
      label: transformI18n("imip.page2.obj37"),
      prop: "classes",
      valueType: "select",
      options: [
        {
          label: transformI18n("imip.page2.obj38"),
          value: "白班"
        },
        {
          label: transformI18n("imip.page2.obj39"),
          value: "夜班"
        }
      ]
    },
    {
      label: transformI18n("imip.page3.obj1"),
      prop: "workType",
      valueType: "select",
      options: UnloadWorkTypeOptions
    }
  ];
  const handleUnloadWork = (row, type, callback?: () => void) => {
    const loading = ref(false);
    const { duId, classes, workType, classTime, leader } = row;
    if (!classTime || !leader || !classes) {
      ElMessage.error(transformI18n("imip.page3.obj2"));
      return;
    }
    const label = moreBtn.value.find(item => item.value === type)?.label;
    if (type !== "over") {
      addDialog({
        width: "400px",
        title: label,
        sureBtnLoading: loading.value,
        contentRenderer: () => h(UnloadStatusForm, { ref: formRef }),
        props: {
          // 赋默认值
          id: duId,
          type,
          label
        },
        beforeSure: done => {
          const { submitForm } = formRef.value;
          loading.value = true;
          submitForm(formData => {
            formData.duId = duId;
            return (
              type === "stop"
                ? pauseHarborUnload({
                    duId,
                    pauseReason: formData.pauseReason,
                    startTime: formData.time
                  })
                : recoverHarborUnload({
                    duId,
                    endTime: formData.time
                  })
            )
              .then(() => {
                loading.value = false;
                ElMessage.success(`${label}单成功`);
                done();
                callback?.();
              })
              .catch(err => {
                loading.value = false;
                ElMessage.error(`${label}单失败：${err.message}`);
              });
          });
        }
      });
      return;
    }
    addDialog({
      width: "35%",
      title: label,
      sureBtnLoading: loading.value,
      contentRenderer: () => h(UnloadOverForm, { ref: formRef }),
      props: {
        // 赋默认值
        id: duId,
        label: "结束",
        classes,
        workType
      },
      beforeSure: done => {
        const { submitForm } = formRef.value;
        loading.value = true;
        submitForm(formData => {
          formData.duId = duId;
          return endHarborUnload(formData)
            .then(() => {
              loading.value = false;
              ElMessage.success(`结束作业单成功`);
              done();
              callback?.();
            })
            .catch(err => {
              loading.value = false;
              ElMessage.error(`结束作业单失败：${err.message}`);
            });
        });
      }
    });
  };
  const updateUnloadLogDialog = (row: any, callback?: () => void) => {
    const loading = ref(false);
    addDialog({
      width: "35%",
      title: "修改卸货单日志",
      sureBtnLoading: loading.value,
      contentRenderer: () => h(UpdateForm, { ref: formRef }),
      props: {
        // 赋默认值
        data: row
      },
      beforeSure: done => {
        const { submitForm } = formRef.value;
        loading.value = true;
        submitForm(formData => {
          formData.dudId = row.dudId;
          return updateHarborUnloadLog(formData)
            .then(() => {
              loading.value = false;
              ElMessage.success(`修改日志成功`);
              done();
              callback?.();
            })
            .catch(err => {
              loading.value = false;
              ElMessage.error(`修改日志失败：${err.message}`);
            });
        });
      }
    });
  };
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
  const exportWorkLogList = async (planId: string) => {
    try {
      const res: any = await exportHarborUnloadLog({ planId });
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
      downloadFun(res, "作业卸货单日志列表");
      ElMessage({
        message: "导出成功",
        type: "success"
      });
    } catch (error) {
      console.error("导出失败", error);
    }
  };
  const exportWorkList = async (planId: string, ids: string | string[]) => {
    try {
      const res: any = await exportHarborUnload({ planId, ids: ids });
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
      downloadFun(res, "作业卸货单列表");
      ElMessage({
        message: "导出成功",
        type: "success"
      });
    } catch (error) {
      console.error("导出失败", error);
    }
  };
  return {
    unloadColums,
    formColumns,
    handleMenu,
    moreBtn,
    getDictOptions,
    exportWorkList,
    exportWorkLogList,
    handleUnloadWork,
    updateUnloadLogDialog
  };
};
