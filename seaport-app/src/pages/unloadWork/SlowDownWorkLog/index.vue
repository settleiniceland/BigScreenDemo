<template>
	<view class="log-list-page">
		<wd-card
			v-for="(item, index) in slowWorkLogs"
			:title="$t('others.obj28', { n: index + 1 })"
			:key="index"
			class="log-card"
		>
			<view class="log-item">
			  <text class="label">{{ $t('port.workLog.startTime') }}</text>
			  <text class="value">{{ item.startTime }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('port.time.endTime') }}</text>
			  <text class="value">{{ item.endTime }}</text>
			</view>
			<view class="log-item">
			  <text class="label">{{ $t('others2.obj8') }}</text>
			  <text class="value">{{ item.remark }}</text>
			</view>
			<template #footer>
			  <wd-button
			    size="small"
			    :round="false"
					type="success"
			    @tap="handleUpdateLog(item)">
					{{ $t('contans.operations.edit') }}
				</wd-button>
				<wd-button
					size="small" 
					:round="true" 
					type="warning"
					 style="margin-left: 20rpx;"
					@tap="handleDelLog(item)">
					{{ $t('delete') }}
				</wd-button>
			</template>
		</wd-card>
	</view>
	<wd-status-tip
	  v-if="slowWorkLogs.length<1"
	  image="search"
	  :tip="$t('unloadWorkLog.noLog')"
	/>
	<!-- 慢作业日志填报模板 -->
	<wd-action-sheet
		v-model="showSlowDownWorkLogSubmit"
		:title="t('others2.obj9')"
	>
		<SlowDownWorkLogSubmit
			ref="SlowDownWorkRef"
			:slowWorkLog="updateItem"
			:submitComplete="slowDownWorkLogSubmitComplete"
		/>
	</wd-action-sheet>
	<wd-toast />
</template>
<script setup>
import {ref,reactive} from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import {getSlowDownWorkLogs,delSlowDownWorkLogs} from '@/api/modules/berth'
import SlowDownWorkLogSubmit from '@/pages/unloadWork/components/SlowDownWorkLogSubmit.vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
const { t } = useI18n()
const toast = useToast()
const slowWorkParams =ref({
	planId: undefined,
	unloadWorkId: undefined,
	params: {
		startTime: undefined,
		endTime: undefined
	}
})
const slowWorkLogs = ref([])
const updateItem = reactive({})
const showSlowDownWorkLogSubmit = ref(false)
onLoad((options) => {
	slowWorkParams.value.params.startTime = options.startTime
	slowWorkParams.value.params.endTime = options.endTime
	slowWorkParams.value.planId = options.planId;
	slowWorkParams.value.unloadWorkId = options.duId;
})
onShow(()=>{
	refrushData()
})
const refrushData =async ()=>{
	const res = await getSlowDownWorkLogs(slowWorkParams.value);
	slowWorkLogs.value = res.data
}
const handleDelLog =async (item)=>{
	uni.showModal({
		title: t('delete'),
		content: t('delConfirm'),
		confirmText: t('delete'),
		cancelText: t('cancel'),
		confirmColor: '#f56c6c',
		success: async(resp)=>{
			if(resp.confirm){
				const res = await delSlowDownWorkLogs(item.id);
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
const handleUpdateLog = (item)=>{
	Object.assign(updateItem,{
		...item,
		params: {
			startTime: slowWorkParams.value.params.startTime,
			endTime: slowWorkParams.value.params.endTime
		}
	})
	showSlowDownWorkLogSubmit.value = true
}
const slowDownWorkLogSubmitComplete = ()=>{
	refrushData()
	showSlowDownWorkLogSubmit.value = false
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