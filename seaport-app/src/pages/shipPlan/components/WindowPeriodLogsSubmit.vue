<template>
	<wd-form ref="formRef" :model="formData" :rules="rules">
		<wd-cell-group border>
			<wd-input
				v-model="formData.planId"
				prop="planId"
				v-show="false"/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-input
				v-model="formData.stopClassDetail"
				prop="stopClassDetail"
				v-show="false"/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-input
				v-model="formData.startTime"
				:label="originStartTimeLabel"
				prop="startTime"
				label-width="90px"
				disabled
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-input
				v-model="formData.endTime"
				:label="originEndTimeLabel"
				prop="endTime"
				label-width="90px"
				disabled
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-picker
				v-model="formData.periodType"
				:columns="[periodTypeDict]"
				:label="t('others.obj20')"
				prop="periodType"
				label-width="90px"
				range-key="label"
				disabled
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-picker
				v-model="formData.avoidCollectFee"
				:columns="[commonDict]"
				:label="t('others.obj21')"
				prop="avoidCollectFee"
				label-width="90px"
				range-key="label"
				disabled
			/>
		</wd-cell-group>
		<wd-divider color="#4D80F0">✍👇👇👇空窗期</wd-divider>
		<wd-cell-group border>
			<wd-calendar
				type="datetime"
				hide-second
				:label="t('port.workLog.startTime')"
				prop="remark1"
				label-width="90px"
				v-model="formData.remark1"
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-calendar
				type="datetime"
				hide-second
				:label="t('port.time.endTime')"
				prop="remark2"
				label-width="90px"
				v-model="formData.remark2"
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-picker
				v-model="formData.stopClassId"
				:columns="[allStopClassDict]"
				@confirm="onStopClassSelect"
				:label="t('others.obj22')"
				value-key="id"
				label-key="showDetail"
				prop="stopClassId"
				label-width="90px"
				clearable>
			</wd-picker>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-textarea
				v-model="formData.remark"
				:label="t('task.remark')"
				prop="remark"
				label-width="90px"
				clearable
			/>
		</wd-cell-group>
	</wd-form>
	<view class="action-buttons">
	  <wd-button class="btn" type="primary" @tap="handleSubmit">{{
	    t('submit')
	  }}</wd-button>
	</view>
	<wd-toast />
