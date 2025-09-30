<template>
  <div class="list">
    <div class="table">
      <pure-table
        ref="tableRef"
        adaptive
        row-key="duId"
        :data="unloadWorkList ?? []"
        :columns="unloadColums"
        width="100%"
        showOverflowTooltip
        :border="false"
        maxHeight="500"
        style="height: auto"
        :header-cell-style="{
          background: 'var(--el-fill-color-light)',
          color: 'var(--el-text-color-primary)'
        }"
        @row-click="handleRowClick"
      >
        <!-- <template #expand="{ row }">
          <div class="bg-gray-100">
            <LogTable
              :key="row.localKey"
              :duId="row.duId"
              :isOperation="row.workType === '0'"
            />
          </div>
        </template> -->
        <template #workType="{ row }">
          <Badge
            style="margin-left: 5px"
            :status="UnloadWorkTypeTagOptions[row.workType].type"
            :text="UnloadWorkTypeTagOptions[row.workType].label"
          />
        </template>
        <template #operation="{ row }">
          <span v-if="row.workType === '2'">无</span>
          <el-button
            v-for="item in handleMenu(row.workType)"
            v-else
            :key="item.key"
            class="reset-margin"
            style="margin-left: 4px"
            :icon="item.icon"
            :type="item.type"
            link
            @click="handleUnloadWork(row, item)"
          >
            {{ item.label }}
          </el-button>
        </template>
      </pure-table>
    </div>
    <div class="logList bg-gray-100">
      <!-- <div class="count">
        <span class="label">暂停次数：</span>
        <span class="count">{{ currentLogList?.length ?? 0 }}</span>
      </div> -->
      <pure-table
        maxHeight="500"
        :data="currentLogList ?? []"
        :columns="columnsLog"
        :border="false"
        showOverflowTooltip
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { CaretRight, SwitchButton } from "@element-plus/icons-vue";
import Pause from "@iconify-icons/ri/pause-circle-line";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { Badge } from "ant-design-vue";
import { UnloadWorkTypeTagOptions } from "@/contans";
import { onMounted, ref } from "vue";
const currentLogList = ref([]);
const handleRowClick = (row: any) => {
  currentLogList.value = row?.dockUnloadDetailList;
};
const unloadColums: any[] = [
  {
    label: "日期",
    prop: "classTime",
    width: 100
  },
  { label: "班次", prop: "classes", width: 55 },
  {
    label: "负责人",
    prop: "leader",
    width: 70
  },
  {
    label: "状态",
    slot: "workType",
    width: 80
  },
  {
    label: "有效卸率",
    prop: "effectiveRate",
    width: 80
  },
  {
    label: "有效时间",
    prop: "effectiveTime",
    width: 85
  },
  {
    label: "开始时间",
    prop: "startTime",
    width: 100
  },
  {
    label: "暂停次数",
    prop: "stopCount",
    width: 80
  },
  { label: "操作", slot: "operation", fixed: "right", width: 135 }
];
const columnsLog = [
  { label: "开始时间", prop: "startTime" },
  { label: "结束时间", prop: "endTime" },
  { label: "暂停间隔", prop: "pauseInterval" },
  {
    label: "暂停原因",
    prop: "reason"
  }
];
const { unloadWorkList, handleUnloadWork } = defineProps({
  unloadWorkList: {
    type: Array,
    default: () => []
  },
  handleUnloadWork: {
    type: Function,
    default: () => {}
  }
});
const moreBtn = ref(null);
moreBtn.value = [
  {
    key: "0",
    label: "恢复",
    value: "recover",
    icon: CaretRight,
    color: "#67C23A",
    type: "primary"
  },
  {
    key: "1",
    value: "stop",
    label: "暂停",
    icon: useRenderIcon(Pause),
    color: "#E6A23C",
    type: "warning"
  },
  {
    key: "2",
    label: "结束",
    value: "over",
    icon: SwitchButton,
    color: "#909399",
    type: "success"
  }
];
onMounted(() => {
  currentLogList.value = unloadWorkList[0]?.dockUnloadDetailList ?? [];
});
const handleMenu = (workType: string) => {
  return moreBtn.value.filter(item => item.key !== workType);
};
</script>

<style lang="less" scoped>
.list {
  width: 100%;
  display: flex;
  max-height: 500px;
  overflow: hidden;

  .table {
    // max-height: 500px;
    width: 700px;

    overflow: auto;
    ::v-deep(.el-table) {
      height: 100% !important;
    }
  }
  .logList {
    flex: 1;
    // height: 100%;
    // max-height: 500px;
    padding: 10px;
    // overflow: auto;
    .count {
      margin-bottom: 5px;

      .label {
        font-weight: bold;
      }
    }
  }
}
</style>
<style>
.el-dialog__body {
  overflow-y: auto !important;
}
</style>
