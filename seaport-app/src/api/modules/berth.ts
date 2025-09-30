import http from '@/api/http'

// 查询泊位状态列表
export const getBerthStatusList = (query ?: any) =>
	http.get(`/app/statistics/berthStatus`, { params: query });

export const getWindowPeriodList = (data: any) => {
	return http.post('/windowperiod/manager/list',data);
}

export const getLackTypeList = (data: any) => {
	return http.post('/windowperiod/manager/getLackList',data);
}

export const getStopClassList = (data:any) => {
	return http.post('/dockStopClass/manager/list',data);
}

export const addWindowPeriod = (data: any) => {
	return http.post('/windowperiod/manager/add',data);
}

export const delWindowPeriodById = (id: number)=>{
 return http.get(`/windowperiod/manager/del/${id}`);
}

export const updateWindowPeriod = (data: any)=>{
	return http.post("/windowperiod/manager/update",data);
}

export const getSlowDownWorkLogs = (data: any)=>{
	return http.post('/slowDownWork/manager/list', data);
}

export const addSlowDownWorkLogs = (data: any)=>{
	return http.post('/slowDownWork/manager/add', data);
}

export const updateSlowDownWorkLogs = (data: any)=>{
	return http.post('/slowDownWork/manager/update', data);
}

export const delSlowDownWorkLogs = (id: number)=>{
	return http.get(`/slowDownWork/manager/del/${id}`);
}