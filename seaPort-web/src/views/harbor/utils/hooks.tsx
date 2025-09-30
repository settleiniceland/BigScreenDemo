import { listData } from "@/api/system/dict/data";
import { addDialog, closeDialog, dialogStore } from "@/components/ReDialog";
import { BerthStatusTagOptions } from "@/contans";
import type { PlusColumn } from "plus-pro-components";
import WaitPlanForm from "../components/WaitTable/WaitPlanForm.vue";
import { h, ref } from "vue";
import { useWebSocketStore } from "@/store/modules/websocketStore";
import { getHarborBerthList } from "@/api/pier";
import UnloadWorkList from "../components/UnloadWorkList/index.vue";
import UnloadStatusForm from "@/views/shipPlan/components/UnloadStatus/UnloadStatusForm.vue";
import UnloadOverForm from "@/views/shipPlan/components/UnloadStatus/UnloadOverForm.vue";
import {
  ElMessage,
  ElMessageBox,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem
} from "element-plus";
import { ArrowDownBold } from "@element-plus/icons-vue";
import {
  endHarborUnload,
  pauseHarborUnload,
  recoverHarborUnload
} from "@/api/plan/unloadingOrder";
import BerthDialog from "../components/BerthDialog/index.vue";

const websocketStore = useWebSocketStore();
export enum BerthOperationType {
  /** 更换泊位 */
  CHANGE_BERTH = "1",
  /** 靠泊 */
  BERTHING = "2",
  // 靠泊中
  BERTHING_IN = "3",
  // 预计靠泊
  PREDICT_BERTHING = "4"
}
export const BerthOperationTypeOptions = {
  [BerthOperationType.CHANGE_BERTH]: "更换泊位",
  [BerthOperationType.BERTHING]: "计划单靠泊",
  [BerthOperationType.PREDICT_BERTHING]: "预计靠泊"
};

