<template>
  <div ref="phoneRef" style="background-color: black;">
    <div v-if="isOverlayVisible" class="overlay"></div>
    <div
      class="knob"
      ref="knobRef"
      @mousedown="startDrag"
      @touchstart="startDrag"
      :style="{ transform: `rotate(${rotateDeg}deg)` }">
      <div class="knob-handle"></div>
      <div class="center-text">{{ Math.round(rotateDeg) }}°</div>
    </div>
    <button class="anger-adjust-convenience-btn btn1" @click="angerAdjust(0)">
      竖向
    </button>
    <button class="anger-adjust-convenience-btn btn2" @click="angerAdjust(90)">
      横向
    </button>
    <button class="fullscreen-btn" @click="toggleFullscreen">
      {{ isFullscreen ? "退出全屏" : "全屏" }}
    </button>
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
    <halfPopWorkingPlanForPhone 
      ref="halfPopWorkingPlanRef" 
      class="half-pop-rotate" 
      :style="{ '--deg': rotateDeg + 'deg' }"
      :data="plan3DataRightMiddle"
      :handleWindowUpdateLogDetail="handleWindowUpdateLogDetail"
      :handleLoadingDetail="handleLoadingDetail"/>
    <popLoadingLedger 
      ref="popLoadRef" 
      class="half-pop-rotate" 
      :style="{ '--deg': rotateDeg + 'deg' }"
      :handleStopSlowDetail="interiorHandleStopSlowDetail" 
      :closeSubItemStopSlowForLoad="interiorClosePops"
      :setOverlay="setOverlay"/>
    <popWinUpdLedger 
      ref="popWinUpdRef"
      class="half-pop-rotate" 
      :style="{ '--deg': rotateDeg + 'deg' }"
      :setOverlay="setOverlay"/>
    <popStopSlowLedger 
      ref="popStopSlowRef"
      class="half-pop-rotate" 
      :style="{ '--deg': rotateDeg + 'deg' }"
      :setOverlay="setOverlay"/>
  </div>
