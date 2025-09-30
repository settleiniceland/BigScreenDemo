<template>
  <view class="editUnloadWeight">
    <!-- 计划单信息区域 -->
    <div class="loading" v-if="state.loading">
      <wd-loading />
    </div>
    <div class="main" v-else>
      <PlanInfo :key="state.planInfo?.status" :planInfo="state.planInfo" />
      <div class="title">{{ t('port.workLog.logList') }}</div>
      <div class="weightList">
        <wd-status-tip
          v-if="state.unloadWorkLogList.length === 0"
          image="content"
          :tip="t('port.workLog.noLogRecords')"
        />
        <view v-else class="list-container">
          <view
            v-for="(item, index) in state.unloadWorkLogList"
            :key="item.dhuId"
            class="list-item"
          >
            <view class="item-content">
              <view class="item-row">
                <text class="label">{{ t('port.workLog.startTime') }}:</text>
                <text class="value">{{
                  formatTimestampToDateTime(item.startTime)
                }}</text>
              </view>
              <view class="item-row">
                <text class="label">{{ t('port.time.endTime') }}:</text>
                <text class="value">{{
                  formatTimestampToDateTime(item.endTime)
                }}</text>
              </view>
              <view class="item-row">
                <text class="label">{{ t('port.workLog.workVolume') }}:</text>
                <text class="value">
                  {{
                    item.unloadWeight
                      ? `${item.unloadWeight + t('port.workLog.tons')}`
                      : `${item.unloadNum + t('port.workLog.pieces')}`
                  }}
                </text>
              </view>
              <view class="item-row" v-if="item.exceptionReason">
                <text class="label">{{ t('port.workLog.lowReason') }}:</text>
                <text class="value">
                  {{ item.exceptionReason }}
                </text>
              </view>
            </view>
            <view class="item-actions">
              <wd-button
                size="small"
                type="primary"
                plain
                @tap="handleEdit(item, index)"
              >
                {{ t('contans.operations.edit') }}
              </wd-button>
              <wd-button
                size="small"
                type="error"
                plain
                @tap="handleDelete(item.dhuId)"
              >
                {{ t('delete') }}
              </wd-button>
            </view>
          </view>
        </view>
      </div>
      <wd-fab
        v-if="!state.isShowEidt"
        position="left-bottom"
        :draggable="true"
        activeIcon="add"
        @tap="handleShowEdit(OpertionsMap.ADD)"
        :expandable="false"
      />
      <wd-action-sheet
        v-model="state.isShowEidt"
        :title="`${state.currentType + t('port.workLog.workLog')}`"
        @closed="handleMoveClosed"
      >
        <wd-form ref="formRef" :model="editWeightForm" :rules="rules">
          <wd-cell-group border>
            <wd-datetime-picker
              v-model="editWeightForm.startTime"
              :label="t('port.workLog.startTime')"
              prop="startTime"
              label-width="100px"
              :placeholder="t('port.berth.pleaseSelect')"
              :filter="filter"
              :disabled="!state.isFirstAdd"
            />
            <wd-datetime-picker
              v-model="editWeightForm.endTime"
              :label="t('port.time.endTime')"
              prop="startTime"
              :disabled="true"
              label-width="100px"
              :placeholder="t('port.berth.pleaseSelect')"
              :filter="filter"
            />
            <view class="flex">
              <wd-input
                v-model="editWeightForm[columnLabel.value]"
                type="number"
                :label="columnLabel.label"
                :prop="columnLabel.value"
                label-width="100px"
                :placeholder="`${t('port.workLog.enter') + columnLabel.label}`"
                clearable
              />
              <view class="flex justify-center items-center">
                <wd-radio-group
                  v-model="state.unLoadType"
                  shape="button"
                  @change="change"
                  class="flex"
                >
                  <wd-radio :value="'吨'">{{ t('port.workLog.ton') }}</wd-radio>
                  <wd-radio :value="'件'">{{
                    t('port.workLog.piece')
                  }}</wd-radio>
                </wd-radio-group>
              </view>
            </view>
          </wd-cell-group>
          <view class="footer">
            <wd-button type="primary" size="large" @tap="submitForm" block>
              {{ t('submit') }}
            </wd-button>
          </view>
        </wd-form>
      </wd-action-sheet>
    </div>
    <wd-popup
      v-model="state.showPopup"
      position="top"
      custom-style="padding-top:100rpx"
      @close="handleClose"
    >
      <wd-form ref="popupFormRef" :model="editWeightForm" :rules="popupRules">
        <wd-cell-group border>
          <wd-select-picker
            v-model="editWeightForm.exceptionReason"
            :label="t('port.workLog.exceptionReason')"
            label-width="100px"
            class="reason-picker"
            :placeholder="t('port.workLog.selectReason')"
            :columns="state.reasonList"
            clearable
            filterable
            :display-format="displayFormat"
            prop="exceptionReason"
          />
          <wd-input
            v-if="state.isOtherReason"
            v-model="editWeightForm.otherExceptionReason"
            :label="t('port.workLog.otherReason')"
            prop="otherExceptionReason"
            label-width="100px"
            :placeholder="t('port.workLog.enterOtherReason')"
            clearable
          />
        </wd-cell-group>
        <view class="footer">
          <wd-button type="primary" size="large" @tap="submitForm" block>
            {{ t('submit') }}
          </wd-button>
        </view>
      </wd-form>
    </wd-popup>
  </view>
