import { http } from "@/utils/http";

// 新增物资
export const addMaterial = (query: any) =>
  http.request("post", `/harbor/material`, { data: query });

// 修改物资
export const updateMaterial = (query: any) =>
  http.request("put", `/harbor/material`, { data: query });

// 获取物资列表
export const getMaterialList = (query?: any) =>
  http.request("get", `/harbor/material/list`, { params: query });

// 获得启用列表
export const getMaterialStatusList = (query?: any) =>
  http.request("get", `/harbor/material/enableList`, { params: query });
// 删除物资（修正传参）
export const delMaterial = (id: any) =>
  http.request("delete", `/harbor/material/${id}`);

// 启用禁用
export const updateStatus = (id: number, materialStatus: string) =>
  http.request("put", `/harbor/material/controls/${id}/${materialStatus}`);
