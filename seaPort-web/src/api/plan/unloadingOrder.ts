import { http } from "@/utils/http";
import type { Result } from "postcss";
// // 查询计划单列表
export const getUnloadList = (query?: any) =>
  http.request("get", `/harbor/unloadWork/list`, {
    params: query
  });
// 新增卸货单
export const addHarborUnload = (data: any) => {
  return http.request<Result>("post", `/harbor/unloadWork`, { data });
};
// 修改卸货单
export const updateHarborUnload = (data: any) => {
  return http.request<Result>("put", `/harbor/unloadWork`, { data });
};
// 查看卸货单详情
export const getHarborUnloadInfo = (duId: string) => {
  return http.request<Result>("get", `/harbor/unloadWork/${duId}`);
};
//删除计划单
export const deleteHarborUnload = (duId: string | string[]) => {
  return http.request<Result>("delete", `/harbor/unloadWork/${duId}`);
};

// 卸货日志列表
export const harborDetailList = (query: any) => {
  return http.request<Result>("get", `/harbor/detail/list`, {
    params: query
  });
};

//暂停
export const pauseHarborUnload = (data: any) => {
  return http.request<Result>("put", `/harbor/unloadWork/stop`, {
    data
  });
};

//恢复
export const recoverHarborUnload = (data: any) => {
  return http.request<Result>("put", `/harbor/unloadWork/recover`, {
    data
  });
};

//结束
export const endHarborUnload = (data: any) => {
  return http.request<Result>("put", `/harbor/unloadWork/jobOver`, {
    data
  });
};

//导出卸货列表
export const exportHarborUnload = (data: any) => {
  return http.request<Result>("post", `/harbor/unloadWork/export`, {
    data,
    responseType: "blob" // 关键点：确保接收的是二进制流
  });
};
//导出卸货单日志列表
export const exportHarborUnloadLog = (data: any) => {
  return http.request<Result>("post", `/harbor/detail/export`, {
    data,
    responseType: "blob" // 关键点：确保接收的是二进制流
  });
};

// 完成卸货单
export const finishHarborUnload = (planId: string) => {
  return http.request<Result>(
    "put",
    `/harbor/unloadWork/jobComplete/${planId}`
  );
};
//恢复卸货单
export const cancelCompleteHarborUnload = (planId: string) => {
  return http.request<Result>(
    "put",
    `/harbor/unloadWork/cancelComplete/${planId}`
  );
};

//修改卸货单日志
export const updateHarborUnloadLog = (data: any) => {
  return http.request<Result>("put", `/harbor/detail`, {
    data
  });
};
//更改是否记录日志
export const updateRecordStatus = (data: any) => {
  return http.request<Result>("put", `harbor/detail/updateRecord`, {
    data
  });
};
