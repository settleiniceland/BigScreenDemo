<template>
  <view class="page">
    <view class="title">
      <view>欢迎使用<br />海港管理系统</view>
    </view>

    <view class="form">
      <view class="tab">
        <view class="select type1">账号登陆</view>
      </view>
      <view class="inputs_button">
        <wd-form ref="form1" :model="data.formData" errorType="toast">
          <view class="inputs">
            <view class="account">
              <wd-input
                prop="username"
                clearable
                v-model="data.formData.username"
                :placeholder="t('login.usernamePlaceholder')"
                :rules="[
                  { required: true, message: t('login.usernamePlaceholder') }
                ]"
              />
            </view>
            <view class="password">
              <wd-input
                prop="password"
                show-password
                v-model="data.formData.password"
                :placeholder="t('login.passwordPlaceholder')"
                :rules="[
                  { required: true, message: t('login.passwordPlaceholder') }
                ]"
              />
            </view>
          </view>
        </wd-form>

        <view class="button">
          <wd-button @click="submit" :round="false" size="large" block
            >登录</wd-button
          >
        </view>
      </view>
    </view>
  </view>
  <wd-toast selector="login" />
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { onReady } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { userApi } from '@/api'
import { useUserStore } from '@/store'
import { useToast } from 'wot-design-uni'

const { t } = useI18n()
const toast = useToast('login')
// 获取自定义的store
const store = useUserStore()
const loading = ref(false)
const form1 = ref()
const data = reactive({
  formData: {
    username: '',
    password: ''
  }
})

const getInfo = () => {
  console.log('store.tabbar', store.tabbar)
  userApi.getUserInfo().then(async (res) => {
    await store.login(res)
    console.log('tabbar=======', store.tabbar)
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
}
const submit = () => {
  uni.showLoading()
  form1.value
    .validate()
    .then(({ valid, errors }) => {
      if (valid) {
        loading.value = true
        userApi
          .login(data.formData)
          .then((res) => {
            store.setToken(res.token)
            getInfo()
            uni.hideLoading()
            loading.value = false
          })
          .catch((err) => {
            toast.error(err.msg)
            uni.hideLoading()
            loading.value = false
          })
      }
    })
    .catch((error) => {
      uni.hideLoading()
      console.log(error, 'error')
    })
}
onReady(() => {})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: url('../../static/image/bc2.png') no-repeat top center;
  background-size: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .title {
    padding-top: 280rpx;
    padding-bottom: 70rpx;

    view {
      padding-left: 64rpx;
      font-size: 48rpx;
      font-weight: 700;
      color: #fff;
    }
  }

  .form {
    flex: 1;
    // margin: 0 32rpx;
    border-radius: 40rpx;
    background-color: #fff;
    overflow: hidden;

    .wd-input {
      width: 100%;
      background: unset;
      :deep(.wd-input__icon),
      :deep(.wd-icon) {
        background: unset;
      }
    }

    .wd-input::after {
      background-color: unset;
    }

    .tab {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background-color: #ecf1fb;
      height: 100rpx;
      background: linear-gradient(180deg, #e3ebf9 0%, #ffffff 100%);

      view {
        width: 340rpx;
        line-height: 100rpx;
        text-align: center;
        color: #9e9e9e;
        position: relative;
      }

      view::after {
        content: '';
        width: 50rpx;
        height: 8rpx;
        display: block;
        background-color: #3264ed;
        border-radius: 4rpx;
        position: absolute;
        left: 50%;
        bottom: 14rpx;
        transform: translateX(-50%);
        opacity: 0;
      }

      view.select {
        background-color: #fff;
        color: #3264ed;
      }

      view.type1.select {
        border-radius: 0 40rpx 0 0;
      }

      view.type2.select {
        border-radius: 40rpx 0 0;
      }

      view.select::after {
        opacity: 1;
      }
    }

    .inputs_button {
      background-color: #fff;

      .inputs {
        padding: 80rpx 32rpx 0;
        margin-bottom: 80rpx;

        .account,
        .password,
        .vcode {
          height: 96rpx;
          border-radius: 20rpx;
          padding: 0 48rpx;
          display: flex;
          align-items: center;
          background-color: #f7fafc;
          :deep(.wd-input__suffix) {
            display: flex;
            align-items: center;
          }
          .code {
            height: 60rpx;
          }

          input {
            flex: 1;
          }
        }

        .account,
        .password {
          margin-bottom: 48rpx;
        }

        .vcode {
          text {
            text-wrap: nowrap;
            font-size: 26rpx;
            background-color: #fff;
            padding: 14rpx 30rpx;
            border-radius: 12rpx;
          }
        }
      }

      .button {
        padding: 0 32rpx;

        view {
          line-height: 96rpx;
          border-radius: 20rpx;
          text-align: center;
          font-size: 32rpx;
          background-color: #3264ed;
          color: #fff;
        }
      }
    }
  }
}
</style>
