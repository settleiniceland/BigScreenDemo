<template>
  <view>
    <wd-form ref="form" :model="modelForm">
      <wd-cell-group border>
        <wd-cell
          :title="t('port.planManagement.pierType')"
          title-width="100px"
          prop="pierType"
        >
          <wd-radio-group
            v-model="modelForm.pierType"
            shape="button"
            class="flex"
          >
            <wd-radio v-for="item in PierTypeOptions" :value="item.value">{{
              t(item.label)
            }}</wd-radio>
          </wd-radio-group>
        </wd-cell>
        <wd-input
          :label="t('port.planManagement.shipName')"
          label-width="100px"
          prop="shipName"
          clearable
          v-model="modelForm.shipName"
          :placeholder="t('port.planManagement.inputShipName')"
        />
        <wd-input
          :label="t('port.planManagement.mineNumber')"
          label-width="100px"
          prop="mineNumber"
          clearable
          v-model="modelForm.mineNumber"
          :placeholder="t('port.planManagement.mineNumber')"
        />
        <wd-picker
          :columns="statusOptions"
          prop="status"
          label-width="100px"
          clearable
          :label="t('port.planManagement.status')"
          :placeholder="t('port.planManagement.selectStatus')"
          v-model="modelForm.status"
        />
        <wd-picker
          :columns="state.materialList"
          prop="materialName"
          clearable
          label-width="100px"
          :label="t('port.planManagement.materialName')"
          :placeholder="t('port.workLog.materialName')"
          v-model="modelForm.materialName"
        />
        <wd-picker
          :columns="state.pireList"
          prop="pierId"
          label-width="100px"
          clearable
          :label="t('port.planManagement.dock')"
          :placeholder="t('port.planManagement.selectDock')"
          v-model="modelForm.pierId"
        />
        <wd-calendar
          type="datetimerange"
          hide-second
          prop="endTime"
          label-width="100px"
          :label="t('port.time.endTime')"
          clearable
          v-model="modelForm.endTimes"
          :placeholder="t('port.workLog.selectEndTime')"
        />
        <wd-picker
          :columns="
            UnloadStatusOptions.map((item) => ({
              ...item,
              label: t(item.label)
            }))
          "
          prop="unloadStatus"
          clearable
          label-width="100px"
          :label="t('port.planManagement.unloadingBillStatus')"
          :placeholder="t('port.planManagement.selectUnloadingBillStatus')"
          v-model="modelForm.unloadStatus"
        />
      </wd-cell-group>
    </wd-form>
  </view>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { onShow } from '@dcloudio/uni-app'
import { defineProps, reactive, watchEffect } from 'vue'
import { getMaterialStatusList, getHarborPierList } from '@/api/modules/plan'
import { UnloadStatusOptions, PierTypeOptions } from '@/contans/index'

const props = defineProps({
  modelForm: {
    type: Object,
    default: () => ({
      pierType: undefined,
      shipName: '',
      mineNumber: '',
      status: undefined,
      materialName: '',
      pierId: undefined,
      endTimes: null, // 保证类型正确
      unloadStatus: undefined
    })
  },
  statusOptions: {
    type: Array,
    default: () => []
  }
})
const { modelForm, statusOptions } = props
const state = reactive({
  materialList: [],
  pireList: []
})
const { t } = useI18n()

// 保证 modelForm.endTimes 不为 undefined
watchEffect(() => {
  if (modelForm.endTimes === undefined) {
    modelForm.endTimes = null
  }
})

const getMaterialList = async () => {
  try {
    const { rows } = await getMaterialStatusList()
    state.materialList = rows?.map((item) => {
      return {
        label: item.materialName,
        value: item.materialName
      }
    })
  } catch (error) {
    console.error('获取数据失败', error)
  }
}
const getPierList = async () => {
  try {
    const { data } = await getHarborPierList()
    state.pireList = data?.map((item) => {
      return {
        label: item.pierName,
        value: item.pierId
      }
    })
  } catch (error) {
    console.error('获取数据失败', error)
  }
}
onShow(() => {
  getMaterialList()
  getPierList()
})
</script>

<style lang="scss"></style>
