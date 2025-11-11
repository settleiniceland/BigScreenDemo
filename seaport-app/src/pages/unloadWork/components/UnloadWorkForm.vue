<script setup>
import {ref,reactive,watch} from 'vue'
import { useToast } from 'wot-design-uni'
import { UnloadWorkType, ClassOptions } from '@/contans/index'
import {
  startWorkApp,
  addHarborUnload,
  updateHarborUnload
} from '@/api/modules/plan'
import { formatTimestampToDateTime } from '@/utils/time'
import dayjs from 'dayjs'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const currentIndex = ref(0)
const swiperDisabled = ref(false)
const materials = ref([])
const selectMaterial = ref({})
const props = defineProps({
  modelForm: {
    type: Object,
    default: () => ({})
  },
	planInfo: {
		type: Object,
		default: () => ({})
	},
  type: {
    type: String,
    default: UnloadWorkType.Add
  },
  getUnloadWorkList: {
    type: Function,
    default: () => {}
  },
})
const originOpertionTime = ref()
const data = reactive({
	classTime:null,
	classes:null,
	operationTime:null,
	startTime:null,
	endTime:null,
	leader:null,
	remark01:null,
})

// emits
const emit = defineEmits(['update:modelForm'])
watch(
  () => props.modelForm,
  (val) => {
    if (val){
			Object.keys(data).forEach(key => delete data[key])
			Object.assign(data, val);
			data.remark01='1'
			originOpertionTime.value = data.operationTime
		} 
  },
  { immediate: true, deep: true }
)
watch(
  () => props.planInfo,
  (val) => {
    if (val && Object.keys(val).length > 0) {
			materials.value = [];
			materials.value.push({
				loadSequence: 1,
				usageUnit: val.usageUnit,
				materialName: val.materialName,
				dataUnit: val.remark03,
				loadType: val.remark01
			})
			if(val.params.subMaterial!==null && val.params.subMaterial!==undefined){
				const sortedList = [...val.params.subMaterial].sort((a, b) => a.loadSequence - b.loadSequence)
				for(const item of sortedList){
					materials.value.push({
						loadSequence: item.loadSequence,
						usageUnit: item.usageUnit,
						materialName: item.materialName,
						dataUnit: item.remark02,
						loadType: val.remark01
					})
				}
			}
		}
  },
  { immediate: true, deep: true }
)
const toast = useToast()
const form = ref()
// 提交表单
const handleSubmit = () => {
  form.value.validate().then(({ valid }) => {
		if (valid) handleUnloadWork()
	}).catch((err) => {
		console.error(err, '校验失败')
	})
}

// 作业处理函数
const handleUnloadWork = async () => {
  data.classTime = dayjs(data.classTime).format('YYYY-MM-DD')
  if (props.type == UnloadWorkType.Start) {
		const regex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
		if(!regex.test(data.operationTime)){
			data.operationTime = formatTimestampToDateTime(data.operationTime)
		}
    try {
      const res = await startWorkApp(data)
      if (res.code) {
        toast.success({
          msg: '开始作业成功',
        })
				setTimeout(() => {
				  uni.navigateTo({
				    url: `/pages/unloadWork/index?planId=${data.planId}`
				  })
				}, 300)
      } else {
        toast.error(res.msg)
      }
    } catch (err) {
      toast.error(err.msg)
      console.error('接口报错 ', err)
    }
    return
  }

  // 新增或编辑
  data.startTime = formatTimestampToDateTime(data.startTime)
	if(props.type == UnloadWorkType.Edit){
		data.endTime = formatTimestampToDateTime(data.endTime)
	}
  const typeLabel =
    props.type == UnloadWorkType.Add
      ? t('contans.operations.add')
      : t('contans.operations.edit')
  try {
    const res =
      props.type == UnloadWorkType.Add
        ? await addHarborUnload(data)
        : await updateHarborUnload(data)
    if (res.code) {
      toast.success(t('unloadWorkLog.operateSuccess', { type: typeLabel }))
      props.getUnloadWorkList()
    } else {
      toast.error(res.msg)
    }
  } catch (err) {
    toast.error(
      t('unloadWorkLog.operateFail') + ', ' + err.msg
    )
		console.error('接口报错 ', err)
		props.getUnloadWorkList()
  }
}
const onSwiperChange = (e) => {
	selectMaterial.value = materials.value[e.detail.current]
	data.remark01 = selectMaterial.value.loadSequence
}
</script>

<template>
  <view class="workForm">
		<view class="material-swiper-wrapper" v-if="data.duId===null || data.duId===undefined || data.duId===''">
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
							<wd-cell :title="t('others.obj2')" :value="item.dataUnit" />
							<wd-cell :title="t('others.obj6')" :value="item.loadSequence+'->'+item.loadType" />
						</wd-cell-group>
					</view>
				</swiper-item>
			</swiper>
		</view>
		<wd-divider color="#4D80F0">✍️👇👇👇装卸单</wd-divider>
    <wd-form ref="form" :model="data">
      <wd-cell-group border>
				<wd-input
					v-model="data.remark01"
					:label="t('others.obj6')"
					prop="remark01"
					label-width="80px"
					:placeholder="t('others.obj8')"
					disabled
					:rules="[
					  { required: true, message: t('others.obj8') }
					]"
				/>
        <wd-calendar
          v-model="data.classTime"
          type="date"
          :label="t('port.unloadWork.shiftTime')"
          prop="classTime"
          label-width="80px"
          :placeholder="t('port.unloadWork.selectShiftTime')"
          clearable
          :rules="[
            { required: true, message: t('port.unloadWork.inputShiftTime') }
          ]"
        />
        <wd-cell
          :title="t('port.unloadWork.shiftType')"
          title-width="80px"
          prop="classes"
          :rules="[{ required: true, message: t('task.classesPlaceholder') }]"
        >
          <wd-radio-group
            v-model="data.classes"
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
        <wd-calendar
          v-if="type == UnloadWorkType.Start"
          v-model="data.operationTime"
          type="datetime"
          :label="t('port.workLog.startTime')"
          prop="operationTime"
          hide-second
          label-width="80px"
          :placeholder="t('port.workLog.selectStartTime')"
          clearable
          :rules="[
            { required: true, message: t('port.workLog.selectStartTime') }
          ]"
        />
        <wd-calendar
          v-else
          v-model="data.startTime"
          hide-second
          type="datetime"
          :label="t('port.workLog.startTime')"
          prop="startTime"
          label-width="80px"
          :placeholder="t('port.workLog.selectStartTime')"
          clearable
          :rules="[
            { required: true, message: t('port.workLog.selectStartTime') }
          ]"
        />
				<wd-calendar
				  v-if="type==UnloadWorkType.Edit && 
						data.endTime!==undefined &&
						data.endTime!==null &&
						data.endTime!==''"
				  v-model="data.endTime"
				  type="datetime"
				  :label="t('work.stopTime')"
				  prop="endTime"
				  hide-second
				  label-width="80px"
				  :placeholder="t('port.workLog.selectEndTime')"
				  clearable
				  :rules="[
				    { required: true, message: t('port.workLog.selectEndTime') }
				  ]"
				/>
        <wd-input
          v-model="data.leader"
          :label="t('port.unloadWork.responsible')"
          prop="leader"
          label-width="80px"
          :placeholder="t('port.unloadWork.inputResponsible')"
          clearable
          :rules="[
            { required: true, message: t('port.unloadWork.inputResponsible') }
          ]"
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