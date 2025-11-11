import { http } from "@/utils/http";
import { Result } from "postcss";

export const allDept = () => {
  return http.request<Result>("get","/newScreen/allDept");
}
export const getBerchs = (deptId)=>{
  return http.request<Result>("get","/newScreen/getBerchs/"+deptId);
}
export const getStatistics = (deptId)=>{
  return http.request<Result>("get","/newScreen/getStatisticsData/"+deptId);
}
export const getArriveLeavingPlan = (deptId)=>{
  return http.request<Result>("get","/newScreen/getArriveLeavingPlan/"+deptId);
}
export const getPlan2ByDeptId = (deptId)=>{
  return http.request<Result>("get","/newScreen/getPlan2/"+deptId)
}
export const getOldPlan3 = (deptId)=>{
  return http.request<Result>("get","/newScreen/getWorkingPlan/"+deptId)
}
export const getPopData = (deptId,berchCode)=>{
  return http.request<Result>("get","/newScreen/getPopWindowData/"+deptId+"/"+berchCode);
}
export const getAllMaterial = ()=>{
  return http.request<Result>("get","/harbor/material/getAll")
}