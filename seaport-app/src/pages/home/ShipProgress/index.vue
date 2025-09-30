<template>
  <view class="shipProgress">
    <!-- 顶部分类导航 -->
    <wd-notice-bar
      :text="t('port.berth.hint')"
      prefix="warn-bold"
      :scrollable="false"
      closable
    />
    <view class="tab-container">
      <view
        v-for="(tab, index) in PierTypeOptions"
        :key="index"
        class="tab-item cursor-pointer"
        :class="{ active: state.currentTab === index }"
        @tap="switchTab(index)"
      >
        <text class="tab-text">{{ t(tab.label) }}</text>
      </view>
      <view
        class="tab-slider"
        :style="{ left: state.tabSliderLeft + 'rpx' }"
      ></view>
    </view>
    <view class="standard">
      <wd-collapse v-model="state.collapseValue">
        <wd-collapse-item
          :title="t('port.berth.standardRate')"
          name="standardRate"
        >
          <template #title="{ expanded, disabled, isFirst }">
            <view class="header">
              <text style="color: #666">{{
                state.currentTab === 1
                  ? t('port.berth.efficiencyWarning')
                  : t('port.berth.standardRate')
              }}</text>
              <text style="color: #409eff">{{
                expanded ? t('port.berth.close') : t('port.berth.view')
              }}</text>
            </view>
          </template>
          <view class="rate-container">
            <view class="rate-grid">
              <template v-if="state.currentTab === 1">
                <!-- 驳船码头 -->
                <view
                  v-for="(item, index) in progressRateData['驳船码头']"
                  :key="index"
                  class="rate-card"
                >
                  <view class="progress-rate">{{ item.progressRate }}</view>
                  <view class="rate-value"
                    >{{ item.rate }} <span>T/H</span></view
                  >
                </view>
              </template>
              <template v-else>
                <!-- 大码头（按泊位分组展示） -->
                <view
                  v-for="(groupItems, berthName) in groupedRateData"
                  :key="berthName"
                  class="berth-group"
                >
                  <view class="berth-title">{{ berthName }}</view>
                  <view class="rate-grid">
                    <view
                      v-for="(item, index) in groupItems"
                      :key="index"
                      class="rate-card"
                    >
                      <view class="progress-rate">{{ item.phase }}</view>
                      <view class="rate-value"
                        >{{ item.progressRate }} → {{ item.rate }}
                        <span>T/H</span>
                      </view>
                    </view>
                  </view>
                </view>
              </template>
            </view>
          </view>
        </wd-collapse-item>
      </wd-collapse>
    </view>
    <!-- 搜索筛选区 -->
    <view class="search-filter">
      <wd-select-picker
        class="filter-hbId"
        clearable
        :label="t('port.berth.pier')"
        :placeholder="t('port.berth.pleaseSelect')"
        label-width="50px"
        type="checkbox"
        filterable
        v-model="filterForm.berthName"
        :columns="state.berthList"
        @confirm="() => fetchWorkList()"
        @clear="() => fetchWorkList()"
        :display-format="displayFormat"
      />
      <wd-select-picker
        class="filter-material"
        clearable
        :label="t('port.berth.material')"
        :placeholder="t('port.berth.pleaseSelect')"
        label-width="50px"
        type="checkbox"
        filterable
        v-model="filterForm.materialName"
        :columns="state.materialList"
        @confirm="() => fetchWorkList()"
        @clear="() => fetchWorkList()"
        :display-format="displayFormat"
      />
    </view>

    <!-- 信息列表 -->
    <view class="card-list">
      <div class="loading" v-if="state.loading">
        <wd-loading />
      </div>
      <BerthCard
        v-for="(item, index) in state.workList"
        :key="item.planId"
        :data="item"
        :currentTab="state.currentTab"
        :reasonList="state.reasonList"
      />
      <wd-status-tip
        class="empty-data"
        image="content"
        :tip="t('port.berth.noContent')"
        v-if="state.workList.length === 0 && !state.loading"
      />
    </view>
  </view>
</template>

<script setup>
import { ref, computed, reactive, onUnmounted } from 'vue'
import { onLoad, onShow, onHide } from '@dcloudio/uni-app'
import {
  getHarborBerthList,
  getMaterialStatusList,
  getPierPlanList
} from '@/api/modules/plan'
import {
  PierTypeOptions,
  PlanStatusOptions,
  UnloadWorkTypeTagOptions
} from '@/contans/index'
import { diclist } from '@/api/modules/system'
import BerthCard from './BerthCard.vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const state = reactive({
  currentTab: 0,
  tabSliderLeft: 0,
  workList: [],
  berthList: [],
  materialList: [],
  loading: true,
  collapseValue: [], // 默认折叠面板
  reasonList: []
})

