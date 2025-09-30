<template>
  <div class="container-work">
    <div class="title-box">
      <span style="cursor: pointer">
        {{ props.title }}
      </span>
    </div>
    <div class="tables-container">
    <!-- 左边 -->
    <div class="table-box">
      <div class="title">暂停日志</div>
      <el-table :data="props.stopLogs" style="width: 100%;">
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="remark" label="因素" />
        <el-table-column prop="reason" label="原因" show-overflow-tooltip/>
      </el-table>
    </div>

    <!-- 右边 -->
    <div class="table-box">
      <div class="title">慢作业日志</div>
      <el-table :data="props.slowDownLogs" style="width: 100%;">
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="remark" label="原因" show-overflow-tooltip/>
      </el-table>
    </div>
  </div>
  </div>
</template>
<script setup>
const props = defineProps({
  title: {
    type: String,
    default: "未选择装卸货单"
  },
  stopLogs: {
    type: Array,
    default: ()=>[]
  },
  slowDownLogs: {
    type: Array,
    default: ()=>[]
  },
})
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
// .content-box {
//   flex: 1;
//   overflow-y: auto;
//   padding: 10px;
//   background: linear-gradient(to left, rgba(#005f99, 0.3), rgba(#005f99, 0.6));
//   backdrop-filter: blur(2px);
//   border-radius: 0 0 8px 8px;
// }
.tables-container {
  display: flex;         /* 横向布局 */
  gap: 20px;             /* 左右间距 */
  height: 100%;          /* 如果要铺满父容器 */
}

.table-box {
  flex: 1;               /* 左右平均分 */
  display: flex;
  padding: 3px;
  flex-direction: column;
  background: linear-gradient(to left, rgba(#005f99, 0.3), rgba(#005f99, 0.6));
  overflow: hidden;
}

.title {
  font-size: 16px;
  font-weight: bold;
  padding: 10px;
  background: #29566e;
  color: #fff;
  text-align: center;
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