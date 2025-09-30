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

// 查询用户列表
export const listUser = (query: any) =>
  http.request("get", `${serverUrl}/user/list`, { params: query });

// 查询用户详细
export const getUser = (userId?: any) =>
  http.request("get", `${serverUrl}/user/${userId}`);

// 新增用户
export const addUser = (data: any) =>
  http.request("post", `${serverUrl}/user`, { data });

// 修改用户
export const updateUser = (data: any) =>
  http.request("put", `${serverUrl}/user`, { data });

// 删除用户
export const delUser = (userId: any) =>
  http.request("delete", `${serverUrl}/user/${userId}`);

// 用户密码重置
export const resetUserPwd = (userId: any, password: any) => {
  const data = { userId, password };
  return http.request("put", `${serverUrl}/user/resetPwd`, { data });
};

// 用户状态修改
export const changeUserStatus = (userId: any, status: any) => {
  const data = { userId, status };
  return http.request("put", `${serverUrl}/user/changeStatus`, { data });
};

// 查询用户个人信息
export const getUserProfile = () =>
  http.request("get", `${serverUrl}/user/profile`);

// 修改用户个人信息
export const updateUserProfile = (data: any) =>
  http.request("put", `${serverUrl}/user/profile`, { data });

// 用户密码重置
export const updateUserPwd = (oldPassword: any, newPassword: any) => {
  const data = { oldPassword, newPassword };
  return http.request("put", `${serverUrl}/user/profile/updatePwd`, {
    params: data
  });
};

// 用户头像上传
export const uploadAvatar = (data: any) => {
  const formData = new FormData();
  formData.append("avatarfile", data);
  return http.request("post", `${serverUrl}/user/profile/avatar`, {
    data: formData,
    headers: { "Content-Type": "multipart/form-data" }
  });
};

// 查询授权角色
export const getAuthRole = (userId: any) =>
  http.request("get", `${serverUrl}/user/authRole/${userId}`);

// 保存授权角色
export const updateAuthRole = (data: any) =>
  http.request("put", `${serverUrl}/user/authRole`, { params: data });

// 查询部门下拉树结构
export const deptTreeSelect = () =>
  http.request("get", `${serverUrl}/user/deptTree`);


export const getDeptId = (userId?: number) => {
  return http.request("get",`/system/user/getDeptIdByUserId/${userId}`)
}
