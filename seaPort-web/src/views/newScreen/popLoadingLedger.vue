<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${llbd})`
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
        <table class="custom-table">
          <thead>
            <tr>
              <th>班次</th>
              <th>日期</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>状态</th>
              <th>卸货(T)</th>
              <th>卸货(件)</th>
              <th>作业机具</th>
              <th>备注</th>
              <th>查看日志</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in data" :key="index">
              <td class="ship-cell"><span :title="row.classes">{{ row.classes }}</span></td>
              <td class="ship-cell"><span :title="row.classTime">{{ row.classTime }}</span></td>
              <td class="ship-cell"><span :title="row.startTime">{{ row.startTime }}</span></td>
              <td class="ship-cell"><span :title="row.endTime">{{ row.endTime }}</span></td>
              <td class="ship-cell">
                <span :title="getLoadingLedgerStatusName(row.workType).name"
                  :style="{color: getLoadingLedgerStatusName(row.workType).color}"
                >
                  {{ getLoadingLedgerStatusName(row.workType).name }}
                </span>
              </td>
              <td class="ship-cell"><span :title="row.totalUnloadWeight">{{ row.totalUnloadWeight }}</span></td>
              <td class="ship-cell"><span :title="row.unloadNum">{{ row.unloadNum }}</span></td>
              <td class="ship-cell"><span :title="row.workEquipment">{{ row.workEquipment }}</span></td>
              <td class="ship-cell"><span :title="row.remark">{{ row.remark }}</span></td>
              <td>
                <button class="link-btn" @click="showDetail(row)">查看详情</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,defineProps} from "vue";
import {getLoadingLedgerStatusName} from "./data.js"
import llbd from "@/assets/newScreen/loadingLedgerBg.png"
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const originX = ref(0);
const originY = ref(0);
const data = ref([]);
const title = ref(undefined)
let dragging = false;
let offsetX = 0;
let offsetY = 0;
const props = defineProps({
  handleStopSlowDetail: {
    type: Function,
    default:()=>{}
  },
  closeSubItemStopSlowForLoad: {
    type: Function,
    default:()=>{}
  }
})
const open = (options = {}) => {
  close();
  originX.value = options.x;
  originY.value = options.y;
  if(options.data!==null&&options.data!==undefined){
    data.value = options.data;
  }
  title.value = options.title;
  visible.value = true;
};
const close = () => {
  props.closeSubItemStopSlowForLoad();
  visible.value = false;
  top.value = 0;
  left.value = 0;
  data.value = [];
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
const showDetail=(row)=>{
  props.handleStopSlowDetail(row.params.unloadWorkDetail,row.params.slowDownWork,title.value);
}
defineExpose({ open, close });
</script>
<style scoped>
.popup-box {
  position: absolute;
  border-radius: 6px;
  z-index: 10;
  width: 45.6%;
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
.content-title {
  color: aliceblue;
  margin-left: 20vh;
  font-weight: bold;
  font-size: 1.11vh;
}
.content-box {
  margin-top: 0.3vh;
  margin-left: 2vh;
  max-height: 14vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  width: 96%;
  font-size: 1.2vh;
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
.ship-cell {
  max-width: 12vh;       /* 限制宽度 */
  overflow: hidden;     /* 超出隐藏 */
  white-space: nowrap;  /* 不换行 */
  text-overflow: ellipsis; /* 超出用...显示 */
}
</style>