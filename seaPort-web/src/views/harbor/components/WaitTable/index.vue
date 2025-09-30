<template>
  <div class="table">
    <el-table
      ref="tableRef"
      :data="tableData"
      :border="false"
      showOverflowTooltip
      :tooltipOptions="{appendTo: 'body'}"
      @row-click="handleRowClick"
      style="width: 100%; padding-bottom: 15px"
    >
      <!-- 添加序号列 -->
      <el-table-column type="index" label="序号" width="40" align="center"/>
      <el-table-column prop="hbName" label="泊位" width="50" sortable/>
      <el-table-column prop="shipName" label="船名" width="170"/>
      <el-table-column prop="materialName" label="物资" width="270"/>
      <el-table-column prop="planTonnage" label="计划重量（吨）" width="90"/>
      <el-table-column prop="arrivalTime" label="到港时间"/>
      <el-table-column prop="planDockingTime" label="计划靠泊时间" />
      <el-table-column label="等靠偏差" width="180">
        <template #default="{ row }">
          <span :style="{ color: getTimeDiffColor(row.planDockingTime) }">
            {{ formatTimeDiff(row.planDockingTime) }}
          </span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useWebSocketStore } from "@/store/modules/websocketStore";
const websocketStore = useWebSocketStore();
const tableData = ref([]);
const props = defineProps({
  customMapArea: {
    type: Function,
    default: ()=>{}
  }
})
const handleRowClick = (row,column,event)=>{
  for(const item of websocketStore.screen_dockBerths){
    if(item.berthName === row.hbName){
      const point = JSON.parse(item.berthGeoJson).geometry.coordinates[0][1];
      props.customMapArea([point[1],point[0]]);
      break;
    }
  }
}
watch(
  () => websocketStore.screen_dockPlans2,
  newVal => {
    if (newVal && JSON.stringify(newVal) !== JSON.stringify(tableData.value)) {
      tableData.value = [...newVal]; // 使用扩展运算符确保响应式
    }
  }
);
const formatTimeDiff = (planDockingTime)=>{
  if (!planDockingTime) return '-'
  const now = new Date().getTime()
  const target = new Date(planDockingTime).getTime()
  let diff = target - now // 计划时间 - 当前时间
  const sign = diff >= 0 ? '' : '-' // 正负号
  diff = Math.abs(diff)
  const minutes = Math.floor(diff / 1000 / 60)
  const days = Math.floor(minutes / (60 * 24))
  const hours = Math.floor((minutes % (60 * 24)) / 60)
  const mins = minutes % 60
  return `${sign}${days}天${hours}小时${mins}分钟`
}
const getTimeDiffColor = (planDockingTime)=>{
  if (!planDockingTime) return '#fff'
  const now = new Date().getTime()
  const target = new Date(planDockingTime).getTime()
  return target - now >= 0 ? '#39FF14' : '#fb4c2d'
}
</script>
<style scoped lang="less">
.table {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  height: 100%;

  :deep(.el-table__body-wrapper) {
    overflow-y: auto !important;
    max-height: 100% !important;
  }

  .fullscreen-container & {
    height: 100%;
    :deep(.el-table) {
      height: calc(100% - 40px);
      :deep(.el-table__body-wrapper) {
        height: calc(100% - 40px);
      }
    }
  }

  .dropdown-link {
    color: #e6a23c !important;
  }

  ::v-deep(.el-table) {
    background-color: transparent !important;
    --el-table-tr-bg-color: transparent !important;
    --el-table-header-bg-color: transparent !important;
    --el-table-border-color: transparent !important;
    --el-table-border: none !important;
    --el-table-header-text-color: #ffffff;
    --el-table-row-hover-bg-color: rgba(33, 148, 242, 0.15);
    color: #feffff;

    .el-table__cell {
      padding: 5px 0 !important;
      line-height: normal;
      font-size: 0.9em;
    }

    .el-table-fixed-column--right {
      text-align: center;
      background-color: rgba(#5b88c4, 0.3) !important;
    }

    .cell {
      padding: 0;
      font-size: 1em;
      line-height: normal;
      .el-icon {
        color: #ffffff;
        font-weight: 800;
      }
    }
  }

  .color-warning {
    color: #e6a23c !important;
    font-weight: 600;
  }
}
</style>