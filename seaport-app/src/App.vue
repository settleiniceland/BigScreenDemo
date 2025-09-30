<script setup lang="ts">
import { onLaunch, onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { userApi } from './api'
import { useUserStore } from '@/store'
import { checkVersion } from '@/api/modules/system'

const { locale, t } = useI18n()

const store = useUserStore()
uni.hideTabBar()
const checkUpdate = () => {
  // 查询当前版本号
  // #ifdef APP-PLUS
  // const { platform } = uni.getSystemInfoSync()
  return new Promise((resolve, reject) => {
    plus.runtime.getProperty(plus.runtime.appid as string, (wgtinfo) => {
      console.log('检查版本更新wgtinfo---', wgtinfo)
      const { versionCode } = wgtinfo // 客户端版本号
      const data = {
        os: 0,
        version: versionCode
      }
      checkVersion(data)
        .then((res) => {
          console.log('检查版本更新res---', res)
          if (res.data) {
            store.isUpdate = true
            store.updateInfo = res.data
            uni.hideTabBar()
          } else {
            store.isUpdate = false
          }
        })
        .catch((err) => {
          console.log('检查版本更新失败---', res)
          console.log('err', err)
        })
        .finally(() => {
          resolve(store.isUpdate)
        })
    })
  })

  // #endif
}

const getInfo = () => {
  userApi.getUserInfo().then(async (res) => {
    await store.login(res)
    console.log('getInfo-----------------tabbar', store.tabbar)
    uni.switchTab({
      url: store.tabbar[0].url,
      success: (res) => {
        console.log('app-onLaunch-switchTab--h回调成功', res, store.tabbar[0])
      },
      fail: (res) => {
        console.log('app-onLaunch-switchTab--回调失败', res, store.tabbar[0])
      }
    })
  })
}
onLaunch(() => {
  const localeStorage = uni.getStorageSync('language') || 'zh-Hans'
  locale.value = localeStorage
  uni.setStorageSync('language', localeStorage)
  if (uni.getStorageSync('token')) {
    if (!uni.getStorageSync('userInfo')) {
      getInfo()
    }
  } else {
    uni.redirectTo({
      url: '/pages/login/login'
    })
  }
  checkUpdate()
})
</script>
<style lang="scss">
// @import url('./uni.scss');
/* 修改 uni-page-body 样式 */
uni-page-body {
  background: $bg-color-grey;
  height: 100%;
  /* 适配全屏高度 */
}
</style>
