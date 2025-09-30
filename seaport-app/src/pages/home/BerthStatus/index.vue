<template>
  <div class="loading" v-if="state.loading">
    <wd-loading size="50px" type="outline" />
  </div>
  <view class="container-berth" v-else>
    <!-- 搜索框 -->
    <view class="search-section">
      <view class="search-wrapper">
        <wd-icon class="search-icon" name="search1" size="22px" />
        <input
          class="search-input"
          type="text"
          :placeholder="t('port.berthStatus.inputBerthName')"
          v-model="state.searchQuery"
          @input="handleSearch"
        />
        <text class="clear-icon" v-if="state.searchQuery" @tap="clearSearch"
          >✖</text
        >
      </view>
    </view>

    <!-- 统计信息（Tabs） -->
    <view class="stats-section">
      <view class="tabs">
        <view
          v-for="item in BerthStatusOptions"
          :key="item.value"
          class="tab-item"
          :class="[
            getStatusClass(item.value),
            { active: state.activeStatus === item.value }
          ]"
          @tap="switchStatus(item.value)"
        >
          <text class="tab-text">{{ item.label }}</text>
          <view class="tab-badge">{{ item.count }}</view>
          <view class="tab-line" v-if="state.activeStatus === item.value" />
        </view>
      </view>
    </view>

    <!-- 泊位列表 -->
    <view class="berth-list">
      <wd-status-tip
        v-if="!displayedBerths.length"
        image="content"
        :tip="t('port.berth.noContent')"
      />
      <view v-else class="berth-items">
        <view
          v-for="berth in displayedBerths"
          :key="berth.berthCode"
          class="berth-item"
          :class="getStatusClass(berth.status)"
        >
          <view class="berth-header">
            <text class="berth-info">{{ berth.berthName }}</text>
            <text class="berth-code"
              >{{ t('port.berthStatus.berthCode') }}:
              {{ berth.berthCode }}</text
            >
          </view>
          <text
            v-if="berth.status === '2' && berth.remark01"
            class="reason-text"
          >
            {{ t('port.berthStatus.maintenanceReason') }}: {{ berth.remark01 }}
          </text>
        </view>
      </view>
    </view>
  </view>
  <wd-toast />
</template>

<script setup>
import { reactive, computed } from 'vue'
import { getBerthStatusList } from '@/api/modules/berth'
import { useToast } from 'wot-design-uni'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
const toast = useToast()
const { t } = useI18n()
const state = reactive({
  loading: false,
  berthsList: [],
  activeStatus: '1',
  searchQuery: ''
})

// Flatten berth data
const berths = computed(() =>
  state.berthsList.flatMap((group) =>
    group.dockBerthInfoChildren.map((berth) => ({
      berthName: berth.berthName,
      berthCode: berth.berthCode,
      status: group.berthStatus,
      remark01: berth.remark01
    }))
  )
)

// Filtered berths based on search query
const filteredBerths = computed(() =>
  state.searchQuery
    ? berths.value.filter((b) => {
        return b.berthName
          .toLowerCase()
          .includes(state.searchQuery.toLowerCase())
      })
    : berths.value
)

// Status options with dynamic counts
const BerthStatusOptions = computed(() => [
  {
    value: '0',
    label: t('port.berthStatus.free'),
    count: filteredBerths.value.filter((b) => b.status === '0').length
  },
  {
    value: '1',
    label: t('port.berthStatus.occupied'),
    count: filteredBerths.value.filter((b) => b.status === '1').length
  },
  {
    value: '2',
    label: t('port.berthStatus.maintenance'),
    count: filteredBerths.value.filter((b) => b.status === '2').length
  }
])

// Displayed berths for current status
const displayedBerths = computed(() =>
  filteredBerths.value.filter((b) => b.status === state.activeStatus)
)

// Switch status
const switchStatus = (status) => {
  state.activeStatus = status
}

// Handle search and auto-switch status
const handleSearch = () => {
  if (state.searchQuery) {
    const matchedBerth = berths.value.find((b) =>
      b.berthName.includes(state.searchQuery)
    )
    if (matchedBerth) state.activeStatus = matchedBerth.status
  }
}

// Clear search
const clearSearch = () => {
  state.searchQuery = ''
}

// Status class mapping
const getStatusClass = (status) => ({
  occupied: status === '1',
  free: status === '0',
  maintenance: status === '2'
})

