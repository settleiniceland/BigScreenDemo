<template>
  <view
    class="berth-card cursor-pointer"
    :class="{ pause: data.workType === '1' }"
  >
    <view class="card-header">
      <view class="berth-info">
        <text class="berth-number">{{ data.berthName }}</text>
        <wd-tooltip placement="bottom" :content="data.shipName">
          <wd-text
            class="ship-name"
            style="max-width: 220px"
            :lines="1"
            :text="data.shipName"
            lineHeight="20px"
          />
        </wd-tooltip>
      </view>
      <wd-tag
        custom-class="status-tag"
        :type="PlanStatusOptions[data.status]?.type"
      >
        {{ t(PlanStatusOptions[data.status]?.label) }}
      </wd-tag>
    </view>
    <view class="card-content">
      <view class="material-info">
        <text class="material-name">{{ data.materialName }}</text>
        <text class="mine-number" v-if="currentTab === 1 && data.mineNumber">
          {{ data.mineNumber }}
        </text>
        <text class="total-weight">{{ data.tonnage }}T</text>
      </view>
      <view class="operation-info">
        <view class="progress-bar">
          <view
            class="progress-fill"
            :style="{
              width: data.progress + '%',
              backgroundColor: getProgressColor(data.progress)
            }"
          >
            <text
              style="
                color: #ffffff;
                font-size: 10px;
                z-index: 1;
                margin-left: 20rpx;
              "
            >
              {{ data.progress }}%
            </text>
          </view>
        </view>
        <view class="weight-info">
          <text
            >{{ t('port.berth.unloaded') }}: {{ data.unloadWeight || 0 }}T</text
          >
          <text
            >{{ t('port.berth.remaining') }}:
            {{ data.remainingWeight || 0 }}T</text
          >
        </view>
      </view>
      <view
        class="avgDischargeRate-info"
        :class="{ rateLow: data.exceptionFlag }"
      >
        <view class="workRate">
          <text class="label">{{ t('port.berth.efficiency') }}:</text>
          <text class="value value-right">
            {{ data.avgDischargeRate }}<text class="unit">T/H</text>
          </text>
        </view>
        <view v-if="data.exceptionFlag" class="reason">
          <text class="label">{{ t('port.berth.abnormalReason') }}: </text>
          <text class="value" v-if="data.exceptionReason || exceptionReason">{{
            data.exceptionReason || exceptionReason
          }}</text>
          <wd-select-picker
            class="reason-picker"
            v-else
            :placeholder="t('port.berth.selectAbnormalReason')"
            :columns="reasonList"
            @confirm="submitExceptionReason"
          ></wd-select-picker>
        </view>
      </view>

      <view v-if="data.workType === '1' && data.reason" class="pause-reason">
        <text class="pause-label">{{ t('port.berth.pauseReason') }}: </text>
        <text class="pause-value">{{ data.reason }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { defineProps, ref } from 'vue'
import { PlanStatusOptions } from '@/contans/index'
import { updateReason } from '@/api/modules/plan'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const { data, currentTab, reasonList } = defineProps({
  data: {
    type: Object,
    required: true,
    default: () => ({})
  },
  currentTab: {
    type: Number,
    default: 0
  },
  reasonList: {
    type: Object,
    required: true,
    default: () => ({})
  }
})
const exceptionReason = ref('')

const getProgressColor = (progress) => {
  if (progress <= 20) return '#f56c6c'
  if (progress <= 40) return '#e6a23c'
  if (progress <= 60) return '#5cb87a'
  if (progress <= 80) return '#1989fa'
  return '#67C23A'
}
const submitExceptionReason = async ({ value }) => {
  if (!value) return
  const { planId, progress, berthName } = data
  const params = {
    id: planId,
    progress,
    berthName,
    reason: value.join(','),
    type: currentTab === 0 ? 'type1' : 'type2'
  }
  try {
    const res = await updateReason(params)
    if (res.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      exceptionReason.value = value.join(',')
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
    console.error('保存原因失败', error)
  }
}
</script>

<style lang="scss" scoped>
.berth-card {
  border-radius: 12rpx;
  padding: 10rpx 20rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background: #fff;
  transition: background-color 0.3s ease;

  :deep(.wd-tooltip__inner) {
    white-space: normal !important;
  }

  :deep(.wd-tooltip__hidden) {
    left: 0;
    bottom: 0;
  }
}

.pause {
  background-color: rgba(245, 61, 37, 0.2) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.berth-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex-wrap: wrap;
}

.berth-number {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
}

.ship-name {
  font-size: 14px;
  color: #666666;
}

.mine-number {
  font-size: 14px;
  color: #444444;
}

.status-tag {
  padding: 0 6rpx 4rpx;
  font-size: 12px;
  color: #ffffff;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.material-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.material-name {
  font-size: 14px;
  font-weight: bold;
  color: #2e7d32;
}

.total-weight {
  font-size: 14px;
  color: #666666;
}

.operation-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.progress-bar {
  height: 24rpx;
  background-color: #f5f5f5;
  border-radius: 14rpx;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease, background-color 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.weight-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999999;
}

.avgDischargeRate-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(#4cf539, 0.2);
  padding: 10rpx 14rpx;
  border-radius: 8rpx;
  color: #2e7d32;
  gap: 8rpx;

  .label {
    font-size: 14px;
    // margin-bottom: 10rpx;
    margin-right: 10rpx;
  }

  .value {
    flex: 1;
    font-size: 18px;
    font-weight: bold;
  }

  .value-right {
    text-align: right;
  }

  .workRate {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  .reason {
    flex: 1;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .value {
      font-size: 14px;
    }

    /* 简化的 wd-picker 样式 */
    .reason-picker {
      flex: 1;
      background-color: transparent;

      :deep(.wd-select-picker__cell) {
        background-color: transparent;
        padding: 0;

        /* 输入框样式 */
        .wd-select-picker__body {
          height: 50rpx;
          line-height: 50rpx;
          padding: 0 20rpx;
          border: 1rpx solid #dcdfe6;
          border-radius: 8rpx;
          background-color: #ffffff;
          color: #d32f2f;
          text-align: left;
        }
      }
    }
  }
}

.rateLow {
  background-color: rgba(245, 61, 37, 0.1);
  color: #d32f2f;
}

.unit {
  font-size: 12px;
  margin-left: 4rpx;
}

.pause-reason {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 14rpx;
  background-color: rgba(245, 61, 37, 0.1);
  border-radius: 8rpx;
  font-size: 14px;
  color: #d32f2f;
}
</style>
