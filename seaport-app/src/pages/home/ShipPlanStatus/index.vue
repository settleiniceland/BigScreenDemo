<template>
  <view class="page-status">
    <!-- 总数卡片 -->
    <view class="total-card">
      <div class="left">
        <text class="total-title">{{ t('port.planStatus.totalPlans') }}</text>
        <text class="total-count">{{ state.totalCount ?? 0 }}</text>
      </div>
      <div class="right">
        <wd-icon name="list" size="22px"></wd-icon>
      </div>
    </view>
    <!-- 标题和刷新 -->
    <view class="header">
      <text class="header-title">{{
        t('port.planStatus.statusDistribution')
      }}</text>
      <view
        class="refresh-btn"
        @tap="refresh"
        :class="{ disabled: state.isRefreshing || state.isLoading }"
      >
        <wd-icon
          name="refresh1"
          size="16px"
          class="refresh-img"
          :class="{ rotating: state.isRefreshing }"
        ></wd-icon>
        <text class="refresh-btn-text">{{ t('port.planStatus.refresh') }}</text>
      </view>
    </view>
    <div class="loading" v-if="state.isLoading">
      <wd-loading />
    </div>

    <!-- 状态列表 -->
    <view class="status-grid" v-else>
      <view
        v-for="(item, index) in state.planStatusList"
        :key="index"
        class="status-card"
      >
        <!-- 颜色条 -->
        <view
          class="color-bar"
          :style="{ backgroundColor: Colors[item.status] || '#999' }"
        ></view>
        <!-- 状态标题 -->
        <view class="status-header" @tap="toggleExpand(index)">
          <view class="statusInfo">
            <text class="status-title">{{
              state.statusNames[item.status] ||
              t('port.planStatus.unknownStatus')
            }}</text>
            <text class="status-count">{{ item.count }}</text>
          </view>
          <view class="expand">
            <text class="expand-icon">{{
              state.expandedStatus[index] ? '▲' : '▼'
            }}</text>
          </view>
        </view>
        <!-- 物资列表 -->
        <view v-if="state.expandedStatus[index]" class="material-list">
          <view v-if="item.childrenMaterialVo.length > 0">
            <view
              v-for="(material, mIndex) in item.childrenMaterialVo"
              :key="mIndex"
              class="material-item"
            >
              <view class="material-progress-wrapper">
                <view class="progress-info">
                  <text class="material-name">{{ material.materialName }}</text>
                  <text class="progress-count"
                    >{{ material.count }} {{ t('port.summary.ships') }}
                  </text>
                  <text class="progress-percentage">
                    {{
                      getProgressWidth(item, material.count).toFixed(0)
                    }}%</text
                  >
                </view>
                <view class="material-progress">
                  <view
                    class="progress-bar"
                    :style="{
                      width: getProgressWidth(item, material.count) + '%',
                      backgroundColor: Colors[item.status] || '#999'
                    }"
                  >
                  </view>
                </view>
              </view>
            </view>
          </view>
          <view v-else class="empty-text">{{
            t('port.planStatus.noPlans')
          }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { getPlanStatusList } from '@/api/modules/plan'
import { Colors } from '@/contans/index'
import { diclist } from '@/api/modules/system'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const state = reactive({
  isRefreshing: false,
  isLoading: false,
  planStatusList: [],
  totalCount: '',
  statusNames: {},
  expandedStatus: []
})

// 切换折叠状态
const toggleExpand = (index) => {
  state.expandedStatus[index] = !state.expandedStatus[index]
}

// 计算进度条宽度
const getProgressWidth = (item, count) => {
  const maxCount = Math.max(item.count || 1, 1) // 使用 item.count，防止除以 0
  return Math.min((count / maxCount) * 100, 100) // 百分比，最大 100%
}

// 获取字典数据
const getDicData = async () => {
  try {
    const statusData = await diclist({
      dictType: 'plan_status',
      pageSize: 50
    })
    statusData?.rows?.forEach((i) => {
      state.statusNames[i.dictValue] = i.dictLabel
    })
  } catch (error) {
    console.error('获取字典数据失败:', error)
    uni.showToast({
      title: '获取状态字典失败',
      icon: 'error',
      duration: 1000
    })
  }
}

// 获取计划状态数据
const getPlanStatusData = async (isRefresh = false) => {
  state.isLoading = true
  try {
    const res = await getPlanStatusList()
    if (res.code === 200) {
      state.planStatusList = res.data.planStatusChildren || []
      state.totalCount = res.data.count || 0
      state.expandedStatus = new Array(state.planStatusList.length).fill(false)
      if (isRefresh) {
        state.isRefreshing = false
        uni.showToast({
          title: '已刷新',
          icon: 'success',
          duration: 1000
        })
      }
    } else {
      uni.showToast({
        title: '获取数据失败',
        icon: 'error',
        duration: 1000
      })
    }
  } catch (error) {
    console.error('获取计划状态失败:', error)
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error',
      duration: 1000
    })
  } finally {
    state.isLoading = false
    state.isRefreshing = false
  }
}

// 刷新：收起所有卡片并重新加载数据
const refresh = () => {
  if (state.isRefreshing || state.isLoading) return
  state.isRefreshing = true
  getPlanStatusData(true)
}

// 页面显示时加载数据
onShow(() => {
  getPlanStatusData()
  getDicData()
})
</script>

