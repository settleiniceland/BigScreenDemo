<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${wulb})`
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
      
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount} from "vue";
import wulb from "@/assets/newScreen/winUpdateLedferBg.png"
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
.content-title {
  color: aliceblue;
  margin-left: 12vh;
  font-weight: bold;
  font-size: 1.11vh;
}
</style>