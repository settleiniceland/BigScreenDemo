<template>
  <div class="table">
    <el-table
      ref="tableRef"
      :data="tableData"
      :border="false"
      showOverflowTooltip
      :tooltipOptions="{
        appendTo: 'body'
      }"
      style="width: 100%; padding-bottom: 15px"
    >
      <!-- 添加序号列 -->
      <el-table-column type="index" label="序号" width="40" />
      <el-table-column prop="shipName" label="船名" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag
            :style="{
              color: Colors[scope.row?.status],
              border: `1px solid ${Colors[scope.row?.status]}`
            }"
            effect="plain"
          >
            {{
              planStatusOptions.find(item => item.value === scope.row.status)
                ?.label
            }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="materialName" label="物资" width="60" />
      <el-table-column prop="shipLength" label="船长(m)" width="60" />
      <el-table-column prop="tonnage" label="总吨" width="60" />
      <el-table-column prop="shipAgency" label="船代理公司" width="80" />
      <el-table-column prop="draft" label="水尺" width="60" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Colors } from "@/contans";
const { planStatusOptions, tableData } = defineProps<{
  planStatusOptions: any;
  tableData: any;
}>();
</script>

<style scoped lang="less">
.table {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding-bottom: 15px;

  :deep(.el-table__body-wrapper) {
    overflow-y: auto !important;
    max-height: 100% !important;
  }

  ::v-deep(.el-table) {
    background-color: transparent !important;
    --el-table-tr-bg-color: transparent !important;
    --el-table-header-bg-color: transparent !important;
    --el-table-border-color: transparent !important;
    --el-table-border: none !important;
    --el-table-header-text-color: #ffffff;
    --el-table-row-hover-bg-color: rgba(33, 148, 242, 0.15);
    color: #feffff;

    .el-table__cell {
      padding: 5px 0 !important;
      line-height: normal;
      font-size: 0.9em;
    }

    .el-table-fixed-column--right {
      text-align: center;
      background-color: rgba(#5b88c4, 0.3) !important;
    }

    .cell {
      padding: 0;
      font-size: 1em;
      line-height: normal;
    }
  }
}
</style>
