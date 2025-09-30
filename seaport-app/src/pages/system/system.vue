<template>
  <view class="container">
    <view class="my-top">
      <view class="my-info">
        <view class="avatar">
          <wd-img
            v-if="userInfo?.avatar"
            :width="80"
            :height="80"
            :src="baseUrl + userInfo?.avatar"
            mode="aspectFill"
          >
            <template #error>
              <view class="error-wrap">
                <wd-img
                  :width="80"
                  :height="80"
                  src="../../static/image/avatar.png"
                />
              </view>
            </template>
          </wd-img>
          <wd-img
            v-else
            :width="80"
            :height="80"
            mode="aspectFill"
            src="../../static/image/avatar.png"
          ></wd-img>
        </view>

        <view class="name">
          {{ userInfo?.nickName }}
        </view>
      </view>
    </view>
    <view class="content">
      <view class="my-menu">
        <view class="tabs">
          <wd-tabs v-model="current">
            <wd-tab v-for="item in items" :key="item" :title="`${item}`">
            </wd-tab>
          </wd-tabs>
        </view>
        <view v-show="current === 0">
          <wd-cell-group border>
            <wd-cell
              size="large"
              :title="t('person.username')"
              :value="userInfo?.userName"
            ></wd-cell>
            <wd-cell
              size="large"
              :title="t('person.email')"
              :value="userInfo?.email"
              icon="email"
            ></wd-cell>
            <wd-cell
              size="large"
              :title="t('person.phone')"
              :value="userInfo?.phonenumber"
            ></wd-cell>
            <wd-cell
              size="large"
              :title="t('person.sex')"
              :value="
                userInfo?.sex === '0' ? t('person.male') : t('person.female')
              "
            ></wd-cell>
            <wd-cell
              size="large"
              :title="t('person.dept')"
              :value="userInfo?.dept?.deptName"
            ></wd-cell>
            <wd-cell
              size="large"
              :title="t('person.role')"
              :value="userInfo?.roles?.map((item) => item.roleName).join('/')"
            ></wd-cell>
          </wd-cell-group>
          <wd-button
            type="primary"
            class="chang-info"
            size="large"
            block
            @click="changePersonInfo"
          >
            {{ t('person.changeInfo') }}
          </wd-button>
        </view>
        <view v-show="current === 1">
          <wd-form ref="form1" :model="data.formData" errorType="toast">
            <wd-cell-group border>
              <wd-input
                :label="t('person.oldPassword')"
                prop="oldPassword"
                show-password
                v-model="data.formData.oldPassword"
                :placeholder="t('person.oldPasswordPlaceholder')"
                :rules="[
                  {
                    required: true,
                    message: t('person.oldPasswordPlaceholder')
                  }
                ]"
              />

              <wd-input
                :label="t('person.newPassword')"
                prop="newPassword"
                show-password
                v-model="data.formData.newPassword"
                :placeholder="t('person.newPasswordPlaceholder')"
                :rules="[
                  {
                    required: true,
                    message: t('person.newPasswordPlaceholder')
                  }
                ]"
              />

              <wd-input
                :label="t('person.confirmPassword')"
                prop="confirmPassword"
                show-password
                v-model="data.formData.confirmPassword"
                :placeholder="t('person.confirmPasswordPlaceholder')"
                :rules="[
                  {
                    required: true,
                    message: t('person.confirmPasswordPlaceholder')
                  }
                ]"
              />
            </wd-cell-group>
          </wd-form>
          <wd-button
            class="chang-info"
            size="large"
            block
            @click="changePassword"
          >
            {{ t('submit') }}
          </wd-button>
        </view>
        <view v-show="current === 2">
          <wd-cell-group border>
            <!-- <wd-cell
              :title="t('person.systemLanguage')"
              :value="currentLangName"
              @click="showLanguageSelector"
              is-link
            /> -->
            <!-- #ifdef APP-PLUS -->
            <!-- <wd-cell :title="t('common.version')" :value="version" /> -->
            <!-- #endif -->
						<wd-cell
							v-for="item in languageRange"
							:key="item.value"
							:title="item.text"
							:is-link="true"
							@click="changeLanguage(item.value)"
							:value="currentLang === item.value ? '✔' : ''"
						/>
          </wd-cell-group>
        </view>
      </view>
      <div class="my-logout" v-show="current === 2">
        <wd-button
          class="chang-info"
          type="info"
          size="large"
          block
          :round="false"
          @click="handleLogout"
        >
          {{ t('login.logout') }}
        </wd-button>
      </div>
    </view>
  </view>
  <wd-toast selector="system" />
  <wd-message-box selector="system" />
  <tabbar :popVal="2"></tabbar>
