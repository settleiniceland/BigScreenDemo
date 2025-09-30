import { http } from "@/utils/http";
import type { Result } from "postcss";
// 新增码头/泊位
export const addHarbor = (query?: any) =>
  http.request("post", `/harbor/berth`, { data: query });

//修改码头/泊位
export const updateHarbor = (query?: any) =>
  http.request("put", `/harbor/berth`, { data: query });

// 列表接口
export const getHarborList = (query?: any) =>
  http.request("get", `/harbor/pier/list`, { params: query });

// 删除泊位或者码头
export const delHarbor = (query?: any) =>
  http.request("put", `/harbor/berth/remove`, { data: query });

// 查询泊位信息列表
export const getHarborBerthList = (query?: any) => {
  return http.request("get", `/harbor/berth/list`, { params: query });
};

//码头列表搜索
export const getHarborPierList = (query?: any) => {
  return http.request("get", `/harbor/pier/selectList`, { params: query });
};

// 查询泊位使用率
export const getHarborBerthUsageRate = (query?: any) => {
  return http.request("get", `/harbor/statistics/berthUsageRate`, {
    params: query
  });
};

// 导出泊位使用率
export const exportHarborBerthUsageRate = (data: any) => {
  return http.request<Result>(
    "post",
    `/harbor/statistics/exportBerthUsageRate`,
    {
      data,
      responseType: "blob" // 关键点：确保接收的是二进制流
    }
  );
};

// 导出泊位耗时明细
export const exportHarborBerthTime = (data: any) => {
  return http.request<Result>(
    "post",
    `/harbor/statistics/exportBerthUsageDetail`,
    {
      data,
      responseType: "blob" // 关键点：确保接收的是二进制流
    }
  );
};

//查询小时斜率标准列表
export const getPierHourRateList = (query?: any) => {
  return http.request("get", `/harbor/range/list`, { params: query });
};

//查询小时斜率标准详情
export const getPierHourRateDetail = (id?: string) => {
  return http.request("get", `/harbor/range/${id}`);
};

//新增小时斜率标准
export const addPierHourRate = (data?: any) => {
  return http.request("post", `/harbor/range`, { data });
};

//修改小时斜率标准
export const updatePierHourRate = (data?: any) => {
  return http.request("put", `/harbor/range`, { data });
};

//删除小时斜率标准
export const delPierHourRate = (ids?: string) => {
  return http.request("delete", `/harbor/range/${ids}`);
};
