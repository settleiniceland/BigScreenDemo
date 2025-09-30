<template>
  <div class="main">
    <!-- 地图 -->
    <div id="map" class="map" />
    <!-- 头部 -->
    <div class="header">
      <div class="area-tabs">
        <div
          class="area-tab"
          :class="{ active: activeDeptId === dept.deptId }"
          v-for="dept in websocketStore.screen_depts"
          :key="dept.deptId"
          @click="changeDept(dept.deptId)"
        >
          {{dept.deptName}}
        </div>
      </div>
      <div class="title">港口管理系统平台 - 数据看板</div>
    </div>

    <!-- 泊位状态区域，添加动画和显示控制 -->
    <div
      ref="berthEchatRef"
      class="berthEchat bg-img"
      :class="{
        'berth-hidden': !isBerthStatusExpanded,
        'section-hidden': !isAllSectionsExpanded
      }"
    >
      <BerthEchats
        title="泊位状态"
        :containerRef="berthEchatRef"
        :enterFullscreen="() => enterFullscreen('berthEchat', berthEchatRef)"
        :exitFullscreen="() => exitFullscreen('berthEchat')"
        :isFullscreen="isFullscreen('berthEchat')"
      />
    </div>

    <div
      ref="berthListRef"
      class="left bg-2"
      :class="{ 'section-hidden': !isAllSectionsExpanded }"
    >
      <div class="title-container">
        <div class="title" :class="isFullscreen('berthList') && 'isFullTitle'">
          {{"港口作业汇总"}}
        </div>

        <!-- 泊位状态浮动按钮 -->
        <div
          v-if="!isFullscreen('berthList')"
          class="berth-status-toggle"
          @click="toggleBerthStatus"
        >
          <el-tooltip
            class="box-item"
            effect="dark"
            :content="
              isBerthStatusExpanded ? '收起泊位状态区域' : '展开泊位状态区域'
            "
            placement="top-start"
          >
            <IconifyIconOffline
              :icon="isBerthStatusExpanded ? Fold : Expand"
              class="toggle-icon"
            />
          </el-tooltip>
        </div>
        <IconifyIconOffline
          :icon="isFullscreen('berthList') ? ExitFullscreen : Fullscreen"
          class="icon"
          @click="
            isFullscreen('berthList')
              ? exitFullscreen('berthList')
              : enterFullscreen('berthList', berthListRef)
          "
        />
      </div>
      <WorkSummary/>
    </div>
    <div class="right flex flex-col gap-2"
      :class="{ 'section-hidden_150': !isAllSectionsExpanded }">
      <div class="h-[33%]">
        <dockPlan31
          :data="websocketStore.screen_dockPlans3"
          :selectEvent="definedPlanIdSequence"
          :customMapArea="customMapArea"
        />
      </div>
      <div class="h-[33%]">
        <dockPlan32
          :data="selectLoadWorks"
          :title="selectPlanLoadWorksTitle"
          :selectEvent="definedPlanLoadWorkDuid"
        />
      </div>
      <div class="h-[33%]">
        <dockPlan33
          :title="selectPlanLoadWorkLogsTitle"
          :stopLogs="selectLoadWorkStopLogs"
          :slowDownLogs="selectLoadWorkSlowWorkLogs"
        />
      </div>
    </div>
    <div
      class="bottom gap-2"
      :class="{ 'section-hidden': !isAllSectionsExpanded }"
    >
      <div ref="waitListRef" class="waitList bg-img">
        <div class="title-container">
          <div class="title">等泊计划</div>
          <IconifyIconOffline
            :icon="isFullscreen('waitList') ? ExitFullscreen : Fullscreen"
            class="icon"
            @click="isFullscreen('waitList') ? exitFullscreen('waitList') : enterFullscreen('waitList', waitListRef)"
          />
        </div>
        <WaitTable :customMapArea="customMapArea"/>
      </div>
    </div>

    <div class="fullscreen" @click="onContentFullScreenChange">
      <div class="time_info">
        <div class="timer">{{ nowTime }}</div>
        <div class="week">星期{{ week }}</div>
      </div>
      <div class="all-sections-toggle" @click.stop="toggleAllSections">
        <el-tooltip
          class="box-item"
          effect="dark"
          :content="isAllSectionsExpanded ? '收起所有区域' : '展开所有区域'"
          placement="top-start"
        >
          <IconifyIconOffline
            :icon="isAllSectionsExpanded ? Fold : Expand"
            class="toggle-icon"
          />
        </el-tooltip>
      </div>
      <IconifyIconOffline
        :icon="fullscreenStatus === 0 ? Fullscreen : ExitFullscreen"
      />
    </div>
    <div v-if="isFullscreen()" class="fullscreen-overlay" />
  </div>
