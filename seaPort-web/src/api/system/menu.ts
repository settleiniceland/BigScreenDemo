import { http } from "@/utils/http";
import type { Result } from "postcss";

// 查询菜单列表
export const listMenu = (query?: any) => {
  return http.request<Result>("get", `/system/menu/list`, {
    params: query
  });
};

// 查询菜单详细
export const getMenu = (menuId: any) => {
  return http.request<Result>("get", `/system/menu/${menuId}`);
};

// 查询菜单下拉树结构
export const treeselect = () => {
  return http.request<Result>("get", `/system/menu/treeselect`);
};

// 根据角色ID查询菜单下拉树结构
export const roleMenuTreeselect = (roleId: any) => {
  return http.request<Result>(
    "get",
    `/system/menu/roleMenuTreeselect/${roleId}`
  );
};

// 新增菜单
export const addMenu = (data: any) => {
  return http.request<Result>("post", `/system/menu`, { data });
};

// 修改菜单
export const updateMenu = (data: any) => {
  return http.request<Result>("put", `/system/menu`, { data });
};

// 删除菜单
export const delMenu = (menuId: any) => {
  return http.request<Result>("delete", `/system/menu/${menuId}`);
};

// 获取路由
export const getAsyncRoutes = () => {
  return http.request<Result>("get", `/system/getRouters`);
};
