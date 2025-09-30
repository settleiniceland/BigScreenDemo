import { http } from "@/utils/http";

export const getBerthUserList = (query?: any) => {
  return http.request("get",`/userBerch/map/list`,{params: query});
}
export const getSelectBerthUserList = (query?: any) => {
  return http.request("get",`/userBerch/map/userBerchList`,{params: query});
}

export const batchUpdateUserBerth = (userBerthList: any[],berthCode: string) => {
  return http.request("post",
    `/userBerch/map/batchUpdate`,
    {data: {
      berthCode: berthCode,
      userBerth: userBerthList
    }}
  )
}

export const listUser = (query: any) =>http.request("get", `/userBerch/map/userList`, { params: query });