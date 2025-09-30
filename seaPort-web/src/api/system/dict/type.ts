import { http } from "@/utils/http";
const serverUrl = "system";

// 查询字典类型列表
export const listType = (query?: any) =>
  http.request("get", `${serverUrl}/dict/type/list`, { params: query });

// 查询字典类型详细
export const getType = (dictId: any) =>
  http.request("get", `${serverUrl}/dict/type/${dictId}`);

// 新增字典类型
export const addType = (data: any) =>
  http.request("post", `${serverUrl}/dict/type`, { data });

// 修改字典类型
export const updateType = (data: any) =>
  http.request("put", `${serverUrl}/dict/type`, { data });

// 删除字典类型
export const delType = (dictId: any) =>
  http.request("delete", `${serverUrl}/dict/type/${dictId}`);

// 刷新字典缓存
export const refreshCache = () =>
  http.request("delete", `${serverUrl}/dict/type/refreshCache`);

// 获取字典选择框列表
export const optionselect = () =>
  http.request("get", `${serverUrl}/dict/type/optionselect`);
