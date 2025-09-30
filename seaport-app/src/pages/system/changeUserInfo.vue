<template>
  <view class="change-info">
    <wd-form ref="form1" :model="data.formData" errorType="toast">
      <wd-cell-group border>
        <wd-input
          :label="t('person.nickName')"
          prop="nickName"
          v-model="data.formData.nickName"
          disabled
          :rules="[{ required: true, message: t('person.inputNickName') }]"
        />
        <wd-input
          :label="t('person.phone')"
          prop="phonenumber"
          v-model="data.formData.phonenumber"
          :rules="[{ required: true, message: t('person.inputPhone') }]"
        />
        <wd-input
          :label="t('person.email')"
          prop="email"
          v-model="data.formData.email"
          :rules="[{ required: true, message: t('person.inputEmail') }]"
        />
        <wd-select-picker
          :label="t('person.sex')"
          prop="sex"
          type="radio"
          v-model="data.formData.sex"
          :columns="sexList"
        ></wd-select-picker>
      </wd-cell-group>
    </wd-form>
    <wd-button
      @click="submit"
      size="large"
      block
      :disabled="loading"
      class="submit-btn"
      >{{ t('submit') }}</wd-button
    >
    <wd-toast selector="change-user-info" />
  </view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { onReady } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { userApi } from '@/api'
import { DefaultDict } from '@/api/interface/index'
import { getDicts } from '@/api/modules/system'
import { useToast } from 'wot-design-uni'

const { locale, t } = useI18n()
const toast = useToast('change-user-info')
const sexList = ref<DefaultDict[]>([])
const loading = ref(false)
const data = reactive({
  formData: {
    admin: true,
    avatar: '',
    delFlag: '',
    deptId: 0,
    email: '',
    nickName: '',
    phonenumber: '',
    remark: '',
    sex: '',
    status: '',
    userId: 0,
    userName: ''
  }
})
const form1 = ref()
onReady(() => {
  userApi
    .getUserInfo()
    .then((res) => {
      data.formData = res.user
    })
    .catch((err) => {
      toast.error(err.msg)
    })
  getDicts('sys_user_sex').then((res) => {
    sexList.value = res.data.map((item) => {
      let label = ''
      switch (item.dictValue) {
        case '0':
          label = t('person.male')
          break
        case '1':
          label = t('person.female')
          break
        case '2':
          label = t('person.unknown')
          break
        default:
          label = t('person.unknown')
          break
      }
      return {
        label,
        value: item.dictValue
      }
    })
  })
})
const submit = () => {
  form1.value.validate().then(({ valid, errors }) => {
    if (valid) {
      userApi
        .updateUserProfile(data.formData)
        .then(() => {
          toast.success(t('person.changeInfoSuccess'))
        })
        .catch((err: any) => {
          toast.error(err.msg)
        })
    }
  })
}
</script>

<style lang="less">
.change-info {
  padding: 20rpx;
  height: 100vh;
  background: #f6f7fb;
  .submit-btn {
    margin-top: 50rpx;
  }
}
</style>
