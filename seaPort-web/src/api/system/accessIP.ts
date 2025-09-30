import { http } from "@/utils/http";

// 查询列表
export const listIp = (query?: any) =>
  http.request("get", `/system/address/list`, { params: query });
// 查
export const getIpInfo = (id: string) =>
  http.request("get", `/system/address/${id}`);

// 新增
export const addIp = (data: any) =>
  http.request("post", `/system/address`, { data });

// 修改部门
export const updateIp = (data: any) =>
  http.request("put", `/system/address`, { data });

// 删除部门
export const delIp = (ids: string) =>
  http.request("delete", `/system/address/${ids}`);
