<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${pwf})`
    }"@mousedown="startDrag">
    <div class="popup-header">
      <div class="status-group">
        <div v-for="tap in tap3" :key="tap.key" 
            class="status-item" @click="changeJobStatus(tap.key)"
            :style="{backgroundImage: `url(${choseStatus===tap.key?pssf:psf})`}">
          {{ tap.value }}
          <span class="dot" :style="{color: `${tap.color}`}"></span>
        </div>
      </div>
      <div class="action-group">
        <button class="more-btn" @click="showMoreData" v-if="data.length>0">查看更多</button>
        <button class="close-btn" @click.stop="close">关闭</button>
      </div>
    </div>
    <div  class="property-wrapper" v-if="showPopFirstData!==undefined">
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <span>泊位名：</span>
        <span>{{ showPopFirstData.hbName }}</span>
      </div>
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <span>船名：</span>
        <span>{{ showPopFirstData.shipName }}</span>
      </div>
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <span>物料名：</span>
        <span>{{ showPopFirstData.params.completeName }}</span>
      </div>
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <span>客户单位：</span>
        <span>{{ showPopFirstData.params.completeClientName }}</span>
      </div>
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <span>装卸类型：</span>
        <span>{{ showPopFirstData.params.loadType }}</span>
      </div>
      <div class="property-class" :style="{backgroundImage: `url(${pdf})`}">
        <template v-if="choseStatus===1">
          <span>装卸情况：</span>
          <span>{{ showPopFirstData.params.completeAchieveWork }}</span>
        </template>
        <template v-else-if="choseStatus===2">
          <span>到港时间：</span>
          <span>{{ showPopFirstData.arrivalTime }}</span>
        </template>
        <template v-else-if="choseStatus===3">
          <span>作业完成时间：</span>
          <span>{{ showPopFirstData.endTime }}</span>
        </template>
      </div>
    </div>
    <div v-else class="no-property-class">
      <span >无数据</span>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,nextTick,computed,defineProps} from "vue";
import pdf from "@/assets/newScreen/popDetailFrame.png"
import psf from "@/assets/newScreen/popStatusFrame.png"
import pssf from "@/assets/newScreen/popSelectStatusFrame.png"
import pwf from "@/assets/newScreen/popWindowFrame.png"
import {tap3} from "./data.js"
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const originX = ref(0);
const originY = ref(0);
const data = ref([]);
const choseStatus = ref(1);
let dragging = false;
let offsetX = 0;
let offsetY = 0;
//★★★★★★★★👇工具方法，获取该弹窗左上角坐标👇★★★★★★★★
function logPopupPosition() {
  const popupEl = document.querySelector('.popup-box');
  if (!popupEl) {
    console.warn('popup-box 元素未找到');
    return;
  }
  const rect = popupEl.getBoundingClientRect();
  const bodyRect = document.body.getBoundingClientRect();
  const percentX = rect.left / bodyRect.width;
  const percentY = rect.top / bodyRect.height;
  console.info('弹窗左上角百分比坐标 (相对于 body):', {
    x: percentX,
    y: percentY
  });
  console.info('弹窗左上角像素坐标 (相对于 body):', {
    x: rect.left,
    y: rect.top
  });
}
const testCoordinateLeftTop=()=>{
  setTimeout(() => {
    // 等 Vue 渲染完再取元素
    nextTick(() => {
      logPopupPosition();
    });
  }, 1000);
}
//★★★★★★★★👆工具方法👆★★★★★★★★★★
const props = defineProps({
  handleShowMoreDetail: {
    type: Function,
    default:()=>{}
  },
  cancelSelectDing: {
    type: Function,
    default:()=>{}
  },
  closeSubItem: {
    type: Function,
    default:()=>{}
  }
})
const open = (options = {}) => {
  reFlush();
  originX.value = options.x
  originY.value = options.y;
  visible.value = true;
  data.value = options.data;
  // testCoordinateLeftTop();//👈调试用
};
const close = () => {
  visible.value = false;
  reFlush();
  props.cancelSelectDing();
  props.closeSubItem();
};
const reFlush=()=>{
  top.value = 0;
  left.value = 0;
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
const changeJobStatus = (item)=>{
  choseStatus.value = item;
}
const showPopFirstData=computed (()=>{
  switch(choseStatus.value){
    case 1: //作业中
      for(const item of data.value){
        if(item.status==="4"){
          return item;
        }
      }
      break;
    case 2: //到港
      for(const item of data.value){
        if(item.status==="0"||item.status==="1"||item.status==="2"||item.status==="3"){
          return item;
        }
      }
      break;
    case 3: //离港
      for(const item of data.value){
        if(item.status==="5"||item.status==="6"){
          return item;
        }
      }
      break;
    default:
      return undefined;
  }
})
const showMoreData = ()=>{
  props.handleShowMoreDetail(data.value,data.value[0].hbName+" 泊位查看更多")
}
</script>
<style scoped>
.popup-box {
  position: absolute;
  margin: 0;           /* 清除可能的 margin */
  transform: none;     /* 清除 transform */
  border-radius: 6px;
  z-index: 3;
  width: 50vh;
  height: 29vh;
  user-select: none; /* 拖动时防止选中文字 */
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  font-weight: bold;
  cursor: move;
  border-top-left-radius: 6px;
  border-top-right-radius: 6px;
}

.status-group {
  display: flex;
  gap: 6px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  font-size: 12px;
  color: #fff;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  border-radius: 4px;
}
/* 圆点 */
.dot {
  width: 0.7vh;
  height: 0.7vh;
  border-radius: 50%;
  display: inline-block;
  background: currentColor;
}
/* 右侧按钮 */
.action-group {
  display: flex;
  gap: 12px;
}
.more-btn,
.close-btn {
  background: transparent;
  border: none;
  color: #0fe0d6;
  font-size: 13px;
  cursor: pointer;
}
.property-wrapper {
  display: flex;         /* 或者 display: grid */
  flex-direction: column;/* 竖直排列 */
  gap: 1vh;              /* 这里才生效 */
  color: aqua;
}
.property-class {
  margin-left: 1.6vh;
  margin-right: 1.6vh;
  height: 3vh;
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 4;
  display: flex;                /* 开启 flex 布局 */
  justify-content: space-between; /* 左右两边分散对齐 */
  align-items: center;          /* 垂直居中 */
  padding: 0 1vh;               /* 左右留点内边距，避免贴边 */
}
.no-property-class {
  color: aqua;
  margin-left: 1.6vh;
  margin-right: 1.6vh;
}
</style>