</template>
<script setup>
import {reactive,ref,computed,onMounted,watch,defineProps} from 'vue'
import {getStopClassList,addWindowPeriod,updateWindowPeriod} from '@/api/modules/berth'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
const toast = useToast()
const { t } = useI18n()
const formRef = ref()
const originStartTimeLabel = ref()
const originEndTimeLabel = ref()
const formData = reactive({
	id: undefined,
	planId: undefined,
	startTime: undefined,
	endTime: undefined,
	periodType: undefined,
	stopClassId: undefined,
	stopClassDetail: undefined,
	avoidCollectFee: undefined,
	remark: undefined,
	remark1: undefined,
	remark2: undefined,
})
const rules = {
	planId: [{required:true, message:'', trigger: 'blur'}],
	startTime: [{required:true, message:'', trigger: 'blur'}],
	endTime: [{required:true, message:'', trigger: 'blur'}],
	remark1: [{required:true, message:'事件开始时间不能为空', trigger: 'blur'}],
	remark2: [{required:true, message:'事件结束时间不能为空', trigger: 'blur'}],
	periodType: [{required:true, message:'空窗类型不能为空', trigger: 'blur'}],
	stopClassId: [{required:true, message:'空窗原因必须选择', trigger: 'blur'}],
	stopClassDetail: [{required:true, message:'空窗原因必须选择', trigger: 'blur'}],
	avoidCollectFee: [{required:true, message:'空窗原因必须选择', trigger: 'blur'}],
}
const periodTypeDict = [
	{value:0,label:t('others.obj24')},
	{value:1,label:t('others.obj25')},
	{value:2,label:t('others.obj26')},
]
const allStopClassDict = ref([])
const commonDict = [
	{value:true,label:'是|yes'},
	{value:false,label:'否|no'},
]
const {planData,closeWindowPerios,updateLogData,insertLogData} = defineProps({
  planData: {
    type: Object,
    default: () => ({})
  },
	closeWindowPerios: {
		type: Function,
    default: () => {}
	},
	updateLogData: {
		type: Object,
		default: () => ({})
	},
	insertLogData: {
		type: Object,
		default: () => ({})
	}
})
watch(
	() => insertLogData,
	(val)=>{
		if(val && Object.keys(val).length > 0){
			setTimeout(()=>{
				Object.assign(formData,{
					id: undefined,
					planId: undefined,
					startTime: undefined,
					endTime: undefined,
					periodType: undefined,
					stopClassId: undefined,
					stopClassDetail: undefined,
					avoidCollectFee: undefined,
					remark: undefined,
					remark1: undefined,
					remark2: undefined,
				});
				formData.planId = val.planId;
				formData.startTime = val.startTime;
				formData.endTime = val.endTime;
				formData.periodType = val.periodType;
				if(formData.periodType == 0){
					originStartTimeLabel.value = t('port.time.arrivalTime')
					originEndTimeLabel.value = t('port.time.dockingTime')
				}else if(formData.periodType == 1){
					originStartTimeLabel.value = t('port.time.dockingTime')
					originEndTimeLabel.value = t('port.time.operationTime')
				}else if(formData.periodType == 2){
					originStartTimeLabel.value = t('port.time.endTime')
					originEndTimeLabel.value = t('port.time.outBerthTime')
				}
			},100)
		}
	},
	{immediate:true, deep: true}
)
watch(
	() => updateLogData,
	(val) => {
		if(val && Object.keys(val).length > 0){
			setTimeout(()=>{
				Object.assign(formData,val);
				if(formData.periodType == 0){
					originStartTimeLabel.value = t('port.time.arrivalTime')
					originEndTimeLabel.value = t('port.time.dockingTime')
				}else if(formData.periodType == 1){
					originStartTimeLabel.value = t('port.time.dockingTime')
					originEndTimeLabel.value = t('port.time.operationTime')
				}else if(formData.periodType == 2){
					originStartTimeLabel.value = t('port.time.endTime')
					originEndTimeLabel.value = t('port.time.outBerthTime')
				}
			},100)
		}
	},
	{immediate:true, deep: true}
)
watch(
	() => planData,
	(val) => {
		if(val && Object.keys(val).length > 0){
			const type = val.lackTypes[0];
			setTimeout(() => {
				Object.assign(formData,{
					id: undefined,
					planId: undefined,
					startTime: undefined,
					endTime: undefined,
					periodType: undefined,
					stopClassId: undefined,
					stopClassDetail: undefined,
					avoidCollectFee: undefined,
					remark: undefined,
					remark1: undefined,
					remark2: undefined,
				});
				formData.planId = val.id;
				formData.periodType = type
				switch(type){
					// case 0:{
					// 	formData.startTime = val.arrivalTime
					// 	formData.endTime = val.dockingTime
					// 	break;}
					case 1:{
						formData.startTime = val.dockingTime
						formData.endTime = val.operationTime
						originStartTimeLabel.value = t('port.time.dockingTime')
						originEndTimeLabel.value = t('port.time.operationTime')
						break;}
					case 2:{
						formData.startTime = val.endTime
						formData.endTime = val.outBerthTime
						originStartTimeLabel.value = t('port.time.endTime')
						originEndTimeLabel.value = t('port.time.outBerthTime')
						break;}
				}
			},100);
		}
	},
	{immediate:true, deep: true}
)
onMounted(async ()=>{
	allStopClassDict.value = []
	const res = await getStopClassList({})
	if(uni.getStorageSync('language')=='id'){
		for(const item of res.data){
			allStopClassDict.value.push({
				...item,
				showDetail: item.remark01
			})
		}
	}else{
		for(const item of res.data){
			allStopClassDict.value.push({
				...item,
				showDetail: item.detail
			})
		}
	}
})
const selectedStopClassLabel = computed(() => {
  const item = allStopClassDict.value.find(i => i.id === formData.stopClassId)
  return item ? item.detail : ''
})
const onStopClassSelect = (e) => {
	if(e.selectedItems){
		formData.stopClassDetail = e.selectedItems.detail+"<--->"+e.selectedItems.remark01;
		formData.avoidCollectFee = e.selectedItems.avoidCollectFee;
	}
}
const formatTimestamp = (ts) => {
	if (typeof ts === "string" && /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(ts)) {
		return ts;
	}
	const date = new Date(ts);
	const pad = (n) => (n < 10 ? '0' + n : n);
	return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
			 + `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}
const toTimestamp = (str) => {
	return new Date(str.replace(/-/g, '/')).getTime();
}
const handleSubmit =async () => {
	formRef.value.validate().then(async({valid}) => {
		if (valid) {
			let code = 0
			if(formData.remark1 >= formData.remark2){
				toast.error(t("port.time.endTimeCannotBeEarlierThanStartTime"))
				return;
			}
			if(formData.remark1 < toTimestamp(formData.startTime) || formData.remark2 > toTimestamp(formData.endTime)){
				toast.error(t("others2.obj10"))
				return;
			}
			formData.remark1 = formatTimestamp(formData.remark1)
			formData.remark2 = formatTimestamp(formData.remark2)
			if(formData.id!==undefined){//TODO 修改
				const res=await updateWindowPeriod(formData)
				code = res.code
				
			}else{//新增
				const res=await addWindowPeriod(formData)
				code = res.code
			}
			if(code===200){
				closeWindowPerios();
			}
		}
	})
}
</script>
<style>
.action-buttons {
	display: flex;
	justify-content: center;
	padding: 10px;
	border-top: 1px solid #eee;
	.btn {
		margin-right: 10rpx;
	}
}
</style>