const progressRateData = ref({
  驳船码头: [
    {
      progressRate: '1%~10%',
      rate: 140
    },
    {
      progressRate: '11%~20%',
      rate: 200
    },
    {
      progressRate: '21%~30%',
      rate: 240
    },
    {
      progressRate: '31%~40%',
      rate: 260
    },
    {
      progressRate: '41%~50%',
      rate: 300
    },
    {
      progressRate: '51%~60%',
      rate: 320
    },
    {
      progressRate: '61%~70%',
      rate: 350
    },
    {
      progressRate: '71%~80%',
      rate: 380
    },
    {
      progressRate: '81%~90%',
      rate: 380
    },
    {
      progressRate: '91%~100%',
      rate: 370
    }
  ],
  大码头: [
    {
      berthName: '1#',
      phase: '开仓',
      progressRate: '0%-70%',
      rate: 3500
    },
    {
      berthName: '1#',
      phase: '暗仓',
      progressRate: '70%-90%',
      rate: 2300
    },
    {
      berthName: '1#',
      phase: '清仓',
      progressRate: '90%-100%',
      rate: 1300
    },
    {
      berthName: '4#',
      phase: '开仓',
      progressRate: '0%-70%',
      rate: 3000
    },
    {
      berthName: '4#',
      phase: '暗仓',
      progressRate: '70%-90%',
      rate: 2300
    },
    {
      berthName: '4#',
      phase: '清仓',
      progressRate: '90%-100%',
      rate: 1300
    },
    {
      berthName: '8#',
      phase: '开仓',
      progressRate: '0%-70%',
      rate: 3000
    },
    {
      berthName: '8#',
      phase: '暗仓',
      progressRate: '70%-90%',
      rate: 2300
    },
    {
      berthName: '8#',
      phase: '清仓',
      progressRate: '90%-100%',
      rate: 1300
    }
  ]
})
// 轮询设置
let pollingInterval = null
const filterForm = reactive({
  berthName: [],
  materialName: []
})

const displayFormat = (items, columns) => {
  let showValue = ''
  const values = items?.length <= 3 ? items : items.slice(0, 3)
  columns.forEach((column) => {
    values.forEach((item) => {
      if (column.value === item) {
        showValue
          ? (showValue += `,${column.label}`)
          : (showValue += `${column.label}`)
      }
    })
  })
  return showValue
}

// Dynamic tab width
const tabSliderWidth = computed(() => 750 / PierTypeOptions.length)

const switchTab = (index) => {
  state.currentTab = index
  state.tabSliderLeft = index * tabSliderWidth.value
  state.currentTab === 1
    ? getDicData('exception_reason')
    : getDicData('grand_pier_reason')

  fetchWorkList()
}

// 数据对比函数，判断数据是否变化
const isDataChanged = (newData, oldData) => {
  if (newData.length !== oldData.length) return true
  return newData.some((newItem, index) => {
    const oldItem = oldData[index]
    return (
      newItem.planId !== oldItem.planId ||
      JSON.stringify(newItem) !== JSON.stringify(oldItem)
    )
  })
}
const groupedRateData = computed(() => {
  const grouped = {}
  progressRateData.value['大码头'].forEach((item) => {
    if (!grouped[item.berthName]) {
      grouped[item.berthName] = []
    }
    grouped[item.berthName].push(item)
  })
  return grouped
})

// 正常搜索的 fetchWorkList
const fetchWorkList = async (isPoll = false) => {
  const { berthName, materialName } = filterForm
  if (!isPoll) {
    state.loading = true
  }
  const query = {
    pierType: PierTypeOptions[state.currentTab].value
  }
  if (berthName.length > 0) {
    query.berthName = berthName.join(',')
  }
  if (materialName.length > 0) {
    query.materialName = materialName.join(',')
  }
  try {
    const res = await getPierPlanList(query)
    if (res.code === 200) {
      if (isPoll) {
        const newData = res.data
        if (isDataChanged(newData, state.workList)) {
          state.workList = newData
        }
      } else {
        state.workList = res.data
      }
    }
  } catch (error) {
    console.error(error.msg)
    uni.showToast({
      title: error.msg || '网络错误',
      icon: 'error'
    })
  } finally {
    state.loading = false
  }
}

