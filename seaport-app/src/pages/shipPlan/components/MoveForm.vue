<template>
  <view class="move-page">
    <wd-form ref="form" :model="modelForm" :rules="rules">
      <wd-cell-group border>
        <wd-picker
          :columns="berthList"
          prop="hbId"
          v-model="modelForm.hbId"
          label-width="100px"
          clearable
          :label="t('port.berth.pier')"
          :placeholder="t('port.berth.pleaseSelect')"
        />
        <view v-if="modelForm.hbId">
          <wd-calendar
            v-model="modelForm.dockingTime"
            type="datetime"
            :label="t('port.time.dockingTime')"
            prop="dockingTime"
            label-width="100px"
            hide-second
            :rules="[
              {
                required: true,
                message: t('port.selectTypeTime', {
                  type: t('port.time.dockingTime')
                })
              }
            ]"
          />
          <wd-calendar
            v-model="modelForm.operationTime"
            type="datetime"
            :label="t('port.time.operationTime')"
            prop="operationTime"
            label-width="100px"
            hide-second
          />
        </view>
      </wd-cell-group>
    </wd-form>
    <view class="action-buttons">
      <wd-button style="width: 80%" type="primary" @tap="handleSubmit">{{
        t('confirm')
      }}</wd-button>
    </view>
    <wd-toast />
  </view>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'
import { moveHarborPlan } from '@/api/modules/plan'
import { formatTimestampToDateTime } from '@/utils/time'
import { useToast } from 'wot-design-uni'
import { useI18n } from 'vue-i18n'

const toast = useToast()
const form = ref()
const { t } = useI18n()

const props = defineProps({
  modelForm: {
    type: Object,
    required: true
  },
  berthList: {
    type: Array,
    default: () => []
  },
  handleFinished: {
    type: Function,
    default: () => {}
  }
})

const emit = defineEmits(['update:modelForm'])

// 监听 modelForm 的变化，重置表单状态
watch(
  () => props.modelForm,
  (newVal) => {
    if (form.value) {
      form.value.reset() // 重置表单验证状态
    }
  },
  {
    deep: true,
    immediate: true
  }
)

const handleMove = async () => {
  const { moveBerthTime, dockingTime, operationTime } = props.modelForm
  const data = {
    ...props.modelForm,
    hbName: props.berthList.find((i) => i.value === props.modelForm.hbId)
      ?.label,
    moveBerthTime: formatTimestampToDateTime(moveBerthTime),
    dockingTime: formatTimestampToDateTime(dockingTime),
    operationTime: formatTimestampToDateTime(operationTime)
  }
  try {
    const res = await moveHarborPlan(data)
    if (res.code === 200) {
      toast.success(t('success'))
      props.handleFinished()
    } else {
      toast.error(res.msg)
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('修改失败', error)
  }
}

const handleSubmit = () => {
  form.value
    .validate()
    .then(({ valid }) => {
      if (valid) {
        handleMove()
      }
    })
    .catch((error) => {
      console.log(error, 'error')
    })
}
</script>

<style lang="scss">
.move-page {
  .action-buttons {
    display: flex;
    justify-content: center;
    padding: 10px;
    border-top: 1px solid #eee;
  }
}
</style>