</template>
<script setup lang="ts">
import { computed,onMounted, onUnmounted, ref } from "vue";
import { useMap } from "./utils/useMap";
import ExitFullscreen from "@iconify-icons/ri/fullscreen-exit-fill";
import Fullscreen from "@iconify-icons/ri/fullscreen-fill";
import Expand from "@iconify-icons/ep/expand";
import Fold from "@iconify-icons/ep/fold";
import { useCurrentTime } from "@/hooks/useCurrentTime";
import BerthEchats from "./components/BaseEchats/LineChart.vue";
import { useFullscreen } from "./utils/useFullscreen";
import WaitTable from "./components/WaitTable/index.vue";
import { useWebSocketStore } from "@/store/modules/websocketStore";
import dockPlan31 from "@/views/harbor/components/NewFloder/dockPlan3-1.vue";
import dockPlan32 from "@/views/harbor/components/NewFloder/dockPlan3-2.vue";
import dockPlan33 from "@/views/harbor/components/NewFloder/dockPlan3-3.vue";
import WorkSummary from "./components/WorkSummary/index.vue";
import {ElMessage} from "element-plus";
import { useUserStore } from "@/store/modules/user";
import {getDeptId} from "@/api/system/user";
const userStore = useUserStore();
const {
  initializeMap,
  fullscreenStatus,
  onContentFullScreenChange,
  customMapArea
} = useMap();
const { nowTime, week } = useCurrentTime();
const { enterFullscreen, exitFullscreen, isFullscreen } = useFullscreen();
const berthListRef = ref();
const waitListRef = ref();
const berthEchatRef = ref();
const planEchatRef = ref();
const BigPeriRef = ref();
const BargeListRef = ref();
// 选中的计划信息
const selectPlanId = ref()
const selectPlanLoadSequence = ref()
const selectPlanLoadWorksTitle = ref()
const selectPlanLoadWorkDuid = ref()
const selectPlanLoadWorkLogsTitle = ref()
const definedPlanIdSequence = (id,loadSequence,title)=>{
  selectPlanId.value = id;
  selectPlanLoadSequence.value = loadSequence;
  selectPlanLoadWorksTitle.value = title;
}
const selectLoadWorks = computed(()=>{
  let loadWorks = getLoadWork(websocketStore.screen_dockPlans3,selectPlanId.value,selectPlanLoadSequence.value);
  return loadWorks;
})
const definedPlanLoadWorkDuid = (duid,title)=>{
  selectPlanLoadWorkDuid.value = duid;
  selectPlanLoadWorkLogsTitle.value = title;
}
const selectLoadWorkStopLogs = computed(()=>{
  let loadWorks = getLoadWork(websocketStore.screen_dockPlans3,selectPlanId.value,selectPlanLoadSequence.value);
  return getLoadWorkStopLogs(loadWorks,selectPlanLoadWorkDuid.value);
})
const selectLoadWorkSlowWorkLogs = computed(()=>{
  let loadWorks = getLoadWork(websocketStore.screen_dockPlans3,selectPlanId.value,selectPlanLoadSequence.value);
  return getLoadWorkSlowDownLogs(loadWorks,selectPlanLoadWorkDuid.value);
})
const getLoadWork=(originPlans,id,loadSequence)=>{
  let loadWorks = [];
  outer: for(const plan of originPlans){
    if(plan.id === id){
      if(loadSequence>1){
        for(const ass of plan.params.assistantList){
          if(ass.loadSequence === loadSequence){
            if(ass.params.unloadWorkList !== undefined && ass.params.unloadWorkList !== null){
              loadWorks = ass.params.unloadWorkList;
            }
            break outer;
          }
        }
      }else{
        if(plan.params.unloadWorkList !== undefined && plan.params.unloadWorkList !== null){
          loadWorks = plan.params.unloadWorkList;
        }
        break outer;
      }
    }
  }
  return loadWorks;
}
const getLoadWorkStopLogs=(loadWorks,duid)=>{
  let stopLogs = [];
  for(const work of loadWorks){
    if(duid === work.duId){
      if(work.params.unloadWorkDetail!==undefined && work.params.unloadWorkDetail!==null){
        stopLogs = work.params.unloadWorkDetail
      }
      break;
    }
  }
  return stopLogs;
}
const getLoadWorkSlowDownLogs=(loadWorks,duid)=>{
  let slowDownLogs = [];
  for(const work of loadWorks){
    if(duid === work.duId){
      if(work.params.slowDownWork!==undefined && work.params.slowDownWork!==null){
        slowDownLogs = work.params.slowDownWork
      }
      break;
    }
  }
  return slowDownLogs;
}
// 添加泊位状态展开/收起功能
const isBerthStatusExpanded = ref(
  localStorage.getItem("berthStatusExpanded") !== "false"
);
// 新增统一收起/展开功能
const isAllSectionsExpanded = ref(
  localStorage.getItem("allSectionsExpanded") !== "false"
);

