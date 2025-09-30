import { http } from "@/utils/http";
import type { Result } from "postcss";
// 查询计划单列表
export const getPlanList = (query?: any) =>
  http.request("get", `/harbor/plan/list`, { params: query });
// 新增计划单
export const addHarborPlan = (data: any) => {
  return http.request<Result>("post", `/harbor/plan`, { data });
};
// 修改计划单
export const updateHarborPlan = (data: any) => {
  return http.request<Result>("put", `/harbor/plan`, { data });
};
// 查看计划单详情
export const getHarborPlan = (id: string) => {
  return http.request<Result>("get", `/harbor/plan/${id}`);
};
//删除计划单
export const deleteHarborPlan = (id: string | string[]) => {
  return http.request<Result>("delete", `/harbor/plan/${id}`);
};
// 下载计划单模版
export const downloadPlanImportTemplate = () => {
  return http.request<Result>("post", `/harbor/plan/importTemplate`, {
    responseType: "blob" // 关键点：确保接收的是二进制流
  });
};
// 导入计划单
export const importHarborPlan = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/importData`, {
    data,
    headers: { "Content-Type": "multipart/form-data" }
  });
};
// 导出计划单
export const exportHarborPlan = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/export`, {
    data,
    responseType: "blob" // 关键点：确保接收的是二进制流
  });
};

//移泊计划单
export const moveHarborPlan = (data: any) => {
  return http.request<Result>("post", "/harbor/plan/moveBerth", { data });
};

//归档计划单
export const archiveHarborPlan = (data: any) => {
  return http.request<Result>(
    "put",
    `/harbor/plan/archived/${data?.ids}/${data?.monthQuery}`,
    {
      data
    }
  );
};

//归档列表
export const archivePlanList = (query: any) => {
  return http.request<Result>("get", `/harbor/plan/archivedList`, {
    params: query
  });
};

//修改归档计划单
export const updateArchivedPlan = (data: any) => {
  return http.request<Result>("put", `/harbor/plan/archived/update`, {
    data
  });
};

//取消归档
export const cancelArchivedPlan = (ids: string | string[]) => {
  return http.request<Result>("put", `/harbor/plan/cancelArchived/${ids}`);
};

// 导出计划单卸率模板
export const downloadPlanRateTemplate = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/effectiveRateExport`, {
    data,
    responseType: "blob" // 关键点：确保接收的是二进制流
  });
};

// 导入计划单卸率
export const importHarborRatePlan = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/importEffectiveRate`, {
    data,
    headers: { "Content-Type": "multipart/form-data" }
  });
};

//批量导出已作业量
export const exportUnloadWeight = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/exportUnloadWeight`, {
    data,
    responseType: "blob" // 关闭 blob 浏览器弹窗
  });
};

//批量导入已卸货量
export const importUnloadWeight = (data: any) => {
  return http.request<Result>("post", `/harbor/plan/batchUpdateUnloadWeight`, {
    data,
    headers: { "Content-Type": "multipart/form-data" }
  });
};

//靠泊中
export const toPlanDocking = data => {
  return http.request<Result>("post", `/harbor/plan/toDocking`, {
    data
  });
};

//修改计划单大屏显示
export const updateHarborPlanScreen = (data: any) => {
  return http.request<Result>(
    "put",
    `/harbor/plan/updateScreenStatus/${data.id}/${data.screenStatus}`
  );
};

//获取泊位计划单
export const getHarborBerthPlan = (berthId: string) => {
  return http.request<Result>(
    "get",
    `/harbor/screenArea/selectDetailByBerthName/${berthId}`
  );
};
