<template>
  <view class="summaryPage">
    <view class="container">
      <view class="gridWrapper annual">
        <view class="card annual-card">
          <text class="label"
            >{{ t('port.summary.annualThroughput') }} (T)</text
          >
          <text class="value">{{
            formatNumber(state.throughputData.year)
          }}</text>
        </view>
      </view>
      <!-- Throughput Data -->
      <view class="gridWrapper">
        <view v-for="(item, index) in summaryData" :key="item" class="card">
          <text class="label"
            >{{
              item === 'yesterday'
                ? t('port.summary.yesterdayThroughput')
                : t('port.summary.monthlyThroughput')
            }}
            (T)</text
          >
          <text class="value">{{
            formatNumber(state.throughputData[item])
          }}</text>
        </view>
      </view>

      <!-- Loading State -->
      <view v-if="state.loading" class="loadingWrapper">
        <wd-loading />
      </view>

      <!-- Ship Arrival Tabs -->
      <view v-else class="tabsWrapper">
        <view class="tabs">
          <view
            v-for="tab in tabs"
            :key="tab.key"
            class="tabItem"
            :class="{ active: state.currentTab === tab.key }"
            @tap="state.currentTab = tab.key"
          >
            {{ tab.label }}
            <view
              class="count-badge"
              :class="tab.key === 'today' ? '' : 'tomorrow-count'"
            >
              {{ getTabCount(tab.key) }}
            </view>
          </view>
        </view>

        <scroll-view
          scroll-y
          class="scrollArea"
          refresher-enabled
          :refresher-triggered="state.refreshing"
          @refresherrefresh="onRefresh"
        >
          <view class="listContainer">
            <wd-status-tip
              v-if="!getCurrentTabData?.childrenMaterialVo.length"
              class="emptyTip"
              image="content"
              :tip="t('port.berth.noContent')"
            />
            <view
              v-for="(material, idx) in getCurrentTabData?.childrenMaterialVo"
              :key="material.materialName + idx"
              :class="[
                'materialItem',
                material.materialName.includes('煤')
                  ? 'coal'
                  : material.materialName.includes('镍')
                  ? 'nickel'
                  : ''
              ]"
            >
              <text class="materialName">{{ material.materialName }}</text>
              <text class="materialCount">
                <text class="font-bold text-xl">{{ material.count }}</text
                >{{ t('port.summary.refreshed') }}
              </text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getThroughput, getShipArrival } from '@/api/modules/plan'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const tabs = [
  {
    key: 'today',
    label: t('port.summary.yesterdayThroughput')
  },
  {
    key: 'tomorrow',
    label: t('port.summary.monthlyThroughput')
  }
]
const summaryData = ['month', 'yesterday']

const state = reactive({
  currentTab: 'today',
  refreshing: false,
  loading: false,
  throughputData: {},
  shipCountData: []
})

const formatNumber = (num) => {
  if (num == null || isNaN(num)) return '0'
  const [integer, decimal = ''] = String(num).split('.')
  let result = ''
  for (let i = integer.length - 1, count = 0; i >= 0; i--) {
    result = integer[i] + result
    count++
    if (count % 3 === 0 && i > 0) result = ',' + result
  }
  return decimal ? `${result}.${decimal}` : result
}

const getTabCount = (tab) =>
  state.shipCountData.find((data) => data.key === tab)?.count || 0

const getCurrentTabData = computed(
  () =>
    state.shipCountData.find((data) => data.key === state.currentTab) || {
      childrenMaterialVo: []
    }
)

const fetchData = async (isRefresh = false) => {
  if (!isRefresh) state.loading = true
  try {
    const [throughputRes, shipRes] = await Promise.all([
      getThroughput(),
      getShipArrival()
    ])
    if (throughputRes.code === 200) {
      state.throughputData = throughputRes.data
    }
    if (shipRes.code === 200) {
      state.shipCountData = shipRes.data
    }
    if (isRefresh) {
      state.refreshing = false
      uni.showToast({
        title: t('port.summary.networkError'),
        icon: 'success'
      })
    }
  } catch (error) {
    uni.showToast({
      title: error.msg || t('port.summary.networkError'),
      icon: 'error'
    })
    console.error('接口报错:', error)
  } finally {
    state.loading = false
  }
}

const onRefresh = () => {
  state.refreshing = true
  setTimeout(() => {
    fetchData(true)
  }, 1000)
}

onShow(() => fetchData())
</script>

<style lang="scss">
.summaryPage {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  .container {
    flex: 1;
    padding: 28rpx;
    background-color: #f9fafb; // bg-gray-50
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .annual {
    margin-bottom: 20rpx;
  }

  .gridWrapper {
    display: flex;
    gap: 24rpx;
    flex-wrap: nowrap;
  }

  .card {
    flex: 1;
    background-color: #ffffff;
    padding: 24rpx;
    border: 2rpx solid #e5e7eb;
    border-radius: 20rpx;
    box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
    transition: all 0.2s;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 0;
  }

  .card:hover {
    box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.15);
    background-color: #f9fafb;
  }

  .card:active {
    background-color: #f3f4f6; // active:bg-gray-100
  }

  .label {
    font-size: 28rpx;
    color: #6b7280; // text-gray-500
    margin-bottom: 12rpx;
    font-weight: 500;
  }

  .value {
    font-size: 48rpx;
    font-weight: bold;
    color: #111827; // text-gray-900
  }

  .loadingWrapper {
    display: flex;
    justify-content: center;
    margin-top: 80rpx;
  }

  .tabsWrapper {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
    margin-top: 32rpx;
  }

  .tabs {
    display: flex;
    background-color: #f3f4f6;
    border-radius: 12rpx;
    padding: 8rpx;
    flex-shrink: 0; // 明确 tabs 不被压缩
  }

  .tabItem {
    flex: 1;
    text-align: center;
    padding: 16rpx 32rpx;
    font-weight: bold;
    color: #6b7280;
    border-radius: 8rpx;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .count-badge {
    background-color: #2ecc71; // Green for today
    color: #fff;
    font-size: 12px;
    font-weight: 600;
    padding: 2px 8px;
    border-radius: 12px;
    line-height: 1.5;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
    margin-left: 10rpx;
  }

  .tomorrow-count {
    background-color: #e67e22; // Orange for tomorrow
  }

  .tabItem:hover {
    background-color: #e5e7eb;
  }

  .tabItem.active {
    background-color: #ffffff;
    box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.05);
    color: #111827;
  }

  .scrollArea {
    flex: 1; // 填充 tabsWrapper 剩余高度
    // height: 100%; // 确保占满父容器
    margin-top: 32rpx;
    overflow-y: auto;
  }

  .listContainer {
    background-color: #ffffff;
    border-radius: 24rpx;
    border: 2rpx solid #e5e7eb;
    overflow: hidden;
  }

  .emptyTip {
    padding: 80rpx 0;
  }

  .materialItem {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx;
    border-top: 2rpx solid #f3f4f6;
  }

  .materialItem:first-child {
    border-top: none;
  }

  .materialName {
    color: #111827;
    font-weight: 500;
  }

  .materialCount {
    color: #4b5563;
  }

  .coal {
    background-color: #eff6ff;
    border-left: 8rpx solid #3b82f6;
  }

  .nickel {
    background-color: #fff7ed;
    border-left: 8rpx solid #f97316;
  }
}
</style>
