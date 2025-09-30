import { http } from "@/utils/http";
import { Result } from "postcss";

export const addNewPlan = (data?: any) => {
  return http.request<Result>("post","/harbor/plan/add",{data});
}
export const getPlan = (id:string)=>{
  return http.request<Result>("get",`/harbor/plan/getById/${id}`);
}

export const updateNewPlan = (data?: any) => {
  return http.request<Result>("post","/harbor/plan/update",{data});
}