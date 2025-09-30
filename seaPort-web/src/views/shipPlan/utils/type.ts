export interface PlanListParams {
  pageNum?: number;
  pageSize?: number;
  shipName?: string;
  status?: string;
  materialName?: string;
  hbName?: string;
  endTime?: string;
  usageUnit?: string;
}
export enum OptionPlanStatus {
  "ADD" = "imip.button.obj3",
  "EDIT" = "imip.button.obj1",
  "CHECK" = "imip.page2.obj23"
}
