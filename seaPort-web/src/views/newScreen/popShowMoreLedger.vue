<template>
  <div
    v-if="visible"
    class="popup-box"
    :style="{ 
      left: `calc(${left}px + ${originX * 100}%)`, 
      top: `calc(${top}px + ${originY * 100}%)`,
      backgroundImage: `url(${smbg})`
    }"@mousedown="startDrag">
    <div class="popup-header">
      <div class="status-group">
        <div class="content-title">{{ title }}</div>
      </div>
      <div class="action-group">
        <button class="close-btn" @click.stop="close">关闭</button>
      </div>
    </div>
    <div class="content-box">
      <table class="custom-table">
        <thead>
          <tr>
            <th>状态</th>
            <th>船名</th>
            <th>到港时间</th>
            <th>作业完成时间</th>
            <th>装卸类型</th>
            <th>物料</th>
            <th>客户单位</th>
            <th>已作业量/总量</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item,index) in showData" :key="index">
            <td v-if="item.rowspan>0" :rowspan="item.rowspan">
              <span :title="item.typeName">
                {{ item.typeName }}
                <span class="dot" :style="{color: `${item.typeColor}`}"></span>
              </span>  
            </td>
            <td v-if="item.rowspan>0" :rowspan="item.rowspan">
              <span :title="item.shipName">{{ item.shipName }}</span>
            </td>
            <td v-if="item.rowspan>0" :rowspan="item.rowspan">
              <span :title="item.arrivalTime">{{ item.arrivalTime }}</span>
            </td>
            <td v-if="item.rowspan>0" :rowspan="item.rowspan">
              <span :title="item.endTime">{{ item.endTime }}</span>
            </td>
            <td class="item-class">
              <span :title="item.remark01">{{ item.remark01 }}</span>
            </td>
            <td>
              <span :title="item.materialName">{{ item.materialName }}</span>
            </td>
            <td>
              <span :title="item.usageUnit">{{ item.usageUnit }}</span>
            </td>
            <td>
              <span :title="item.workStatus">{{ item.workStatus }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup>
import {ref,onBeforeUnmount,computed} from "vue";
import {tap3} from "./data.js"
import smbg from "@/assets/newScreen/showMoreBg.png"
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
    const map=new Map();
    tap3.forEach((item)=>{
      map.set(item.key,item);
    })
    data.value.forEach((item)=>{
      if(item.status==="4"){
        item.typeName = map.get(1).value;
        item.typeColor = map.get(1).color;
      }else if(item.status==="0"||item.status==="1"||item.status==="2"||item.status==="3"){
        item.typeName = map.get(2).value;
        item.typeColor = map.get(2).color;
      }else if(item.status==="5"||item.status==="6"){
        item.typeName = map.get(3).value;
        item.typeColor = map.get(3).color;
      }
    })
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
const showData = computed(()=>{
  const resu = [];
  data.value.forEach((item)=>{
    resu.push({
      typeColor: item.typeColor,
      typeName: item.typeName,
      shipName: item.shipName,
      arrivalTime: item.arrivalTime,
      endTime: item.endTime,
      //------👆共有---👇私有------------
      remark01: item.remark01,
      materialName: item.materialName,
      usageUnit: item.usageUnit,
      workStatus: (item.unloadWeight!==null?item.unloadWeight:0)+" / "+item.tonnage,
      rowspan: item.params.dpss.length+1
    });
    if(item.params.dpss.length>0){
      item.params.dpss.forEach((ass)=>{
        resu.push({
          typeColor: item.typeColor,
          typeName: item.typeName,
          shipName: item.shipName,
          arrivalTime: item.arrivalTime,
          endTime: item.endTime,
          remark01: ass.remark01,
          materialName: ass.materialName,
          usageUnit: ass.usageUnit,
          workStatus: (ass.unloadWeight!==null?ass.unloadWeight:0)+" / "+ass.tonnage,
          rowspan: 0
        })
      })
    }
  })
  return resu;
})
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
  margin-left: 22vh;
  font-weight: bold;
  font-size: 1.2vh;
}
.content-box {
  margin-top: 1vh;
  margin-left: 1vh;
  max-height: 15vh; /* 限制高度，表头固定时才会有滚动 */
  overflow-y: auto;
  scrollbar-width: none; /* 火狐隐藏滚动条 */
  width: 96%;
  font-size: 1.2vh;
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
.dot {
  width: 0.7vh;
  height: 0.7vh;
  border-radius: 50%;
  display: inline-block;
  background: currentColor;
}
.item-class {
  max-width: 6vh;       /* 限制宽度 */
  overflow: hidden;   /*   超出隐藏 */
  white-space: nowrap;  /* 不换行 */
  text-overflow: ellipsis; /* 超出用...显示 */
}
</style>