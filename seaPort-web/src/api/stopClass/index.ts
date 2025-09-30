import { http } from "@/utils/http";
export const getStopClassList = (query?: any) => {
  return http.request("post",`/dockStopClass/manager/list`,{data: query});
}
export const addStopClass = (data?: any) => {
  return http.request("post",`/dockStopClass/manager/add`,{data});
}
export const updateStopClass = (data?: any) => {
  return http.request("post",`/dockStopClass/manager/update`,{data});
}
export const delStopClass = (id: number) => {
  return http.request("get",`/dockStopClass/manager/del/${id}`);
}