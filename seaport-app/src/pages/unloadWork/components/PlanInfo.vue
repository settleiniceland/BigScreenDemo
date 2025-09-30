<template>
  <view class="plan-info">
    <wd-collapse v-model="state.collapseValue">
      <wd-collapse-item :title="t('port.workLog.planInfo')" name="planInfo">
        <!-- 船名单独一行，带展开 -->
        <view class="info-item ship-row">
          <text class="info-label">{{ t('port.workLog.shipName') }}:</text>
          <view class="ship-container">
            <wd-collapse viewmore :line-num="1" v-model="state.shipNameValue">
              <text class="info-value ship-name">
                {{ planInfoItems[0].value || '-' }}
              </text>
            </wd-collapse>
          </view>
        </view>
        <!-- 其他字段两两一行 -->
        <view
          class="info-row"
          v-for="(pair, index) in pairedItems"
          :key="index"
        >
          <view
            class="info-item"
            v-for="item in pair"
            :key="item.label"
            :data-label="item.label"
          >
            <text class="info-label">{{ t(item.label) }}:</text>
            <text class="info-value" v-if="item.label !== '计划状态'">{{
              item.value || '-'
            }}</text>
            <wd-tag
              class="tag_class"
              v-else
              :color="Colors[item.value ?? '0']"
              :bg-color="Colors[item.value]"
              round
              plain
            >
              {{
                state.statusOptions.find((i) => i.value === item.value)?.label
              }}
            </wd-tag>
          </view>
        </view>
      </wd-collapse-item>
    </wd-collapse>
  </view>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { Colors } from '@/contans/index'
import { diclist } from '@/api/modules/system'

const { t } = useI18n()
const { planInfo } = defineProps({
  // 当前的值
  planInfo: {
    type: Object,
    default: () => ({})
  }
})

const state = reactive({
  collapseValue: [],
  shipNameValue: '',
  statusOptions: []
})
// 计算属性：计划单信息字段
const planInfoItems = computed(() => {
  return [
    {
      label: 'port.workLog.shipName',
      value: planInfo?.shipName
    },
    // {
    //   label: 'port.workLog.materialName',
    //   value: planInfo?.materialName
    // },
    // {
    //   label: 'port.workLog.planStatus',
    //   value: planInfo?.status
    // },
    {
      label: 'port.berth.pier',
      value: planInfo?.hbName
    },
    {
      label: '总吨位',
      value: planInfo?.planTonnage
    },
    {
      label: 'port.time.operationTime',
      value: planInfo?.operationTime
    },
    // {
    //   label: 'port.planManagement.inputCompletedWork',
    //   value: planInfo?.unloadWeight
    // }
  ]
})

// 计算属性：除船名外两两分组
const pairedItems = computed(() => {
  const items = planInfoItems.value.slice(1) // 去掉船名
  const pairs = []
  for (let i = 0; i < items.length; i += 2) {
    pairs.push(items.slice(i, i + 2))
  }
  return pairs
})

const getDicData = async () => {
  try {
    const statusData = await diclist({
      dictType: 'plan_status',
      pageSize: 50
    })
    state.statusOptions = statusData?.rows?.map((i) => ({
      label: i.dictLabel,
      value: i.dictValue
    }))
  } catch (error) {
    console.error('接口报错 ', error)
  }
}

onShow(() => {
  if (planInfo?.id) {
    getDicData()
  }
})
</script>

<style lang="scss">
.plan-info {
  :deep(.wd-collapse) {
    background: $card-bg-color;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

    .is-viewmore {
      padding: 0;
    }

    .wd-collapse-item {
      .wd-collapse-item__title {
        font-size: 30rpx;
        font-weight: 500;
        color: #333;
      }
    }

    .ship-row {
      display: flex;
      align-items: center;
      border-bottom: 1px solid #eee;

      .info-label {
        flex: 0 0 120rpx;
        font-size: 28rpx;
        color: #666;
        line-height: 40rpx;
      }

      .ship-container {
        flex: 1;
        display: flex;
        align-items: center;

        .info-value.ship-name {
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          line-height: 40rpx;
          font-weight: 600;
        }
      }
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 14rpx 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .info-item {
        flex: 1;
        display: flex;
        align-items: flex-start;

        .info-label {
          flex: 0 0 130rpx;
          font-size: 28rpx;
          color: #666;
          line-height: 40rpx;
        }

        .info-value {
          flex: 1;
          font-size: 28rpx;
          color: #333;
          line-height: 40rpx;
          font-weight: 600;
          word-break: break-all;
        }

        // 为"作业时间"字段增加空间
        &[data-label='作业时间'] {
          flex: 2; // 比其他字段占用更多空间

          .info-value {
            white-space: nowrap; // 避免换行
            overflow: hidden;
            text-overflow: ellipsis; // 超长时显示省略号
          }
        }
      }

      .info-item + .info-item {
        margin-left: 20rpx; // 两列间距
      }
    }
  }
}
</style>
