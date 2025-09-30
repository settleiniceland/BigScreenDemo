<template>
  <wd-form ref="formRef" :model="localModelForm" :rules="rules">
    <wd-cell-group border>
      <wd-calendar
        :disabled="isEdit"
        v-model="localModelForm.time"
        type="datetime"
        hide-second
        :label="t('port.typeTime', { type: t(type) })"
        prop="time"
        label-width="100px"
        :placeholder="t('port.selectTypeTime', { type: t(type) })"
        clearable
      />
			<wd-cell
			  :title="t('others.obj14')"
			  title-width="80px"
			  prop="remark"
			  :rules="[{ required: true, message: t('others.obj14') }]"
				v-if="type === WorkStatusType.Pause"
			>
			  <wd-radio-group
			    v-model="localModelForm.remark"
			    shape="button"
			    class="flex"
			  >
			    <wd-radio
			      v-for="classItem in ClassOptions"
			      :key="classItem.value"
			      :value="classItem.value"
			    >
			      {{ t(classItem.label) }}
			    </wd-radio>
			  </wd-radio-group>
			</wd-cell>
      <wd-textarea
        v-if="type === WorkStatusType.Pause"
        label-width="100px"
        :label="t('port.berth.pauseReason')"
        clearable
        v-model="localModelForm.pauseReason"
        :placeholder="t('unloadWorkLog.inputPauseReason')"
        auto-height
        prop="pauseReason"
      />
      <view class="" v-if="type === WorkStatusType.Over">
        <view class="flex">
          <wd-input
            v-model="localModelForm[columnLabel.value]"
            type="number"
            :label="columnLabel.label"
            :prop="columnLabel.value"
            label-width="100px"
            :placeholder="`请输入${columnLabel.label}`"
            clearable
          />
          <view class="flex justify-center items-center">
            <wd-radio-group
              v-model="unLoadType"
              shape="button"
              @change="change"
              class="flex"
              :disabled="radioDisabled"
            >
              <wd-radio
                v-for="item in radioOptions"
                :key="item.value"
                :value="item.value"
                >{{ item.label }}</wd-radio
              >
            </wd-radio-group>
          </view>
        </view>
        <wd-input
          v-model="localModelForm.workEquipment"
          :label="t('port.workEquipment')"
          prop="workEquipment"
          label-width="100px"
          :placeholder="t('port.inputWorkEquipment')"
          clearable
        />
        <wd-cell
          :title="t('port.lastShift')"
          title-width="110px"
          prop="isOver"
          v-if="!isEdit&&dataStatus==='4'"
        >
          <wd-radio-group
            v-model="localModelForm.isOver"
            shape="button"
            class="flex"
          >
            <wd-radio :value="'0'">{{ t('port.no') }}</wd-radio>
            <wd-radio :value="'1'">{{ t('port.yes') }}</wd-radio>
          </wd-radio-group>
        </wd-cell>
        <wd-textarea
          label-width="100px"
          :label="t('task.remark')"
          clearable
          v-model="localModelForm.remark"
          :placeholder="t('task.inputError', { msg: t('task.remark') })"
          auto-height
          prop="remark"
        />
      </view>
    </wd-cell-group>
    <view class="footer">
      <wd-button type="primary" size="large" @tap="submitForm" block>{{
        t('submit')
      }}</wd-button>
    </view>
  </wd-form>
</template>

