<template>
  <view>
    <wd-form ref="form" :model="modelForm">
      <wd-cell-group border>
        <wd-calendar
          v-model="modelForm.classTime"
          type="date"
          :label="t('port.unloadWork.shiftTime')"
          prop="classTime"
          :label-width="labelWidth"
          :placeholder="t('port.unloadWork.selectShiftTime')"
          clearable
        />
        <wd-cell
          :title="t('port.unloadWork.shiftType')"
          :title-width="labelWidth"
          prop="classes"
        >
          <wd-radio-group
            v-model="modelForm.classes"
            shape="button"
            class="flex"
          >
            <wd-radio
              v-for="classItem in formattedClassOptions"
              :key="classItem.value"
              :value="classItem.value"
            >
              {{ classItem.label }}
            </wd-radio>
          </wd-radio-group>
        </wd-cell>
        <wd-picker
          :columns="formattedUnloadWorkTypeOptions"
          prop="workType"
          :label-width="labelWidth"
          clearable
          :label="t('port.unloadWork.workStatus')"
          :placeholder="t('port.unloadWork.selectWorkStatus')"
          v-model="modelForm.workType"
        />
      </wd-cell-group>
    </wd-form>
  </view>
</template>

<script setup>
import { UnloadWorkTypeOptions, ClassOptions } from '@/contans/index'
import { formatDictItems } from '@/utils/dict'
import { useI18n } from 'vue-i18n'
import { computed } from 'vue'

const { t } = useI18n()

const { modelForm } = defineProps({
  // 当前的值
  modelForm: {
    type: Object,
    default: () => ({})
  }
})
const labelWidth = '80px'

// 使用统一的字典格式化函数处理班次选项
const formattedClassOptions = computed(() => formatDictItems(ClassOptions))

// 使用统一的字典格式化函数处理卸货工作类型选项
const formattedUnloadWorkTypeOptions = computed(() =>
  formatDictItems(UnloadWorkTypeOptions)
)
</script>

<style lang="scss"></style>
