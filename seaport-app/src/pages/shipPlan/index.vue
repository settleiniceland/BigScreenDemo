<template>
  <view class="ship-page">
    <view class="top flex justify-between">
      <view class="plan-btn top-btn">
        <wd-select-picker
          :label="t('port.berth.pier')"
          :placeholder="t('port.berth.pleaseSelect')"
          label-width="60px"
          type="checkbox"
          clearable
          required
          filterable
          v-model="form.hbIds"
          :columns="state.berthList"
          @confirm="handleHbIdsChange"
          @clear="handleHbIdsClear"
          :display-format="displayFormat"
        />
      </view>
      <view class="filter-btn top-btn" @tap="handleFilterChange">
        <wd-icon
          name="filter"
          :size="fontSize"
          class="mg-r10"
          :color="hasFilter ? '#4d80f0' : '#666666'"
        />
        <wd-text
          :text="t('port.planManagement.filter')"
          :size="fontSize"
          :color="hasFilter ? '#4d80f0' : '#666666'"
        />
      </view>
      <view class="resert-btn top-btn" @tap="handleReset">
        <wd-icon
          name="refresh"
          :size="fontSize"
          color="#666666"
          class="mg-r10"
        />
        <wd-text
          :text="t('port.planManagement.clear')"
          :size="fontSize"
          color="#666666"
        />
      </view>
    </view>
    <!-- 列表区域 -->
    <scroll-view scroll-y class="content" @scrolltolower="scrolltolower">
      <PlanCard
        v-for="(item, index) in state.planList"
        :key="item.id"
        :index="index"
        :statusOptions="state.statusOptions"
        :data="item"
        :handleEdit="handleEdit"
        :handleMove="handleMove"
        :handlePlanDocking="handlePlanDocking"
				:handleConfirm="handleConfirm"
      />
      <wd-status-tip
        v-if="
          (state.planList.length === 0 && state.loadStatus === 'finished') ||
          state.loadStatus === 'error'
        "
        image="content"
        :tip="t('port.berth.noContent')"
      />
      <load-more v-else :status="state.loadStatus"></load-more>
    </scroll-view>
    <!-- 筛选面板 -->
    <wd-action-sheet
      v-model="state.filterActionShow"
      :title="t('port.planManagement.filter')"
    >
      <FilterForm :modelForm="form" :statusOptions="state.statusOptions" />
      <view class="action-buttons">
        <wd-button class="mg-r10" type="info" @tap="handleCancel">{{
          t('cancel')
        }}</wd-button>
        <wd-button type="primary" @tap="handleConfirm">{{
          t('confirm')
        }}</wd-button>
      </view>
    </wd-action-sheet>
    <!-- 编辑面板 -->
    <wd-action-sheet
      v-model="state.editPlanShow"
      :title="t('port.planManagement.editPlan')"
    >
      <EditPlanForm
        :modelForm="editForm"
        :berthList="state.berthList"
        :handleCancel="
          () => {
            state.editPlanShow = false
          }
        "
        :handleConfirm="handleEditConfirm"
      />
    </wd-action-sheet>
		<wd-action-sheet
			v-model="state.WindowPeriosLogSubmitShow"
			:title="t('others.obj23')">
			<WindowPeriodLogsSubmit
				:planData="planData"
				:closeWindowPerios="closeWindowPerios"/>
		</wd-action-sheet>
    <tabbar :popVal="1"></tabbar>
    <pop :updateInfo="store.updateInfo" v-if="store.isUpdate" />
    <wd-toast />
  </view>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
