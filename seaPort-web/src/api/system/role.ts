import { http } from "@/utils/http";
const serverUrl = `/system`;

// 查询角色列表
export const listRole = (query: any) =>
  http.request("get", `${serverUrl}/role/list`, { params: query });

// 查询角色详细
export const getRole = (roleId: any) =>
  http.request("get", `${serverUrl}/role/${roleId}`);

// 新增角色
export const addRole = (data: any) =>
  http.request("post", `${serverUrl}/role`, { data });

// 修改角色
export const updateRole = (data: any) =>
  http.request("put", `${serverUrl}/role`, { data });

// 角色数据权限
export const dataScope = (data: any) =>
  http.request("put", `${serverUrl}/role/dataScope`, { data });

// 角色状态修改
export const changeRoleStatus = (roleId: any, status: any) => {
  const data = { roleId, status };
  return http.request("put", `${serverUrl}/role/changeStatus`, { data });
};

// 删除角色
export const delRole = (roleId: any) =>
  http.request("delete", `${serverUrl}/role/${roleId}`);

// 查询角色已授权用户列表
export const allocatedUserList = (query: any) =>
  http.request("get", `${serverUrl}/role/authUser/allocatedList`, {
    params: query
  });

// 查询角色已授权用户列表（新）
export const allocatedUserListNew = (query: any) =>
  http.request("get", `${serverUrl}/role/authUser/newAllocatedList`, {
    params: query
  });

// 查询角色未授权用户列表
export const unallocatedUserList = (query: any) =>
  http.request("get", `${serverUrl}/role/authUser/unallocatedList`, {
    params: query
  });

// 取消用户授权角色
export const authUserCancel = (data: any) =>
  http.request("put", `${serverUrl}/role/authUser/cancel`, { data });

// 批量取消用户授权角色
export const authUserCancelAll = (data: any) =>
  http.request("put", `${serverUrl}/role/authUser/cancelAll`, { params: data });

// 授权用户选择
export const authUserSelectAll = (data: any) =>
  http.request("put", `${serverUrl}/role/authUser/selectAll`, { params: data });

// 根据角色ID查询部门树结构
export const deptTreeSelectByRoleId = (roleId: any) =>
  http.request("get", `${serverUrl}/role/deptTree/${roleId}`);
