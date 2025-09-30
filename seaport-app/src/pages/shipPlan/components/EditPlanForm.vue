<template>
  <view class="edit-form">
    <wd-form ref="form" :model="localModel" class="form-container">
      <wd-cell-group border>
        <!-- 泊位 -->
        <wd-picker
          :columns="
            berthList.map((i) => {
              return i.label
            })
          "
          prop="hbName"
          label-width="100px"
          clearable
          :label="t('port.berth.pier')"
          :placeholder="t('port.berth.pleaseSelect')"
          v-model="localModel.hbName"
					disabled
        />
        <!-- 已作业量 -->
        <!-- <wd-input
          type="number"
          :label="t('port.planManagement.inputCompletedWork')"
          label-width="100px"
          prop="unloadWeight"
          clearable
          v-model="localModel.unloadWeight"
          :placeholder="t('port.planManagement.inputCompletedWork')"
        /> -->
        <!-- 时间信息 -->
				<!-- <view class="calendar-item">
					<wd-calendar
						type="datetime"
						hide-second
						:label="t('port.time.arrivalTime')"
						:prop="arrivalTime"
						label-width="100px"
						v-model="localModel.arrivalTime"/>
				</view>
				<view class="calendar-item">
					<wd-calendar
						type="datetime"
						hide-second
						:label="t('port.time.planDockingTime')"
						:prop="planDockingTime"
						label-width="100px"
						v-model="localModel.planDockingTime"/>
				</view>
				<view class="calendar-item">
					<wd-calendar
						type="datetime"
						hide-second
						:label="t('port.time.dockingTime')"
						:prop="dockingTime"
						label-width="100px"
						v-model="localModel.dockingTime"/>
				</view> -->
        <view
          class="calendar-item"
          v-for="(item, index) in TimeFields"
          :key="index">
          <!-- @tap="handleCalendarTap(item.prop)"> -->
          <wd-calendar
            type="datetime"
            hide-second
            :label="t(item.label)"
            :prop="item.prop"
            label-width="100px"
            v-model="localModel[item.prop]"
						:disabled="item.isDisabled"
          />
          <wd-button
            v-if="localModel[item.prop]"
            type="icon"
            icon="error-fill"
            size="small"
            @tap.stop="clearDate(item.prop)"
						:disabled="item.isDisabled"
          ></wd-button>
        </view>
      </wd-cell-group>
    </wd-form>
    <view class="action-buttons">
      <wd-button style="margin-right: 10px" type="info" @tap="handleCancel">{{
        t('cancel')
      }}</wd-button>
      <wd-button type="primary" @tap="handleEditConfirm">{{
        t('confirm')
      }}</wd-button>
    </view>
  </view>
</template>

<script setup>
import { onShow } from '@dcloudio/uni-app'
import { defineProps, ref, reactive, watch } from 'vue'
import {
  formatTimestampToDateTime,
  dateTimeToFormatTimestamp
} from '@/utils/time'
import { TimeFields } from '@/contans/index'
import { useI18n } from 'vue-i18n'
import dayjs from 'dayjs'
const { t } = useI18n()
const form = ref()
const props = defineProps({
  modelForm: {
    type: Object,
    default: () => ({})
  },
  berthList: {
    type: Array,
    default: () => []
  },
  handleCancel: {
    type: Function,
    default: () => {}
  },
  handleConfirm: {
    type: Function,
    default: () => {}
  }
})
const localModel = ref({ ...props.modelForm })
watch(
  () => props.modelForm,
  (newVal) => {
    localModel.value = { ...newVal }
  },
  { deep: true, immediate: true }
)
// 防御性处理，保证所有时间字段不是空字符串
TimeFields.forEach((item) => {
  if (localModel.value[item.prop] === '') {
    localModel.value[item.prop] = null
  }
})
// 新增时时间字段默认当前时间（赋值为时间戳）
if (!localModel.value.id) {
  TimeFields.forEach((item) => {
    if (!localModel.value[item.prop]) {
      localModel.value[item.prop] = dayjs().valueOf()
    }
  })
}
const handleEditConfirm = () => {
  localModel.value.remainingWeight = localModel.value.remainingWeight
    ? Number(localModel.value.remainingWeight)
    : null
  TimeFields.forEach((item) => {
    if (localModel.value[item.prop]) {
      localModel.value[item.prop] = formatTimestampToDateTime(
        localModel.value[item.prop]
      )
    } else {
      localModel.value[item.prop] = null
    }
  })
  props.handleConfirm?.(localModel.value)
}
// 清除指定日期字段
const clearDate = (prop) => {
  localModel.value[prop] = null // 将对应字段置为空
}
function handleCalendarTap(prop) {
  if (!localModel.value[prop]) {
    localModel.value[prop] = dayjs().valueOf()
  }
}
</script>

<style lang="scss">
.edit-form {
  .action-buttons {
    display: flex;
    justify-content: center;
    padding: 10px;
    border-top: 1px solid #eee;
  }

  .form-container {
    background: #fff;
    border-radius: 16rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
    overflow: hidden;

    .wd-cell-group {
      .calendar-item {
        display: flex;
        align-items: center;

        &:last-child {
          border-bottom: none;
        }

        .wd-calendar {
          flex: 1;
          .wd-cell__title {
            width: 120px;
            font-size: 28rpx;
            color: #6b7280;
            flex-shrink: 0;
            line-height: 40rpx;
          }
          .wd-cell__value {
            flex: 1;
            .wd-datetime-picker__input {
              font-size: 28rpx;
              color: #1f2937;
              background: #f8fafc;
              border-radius: 8rpx;
              padding: 12rpx 16rpx;
              border: 1rpx solid #e5e7eb;
            }
          }
        }
      }
    }
  }
}
</style>