<style lang="scss">
$primary-color: #007aff; // 主色调（与 material-count 一致）
$background-color: #f6f8fa; // 页面背景
$card-background: #ffffff; // 卡片背景
$border-radius: 20rpx; // 统一圆角
$shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.1); // 卡片阴影
$gap: 32rpx; // 网格间距
$font-size-base: 28rpx; // 基础字体
$font-size-title: 30rpx; // 标题字体
$font-size-count: 38rpx; // 计数字体

.page-status {
  background-color: $background-color;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 26rpx;
  box-sizing: border-box;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .header-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333333;
    }

    .refresh-btn {
      display: flex;
      align-items: center;
      color: #2b5de0;

      &.disabled {
        color: #999999;
        pointer-events: none;
      }

      .refresh-img {
        font-size: 28rpx;
        margin-right: 8rpx;

        &.rotating {
          animation: rotate 1s linear infinite;
        }
      }

      .refresh-btn-text {
        font-size: $font-size-base;
      }
    }
  }

  .loading {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .total-card {
    background-color: rgb(43, 93, 224);
    color: #ffffff;
    padding: 32rpx;
    border-radius: $border-radius;
    margin-bottom: 24rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: $shadow;

    .left {
      display: flex;
      flex-direction: column;

      .total-title {
        font-size: $font-size-base;
        margin-bottom: 24rpx;
      }

      .total-count {
        font-size: 60rpx;
        font-weight: 700;
      }
    }

    .right {
      display: flex;
      align-items: center;

      .wd-icon {
        font-size: 44rpx;
      }
    }
  }

  .status-grid {
    flex: 1;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $gap;
    overflow-y: auto;
    box-sizing: border-box;

    .status-card {
      width: 100%;
      background-color: $card-background;
      border-radius: 0 0 $border-radius $border-radius;
      box-shadow: $shadow;
      box-sizing: border-box;

      .color-bar {
        height: 6rpx;
        width: 100%;
        border-radius: $border-radius $border-radius 0 0;
      }

      .status-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 20rpx 30rpx;
        background-color: $card-background;
        border-radius: $border-radius;

        .statusInfo {
          display: flex;
          flex-direction: column;
          color: #333333;

          .status-title {
            font-size: $font-size-title;
            margin-bottom: 10rpx;
          }

          .status-count {
            font-size: $font-size-count;
            font-weight: 700;
            color: #000000;
          }
        }

        .expand {
          display: flex;
          flex: 1;
          height: 100%;
          justify-content: flex-end;
          align-items: flex-start;

          .expand-icon {
            font-size: $font-size-base;
            color: #555555;
            transition: transform 0.3s ease;
          }
        }
      }

      .material-list {
        border-top: 1rpx solid #f3f4f6;
        padding: 0 20rpx;
        background-color: #fafafa;
        // max-height: 500rpx;
        overflow-y: auto;

        .material-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 16rpx 0;
          border-bottom: 1rpx solid #eeeeee;

          .material-name {
            font-size: $font-size-base;
            color: #333333;
            max-width: 50%;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .material-progress {
            flex: 1;
            height: 40rpx;
            background-color: #e6e6e6;
            border-radius: 20rpx;
            overflow: hidden;
            position: relative;
            margin-left: 16rpx;

            .progress-bar {
              height: 100%;
              background-color: $primary-color;
              display: flex;
              align-items: center;
              justify-content: flex-end;
              padding-right: 8rpx;
              transition: width 0.5s ease;
              border-radius: 20rpx;

              .progress-count {
                font-size: 24rpx;
                color: #ffffff;
                font-weight: 500;
              }
            }
          }
        }

        .empty-text {
          font-size: $font-size-base;
          color: #999999;
          text-align: center;
          padding: 20rpx 0;
        }
      }
    }
  }

  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }

    to {
      transform: rotate(360deg);
    }
  }

  /* 响应式：手机屏幕一栏 */
  @media screen and (max-width: 768px) {
    .page {
      padding: 16rpx;
    }

    .header {
      margin: 20rpx 0;

      .header-title {
        font-size: 32rpx;
      }
    }

    .total-card {
      padding: 24rpx;
      margin-bottom: 16rpx;

      .left {
        .total-title {
          font-size: 24rpx;
          margin-bottom: 20rpx;
        }

        .total-count {
          font-size: 48rpx;
        }
      }

      .right {
        .wd-icon {
          font-size: 36rpx;
        }
      }
    }

    .status-grid {
      grid-template-columns: 1fr;
      gap: 24rpx;

      .status-card {
        .status-header {
          padding: 16rpx 24rpx;

          .statusInfo {
            .status-title {
              font-size: 28rpx;
              margin-bottom: 8rpx;
            }

            .status-count {
              font-size: 32rpx;
            }
          }

          .expand {
            .expand-icon {
              font-size: 24rpx;
            }
          }
        }

        .material-list {
          padding: 20rpx 16rpx;

          .material-item {
            padding: 12rpx 0;

            .material-progress-wrapper {
              width: 100%;

              .progress-info {
                display: flex;
                justify-content: space-between;
                margin-bottom: 4rpx;

                .material-name {
                  font-size: 24rpx;
                  max-width: 60%;
                  font-weight: 600;
                }

                .progress-count {
                  font-size: 24rpx;
                  margin-right: 10rpx;
                }
              }

              .material-progress {
                height: 18rpx;
                margin-left: 2rpx;
                border-radius: 16rpx;

                .progress-bar {
                  border-radius: 16rpx;
                }
              }
            }
          }

          .empty-text {
            font-size: 24rpx;
            padding: 16rpx 0;
          }
        }
      }
    }
  }
}
</style>
