import { http } from "@/utils/http";

const baseUrl = import.meta.env.VITE_BASE_API;
const serverUrl = baseUrl + import.meta.env.VITE_SYSTEM_API;
// 查询菜单列表
export function listMenu(query?: any) {
  return http.request2({
    url: serverUrl + "/menu/list",
    method: "get",
    params: query
  });
}

// 查询菜单详细
export function getMenu(menuId: any) {
  return http.request2({
    url: serverUrl + "/menu/" + menuId,
    method: "get"
  });
}

// 查询菜单下拉树结构
export function treeselect() {
  return http.request2({
    url: serverUrl + "/menu/treeselect",
    method: "get"
  });
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeselect(roleId: any) {
  return http.request2({
    url: serverUrl + "/menu/roleMenuTreeselect/" + roleId,
    method: "get"
  });
}

// 新增菜单
export function addMenu(data: any) {
  return http.request2({
    url: serverUrl + "/menu",
    method: "post",
    data: data
  });
}

// 修改菜单
export function updateMenu(data: any) {
  return http.request2({
    url: serverUrl + "/menu",
    method: "put",
    data: data
  });
}

// 删除菜单
export function delMenu(menuId: any) {
  return http.request2({
    url: serverUrl + "/menu/" + menuId,
    method: "delete"
  });
}

// 获取路由
export const getRouters = () => {
  return http.request2({
    url: "/getRouters",
    method: "get"
  });
};