const toggleBerthStatus = () => {
  isBerthStatusExpanded.value = !isBerthStatusExpanded.value;
  localStorage.setItem(
    "berthStatusExpanded",
    isBerthStatusExpanded.value.toString()
  );
};

const toggleAllSections = () => {
  isAllSectionsExpanded.value = !isAllSectionsExpanded.value;
  localStorage.setItem(
    "allSectionsExpanded",
    isAllSectionsExpanded.value.toString()
  );
  // 组件隐藏/显示后，延迟刷新地图
  setTimeout(() => {
    const mapElement = document.getElementById("map");
    if (mapElement) {
      mapElement.dispatchEvent(new Event("resize"));
    }
  }, 600); // 等待动画完成（500ms）+缓冲
};

const websocketStore = useWebSocketStore();

let refreshIntervalId: number | undefined;
const clearRef = () => {
  berthListRef.value = null;
  waitListRef.value = null;
  berthEchatRef.value = null;
  planEchatRef.value = null;
  BigPeriRef.value = null;
  BargeListRef.value = null;
};

onMounted(() => {
  console.log("页面已加载，准备初始化 WebSocket");
  websocketStore.initWebSocket();
  initializeMap();
  getDeptId(Number(userStore.userId)).then((res)=>{
    activeDeptId.value = res.data;
  })
});

onUnmounted(() => {
  websocketStore.closeWebSocket();
  clearRef();
  if (refreshIntervalId) {
    clearInterval(refreshIntervalId);
  }
});

defineOptions({
  name: "harborView"
});
const activeDeptId = ref(null);
const changeDept = (deptId)=>{
  websocketStore.sendMessage({deptId:deptId}, {
    onSuccess: () => {},
    onError: error => {
      ElMessage.error(error.message);
    }
  });
  localStorage.setItem("activeDeptId", deptId);
  activeDeptId.value = deptId;
  setTimeout(() => {
    for (const item of websocketStore.screen_dockPiers) {
      if (item.deptId === deptId) {
        const point = JSON.parse(item.pierGeoJson).geometry.coordinates[0][1];
        customMapArea([point[1],point[0]]);
        break;
      }
    }
  }, 1000);
}
</script>

<style scoped lang="less">
.main {
  position: relative;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

#map {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.isFullTitle {
  font-size: 1.5rem !important;
}

