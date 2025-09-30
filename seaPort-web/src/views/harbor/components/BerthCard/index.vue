<template>
  <div
    class="card"
    :class="{ enlarged: isFullscreen }"
    @click="openUnloadListDialog(data?.unloadWorkList)"
  >
    <div class="content" :class="[isPause && 'pause']">
      <div class="card-title">
        <span class="title">{{ data.berthName }}</span>
        <span class="code">{{ data.berthCode }}</span>
        <div class="status">
          <el-tooltip :disabled="!isPause" placement="right" width="200">
            <template #content>
              <div class="tooltip-content">
                <div style="font-size: 14px">暂停原因：</div>
                <div style="font-size: 16px">{{ data.reason }}</div>
              </div>
            </template>
            <el-tag v-if="isPause" type="danger" effect="dark">暂停中</el-tag>

            <el-tag
              v-else
              :type="statusOption.type"
              effect="dark"
              :size="isFullscreen ? 'large' : 'small'"
              >{{ statusOption.label }}
            </el-tag>
          </el-tooltip>
        </div>
      </div>
      <div class="items">
        <div v-for="item in berthOptions" :key="item.label" class="item">
          <div class="item-title">{{ item.label }}:</div>
          <div v-if="item.label === '计划状态'" class="status">
            <el-tag
              v-if="planStatus.value"
              :style="{
                color: Colors[planStatus.value],
                border: `1px solid ${Colors[planStatus.value]}`
              }"
              :size="isFullscreen ? 'large' : 'small'"
              effect="plain"
              >{{ planStatus.label }}
            </el-tag>
            <el-tag v-else size="small" type="info">无</el-tag>
          </div>
          <div
            v-else-if="item.label === '已用时间' && item.value"
            class="item-value work-type"
          >
            <el-text class="mx-1" type="warning" style="font-weight: 600">{{
              item.value
            }}</el-text>
          </div>
          <div v-else class="item-value">
            <TypographyText
              :style="{
                color: '#fff',
                width: isFullscreen ? '210px' : '130px',
                fontSize: isFullscreen ? '20px' : '14px'
              }"
              :ellipsis="ellipsis ? { tooltip: item.value } : false"
              :content="item.value ?? '-'"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { BerthStatusTagOptions } from "@/contans";
import { computed, ref } from "vue";
import { TypographyText } from "ant-design-vue";
import { Colors } from "@/contans";
import { useHarborHook } from "../../utils/hooks";
const { openUnloadListDialog } = useHarborHook();

const ellipsis = ref(true);

const { data, planStatusOptions, isFullscreen } = defineProps({
  data: {
    type: Object,
    required: true,
    default: () => {}
  },
  planStatusOptions: {
    type: Array,
    required: true,
    default: () => []
  },
  isFullscreen: {
    type: Boolean,
    default: false // 默认不放大
  },
  handleClickBerth: {
    type: Function,
    default: () => {}
  }
});
const isPause = computed(() => {
  return data.berthStatus === "3" && data?.workType === "1";
});

const statusOption = computed(() => {
  if (data.berthStatus === "3" && data?.workType !== "1") {
    return BerthStatusTagOptions[1];
  }
  return BerthStatusTagOptions[data?.berthStatus ?? "0"];
});

const planStatus: any = computed(() => {
  return (
    planStatusOptions.find((item: any) => item?.value === data?.status) ?? {
      label: "无",
      value: ""
    }
  );
});
const berthOptions = computed(() => {
  const {
    tonnage,
    materialName,
    status,
    operationTime,
    dockingTime,
    expendTime
  } = data;
  return [
    {
      label: "物资名称",
      value: materialName || "-"
    },
    {
      label: "靠泊时间",
      value: dockingTime || "-"
    },
    {
      label: "物资重量",
      value: tonnage || "-"
    },
    {
      label: "作业时间",
      value: operationTime || "-"
    },

    {
      label: "计划状态",
      value: status
    },
    {
      label: "已用时间",
      value: expendTime
    }
  ];
});
</script>
<style lang="scss" scoped>
$base-font-size: 1rem; // 默认字体大小
$fullscreen-font-size: 2rem; // 放大后的字体大小（适中）
$fullscreen-height: 160px; // 放大后的高度

.card {
  padding: 5px;
  width: 100%;
  height: 115px; /* 默认高度 */
  cursor: pointer;
  display: flex;
  color: #fff;
  .pause {
    background-color: rgba(#f56c6c, 0.3) !important;
  }
  .content {
    flex: 1;
    padding: 4px;
    border-radius: 10px;
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
    font-size: $base-font-size; /* 默认字体大小 */
    &:hover {
      background-color: rgba(126, 150, 202, 0.2);
    }
    // ::v-deep(.ant-badge-status-dot) {
    //   width: 8px;
    //   height: 8px;
    // }
    .card-title {
      font-size: 1em; /* 相对字体大小 */
      font-weight: 600;
      margin-bottom: 2px;
      display: flex;
      justify-content: space-between;

      .status {
        font-size: 0.8em;
        color: #fff;
        padding: 2px 5px;
      }
    }

    .items {
      display: flex;
      flex-wrap: wrap;

      .item {
        width: 50%;
        display: flex;
        align-items: center;
        justify-content: start;
        margin-bottom: 2px;
        font-size: 0.8em;

        .item-title {
          min-width: 60px;
        }
        .item-value {
          min-width: 70px;
        }
        .work-type {
          color: #fff;
        }
      }
    }
  }

  /* 仅调整字体和高度 */
  &.enlarged {
    height: $fullscreen-height; /* 盒子变高 */
    .mx-1 {
      font-size: calc(1.2 * $base-font-size);
    }
    /* 让 .content 及其子元素字体变大 */
    .content {
      font-size: $fullscreen-font-size; /* 变大字体 */
    }

    ::v-deep(.el-tag) {
      font-size: $base-font-size;
    }
    /* 让卡片标题的字体也放大 */
    .card-title {
      font-size: calc(1.2 * $base-font-size);
    }

    /* 让 .item-title 和 .item-value 统一字体大小 */
    .items .item {
      font-size: calc(1.2 * $base-font-size);

      .item-title,
      .item-value {
        font-size: inherit;
      }
    }
  }
}
</style>