export const useHarborHook = () => {
  const formRef = ref();

  const workColumns: PlusColumn[] = [
    {
      label: "船名",
      prop: "shipName",
      tableColumnProps: {
        align: "center",
        fixed: "left"
      }
    },
    {
      label: "泊位",
      prop: "hbName"
    },
    {
      label: "日期 ",
      prop: "classTime",
      tableColumnProps: {
        align: "center",
        sortable: true
      }
    },
    {
      label: "班次",
      prop: "classes",
      tableColumnProps: {
        align: "center"
      }
    },
    {
      label: "状态",
      prop: "workType",
      valueType: "select",
      tableColumnProps: {
        sortable: true
      }
    }
  ];
  const getBerthStatusOptions = originData => {
    const countMap = originData.reduce((acc, item) => {
      acc[item.berthStatus] = (acc[item.berthStatus] || 0) + 1
      return acc
    }, {})
    const data = Object.keys(countMap).map(key => ({
      status: key,
      count: countMap[key]
    }))
    const newData = data?.map(item => {
      return {
        value: item?.count ?? 0,
        name: BerthStatusTagOptions[item.status]?.label,
        itemStyle: { color: BerthStatusTagOptions[item.status]?.color }
      };
    });
    return newData;
  };
  const getBerthList = async () => {
    const res: any = await getHarborBerthList();
    if (res.code === 200) {
      return res.rows?.map((item: any) => {
        return {
          label: item.berthName, // 显示的名称
          value: item.berthId // 绑定的值
        };
      });
    }
  };
  const handleWaitPlan = async (row, type) => {
    const msg: any = {
      planId: row.planId
    };
    if (type === BerthOperationType.BERTHING_IN) {
      ElMessageBox.confirm("确定将计划单变为靠泊中吗？", "靠泊中", {
        type: "warning"
      })
        .then(() => {
          msg.type = 6;
          websocketStore.sendMessage(msg, {
            onSuccess: () => {},
            onError: error => {
              ElMessage.error(error.message);
            }
          });
        })
        .catch(() => {});
      return;
    }
    const berthList =
      type === BerthOperationType.CHANGE_BERTH ? await getBerthList() : [];
    addDialog({
      title: BerthOperationTypeOptions[type],
      props: {
        type,
        formInline: {
          hbId: row.berthId,
          planDockingTime: row.planDockingTime
        },
        berthList
      },
      width: "350px",
      alignCenter: true,
      draggable: true,
      fullscreenIcon: true,
      closeOnClickModal: true,
      contentRenderer: () => h(WaitPlanForm, { ref: formRef }),
      beforeSure: (done, {}) => {
        const { submitForm } = formRef.value;
        submitForm(formData => {
          msg.type = 4;
          if (type === BerthOperationType.CHANGE_BERTH) {
            msg.hbId = formData.hbId;
            msg.hbName = formData.hbName;
          } else if (type === BerthOperationType.PREDICT_BERTHING) {
            msg.planDockingTime = formData.planDockingTime;
          } else {
            msg.dockingTime = formData.dockingTime;
          }
          websocketStore.sendMessage(msg, {
            onSuccess: () => {},
            onError: error => {
              ElMessage.error(error.message);
            }
          });
          return done();
        });
      }
    });
  };
  const handleUnloadWork = (row, menu) => {
    const loading = ref(false);
    const { duId, classes, classTime, workType, leader } = row;
    if (!classes || !leader || !classTime) {
      ElMessage.error("请先去卸货单页面填写班次，负责人，班次日期等必要信息");
      return;
    }
    const { label, value } = menu;
    if (value !== "over") {
      addDialog({
        width: "400px",
        title: `${label}作业单`,
        sureBtnLoading: loading.value,
        contentRenderer: () => h(UnloadStatusForm, { ref: formRef }),
        props: {
          // 赋默认值
          id: duId,
          type: value,
          label: label
        },
        beforeSure: done => {
          const { submitForm } = formRef.value;
          loading.value = true;
          submitForm(formData => {
            formData.duId = duId;
            return (
              value === "stop"
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
                closeUnloadListDialog();
                ElMessage.success(`${label}单成功`);
                done();
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
      title: "结束作业单",
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
              closeUnloadListDialog();
            })
            .catch(err => {
              loading.value = false;
              ElMessage.error(`结束作业单失败：${err.message}`);
            });
        });
      }
    });
  };
  const closeUnloadListDialog = () => {
    const index = dialogStore.value.findIndex(d => d.title === "卸货单列表");
    if (index !== -1) {
      closeDialog(dialogStore.value[index], index);
    }
  };
  const openUnloadListDialog = (unloadWorkList: any) => {
    addDialog({
      width: "1300px",
      destroyOnClose: true,
      // fullscreen: true,
      // fullscreenIcon: true,
      top: "10%",
      title: "卸货单列表",
      hideFooter: true,
      contentRenderer: () => h(UnloadWorkList),
      props: {
        unloadWorkList,
        handleUnloadWork
      }
    });
  };

  // // 获取某个字典（例如 "shipStatus"）
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

  const openBerthDialog = async (data: any) => {
    const berthInfo = ref({
      label: data.berthName,
      value: data.berthId
    });
    const berthList = await getBerthList();
    addDialog({
      destroyOnClose: true,
      top: "10%",
      width: "70%",
      hideFooter: true,
      // modal: false ,
      class: "berth-detail-dialog",
      headerRenderer() {
        return h(
          "div",
          {
            class: "berth-dialog",
            style: "display: flex; align-items: center; gap: 10px; color: #fff;"
          },
          [
            h(
              ElDropdown,
              {
                onCommand: (val: any) => (berthInfo.value = val)
              },
              {
                default: () =>
                  h(
                    "div",
                    {
                      style:
                        "display: flex; align-items: center; color: #fff; font-size: 1.4rem;font-weight: bold;"
                    },
                    [
                      h(
                        "span",
                        { style: "font-size: 1.6rem;" },
                        berthInfo.value.label
                      ),
                      h(ArrowDownBold, {
                        style: {
                          width: "1.2rem",
                          height: "1.2rem",
                          marginLeft: "6px"
                        }
                      })
                    ]
                  ),
                dropdown: () =>
                  h(
                    ElDropdownMenu,
                    {
                      style: {
                        maxHeight: "300px",
                        overflowY: "auto"
                      }
                    },
                    berthList.map(opt =>
                      h(
                        ElDropdownItem,
                        {
                          command: {
                            label: opt.label,
                            value: opt.value
                          }
                        },
                        () => opt.label
                      )
                    )
                  )
              }
            )
          ]
        );
      },
      contentRenderer: () =>
        h(BerthDialog, {
          berthInfo: {
            berthName: berthInfo.value.label,
            berthId: berthInfo.value.value
          }
        }),
      style: {
        backgroundColor: "rgba(0, 95, 153, 0.6)",
        backdropFilter: "blur(2px)",
        minWidth: "600px"
      }
    });
  };

  return {
    getBerthStatusOptions,
    getDictOptions,
    workColumns,
    handleWaitPlan,
    openUnloadListDialog,
    handleUnloadWork,
    openBerthDialog
  };
};
