export const UnloadWorkTypeOptions = [
  {
    label: "进行中",
    value: "0"
  },
  { label: "暂停", value: "1" },
  { label: "结束", value: "2" }
];

export const UnloadWorkTypeTagOptions = {
  0: {
    label: "进行中",
    color: "primary",
    type: "processing"
  },
  1: {
    label: "暂停",
    color: "warning",
    type: "warning"
  },
  2: {
    label: "结束",
    color: "success",
    type: "success"
  },
  4: {
    label: "未开始",
    color: "info",
    type: "default"
  }
};
export const PlanType = [
  { label: "速遣", value: "2" },
  { label: "滞期", value: "1" }
];
export const PlanTypeOptionsObj = {
  1: {
    label: "滞期",
    value: "1",
    type: "red"
  },
  2: {
    label: "速遣",
    value: "2",
    type: "cyan"
  }
};
export const BerthStatusTagOptions = {
  0: {
    label: "空闲",
    value: "0",
    type: "primary",
    color: "#409EFF"
  },
  1: {
    label: "占用中",
    value: "1",
    type: "success",
    color: "#67C23A"
  },
  2: {
    label: "维护",
    value: "2",
    color: "orange",
    type: "warning"
  },
  3: {
    label: "暂停",
    value: "3",
    type: "danger",
    color: "#F56C6C"
  }
};

export const BerthStatusOptions = [
  {
    label: "imip.page4.obj35",
    value: "0"
  },
  {
    label: "imip.page4.obj36",
    value: "1"
  },
  {
    label: "imip.page4.obj37",
    value: "2"
  },
  {
    label: "imip.page4.obj38",
    value: "3"
  }
];

export const PireTypeOptions = [
  {
    label: "imip.items.item1",
    value: "1"
  },
  {
    label: "imip.items.item2",
    value: "2"
  }
];

export const PireTypeOptionsObj = {
  1: {
    label: "大码头",
    value: "1",
    color: "info"
  },
  2: {
    label: "驳船码头",
    value: "2",
    color: "warning"
  }
};

export const Colors = {
  0: "#FFC107", // 计划（琥珀黄）
  1: "#36CFA9", // 在途（亮蓝色）
  2: "#FF5355", // 等泊（深紫色）
  3: "#2ECC71", // 靠泊（绿色）
  4: "#409EFF", // 在装卸（橙色）
  5: "#9B59B6", // 待离泊（紫丁香色，新推荐）
  6: "#F79BBA", // 待离港（蓝色）
  7: "#27AE60", // 完成（深绿色）
  8: "#2980B9", // 待回靠（深蓝色）
  9: "#E74C3C", // 移泊（红色）
  10: "#F39C12" // 靠泊中（亮黄色）
};

export const PlanStatusOptions = {
  3: {
    label: "待作业",
    value: 4,
    color: "#409EFF"
  },
  4: {
    label: "在作业",
    value: 5,
    color: "#67C23A"
  },
  5: {
    label: "待离泊",
    value: 6,
    color: "#9B59B6"
  },
  10: {
    label: "靠泊中",
    value: 10,
    color: "#F39C12"
  }
};
export const UnloadStatusOptions = [
  {
    label: "未卸货",
    value: "0",
    type: "info"
  },
  {
    label: "正在卸货",
    value: "1",
    type: "primary"
  },
  {
    label: "卸货完成",
    value: "2",
    type: "success"
  },
  {
    label: "恢复卸货",
    value: "3",
    type: "primary"
  }
];

export const SumMap = {
  shipCount: "imip.page1.obj39",
  totalWeight: "imip.page1.obj40",
  totalDemurrageFee: "imip.page1.obj29",
  totalDispatchFee: "imip.page1.obj41"
};

export const SumMap_Archive = {
  shipCount: "imip.page1.obj39",
  totalWeight: "imip.page1.obj40",
  totalDemurrageFee: "imip.page1.obj29",
  totalDispatchFee: "imip.page1.obj41",
  effectiveRate: "imip.page1.obj27",
  avgRate: "imip.page1.obj28"
};

export enum ExportPlanType {
  Plan = "imip.button.obj12",
  PlanRate = "imip.button.obj13",
  PlantUnloadWeight = "imip.button.obj14"
}
