import http from '@/api/http'

// 查询计划单列表
export const getPlanList = (query ?: any) =>
	http.get(`/harbor/plan/list`, { params: query });
// 查看计划单详情
export const getHarborPlan = (id : string) => {
	return http.get(`/harbor/plan/${id}`);
};

// 修改计划单
export const updatePlanApp = (data : any) => {
	return http.put(`/harbor/plan/appEdit`, data);
};
// 查询泊位信息列表
export const getHarborBerthList = (query ?: any) => {
	return http.get(`/harbor/berth/list`, { params: query });
};
//码头列表搜索
export const getHarborPierList = (query ?: any) => {
	return http.get(`/harbor/pier/selectList`, { params: query });
};

// 获得物资启用列表
export const getMaterialStatusList = (query ?: any) =>
	http.get(`/harbor/material/enableList`, { params: query });
//移泊计划单
export const moveHarborPlan = (data : any) => {
	return http.post("/harbor/plan/moveBerth", data);
};
export const submitUnloadWork = (data:any) => {
	return http.post("/harbor/plan/submitUnloadWork",data);
}
//开始作业
export const startWorkApp = (data : any) => {
	return http.put(`harbor/plan/startWork`, data);
};

// // 查询计划单列表
export const getUnloadList = (query ?: any) =>
	http.get(`/harbor/unloadWork/list`, {
		params: query
	});
// 新增卸货单
export const addHarborUnload = (data : any) => {
	return http.post(`/harbor/unloadWork`, data);
};
// 修改卸货单
export const updateHarborUnload = (data : any) => {
	return http.put(`/harbor/unloadWork`, data);
};
// 查看卸货单详情
export const getHarborUnloadInfo = (duId : string) => {
	return http.get(`/harbor/unloadWork/${duId}`);
};
//删除卸货单
export const deleteHarborUnload = (duId : string | string[]) => {
	return http.delete(`/harbor/unloadWork/${duId}`);
};
//暂停
export const pauseHarborUnload = (data : any) => {
	return http.put(`/harbor/unloadWork/stop`,
		data
	);
};

//恢复
export const recoverHarborUnload = (data : any) => {
	return http.put(`/harbor/unloadWork/recover`,
		data
	);
};

//结束
export const endHarborUnload = (data : any) => {
	return http.put(`/harbor/unloadWork/jobOver`,
		data
	);
};

// 卸货日志列表
export const harborDetailList = (query : any) => {
	return http.get(`/harbor/detail/list`, {
		params: query
	});
};

//修改卸货单日志
export const updateHarborUnloadLog = (data : any) => {
	return http.put(`/harbor/detail`, data);
};

//删除卸货单日志（卸货子单）
export const deleteHarborUnloadLog = (dudId : any) => {
	return http.delete(`/harbor/detail/del/${dudId}`);
}

//靠泊中
export const toPlanDocking = (data : any) => {
	return http.post(`/harbor/plan/toDocking`, data);
};

//驳船进度
export const getPierPlanList = (query : any) => {
	return http.get(`/app/statistics/pierPlanList`, {
		params: query
	});
};


//计划单状态分布
export const getPlanStatusList = (query ?: any) => {
	return http.get(`/app/statistics/planStatusList`, {
		params: query
	});
};

//昨日/当月累计吞吐量
export const getThroughput = (query ?: any) => {
	return http.get(`/app/statistics/throughput`, {
		params: query
	});
};

//今日/明日到船（物资）统计
export const getShipArrival = (query ?: any) => {
	return http.get(`/app/statistics/shipArrival`, {
		params: query
	});
};

//查询指定计划单的每小时的卸货日志列表
export const getUnloadLogList = (query ?: any) => {
	return http.get('/harbor/unloadLog/list', {
		params: query
	});
};

//查询卸货日志详情
export const getUnloadLog = (duhId ?: string) => {
	return http.get(`/harbor/unloadLog/${duhId}`);
};

//新增卸货日志
export const addUnloadLog = (data : any) => {
	return http.post(`/harbor/unloadLog`, data);
};

//修改卸货日志
export const editUnloadLog = (data : any) => {
	return http.put(`/harbor/unloadLog`,
		data
	);
};
//删除卸货日志
export const deletUnloadLog= (dhuIds : string | string[]) => {
	return http.delete(`/harbor/unloadLog/${dhuIds}`);
};

//删除已完成的卸货单
export const appUpdateUnload = (data : any) => {
	return http.put(`/harbor/unloadWork/appUpdateUnload`,
		data
	);
};

// 维护卸率原因
export const updateReason = (data : any) => {
	return http.put(`/harbor/plan/updateReason`,
		data
	);
};

//计算卸率标准
export const calculateUnloadingRate = (data : any) => {
	return http.post(`/harbor/unloadLog/calculateUnloadingRate`, data);
};
