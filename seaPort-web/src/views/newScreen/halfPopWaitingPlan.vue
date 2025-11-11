<template>
  <div
    v-show="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${data4Bg})`
    }"@mousedown="startDrag">
    <div class="popup-header">
      <div class="status-group">
        <div class="content-title">{{ title }}</div>
      </div>
      <div class="action-group">
        <!-- <button class="close-btn" @click.stop="testItem">查看数据</button> -->
      </div>
    </div>
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
          <tr v-for="(item, idx) in props.data" :key="idx">
            <td><span v-html="idx + 1"></span></td>
            <td><span :title="item.hbName" v-html="item.hbName"></span></td>
            <td><span :title="item.shipName" v-html="item.shipName" class="commen-style" style="width: 11vh;"></span></td>
            <td><span :title="item.materialName" v-html="item.materialName" ></span></td>
            <td><span :title="item.planTonnage" v-html="item.planTonnage" ></span></td>
            <td><span :title="item.arrivalTime" v-html="item.arrivalTime.replace(/ /g, '<br>')" ></span></td>
            <td><span :title="item.planDockingTime" v-html="item.planDockingTime.replace(/ /g, '<br>')" ></span></td>
            <td>
              <span :style="{color: getTimeDiffColor(item.planDockingTime)}"
                :title="formatTimeDiff(item.planDockingTime)" 
                v-html="formatTimeDiff(item.planDockingTime)">
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,onMounted} from "vue";
import data4Bg from "@/assets/newScreen/data4Bg.png"
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
  data: Array
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
const tableContainer4 = ref(null)
let scrollTimer4 = null
let isPaused4 = false
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
  }, 130) // 调整滚动速度（越小越快）
}
const pauseScroll4 = () => {// 暂停/恢复
  isPaused4 = true
}
const resumeScroll4 = () => {
  isPaused4 = false
}
const getTimeDiffColor = (planDockingTime)=>{
  if (!planDockingTime) return '#fff'
  const now = new Date().getTime()
  const target = new Date(planDockingTime).getTime()
  return target - now >= 0 ? '#39FF14' : '#fb4c2d'
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
onMounted(()=>{
  startAutoScroll4();
})
</script>
<style scoped>
.popup-box {
  position: absolute;
  border-radius: 6px;
  z-index: 10;
  width: 50%;
  height: 33vh;
  user-select: none; /* 拖动时防止选中文字 */
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
}
.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.7vh 12px;
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
.table-container {
  max-height: 30vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  width: 100%;
  font-size: 1.2vh;
}
.table-container::-webkit-scrollbar {
  display: none; 
}
.custom-table {
  width: 100%;
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
.commen-style {
  display: inline-block;          /* inline-block 才能用 ellipsis */
  white-space: nowrap;            /* 禁止自动换行 */
  overflow: hidden;               /* 超出隐藏 */
  text-overflow: ellipsis;        /* 超出显示省略号 */
  max-width: 100%;                /* 省略号区域的限制 */
  vertical-align: middle;
}
</style>