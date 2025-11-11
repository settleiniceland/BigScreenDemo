<template>
  <view>
    <wd-card class="unload-custom-card">
      <!-- 头部 -->
      <view class="card-header">
        <view class="header-left">
          <wd-text
            bold
            class="class-time"
            color="#F57C00"
            :text="data.classTime || '-'"
          />
        </view>
        <view class="header-center">
          <wd-text
            bold
            class="class-leader"
            color="#1010c6"
            :text="data.leader || '-'"
          />
        </view>
        <view class="header-right">
          <wd-tag
            v-if="data.classes"
            class="tag_class mr-1"
            :type="ClassTypeOptions[data.classes] || 'primary'"
            mark
            plain
          >
            {{ t('contans.class.' + getClassKeyByValue(data.classes)) }}
          </wd-tag>
          <wd-tag
            class="tag_class"
            :type="UnloadWorkTypeTagOptions[data.workType]?.type || 'default'"
            mark
          >
            {{
              findDictLabelFromObject(
                UnloadWorkTypeTagOptions,
                data.workType
              ) || t('common.unknown')
            }}
          </wd-tag>
        </view>
      </view>

      <!-- 中间内容 -->
      <view class="card-body">
        <view class="card-content">
          <!-- 左侧关键数据（灰色背景） -->
          <view class="left-column" v-if="data.workType === '2'">
            <view class="info-row highlight">
              <text style="color:yellowgreen; font-weight:bold; font-size: 27px;">
                {{ data.classTime }}
              </text>
						</view>
						<view class="info-row highlight">
							<text style="color:black; font-weight:bold; font-size: 36px;">
							  {{ t(showClassesName(data.classes)) }}
							</text>
            </view>
						<view class="info-row highlight">
							<text style="color:deepskyblue; font-weight:bold; font-size: 18px;">
								{{t('others2.obj11')}}:
							</text>
						</view>
						<view class="info-row highlight">
							<text style="color:blue; font-weight:bold; font-size: 36px;">
								{{data.totalUnloadWeight}}
							</text>
						</view>
          </view>
          <view class="left-btn" v-else>
            <wd-button
              v-if="data.workType === '1'"
              icon="play-circle"
              @tap="handleUnload(WorkStatusType.Refresh)"
              >{{ t('work.resume') }}</wd-button
            >
            <wd-button
              type="warning"
              v-if="data.workType === '0'"
              icon="pause-circle"
              @tap="handleUnload(WorkStatusType.Pause)"
              >{{ t('work.pause') }}</wd-button
            >
            <wd-button
              class="mt-2"
							v-if="data.workType === '0'"
              type="success"
              icon="poweroff"
              @tap="handleUnload(WorkStatusType.Over)"
              >{{ t('work.endShift') }}</wd-button
            >
						<wd-divider>
						  <wd-icon name="arrow-down" size="20" color="#1989fa" />
						</wd-divider>
						<view class="info-row" v-if="data.workType !== '0'">
							<text class="label">{{t('others.obj10')}}：</text>
							<text style="color:crimson">{{planBaseInfo.dataUnit}}</text>
						</view>
          </view>
          <!-- 右侧次要数据 -->
          <view class="right-column">
						<view class="info-row">
							<text class="label">{{t('others.obj1')}}：</text>
							<text style="color:crimson; font-weight:bold;">{{planBaseInfo.materialName}}</text>
						</view>
						<view class="info-row">
							<text class="label">{{t('others.obj5')}}：</text>
							<text style="color:crimson; font-weight:bold;">{{planBaseInfo.usageUnit}}</text>
						</view>
						<view class="info-row" v-if="data.workType === '0' || data.workType === '2'">
							<text class="label">{{t('others.obj2')}}：</text>
							<text style="color:blue; font-weight:bold; font-size: 26px;">{{planBaseInfo.dataUnit}}</text>
						</view>
						<view class="info-row">
							<text class="label">{{t('others.obj6')}}：</text>
							<text style="color:crimson; font-weight:bold;">{{planBaseInfo.loadSequence}}</text>
						</view>
            <view
              v-for="(item, index) in rightFields"
              :key="index"
              class="info-row"
            >
              <text class="label">{{ t(item.label) }}</text>
              <text
                :class="['value', item.class]"
                :style="{ color: item.color}"
              >
                {{ data[item.key] || '-' }}
              </text>
            </view>
          </view>
        </view>
        <!-- 新增备注区域 -->
        <view class="remark-section" v-if="data.remark">
          <text class="remark-label">{{ t('work.remark') }}: </text>
          <wd-text
            class="remark-value"
            :text="data.remark"
            size="22rpx"
          ></wd-text>
        </view>
      </view>
      <!-- 尾部 -->
      <template #footer>
        <view class="card-actions">
					<wd-button
					  class="action-btn"
					  size="small"
					  type="error"
					  @tap="showPopup=true"
					  >{{ t('others.obj29') }}</wd-button
					>
          <wd-button
            class="action-btn"
            size="small"
            plain
            type="warning"
            @tap="handleGoLog"
            >{{ t('work.logDetail') }}</wd-button
          >
          <view v-if="data.workType === '2'" class="card-actions">
            <wd-button
              class="action-btn"
              type="warning"
              size="small"
              :disabled="unloadStatus === '2'"
              @tap="handleUnloadWork(String(UnloadWorkType.Edit), data)"
              >{{ t('work.editShift') }}</wd-button
            >
            <wd-button
              class="action-btn"
              size="small"
              :disabled="unloadStatus === '2'"
              @tap="handleEditOver(OpertionsMap.EDIT)"
              >{{ t('contans.operations.edit') }}</wd-button
            >
          </view>
          <view v-if="data.workType === '0'" class="card-actions">
            <wd-button
              class="action-btn"
							type="warning"
              size="small"
              :disabled="unloadStatus === '2'"
              @tap="handleUnloadWork(String(UnloadWorkType.Edit), data)"
              >{{ t('work.editShift') }}</wd-button
            >
          </view>
          <wd-button
            class="action-btn"
            type="error"
            size="small"
            plain
            @tap="handleDelete"
            :disabled="unloadStatus === '2'"
            >{{ t('contans.operations.delete') }}</wd-button
          >
        </view>
      </template>
      <!-- 更改作业状态面板 -->
      <wd-action-sheet
        v-model="state.showEditWorkStatus"
        :title="t(state.currentWorkStatus) + t('work.operation')"
        @closed="handleWorkClosed"
      >
        <ChangeWorkStatus
					ref="ChangeWorkStatusRef"
          :modelForm="editStatusForm"
          :type="String(state.currentWorkStatus)"
          :isEdit="state.isEdit"
          :handleWorkStatus="handleWorkStatus"
          :startTime="data.startTime"
          :dataUnit="planBaseInfo.dataUnit"
					:dataStatus="planBaseInfo.status"
        />
      </wd-action-sheet>
			<!-- 慢作业操作选择弹窗 -->
			<view v-if="showPopup" class="popup-mask">
			  <view class="popup-content">
			    <view class="btn-row">
			      <button @click="handleGoSlowDownWorkSubmit" class="btn-primary" type="button">{{$t('others2.obj2')}}</button>
			      <button @click="handleGoSlowDownWorkLogs" class="btn-warn" type="button">{{$t('others2.obj3')}}</button>
			    </view>
			    <wd-divider>
			      <wd-icon name="arrow-down" size="20" color="#1989fa" />
			    </wd-divider>
			    <button @click="cancelSlowDownWorkOptions" type="button">{{$t('others2.obj1')}}</button>
			  </view>
			</view>
      <wd-toast />
			<!-- 慢作业日志填报模板 -->
			<wd-action-sheet
				v-model="state.showSlowDownWorkLogSubmit"
				:title="t('others2.obj4')"
				@closed="showAddFormFn(true)"
			>
				<SlowDownWorkLogSubmit
					ref="SlowDownWorkRef"
					:unloadWork="data"
					:submitComplete="slowDownWorkLogSubmitComplete"
				/>
			</wd-action-sheet>
    </wd-card>
  </view>