<script setup>
import { defineProps, ref, reactive, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import dayjs from 'dayjs'
import { WorkStatusType } from '@/contans/index'
const { t } = useI18n()
const unLoadType = ref()
const formRef = ref(null)
const ClassOptions = ref([
	{label:"others.obj15",value:"1"},
	{label:"others.obj16",value:"2"}
])
const {modelForm,type,handleWorkStatus,isEdit,startTime,packageNum} =
  defineProps({
    modelForm: {
      type: Object,
      default: () => ({})
    },
    type: {
      type: String,
      default: ''
    },
    handleWorkStatus: {
      type: Function,
      default: () => {}
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    startTime: {
      type: [String, Number],
      default: null
    },
    packageNum: {
      type: String,
      default: '0'
    },
		dataStatus: {
			type: String,
			default: '4'
		}
  })
// 创建本地响应式数据，避免直接修改 props
const localModelForm = ref({ ...modelForm })
const openOperation = ()=>{
	localModelForm.value = { ...modelForm }
	if(localModelForm.value.workEquipment!==null&&//判断是否是修改
		localModelForm.value.workEquipment!==''&&
		localModelForm.value.workEquipment!==undefined){
			if(localModelForm.value.totalUnloadWeight!==null&&
				localModelForm.value.totalUnloadWeight!==''&&
				localModelForm.value.totalUnloadWeight!==undefined){
				unLoadType.value = 1;
			}else if(localModelForm.value.unloadNum!==null&&
				localModelForm.value.unloadNum!==''&&
				localModelForm.value.unloadNum!==undefined){
				unLoadType.value = 2
			}
		}
}
const columnLabel = computed(() => {
  if (unLoadType.value === 2) {
    return {
      label: t('port.workLog.shiftUnloadPieces'),
      value: 'unloadNum',
      placeholder: t('port.workLog.inputShiftUnloadPieces')
    }
  }else{
		return {
			label: t('port.workLog.shiftUnloadTonnage'),
			value: 'totalUnloadWeight',
			placeholder: t('port.workLog.inputShiftUnloadTonnage')
		}
	}
})

const radioOptions = computed(() => {
  if (packageNum === 1) {
    return [{ label: t('port.workLog.tons'), value: 1 }]
  }
  if (packageNum === 2) {
    return [
      { label: t('port.workLog.pieces'), value: 2 }
    ]
  }
  return [
    { label: t('port.workLog.tons'), value: 1 },
    { label: t('port.workLog.pieces'), value: 2 }
  ]
})
const radioDisabled = computed(() => packageNum === 1 || packageNum === 2)
// 自定义校验函数：检查结束时间是否大于开始时间
const validateEndTime = (rule, value) => {
  if (!value) {
    return true
  }

  // 暂停作业不需要校验结束时间
  if (type === WorkStatusType.Pause) {
    return true
  }

  if (!startTime) {
    return true
  }

  const endTime = dayjs(value)
  const startTimeDate = dayjs(startTime)

  if (endTime.isBefore(startTimeDate)) {
    return t('port.time.endTimeCannotBeEarlierThanStartTime')
  }

  return true
}

const rules = reactive({
  time: [
    {
      required: true,
      message: t('port.selectTypeTime', {
        type: t(type)
      })
    },
    {
      validator: validateEndTime,
      trigger: 'change'
    }
  ],
  pauseReason: [
    {
      required: true,
      message: t('unloadWorkLog.inputPauseReason')
    }
  ],
	remark: [
		{
		  required: true,
		  message: t('others.obj13')
		}
	],
  totalUnloadWeight: [
    {
      required: true,
      message: t('port.workLog.inputShiftUnloadTonnage')
    }
  ],
  unloadNum: [
    {
      required: true,
      message: t('port.workLog.inputShiftUnloadPieces')
    }
  ],
  workEquipment: [
    {
      required: true,
      message: t('port.inputWorkEquipment')
    }
  ],
  isOver: [
    {
      required: true,
      message: t('port.lastShift')
    }
  ]
})

const change = () => {
	localModelForm.value.totalUnloadWeight = null
	localModelForm.value.unloadNum = null
}

const submitForm = () => {
  formRef.value
    .validate()
    .then(({ valid }) => {
      if (valid) {
        // 将当前表单数据传递给父组件
        handleWorkStatus(localModelForm.value)
      }
    })
    .catch((error) => {
      console.error(error, 'error')
    })
}
defineExpose({openOperation})
</script>
<style>
.footer {
  padding: 12px;
}
</style>
