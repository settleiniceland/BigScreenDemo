import { http } from "@/utils/http";
export const getTaskConfigList = (query?: any) => {
  return http.request("post",`/dockTaskConfig/manager/list`,{data: query});
}
export const addTaskConfig = (data?: any) => {
  return http.request("post",`/dockTaskConfig/manager/add`,{data});
}
export const updateTaskConfig = (data?: any) => {
  return http.request("post",`/dockTaskConfig/manager/update`,{data});
}
export const delTaskConfig = (id: number) => {
  return http.request("get",`/dockTaskConfig/manager/del/${id}`);
}