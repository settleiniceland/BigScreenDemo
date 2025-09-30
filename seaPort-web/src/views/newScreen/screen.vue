<template>
  <div class="screen-container"
    @click="handlePortClick"
    ref="bgRef"
    :style="{ backgroundImage: `url(${showImg[selectBut]})` }">
    <button class="fullscreen-btn" @click="toggleFullscreen">
      {{ isFullscreen ? "退出全屏" : "全屏" }}
    </button>
    <!-- 👇👇👇测试👇👇👇 -->
    <div v-if="testX!==undefined && testY!==undefined" 
      :style="{ backgroundImage: `url(${ding})`,
        left:`calc(${testX * 100}% - 4vh)`, 
        top:`calc(${testY * 100}% - 5vh)`,
        border: isOpenBorderDing ? '1px solid red' : 'none'
      }" class="ding-item">
      <span class="ding-label">basterd</span>
    </div>
    <!-- 👆👆👆测试👆👆👆 -->
    <div v-if="berthCoordinate!==undefined" 
      v-for="ber in berths"
      :key="ber.berthId"
      class="ding-item"
      @click="clickBerth(ber.berthCode)"
      :style="{
        backgroundImage: `url(${selectBerth === ber.berthCode?selectDing:ding})`,
        left:`calc(${berthCoordinate.get(ber.berthCode).x * 100}% - 4vh)`,
        top:`calc(${berthCoordinate.get(ber.berthCode).y * 100}% - 5vh)`,
        border: isOpenBorderDing ? '1px solid red' : 'none'
      }">
      <span class="ding-label">{{ber.berthCode}}</span>
    </div>
    <div class="mianData">
      <div class="left">
        <div class="btn-group">
          <div
            v-for="item in deptButton"
            :key="item.deptId"
            class="btn-item"
            :style="{ backgroundImage: `url(${selectBut === item.deptId?selectDept:normalDept})` }"
            @click="choseDept(item.deptId)">
            <span class="btn-label">{{ item.deptName }}</span>
          </div>
        </div>
      </div>
      <div class="right">
        <div class="bg bg1" :style="{ backgroundImage: `url(${data1Bg})` }">
          <div :style="{ backgroundImage: `url(${staticsBg})`}" class="sub-box">
            <div class="sub-box-data" v-if="statisticsData!==undefined">{{ statisticsData.yesterdayThrouhPut }}</div>
            <div class="sub-box-title">昨日累计吞吐量</div>
          </div>
          <div :style="{ backgroundImage: `url(${staticsBg})`}" class="sub-box">
            <div class="sub-box-data" v-if="statisticsData!==undefined">{{ statisticsData.yearThroughPut }}</div>
            <div class="sub-box-title">年度累计吞吐量</div>
          </div>
          <div :style="{ backgroundImage: `url(${staticsBg})`}" class="sub-box">
            <div class="sub-box-data" v-if="statisticsData!==undefined">{{ statisticsData.monthThroughPut }}</div>
            <div class="sub-box-title">本月累计吞吐量</div>
          </div>
           <!-- 饼图 -->
          <div class="chart-box">
            <div ref="chartRef" style="width: 100%; height: 100%;"></div>
          </div>
          <!-- 右侧说明 -->
          <div class="legend-box">
            <div
              v-for="(item, index) in chartData"
              :key="item.name"
              class="flex items-center gap-2">
              <div
                class="w-4 h-4 rounded"
                :style="{ backgroundColor: colors[index % colors.length] }"
              ></div>
              <span>{{ item.name }}：{{ item.value }}</span>
            </div>
          </div>
        </div>
        <div class="bg bg2" :style="{ backgroundImage: `url(${data2Bg})` }">
          <div class="btn-group-bg2">
            <div v-for="item in planDict"
              :key="item.key"
              class="btn-item-bg2"
              :style="{backgroundImage: `url(${plan1Chose===item.key?selectDept:normalDept})`}"
              @click="chosePlan1(item.key)">
              <span class="btn-label-bg2">{{ item.value }}<span v-if="plan1Chose===item.key" style="color: aqua;">&nbsp;{{ plan1ChoseData.length }}</span> </span>
            </div>
          </div>
          <div class="content-wrapper">
            <div
              v-for="obj in plan1ChoseData"
              :key="obj.id"
              class="content-card"
              :style="{ backgroundImage: `url(${p1PB})` }">
                <span class="card-text" :title="obj.hbName">{{ obj.hbName }}</span>
                <span class="card-text" :title="obj.materialName">{{ obj.materialName }}</span>
            </div>
          </div>
        </div>
        <div class="bg bg3" :style="{ backgroundImage: `url(${data3Bg})` }">
          <div class="content-box"
              ref="tableContainer3" 
              @mouseenter="pauseScroll3"
              @mouseleave="resumeScroll3">
            <table class="custom-table">
              <thead>
                <tr>
                  <th>泊位</th>
                  <th>船名</th>
                  <th>滞期费</th>
                  <th>物料</th>
                  <th>客户</th>
                  <th>作业量</th>
                  <th>进度</th>
                  <th>效率</th>
                  <th>空窗期日志&更改日志</th>
                  <th>装卸货单</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in plan3DataRightMiddle" :key="index">
                  <td v-if="row.rowspan > 0" :rowspan="row.rowspan">
                    <span :title="row.hbName">{{ row.hbName }}</span>
                  </td>
                  <td v-if="row.rowspan > 0" :rowspan="row.rowspan" class="ship-cell">
                    <span :title="row.shipName">{{ row.shipName }}</span>
                  </td>
                  <td v-if="row.rowspan > 0" :rowspan="row.rowspan">
                    <span :title="row.collectFee">{{ row.collectFee }}</span>
                  </td>
                  <td><span :title="row.materialName">{{ row.materialName }}</span></td>
                  <td><span :title="row.usageUnit">{{ row.usageUnit }}</span></td>
                  <td><span :title="row.progressDetail">{{ row.progressDetail }}</span></td>
                  <td class="progress-cell">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: (row.progress>100?100:row.progress) + '%' }"></div>
                      <span class="progress-text">{{ row.progress }}%</span>
                    </div>
                  </td>
                  <td><span :title="row.efficiency">{{ row.efficiency }}</span></td>
                  <td v-if="row.rowspan > 0" :rowspan="row.rowspan">
                    <button class="link-btn" @click="handleWindowUpdateLogDetail(row.windowPeriodList,row.updateLogs,row.hbName,row.shipName,row.materialName,row.usageUnit)">查看详情</button>
                  </td>
                  <td>
                    <button class="link-btn" @click="handleLoadingDetail(row.unloadWorkList,row.hbName,row.shipName,row.materialName,row.usageUnit)">查看详情</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="bg bg4" :style="{ backgroundImage: `url(${data4Bg})` }">
          <div class="table-container"
              ref="tableContainer4" 
              @mouseenter="pauseScroll4"
              @mouseleave="resumeScroll4">
            <table class="custom-table">
              <thead>
                <tr>
                  <th style="width: 50px">序号</th>
                  <th>泊位</th>
                  <th>船名</th>
                  <th>物资</th>
                  <th>计划重量(吨)</th>
                  <th>到港时间</th>
                  <th>计划靠泊时间</th>
                  <th>等靠偏差</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, idx) in plan2" :key="idx">
                  <td>{{ idx + 1 }}</td>
                  <td>{{ item.hbName }}</td>
                  <td>{{ item.shipName }}</td>
                  <td>{{ item.materialName }}</td>
                  <td>{{ item.planTonnage }}</td>
                  <td>{{ item.arrivalTime }}</td>
                  <td>{{ item.planDockingTime }}</td>
                  <td>
                    <span :style="{ color: getTimeDiffColor(item.planDockingTime) }">
                      {{ formatTimeDiff(item.planDockingTime) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    <popWindow ref="popupRef" :handleShowMoreDetail="interiorHandleShowMoreDetail" :cancelSelectDing="cancelSelectDing" :closeSubItem="closeSubItem"/>
    <popLoadingLedger ref="popLoadRef" :handleStopSlowDetail="interiorHandleStopSlowDetail" :closeSubItemStopSlowForLoad="closeSubItemStopSlowForLoad"/>
    <popWinUpdLedger ref="popWinUpdRef"/>
    <popStopSlowLedger ref="popStopSlowRef"/>
    <popShowMoreLedger ref="popShowMoreRef"/>
  </div>
</template>
<script setup>
import ding from "@/assets/newScreen/ding.png"
import selectDing from "@/assets/newScreen/selectDing.png"
import data2Bg from "@/assets/newScreen/data2Bg.png"
import data1Bg from "@/assets/newScreen/data1Bg.png"
import data3Bg from "@/assets/newScreen/data3Bg.png"
import data4Bg from "@/assets/newScreen/data4Bg.png"
import normalDept from "@/assets/newScreen/normalDept.png"
import selectDept from "@/assets/newScreen/selectDept.png"
import staticsBg from "@/assets/newScreen/staticsBg.png"
import p1PB from "@/assets/newScreen/plan1PropertyBg.png"
import {showImg,berth1Coordinate,berth2Coordinate,berth3Coordinate,
  statusMap,colors,planDict,cycleRefulshSecond} from "./data.js"
import {isOpenTestDing,isOpenBorderDing} from "./test.js"
import popWindow from "./popWindow.vue"
import popLoadingLedger from "./popLoadingLedger.vue"
import popWinUpdLedger from "./popWinUpdLedger.vue"
import popStopSlowLedger from "./popStopSlowLedger.vue"
import popShowMoreLedger from "./popShowMoreLedger.vue"
import {allDept,getBerchs,getPopData,getStatistics,getArriveLeavingPlan,
  getPlan2ByDeptId,getOldPlan3} from "@/api/newScreen/index.ts"
import {ref,onMounted,computed,watchEffect,onUnmounted,nextTick} from "vue"
import * as echarts from "echarts";
const isFullscreen = ref(false);
const bgRef = ref(undefined)
const testX = ref(undefined)//TODO 测试
const testY = ref(undefined)//TODO 测试
const popupRef = ref(undefined);
const popLoadRef = ref(undefined);
const popWinUpdRef = ref(undefined);
const popStopSlowRef = ref(undefined);
const popShowMoreRef = ref(undefined);
const deptButton = ref([])
const selectBut = ref()//选中部门
const selectBerth = ref()//选中泊位
const berths = ref([])//所有泊位
const berthCoordinate = ref(undefined)//泊位坐标map
const statisticsData = ref(undefined)//吞吐量统计
const plan1 = ref([])//计划1，所有今日到船，明日到船，今日离泊，明日离泊的数据
const plan1Chose = ref(1)//选中的计划1类型
const plan1ChoseData = ref([])//选中的计划1数据
const plan2 = ref([])//计划2，所有等泊的计划
const plan3=ref([])//计划3（就是老计划3，超大对象）
const chartRef = ref(null);
let chart = null;
let timer = null;
//3 4 数据栏自动滑动
const tableContainer3 = ref(null)
let scrollTimer3 = null
let isPaused3 = false
const tableContainer4 = ref(null)
let scrollTimer4 = null
let isPaused4 = false
const startAutoScroll3 = () => {// 自动滚动
  if (!tableContainer3.value) return
  const el = tableContainer3.value
  scrollTimer3 = setInterval(() => {
    if (isPaused3) return   // 如果暂停，就不滚动
    if (el.scrollTop + el.clientHeight >= el.scrollHeight) {// 如果到达底部
      clearInterval(scrollTimer3)
      setTimeout(() => {
        el.scrollTop = 0   // 回到顶部
        setTimeout(()=>{
          startAutoScroll3()// 继续滚动
        },1000)
      }, 1000) // 停 1 秒
    } else {
      el.scrollTop += 1   // 每次下滑 1px
    }
  }, 50) // 调整滚动速度（越小越快）
}
const pauseScroll3 = () => {// 暂停/恢复
  isPaused3 = true
}
const resumeScroll3 = () => {
  isPaused3 = false
}
const startAutoScroll4 = () => {// 自动滚动
  if (!tableContainer4.value) return
  const el = tableContainer4.value
  scrollTimer4 = setInterval(() => {
    if (isPaused4) return   // 如果暂停，就不滚动
    if (el.scrollTop + el.clientHeight >= el.scrollHeight) {// 如果到达底部
      clearInterval(scrollTimer4)
      setTimeout(() => {
        el.scrollTop = 0   // 回到顶部
        setTimeout(()=>{
          startAutoScroll4()// 继续滚动
        },1000)
      }, 1000) // 停 1 秒
    } else {
      el.scrollTop += 1   // 每次下滑 1px
    }
  }, 50) // 调整滚动速度（越小越快）
}
const pauseScroll4 = () => {// 暂停/恢复
  isPaused4 = true
}
const resumeScroll4 = () => {
  isPaused4 = false
}
onMounted(async()=>{
  console.log("screen主DOM重新加载")
  await getAllDept();
  await realTimeExecuteMethod();
  chart = echarts.init(chartRef.value);
  //定时任务刷新
  timer = setInterval(realTimeExecuteMethod,cycleRefulshSecond*1000);
  //全屏绑定F11
  window.addEventListener("keydown", handleF11);
  document.addEventListener("fullscreenchange", fullscreenChangeHandler);
  //自动下滑
  startAutoScroll3();
  startAutoScroll4();
   // 用 watchEffect 自动更新
  watchEffect(() => {
    if (!chart) {
      return;
    }
    chart.setOption({
      tooltip: { trigger: "item" },
      series: [
        {
          type: "pie",
          radius: "100%",
          data: chartData.value.map((d, i) => ({
            ...d,
            itemStyle: { color: colors[i % colors.length] }
          })),
          label: { show: false }
        }
      ]
    })
  })
})
onUnmounted(() => {// 离开页面时销毁定时器
  console.log("screen主DOM卸载")
  window.removeEventListener("keydown", handleF11);
  document.removeEventListener("fullscreenchange", fullscreenChangeHandler);
  clearInterval(scrollTimer3)
  clearInterval(scrollTimer4)
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});
const getAllDept=async ()=>{//获取所有部门，无需定时查
  const res = await allDept();
  deptButton.value = res.rows;
  selectBut.value = deptButton.value[0].deptId;
}
const getAllPorts=async ()=>{//获取所有泊位，需要定时查★★★
  const res = await getBerchs(selectBut.value);
  berths.value = res.rows;
}
const getStatisticsData=async ()=>{//获取吞吐量统计数据，需要定时查★★★
  const res = await getStatistics(selectBut.value);
  statisticsData.value = res;
}
const getPlan1=async ()=>{//获取今明相关数据，需要定时查★★★
  const res = await getArriveLeavingPlan(selectBut.value);
  plan1.value=res.rows;
  chosePlan1(plan1Chose.value)
}
const getPlan2=async ()=>{//获取所有在等泊的计划，需要定时查★★★
  const res = await getPlan2ByDeptId(selectBut.value);
  plan2.value=res.rows;
}
const getWorkingOldPlan3=async ()=>{//获取所有working计划（老plan3），需要定时查★★★
  const res = await getOldPlan3(selectBut.value);
  plan3.value=res.rows;
}
const realTimeExecuteMethod=()=>{//所有需要定时查的
  getStatisticsData();
  getAllPorts();
  getPlan1();
  getPlan2();
  getWorkingOldPlan3();
}
const chartData = computed(() => {
  return Object.values(
    berths.value.reduce((acc, item) => {
      const status = item.berthStatus;
      if (!acc[status]) {
        acc[status] = { name: statusMap[status], value: 0 };
      }
      acc[status].value += 1;
      return acc;
    }, {})
  );
});
const plan3DataRightMiddle = computed(()=>{
  let rows = [];
  plan3.value.forEach(item=>{
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
      progressDetail: "已作业"+((item.unloadWeight===undefined||item.unloadWeight===null)?0:item.unloadWeight)+"，共"+item.tonnage,
      progress: Math.round(safeDivide(item.unloadWeight,item.tonnage)),
      rowspan: rowsNum,
      id: item.id,
      loadSequence: 1,
      updateLogs: item.params.updateLogs,
      windowPeriodList: item.params.windowPeriodList,
      unloadWorkList: item.params.unloadWorkList,
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
          loadSequence: ass.loadSequence,
          updateLogs: item.params.updateLogs,
          windowPeriodList: item.params.windowPeriodList,
          unloadWorkList: ass.params.unloadWorkList,
        })
      });
    }
  });
  return rows;
});
const safeDivide= (strNum1, strNum2) => {
  const num1 = Number(strNum1)
  const num2 = Number(strNum2)
  if (isNaN(num1) || isNaN(num2) || num2 === 0) {
    return 0
  }
  return num1 / num2 * 100
}
const handlePortClick = (e)=>{//开发用工具方法（略过）
  const rect = bgRef.value.getBoundingClientRect()
  const offsetX = e.clientX - rect.left
  const offsetY = e.clientY - rect.top
  const x = offsetX / rect.width
  const y = offsetY / rect.height
  console.info("点击比例坐标：", { x, y })
  if(isOpenTestDing){
    testX.value = x
    testY.value = y
  }
}
const choseDept =async (id)=>{
  await bigReset();
  selectBut.value = id;
  if(id===103){
    berthCoordinate.value = berth2Coordinate
  }else if(id===221){
    berthCoordinate.value = berth1Coordinate
  }else if(id===222){
    berthCoordinate.value = berth3Coordinate
  }else{
    berthCoordinate.value = undefined;
  }
  await colseAllPop();
  await nextTick();
  await realTimeExecuteMethod();
  if(chartRef.value){
    echarts.dispose(chartRef.value);
  }
  chart = echarts.init(chartRef.value);
}
const bigReset =async ()=>{//疑难杂症，只能病急乱投医
  selectBut.value = 999999;
  berthCoordinate.value = undefined;
  await colseAllPop();
  await nextTick();
  await realTimeExecuteMethod();
  if(chartRef.value){
    echarts.dispose(chartRef.value);
  }
  chart = echarts.init(chartRef.value);
  await new Promise(resolve => setTimeout(resolve, 200));
}
const chosePlan1 = (id)=>{
  plan1Chose.value = id;
  switch(id){
    case 1://今日到船
      plan1ChoseData.value = []
      plan1.value.forEach((item)=>{
        if(item.outBerthTime===null && item.arrivalTime!==null && checkArrivalDate(item.arrivalTime)===1){
          plan1ChoseData.value.push(item);
        }
      })
      break;
    case 2://明日到船
      plan1ChoseData.value = []
      plan1.value.forEach((item)=>{
        if(item.outBerthTime===null && item.arrivalTime!==null && checkArrivalDate(item.arrivalTime)===2){
          plan1ChoseData.value.push(item);
        }
      })
      break;
    case 3://今日离泊
      plan1ChoseData.value = []
      plan1.value.forEach((item)=>{
        if(item.outBerthTime!==null && checkArrivalDate(item.outBerthTime)===1){
          plan1ChoseData.value.push(item);
        }
      })
      break;
    case 4://明日离泊
      plan1ChoseData.value = []
      plan1.value.forEach((item)=>{
        if(item.outBerthTime!==null && checkArrivalDate(item.outBerthTime)===2){
          plan1ChoseData.value.push(item);
        }
      })
  }
}
const checkArrivalDate=(arrivalTime)=>{// 1 今天； 2 明天
  const arrivalDate = new Date(arrivalTime.replace(/-/g, "/")); // Safari兼容
  const today = new Date();
  // 取 yyyy-mm-dd
  const format = (d) =>`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  const arrivalDay = format(arrivalDate);
  const todayDay = format(today);
  // 明天
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 1);
  const tomorrowDay = format(tomorrow);
  if (arrivalDay === todayDay) {
    return 1;//今天
  } else if (arrivalDay === tomorrowDay) {
    return 2;//明天
  } else {
    return 3;//error
  }
}
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
const interiorHandleStopSlowDetail = (dataStop,dataSlow,title) => {
  popStopSlowRef.value.open({
    x:0.23207990599294948,
    y:0.48510223601811914,
    dataStop,
    dataSlow,
    title
  })
}
const handleWindowUpdateLogDetail =async (dataWin,dataUpd,hbName,shipName,materialName,usageUnit)=>{
  const title = hbName+">>"+shipName+">>"+materialName+">>"+usageUnit;
  await closeRepetitionPops();
  popWinUpdRef.value.open({
    x: 0.02529601722282024,
    y: 0.7184133114363574,
    dataWin,
    dataUpd,
    title
  })
}
const handleLoadingDetail =async (data,hbName,shipName,materialName,usageUnit)=>{
  const title = hbName+">>"+shipName+">>"+materialName+">>"+usageUnit;
  await closeRepetitionPops();
  popLoadRef.value.open({
    x: 0.02529601722282024,
    y: 0.7184133114363574,
    data,
    title
  })
}
const interiorHandleShowMoreDetail =async (data,title)=>{
  await closeRepetitionPops();
  popShowMoreRef.value.open({
    x: 0.02529601722282024,
    y: 0.7184133114363574,
    data,
    title
  })
}
const clickBerth =async (berchCode)=>{
  selectBerth.value = berchCode;
  const res =await getPopData(selectBut.value,selectBerth.value);
  popupRef.value.open({
    x: 0.20799059929494712,
    y: 0.2517911605998809,
    data: res.rows
  });
}
const toggleFullscreen = ()=>{
  if (!isFullscreen.value) {
    bgRef.value.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
}
const fullscreenChangeHandler = ()=>{
  isFullscreen.value = !!document.fullscreenElement;
}
const handleF11 = (e)=>{
  if (e.key === "F11") {
    e.preventDefault(); // 阻止浏览器默认全屏
    toggleFullscreen(); // 调用你自己的方法，让父组件全屏
  }
}
const cancelSelectDing = ()=>{
  selectBerth.value = undefined;
}
const colseAllPop = ()=>{
  popupRef.value.close();
	popLoadRef.value.close();
	popWinUpdRef.value.close();
	// popStopSlowRef.value.close();
	// popShowMoreRef.value.close();
}
const closeSubItem = ()=>{
  popShowMoreRef.value.close();
}
const closeSubItemStopSlowForLoad=()=>{
  popStopSlowRef.value.close();
}
const closeRepetitionPops=()=>{
  popLoadRef.value.close();
  popWinUpdRef.value.close();
  popShowMoreRef.value.close();
}
</script>
<style lang="css" scoped>
.screen-container {
  position: relative;
  width: 100vw;
  height: 90vh;
  overflow: hidden;
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 0;
}
.buttonTest {
  position: absolute;
  z-index: 1;
}
.ding-item {
  width: 8vh;
  height: 5.89vh;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  align-items: flex-start;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
  cursor: pointer;
  z-index: 3;
}
.ding-label {
  margin-top: 0.35vh;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  z-index: 2;
}
.btn-group {
  display: flex;
  gap: 12px;
  justify-content: center; /* 居中放置 */
  margin-top: 20px;
}
.btn-item {
  top: 5vh;
  width: 10vh;
  height: 4vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background-repeat: no-repeat;
  background-position: center;
  background-size: 100% 100%;
  cursor: pointer;
  z-index: 1;
}
.btn-label {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  z-index: 2; /* 保证文字在最上层 */
}
.mianData{
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  display: flex; 
  .left {
    flex: 1;
  }
  .right {
    flex: 1;
    display: grid;
    grid-template-rows: 7% 25% 35% 33%;/* 高度比例*/
    grid-template-columns: 1fr 1fr;   /* 上面两块平分 */
    gap: 7px;                        /* 每块之间的间距 */
    .bg {
      display: flex;
      align-items: flex-start;
      justify-content: center;
      font-weight: bold;
      background-size: 98% 100%;
      background-position: left;
      background-repeat: no-repeat;
    }
    .bg1 {
      grid-row: 2;
      grid-column: 1;
      display: grid;
      grid-template-columns: repeat(3, 1fr); /* 3等分 */
      grid-template-rows: auto auto;
      .sub-box {
        background-size: 100% 100%;
        background-repeat: no-repeat;
        margin-top: 3vh;
        margin-left: 1vh;
        margin-right: 2vh;
        height: 5vh;
        z-index: 3;
        color: #f0f0ed;
        .sub-box-data {
          font-size: 2vh;
          margin-left: 0.5vh;
        }
        .sub-box-title {
          font-size: 1vh;
          margin-left: 0.5vh;
          margin-top: 0.5vh;
        }
      }
      .chart-box {
        grid-column: span 1; /* 占两份 */
      }
      .legend-box {
        grid-column: span 2; /* 占一份 */
        display: flex;
        color: #ffffff;
        flex-direction: column;
        justify-content: center;
        margin-top: 2vh;
      }
    }
    .bg2 {
      grid-row: 2;
      grid-column: 2;
      margin-right: 1vh;
      display: flex;              /* 竖直布局 */
      flex-direction: column;     /* 上下排列 */
      align-items: center;        /* 居中对齐 */
      .btn-group-bg2 {
        display: flex;
        gap: 2vh;
        justify-content: center; /* 居中放置 */
        margin-top: 2vh;
      }
      .btn-item-bg2 {
        top: 1vh;
        width: 8vh;
        height: 3vh;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100% 100%;
        cursor: pointer;
        z-index: 1;
      }
      .btn-label-bg2 {
        color: #fff;
        font-size: 1.3vh;
        font-weight: bold;
        z-index: 2; /* 保证文字在最上层 */
      }
      .content-wrapper {
        margin-top: 2vh;
        gap: 0.5vh 1vh;
        flex-basis: 100%;
        display: grid;
        height: 20vh;
        grid-template-columns: repeat(2, 1fr); /* 每行2个 */
        grid-auto-rows: 2.89vh;
        overflow-y: auto;                       /* 可下拉 */
        scrollbar-width: none;  /* Firefox 隐藏滚动条 */
      }
      /* Chrome/Edge 隐藏滚动条 */
      .content-wrapper::-webkit-scrollbar {
        display: none;
      }
      .content-card {
        width: 18.4vh;
        height: 2.5vh;
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100% 100%;
        border: 1px dashed rgba(255, 255, 255, 0.5);
        display: flex;
        flex-direction: row;
        align-items: center;
        color: white;
        font-size: 1.2vh;
        cursor: pointer;
        z-index: 2;
        .card-text {
          flex: 1;
          max-width: 50%;                /* 每侧最多占一半 */
          overflow: hidden;              /* 超出隐藏 */
          white-space: nowrap;           /* 不换行 */
          text-overflow: ellipsis;       /* 省略号 */
          text-align: center;
        }
      }
    }
    .bg3 {
      grid-row: 3;
      grid-column: 1 / span 2; /* 占满两列 */
    }
    .bg4 {
      grid-row: 4;
      grid-column: 1 / span 2; /* 占满两列 */
      margin-bottom: 5vh;
    }
  }
}
.table-container {
  margin-top: 4vh;
  max-height: 19vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  width: 96%;
  font-size: 1.2vh;
}
/* Chrome Safari 隐藏滚动条 */
.table-container::-webkit-scrollbar {
  display: none; 
}
.custom-table {
  width: 100%;
  border-collapse: collapse;
  color: white; /* 字体白色 */
}

.custom-table th,
.custom-table td {
  border: 1px solid rgba(2, 176, 219, 0.589); /* 半透明白色边框 */
  padding: 8px;
  text-align: center;
}

.custom-table thead th {
  position: sticky;
  top: 0;
  background: rgba(17, 137, 167, 0.83); /* 表头固定时加点背景 */
  z-index: 2;
}
.content-box {
  margin-top: 4vh;
  max-height: 27vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  width: 96%;
  font-size: 1.2vh;
}
.ship-cell {
  max-width: 12vh;       /* 限制宽度 */
  overflow: hidden;     /* 超出隐藏 */
  white-space: nowrap;  /* 不换行 */
  text-overflow: ellipsis; /* 超出用...显示 */
}
.progress-cell {
  width: 12vh;       /* 固定单元格宽度 */
}
.progress-bar {
  position: relative;
  width: 100%;
  height: 14px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 7px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #b8ff14;
  border-radius: 7px;
}
.progress-text {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: rgb(228, 94, 94);
  font-weight: bold;
}
.link-btn {
  width: 5vh;
  background: none;       /* 去掉背景 */
  border: none;           /* 去掉边框 */
  color: rgb(5, 148, 148);            /* 文字颜色 */
  cursor: pointer;        /* 鼠标悬停变小手 */
  text-decoration: underline; /* 下划线，像链接 */
  padding: 0;             /* 去掉默认内边距 */
  font-size: inherit;     /* 跟随表格字体大小 */
}
.link-btn:hover {
  color: #00ffff;         /* 悬停时颜色更亮 */
}
.fullscreen-btn {
  position: absolute;
  top: 4vh;
  right: 4vh;
  padding: 0.6vh 1.2vh;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 0.6vh;
  color: #fff;
  font-size: 1.4vh;
  cursor: pointer;
  z-index: 3;
  transition: background 0.3s;
}

.fullscreen-btn:hover {
  background: rgba(255, 255, 255, 0.4);
}
</style>