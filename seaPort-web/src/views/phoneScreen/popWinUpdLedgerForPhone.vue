<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${wulb})`
    }"@touchstart="startDrag"
    @mousedown="startDrag">
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
          <div v-for="tap in tap1"
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
                <th>次序</th>
                <th>空窗类型</th>
                <th>开始时间</th>
                <th>结束时间</th>
                <th>暂停内容</th>
                <th>备注</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in dataWin" :key="index">
                <td class="ship-cell"><span :title="index+1">{{ index+1 }}</span></td>
                <td class="ship-cell"><span :title="getWindowLogName(row.periodType)">{{getWindowLogName(row.periodType)}}</span></td>
                <td class="ship-cell"><span :title="row.remark1">{{ row.remark1 }}</span></td>
                <td class="ship-cell"><span :title="row.remark2">{{ row.remark2 }}</span></td>
                <td class="ship-cell1"><span :title="row.stopClassDetail">{{ row.stopClassDetail }}</span></td>
                <td class="ship-cell2"><span :title="row.remark">{{ row.remark }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="table-container" v-if="selectItem===2">
          <table class="custom-table">
            <thead>
              <tr>
                <th>次序</th>
                <th>物料</th>
                <th>更改人</th>
                <th>更改时间</th>
                <th>距上次(h)</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in dataUpd" :key="index">
                <td class="ship-cell"><span :title="index">{{ index+1 }}</span></td>
                <td class="ship-cell"><span :title="row.materialName">{{ row.materialName }}</span></td>
                <td class="ship-cell"><span :title="row.nickName">{{ row.nickName }}</span></td>
                <td class="ship-cell"><span :title="row.refreshTime.replace('T', ' ')">{{ row.refreshTime.replace('T', ' ') }}</span></td>
                <td class="ship-cell"><span :title="row.distanceLastTime">{{ row.distanceLastTime }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,defineProps} from "vue";
import {getWindowLogName,tap1} from "@/views/newScreen/data.js"
import wulb from "@/assets/newScreen/winUpdateLedferBg.png"
import tn from "@/assets/newScreen/tapNormal.png"
import ts from "@/assets/newScreen/tapSelected.png"
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const originX = ref(0);
const originY = ref(0);
const dataWin = ref([]);
const dataUpd = ref([]);
const title = ref(undefined)
let dragging = false;
let offsetX = 0;
let offsetY = 0;
const selectItem = ref(1);
const props = defineProps({
  setOverlay: {
    type: Function,
    default:()=>{}
  }
})
const open = (options = {}) => {
  close();
  props.setOverlay(true);
  originX.value = options.x;
  originY.value = options.y;
  if(options.dataWin!==null&&options.dataWin!==undefined){
    dataWin.value = options.dataWin;
  }
  if(options.dataUpd!==null&&options.dataUpd!==undefined){
    dataUpd.value = options.dataUpd;
  }
  title.value = options.title;
  visible.value = true;
};
const close = () => {
  props.setOverlay(false);
  visible.value = false;
  top.value = 0;
  left.value = 0;
  dataWin.value = [];
  dataUpd.value = [];
  title.value = undefined;
  originX.value = 0;
  originY.value = 0;
};
const startDrag = (e) => {
  const point = e.touches ? e.touches[0] : e;
  if (!e.target.closest(".popup-header")) return;
  dragging = true;
  offsetX = point.clientX - left.value;
  offsetY = point.clientY - top.value;
  document.addEventListener("mousemove", onDrag);
  document.addEventListener("mouseup", stopDrag);
  document.addEventListener("touchmove", onDrag, { passive: false });
  document.addEventListener("touchend", stopDrag);
};
const onDrag = (e) => {
  if (!dragging) return;
  const point = e.touches ? e.touches[0] : e;
  left.value = point.clientX - offsetX;
  top.value = point.clientY - offsetY;
  if (e.cancelable) e.preventDefault();
};
const stopDrag = () => {
  dragging = false;
  document.removeEventListener("mousemove", onDrag);
  document.removeEventListener("mouseup", stopDrag);
  document.removeEventListener("touchmove", onDrag);
  document.removeEventListener("touchend", stopDrag);
};
onBeforeUnmount(() => {
  document.removeEventListener("mousemove", onDrag);
  document.removeEventListener("mouseup", stopDrag);
  document.removeEventListener("touchmove", onDrag);
  document.removeEventListener("touchend", stopDrag);
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
  /* width: 100%; */
  height: 27vh;
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
  white-space: nowrap;
}
.content-title {
  color: aliceblue;
  margin-left: 17vh;
  font-weight: bold;
  font-size: 1.11vh;
  white-space: nowrap;
}
.content-box {
  margin-left: 0.2vh;
  overflow-y: auto;
  width: 100%;
  font-size: 1.2vh;
}
.table-container {
  margin-top: 0.6vh;
  max-height: 22vh; /* 限制高度，表格滚动 */
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  overflow-y: auto;
}
/* 保留原有表格样式 */
.custom-table {
  width: 99%;
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
  /* max-width: 6vh;       限制宽度 */
  /*  overflow: hidden;    超出隐藏 */
  white-space: nowrap;  /* 不换行 */
  /* text-overflow: ellipsis; 超出用...显示 */
}
.ship-cell1 {
  max-width: 20vh;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.ship-cell2 {
  min-width: 10vh;
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