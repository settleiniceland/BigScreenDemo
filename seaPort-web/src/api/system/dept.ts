import { http } from "@/utils/http";
const serverUrl = `/system`;

// 查询部门列表
export const listDept = (query?: any) =>
  http.request("get", `${serverUrl}/dept/list`, { params: query });

// 查询部门列表（排除节点）
export const listDeptExcludeChild = (deptId: any) =>
  http.request("get", `${serverUrl}/dept/list/exclude/${deptId}`);

// 查询部门详细
export const getDept = (deptId: any) =>
  http.request("get", `${serverUrl}/dept/${deptId}`);

// 新增部门
export const addDept = (data: any) =>
  http.request("post", `${serverUrl}/dept`, { data });

// 修改部门
export const updateDept = (data: any) =>
  http.request("put", `${serverUrl}/dept`, { data });

// 删除部门
export const delDept = (deptId: any) =>
  http.request("delete", `${serverUrl}/dept/${deptId}`);