</template>
<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import {
  formatTimestampToDateTime,
  dateTimeToFormatTimestamp
} from '@/utils/time'
import {
  getHarborPlan,
  getUnloadLogList,
  getUnloadLog,
  addUnloadLog,
  editUnloadLog,
  deletUnloadLog,
  calculateUnloadingRate
} from '@/api/modules/plan'
import { diclist } from '@/api/modules/system'
import PlanInfo from '@/pages/unloadWork/components/PlanInfo.vue'
import { OpertionsMap } from '@/contans/index'
import dayjs from 'dayjs'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const formRef = ref(null)
const popupFormRef = ref(null)
// 响应式状态
const state = reactive({
  planId: '',
  planInfo: {},
  loading: false,
  isShowEidt: false,
  currentType: OpertionsMap.ADD,
  unLoadType: '吨',
  unloadWorkLogList: [],
  isFirstAdd: true,
  currentEditId: null,
  showPopup: false,
  reasonList: [],
  isOtherReason: false,
  exceptionStatus: 1
})

// 表单验证规则
const rules = reactive({
  startTime: [
    {
      required: true,
      message: t('port.workLog.selectStartTime')
    }
  ],
  endTime: [
    {
      required: true,
      message: t('port.workLog.enterEndTime')
    }
  ],
  unloadWeight: [
    {
      required: true,
      message: t('port.workLog.enterWeight')
    }
  ],
  unloadNum: [
    {
      required: true,
      message: t('port.workLog.enterQuantity')
    }
  ]
})
// 弹窗表单验证规则（用于原因表单）
const popupRules = reactive({
  exceptionReason: [
    {
      required: true,
      message: t('port.workLog.selectExceptionReason')
    }
  ],
  otherExceptionReason: [
    {
      required: true,
      message: t('port.workLog.enterOtherReason')
    }
  ]
})

const getTime = (name) => {
  if (name === 'startTime') {
    return dateTimeToFormatTimestamp(
      dayjs().minute(0).second(0).format('YYYY-MM-DD HH:mm:ss')
    )
  }
  return dateTimeToFormatTimestamp(
    dayjs().minute(0).second(0).add(1, 'hour').format('YYYY-MM-DD HH:mm:ss')
  )
}

// 初始化 editWeightForm
const editWeightForm = ref({
  startTime: getTime('startTime'),
  endTime: getTime('endTime'),
  unloadNum: '',
  unloadWeight: '',
  exceptionReason: '',
  otherExceptionReason: ''
})
const filter = (type, values) => {
  if (type === 'minute') {
    return values.filter((value) => value === 0)
  }
  return values
}

const columnLabel = computed(() => {
  return {
    label:
      state.unLoadType === '件'
        ? t('port.workLog.quantity')
        : t('port.workLog.weight'),
    value: state.unLoadType === '件' ? 'unloadNum' : 'unloadWeight'
  }
})

const change = () => {
  const otherField = state.unLoadType === '件' ? 'unloadWeight' : 'unloadNum'
  editWeightForm.value[otherField] = ''
}

