<template>
  <div class="container-work">
    <div class="title-box">
      <span style="cursor: pointer">
        {{ props.title }}
      </span>
    </div>
    <div class="content-box">
      <el-table
        :data="props.data"
        @row-click="handleRowClick"
        style="width: 100%">
        <el-table-column prop="classes" label="班次" width="30">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.classes" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.classes }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="classTime" label="日期" width="70">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.classTime" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.classTime }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="75">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.startTime" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.startTime }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="75">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.endTime" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.endTime }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="workType" label="状态" width="35">
          <template #default="{ row }">
            {{ showUnloadWorkStatus(row.workType) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalUnloadWeight" label="卸货(吨)" width="55">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.totalUnloadWeight" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.totalUnloadWeight }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="unloadNum" label="卸货(件)" width="55">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.unloadNum" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.unloadNum }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="workEquipment" label="作业机具" width="70">
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.workEquipment" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.workEquipment }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip effect="dark" :content="row.remark" placement="top" :teleported="true">
              <span class="ellipsis">{{ row.remark }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  title: {
    type: String,
    default: "未选择计划"
  },
  data: {
    type: Array,
    default: ()=>[]
  },
  selectEvent: {
    type: Function,
    default: ()=>{}
  }
})
const handleRowClick = (row, column, event)=>{
  props.selectEvent(row.duId,props.title+"-"+row.classTime+row.classes);
}
const showUnloadWorkStatus = (val)=>{
  switch(val){
    case "0":
      return "进行中";
    case "1":
      return "暂停";
    case "2":
      return "结束";
  }
}
</script>
<style scoped lang="less">
.container-work {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}
.title-box {
  width: 100%;
  font-size: 18px;
  font-weight: bold;
  color: white;
  background:
    url("/src/assets/images/12.png") no-repeat center,
    #29566e;
  background-size: 100% 100%;
  border-radius: 18px 8px 0 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  span {
    flex: 1;
    text-align: center;
  }
  .icon-style {
    width: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
.content-box {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: linear-gradient(to left, rgba(#005f99, 0.3), rgba(#005f99, 0.6));
  backdrop-filter: blur(2px);
  border-radius: 0 0 8px 8px;
}
::v-deep(.el-table) {
  height: 100%;
  width: 100%;
  background-color: transparent !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: transparent !important;
  --el-table-header-text-color: #b8ff14;
  --el-table-row-hover-bg-color: rgba(33, 148, 242, 0.15);
  color: #ffffff;
  .el-table__cell {
    padding: 7px 0 !important;
    line-height: normal;
    font-size: 0.8rem;
  }
  .el-table-fixed-column--right {
    text-align: center;
    background-color: rgba(#5b88c4, 0.3) !important;
  }
  .cell {
    padding: 0;
    font-size: 1em;
    line-height: normal;
    .el-icon {
      color: #ffffff;
      font-weight: bold;
    }
  }
  .el-progress__text {
    color: white;
  }
}
::deep(.ellipsis){
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>