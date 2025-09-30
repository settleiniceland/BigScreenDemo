// * 请求响应参数(不包含data)
export interface Result {
  code: number
  msg: string
}

// * 请求响应参数(包含data)
export interface ResultData<T = any> extends Result {
  data: T
}

// * 分页响应参数
export interface ResPage<T> extends Result {
  rows: T[]
  total: number
}

// * 分页请求参数
export interface ReqPage {
  pageNum: number
  pageSize: number
}

// * 字典
export interface Dict {
  dictLabel: string
  dictValue: string
  listClass: string
  status: string
}

export interface ListItem {
  text: string
  value: number
}

export interface DefaultDict {
  label: string
  value: string
}
export interface DefaultNumberDict {
  label: string
  value: number
}