import { onShow } from '@dcloudio/uni-app'
import tabbar from '@/components/tabbar/index.vue'
import { useUserStore } from '@/store'
import pop from '@/uni_modules/uni-upgrade-center-app/pages/upgrade-popup.vue'
import {
  getHarborBerthList,
  getPlanList,
  updatePlanApp,
  toPlanDocking,
	getHarborPlan
} from '@/api/modules/plan'
import FilterForm from './components/FilterForm.vue'
import EditPlanForm from './components/EditPlanForm.vue'
import PlanCard from './components/PlanCard.vue'
import loadMore from '@/components/load-more.vue'
import WindowPeriodLogsSubmit from './components/WindowPeriodLogsSubmit.vue'
import {
  formatTimestampToDateTime,
  dateTimeToFormatTimestamp
} from '@/utils/time'
import { diclist } from '@/api/modules/system'
import { TimeFields } from '@/contans/index'
import {getLackTypeList} from '@/api/modules/berth'
const BERTH_STORAGE_KEY = 'SELECTED_BERTH_IDS'
const { t } = useI18n()
const toast = useToast()
const store = useUserStore()
const fontSize = ref('26rpx')
const state = reactive({
  filterActionShow: false, // 筛选面板状态
  filterSidebarActive: 0, // 筛选面板激活项
  editPlanShow: false,
	WindowPeriosLogSubmitShow: false,
  moveShow: false,
  loadStatus: 'loading', // 加载状态 loading/finished
  loading: false,
  total: 0, // 总条数
  berthList: [],
  statusOptions: [],
  planList: [] // 计划列表数据
})
const planData = reactive({})
const displayFormat = (items, columns) => {
  let showValue = ''
  // 使用 slice 限制只取前5个
  const values = items?.length <= 5 ? items : items.slice(0, 5)
  columns.forEach((column) => {
    values.forEach((item, index) => {
      if (column.value === item) {
        showValue
          ? (showValue += `,${column.label}`)
          : (showValue += `${column.label}`)
      }
    })
  })
  return showValue
}

// 分页相关的基础字段
const baseForm = {
  pageSize: 20,
  pageNum: 1,
  isArchived: 0
}

// 可重置的显示字段
const resetableFields = {
  endTimes: [], // 结束时间范围
  status: '', // 状态
  planNo: '', // 计划编号
  shipName: '' // 船名
}

const form = ref({
  ...baseForm,
  ...resetableFields
})

const editForm = ref({
  hbName: '',
  remainingWeight: '',
  arrivalTime: null,
  dockingTime: null,
  operationTime: null,
  endTime: null,
  outBerthTime: null,
  leaveTime: null
})

