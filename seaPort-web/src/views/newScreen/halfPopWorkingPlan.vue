<template>
  <div
    v-show="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${data3Bg})`
    }"@mousedown="startDrag">
    <div class="popup-header">
      <div class="status-group">
        <div class="content-title">{{ title }}</div>
      </div>
      <div class="action-group">
        <!-- <button class="close-btn" @click.stop="testItem">查看数据</button> -->
      </div>
    </div>
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
            <th>粗略效率</th>
            <th>物料</th>
            <th>装卸</th>
            <th>客户</th>
            <th>作业量</th>
            <th>进度</th>
            <th>效率</th>
            <th>日志</th>
            <th>装卸单</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in props.data" :key="index" :style="{backgroundColor: getBG(row)}">
            <td v-if="getVIf('2-1','1-1',row,1)" v-bind="getVBind('2-1','1-1',row,1)" style="max-width: 6.7vh;">
              <span :title="row.hbName" v-html="row.hbName" class="commen-style"></span>
            </td>
            <td v-if="getVIf('2-2','1-2',row,3)" v-bind="getVBind('2-2','1-2',row,3)" class="ship-cell">
              <span :title="row.shipName" v-html="row.shipName" class="commen-style"></span>
            </td>
            <td v-if="getVIf('2-3','1-3',row,0)" v-bind="getVBind('2-3','1-3',row,0)">
              <span :title="row.collectFee" v-html="row.collectFee"></span>
            </td>
            <td v-if="getVIf('2-3','1-3',row,0)" v-bind="getVBind('2-3','1-3',row,0)">
              <span :title="row.roughEfficiency" v-html="row.roughEfficiency"></span>
            </td>
            <td v-if="getVIf('2-3','1-4',row,1)" v-bind="getVBind('2-3','1-4',row,1)">
              <span :title="row.materialName" v-html="row.materialName" class="commen-style"></span>
            </td>
            <td class="ship-cell-mini" v-if="getVIf('2-3','1-4',row,1)" v-bind="getVBind('2-3','1-4',row,1)">
              <span :title="row.loadType" v-html="row.loadType" class="commen-style"></span>
            </td>
            <td class="ship-cell" v-if="getVIf('2-3','1-4',row,1)" v-bind="getVBind('2-3','1-4',row,1)">
              <span :title="row.usageUnit" v-html="row.usageUnit" class="commen-style"></span>
            </td>
            <td v-if="getVIf('2-3','1-5',row,5)" v-bind="getVBind('2-3','1-5',row,5)">
              <span :title="row.progressDetail" v-html="row.progressDetail" class="commen-style"></span>
            </td>
            <td class="progress-cell" v-if="getVIf('2-3','1-6',row,0)" v-bind="getVBind('2-3','1-6',row,0)">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: (row.progress>100?100:row.progress) + '%' }"></div>
                <span class="progress-text">{{ row.progress }}%</span>
              </div>
            </td>
            <td v-if="getVIf('2-3','1-6',row,0)" v-bind="getVBind('2-3','1-6',row,0)">
              <span :title="row.efficiency" v-html="row.efficiency" class="commen-style"></span>
            </td>
            <td v-if="getVIf('2-3','1-7',row,0)" v-bind="getVBind('2-3','1-7',row,0)">
              <button class="link-btn" @click="handleWindowUpdateLogDetail(row.windowPeriodList,row.updateLogs,row.hbName,row.shipName,row.materialName,row.usageUnit)">查看</button>
            </td>
            <td v-if="getVIf('2-3','1-6',row,0)" v-bind="getVBind('2-3','1-6',row,0)">
              <button class="link-btn" @click="handleLoadingDetail(row.unloadWorkList,row.hbName,row.shipName,row.materialName,row.usageUnit,row.loadType)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,onMounted} from "vue";
