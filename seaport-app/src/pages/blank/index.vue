<template>
  <view class="blank-page">
    <view class="loading">
      <!-- <wd-loading type="outline" size="50" /> -->
      <image src="../../static/image/alogo.png" mode="aspectFill" />
    </view>
    <view class="title">
      <text>{{ t('app.title') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onLoad, onReady } from '@dcloudio/uni-app'
import { useUserStore } from '@/store'
import { setTabBarByPermissions } from '@/utils/tabbar'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const store = useUserStore()

onLoad(() => {})
onReady(async () => {
  console.log('blank-page-onReady')
  // 设置tabbar权限
  await setTabBarByPermissions()
  uni.switchTab({
    url: store.tabbar[0].url,
    success: (res) => {
      console.log('switchTab--h回调成功', res, store.tabbar[0])
    },
    fail: (res) => {
      console.log('switchTab--回调失败', res, store.tabbar[0])
    }
  })
})
</script>

<style scoped lang="less">
.blank-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100wh;
  background: linear-gradient(180deg, #cbdeff 0%, #ffffff 100%);
  .loading {
    height: 90vh;
    width: 100wh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    image {
      width: 100px;
      height: 100px;
    }
  }
  .title {
    color: #0073ff;
    font-size: 16px;
  }
}
</style>