</template>

<script setup>
import {computed,reactive,ref,watch} from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
import dayjs from 'dayjs'
import {
  UnloadWorkTypeTagOptions,
  WorkStatusType,
  UnloadWorkType,
  OpertionsMap,
  ClassTypeOptions,
  ClassOptions
} from '@/contans/index'
import { findDictLabelFromObject } from '@/utils/dict'
import {
  formatTimestampToDateTime,
  dateTimeToFormatTimestamp
} from '@/utils/time'
import {
  deleteHarborUnload,
  pauseHarborUnload,
  recoverHarborUnload,
  endHarborUnload,
  appUpdateUnload
} from '@/api/modules/plan'
import ChangeWorkStatus from './UnloadStatus/ChangeWorkStatus.vue'
import SlowDownWorkLogSubmit from '@/pages/unloadWork/components/SlowDownWorkLogSubmit.vue'
import { nextTick } from 'process'
const { t } = useI18n()
const planBaseInfo = ref({
	tonnage: undefined,
	unloadWeight: undefined,
	dataUnit: undefined,
	usageUnit: undefined,
	materialName: undefined,
	isFinish: undefined,
	loadSequence: undefined,
})
const ChangeWorkStatusRef = ref()
const SlowDownWorkRef = ref()
const toast = useToast()
const showPopup = ref(false)
const state = reactive({
  showEditWorkStatus: false,
  currentWorkStatus: WorkStatusType.Over,
  isEdit: false,
	showSlowDownWorkLogSubmit: false
})
const {
  data,
  unloadStatus,
  getUnloadWorkList,
  handleUnloadWork,
  showAddFormFn,
  getPlanDetail,
	planData,
} = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  unloadStatus: {
    type: String,
    default: '1'
  },
  getUnloadWorkList: {
    type: Function,
    default: () => {}
  },
  handleUnloadWork: {
    type: Function,
    default: () => {}
  },
  showAddFormFn: {
    type: Function,
    default: () => {}
  },
  getPlanDetail: {
    type: Function,
    default: () => {}
  },
	planData: {
		type: Object,
		default: () => ({})
	},
})
watch(
	()=>[data,planData],
	([dataVal,pdVal])=>{
		console.info("⏳装卸卡片-初次进",dataVal,pdVal)
		if(dataVal && Object.keys(dataVal).length > 0 && pdVal && Object.keys(pdVal).length > 0){
			if(dataVal.remark01 === "1" || dataVal.remark01 === null){
				planBaseInfo.value.tonnage = pdVal.tonnage;
				planBaseInfo.value.unloadWeight = pdVal.unloadWeight;
				planBaseInfo.value.dataUnit = pdVal.remark03
				planBaseInfo.value.usageUnit = pdVal.usageUnit;
				planBaseInfo.value.materialName = pdVal.materialName;
				planBaseInfo.value.loadSequence = 1;
				console.info("✅装卸卡片-赋值第一个物料",planBaseInfo.value)
			}else{
				const sortedList = [...pdVal.params.subMaterial].sort((a, b) => a.loadSequence - b.loadSequence)
				for(const item of sortedList){
					if(item.loadSequence == dataVal.remark01){
						planBaseInfo.value.tonnage = item.tonnage;
						planBaseInfo.value.unloadWeight = item.unloadWeight;
						planBaseInfo.value.dataUnit = item.remark02
						planBaseInfo.value.usageUnit = item.usageUnit;
						planBaseInfo.value.materialName = item.materialName;
						planBaseInfo.value.loadSequence = item.loadSequence;
						console.info("✅装卸卡片-赋值其他物料",planBaseInfo.value)
						break;
					}
				}
			}
		}
	},
	{immediate:true,deep:true}
)
watch(() => state.showEditWorkStatus, (val) => {
  if (val) {
    nextTick(() => {
      tryOpenOperationWithRetry()
    })
  }
})
const tryOpenOperationWithRetry = (retryCount = 0) => {
  const maxRetries = 10
  const delay = 500
  if (ChangeWorkStatusRef.value?.openOperation) {
    console.info("✅ openOperation 方法已就绪，开始调用")
    ChangeWorkStatusRef.value.openOperation()
  } else {
    console.info(`⏳ openOperation 未就绪，重试中（第 ${retryCount + 1} 次）`)
    if (retryCount < maxRetries) {
      setTimeout(() => {
        tryOpenOperationWithRetry(retryCount + 1)
      }, delay)
    } else {
      console.info("❌ 重试超过最大次数，openOperation 仍未就绪")
    }
  }
}
const editStatusForm = ref({
  time: dayjs().second(0).valueOf(),
  duId: data.duId
})

