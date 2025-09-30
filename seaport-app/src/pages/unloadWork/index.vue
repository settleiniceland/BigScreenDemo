<template>
  <view class="unloadPage">
    <wd-notice-bar
      v-if="state.planInfo?.unloadStatus == '2'"
      :scrollable="false"
      :text="t('port.unloadWork.completedUnloadWarning')"
      closable
      prefix="check-outline"
    />
    <view class="top flex justify-between">
      <view class="filter-btn top-btn" @tap="handleFilterChange">
        <wd-drop-menu custom-class="custom-drop-menu">
          <wd-drop-menu-item
            ref="dropMenu"
            :title="t('port.planManagement.filter')"
            icon="filter1"
            icon-size="24px"
          >
            <view class="filter-form">
              <FilterForm :modelForm="filterForm" />
              <view class="action-buttons">
                <wd-button style="width: 80%" suck @tap="handleConfirm">{{
                  t('task.search')
                }}</wd-button>
              </view>
            </view>
          </wd-drop-menu-item>
        </wd-drop-menu>
      </view>
      <wd-divider vertical style="height: 100%; margin: 0" />
      <view class="resert-btn top-btn" @tap="handleReset">
        <wd-text
          :text="t('port.planManagement.clear')"
          :size="fontSize"
          color="#666666"
          style="margin-right: 5rpx"
        />
        <wd-icon name="refresh" :size="fontSize" color="#666666" />
      </view>
    </view>

    <!-- 计划单信息区域 -->
    <view class="planfnfo">
      <PlanInfo :key="state.planInfo?.status" :planInfo="state.planInfo" />
    </view>

    <!-- 列表区域 -->
    <scroll-view scroll-y class="content" @scrolltolower="scrolltolower">
      <!-- 进行中卸货单 -->
      <UnloadCard
        v-for="item in inProgressList"
        :key="item.id"
        :data="item"
        :getUnloadWorkList="getUnloadWorkList"
        :handleUnloadWork="handleUnloadWork"
        :getPlanDetail="getPlanDetail"
        :showAddFormFn="
          (value) => {
            state.showAddForm = value
          }"
				:planData="unComplatePlanInfo"
      />
      <!-- 已完成卸货单 -->
      <view class="section" v-if="completedList.length > 0">
        <view class="section-title">{{
          t('port.unloadWork.completedUnloadBills')
        }}</view>
        <UnloadCard
          v-for="item in completedList"
          :unloadStatus="state.planInfo?.unloadStatus"
          :key="item.id"
          :data="item"
          :getUnloadWorkList="getUnloadWorkList"
          :handleUnloadWork="handleUnloadWork"
          :showAddFormFn="
            (value) => {
              state.showAddForm = value
            }"
					:planData="complatePlanInfo"
        />
      </view>
      <!-- 无数据提示 -->
      <wd-status-tip
        v-if="state.unloadList.length == 0 && state.loadStatus == 'finished'"
        image="content"
        :tip="t('port.unloadWork.noUnloadBills')"
      />
      <load-more v-else :status="state.loadStatus"></load-more>
    </scroll-view>
    <!-- 新建作业面板 -->
    <wd-action-sheet
      v-model="state.showUnloadWorkForm"
      :title="
        state.currentUnloadType == UnloadWorkType.Add
          ? t('port.unloadWork.createUnloadBill')
          : t('port.unloadWork.editUnloadBill')
      "
      @closed="handleWorkClosed">
      <UnloadWorkForm
        v-model:modelForm="unloadWorkForm"
        :type="String(state.currentUnloadType)"
        :getUnloadWorkList="getUnloadWorkList"
        :planInfo="state.planInfo"
      />
    </wd-action-sheet>
    <wd-fab
      v-if="!state.showUnloadWorkForm && state.showAddForm"
      :disabled="state.planInfo?.unloadStatus == '2'"
      position="left-bottom"
      :draggable="true"
      activeIcon="add"
      @tap="handleUnloadWork(UnloadWorkType.Add)"
      :expandable="false"
    >
      <wd-button custom-class="custom-button" icon="add" type="primary">
        {{ t('port.unloadWork.createUnloadBill') }}
      </wd-button>
    </wd-fab>
    <pop :updateInfo="store.updateInfo" v-if="store.isUpdate" />
    <wd-toast />
  </view>