</template>
<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { onReady, onLoad, onShow } from '@dcloudio/uni-app'
import { userApi } from '@/api'
import { getDicts } from '@/api/modules/system'
import { Login } from '@/api/interface/login'
import { Dict } from '@/api/interface/index'
import { useUserStore } from '@/store'
import tabbar from '@/components/tabbar/index.vue'
import { useToast, useMessage } from 'wot-design-uni'
const popupRef = ref(null)
const baseUrl = import.meta.env.VITE_APP_STATIC_BASE_URL + '/'
// 获取自定义的store
const store = useUserStore()
const toast = useToast('system')
const message = useMessage('system')
const { locale, t } = useI18n() // 先调用此方法，然后再使用
const userInfo = ref<Login.UserInfo>()
const current = ref<number>(0)
const items = computed(() => {
  return reactive([
    t('person.info'),
    t('person.changePassword'),
    t('person.systemSetting')
  ])
})
const languageRange = [
	{value:"zh-Hans",text:"简体中文"},
	{value:"id",text:"Bahasa Indonesia"},
]
const onClickItem = (currentIndex: number) => {
  current.value = currentIndex
}

// 语言相关配置
const currentLang = ref<string>(uni.getStorageSync('language') || 'zh-Hans')

// 当前语言显示名称
const currentLangName = computed(() => {
  return currentLang.value === 'zh-Hans' ? '简体中文' : 'Bahasa Indonesia'
})
const changeLanguage = (e: any) => {
	currentLang.value = e;
	handleLanguageChange(currentLang.value)
}
// 显示语言选择器
const showLanguageSelector = () => {
  uni.showActionSheet({
    itemList: ['简体中文', 'Bahasa Indonesia'],
		cancelText: t('common.cancel'),
    success: (res) => {
      console.log('选择的索引:', res.tapIndex)
      const selectedLang = res.tapIndex === 0 ? 'zh-Hans' : 'id'
      handleLanguageChange(selectedLang)
    },
    fail: (err) => {
      console.log('取消选择:', err)
    }
  })
}
const form1 = ref()
const data = reactive({
  formData: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
})
// #ifdef APP-PLUS
const version = ref('')
onLoad(() => {
  plus.runtime.getProperty(plus.runtime.appid, (info) => {
    version.value = info.version
  })
})
// #endif
onReady(() => {})
onShow(() => {
  userApi
    .getUserInfo()
    .then((res) => {
      userInfo.value = res.user
    })
    .catch((err) => {
      toast.error(err.msg)
    })

  console.log('local---', uni.getStorageSync('locale'))
})
const changePassword = () => {
  form1.value.validate().then(({ valid, errors }) => {
    if (data.formData.newPassword !== data.formData.confirmPassword) {
      toast.error(t('person.passwordNotSame'))
      return
    }
    if (valid) {
      userApi
        .updateUserPwd(data.formData.oldPassword, data.formData.newPassword)
        .then((obj) => {
          toast.success(obj.msg)
        })
        .catch((e) => {
          toast.error(e.msg)
        })
    }
  })
}

// 处理语言切换
const handleLanguageChange = (langCode: string) => {
  console.log('切换语言:', langCode)

  if (!langCode) {
    console.warn('无效的语言选择')
    return
  }

  currentLang.value = langCode
  locale.value = langCode // 立即切换语言
  uni.setStorageSync('language', langCode) // 持久化存储
  uni.setLocale(langCode) // uni-app 内部切换

  // 更新store中的语言状态
  store.setLanguage(langCode)

  // 显示切换成功提示
  setTimeout(() => {
    toast.success(t('person.changeLanguageSuccess') || '语言切换成功')
  }, 100)
}

const handleLogout = () => {
  message
    .confirm({
      msg: t('person.logoutTip'),
      confirmButtonText: t('confirm'),
      cancelButtonText: t('cancel')
    })
    .then(() => {
      store.logout()
      uni.reLaunch({
        url: '/pages/login/login'
      })
    })
    .catch(() => {})
}
const changePersonInfo = () => {
  uni.navigateTo({
    url: '/pages/system/changeUserInfo'
  })
}
</script>
<style lang="scss" scoped>
.container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: $bg-color-grey;
  .my-top {
    overflow: hidden;
    position: relative;
    background: url('../../static/image/bc2.png') no-repeat top center;
    background-size: 100%;
    height: 400rpx;
    .my-info {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      // margin-bottom: 20rpx;
      height: inherit;
      .avatar {
        margin-bottom: 20rpx;
        background: #ffffff;
        border-radius: 50%;
        display: block;
        width: 80px;
        height: 80px;
        overflow: hidden;
      }
      .name {
        line-height: 50rpx;
        font-size: 36rpx;
        font-weight: bold;
        text-align: center;
        color: #ffffff;
      }
      .error-wrap {
        .wd-icon {
          background: #ffffff;
          border-radius: 50%;
        }
      }
    }
  }
  .content {
    flex: 1;
    overflow: auto;
    padding-bottom: 50px;
    .my-menu {
      background-color: #ffffff;
      padding: 20rpx;
      margin: 20rpx;
      border-radius: 8px;
      .wd-tabs {
        :deep(.wd-tabs__nav-item-text) {
          font-size: 32rpx;
        }
      }
      .chang-info {
        margin-top: 60rpx;
      }
    }
    .my-logout {
      margin: 20rpx;
      .wd-button {
        background: #ffffff;
        color: #4d80f0;
      }
    }
  }
}
</style>
