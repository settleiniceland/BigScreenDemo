<template>
  <div class="container-work">
    <div class="title-box">
      <el-tooltip content="查看人工作业量更改日志与空窗期日志" placement="bottom">
        <el-icon
          class="icon-btn"
          @click="toggleSidebar">
          <Menu />
        </el-icon>
      </el-tooltip>
      <span style="cursor: pointer">
        计划
      </span>
    </div>
    <div class="content-box">
      <el-table
        :data="showTableData1"
        :span-method="spanMethod"
        @row-click="handleRowClick"
        style="width: 100%">
        <el-table-column prop="hbName" label="泊位" width="30">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.hbName" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.hbName }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="shipName" label="船名" width="110" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.shipName" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.shipName }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="collectFee" label="滞期费" width="46">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.collectFee" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.collectFee }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
         <el-table-column prop="materialName" label="物料" width="60">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.materialName" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.materialName }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
         <el-table-column prop="usageUnit" label="客户" width="60" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.usageUnit" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.usageUnit }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="作业量" prop="progressDetail" align="center" width="140">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.progressDetail" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.progressDetail }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="进度" prop="progress" align="center" width="77">
          <template #default="{ row }">
            <div>
              <el-progress
                :percentage="row.progress"
                :stroke-width="12"
                :text-inside="true"
                color="#b8ff14">
                <template #default="{ percentage }">
                  <span style="color: red; font-weight: bold;">{{ percentage }}%</span>
                </template>
              </el-progress>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="efficiency" label="效率" width="80">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.efficiency" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.efficiency }}</span>
            </el-tooltip>
          </template>
        </el-table-column>  
      </el-table>
    </div>
    <transition name="slide">
      <div v-show="showSidebar" class="sidebar-box">
        <h3>人工作业量更改日志</h3>
        <el-table
          :data="weightUpdateData"
          style="width: 100%">
          <el-table-column prop="loadSequence" label="次序" width="30">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.loadSequence" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.loadSequence }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="materialName" label="物料" width="60">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.materialName" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.materialName }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="nickName" label="更改人" width="60">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.nickName" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.nickName }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="refreshTime" label="更改时间" width="130">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.refreshTime" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.refreshTime }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="distanceLastTime" label="距上次(h)">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.distanceLastTime" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.distanceLastTime }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </transition>
    <transition name="slide">
      <div v-show="showSidebar" class="sidebar-box1">
        <h3>空窗期日志</h3>
        <el-table
          :data="dockWindowData"
          style="width: 100%">
          <el-table-column prop="periodType" label="空窗类型" width="60">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.periodType" placement="top" :teleported="true">
                <span class="ellipsis">{{ showPeriodTypeName(row.periodType) }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="remark1" label="开始时间" width="70">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.remark1" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.remark1 }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="remark2" label="结束时间" width="70">
            <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.remark2" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.remark2 }}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="stopClassDetail" label="暂停内容" width="60" show-overflow-tooltip>
            <!-- <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.stopClassDetail" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.stopClassDetail }}</span>
              </el-tooltip>
            </template> -->
          </el-table-column>
          <el-table-column prop="remark" label="备注" width="50" show-overflow-tooltip>
            <!-- <template #default="{ row }">
              <el-tooltip effect="dark" :content="row.remark" placement="top" :teleported="true">
                <span class="ellipsis">{{ row.remark }}</span>
              </el-tooltip>
            </template> -->
          </el-table-column>
        </el-table>
      </div>
    </transition>
  </div>
</template>
<script setup>
import { computed,ref } from "vue";
import { Menu } from "@element-plus/icons-vue";
import { useWebSocketStore } from "@/store/modules/websocketStore";
const websocketStore = useWebSocketStore();
const showSidebar = ref(false);
const props = defineProps({
  data: {
    type: Array,
    default: ()=>[]
  },
  selectEvent: {
    type: Function,
    default: ()=>{}
  },
  customMapArea: {
    type: Function,
    default: ()=>{}
  }
})
const rowId = ref()
const rowLoadSequence = ref()
const weightUpdateData = computed(()=>{
  return getUpdateLogs(websocketStore.screen_dockPlans3,rowId.value,rowLoadSequence.value);
});
const dockWindowData = computed(()=>{
  return getDockWindowData(websocketStore.screen_dockPlans3,rowId.value);
});
const handleRowClick = (row, column, event) => {
  props.selectEvent(row.id,row.loadSequence,row.hbName+"泊位-"+row.materialName+"-客户<"+row.usageUnit+">装卸货单");
  // showSidebar.value = true;
  rowId.value = row.id;
  rowLoadSequence.value = row.loadSequence;
  for(const item of websocketStore.screen_dockBerths){
    if(item.berthName === row.hbName){
      const point = JSON.parse(item.berthGeoJson).geometry.coordinates[0][1];
      props.customMapArea([point[1],point[0]]);
      break;
    }
  }
};
const showPeriodTypeName = (val)=>{
  switch(val){
    case 1:
      return "靠泊时间-作业时间";
    case 2:
      return "结束时间-离泊时间";
  }
}
const showTableData1 = computed(()=>{
  let rows = [];
  props.data.forEach(item=>{
    let rowsNum = 1;
    if(item.params.assistantList!==undefined && item.params.assistantList.length>0){
      rowsNum+=item.params.assistantList.length
    }
    rows.push({
      hbName: item.hbName,
      shipName: item.shipName,
      collectFee: item.params.collectFee,
      materialName: item.materialName,
      usageUnit: item.usageUnit,
      efficiency: item.params.efficiency,
      progressDetail: "已作业"+(item.unloadWeight===undefined?0:item.unloadWeight)+"，共"+item.tonnage,
      progress: Math.round(safeDivide(item.unloadWeight,item.tonnage)),
      rowspan: rowsNum,
      id: item.id,
      loadSequence: 1
    })
    if(rowsNum>1){
      item.params.assistantList.forEach(ass => {
        rows.push({
          hbName: item.hbName,
          shipName: item.shipName,
          collectFee: item.params.collectFee,
          materialName: ass.materialName,
          usageUnit: ass.usageUnit,
          efficiency: ass.params.efficiency,
          progressDetail: "已作业"+ass.unloadWeight+"，共"+ass.tonnage,
          progress: Math.round(ass.unloadWeight/ass.tonnage*100),
          rowspan: 0,
          id: item.id,
          loadSequence: ass.loadSequence
        })
      });
    }
  });
  return rows;
});
const spanMethod = ({ row,columnIndex }) => {
  if (columnIndex <= 2) {
    return {
      rowspan: row.rowspan,
      colspan: row.rowspan > 0 ? 1 : 0
    };
  }
};

const safeDivide= (strNum1, strNum2) => {
  const num1 = Number(strNum1)
  const num2 = Number(strNum2)
  if (isNaN(num1) || isNaN(num2) || num2 === 0) {
    return 0
  }
  return num1 / num2 * 100
}
const toggleSidebar = () => {
  showSidebar.value = !showSidebar.value;
};
const getUpdateLogs=(originPlans,id,loadSequence)=>{
  let updateLogs = [];
  outer: for(const plan of originPlans){
    if(plan.id === id){
      if(plan.params.windowPeriodList !== undefined && plan.params.windowPeriodList !== null){
        dockWindowData.value = plan.params.windowPeriodList;
      }else{
        dockWindowData.value = []
      }
      if(loadSequence>1){
        for(const ass of plan.params.assistantList){
          if(ass.loadSequence === loadSequence){
            if(ass.params.updateLogs !== undefined && ass.params.updateLogs !== null){
              updateLogs = ass.params.updateLogs;
            }
            break outer;
          }
        }
      }else{
        if(plan.params.updateLogs !== undefined && plan.params.updateLogs !== null){
          updateLogs = plan.params.updateLogs;
        }
        break outer;
      }
    }
  }
  return updateLogs;
}
const getDockWindowData=(originPlans,id)=>{
  let dockWindowLogs=[];
  outer: for(const plan of originPlans){
    if(plan.id === id){
      if(plan.params.windowPeriodList !== undefined && plan.params.windowPeriodList !== null){
        dockWindowLogs = plan.params.windowPeriodList;
      }else{
        dockWindowLogs = []
      }
      break outer;
    }
  }
  return dockWindowLogs;
}
</script>
<style scoped lang="less">
.container-work {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  width: 100%;
}
.title-box {
  padding-left: 3vh;
  width: 100%;
  font-size: 18px;
  font-weight: bold;
  color: white;
  background:
    url("/src/assets/images/12.png") no-repeat center,
    #29566e;
  background-size: 100% 100%;
  border-radius: 18px 8px 0 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  span {
    flex: 1;
    text-align: center;
  }
  .icon-style {
    width: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
.content-box {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: linear-gradient(to left, rgba(#005f99, 0.3), rgba(#005f99, 0.6));
  backdrop-filter: blur(2px);
  border-radius: 0 0 8px 8px;
}
::v-deep(.el-table) {
  height: 100%;
  width: 100%;
  background-color: transparent !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: transparent !important;
  --el-table-header-text-color: #b8ff14;
  --el-table-row-hover-bg-color: rgba(33, 148, 242, 0.15);
  color: #ffffff;
  .el-table__cell {
    padding: 7px 0 !important;
    line-height: normal;
    font-size: 0.8rem;
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
      font-weight: bold;
    }
  }
  .el-progress__text {
    color: white;
  }
}
::deep(.ellipsis){
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.icon-btn {
  margin-right: 8px;
  font-size: 20px;
  cursor: pointer;
}

/* 侧边栏样式 */
.sidebar-box {
  position: absolute;
  top: 0;
  left: -40vh;
  width: 39vh;
  height: 100%;
  background: rgba(0, 95, 153, 0.9);
  color: #fff;
  padding: 15px;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.3);
}
.sidebar-box1 {
  position: absolute;
  top: 0;
  left: -75vh;
  width: 34vh;
  height: 100%;
  background: rgba(0, 95, 153, 0.9);
  color: #fff;
  padding: 15px;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.3);
}

/* 过渡动画 */
.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}
</style>