</template>

<script setup>
import {
  onLoad,
  onBackPress,
  onNavigationBarButtonTap
} from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { useToast } from 'wot-design-uni'
import dayjs from 'dayjs'
import { reactive, ref, computed, watch } from 'vue'
import loadMore from '@/components/load-more.vue'
import { UnloadWorkType } from '@/contans/index'
import { useUserStore } from '@/store'
import pop from '@/uni_modules/uni-upgrade-center-app/pages/upgrade-popup.vue'
import { getUnloadList, getHarborPlan } from '@/api/modules/plan'
import UnloadCard from './components/UnloadCard.vue'
import FilterForm from './components/FilterForm.vue'
import UnloadWorkForm from './components/UnloadWorkForm.vue'
import PlanInfo from './components/PlanInfo.vue'
import { dateTimeToFormatTimestamp } from '@/utils/time'

const { t } = useI18n()
const store = useUserStore()
const fontSize = '24rpx'
const dropMenu = ref()
const toast = useToast()
const state = reactive({
  unloadList: [],
  total: '',
  loadStatus: 'loading',
  planId: '',
  planInfo: {},
  showUnloadWorkForm: false,
  currentUnloadType: UnloadWorkType.Add,
  showAddForm: true
})
const unComplatePlanInfo = reactive({})
const complatePlanInfo = reactive({})
const filterForm = ref({
  pageSize: 20,
  pageNum: 1
})
const startTime = computed(() => {
  const hourNum = state.planInfo.pierType == '2' ? 6 : 7
  const minute = state.planInfo.pierType == '2' ? 30 : 0
  const time = dayjs()
    .hour(hourNum)
    .minute(minute)
    .second(0)
    .format('YYYY-MM-DD HH:mm:ss')
  return dateTimeToFormatTimestamp(time)
})
const getTime = (newClass) => {
  let hourNum
  const minute = state.planInfo.pierType == '2' ? 30 : 0
  if (newClass == '白班') {
    hourNum = state.planInfo.pierType == '2' ? 6 : 7
  } else {
    hourNum = state.planInfo.pierType == '2' ? 18 : 19
  }
  const time = dayjs()
    .hour(hourNum)
    .minute(minute)
    .second(0)
    .format('YYYY-MM-DD HH:mm:ss')
  return dateTimeToFormatTimestamp(time)
}
const unloadWorkForm = ref({
  classTime: undefined,
  classes: undefined,
  startTime: undefined,
	endTime: undefined
})
const handleUnloadWork = (type, data) => {
  state.currentUnloadType = type
  if (type == UnloadWorkType.Edit) {
    const { classTime, startTime,endTime} = data
    unloadWorkForm.value = {
      ...data,
      classTime: dateTimeToFormatTimestamp(classTime),
      startTime: dateTimeToFormatTimestamp(startTime),
			endTime: dateTimeToFormatTimestamp(endTime)
    }
  } else {
    unloadWorkForm.value.planId = state.planId
  }
  state.showUnloadWorkForm = true
}
const handleWorkClosed = () => {
  unloadWorkForm.value = {
    classTime: undefined,
    classes: undefined,
    startTime: undefined,
		endTime: undefined
  }
}

// 计算属性：进行中的卸货单 (workType 为 '0' 或 '1')
const inProgressList = computed(() => {
  return state.unloadList.filter(
    (item) => item.workType == '0' || item.workType == '1'
  )
})

// 计算属性：已完成的卸货单 (workType 为 '2')
const completedList = computed(() => {
  return state.unloadList.filter((item) => item.workType == '2')
})

