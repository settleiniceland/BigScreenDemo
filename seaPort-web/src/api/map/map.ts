import { http } from "@/utils/http";



export const getAccessToken = (data?: any) =>
http.request("post", `/map/getAccessToken`, { data: data });