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
				v-model="formData.unloadWorkId"
				prop="unloadWorkId"
				v-show="false"/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-calendar
				v-model="formData.startTime"
			  type="datetime"
			  hide-second
			  :label="t('port.workLog.startTime')"
			  prop="startTime"
			  label-width="90px"
			/>
		</wd-cell-group>
		<wd-cell-group border>
			<wd-calendar
			  v-model="formData.endTime"
			  type="datetime"
			  hide-second
			  :label="t('port.time.endTime')"
			  prop="endTime"
			  label-width="90px"
			/>
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
	  <wd-button class="btn" type="primary" @tap="submit">{{
	    t('submit')
	  }}</wd-button>
	</view>
</template>
<script setup>
import { formatTimestampToDateTime } from '@/utils/time'
import {reactive,ref,computed,onMounted,watch,defineProps} from 'vue'
import {addSlowDownWorkLogs,updateSlowDownWorkLogs} from '@/api/modules/berth'
import { useToast } from 'wot-design-uni'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const toast = useToast()
const formRef = ref()
const formData = reactive({
	id: undefined,
	planId: undefined,
	unloadWorkId: undefined,
	startTime: undefined,
	endTime: undefined,
	remark: undefined,
})
const rules = {
	planId: [{required:true, message:'---', trigger: 'blur'}],
	unloadWorkId: [{required:true, message:'---', trigger: 'blur'}],
	startTime: [{required:true, message:t('port.workLog.selectStartTime'), trigger: 'blur'}],
	endTime: [{required:true, message:t('port.workLog.selectEndTime'), trigger: 'blur'}],
	remark: [{required:true, message:t('others2.obj5'), trigger: 'blur'}],
}
const regex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
const {unloadWork,slowWorkLog,submitComplete} = defineProps({
	unloadWork: {
		type: Object,
		default: () => ({})
	},
	slowWorkLog: {
		type: Object,
		default: () => ({})
	},
	submitComplete: {
		type: Function,
		default: () => {}
	}
})
const minStartTime = ref()
const maxEndTime = ref(new Date("2099/12/31 23:59:59").getTime())
watch(
	()=>unloadWork,
	(val)=>{
		if(val && Object.keys(val).length > 0){
			Object.assign(formData,{
				id: undefined,
				planId: undefined,
				unloadWorkId: undefined,
				startTime: undefined,
				endTime: undefined,
				remark: undefined,
			})
			formData.planId = val.planId;
			formData.unloadWorkId = val.duId;
			minStartTime.value = new Date(val.startTime.replace(/-/g, '/')).getTime();
			if(regex.test(val.params.endTime)){
				maxEndTime.value = new Date(val.endTime.replace(/-/g, '/')).getTime();
			}
		}
	},
	{immediate:true,deep:true}
)
watch(
	()=>slowWorkLog,
	(val)=>{
		if(val && Object.keys(val).length > 0){
			Object.assign(formData,val)
			minStartTime.value = new Date(val.params.startTime.replace(/-/g, '/')).getTime();
			if(regex.test(val.params.endTime)){
				maxEndTime.value = new Date(val.params.endTime.replace(/-/g, '/')).getTime();
			}
		}
	},
	{immediate:true,deep:true}
)
const submit = async ()=>{
	formRef.value.validate().then(async({valid})=>{
		if(valid){
			if(!regex.test(formData.startTime)){
				formData.startTime = formatTimestampToDateTime(formData.startTime)
			}
			if(!regex.test(formData.endTime)){
				formData.endTime = formatTimestampToDateTime(formData.endTime)
			}
			const start = new Date(formData.startTime.replace(/-/g, '/')).getTime()
			const end   = new Date(formData.endTime.replace(/-/g, '/')).getTime()
			if(!(maxEndTime.value>end && end>start && start>minStartTime.value)){
				toast.error({
				  msg: t('others2.obj6'),
				})
			}else{
				if(formData.id){//修改
					const res = await updateSlowDownWorkLogs(formData)
					if(res.code==200){
						toast.success({
						  msg: t('person.changeInfoSuccess'),
						})
					}
				}else{//新增
					const res = await addSlowDownWorkLogs(formData)
					if(res.code==200){
						toast.success({
						  msg: t('others2.obj7'),
						})
					}
				}
				Object.assign(formData,{
					id: undefined,
					planId: undefined,
					unloadWorkId: undefined,
					startTime: undefined,
					endTime: undefined,
					remark: undefined,
				})
				submitComplete()
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