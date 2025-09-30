<template>
  <div v-if="loading" class="loading">
    <Spin
      size="large"
      tip="Loading..."
      :spinning="loading"
      style="color: #fff"
    />
  </div>
  <div v-else class="berth-dialog">
    <div
      v-if="berthData.berthStatus === '0' || berthData.berthStatus === '2'"
      class="free"
    >
      <div class="berth-status-card">
        泊位状态：
        <el-tag :type="statusInfo.type" effect="dark">
          {{ statusInfo.label }}
        </el-tag>
      </div>
      <template v-if="berthData.berthStatus === '2'">
        <div class="info-card">
          <h3>维护信息</h3>
          <p>
            <strong>维护原因：</strong>
            {{ berthData.remark || "无备注" }}
          </p>
        </div>
      </template>
      <div v-else class="empty-container">
        <el-empty
          description="暂无占用该泊位的船只列表"
          :image-size="100"
          class="custom-empty"
        />
      </div>
    </div>
    <div v-else class="left">
      <!-- 只有一条计划单，直接展示 -->
      <div v-if="planInfoList.length === 1" class="tab-content">
        <div class="plan-content">
          <div class="info-text">
            <strong>计划单状态：</strong>
            <ElTag
              v-if="planInfoList[0].workType !== '1'"
              :color="
                PlanStatusOptions[planInfoList[0].status]?.color ?? 'info'
              "
              effect="dark"
            >
              {{ PlanStatusOptions[planInfoList[0].status]?.label ?? "其他" }}
            </ElTag>
            <ElTag v-else type="danger" effect="dark">暂停中 </ElTag>
          </div>
          <div v-if="planInfoList[0].workType === '1'" class="info-text">
            <strong>暂停原因：</strong>{{ planInfoList[0].reason || "-" }}
          </div>
          <div
            v-for="[key, field] in filteredFieldMap"
            :key="key"
            class="info-text"
          >
            <strong>{{ field.label }}：</strong
            >{{ planInfoList[0][key] || field.defaultValue }}
          </div>
          <div
            v-if="planInfoList[0].efficiencyByHour?.length > 0"
            class="echats"
          >
            每小时效率图：
            <EfficiencyChart
              height="280px"
              :efficiencyData="planInfoList[0].efficiencyByHour || []"
            />
          </div>
        </div>
      </div>
      <!-- 多条计划单，使用 Tab 切换 -->
      <ElTabs v-else v-model="activeTab" class="plan-tabs">
        <ElTabPane
          v-for="(item, index) in planInfoList"
          :key="item.planId"
          :label="`船 ${index + 1}`"
          :name="item.planId"
        >
          <div class="plan-content">
            <div class="info-text">
              <strong>计划单状态：</strong>
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
            <div v-if="item.efficiencyByHour?.length > 0" class="echats">
              每小时效率图：
              <EfficiencyChart
                height="280px"
                :efficiencyData="item.efficiencyByHour || []"
              />
            </div>
          </div>
        </ElTabPane>
      </ElTabs>
    </div>

    <div class="right">
      <div class="title">
        关联船只列表：<span class="count">{{ rightList.length }}</span
        >条
      </div>
      <RightTable
        :tableData="rightList"
        :planStatusOptions="planStatusOptions"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import { getHarborBerthPlan } from "@/api/plan/planOrder";
import { computed, onMounted, ref, watch } from "vue";
import { Spin } from "ant-design-vue";
import { listData } from "@/api/system/dict/data";
import { BerthStatusTagOptions, PlanStatusOptions } from "@/contans";
import RightTable from "./RightTable.vue";
import EfficiencyChart from "./EfficiencyChart.vue";
import { transformI18n } from "@/plugins/i18n";
const { berthInfo } = defineProps<{
  berthInfo: any;
}>();
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

// 获取某个字典（例如 "shipStatus"）
const loading = ref(false);
const planInfoList = ref([]);
const berthData: any = ref({});
const rightList = ref([]);
const planStatusOptions = ref([]);
const activeTab = ref<string>("0");