const handleShowEdit = (type) => {
  state.currentType = type
  state.isShowEidt = true
  if (type === OpertionsMap.ADD && !state.isFirstAdd) {
    const startTime = dateTimeToFormatTimestamp(
      state.unloadWorkLogList[state.unloadWorkLogList?.length - 1].endTime
    )
    editWeightForm.value.startTime = startTime
    editWeightForm.value.endTime = dayjs(startTime).add(1, 'hour').valueOf()
  }
}

const handleEdit = (item, index) => {
  state.currentType = OpertionsMap.EDIT
  state.currentEditId = item.dhuId
  state.isShowEidt = true
  state.unLoadType = item.unloadWeight ? '吨' : '件'
  editWeightForm.value = {
    startTime: dateTimeToFormatTimestamp(item.startTime),
    endTime: dateTimeToFormatTimestamp(item.endTime),
    unloadWeight: item.unloadWeight || '',
    unloadNum: item.unloadNum || ''
  }
}
const handleDelete = async (dhuId) => {
  uni.showModal({
    title: '提示',
    content: '确定要删除此作业量记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          const response = await deletUnloadLog(dhuId)
          if (response.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            getUnloadWorkLogList()
          }
        } catch (error) {
          uni.showToast({
            title: error.msg || '网络错误',
            icon: 'error'
          })
          console.error('删除失败:', error)
        }
      }
    }
  })
}
const handleUnloadingRate = async (data) => {
  try {
    const res = await calculateUnloadingRate(data)
    if (res.code !== 200) return
    state.exceptionStatus = res.data.result
    if (res.data.result === 1) {
      submitUnloadLog(data)
    } else if (res.data.result === 2) {
      state.showPopup = true
    } else {
      uni.showToast({
        title: '计算卸率异常，请排查',
        icon: 'error'
      })
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
    console.error('接口报错 ', error)
  }
}

const displayFormat = (items, columns) => {
  let showValue = ''
  columns.forEach((column) => {
    items.forEach((item) => {
      if (column.value === item) {
        showValue
          ? (showValue += `,${column.label}`)
          : (showValue += `${column.label}`)
      }
    })
  })
  return showValue
}
const submitForm = () => {
  const targetFormRef = state.showPopup ? popupFormRef : formRef
  targetFormRef.value
    .validate()
    .then(({ valid }) => {
      if (valid) {
        handleConfirm()
      }
    })
    .catch((error) => {
      console.error(error, 'error')
    })
}
const submitUnloadLog = async (data) => {
  try {
    const res =
      state.currentType === OpertionsMap.ADD
        ? await addUnloadLog(data)
        : await editUnloadLog(data)
    if (res.code === 200) {
      uni.showToast({
        title: `${state.currentType}success`,
        icon: 'success'
      })
      state.isShowEidt = false
      state.showPopup = false
      handleMoveClosed()
      getUnloadWorkLogList()
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
    console.error('接口报错 ', error)
  }
}
const handleConfirm = async () => {
  const {
    unloadNum,
    unloadWeight,
    endTime,
    startTime,
    exceptionReason,
    otherExceptionReason
  } = editWeightForm.value
  const data = {
    planId: state.planId,
    startTime: formatTimestampToDateTime(startTime),
    endTime: formatTimestampToDateTime(endTime)
  }
  if (unloadNum) {
    data.unloadNum = unloadNum
  } else {
    data.unloadWeight = unloadWeight
  }
  if (state.currentType === OpertionsMap.EDIT) {
    data.dhuId = state.currentEditId
  }
  data.exceptionStatus = state.exceptionStatus
  if (state.showPopup) {
    data.exceptionReason = exceptionReason.filter((i) => i !== '其他').join(',')
    if (otherExceptionReason) {
      data.exceptionReason = data.exceptionReason + `,${otherExceptionReason}`
    }
    submitUnloadLog(data)
    return
  }

  await handleUnloadingRate(data)
}

const handleMoveClosed = () => {
  editWeightForm.value = {
    startTime: getTime('startTime'),
    endTime: getTime('endTime'),
    unloadNum: '',
    unloadWeight: ''
  }
  state.currentEditId = null
  state.unLoadType = '吨'
}

const getUnloadWorkLogList = async () => {
  state.loading = true
  try {
    const res = await getUnloadLogList({
      planId: state.planId
    })
    if (res.code === 200) {
      state.unloadWorkLogList = res.data
      state.isFirstAdd = res.data?.length === 0
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
    console.error('接口报错 ', error)
  } finally {
    state.loading = false
  }
}

const getPlanDetail = async () => {
  try {
    const res = await getHarborPlan(state.planId)
    if (res.code === 200) {
      state.planInfo = {
        ...res.data
      }
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
    console.error('接口报错 ', error)
  }
}
const getDicData = async (type) => {
  try {
    const statusData = await diclist({
      dictType: type,
      pageSize: 50
    })
    state.reasonList = statusData?.rows?.map((i) => {
      return {
        label: i.dictLabel,
        value: i.dictLabel
      }
    })
  } catch (error) {
    console.error('接口报错 ', error)
  }
}
watch(
  () => editWeightForm.value.startTime,
  (newStart) => {
    if (newStart && state.currentType === OpertionsMap.ADD) {
      editWeightForm.value.endTime = dayjs(newStart).add(1, 'hour').valueOf()
    }
  }
)
watch(
  () => editWeightForm.value.exceptionReason,
  (newStart) => {
    if (newStart?.length > 0 && newStart.includes('其他')) {
      state.isOtherReason = true
    }
  }
)
// 生命周期钩子
onLoad((options) => {
  state.planId = options?.planId || ''
})

onShow(() => {
  if (state.planId) {
    getPlanDetail()
    getUnloadWorkLogList()
    getDicData('five_fifteen_exception_reason')
  }
})
</script>

<style lang="scss">
.editUnloadWeight {
  height: 100%;
  display: flex;
  overflow: hidden;
  background: #f5f6fa;

  .loading {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .footer {
    padding: 12px;
  }

  .main {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 16rpx;
    /* Slightly reduced padding for compactness */
  }

  .title {
    font-size: 32rpx;
    font-weight: 600;
    /* Slightly lighter than bold for modern look */
    color: #1a1a1a;
    /* Darker color for better contrast */
    margin: 24rpx 0 16rpx;
    /* Adjusted spacing */
  }

  .weightList {
    flex: 1;
    overflow-y: auto;
    padding-bottom: 20rpx;
    /* Prevent content from sticking to bottom */

    .no-data {
      text-align: center;
      color: #8c8c8c;
      /* Softer gray for better aesthetics */
      font-size: 28rpx;
      padding: 40rpx 0;
      /* More padding for better spacing */
    }

    .list-container {
      display: flex;
      flex-direction: column;
      gap: 16rpx;
      /* Reduced gap for tighter layout */
    }

    .list-item {
      background: #ffffff;
      border-radius: 20rpx;
      /* Slightly larger radius for modern look */
      padding: 20rpx 24rpx;
      /* Adjusted padding for balance */
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
      /* Softer, larger shadow for depth */
      transition: transform 0.2s ease, box-shadow 0.2s ease;
      /* Smooth hover effect */

      /* Hover effect for interactive feedback */
      &:hover {
        transform: translateY(-2rpx);
        box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.12);
      }

      .item-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8rpx;
        /* Tighter spacing between rows */
      }

      .item-row {
        display: flex;
        align-items: center;
        font-size: 28rpx;
        color: #333333;

        .label {
          width: 140rpx;
          /* Slightly reduced width for compactness */
          color: #666666;
          /* Softer label color */
          font-size: 26rpx;
          /* Slightly smaller for hierarchy */
          font-weight: 500;
        }

        .value {
          flex: 1;
          font-size: 28rpx;
          color: #1a1a1a;
          /* Darker value text for contrast */
        }
      }

      .item-actions {
        display: flex;
        flex-direction: column;
        /* Stack buttons vertically for better space usage */
        gap: 12rpx;
        /* Space between buttons */
        align-items: flex-end;

        .wd-button {
          width: 120rpx;
          /* Fixed width for consistency */
          font-size: 24rpx;
          /* Smaller font for buttons */
          padding: 10rpx 0;
          /* Adjusted padding for compact buttons */
          border-radius: 12rpx;
          /* Rounded buttons */
          transition: background-color 0.2s ease;
          /* Smooth button transitions */
        }
      }
    }
  }
}
</style>
