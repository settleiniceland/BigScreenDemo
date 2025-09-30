<template>
  <div class="ship-detail">
    <wd-card
      v-for="(groupItem, index) in group"
      :key="index"
      class="detail-group"
      :title="groupItem.title"
    >
      <view class="group-content">
        <view
          v-for="(column, colIndex) in groupItem.columns"
          :key="colIndex"
          class="info-row"
        >
          <text class="info-label">{{ column.label }}</text>
          <text class="info-value">{{ formatValue(column.prop) }}</text>
        </view>
      </view>
    </wd-card>
  </div>
</template>

<script setup>
import { defineProps, reactive } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getHarborPlan } from '@/api/modules/plan'
import { useI18n } from 'vue-i18n'

// 定义 Props
const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})

// 响应式状态
const state = reactive({
  planId: '',
  planInfo: {}
})

// 分组配置
const { t } = useI18n()
const group = [
  {
    title: t('port.planDetail.baseInfo'),
    columns: [
      { label: t('port.planDetail.tradeType'), prop: 'shipRade', width: 120 },
      { label: t('port.planManagement.shipName'), prop: 'shipName' },
      { label: t('port.planManagement.mineNumber'), prop: 'mineNumber' },
      { label: t('port.planDetail.imo'), prop: 'imo' },
      { label: t('port.planManagement.materialName'), prop: 'materialName' },
      { label: t('port.planDetail.usageUnit'), prop: 'usageUnit' },
      { label: t('port.berth.pier'), prop: 'hbName', width: 120 },
      { label: t('port.planDetail.shipLength'), prop: 'shipLength' },
      { label: t('port.planDetail.planTonnage'), prop: 'planTonnage' },
      { label: t('port.workLog.actualTonnage'), prop: 'tonnage' },
      { label: t('port.planDetail.packageNum'), prop: 'packageNum' },
      { label: t('port.planManagement.completedWork'), prop: 'unloadWeight' },
      { label: t('task.remark'), prop: 'remark' }
    ]
  },
  {
    title: t('port.planDetail.timeInfo'),
    columns: [
      { label: t('port.time.arrivalTime'), prop: 'arrivalTime' },
      { label: t('port.time.planDockingTime'), prop: 'planDockingTime' },
      { label: t('port.time.dockingTime'), prop: 'dockingTime' },
      { label: t('port.time.operationTime'), prop: 'operationTime' },
      { label: t('port.time.endTime'), prop: 'endTime' },
      { label: t('port.time.outBerthTime'), prop: 'outBerthTime' },
      { label: t('port.time.leaveTime'), prop: 'leaveTime' }
    ]
  },
  {
    title: t('port.planDetail.shippingInfo'),
    columns: [
      { label: t('port.planDetail.shipAgency'), prop: 'shipAgency' },
      { label: t('port.planDetail.loadingPort'), prop: 'loadingPort' },
      { label: t('port.planDetail.lastPort'), prop: 'lastPort' },
      { label: t('port.planDetail.nextPort'), prop: 'nextPort' },
      { label: t('port.planDetail.draft'), prop: 'draft' },
      { label: t('port.planDetail.weight'), prop: 'weight' },
      { label: t('port.planDetail.cardCount'), prop: 'cardCount' }
    ]
  },
  {
    title: t('port.planDetail.contractInfo'),
    columns: [
      { label: t('port.planDetail.contractRate'), prop: 'contractRate' },
      { label: t('port.planDetail.contractFee'), prop: 'contractFee' },
      { label: t('contans.planType.demurrage'), prop: 'demurrageFee' },
      { label: t('port.planDetail.orderNo'), prop: 'orderNo' },
      { label: t('port.planDetail.contractNo'), prop: 'contractNo' },
      { label: t('port.planDetail.handledBy'), prop: 'handledBy' },
      { label: t('port.planDetail.suplierName'), prop: 'suplierName' },
      { label: t('port.planDetail.batchNumber'), prop: 'batchNumber' },
      {
        label: t('port.planDetail.inspectionCompany'),
        prop: 'inspectionCompany'
      }
    ]
  }
]

// 格式化显示值
const formatValue = (prop) => {
  const value = state.planInfo[prop] // 优先使用接口数据
  return value === null || value === '' ? '-' : value
}

// 获取计划详情
const getPlanDetail = async () => {
  try {
    const res = await getHarborPlan(state.planId)
    if (res.code === 200) {
      state.planInfo = res.data // 假设接口返回 data 字段
    }
  } catch (error) {
    console.error('获取计划单详情失败: ', error)
  }
}

// 生命周期钩子
onLoad((options) => {
  state.planId = options?.planId || ''
})

onShow(() => {
  if (state.planId) {
    getPlanDetail()
  }
})
</script>

<style lang="scss">
.ship-detail {
  // padding: 20rpx;
  background: #f7f9fc;
  min-height: 100vh;

  .detail-group {
    border-radius: 16rpx;
    background: #fff;
    margin-bottom: 20rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
    overflow: hidden;

    .wd-card__title {
      padding: 16rpx 20rpx;
      font-size: 32rpx;
      font-weight: 600;
      color: #1e293b;
      background: #f8fafc;
      border-bottom: 1rpx solid #e5e7eb;
    }

    .group-content {
      padding: 16rpx 20rpx;

      .info-row {
        display: flex;
        align-items: flex-start;
        padding: 14rpx 0;
        border-bottom: 1rpx dashed #f1f5f9;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          font-size: 26rpx;
          color: #9ca3af;
          flex-shrink: 0;
          width: 180rpx;
          line-height: 36rpx;
        }

        .info-value {
          font-size: 30rpx;
          color: #1f2937;
          font-weight: 600;
          flex: 1;
          text-align: right;
          line-height: 36rpx;
          word-break: break-word;
        }
      }
    }
  }
}
</style>