const handleWorkClosed = () => {
  editStatusForm.value = {
    time: dayjs().second(0).valueOf(),
    duId: data.duId
  }
  showAddFormFn(true)
}
const showClassesName = (name) => {
	switch(name){
		case "白班":
			return "contans.class.day"
		case "夜班":
			return "contans.class.night"
	}
}
// 定义左侧字段
// const leftFields = computed(() => {
//   const baseFields = [
//     {
//       label: '日期',
//       key: 'classTime',
//       color: '#626aef',
//       class: 'rate',
// 			size: 19
//     },
//     {
      
//       key: 'classes',
//       color: '#67C23A',
//       class: 'effective',
// 			size: 36
//     },
//     {
//       label: '装卸量',
//       key: 'totalUnloadWeight',
//       color: '#67C23A',
//       class: 'effective',
// 			size: 36
//     },
//   ]
// 	// switch(data.classes){
// 	// 	case "白班":
// 	// 		baseFields.push({
// 	// 			label: '班次',
// 	// 			key:
// 	// 		})
// 	// 		break;
// 	// 	case "夜班":
		
// 	// 		break;
// 	// }
//   return baseFields
// })
// 定义右侧字段（动态判断 workType，暂停次数放在最上方）
const rightFields = computed(() => {
  const baseFields = [
    {
      label: 'work.pauseCount',
      key: 'stopCount',
      color: '#333'
    }, // 移到最上方
    {
      label: 'work.equipment',
      key: 'workEquipment',
      color: '#333'
    },
    {
      label: 'work.startTime',
      key: 'startTime',
      color: '#409EFF',
      class: 'start-time'
    },
    {
      label: 'work.stopTime',
      key: 'endTime',
      color: '#67C23A',
      class: 'end-time'
    }
  ]
  return baseFields
})
//TODO 这里是暂停，回复，结束作业的最终方法
const handleWorkStatus = async (formData) => {
  const type = state.currentWorkStatus
  // 使用传入的表单数据，如果没有传入则使用editStatusForm.value
  const formValues = formData || editStatusForm.value
  const { pauseReason, duId, time,remark, ...rest } = formValues
  const params = {
    duId
  }
  if (type === WorkStatusType.Pause) {
    params.pauseReason = pauseReason
    params.startTime = formatTimestampToDateTime(time)
		params.remark = remark
  }
  if (type === WorkStatusType.Refresh || type === WorkStatusType.Over) {
    params.endTime = formatTimestampToDateTime(time)
  }
  try {
    let res
    if (type === WorkStatusType.Over) {
      const values = {
        ...rest,
        ...params
      }
      res = state.isEdit
        ? await appUpdateUnload(values)
        : await endHarborUnload(values)
    } else {
      res =
        type === WorkStatusType.Pause
          ? await pauseHarborUnload(params)
          : await recoverHarborUnload(params)
    }

    if (res.code === 200) {
      if (state.isEdit) {
        toast.success(t('unloadWorkLog.editSuccess'))
      } else {
        const typeLabel = t(type)
        toast.success(t('unloadWorkLog.operateSuccess', { type: typeLabel }))
      }
      state.showEditWorkStatus = false
      showAddFormFn(true)
      getPlanDetail?.()
      getUnloadWorkList()
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}
const handleEditOver = (type) => {
  showAddFormFn?.(false)
  state.isEdit = type === OpertionsMap.EDIT
  state.currentWorkStatus = WorkStatusType.Over
  const { duId, remark, endTime, workEquipment, unloadNum, totalUnloadWeight } = data
  editStatusForm.value = {
    duId,
    remark,
    time: dateTimeToFormatTimestamp(endTime),
    workEquipment,
    unloadNum,
    totalUnloadWeight
  }
  state.showEditWorkStatus = true
}
const handleUnload = (type) => {
  const { classes, classTime, leader } = data
  if (!classTime || !leader || !classes) {
    toast.warning(t('unloadWorkLog.editInfoWarning'))
    return
  }
  state.currentWorkStatus = type
  showAddFormFn?.(false)
  editStatusForm.value = {
		time: undefined,
    duId: data.duId,
    pauseReason: ''
  }
  state.showEditWorkStatus = true
}
const handleDelete = () => {
  uni.showModal({
    title: t('unloadWorkLog.deleteTitle'),
    content: t('unloadWorkLog.deleteConfirm'),
    cancelText: t('common.cancel'), // 新增
    confirmText: t('common.confirm'), // 新增
    success: async (res) => {
      if (res.confirm) {
        await deleteHarborUnload(data?.duId)
          .then(() => {
            toast.success(t('unloadWorkLog.deleteSuccess'))
            getUnloadWorkList()
          })
          .catch((error) => {
            toast.error(t('unloadWorkLog.deleteError'))
            console.error('删除失败', error)
          })
      }
    }
  })
}
const cancelSlowDownWorkOptions = () => {
	showPopup.value = false;
}
const handleGoSlowDownWorkLogs = () => {
	uni.navigateTo({
		url: `/pages/unloadWork/SlowDownWorkLog/index?duId=${data.duId}&planId=${data.planId}&startTime=${data.startTime}&endTime=${data.endTime}`
	})
}
const handleGoSlowDownWorkSubmit = () => {
	showAddFormFn(false)
	showPopup.value = false;
	state.showSlowDownWorkLogSubmit = true
}
const handleGoLog = () => {
  uni.navigateTo({
    url: `/pages/unloadWork/WorkLog/index?duId=${data.duId}&planId=${data.planId}&unloadStatus=${unloadStatus}`
  })
}
const slowDownWorkLogSubmitComplete = () => {
	showAddFormFn(true)
	state.showSlowDownWorkLogSubmit = false
	handleGoSlowDownWorkLogs()
}
// 班次 value 转 key 用于国际化
function getClassKeyByValue(value) {
  const found = ClassOptions.find((item) => item.value === value)
  if (found) {
    const labelParts = found.label.split('.')
    return labelParts[labelParts.length - 1]
  }
  return value
}
</script>
<style lang="scss">
.unload-custom-card {
  padding: $card-pd !important;

  :deep(.wd-card__header) {
    padding: 10rpx;
    border-bottom: 1rpx solid #eee;
  }

  :deep(.wd-card__footer) {
    padding: 10rpx;
    border-top: 1rpx solid #eee;
  }

  .remark-section {
    display: flex;
    margin-top: 10rpx;

    .remark-label {
      display: flex;
      font-size: 22rpx;
      width: 80rpx;
      font-weight: bold;
      white-space: nowrap;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 10rpx;
  }

  .header-left {
    display: flex;
    align-items: center;
  }

  .class-time {
    font-size: 28rpx;
    font-weight: bold;
  }

  .header-center {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .class-leader {
    font-size: 26rpx;
    font-weight: bold;
  }

  .header-right {
    display: flex;
    align-items: center;

    .tag_class {
      padding: 6rpx 12rpx !important;
      font-size: 20rpx;
      line-height: 24rpx;
      border-radius: 8rpx;
      font-weight: 500;
    }

    .mr-1 {
      margin-right: 10rpx;
    }
  }

  .card-body {
    padding: 10rpx;
  }

  .card-content {
    display: flex;
    width: 100%;
  }

  .left-column {
    flex: 1.2;
    /* 增加左侧宽度 */
    background: #f5f5f5;
    padding: 10rpx;
    border-radius: 6rpx;
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  .right-column {
    flex: 0.8;
    /* 减少右侧宽度 */
    padding: 10rpx;
    display: flex;
    flex-direction: column;
    gap: 5rpx;
  }

  .info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .info-row.highlight {
    .label {
      width: 120rpx;
      /* 增加 label 宽度，避免换行 */
      color: #666;
      font-size: 26rpx;
      font-weight: bold;
    }

    .value {
      width: 160rpx;
      font-size: 26rpx;
      font-weight: bold;
      white-space: nowrap;
      /* 防止换行 */
    }
  }

  .label {
    width: 120rpx;
    color: #999;
    font-size: 24rpx;
  }

  .value {
    font-size: 24rpx;
    flex: 1;
    white-space: nowrap;
    /* 防止换行 */
  }

  .card-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10rpx;
  }

  .action-btn {
    height: 50rpx;
    line-height: 50rpx;
    font-size: 24rpx;
    padding: 0 15rpx;
    border-radius: 6rpx;
  }
}
.popup-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.popup-content {
  width: 80%;
  background: white;
  padding: 20rpx;
  border-radius: 68rpx;
  text-align: center;
  z-index: 10000;
}
.btn-row {
  display: flex;
  justify-content: space-between;
}
.popup-content .btn-row button {
  width: 260rpx;
  height: 100rpx;
  line-height: 100rpx;
  border-radius: 60rpx;
  font-size: 35rpx;
  color: #fff;
  border: none;
	align-self: center;
}
.btn-primary {
  background-color: #007AFF;
}
.btn-warn {
  background-color: #00aa00;
}
</style>