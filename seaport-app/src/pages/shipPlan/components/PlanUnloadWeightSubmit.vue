<template>
  <view class="workForm">
		<view class="material-swiper-wrapper">
			<swiper 
				class="material-swiper" 
				:current="currentIndex"
				:circular="true"
				:indicator-dots="true"
				@change="onSwiperChange">
				<swiper-item v-for="(item, index) in materials" :key="index">
					<view class="material-card">
						<wd-cell-group>
							<wd-cell :title="t('others.obj1')" :value="item.materialName" />
							<wd-cell :title="t('others.obj5')" :value="item.usageUnit" />
							<wd-cell :title="t('others.obj2')" :value="item.packageNum" />
							<wd-cell :title="t('others.obj6')" :value="item.loadSequence" />
						</wd-cell-group>
					</view>
				</swiper-item>
			</swiper>
		</view>
		<wd-divider color="#4D80F0">✍️👇👇👇</wd-divider>
    <wd-form ref="form" :model="data">
			<wd-cell-group border>
				<wd-input
					v-model="data.planId"
					prop="planId"
					v-show="false"
					:rules="[
					  { required: true, message: t('---') }
					]"
				/>
			</wd-cell-group>
			<wd-cell-group border>
				<wd-input
					v-model="data.loadSequence"
					:label="t('others.obj6')"
					prop="loadSequence"
					label-width="80px"
					disabled
					:rules="[{ required: true, message: t('others.obj6') }]"
				/>
			</wd-cell-group>
      <wd-cell-group border>
				<wd-input
					v-model="data.unloadWeight"
					:label="t('others.obj4')"
					prop="unloadWeight"
					label-width="80px"
					:placeholder="t('port.planManagement.inputCompletedWork')"
					type="digit"
					inputmode="decimal"
				/>
      </wd-cell-group>
    </wd-form>
    <view class="action-buttons">
      <wd-button class="btn" type="primary" @tap="handleSubmit">{{
        t('submit')
      }}</wd-button>
    </view>
    <wd-toast />
  </view>
</template>
<script setup>
import {ref,reactive,watch} from 'vue'
import { useToast } from 'wot-design-uni'
import {submitUnloadWork} from '@/api/modules/plan'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const currentIndex = ref(0)
const materials = ref([])
const selectMaterial = ref({})
const form = ref()
const toast = useToast()
const props = defineProps({
	planInfo: {
		type: Object,
		default: () => ({})
	},
	closeSubmitUnloadWork: {
		type: Function,
		default: () => {}
	}
})
const data = reactive({
	planId: undefined,
	unloadWeight: undefined,
	loadSequence: 1,
})
watch(
  () => props.planInfo,
  (val) => {
    if (val && Object.keys(val).length > 0) {
			data.planId = val.id;
			materials.value = [];
			materials.value.push({
				loadSequence: 1,
				usageUnit: val.usageUnit,
				materialName: val.materialName,
				packageNum: val.packageNum===2?"按件/Barang ":"按吨/Ton",
				unloadWeight: val.unloadWeight
			})
			if(val.params.subMaterial!==null && val.params.subMaterial!==undefined){
				const sortedList = [...val.params.subMaterial].sort((a, b) => a.loadSequence - b.loadSequence)
				for(const item of sortedList){
					materials.value.push({
						loadSequence: item.loadSequence,
						usageUnit: item.usageUnit,
						materialName: item.materialName,
						packageNum: item.packageNum===2?"按件/Barang ":"按吨/Ton",
						unloadWeight: item.unloadWeight
					})
				}
			}
		}
  },
  { immediate: true, deep: true }
)
// 提交表单
const handleSubmit = async () => {
  try {
    const { valid } = await form.value.validate()
    if (valid) {
      if (data.unloadWeight !== undefined) {
        data.unloadWeight = Number(data.unloadWeight)
      }
      const res = await submitUnloadWork(data)
      if (res.code === 200) {
        toast.success({ msg: t('others.obj9') })
				props.closeSubmitUnloadWork()
      }
    }
  } catch (error) {}
}
const onSwiperChange = (e) => {
	selectMaterial.value = materials.value[e.detail.current]
	data.loadSequence = selectMaterial.value.loadSequence
	data.unloadWeight = selectMaterial.value.unloadWeight
}
const onInputNumber = (val) => {
  data.unloadWeight = val.replace(/[^\d.]/g, '').replace(/^(\d*\.?\d*).*$/, '$1')
}
</script>
<style lang="scss">
.workForm {
  .action-buttons {
    display: flex;
    justify-content: center;
    padding: 10px;
    border-top: 1px solid #eee;

    .btn {
      margin-right: 10rpx;
    }
  }
}
.material-swiper-wrapper {
  width: 100%;
  height: 400rpx;
  background-color: #fff;
}
.swiper-mask-mode {
  /* 加遮罩逻辑，比如透明背景防触摸 */
  pointer-events: auto; /* 保证遮挡 */
  background-color: rgba(0, 0, 0, 0); /* 透明或半透明遮罩 */
  z-index: 10;
}
.material-swiper {
  height: 100%;
}
.material-card {
  padding: 20rpx;
  margin: 20rpx;
  background-color: #f8f8f8;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
}
.button-row {
  display: flex;
  justify-content: center;
  gap: 24rpx;
  margin: 20rpx 0;
}
</style>