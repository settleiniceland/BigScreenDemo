<template>
  <div class="container-work">
    <div class="title-box" :class="{ fullscreen: isFullscreen }">
      <el-tooltip
        class="box-item"
        effect="dark"
        :content="`点击标题可以查看${title}阶段卸率标准`"
        placement="bottom"
      >
        <span style="cursor: pointer" @click="() => handleViewStandard()">
          {{ title }}-作业
        </span>
      </el-tooltip>
      <div class="icon-style">
        <IconifyIconOffline
          :icon="isFullscreen ? ExitFullscreen : Fullscreen"
          class="icon"
          @click="toggleFullscreen"
        />
      </div>
    </div>
    <div class="content-box" :class="{ fullscreen: isFullscreen }">
      <el-table
        :style="{ width: '100%' }"
        :data="data"
        show-overflow-tooltip
        :tooltipOptions="{ appendTo: 'body' }"
      >
        <el-table-column
          type="index"
          label="序号"
          :width="isFullscreen ? '100' : '30'"
        />
        <el-table-column
          prop="berthName"
          label="泊位"
          :width="isFullscreen ? '100' : '50'"
          :filters="berthFilters"
          :filter-method="filterBerth"
        />
        <el-table-column
          prop="materialName"
          label="物资"
          :width="isFullscreen ? '150' : '60'"
          :filters="materialFilters"
          :filter-method="filterMaterial"
        />
        <el-table-column
          v-if="title === PireTypeOptionsObj[2]?.label"
          prop="mineNumber"
          label="矿号"
          :width="isFullscreen ? '120' : '50'"
        />
        <el-table-column label="进度" prop="progress" align="center" sortable>
          <template #default="{ row }">
            <div class="progress-container">
              <el-progress
                :percentage="row.progress"
                :stroke-width="12"
                :text-inside="true"
                :color="colors"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="avgDischargeRate"
          label="作业效率"
          align="center"
          :width="isFullscreen ? '120' : '90'"
        >
          <template #default="{ row }">
            <el-tooltip
              class="box-item"
              effect="dark"
              :disabled="!row.exceptionFlag"
              :content="row.exceptionReason || 'APP暂未填写原因'"
              placement="left"
            >
              <span
                class="green-text"
                :class="{ 'error-text': row?.exceptionFlag }"
                >{{ row.avgDischargeRate }}</span
              >
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label="当前吨位|当前件数"
          align="center"
          prop="unloadWeight"
          v-if="isFullscreen ? true : false"
        />
        <el-table-column
          label="总吨位 |总件数"
          align="center"
          prop="tonnage"
          v-if="isFullscreen ? true : false"
        />
        <el-table-column
          label="占泊时间"
          align="center"
          prop="mooringTime"
          v-if="isFullscreen ? true : false"
        />
      </el-table>
    </div>
  </div>
</template>
<script setup lang="ts">
import ExitFullscreen from "@iconify-icons/ri/fullscreen-exit-fill.js";
import Fullscreen from "@iconify-icons/ri/fullscreen-fill.js";
import { computed, ref } from "vue";
import { PireTypeOptionsObj } from "@/contans";

const {
  title,
  data,
  enterFullscreen,
  exitFullscreen,
  isFullscreen
} = defineProps<{
  title: string;
  data: any;
  enterFullscreen: Function;
  exitFullscreen: Function;
  isFullscreen: Boolean;
}>();

const dialogVisible = ref(false);

const colors = computed(() => [
  { color: "#f56c6c", percentage: 20 },
  { color: "#e6a23c", percentage: 40 },
  { color: "#5cb87a", percentage: 60 },
  { color: "#1989fa", percentage: 80 },
  { color: "#67C23A", percentage: 100 }
]);

const berthFilters = computed(() => {
  const uniqueBerths = [...new Set(data.map((item: any) => item.berthName))];
  return uniqueBerths.map((berth: string) => ({
    text: berth || "未知泊位",
    value: berth
  }));
});

const materialFilters = computed(() => {
  const uniqueMaterials = [
    ...new Set(data.map((item: any) => item.materialName))
  ];
  return uniqueMaterials.map((material: string) => ({
    text: material || "未知物资",
    value: material
  }));
});

const filterBerth = (value: string, row: any) => {
  return row.berthName === value;
};

const filterMaterial = (value: string, row: any) => {
  return row.materialName === value;
};

const toggleFullscreen = () => {
  if (isFullscreen) {
    exitFullscreen();
  } else {
    enterFullscreen();
  }
};
const groupByPier = (data: any[]) => {
  return data.reduce((acc: any, item: any) => {
    if (!acc[item.berthName]) acc[item.berthName] = [];
    acc[item.berthName].push(item);
    return acc;
  }, {});
};

const handleViewStandard = () => {
  dialogVisible.value = true;
};
</script>
<style scoped lang="less">
.container-work {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}
.green-text {
  color: #03fff2;
  font-weight: bold;
}
.error-text {
  color: #f56c6c;
  font-weight: bold;
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
  --el-table-border-color: transparent !important;
  --el-table-border: none !important;
  --el-table-header-text-color: #ffffff;
  --el-table-row-hover-bg-color: rgba(33, 148, 242, 0.15);
  color: #feffff;
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
.icon {
  position: absolute;
  margin-right: 10px;
  cursor: pointer;
  font-size: 22px;
}
.fullscreen {
  background: none;
  backdrop-filter: none !important;
}

/* Dialog-specific styles */
::v-deep(.rate-dialog) {
  .el-dialog {
    background: #1a2a44; /* Dark blue background */
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
    z-index: 9999;
  }
  .el-dialog__header {
    background: #29566e;
    color: #ffffff;
    border-radius: 12px 12px 0 0;
    padding: 16px;
    .el-dialog__title {
      font-size: 18px;
      font-weight: bold;
    }
  }
  .el-dialog__body {
    padding: 20px;
    background: #1a2a44;
    color: #ffffff;
  }
  .el-dialog__footer {
    padding: 16px;
    background: #1a2a44;
    border-radius: 0 0 12px 12px;
  }
}

.rate-card {
  background: #2c3e50; /* Slightly lighter dark background */
  border: 1px solid #4b6584; /* Subtle border */
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
  }
}
</style>