const moveForm = ref({
  id: '',
  moveBerthTime: '',
  hbId: '',
  dockingTime: '',
  operationTime: ''
})
const handleMove = (data) => {
  moveForm.value = {
    id: data.id,
    moveBerthTime:
      data?.status === '8' ? dateTimeToFormatTimestamp(data.moveBerthTime) : '',
    hbId: data?.status === '8' ? data.hbId : '',
    dockingTime: '',
    operationTime: ''
  }
  state.moveShow = true
}
const handleMoveFinished = () => {
  state.moveShow = false
  getPlanData()
}
const handleEdit = async (paramData) => {
	const data =await (await getHarborPlan(paramData.id)).data
	editForm.value = {
    hbName: '',
    remainingWeight: '',
    arrivalTime: null,
    dockingTime: null,
    operationTime: null,
    endTime: null,
    outBerthTime: null,
    leaveTime: null
  }
  editForm.value.hbName = data.hbName || ''
  editForm.value.id = data.id
  TimeFields.forEach((item) => {
    editForm.value[item.prop] = data[item.prop]
      ? dateTimeToFormatTimestamp(data[item.prop])
      : ''
  })
	const planDataParams = getLackType(editForm.value)
	const res = await getLackTypeList(planDataParams);//res.data就是[0,1,2]这些
	if(res.data.length===0){
		state.editPlanShow = true
	}else{
		Object.assign(planData, planDataParams)
		planData.lackTypes = res.data
		state.WindowPeriosLogSubmitShow = true
	}
}
const closeWindowPerios = () => {
	toast.success(t('task.addSuccess'))
	Object.assign(planData,{})
	handleReset()
	state.WindowPeriosLogSubmitShow = false
}
const getLackType =(dockPlan) => {
	const planParam = {}
	planParam.id = dockPlan.id
	const dateTimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
	const pad = (n) => n.toString().padStart(2, '0');
	const keys=['arrivalTime','dockingTime','operationTime','endTime','outBerthTime'];
	for(const key of keys){
		if(dateTimeRegex.test(dockPlan[key])){
			planParam[key] = dockPlan[key]
		}else{
			const date = new Date(dockPlan[key]);
			if(!isNaN(date.getTime())){
				planParam[key] = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
															+ `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
			}
		}
	}
	return planParam;
}
const handleConfirm = () => {
  handleCancel()
  getPlanData()
  // 重新获取数据
}
const handleEditConfirm = async (data) => {
	const keys=['arrivalTime','planDockingTime','dockingTime','operationTime','endTime','outBerthTime'];
	const lastKey = [...keys].reverse().find(key => data[key] !== undefined && data[key] !== null && data[key] !== '');
	const map = new Map();
	map.set('arrivalTime','port.time.arrivalTime');
	map.set('planDockingTime','port.time.planDockingTime');
	map.set('dockingTime','port.time.dockingTime');
	map.set('operationTime','port.time.operationTime');
	map.set('endTime','port.time.endTime');
	map.set('outBerthTime','port.time.outBerthTime');
	uni.showModal({
		title: t('others.obj11'),
		content: t('others.obj12')+"-->"+t(map.get(lastKey))+":"+data[lastKey],
		cancelText: t('common.cancel'),
		confirmText: t('common.confirm'),
		success: async (resp) => {
			if (resp.confirm) {
				try {
					const res = await updatePlanApp(data)
					if (res.code === 200) {
						state.editPlanShow = false
						toast.success(t('plan.editSuccess'))
						getPlanData()
					}
				} catch (error) {
					toast.error(error.msg)
					console.error('接口报错 ', error)
				}
			}
		}
	})
}
const handleCancel = () => {
  state.filterActionShow = false // 关闭筛选弹框
}

const hasFilter = computed(() => {
  const value = Object.keys(form.value)
    .filter(
      (key) => !['pageNum', 'pageSize', 'hbIds', 'isArchived'].includes(key)
    ) // 过滤掉 pageNum 和 pageSize
    .some((key) => form.value[key] !== '')
  return value
})
const handleFilterChange = () => {
  state.filterActionShow = true
}
const handleReset = () => {
  // 保留基础字段，只重置显示字段
  form.value = {
    ...form.value,
    ...resetableFields
  }
  saveBerthSelection([])
  getPlanData()
}

const saveBerthSelection = (ids) => {
  try {
    uni.setStorageSync(BERTH_STORAGE_KEY, JSON.stringify(ids))
  } catch (e) {
    console.error('保存泊位选择失败', e)
  }
}

const loadBerthSelection = () => {
  try {
    const savedIds = uni.getStorageSync(BERTH_STORAGE_KEY)
    if (savedIds) {
      form.value.hbIds = JSON.parse(savedIds)
      return true
    }
  } catch (e) {
    console.error('读取泊位选择失败', e)
  }
  return false
}

const handleHbIdsClear = () => {
  form.value.hbIds = []
  state.planList = []
  state.loadStatus = 'finished'
  saveBerthSelection([])
  toast.info(t('port.berth.pleaseSelect'))
}

const clearBerth = () => {
  form.value.hbIds = []
  state.planList = []
  state.loadStatus = 'finished'
  saveBerthSelection([])
  toast.info(t('port.berth.pleaseSelect'))
}

const handleHbIdsChange = () => {
  if (form.value.hbIds && form.value.hbIds.length > 0) {
    saveBerthSelection(form.value.hbIds)
    getPlanData()
  } else {
    toast.error(t('port.berth.pleaseSelect'))
  }
}

const getPlanData = async (isRest = true) => {
  if (!form.value.hbIds || form.value.hbIds.length === 0) {
    toast.error(t('port.berth.pleaseSelect'))
    return
  }

  state.loadStatus = 'loading'
  if (isRest) {
    state.planList = []
    form.value.pageNum = 1
  }
  const { endTimes, hbIds, ...rest } = form.value

  if (endTimes) {
    // 转换为后端需要的 yyyy-MM-dd HH:mm:ss 格式
    rest.endStartTime = formatTimestampToDateTime(
      endTimes[0],
      'YYYY-MM-DD HH:mm:ss'
    )
    rest.endEndTime = formatTimestampToDateTime(
      endTimes[1],
      'YYYY-MM-DD HH:mm:ss'
    )
  }
  rest.hbIds = hbIds.join(',')
  try {
    const res = await getPlanList(rest)
    if (res.code === 200) {
      state.planList = state.planList.concat(res.rows)
      state.total = res.total
    }
    if (state.planList.length >= state.total) state.loadStatus = 'finished'
  } catch (error) {
    toast.error(error.msg || '网络错误')
    console.error('获取计划单报错 ', error)
  } finally {
    state.loadStatus = 'error'
  }
}
const handlePlanDocking = async (id) => {
  try {
    const res = await toPlanDocking({
      id
    })
    if (res.code === 200) {
      toast.success(t('plan.dockingSuccess'))
      getPlanData()
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}
const scrolltolower = () => {
  if (state.planList.length < state.total) {
    form.value.pageNum = (form.value.pageNum || 1) + 1
    getPlanData(false)
  }
}
const getDicData = async () => {
  try {
    const statusData = await diclist({
      dictType: 'plan_status',
      pageSize: 50
    })
    state.statusOptions = statusData?.rows?.map((i) => {
      return {
        label: i.dictLabel,
        value: i.dictValue
      }
    })
  } catch (error) {
    console.error('接口报错 ', error)
  }
}
const getBerthList = async () => {
  try {
    const berthData = await getHarborBerthList()
    state.berthList = berthData?.rows?.map((i) => {
      return {
        label: i.berthName,
        value: i.berthId
      }
    })
  } catch (error) {
    console.error('查询泊位报错 ', error)
  }
}
const getOptions = () => {
  getBerthList()
  getDicData()
}
onShow(() => {
  getOptions()
  // 尝试从 storage 中恢复选择的泊位
  const hasSavedSelection = loadBerthSelection()
  if (hasSavedSelection && form.value.hbIds.length > 0) {
    getPlanData()
  } else {
    state.planList = []
    state.loadStatus = 'finished'
    if (!hasSavedSelection) {
      toast.info(t('port.berth.pleaseSelect'))
    }
  }
})
</script>

<style scoped lang="scss">
.ship-page {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .action-buttons {
    display: flex;
    justify-content: center;
    padding: 10px;
    border-top: 1px solid #eee;
  }

  .mg-r10 {
    margin-right: 10px;
  }

  .top {
    // margin: 14rpx;
    padding-right: $card-pd;
    background: $card-bg-color;
    margin-bottom: 10px;

    // border-radius: $border-radius-lg;
    .top-btn {
      padding: $card-pd 0;
      display: flex;
      align-items: center;
      height: 30rpx;
      line-height: 30rpx;
    }

    .plan-btn {
      flex: 3;
      padding-left: 20rpx;

      // padding-right: 20rpx;
      :deep(.wd-select-picker) {
        width: 100%;

        .wd-select-picker__cell {
          width: 100%;
          padding: 0;
        }
      }
    }

    .filter-btn {
      padding-right: 20rpx;
      margin-left: 20rpx;
    }
  }

  .content {
    flex: 1;
    overflow: auto;
    /* 确保滚动功能 */
    margin-bottom: 50px;
  }
}
</style>