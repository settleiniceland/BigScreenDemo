<template>
  <div class="popup-content">
    <div v-if="props.berchDetail.length<1" class="noData">暂无计划单</div>
    <div v-else>
      <div class="tabs">
        <div
          v-for="menu in filterMenuDict"
          :key="menu.value"
          :class="['tab-item', { active: menu.value === activeCategory }]"
          @click="activeCategory = menu.value">
          {{ menu.label }}
        </div>
      </div>
      <div class="outer-scroll">
        <div v-if="showData(activeCategory).length<1">
          暂无该数据
        </div>
        <div v-else v-for="(item,index) in showData(activeCategory)" :key="index" class="info-block">
          <div v-if="activeCategory===1" class="header">
            <div>泊位名：{{item.hbName}}</div>
            <div>船名：{{item.shipName}}</div>
            <div>物料：{{item.materialName}}</div>
            <div>离泊时间：{{item.outBerthTime}}</div>
          </div>
          <div v-else-if="activeCategory===2" class="header">
            <div>泊位名：{{item.hbName}}</div>
            <div>船名：{{item.shipName}}</div>
            <div class="card">
              <div class="inner-scroll">
                <div
                  v-for="(child, i) in item.children"
                  :key="i"
                  class="material-item">
                  <div>物料：{{ child.materialName }}</div>
                  <div>总量：{{ child.tonnage }}</div>
                  <div>已作业量：{{ child.unloadWeight }}</div>
                  <hr v-if="item.children.length-1>i" class="divider" />
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="activeCategory===3" class="header">
            <div>泊位名：{{item.hbName}}</div>
            <div>船名：{{item.shipName}}</div>
            <div>物料：{{item.materialName}}</div>
            <div>到港时间：{{item.arrivalTime}}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed,onMounted,ref} from "vue";
// 当前选中的分类
const activeCategory = ref()

const props = defineProps({
  berchDetail: {
    type: Array,
    default: ()=>[]
  }
});
// onMounted(()=>{
//   console.log("传来数据",props.berchDetail)
// })
const menuDict=[
  {label:"离泊",value:1},
  {label:"作业中",value:2},
  {label:"到港",value:3},
]
const filterMenuDict = computed(()=>{
  const resultData = [];
  for(const menu of menuDict){
    if(showData(menu.value).length>0){
      resultData.push(menu);
    }
  }
  activeCategory.value = resultData[0].value;
  return resultData;
})
const showData=(type)=>{
  const data = props.berchDetail.filter((item)=>{
    return item.type === type;
  });
  return data
}
</script>
<style scoped>
.popup-content {
  padding: 6px;
  font-size: 14px;
  overflow: hidden;
  overflow-y: auto;
}
.tabs {
  display: flex;
  overflow-x: auto; /* 可以横向滑动 */
  gap: 8px;
  margin-bottom: 10px;
}

.tab-item {
  padding: 6px 12px;
  border-radius: 6px;
  background: #857b77;
  cursor: pointer;
  white-space: nowrap;
}

.tab-item.active {
  background: #409eff;
  color: white;
}

/* 外层滚动条 */
.outer-scroll {
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
  padding-right: 6px;
  max-height: 300px;
  color: #10ff10;
}

/* 每个泊位信息块 */
.info-block {
  margin-bottom: 12px;
  padding-bottom: 8px;
}

.header {
  font-weight: bold;
  margin-bottom: 6px;
}

/* 卡片样式 */
.card {
  border: 1px solid #aaa;
  border-radius: 10px;
  background: transparent;
  padding: 6px;
  height: 100px;
  overflow: hidden;
}

/* 卡片内部滚动条 */
.inner-scroll {
  height: 100%;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
  padding-right: 4px;
}

.material-item {
  margin-bottom: 6px;
  font-size: 14px;
}

.noData {
  font-size: 26px;
  color: rgb(250, 89, 89);
}
.divider {
  border: none;
  border-top: 1px dashed white; /* 白色虚线 */
  margin: 8px 0;                /* 上下间距 */
}
</style>