// 使用 Map 定义字段映射
const fieldMap = new Map<keyof PlanInfo, any>([
  ["shipName", { label: '船名', defaultValue: "-" }],
  ["mineNumber", { label: "矿号", defaultValue: "-" }],
  ["materialName", { label: "物资名称", defaultValue: "-" }],
  ["grossWeight", { label: "总重量", defaultValue: "-" }],
  ["unloadedWeight", { label: "已作业量", defaultValue: "-" }],
  ["plcRealTime", { label: "实时流量", defaultValue: "-" }],
  ["residueWeight", { label: "剩余重量", defaultValue: "-" }],
  ["avgDischargeRate", { label: "平均效率", defaultValue: "-" }]
]);
// 根据 title 过滤 fieldMap
const filteredFieldMap = computed(() => {
  const allowedTitles = ["1#", "4#"];
  if (allowedTitles.includes(berthInfo.berthName)) {
    return fieldMap;
  }
  const filtered = new Map(fieldMap);
  filtered.delete("plcRealTime");
  return filtered;
});

// 当前激活的 Tab

const getDictOptions = async (type: string) => {
  const res: any = await listData({
    dictType: type,
    pageSize: 50
  });
  if (res.code === 200) {
    return res.rows?.map((item: any) => {
      return {
        label: item.dictLabel,
        value: item.dictValue
      };
    });
  }
};
// 获取泊位状态标签
const statusInfo = computed(() => {
  return (
    BerthStatusTagOptions[berthData.value.berthStatus] ?? {
      label: "未知",
      type: "info"
    }
  );
});
const getBerthPlan = async (berthInfo: any) => {
  loading.value = true;
  const res: any = await getHarborBerthPlan(berthInfo.berthId);
  if (res.code === 200) {
    planInfoList.value = res.data.planInfoList;
    rightList.value = res.data.dockPlanList;
    if (res.data.planInfoList.length > 0) {
      activeTab.value = res.data.planInfoList[0]?.planId; // 默认激活第一个计划单1
    }
    berthData.value.berthStatus = res.data.berthStatus;
    berthData.value.remark = res.data.remark;
    setTimeout(() => {
      loading.value = false;
    }, 300);
  }
};
watch(
  () => berthInfo,
  berthInfo => {
    if (berthInfo.berthId) {
      getBerthPlan(berthInfo);
    }
  }
);

onMounted(async () => {
  getBerthPlan(berthInfo);
  planStatusOptions.value = await getDictOptions("plan_status");
});
</script>
<style lang="scss" scoped>
.berth-dialog {
  display: flex;
  min-height: 300px;
  max-height: 630px;
  color: #fff;
  overflow: hidden;
  gap: 15px;

  .left {
    width: 48%;
    border-radius: 8px;
    overflow-y: auto;

    .info-text {
      margin: 5px 0;
      font-size: 1rem;
      line-height: 1.4;
      gap: 8px;

      strong {
        min-width: 80px;
        font-weight: 600;
      }

      .el-tag {
        height: 24px;
        line-height: 24px;
        font-size: 12px;
        padding: 0 10px;
        border-radius: 4px;
      }
    }

    .echats {
      padding: 10px;
      background: rgba(0, 0, 0, 0.1);
      border-radius: 6px;

      > span {
        font-size: 14px;
        font-weight: 600;
      }
    }

    .plan-tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 10px;

        .el-tabs__item {
          color: #ccc;
          font-size: 14px;
          font-weight: 500;
          padding: 0 20px;

          &.is-active {
            color: #fff;
            font-weight: 600;
            background: rgba(255, 255, 255, 0.15);
            border-radius: 4px;
          }

          &:hover {
            color: #fff;
          }
        }

        .el-tabs__nav-wrap::after {
          background: rgba(255, 255, 255, 0.1);
        }
      }
    }
  }

  .free {
    width: 30%;
    padding: 20px;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .berth-status-card {
      padding: 12px;
      background: rgba(255, 255, 255, 0.15);
      border-radius: 6px;
      text-align: center;
      font-size: 14px;

      .el-tag {
        height: 24px;
        line-height: 24px;
        font-size: 12px;
        padding: 0 10px;
        border-radius: 4px;
      }
    }

    .info-card {
      padding: 15px;
      background: rgba(255, 255, 255, 0.15);
      border-radius: 6px;

      h3 {
        margin: 0 0 12px;
        font-size: 16px;
        font-weight: 600;
      }

      p {
        margin: 0;
        font-size: 14px;
        line-height: 1.6;
        word-break: break-all;
      }
    }

    .empty-container {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 6px;

      .custom-empty {
        color: #fff;

        :deep(.el-empty__description) {
          color: #ccc;
          font-size: 14px;
        }

        :deep(.el-empty__image) {
          opacity: 0.8;
        }
      }
    }
  }

  .right {
    flex: 1;
    min-width: 300px;
    padding: 10px;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    .title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 15px;

      .count {
        color: #f77979;
        font-weight: 700;
        margin-left: 8px;
      }
    }
  }
}
.loading {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style>
