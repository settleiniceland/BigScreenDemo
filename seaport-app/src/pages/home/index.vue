<template>
  <view class="home">
    <view class="header">
      <view class="title">{{ homeTitle }}</view>
      <view class="subtitle">{{ t('port.title.operationManagement') }}</view>
    </view>

    <view class="btn-grid">
      <view
        v-for="(item, index) in featureList"
        :key="index"
        class="entry-btn"
        :class="{ disabled: item.disabled }"
        @tap="handleClick(item)"
      >
        <view class="btn-content">
          <image :src="item.icon" class="icon" />
          <text class="label">{{ item.name }}</text>
        </view>
      </view>
    </view>
  </view>

  <tabbar :popVal="0" />
  <pop :updateInfo="store.updateInfo" v-if="store.isUpdate" />
  <wd-toast />
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
import tabbar from '@/components/tabbar/index.vue'
import pop from '@/uni_modules/uni-upgrade-center-app/pages/upgrade-popup.vue'
import { useUserStore } from '@/store'

const toast = useToast()
const store = useUserStore()
const { t } = useI18n()

const homeTitle = computed(() => t('port.title.functionOverview'))

// 使用computed确保语言切换时菜单项能响应式更新
const featureList = computed(() => [
  {
    name: t('port.menu.shipProgress'),
    path: '/pages/home/ShipProgress/index',
    icon: '/static/icons/ship-progress.png',
    disabled: false
  },
  {
    name: t('port.menu.workSummary'),
    path: '/pages/home/WorkSummary/index',
    icon: '/static/icons/work-summary.png',
    disabled: false
  },
  {
    name: t('port.menu.berthStatus'),
    path: '/pages/home/BerthStatus/index',
    icon: '/static/icons/berth-status.png',
    disabled: false
  },
  {
    name: t('port.menu.planStatus'),
    path: '/pages/home/ShipPlanStatus/index',
    icon: '/static/icons/plan-status.png',
    disabled: false
  }
])

const handleClick = (item) => {
  if (item.disabled) {
    toast.info(t('common.comingSoon'))
    return
  }
  if (item.name === '计划单明细') {
    uni.switchTab({
      url: item.path
    })
  } else {
    uni.navigateTo({
      url: item.path
    })
  }
}
</script>

<style scoped>
.home {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0 20rpx;
}

.header {
  padding: 20rpx 0;
  margin-bottom: 24rpx;
  border-bottom: 1rpx solid #eee;
  /* 添加分割线 */
}

.title {
  font-size: 40rpx;
  font-weight: 700;
  color: #2c3e50;
  line-height: 48rpx;
}

.subtitle {
  font-size: 24rpx;
  color: #7f8c8d;
  margin-top: 8rpx;
}

.btn-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 10rpx;
}

.entry-btn {
  width: 48%;
  /* 两列布局 */
  height: 200rpx;
  border-radius: 20rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  /* 白色背景 */
  border: 2rpx solid #e0e4e8;
  /* 浅灰边框 */
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.05);
  /* 轻微阴影 */
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

.btn-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
}

.icon {
  width: 90rpx;
  height: 90rpx;
  margin-bottom: 16rpx;
}

.label {
  font-size: 30rpx;
  font-weight: 500;
  color: #34495e;
  /* 深灰蓝色文字 */
}

.entry-btn:hover {
  border-color: #3498db;
  /* 悬停时边框变蓝 */
}

.entry-btn:active {
  transform: scale(0.96);
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.08);
}

.entry-btn.disabled {
  background-color: #f5f6f8;
  /* 禁用状态浅灰背景 */
  border-color: #d0d4d9;
  box-shadow: none;
}

.entry-btn.disabled .label {
  color: #999;
  /* 禁用状态文字变灰 */
}

.entry-btn.disabled .icon {
  opacity: 0.5;
  /* 禁用状态图标半透明 */
}
</style>
