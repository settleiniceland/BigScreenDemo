export const UnloadWorkTypeOptions = [
	{ label: 'contans.inProgress', value: '0' },
	{ label: 'contans.pause', value: '1' },
	{ label: 'contans.over', value: '2' }
];
export enum WorkStatusType {
	Refresh = 'contans.refresh',
	Pause = 'contans.pause',
	Over = 'contans.over'
}
export const UnloadWorkTypeTagOptions = {
	0: {
		label: 'contans.inProgress',
		color: "primary",
		type: "primary"
	},
	1: {
		label: 'contans.pause',
		color: "danger",
		type: "danger"
	},
	2: {
		label: 'contans.over',
		color: "success",
		type: "success"
	},
};
export const PlanType = [
	{ label: 'contans.planType.dispatch', value: "2" },
	{ label: 'contans.planType.demurrage', value: "1" }
];
export const PlanTypeOptionsObj = {
	1: {
		label: 'contans.planType.demurrage',
		value: "1",
		type: "red"
	},
	2: {
		label: 'contans.planType.dispatch',
		value: "2",
		type: "cyan"
	}
};
export const BerthStatusTagOptions = {
	0: {
		label: 'contans.berthStatus.free',
		value: "0",
		type: "primary",
		color: "#409EFF"
	},
	1: {
		label: 'contans.berthStatus.occupied',
		value: "1",
		type: "success",
		color: "#67C23A"
	},
	2: {
		label: 'contans.berthStatus.maintenance',
		value: "2",
		color: "orange",
		type: "warning"
	},
	3: {
		label: 'contans.berthStatus.pause',
		value: "3",
		type: "danger",
		color: "#F56C6C"
	}
};

export const BerthStatusOptions = [
	{ label: 'contans.berthStatus.free', value: "0" },
	{ label: 'contans.berthStatus.occupied', value: "1" },
	{ label: 'contans.berthStatus.maintenance', value: "2" },
	// {
	// 	label: "暂停",
	// 	value: "3"
	// }
];

export const PierTypeOptions = [
	{ label: 'contans.pierType.main', value: "1" },
	{ label: 'contans.pierType.barge', value: "2" }
];

export const PireTypeOptionsObj = {
	1: {
		label: 'contans.pierType.main',
		value: "1",
		color: "primary"
	},
	2: {
		label: 'contans.pierType.barge',
		value: "2",
		color: "warning"
	}
};

export const Colors = {
	0: '#FFC107', // 计划
	1: '#36CFA9', // 在途
	2: '#FF5355', // 等待
	3: '#2ECC71', // 靠泊
	4: '#409EFF', // 装卸
	5: '#9B59B6', // 待离
	6: '#F79BBA', // 待离泊
	7: '#27AE60', // 完成
	8: '#2980B9', // 待返
	9: '#E74C3C', // 移泊
	10: '#F39C12' // 正在靠泊
}
export const PlanStatusOptions = {
	3: {
		label: 'contans.planStatus.wait',
		value: 4,
		type: "primary"
	},
	4: {
		label: 'contans.planStatus.working',
		value: 5,
		type: "success"
	},
	5: {
		label: 'contans.planStatus.waitDepart',
		value: 6,
		type: "warning"
	}
};
export const UnloadStatusOptions = [
	{ label: 'contans.unloadStatus.not', value: "0", type: "info" },
	{ label: 'contans.unloadStatus.doing', value: "1", type: "primary" },
	{ label: 'contans.unloadStatus.done', value: "2", type: "success" },
	{ label: 'contans.unloadStatus.resume', value: "3", type: "primary" }
];

export const SumMap = {
	shipCount: 'contans.sumMap.shipCount',
	totalWeight: 'contans.sumMap.totalWeight',
	avgRate: 'contans.sumMap.avgRate',
	totalDemurrageFee: 'contans.sumMap.totalDemurrageFee'
};

export enum UnloadWorkType {
	Start = 0,
	Add = 1,
	Edit = 2
}

// 时间字段配置
export const TimeFields = [
	{
		label: 'port.time.arrivalTime',
		prop: 'arrivalTime',
		isDisabled: true
	},
	{
		label: 'port.time.planDockingTime',
		prop: 'planDockingTime',
		isDisabled: true
	},
	{
		label: 'port.time.dockingTime',
		prop: 'dockingTime',
		isDisabled: false
	},
	{
		label: 'port.time.operationTime',
		prop: 'operationTime',
		isDisabled: false
	},
	{
		label: 'port.time.endTime',
		prop: 'endTime',
		isDisabled: false
	},
	{
		label: 'port.time.outBerthTime',
		prop: 'outBerthTime',
		isDisabled: false
	},
	// {
	// 	label: 'port.time.leaveTime',
	// 	prop: 'leaveTime',
	// }
]

// 班次选项
export const ClassOptions = [{
	label: 'contans.class.day',
	value: '白班'
},
{
	label: 'contans.class.night',
	value: '夜班'
}
];

// 班次选项
export const ClassTypeOptions = {
	'白班': 'primary',
	'夜班': 'warning'
};

export const OpertionsMap = {
	ADD: 'contans.operations.add',
	EDIT: 'contans.operations.edit',
	DEl: 'contans.operations.delete',
};

// 装卸船类型
export const ShipOperationType = {
	'装船 Memuat kapal': {
		label: 'contans.shipOperation.loading',
		type: 'primary'
	},
	'卸船 Membongkar kapal': {
		label: 'contans.shipOperation.unloading',
		type: 'success'
	}
};