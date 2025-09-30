<template>
  <div class="popup-info">
    <div class="header">
      <h3 class="info-title">
        {{ popupData.title }}
        <ElTag
          v-if="
            !popupData.planInfoList?.length || popupData.berthStatus === '0'
          "
          :type="statusInfo.type"
          effect="dark"
        >
          {{ statusInfo.label }}
        </ElTag>
      </h3>
    </div>

    <div class="info">
      <template v-if="popupData.berthStatus === '2'">
        <div class="info-text">
          <strong>维护原因：</strong>{{ popupData.reason || "-" }}
        </div>
      </template>
      <template v-else>
        <!-- 无计划单 -->
        <div v-if="!popupData.planInfoList?.length" class="tab-content">
          <div class="info-text">暂无计划单啦</div>
        </div>
        <!-- 只有一条计划单，直接展示 -->
        <div
          v-else-if="popupData.planInfoList.length === 1"
          class="tab-content"
        >
          <ElScrollbar :max-height="scrollbarMaxHeight">
            <div class="plan-content">
              <div class="info-text">
                <strong>计划单状态：</strong>
                <ElTag
                  v-if="popupData.planInfoList[0].workType !== '1'"
                  :color="
                    PlanStatusOptions[popupData.planInfoList[0].status]
                      ?.color ?? 'info'
                  "
                  effect="dark"
                >
                  {{
                    PlanStatusOptions[popupData.planInfoList[0].status]
                      ?.label ?? "其他"
                  }}
                </ElTag>
                <ElTag v-else type="danger" effect="dark">暂停中 </ElTag>
              </div>
              <div
                v-if="popupData.planInfoList[0].workType === '1'"
                class="info-text"
              >
                <strong>暂停原因：</strong
                >{{ popupData.planInfoList[0].reason || "-" }}
              </div>
              <div
                v-for="[key, field] in filteredFieldMap"
                :key="key"
                class="info-text"
              >
                <strong>{{ field.label }}：</strong
                >{{ popupData.planInfoList[0][key] || field.defaultValue }}
              </div>
            </div>
          </ElScrollbar>
        </div>
        <!-- 多条计划单，使用 Tab 切换 -->
        <ElTabs v-else v-model="activeTab" class="plan-tabs">
          <ElTabPane
            v-for="(item, index) in popupData.planInfoList"
            :key="item.planId"
            :label="`船 ${index + 1}`"
            :name="item.planId"
          >
            <ElScrollbar :max-height="scrollbarMaxHeight">
              <div class="plan-content">
                <div class="info-text">
                  <strong>计划单状态12：</strong>
                  <ElTag
                    v-if="item.workType !== '1'"
                    :color="PlanStatusOptions[item.status]?.color ?? 'info'"
                    effect="dark"
                  >
                    {{ PlanStatusOptions[item.status]?.label ?? "其他" }}
                  </ElTag>
                  <ElTag v-else type="danger" effect="dark">暂停中 </ElTag>
                </div>
                <div v-if="item.workType === '1'" class="info-text">
                  <strong>暂停原因：</strong>{{ item.reason || "-" }}
                </div>
                <div
                  v-for="[key, field] in filteredFieldMap"
                  :key="key"
                  class="info-text"
                >
                  <strong>{{ field.label }}：</strong
                  >{{ item[key] || field.defaultValue }}
                </div>
              </div>
            </ElScrollbar>
          </ElTabPane>
        </ElTabs>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, inject, ref, watch } from "vue";
import { ElTag, ElScrollbar, ElTabs, ElTabPane } from "element-plus";
import { BerthStatusTagOptions, PlanStatusOptions } from "@/contans";
// 定义计划项的类型
interface PlanInfo {
  planId: string;
  status: string;
  workType: string;
  reason?: string;
  shipName?: string;
  mineNumber?: string;
  materialName?: string;
  grossWeight?: number | string;
  unloadedWeight?: number | string;
  plcRealTime?: number | string;
  residueWeight?: number | string;
  avgDischargeRate?: number | string;
  efficiencyByHour?: { rate: string; time: string; unloadWeight: string }[];
}

// 使用 Map 定义字段映射
const fieldMap = new Map<keyof PlanInfo, any>([
  ["shipName", { label: "船名", defaultValue: "-" }],
  ["mineNumber", { label: "矿号", defaultValue: "-" }],
  ["materialName", { label: "物资名称", defaultValue: "-" }],
  ["grossWeight", { label: "总重量", defaultValue: "-" }],
  ["unloadedWeight", { label: "已作业量", defaultValue: "-" }],
  ["plcRealTime", { label: "实时流量", defaultValue: "-" }],
  ["residueWeight", { label: "剩余重量", defaultValue: "-" }],
  ["avgDischargeRate", { label: "平均效率", defaultValue: "-" }]
]);

// 注入响应式数据
const popupData: any = inject("popupData");

// 计算泊位状态信息
const statusInfo = computed(() => {
  return (
    BerthStatusTagOptions[popupData.value.berthStatus] ?? {
      label: "未知",
      type: "info"
    }
  );
});
// 根据 title 过滤 fieldMap
const filteredFieldMap = computed(() => {
  const allowedTitles = ["1#", "4#"];
  if (allowedTitles.includes(popupData.value.title)) {
    return fieldMap;
  }
  const filtered = new Map(fieldMap);
  filtered.delete("plcRealTime");
  return filtered;
});

// 当前激活的 Tab
const activeTab = ref<string>("");

// 初始化激活的 Tab，仅在有多个计划单时生效
watch(
  () => popupData.value?.planInfoList,
  newList => {
    if (newList?.length > 1) {
      activeTab.value = newList[0].planId; // 默认激活第一个计划单
    } else {
      activeTab.value = ""; // 无需 Tab 时清空
    }
  },
  { immediate: true }
);

// 可配置的滚动条最大高度
const scrollbarMaxHeight = "500px";
</script>

<style scoped lang="less">
@title-color: #e2df3d;
@text-color: #fff;
@border-color: #fff;
@popup-bg: rgba(0, 95, 153, 0.6);

.popup-info {
  color: @text-color;
  border-radius: 8px;
  min-width: 300px;
  padding: 10px 8px;
  transition: all 0.3s ease-in-out;
  z-index: 1000;
  background-color: @popup-bg;

  .info {
    max-height: 500px;
  }
}
.header {
  display: flex;
  justify-content: space-between;
  // align-items: center;
  padding-right: 10px;
}
.info-title {
  font-size: 1.6rem;
  color: @title-color;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-text {
  margin: 3px 0;
  font-size: 1rem;
}
.plan-tabs {
  :deep(.el-tabs__header) {
    .el-tabs__item {
      color: @text-color; /* 白色字体 */
      font-size: 1rem; /* 与 info-text 一致 */
      font-weight: 600;
      &.is-active {
        color: @title-color; /* 黑色文字确保对比度 */
      }
    }
  }
}

.echats {
  // margin-top: 10px;
}
</style>
