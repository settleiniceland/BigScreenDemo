import { http } from "@/utils/http";
const serverUrl = "system";

// 查询字典数据列表
export const listData = (query?: any) =>
  http.request("get", `${serverUrl}/dict/data/list`, { params: query });

// 查询字典数据详细
export const getData = (dictCode: any) =>
  http.request("get", `${serverUrl}/dict/data/${dictCode}`);

// 根据字典类型查询字典数据信息
export const getDicts = (dictType: any) =>
  http.request("get", `${serverUrl}/dict/data/type/${dictType}`);

// 新增字典数据
export const addData = (data: any) =>
  http.request("post", `${serverUrl}/dict/data`, { data });

// 修改字典数据
export const updateData = (data: any) =>
  http.request("put", `${serverUrl}/dict/data`, { data });

// 删除字典数据
export const delData = (dictCode: any) =>
  http.request("delete", `${serverUrl}/dict/data/${dictCode}`);
