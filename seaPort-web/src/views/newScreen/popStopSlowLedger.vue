<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${ssbg})`
    }"@mousedown="startDrag">
    <div class="popup-header">
      <div class="status-group">
        <div class="content-title">{{ title }}</div>
      </div>
      <div class="action-group">
        <button class="close-btn" @click.stop="close">关闭</button>
      </div>
    </div>
    <div>
      <div class="content-box">
        <div class="tap-container">
          <div v-for="tap in tap2"
            :key="tap.key"
            class="tap-but-bg"
            :style="{backgroundImage: `url(${selectItem===tap.key?ts:tn})`}"
            @click="choseTap(tap.key)">
            <span class="tap-but-title">{{ tap.value }}</span>
          </div>
        </div>
        <div class="table-container" v-if="selectItem===1">
          <table class="custom-table">
            <thead>
              <tr>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>因素</th>
                <th>原因</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in dataStop" :key="index">
                <td class="ship-cell"><span :title="row.startTime">{{ row.startTime }}</span></td>
                <td class="ship-cell"><span :title="row.endTime">{{ row.endTime }}</span></td>
                <td class="ship-cell"><span :title="stopLogTypeName(row.remark)">{{stopLogTypeName(row.remark)}}</span></td>
                <td class="ship-cell"><span :title="row.reason">{{ row.reason }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="table-container" v-if="selectItem===2">
          <table class="custom-table">
            <thead>
              <tr>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>原因</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in dataSlow" :key="index">
                <td class="ship-cell"><span :title="row.startTime">{{ row.startTime }}</span></td>
                <td class="ship-cell"><span :title="row.endTime">{{ row.endTime }}</span></td>
                <td class="ship-cell"><span :title="row.remark">{{ row.remark }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount} from "vue";
import {stopLogTypeName,tap2} from "./data.js"
import ssbg from "@/assets/newScreen/ssbg.png"
import tn from "@/assets/newScreen/tapNormal.png"
import ts from "@/assets/newScreen/tapSelected.png"
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const originX = ref(0);
const originY = ref(0);
const dataStop = ref([]);
const dataSlow = ref([]);
const selectItem = ref(1);
const title = ref(undefined)
let dragging = false;
let offsetX = 0;
let offsetY = 0;
const open = (options = {}) => {
  close();
  originX.value = options.x;
  originY.value = options.y;
  if(options.dataStop!==null&&options.dataStop!==undefined){
    dataStop.value = options.dataStop;
  }
  if(options.dataSlow!==null&&options.dataSlow!==undefined){
    dataSlow.value = options.dataSlow;
  }
  title.value = options.title;
  visible.value = true;
};
const close = () => {
  visible.value = false;
  top.value = 0;
  left.value = 0;
  dataStop.value = [];
  dataSlow.value = [];
  title.value = undefined;
  originX.value = 0;
  originY.value = 0;
};
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
const choseTap = (item)=>{
  selectItem.value = item;
}
</script>
<style scoped>
.popup-box {
  position: absolute;
  border-radius: 6px;
  z-index: 10;
  width: 25%;
  height: 21vh;
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
  margin-left: 12vh;
  font-weight: bold;
  font-size: 1.11vh;
}
.content-box {
  margin-left: 2vh;
  overflow-y: auto;
  width: 96%;
  font-size: 1.2vh;
}
/* .table-wrapper {
  display: flex;
  gap: 0.4vh;
  width: 100%;
} */
.table-container {
  margin-top: 0.6vh;
  max-height: 13vh; /* 限制高度，表格滚动 */
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  overflow-y: auto;
}
/* 保留原有表格样式 */
.custom-table {
  width: 96%;
  border-collapse: collapse;
  color: white;
}
.custom-table th,
.custom-table td {
  border: 1px solid rgba(2, 176, 219, 0.589);
  padding: 8px;
  text-align: center;
}
.custom-table thead th {
  position: sticky;
  top: 0;
  background: rgba(17, 137, 167, 0.83);
  z-index: 2;
}
.ship-cell {
  max-width: 6vh;       /* 限制宽度 */
  overflow: hidden;    /*  超出隐藏 */
  white-space: nowrap;  /* 不换行 */
  text-overflow: ellipsis; /* 超出用...显示 */
}
.tap-container {
  margin-top: 0.3vh;
  display: flex;          /* 横向排列 */
  gap: 1vh;               /* 按钮之间的间距 */
  margin-bottom: 0.3vh;
}
.tap-but-bg {
  width: 10vh;
  height: 3vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
  cursor: pointer;
  z-index: 1;
}
.tap-but-title {
  color: #fff;
  font-size: 1.3vh;
  font-weight: bold;
  z-index: 2; /* 保证文字在最上层 */
}
</style>