import data3Bg from "@/assets/newScreen/data3Bg.png"
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const originX = ref(0);
const originY = ref(0);
const title = ref(undefined)
let dragging = false;
let offsetX = 0;
let offsetY = 0;
const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  handleWindowUpdateLogDetail: {
    type: Function,
    required: true
  },
  handleLoadingDetail: {
    type: Function,
    required: true
  }
});
const open = (options = {}) => {
  close();
  originX.value = options.x;
  originY.value = options.y;
  title.value = options.title;
  visible.value = true;
};
const close = () => {
  visible.value = false;
  top.value = 0;
  left.value = 0;
  title.value = undefined;
  originX.value = 0;
  originY.value = 0;
};
const testItem = () => {
  console.log("现在数据",props.data)
}
const startDrag = (e) => {
  if (!e.target.closest(".popup-header")) return;
  dragging = true;
  offsetX = e.clientX - left.value;
  offsetY = e.clientY - top.value;
  document.addEventListener("mousemove", onDrag);
  document.addEventListener("mouseup", stopDrag);
};
const onDrag = (e) => {
  if (!dragging) return;
  left.value = e.clientX - offsetX;
  top.value = e.clientY - offsetY;
};
const stopDrag = () => {
  dragging = false;
  document.removeEventListener("mousemove", onDrag);
  document.removeEventListener("mouseup", stopDrag);
};
onBeforeUnmount(() => {
  document.removeEventListener("mousemove", onDrag);
  document.removeEventListener("mouseup", stopDrag);
});
defineExpose({ open, close });
/* 👆 上面是通用部分 */
/* 👇 下面为定制部分 */
const tableContainer3 = ref(null)
let scrollTimer3 = null
let isPaused3 = false
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
  }, 130) // 调整滚动速度（越小越快）
}
const pauseScroll3 = () => {// 暂停/恢复
  isPaused3 = true
}
const resumeScroll3 = () => {
  isPaused3 = false
}
onMounted(()=>{
  startAutoScroll3();
})
/**
 * （复杂展示）判断是否显示
 * @param propertyType1
 * 2-1: 无船无作业的泊位
 * 2-2: 无船无作业的船名
 * 2-3：无船无作业的被覆盖玩意
 * @param propertyType2
 * 1-1: 泊位
 * 1-2: 船名
 * 1-3: 滞期费，粗略效率
 * 1-4：物料，装卸，客户
 * 1-5：作业量
 * 1-6：进度，效率，装卸单
 * 1-7: 日志
 * @param row 这一行的值
 */

const getVIf=(propertyType1,propertyType2,row,columnNum)=>{
  if(row.cardCount==="2"){//无船非作业
    switch(propertyType1){
      case "2-1":{
        return true;
      }
      case "2-2":{
        return true;
      }
      case "2-3":{
        return false;
      }
    }
  }else if(row.cardCount==="1"){//有船非作业
    switch(propertyType2){
      case "1-1":{
        if(row.rowspan>0){
          return true;
        }else{
          return false;
        }
      }
      case "1-2":{
        if(row.rowspan>0){
          return true;
        }else{
          return false;
        }
      }
      case "1-3":{
        return false;
      }
      case "1-4":{
        return true;
      }
      case "1-5":{
        if(row.rowspan>0){
          return true;
        }else{
          return false;
        }
      }
      case "1-6":{
        return false;
      }
      case "1-7":{
        return false;
      }
    }
  }else{//之前的作业的（算各种指标的）
    if(propertyType2==="1-1"||propertyType2==="1-2"||propertyType2==="1-3"||propertyType2==="1-7"){
      return row.rowspan>0?true:false;
    }else{
      return true;
    }
  }
}
/**
 * （复杂展示）判断样式
 * @param propertyType1
 * 2-1: 无船无作业的泊位
 * 2-2: 无船无作业的船名
 * 2-3：无船无作业的被覆盖玩意
 * @param propertyType2
 * 1-1: 泊位
 * 1-2: 船名
 * 1-3: 滞期费，粗略效率
 * 1-4：物料，装卸，客户
 * 1-5：作业量
 * 1-6：进度，效率，装卸单
 * 1-7: 日志
 * @param row 这一行的值
 */