// Fetch berth data
const getBerthStatusData = async () => {
  state.loading = true
  try {
    const res = await getBerthStatusList()
    if (res.code === 200) {
      state.berthsList = res.data
      state.loading = false
    } else toast.error('获取泊位数据失败')
  } catch (error) {
    toast.error(error.msg || '网络错误')
    state.loading = false

    console.error('接口报错:', error)
  }
}

onShow(getBerthStatusData)
</script>

<style lang="scss">
.loading {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.container-berth {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 20rpx;
  background: linear-gradient(135deg, #eceff5, #d9dfe9);

  /* 搜索框 */
  .search-section {
    margin: 30rpx 0 20rpx;
  }

  .search-wrapper {
    position: relative;
    display: flex;
    align-items: center;
  }

  .search-input {
    width: 100%;
    height: 80rpx;
    padding: 0 80rpx;
    font-size: 28rpx;
    border-radius: 12rpx;
    background: #fff;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
    border: none;
    transition: box-shadow 0.3s;

    &:focus {
      box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.08);
    }
  }

  .search-icon {
    position: absolute;
    left: 30rpx;
    color: #718096;
  }

  .clear-icon {
    position: absolute;
    right: 30rpx;
    color: #718096;
    font-size: 36rpx;
    cursor: pointer;
    transition: color 0.3s;

    &:hover {
      color: #2d3748;
    }
  }

  /* Tabs */
  .stats-section {
    margin-bottom: 40rpx;
  }

  .tabs {
    display: flex;
    background: #fff;
    border-radius: 16rpx;
    padding: 10rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);
  }

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    cursor: pointer;
    position: relative;
    transition: background 0.3s;

    &:hover {
      background: rgba(0, 0, 0, 0.02);
    }

    &.active {
      .tab-text {
        font-weight: 700;
        transform: scale(1.05);
      }

      &.occupied .tab-badge {
        background: #52c41a;
        color: #fff;
      }

      &.free .tab-badge {
        background: #1890ff;
        color: #fff;
      }

      &.maintenance .tab-badge {
        background: #fa8c16;
        color: #fff;
      }
    }
  }

  .tab-text {
    display: block;
    font-size: 32rpx;
    font-weight: 500;
    transition: transform 0.3s;
  }

  .tab-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 40rpx;
    height: 40rpx;
    padding: 0 10rpx;
    margin-top: 10rpx;
    font-size: 24rpx;
    font-weight: 500;
    border-radius: 20rpx;
    background: rgba(0, 0, 0, 0.08);
    color: #2d3748;
    transition: all 0.3s;
  }

  .tab-line {
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 80rpx;
    height: 6rpx;
    border-radius: 3rpx;
    transform: translateX(-50%);
  }

  /* 泊位列表 */
  .berth-list {
    flex: 1;
    padding: 30rpx;
    background: #fff;
    border-radius: 16rpx;
    overflow-y: auto;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.06);

    &::-webkit-scrollbar {
      width: 10rpx;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.15);
      border-radius: 5rpx;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }

  .berth-items {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300rpx, 1fr));
    gap: 24rpx;
  }

  .berth-item {
    padding: 24rpx;
    border-radius: 12rpx;
    border: 2rpx solid transparent;
    background: #f9fafc;
    transition: all 0.3s;

    &:hover {
      border-color: rgba(0, 0, 0, 0.1);
      transform: translateY(-4rpx);
      box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.08);
    }
  }

  .berth-header {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }

  .berth-info {
    font-size: 32rpx;
    font-weight: 600;
    color: #2d3748;
  }

  .berth-code {
    font-size: 24rpx;
    color: #718096;
  }

  .reason-text {
    display: block;
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #718096;
    line-height: 1.5;
  }

  /* 状态颜色 */
  .occupied {
    color: #52c41a;
    background: rgba(82, 196, 26, 0.05);

    .tab-line {
      background: #52c41a;
    }
  }

  .free {
    color: #1890ff;
    background: rgba(24, 144, 255, 0.05);

    .tab-line {
      background: #1890ff;
    }
  }

  .maintenance {
    color: #fa8c16;
    background: rgba(250, 140, 22, 0.05);

    .tab-line {
      background: #fa8c16;
    }
  }
}
</style>
