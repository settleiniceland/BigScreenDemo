import { http } from "@/utils/http";

// 查询定时任务调度列表
export const listJob = query =>
  http.request("get", "/monitor/job/list", { params: query });

// 查询定时任务调度详细
export const getJob = jobId => http.request("get", `/monitor/job/${jobId}`);

// 新增定时任务调度
export const addJob = data => http.request("post", "/monitor/job", { data });

// 修改定时任务调度
export const updateJob = data => http.request("put", "/monitor/job", { data });

// 删除定时任务调度
export const delJob = jobId => http.request("delete", `/monitor/job/${jobId}`);

// 任务状态修改
export const changeJobStatus = (jobId, status) =>
  http.request("put", "/monitor/job/changeStatus", { data: { jobId, status } });

// 定时任务立即执行一次
export const runJob = (jobId, jobGroup) =>
  http.request("put", "/monitor/job/run", { data: { jobId, jobGroup } });
