<template>
  <view class="log-list-page">
    <wd-card
      v-for="(item, index) in state.logList"
      :title="$t('unloadWorkLog.pauseTitle', { n: index + 1 })"
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
        <text class="label">{{ $t('unloadWorkLog.pauseInterval') }}</text>
        <text class="value">{{ item.pauseInterval }}</text>
      </view>
			<view class="log-item">
				<text class="label">{{ $t('others.obj14') }}</text>
				<text class="value">{{ $t(getRemarkLabel(item.remark)) }}</text>
			</view>
      <view class="log-item">
        <text class="label">{{ $t('port.berth.pauseReason') }}</text>
        <text class="value">{{ item.reason }}</text>
      </view>
      <template #footer>
        <wd-button
          :disabled="!state.isOperation"
          size="small"
          :round="false"
					type="success"
          @tap="handleEdit(item)">
					{{ $t('contans.operations.edit') }}
				</wd-button>
				
				<wd-button
					v-if="item.startTime!==null&&item.startTime!==undefined&&item.startTime!==''
							&&item.endTime!==null&&item.endTime!==undefined&&item.endTime!==''"
					size="small" :round="true" type="warning" style="margin-left: 20rpx;"
					@tap="handleDel(item)">
					{{ $t('delete') }}
				</wd-button>
      </template>
    </wd-card>

    <wd-action-sheet
      v-model="state.showEditLog"
      :title="$t('unloadWorkLog.editLog')"
      @closed="handleWorkClosed"
    >
      <wd-form ref="form" :model="modelForm" :rules="rules">
        <wd-cell-group border>
          <wd-calendar
            v-model="modelForm.startTime"
            type="datetime"
            hide-second
            :label="$t('unloadWorkLog.pauseTime')"
            prop="startTime"
            label-width="100px"
            :placeholder="$t('unloadWorkLog.pauseTime')"
            clearable
          />
          <wd-calendar
            v-model="modelForm.endTime"
            type="datetime"
            hide-second
            :label="$t('unloadWorkLog.resumeTime')"
            prop="endTime"
            label-width="100px"
            :placeholder="$t('unloadWorkLog.resumeTime')"
            clearable
          />
					<wd-cell
					  :title="t('others.obj14')"
					  title-width="80px"
					  prop="remark"
					  :rules="[{ required: true, message: t('others.obj14') }]"
					>
					  <wd-radio-group
					    v-model="modelForm.remark"
					    shape="button"
					    class="flex"
					  >
					    <wd-radio
					      v-for="classItem in ClassOptions"
					      :key="classItem.value"
					      :value="classItem.value"
					    >
					      {{ t(classItem.label) }}
					    </wd-radio>
					  </wd-radio-group>
					</wd-cell>
					<wd-textarea
            label-width="100px"
            :label="$t('port.berth.pauseReason')"
            clearable
            v-model="modelForm.reason"
            :placeholder="$t('unloadWorkLog.inputPauseReason')"
            auto-height
            prop="reason"
          />
        </wd-cell-group>
      </wd-form>
      <view class="action-buttons">
        <wd-button
          style="width: 80%"
          block
          type="primary"
          @tap="handleConfirm"
          >{{ $t('confirm') }}</wd-button
        >
      </view>
    </wd-action-sheet>
    <!-- 空状态 -->
    <wd-status-tip
      v-if="!state.logList.length"
      image="search"
      :tip="$t('unloadWorkLog.noLog')"
    />
  </view>
  <wd-toast />
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import {
	harborDetailList, 
	updateHarborUnloadLog, 
	deleteHarborUnloadLog,
} from '@/api/modules/plan'
import {
  formatTimestampToDateTime,
  dateTimeToFormatTimestamp
} from '@/utils/time'
import { useToast } from 'wot-design-uni'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const toast = useToast()
const getRemarkLabel = (val) => {
	if (val === '1' || val === 1) return 'others.obj15'
	if (val === '2' || val === 2) return 'others.obj16'
	return '-'
}
const ClassOptions = ref([
	{label:"others.obj15",value:"1"},
	{label:"others.obj16",value:"2"}
])
const state = reactive({
  logList: [],
  isOperation: true,
  showEditLog: false,
  total: '',
  loadStatus: 'loading',
  planId: '',
  duId: ''
})
// 校验规则
const rules = reactive({
  startTime: [
    {
      required: true,
      message: t('others.obj17')
    }
  ],
  endTime: [
    {
      required: true,
      message: t('others.obj18')
    }
  ],
	remark: [
		{
			required: true,
			message: t("others.obj13")
		}
	],
  reason: [
    {
      required: true,
      message: t('unloadWorkLog.inputPauseReason')
    }
  ]
})
const modelForm = ref({})
const form = ref({})

const getLogList = async () => {
  try {
    const res = await harborDetailList({
      duId: state.duId
    })
    if (res.code === 200) {
      state.logList = res.data
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}
const editLog = async () => {
  modelForm.value.endTime = formatTimestampToDateTime(modelForm.value.endTime)
  modelForm.value.startTime = formatTimestampToDateTime(modelForm.value.startTime)

  try {
    const res = await updateHarborUnloadLog(modelForm.value)
    if (res.code === 200) {
      state.showEditLog = false
      toast.success(t('unloadWorkLog.editSuccess'))
      getLogList()
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}
const handleConfirm = () => {
  form.value
    .validate()
    .then(({ valid }) => {
      if (valid) {
        editLog()
      }
    })
    .catch((error) => {
      console.log(error, 'error')
    })
}
const handleWorkClosed = () => {
  modelForm.value = {}
}
// 处理返回逻辑
onLoad((options) => {
  state.planId = options?.planId || ''
  state.duId = options?.duId || ''
  state.isOperation = options?.unloadStatus !== '2'
  getLogList()
})

// 修改按钮点击事件
const handleEdit = (item) => {
  modelForm.value = {
    dudId: item.dudId,
    endTime: dateTimeToFormatTimestamp(item.endTime),
    startTime: dateTimeToFormatTimestamp(item.startTime),
    reason: item.reason,
		remark: item.remark
  }
  // 这里添加修改逻辑，可以跳转到编辑页面或打开弹窗
  state.showEditLog = true
}
const handleDel = async (item) => {
	uni.showModal({
		title: t('delete'),
		content: t('delConfirm'),
		confirmText: t('delete'),
		cancelText: t('cancel'),
		confirmColor: '#f56c6c',
		success: (res) => {
			if (res.confirm) {
				deleteHarborUnloadLog(item.dudId).then((resp)=>{
					if(resp.code===200){
						uni.showToast({
						  title: t('unloadWorkLog.deleteSuccess'),
						  icon: 'success',
						  duration: 1500
						})
						getLogList()
					}
				})
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