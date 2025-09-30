import { http } from "@/utils/http";

// 查询调度日志列表
export const listJobLog = (query?: any) =>
  http.request("get", "/monitor/jobLog/list", { params: query });

// 删除调度日志
export const delJobLog = (jobLogId: number | string) =>
  http.request("delete", `/monitor/jobLog/${jobLogId}`);

// 清空调度日志
export const cleanJobLog = () =>
  http.request("delete", "/monitor/jobLog/clean");