const getBerthList = async () => {
  try {
    const berthData = await getHarborBerthList()
    state.berthList = berthData?.rows?.map((i) => ({
      label: i.berthName,
      value: i.berthName
    }))
  } catch (error) {
    console.error('查询泊位报错 ', error)
  }
}

const getMaterialList = async () => {
  try {
    const { rows } = await getMaterialStatusList()
    state.materialList = rows?.map((item) => ({
      label: item.materialName,
      value: item.materialName
    }))
  } catch (error) {
    console.error('获取数据失败', error)
  }
}
const getDicData = async (type) => {
  try {
    const statusData = await diclist({
      dictType: type,
      pageSize: 50
    })
    state.reasonList = statusData?.rows?.map((i) => {
      return {
        label: i.dictLabel,
        value: i.dictLabel
      }
    })
  } catch (error) {
    console.error('接口报错 ', error)
  }
}
const startPolling = () => {
  if (pollingInterval) clearInterval(pollingInterval)
  pollingInterval = setInterval(() => {
    fetchWorkList(true)
  }, 10000)
}

const stopPolling = () => {
  if (pollingInterval) {
    clearInterval(pollingInterval)
    pollingInterval = null
  }
}

// 生命周期钩子
onShow(() => {
  fetchWorkList()
  getBerthList()
  getMaterialList()
  startPolling()
  getDicData('grand_pier_reason')
})

onHide(() => {
  stopPolling()
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style lang="scss">
.shipProgress {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  .tab-container {
    display: flex;
    position: relative;
    height: 80rpx;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    z-index: 10;
    flex-shrink: 0;
  }

  .tab-item {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .tab-text {
    font-size: 16px;
    font-weight: 500;
    color: #666666;
    transition: color 0.3s;
  }

  .tab-item.active .tab-text {
    color: #2196f3;
  }

  .tab-slider {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 375rpx;
    height: 4rpx;
    background-color: #2196f3;
    transition: left 0.3s ease;
  }

  .standard {
    padding: 10rpx;
    flex-shrink: 0;

    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }

  .rate-container {
    padding: 12rpx;
    background-color: #f5f7fa;
    border-radius: 8rpx;
    margin: 8rpx 0;
  }

  .rate-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300rpx, 1fr));
    /* 动态列宽，适配屏幕 */
    gap: 8rpx;
    /* 减少间距 */
  }

  .berth-group {
    margin-bottom: 12rpx;
  }

  .berth-title {
    font-weight: bold;
    font-size: 26rpx;
    /* 稍减小字体 */
    color: #333;
    margin: 8rpx 0;
    /* 减少外边距 */
  }

  .rate-card {
    background-color: #ffffff;
    border: 1rpx solid #dcdfe6;
    border-radius: 6rpx;
    padding: 8rpx;
    /* 减少内边距 */
    text-align: center;
    min-height: 80rpx;
    /* 设置最小高度，防止内容变化导致高度不一 */
    display: flex;
    flex-direction: column;
    justify-content: center;
    transition: transform 0.2s, box-shadow 0.2s;

    &:hover {
      transform: translateY(-2rpx);
      /* 减少悬浮偏移 */
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
  }

  .progress-rate {
    font-size: 28rpx;
    /* 减小字体 */
    font-weight: bold;
    color: #f7b731;
    margin-bottom: 4rpx;
    /* 减少间距 */
    white-space: nowrap;
    /* 防止文本换行 */
    overflow: hidden;
    text-overflow: ellipsis;
    /* 超长文本显示省略号 */
  }

  .rate-value {
    font-size: 30rpx;
    /* 减小字体 */
    color: #67c23a;
    font-weight: bold;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    > span {
      font-weight: 400;
      font-size: 18rpx;
      /* 减小单位字体 */
    }
  }

  .search-filter {
    background-color: #ffffff;
    display: flex;
    flex-shrink: 0;
    justify-content: space-around;
    .wd-select-picker__cell {
      padding: 10rpx 20rpx;
    }

    .filter-material {
      flex: 1;
    }

    .filter-hbId {
      flex: 1;
    }
  }

  .card-list {
    flex: 1;
    padding: 20rpx;
    overflow: auto;

    .loading {
      display: flex;
      width: 100%;
      height: 100%;
      align-items: center;
      justify-content: center;
    }
  }

  .empty-data {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 100rpx 0;
  }

  .cursor-pointer {
    cursor: pointer;
  }
}
</style>