const getVBind=(propertyType1,propertyType2,row,columnNum)=>{
  if(row.cardCount==="2"){//无船非作业
    switch(propertyType1){
      case "2-1":{
        return {colspan: 1};
      }
      case "2-2":{
        return {colspan: 11};
      }
      case "2-3":{
        return {};
      }
    }
  }else if(row.cardCount==="1"){//有船非作业
    switch(propertyType2){
      case "1-1":{
        if(row.rowspan>0){
          return {rowspan: row.rowspan};
        }else{
          return {};
        }
      }
      case "1-2":{
        if(row.rowspan>0){
          return {rowspan: row.rowspan,colspan: columnNum};
        }else{
          return {}
        }
      }
      case "1-3":{
        return {};
      }
      case "1-4":{
        return {};
      }
      case "1-5":{
        if(row.rowspan>0){
          return {rowspan: row.rowspan,colspan: columnNum};
        }else{
          return {}
        }
      }
      case "1-6":{
        return {};
      }
      case "1-7":{
        return {};
      }
    }
  }else{//之前的作业的（算各种指标的）
    if(propertyType2==="1-1"||propertyType2==="1-2"||propertyType2==="1-3"||propertyType2==="1-7"){
      return {rowspan: row.rowspan}
    }else{
      return {}
    }
  }
}
const getBG = (row)=>{
  if(row.cardCount=="2"){
    return "rgba(254, 13, 13, 0.25)";
  }else if(row.cardCount=="1"){
    return "rgba(245, 217, 21, 0.25)";
  }else if(row.status=="4"){
    return "rgba(13, 40, 195, 0.25)";
  }
}
</script>
<style scoped>
.popup-box {
  position: absolute;
  border-radius: 6px;
  z-index: 10;
  /* width: 50%; */
  height: 36vh;
  user-select: none; /* 拖动时防止选中文字 */
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
}
.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.3vh 12px;
  font-weight: bold;
  cursor: move;
  border-top-left-radius: 6px;
  border-top-right-radius: 6px;
}
.status-group {
  display: flex;
  gap: 6px;
}
.action-group {
  display: flex;
  gap: 12px;
}
.close-btn {
  background: transparent;
  border: none;
  color: #0fe0d6;
  font-size: 13px;
  cursor: pointer;
}
.content-title {
  color: aliceblue;
  margin-left: 27vh;
  font-weight: bold;
  font-size: 1.4vh;
}
.content-box {
  margin-top: 0.2vh;
  max-height: 33vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  /* width: 100%; */
  font-size: 1.2vh;
}
.custom-table {
  /* width: 100%; */
  border-collapse: collapse;
  color: rgb(115, 255, 0); /* 字体白色 */
  font-size: 1.88vh;
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
  font-size: 1.1vh;
}
.ship-cell {
  max-width: 6vh;       /* 限制宽度 */
}
.ship-cell-mini {
  max-width: 8vh;       /* 限制宽度 */
}
.progress-cell {
  min-width: 12vh;       /* 固定单元格宽度 */
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
  background: #e5ff00;
  border-radius: 1vh;
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
  width: 2vh;
  background: none;       /* 去掉背景 */
  border: none;           /* 去掉边框 */
  color: rgb(255, 251, 0);            /* 文字颜色 */
  cursor: pointer;        /* 鼠标悬停变小手 */
  text-decoration: underline; /* 下划线，像链接 */
  padding: 0;             /* 去掉默认内边距 */
  font-size: inherit;     /* 跟随表格字体大小 */
}
.link-btn:hover {
  color: #00f7ff;         /* 悬停时颜色更亮 */
}
.commen-style {
  display: inline-block;          /* inline-block 才能用 ellipsis */
  white-space: nowrap;            /* 禁止自动换行 */
  overflow: hidden;               /* 超出隐藏 */
  text-overflow: ellipsis;        /* 超出显示省略号 */
  max-width: 100%;                /* 省略号区域的限制 */
  vertical-align: middle;
}
</style>