.bg-2 {
  background-color: rgba(#005f99, 0.6);
  backdrop-filter: blur(1px);
}

.bg-img {
  background-image: url("@/assets/images/kuang.png");
  background-size: 100% 100%;
  background-repeat: no-repeat;
  backdrop-filter: blur(2px);
}

.header {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  background: rgba(#005f99, 0.6);
  z-index: 10;
  &::before {
    content: "";
    display: block;
    width: 100%;
    height: 90%;
    position: absolute;
    top: 0;
    right: 0;
    background-size: 100% 90%;
  }
  .title {
    font-size: 1.6rem;
    color: #ffffff;
    font-weight: bold;
    line-height: 70px;
    text-align: center;
    background-image: url("@/assets/images/top.png");
    background-size: 100% 100%;
    backdrop-filter: blur(1px);
  }
  .area-tabs {
    position: absolute;
    left: 20px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    z-index: 11;
    .area-tab {
      padding: 5px 15px;
      margin-right: 10px;
      color: #fff;
      cursor: pointer;
      background-color: rgba(#003a5f, 0.5);
      border-radius: 4px;
      transition: all 0.3s ease;
      font-weight: 500;
      position: relative;
      overflow: hidden;
    }
    .area-tab.active {
      background-color: rgba(0, 200, 0, 0.8);
      box-shadow: 0 0 10px rgba(0, 200, 0, 0.6);
    }
  }
}

@keyframes tabHighlight {
  0% {
    width: 0;
    left: 0;
  }
  100% {
    width: 100%;
    left: 0;
  }
}

/* 泊位状态按钮样式 */
.berth-status-toggle {
  position: absolute;
  right: 10%;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 11;
  color: #ffffff;
  transition: all 0.3s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  &:hover {
    background-color: rgba(0, 95, 153, 1);
  }
  .toggle-icon {
    font-size: 24px;
  }
}

/* 统一收起/展开按钮样式 */
.all-sections-toggle {
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  color: #ffffff;
  transition: all 0.3s;
  margin-right: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  &:hover {
    background-color: rgba(0, 95, 153, 1);
  }
  .toggle-icon {
    font-size: 35px;
  }
}

/* 泊位状态组件动画 */
.berthEchat {
  position: absolute;
  width: 230px;
  height: 200px;
  top: 76px;
  left: 20.5%;
  padding-bottom: 6px;
  z-index: 10;
  transition:
    transform 0.5s ease,
    opacity 0.3s ease;
}

.berth-hidden {
  transform: translateX(-100%);
  opacity: 0;
}

/* 其他区域动画 */
.section-hidden {
  transform: translateY(100%);
  opacity: 0;
  transition:
    transform 0.5s ease,
    opacity 0.3s ease;
  pointer-events: none; /* 隐藏时禁用鼠标事件 */
}

/* 其他区域动画 */
.section-hidden_150 {
  transform: translateY(150%);
  opacity: 0;
  transition:
    transform 0.5s ease,
    opacity 0.3s ease;
  pointer-events: none; /* 隐藏时禁用鼠标事件 */
}

.left,
.right {
  position: absolute;
  top: 75px;
  color: #ffffff;
  z-index: 10;
  transition:
    transform 0.5s ease,
    opacity 0.3s ease;
}

.right {
  right: 0;
  height: calc(100% - 9%);
  width: 37%;
  min-width: 300px;
}

.left {
  left: 5px;
  height: 59vh;
  padding-bottom: 10px;
  width: 20%;
  min-width: 300px;
  border-radius: 0 10px 10px 0;
  display: flex;
  flex-direction: column;
  .title {
    color: #ffffff;
    padding-top: 5px;
    font-size: 18px;
    font-weight: 600;
    z-index: 11;
    text-align: center;
  }
  .berthList {
    flex: 1;
    overflow-y: auto;
    padding-bottom: 10px;
  }
}

.bottom {
  height: calc(34% - 70px);
  position: absolute;
  width: 67%;
  bottom: 0;
  left: 0;
  z-index: 10;
  color: #ffffff;
  display: flex;
  overflow-y: hidden;
  overflow-x: auto;
  transition:
    transform 0.5s ease,
    opacity 0.3s ease;
  .waitList {
    flex: 1;
    padding: 5px 5px;
    width: 100%;
    min-width: 400px;
    display: flex;
    flex-direction: column;
    margin-right: 77px;
  }
}

.fullscreen {
  position: absolute;
  right: 5%;
  top: 2%;
  display: flex;
  align-items: center;
  font-size: 35px;
  z-index: 20;
  color: #ffffff;
  font-weight: bold;
  .time_info {
    text-align: right;
    font-size: 1.1rem;
    margin-right: 10px;
    display: flex;
    flex-direction: column;
    .week {
      text-align: center;
      margin-left: 10px;
    }
  }
}

.fullscreen-container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(1);
  width: 100% !important;
  height: 100%;
  background-color: rgba(#000000, 0.6);
  z-index: 1004;
  border-radius: 20px;
  padding: 24px;
  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  overflow: hidden;
  background-image: none;
  margin: 0 !important;
}

.title-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .view-toggle {
    font-size: 20px;
    margin-left: 10px;
    font-weight: bold;
    border-radius: 4px;
    padding: 2px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
    &:hover {
      background-color: rgba(0, 95, 153, 1);
    }
  }
  .title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: bold;
  }
}

.icon {
  margin-right: 10px;
  font-size: 22px;
  cursor: pointer;
}

.justify-end {
  justify-content: flex-end;
}

.fullscreen-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(1px);
  z-index: 777;
}

.area-content {
  height: 100%;
  & > div {
    height: 100%;
  }
}
</style>
<style>
.fullscreen-mode {
  width: 100% !important;
  height: 100% !important;
  z-index: 2002;
  position: fixed;
  inset: 0;
}

/* 确保Leaflet弹窗优先级高于其他组件 */
.leaflet-popup {
  z-index: 1000 !important;
}
</style>