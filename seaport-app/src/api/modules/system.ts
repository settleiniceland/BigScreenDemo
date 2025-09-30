import http from '../http'
import { Dict, ResultData } from '../interface'
const serverUrl = "system";

// 新增字典数据
export const getDicts = (data: string) => {
  return http.get<any, ResultData<Dict[]>>(`system/dict/data/type/${data}`)
}

// 检测APP是否有新版本
export const checkVersion = (data: any) => {
  return http.get<any, ResultData<any>>('system/version/checkVersion', {
    params: data
  })
}

// 查询部门列表 根据权限
export const listDept = (data?: any) => {
  return http.get('/system/dept/list', {
    params: data
  })
}


// 查询字典数据列表
export const diclist = (query?: any) =>
  http.get( `${serverUrl}/dict/data/list`, { params: query });