</template>
<script setup>
import {ref,onMounted,computed,onBeforeUnmount} from "vue"
import {allDept,getBerchs,getOldPlan3} from "@/api/newScreen/index.ts"
import normalDept from "@/assets/newScreen/normalDept.png"
import selectDept from "@/assets/newScreen/selectDept.png"
import halfPopWorkingPlanForPhone from "@/views/phoneScreen/halfPopWorkingPlanForPhone.vue"
import popLoadingLedger from "@/views/phoneScreen/popLoadingLedgerForPhone.vue"
import popWinUpdLedger from "@/views/phoneScreen/popWinUpdLedgerForPhone.vue"
import popStopSlowLedger from "@/views/phoneScreen/popStopSlowLedgerForPhone.vue"
const phoneRef = ref(undefined);
const popLoadRef = ref(undefined);
const popWinUpdRef = ref(undefined);
const halfPopWorkingPlanRef = ref(undefined);
const popStopSlowRef = ref(undefined);
const knobRef = ref(undefined)
const isFullscreen = ref(false);
const isOverlayVisible = ref(false)
const deptButton = ref([])//所有部门
const selectBut = ref()//选中部门
const berths = ref([])//该部门下所有泊位
const plan3=ref([])//计划3（就是老计划3，超大对象）
const rotateDeg = ref(60)
const isIos = () => /iphone|ipad|ipod/i.test(navigator.userAgent);
let isDragging = false
let center = { x: 0, y: 0 }
const getAllDept=async ()=>{//获取所有部门，无需定时查
  const res = await allDept();
  deptButton.value = res.rows;
  selectBut.value = deptButton.value[0].deptId;
}
const getAllPorts=async ()=>{//获取所有泊位
  const res = await getBerchs(selectBut.value);
  berths.value = res.rows;
}
const getWorkingOldPlan3=async ()=>{//获取所有working计划（老plan3）
  const res = await getOldPlan3(selectBut.value);
  plan3.value=res.rows;
}
const realTimeExecuteMethod=()=>{
  getAllPorts();
  getWorkingOldPlan3();
}
onMounted(async()=>{
  await getAllDept();
  realTimeExecuteMethod();
  halfPopWorkingPlanRef.value.open({
    x: 0,
    y: 0.1,
    title: "装卸泊位空泊位及待离港泊位(可拖动✥)"
  })
})
const plan3DataRightMiddle = computed(()=>{
  let rows = [];
  plan3.value.forEach(item=>{
    if(item.cardCount==="2"){
      rows.push({
        hbName: item.hbName,
        shipName: item.batchNumber,
        rowspan: 1,
        progress: 1,
        cardCount: "2",
      })
    }else if(item.cardCount==="1"){
      let rowsNum = 1;
      if(item.params.assistantList!==undefined && item.params.assistantList.length>0){
        rowsNum+=item.params.assistantList.length;
      }
      rows.push({
        hbName: item.hbName,
        shipName: item.shipName,
        materialName: item.materialName,
        loadType: item.remark01,
        usageUnit: item.usageUnit,
        progressDetail: item.batchNumber,
        rowspan: rowsNum,
        progress: 1,
        cardCount: "1",
      })
      if(rowsNum>1){
        item.params.assistantList.forEach(ass => {
          rows.push({
            hbName: item.hbName,
            shipName: item.shipName,
            materialName: ass.materialName,
            loadType: ass.remark01,
            usageUnit: ass.usageUnit,
            progressDetail: item.batchNumber,
            rowspan: 0,
            progress: 1,
            cardCount: "1",
          })
        });
      }
    }else{
    let rowsNum = 1;
    let roughUnloadWeight = 0;
    let assName = "";
    if(item.status!=="4" && (item.cardCount == null || item.cardCount == undefined || item.cardCount == "")){
      assName = "<br>完成";
    }
    if(item.unloadWeight!==null && item.unloadWeight!==undefined){
      roughUnloadWeight = Number(item.unloadWeight);
    }
    if(item.params.assistantList!==undefined && item.params.assistantList.length>0){
      rowsNum+=item.params.assistantList.length;
      item.params.assistantList.forEach(ass => {
        if(ass.unloadWeight!==undefined && ass.unloadWeight!==null){
          roughUnloadWeight+=Number(ass.unloadWeight);
        }
        if(ass.params.updateLogs!==undefined && ass.params.updateLogs!==null){
          item.params.updateLogs.push(...ass.params.updateLogs);
        }
      })
    }
    const start = new Date(item.operationTime.replace(" ", "T")).getTime();
    let end;
    if(item.endTime!==undefined&&item.endTime!==null&&item.endTime!==""){
      end = new Date(item.endTime.replace(" ", "T")).getTime();
    }else{
      end = Date.now();
    }
    const result = Math.round(((end - start) / 3600000) * 100) / 100;
    const roughEfficiency = Math.round(roughUnloadWeight/result*100)/100;
    rows.push({
      hbName: item.hbName+assName,
      shipName: item.shipName,
      status: item.status,
      collectFee: item.params.collectFee,
      roughEfficiency: roughEfficiency,
      materialName: item.materialName,
      loadType: item.remark01,
      usageUnit: item.usageUnit,
      efficiency: item.params.efficiency,
      progressDetail: "作业"+((item.unloadWeight===undefined||item.unloadWeight===null)?0:item.unloadWeight)+"<br>共"+item.tonnage,
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
          hbName: item.hbName+assName,
          shipName: item.shipName,
          status: item.status,
          collectFee: item.params.collectFee,
          roughEfficiency: roughEfficiency,
          materialName: ass.materialName,
          loadType: ass.remark01,
          usageUnit: ass.usageUnit,
          efficiency: ass.params.efficiency,
          progressDetail: "作业"+((ass.unloadWeight===undefined||ass.unloadWeight===null)?0:ass.unloadWeight)+"<br>共"+ass.tonnage,
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
    }
  });
  return rows;
});
const angerAdjust = (num)=>{
  rotateDeg.value=num;
}
const choseDept =async (id)=>{
  selectBut.value = id;
  realTimeExecuteMethod();
  commonClosePops();
}
const setOverlay = (visible) => {
  isOverlayVisible.value = visible
}
const interiorClosePops=()=>{}
const commonClosePops=()=>{
  popLoadRef.value.close();
  popWinUpdRef.value.close();
  popStopSlowRef.value.close();
}
const handleWindowUpdateLogDetail =async (dataWin,dataUpd,hbName,shipName,materialName,usageUnit)=>{
  const title = hbName+">>"+shipName+">>"+materialName+">>"+usageUnit;
  await commonClosePops();
  popWinUpdRef.value.open({
    x: 0,
    y: 0.3,
    dataWin,
    dataUpd,
    title
  })
}
const handleLoadingDetail =async (data,hbName,shipName,materialName,usageUnit,loadType)=>{
  const title = hbName+">>"+shipName+">>"+materialName+">>"+(loadType ? (loadType.match(/[\u4e00-\u9fa5]{1,2}/)?.[0] || "") : "")+">>"+usageUnit;
  await commonClosePops();
  popLoadRef.value.open({
    x: 0,
    y: 0.3,
    data,
    title,
    materialName,
  })
}
const interiorHandleStopSlowDetail =async (dataStop,dataSlow,title) => {
  await commonClosePops();
  popStopSlowRef.value.open({
    x:0,
    y:0.3,
    dataStop,
    dataSlow,
    title
  })
}
const safeDivide= (strNum1, strNum2) => {
  const num1 = Number(strNum1)
  const num2 = Number(strNum2)
  if (isNaN(num1) || isNaN(num2) || num2 === 0) {
    return 0
  }
  return num1 / num2 * 100
}
const toggleFullscreen = ()=>{
  const el = phoneRef.value;
  if (isIos()) {
    isFullscreen.value = !isFullscreen.value;
  } else {
    if (!isFullscreen.value) {
      if (el.requestFullscreen) el.requestFullscreen();
      else if (el.webkitRequestFullscreen) el.webkitRequestFullscreen(); // Safari / Android
      else if (el.msRequestFullscreen) el.msRequestFullscreen(); // IE/Edge
      isFullscreen.value = true;
    } else {
      if (document.exitFullscreen) document.exitFullscreen();
      else if (document.webkitExitFullscreen) document.webkitExitFullscreen();
      else if (document.msExitFullscreen) document.msExitFullscreen();
      isFullscreen.value = false;
    }
  }
}
const startDrag = (e) => {
  isDragging = true
  const rect = knobRef.value.getBoundingClientRect()
  center = { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 }
  window.addEventListener('mousemove', handleDrag)
  window.addEventListener('mouseup', stopDrag)
  window.addEventListener('touchmove', handleDrag)
  window.addEventListener('touchend', stopDrag)
}
const handleDrag = (e) => {
  if (!isDragging) return
  const point = e.touches ? e.touches[0] : e
  const dx = point.clientX - center.x
  const dy = point.clientY - center.y
  const angle = Math.atan2(dy, dx) * (180 / Math.PI)
  rotateDeg.value = angle + 90 // 使 0° 在上方
}
const stopDrag = () => {
  isDragging = false
  window.removeEventListener('mousemove', handleDrag)
  window.removeEventListener('mouseup', stopDrag)
  window.removeEventListener('touchmove', handleDrag)
  window.removeEventListener('touchend', stopDrag)
}
onBeforeUnmount(stopDrag)
</script>
<style lang="css" scoped>
.btn-group {
  position: fixed;
  bottom: 2vh;       /* 距离底部 15% 视口高度 */
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  justify-content: center;
}
.btn-item {
  width: 10vh;
  height: 5vh;
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
  font-size: 15px;
  font-weight: bold;
  z-index: 2; /* 保证文字在最上层 */
}
.knob {
  top: 4vh;
  left: 1vh;
  width: 10vh;
  height: 10vh;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #8ec5fc, #4f8aff);
  box-shadow: inset -4px -4px 10px rgba(0, 0, 0, 0.2),
              6px 6px 15px rgba(0, 0, 0, 0.15);
  position: relative;
  transition: transform 0.1s linear;
  user-select: none;
  touch-action: none;
  z-index: 17;
}
.knob-handle {
  position: absolute;
  top: 10px;
  left: 50%;
  width: 10px;
  height: 20px;
  background: rgb(118, 248, 12);
  border-radius: 5px;
  transform: translateX(-50%);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}
.center-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 22px;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}
.anger-adjust-convenience-btn {
  position: absolute;
  top: 4vh;
  padding: 0.6vh 1.2vh;
  background: transparent;
  border: 1px solid rgb(0, 0, 0);
  border-radius: 0.6vh;
  color: #65fc00;
  font-size: 1.4vh;
  cursor: pointer;
  z-index: 17;
  transition: background 0.3s;
}
.anger-adjust-convenience-btn.btn1{
  left: 13vh;
}
.anger-adjust-convenience-btn.btn2{
  left: 23vh;
}
.fullscreen-btn {
  position: absolute;
  top: 4vh;
  right: 4vh;
  padding: 0.6vh 1.2vh;
  background: transparent;
  border: 1px solid rgb(0, 0, 0);
  border-radius: 0.6vh;
  color: #65fc00;
  font-size: 1.4vh;
  cursor: pointer;
  z-index: 17;
  transition: background 0.3s;
}
/* 在 scoped 或全局样式中加 */
.half-pop-rotate {
  transform: rotate(var(--deg));
  transform-origin: center top;
  position: absolute;
}
.overlay {
  position: fixed;
  inset: 0; /* 等价于 top/right/bottom/left: 0 */
  background: rgba(0, 0, 0, 0.4); /* 半透明黑色 */
  z-index: 5; /* 要比普通内容高，比弹窗低 */
  backdrop-filter: blur(2px); /* 可选：让背景模糊 */
  pointer-events: auto; /* 拦截点击 */
  touch-action: none;   /* ✅ 禁止手势滚动 */
}
</style>