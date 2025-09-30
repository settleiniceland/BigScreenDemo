<template>
	<view class="log-list-page">
		<wd-card
			v-for="(item, index) in logs"
			:title="$t('others.obj27', { n: index + 1 })"
			:key="index"
			class="log-card"
		>
			<view class="log-item">
				<text class="label">{{ $t(originStartTimeLabel(item.periodType)) }}</text>
				<text class="value">{{ item.startTime }}</text>
			</view>
			<view class="log-item">
				<text class="label">{{ $t(originEndTimeLabel(item.periodType)) }}</text>
				<text class="value">{{ item.endTime }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('others.obj20') }}</text>
			  <text class="value">{{ $t(showReriodType(item.periodType)) }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('port.workLog.startTime') }}</text>
			  <text class="value">{{ item.remark1 }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('port.time.endTime') }}</text>
			  <text class="value">{{ item.remark2 }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('others.obj21') }}</text>
			  <text class="value">{{ item.avoidCollectFee }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('others.obj22') }}</text>
			  <text class="value">{{ item.stopClassDetail }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('work.remark') }}</text>
			  <text class="value">{{ item.remark }}</text>
			</view>
			<template #footer>
				<wd-button
				  size="small"
				  :round="true"
					type="success"
				  @tap="handleInsert(item)">
					{{ $t('contans.operations.add') }}
				</wd-button>
			  <wd-button
			    size="small"
			    :round="false"
					type="warning"
					style="margin-left: 20rpx;"
			    @tap="handleUpdate(item)">
					{{ $t('contans.operations.edit') }}
				</wd-button>
				<wd-button
					size="small" 
					:round="true" 
					type="error"
					style="margin-left: 20rpx;"
					@tap="handleDel(item)">
					{{ $t('delete') }}
				</wd-button>
			</template>
		</wd-card>
	</view>
	<wd-status-tip
	  v-if="logs.length<1"
	  image="search"
	  :tip="$t('unloadWorkLog.noLog')"
	/>
	<wd-action-sheet
		v-model="WindowPeriosLogSubmitShow"
		:title="t('contans.operations.edit')">
		<WindowPeriodLogsSubmit
			:updateLogData="updateItem"
			:insertLogData="insertItem"
			:closeWindowPerios="closeWindowPerios"/>
	</wd-action-sheet>
</template>
<script setup>
import {ref,reactive} from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import {getWindowPeriodList,delWindowPeriodById} from '@/api/modules/berth'
import WindowPeriodLogsSubmit from '@/pages/shipPlan/components/WindowPeriodLogsSubmit.vue'
import { useToast } from 'wot-design-uni'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const toast = useToast()
const planId = ref()
const logs = ref([])
const WindowPeriosLogSubmitShow = ref(false)
const updateItem = reactive({})
const insertItem = reactive({})
onLoad((options) => {
	planId.value = options.planId
})
onShow(()=>{
	refrushData()
})
const refrushData =async ()=>{
	const res = await getWindowPeriodList({planId:planId.value});
	logs.value = res.data
}
const showReriodType = (type) =>{
	if(type==0){
		return 'others.obj24'
	}else if(type==1){
		return 'others.obj25'
	}else if(type==2){
		return 'others.obj26'
	}
}
const originStartTimeLabel = (type)=>{
	if(type==1){
		return 'port.time.dockingTime'
	}else if(type==2){
		return 'port.time.endTime'
	}
}
const originEndTimeLabel = (type)=>{
	if(type==1){
		return 'port.time.operationTime'
	}else if(type==2){
		return 'port.time.outBerthTime'
	}
}
const handleDel =async (item)=> {
	uni.showModal({
		title: t('delete'),
		content: t('delConfirm'),
		confirmText: t('delete'),
		cancelText: t('cancel'),
		confirmColor: '#f56c6c',
		success: async(resp)=>{
			if(resp.confirm){
				const res = await delWindowPeriodById(item.id);
				if(res.code===200){
					refrushData()
					uni.showToast({
					  title: t('unloadWorkLog.deleteSuccess'),
					  icon: 'none',
					  duration: 1500
					})
				}
			}else{
				uni.showToast({
				  title: t('unloadWorkLog.deleteError'),
				  icon: 'none',
				  duration: 1500
				})
			}
		}
	})
}
const handleUpdate = (item)=>{
	Object.assign(updateItem,{...item})
	WindowPeriosLogSubmitShow.value = true
}
const handleInsert = (item)=>{
	Object.assign(insertItem,{...item})
	WindowPeriosLogSubmitShow.value = true
}
const closeWindowPerios = ()=>{
	Object.assign(updateItem,{})
	refrushData()
	WindowPeriosLogSubmitShow.value = false
}
</script>
<style lang="scss">
.log-list-page {
  padding: 20rpx;
  background: #f5f5f5;

  .action-buttons {
    display: flex;
    justify-content: center;
    padding: 10px;
    border-top: 1px solid #eee;
  }
}

.log-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.05);

  :deep(.wd-card__title) {
    font-size: 32rpx;
  }
}

.log-item {
  display: flex;
  align-items: center;
  padding: 10rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.label {
  width: 160rpx;
  color: #666;
  font-size: 28rpx;
}

.value {
  flex: 1;
  color: #333;
  font-size: 28rpx;
  font-weight: 600;
}
</style>