const scrolltolower = () => {
  if (state.unloadList.length < state.total) {
    filterForm.value.pageNum = (filterForm.value.pageNum || 1) + 1
    getUnloadWorkList(false)
  }
}

const getUnloadWorkList = async (isFilter = true) => {
  if (state.showUnloadWorkForm) {
    state.showUnloadWorkForm = false
  }
  state.loadStatus = 'loading'
  if (isFilter) {
    state.unloadList = []
    filterForm.value.pageNum = 1
  }
  const { classTime, ...rest } = filterForm.value

  const query = {
    planId: state.planId,
    ...rest
  }

  if (classTime) {
    query.classTime = dateTimeToFormatTimestamp(classTime)
  }
  try {
    const res = await getUnloadList(query)
    if (res.code == 200) {
      state.unloadList = state.unloadList?.concat(res.rows)
      state.total = res.total
      state.loadStatus =
        state.unloadList.length >= state.total ? 'finished' : 'loading'
    } else {
      toast.error(res.msg)
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}

const getPlanDetail = async () => {
  try {
    const res = await getHarborPlan(state.planId)
    if (res.code == 200) {
      state.planInfo = {
        ...res.data
      }
			Object.assign(unComplatePlanInfo,{
				isNewest: true,
				...res.data
			})
			Object.assign(complatePlanInfo,{
				isNewest: false,
				...res.data
			})
    } else {
      toast.error(res.msg)
    }
  } catch (error) {
    toast.error(error.msg)
    console.error('接口报错 ', error)
  }
}

const handleConfirm = () => {
  dropMenu.value.close()
  getUnloadWorkList(true)
}

const handleReset = () => {
  filterForm.value = {
    pageSize: 20,
    pageNum: 1
  }
  getUnloadWorkList(true)
}

// 新增：补充 handleFilterChange 方法，解决警告
const handleFilterChange = () => {
  // 打开筛选下拉菜单
  dropMenu.value.open && dropMenu.value.open()
}

onLoad((options) => {
  state.planId = options?.planId || ''
  if (!state.planId) {
    uni.switchTab({
      url: '/pages/shipPlan/index'
    })
    return
  }
  getUnloadWorkList(false)
  getPlanDetail()
})
// 监听导航栏按钮点击事件
onNavigationBarButtonTap(() => {
  uni.switchTab({
    url: '/pages/shipPlan/index'
  })
})

// 监听返回按钮事件
onBackPress(() => {
  uni.switchTab({
    url: '/pages/shipPlan/index'
  })
  return true
})
</script>

<style lang="scss">
.unloadPage {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .custom-drop-menu {
    height: 60rpx !important;
    width: 100%;

    :deep(.wd-drop-menu__list) {
      height: 60rpx !important;
    }

    :deep(.wd-drop-menu__item) {
      height: 60rpx !important;
      line-height: 60rpx !important;
      display: flex;
      align-items: center;
      justify-content: center;

      .wd-drop-menu__item-title {
        line-height: 60rpx;
      }
    }
  }

  .top {
    padding-right: $card-pd;
    background: $card-bg-color;
    margin-bottom: 10px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.2);

    .top-btn {
      display: flex;
      align-items: center;
      height: 60rpx;
      line-height: 60rpx;
    }

    .filter-form {
      padding: $card-pd;
    }

    .resert-btn {
      flex: 1;
      background: #f9f9f9;
      display: flex;
      justify-content: center;
    }

    .filter-btn {
      flex: 4;
      display: flex;
      justify-content: center;
    }
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    padding-top: 10px;
    border-top: 1px solid #eee;
  }
  .planfnfo {
    padding: 0 20rpx;
  }

  .content {
    flex: 1;
    margin-top: 10px;
    overflow: auto;
    margin-bottom: 20px;
  }

  .section-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #aaaa7f;
    padding: 10rpx 0;
    text-align: center;
    border-bottom: 2rpx solid #aaaa7f;
    margin-bottom: 10rpx;
  }
}
</style>