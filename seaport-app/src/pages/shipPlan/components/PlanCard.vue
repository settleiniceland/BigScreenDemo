<template>
  <wd-card type="rectangle" class="ship-card">
    <view class="card-header">
      <view class="ship-name">
        <wd-tooltip
          @change="handleChange"
          placement="bottom"
          :content="data.shipName"
        >
          <wd-text
            style="max-width: 280px"
            type="primary"
            size="32rpx"
            :lines="0"
            :text="data.shipName"
            lineHeight="20px"
          />
        </wd-tooltip>
      </view>
      <view class="status-badge">
        <wd-tag
          class="tag_class mr-1"
          :type="ShipOperationType[data.remark01]?.type || 'primary'"
          >{{
            t(ShipOperationType[data.remark01]?.label) || data.remark01
          }}</wd-tag
        >
        <wd-tag
          class="tag_class mr-1"
          :type="PireTypeOptionsObj[data.pierType]?.color"
          plain
        >
          {{
            findDictLabelFromObject(PireTypeOptionsObj, data.pierType) ||
            t('common.unknown')
          }}
        </wd-tag>
        <wd-tag class="tag_class" :bg-color="Colors[data.status]">
          {{
            t('contans.colors.' + data.status) ||
            statusOptions.find((item) => item.value === data.status)?.label
          }}
        </wd-tag>
      </view>
    </view>

    <view class="card-body">
      <view class="info-container" @tap="goDetail">
        <view class="info-left">
          <view class="info-primary highlight">{{ data.hbName }}</view>
          <view class="info-primary">{{ data.remark02?Number(data.remark02) + 1:1 }}</view>
        </view>
        <view class="info-right">
					<view v-for="(item, index) in materials" :key="index" class="info-block">
						<view class="info-item">
							<text class="info-label">{{ t('others.obj1') }}:</text>
							<text class="info-value">{{ item.materialName || '-' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">{{ t('others.obj2') }}:</text>
							<text class="info-value">{{ item.packageNum || '-' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">{{ t('others.obj3') }}:</text>
							<text class="info-value">{{ item.tonnage || '-' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">{{ t('others.obj4') }}:</text>
							<text class="info-value">{{ item.unloadWeight || '-' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">{{ t('others.obj5') }}:</text>
							<text class="info-value">{{ item.usageUnit || '-' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">{{ t('others.obj6') }}:</text>
							<text class="info-value">{{ item.loadSequence || '-' }}</text>
						</view>
						<wd-divider/>
					</view>
        </view>
      </view>
    </view>

    <template #footer>
      <view class="footer">
				<wd-button
					size="small"
					type="error"
					class="footer-btn"
					@tap="toWindowPeriodLogs"
				>{{ t('others.obj19')}}</wd-button>
				<wd-button
				  size="small"
					v-if="data.status === '4' || data.status === '5'"
					type="success"
				  class="footer-btn"
				  @tap="openSubmitUnloadWork"
				  >{{ t('others.obj7') }}</wd-button
				>
        <wd-button
          size="small"
          class="footer-btn"
          @tap="() => handleEdit(data)"
          >{{ t('contans.operations.edit') }}</wd-button
        >
        <wd-button
          type="warning"
          v-if="data.status === '2'"
          size="small"
          class="footer-btn"
          @tap="viewDocking"
          >{{ t('port.planManagement.completedWork') }}</wd-button
        >
        <wd-button
          size="small"
          v-if="data.status === '4' && data.unloadStatus === '0'"
          class="footer-btn"
          @tap="startWork"
          type="success"
          >{{ t('port.planManagement.startWork') }}</wd-button
        >
        <wd-button
          size="small"
          v-else-if="data.status >= '4'"
          plain
          class="footer-btn"
          @tap="goUnload"
          >{{ t('port.planManagement.unloadingBill') }}</wd-button
        >
      </view>
    </template>
    <!-- 开始作业面板 -->
    <wd-action-sheet
      v-model="state.showStartWork"
      :title="t('port.planManagement.workLog')"
      @closed="handleWorkClosed"
    >
      <UnloadWorkForm
        v-model:modelForm="startWorkForm"
				:planInfo="planInfo"
        :type="UnloadWorkType.Start"
      />
    </wd-action-sheet>
		<wd-action-sheet
			v-model="state.showSubmitUnloadwork"
			:title="t('others.obj7')"
		>
			<PlanUnloadWeightSubmit :planInfo="planInfo" :closeSubmitUnloadWork="closeSubmitUnloadWork"/>
		</wd-action-sheet>
  </wd-card>
  <wd-message-box />
</template>

<script setup>
import {defineProps,reactive,ref,watch} from 'vue'
import {
  PireTypeOptionsObj,
  Colors,
  UnloadWorkType,
  ShipOperationType
} from '@/contans/index'
import { findDictLabelFromObject } from '@/utils/dict'
import { dateTimeToFormatTimestamp } from '@/utils/time'
import dayjs from 'dayjs'
import UnloadWorkForm from '@/pages/unloadWork/components/UnloadWorkForm.vue'
import PlanUnloadWeightSubmit from '@/pages/shipPlan/components/PlanUnloadWeightSubmit.vue'
import { useMessage } from 'wot-design-uni'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const message = useMessage()
const state = reactive({
  showStartWork: false,
	showSubmitUnloadwork: false
})
const planInfo = ref()
const initWorkForm = {
  classTime: '',
  classes: '白班',
  operationTime: '',
  leader: ''
}
const startWorkForm = ref({
  ...initWorkForm
})
const materials = ref([])
const {
  data,
  statusOptions,
  handleEdit,
  handlePlanDocking,
  index,
	handleConfirm
} = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  statusOptions: {
    type: Array,
    default: []
  },
  handleEdit: {
    type: Function,
    default: () => {}
  },
  handleMove: {
    type: Function,
    default: () => {}
  },
  handlePlanDocking: {
    type: Function,
    default: () => {}
  },
  index: {
    type: [Number, String],
    default: 0
  },
	handleConfirm:{
		type: Function,
		default: () => {}
	}
})
const goDetail = () => {
  uni.navigateTo({
    url: `/pages/shipPlan/PlanDetail/index?planId=${data.id}`
  })
}
const toWindowPeriodLogs = () => {
	uni.navigateTo({
		url: `/pages/shipPlan/WindowPeriodLogsList/index?planId=${data.id}`
	})
}
const viewDocking = () => {
  message
    .confirm({
      msg: '确定将该计划单变更为靠泊中',
      title: '靠泊中'
    })
    .then(() => {
      handlePlanDocking(data.id)
    })
}
const openSubmitUnloadWork = () => {
	planInfo.value = data
	state.showSubmitUnloadwork = true
}
const closeSubmitUnloadWork = () => {
	handleConfirm()
	state.showSubmitUnloadwork = false
}
const startWork = () => {
  startWorkForm.value.planId = data.id
	planInfo.value = data
  state.showStartWork = true
}
const nextLabel = () => {
  if (data.status <= 6) {
    const index = statusOptions.findIndex((item) => item.value === data.status)
    return index !== -1 && index < statusOptions.length - 1
      ? statusOptions[index + 1].label
      : null
  }
  if (data.status === 8) {
    return '移泊'
  }
  return
}
const goUnloadWeight = () => {
  uni.navigateTo({
    url: `/pages/shipPlan/EditUnloadWeight/index?planId=${data.id}`
  })
}
const goUnload = () => {
  uni.navigateTo({
    url: `/pages/unloadWork/index?planId=${data.id}`
  })
}
const handleWorkClosed = () => {
  startWorkForm.value = {
    ...initWorkForm
  }
}
const handleChange = () => {
  // 这里可以根据需要自定义逻辑，暂时为空实现
}
watch(
  () => data,
  (newVal) => {
    if (newVal && Object.keys(newVal).length > 0) {
			materials.value = []
			materials.value.push({
				tonnage: newVal.tonnage,
				unloadWeight: newVal.unloadWeight,
				packageNum: newVal.packageNum === 2?"按件/Barang ":"按吨/Ton",
				usageUnit: newVal.usageUnit,
				materialName: newVal.materialName,
				loadSequence: 1
			})
			if(newVal.params.subMaterial!==null && newVal.params.subMaterial!==undefined){
				const sortedList = [...newVal.params.subMaterial].sort((a, b) => a.loadSequence - b.loadSequence)
				for(const item of sortedList){
					materials.value.push({
						tonnage: item.tonnage,
						unloadWeight: item.unloadWeight===null?'0':String(item.unloadWeight),
						packageNum: item.packageNum === 2?"按件/Barang ":"按吨/Ton",
						usageUnit: item.usageUnit,
						materialName: item.materialName,
						loadSequence: item.loadSequence
					})
				}
			}
    }
  },
  { immediate: true, deep: true }
)
</script>

<style lang="scss">
.ship-card {
  border-radius: 16rpx;
  background: #fff;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

  .wd-card__content {
    padding: 10px 0 !important;
  }

  :deep(.wd-tooltip__inner) {
    white-space: normal !important;
  }

  :deep(.wd-tooltip__hidden) {
    left: 0;
    bottom: 0;
  }

  :deep(.wd-card__footer) {
    padding: 12rpx 20rpx !important;
    background: #fafafa;
    border-top: 1rpx solid #f5f5f5;

    .footer {
      display: flex;
      justify-content: flex-end;
      gap: 12rpx;
    }

    .footer-btn {
      padding: 8rpx 20rpx;
      font-size: 24rpx;
      border-radius: 12rpx;
      transition: all 0.2s ease;

      &:hover {
        opacity: 0.85;
        transform: scale(1.02);
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
    border-bottom: 1rpx solid #f5f5f5;

    .ship-name {
      font-size: 34rpx;
      font-weight: 600;
      color: #1a1a1a;
      // flex: 1;
      padding-right: 12rpx;
    }

    .status-badge {
      display: flex;
      align-items: center;

      .tag_class {
        padding: 10rpx 12rpx;
        font-size: 20rpx;
        line-height: 24rpx;
        border-radius: 8rpx;
        font-weight: 500;
      }
    }
  }

  .card-body {
    padding: 10rpx 20rpx;

    .info-container {
      display: flex;
      gap: 20rpx;

      .info-left {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 12rpx;
        justify-content: center;

        .info-primary {
          font-size: 36rpx;
          color: #333;
          line-height: 40rpx;
          font-weight: 500;
          margin-bottom: 14rpx;

          &.highlight {
            color: #ff6b46;
            font-weight: 600;
          }
        }
      }

      .info-right {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 12rpx;
        background: #f9f9f9;
        padding: 12rpx;
        border-radius: 8rpx;
				max-height: 290rpx;
				overflow-y: auto;
        .info-item {
          display: flex;
          align-items: center;
          gap: 12rpx;

          .info-label {
            font-size: 24rpx;
            color: #888;
          }

          .info-value {
            font-size: 26rpx;
            color: #555;
            font-weight: 500;
          }
        }
      }
    }
  }
}
</style>