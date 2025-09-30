<template>
  <div class="main">
    <PlusSearch
      v-model="searchFormstate"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] pb-[12px] overflow-auto"
      :columns="formColumns"
      :show-number="4"
      label-width="80"
      :colProps="{
        span: 6
      }"
      label-position="right"
      :search-loading="loading"
      @search="handleSearch"
      @reset="handleRest"
    />
    <PureTableBar
      :title='transformI18n("imip.page2.obj27")'
      :columns="unloadColums"
      :isExpandAll="false"
      :tableRef="tableRef?.getTableRef()"
      @refresh="getUnloadData"
    >
      <template #title>
        <div class="font-bold">
          {{ transformI18n("imip.page2.obj27") }}
          <el-tag :type="unloadStatus?.type">{{ unloadStatus?.label }}</el-tag>
        </div>
      </template>
      <template #buttons>
        <!-- <el-button
          type="primary"
          :disabled="isFinished"
          :icon="useRenderIcon(AddFill)"
          @click="openUnloadingDialog(OptionPlanStatus.ADD)"
        >{{ transformI18n("imip.button.obj3") }}
        </el-button> -->
        <Dropdown>
          <template #overlay>
            <Menu>
              <Menu.Item @click="() => exportWorkList(planId, multipleIds)"
                >{{ transformI18n("imip.page2.obj28") }}</Menu.Item
              >
              <Menu.Item @click="() => exportWorkLogList(planId)"
                >{{ transformI18n("imip.page2.obj29") }}</Menu.Item
              >
            </Menu>
          </template>
          <el-button type="success" :icon="useRenderIcon(Export)">
            {{ transformI18n("imip.button.obj5") }}
          </el-button>
        </Dropdown>
        <!-- <el-button
          type="success"
          :icon="useRenderIcon(Export)"
          @click="() => exportWorkList(planId, multipleIds)"
        >
          导出
        </el-button> -->
        <el-tooltip
          placement="top-start"
          :width="200"
          effect="dark"
          :content='transformI18n("imip.page2.obj30")'
        >
          <el-button
            v-if="!isFinished"
            type="warning"
            :icon="useRenderIcon(Finished)"
            :disabled="isFinished"
            @click="handleFinishWork(planId)"
          >{{ transformI18n("imip.page2.obj31") }}
          </el-button>
        </el-tooltip>
        <el-tooltip
          placement="top-start"
          :width="200"
          effect="dark"
          :content='transformI18n("imip.page2.obj32")'
        >
          <el-button
            v-if="isFinished"
            v-auth="['harbor:unloadWork:cancelComplete']"
            type="warning"
            :icon="useRenderIcon(refresh)"
            :disabled="planDetail.status === '2'"
            @click="handleCancelCompleteWork(planId)"
          >{{ transformI18n("imip.page2.obj33") }}
          </el-button>
        </el-tooltip>
        <el-popconfirm
          :title='transformI18n("imip.page2.obj34")'
          @confirm="() => deleteUnLoad(multipleIds)"
        >
          <template #reference>
            <el-button
              type="danger"
              :disabled="multipleIds.length === 0 || isFinished"
              :icon="useRenderIcon(Delete)"
            >{{ transformI18n("imip.button.obj2") }}
            </el-button>
          </template>
        </el-popconfirm>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <el-descriptions
          class="margin-bottom"
          title=""
          :column="7"
          size="default"
          border
        >
          <el-descriptions-item
            v-for="item in planInfoOptions"
            :key="item.label"
          >
            <template #label>
              <div class="cell-item">{{ transformI18n(item.label) }}:</div>
            </template>
            <div
              v-if="item.label !== 'imip.page2.obj35'"
              class="cell-value"
              :class="item.label === 'imip.page1.obj21' ? 'time' : ''"
            >
              <TypographyText
                :style="ellipsis ? { maxWidth: '300px' } : {}"
                :ellipsis="ellipsis ? { tooltip: item.value } : false"
                :content="item.value ?? '-'"
              />
            </div>
            <div v-else class="cell-value">
              <el-tag
                :style="{
                  color: Colors[item.value],
                  border: `1px solid ${Colors[item.value]}`
                }"
                effect="plain"
              >
                {{ planStatusOptions.find(i => i.value === item.value)?.label }}
              </el-tag>
            </div>
          </el-descriptions-item>
        </el-descriptions>
        <pure-table
          ref="tableRef"
          adaptive
          row-key="duId"
          :size="size"
          :data="unloadData"
          :loading="loading"
          :columns="dynamicColumns"
          showOverflowTooltip
          :border="false"
          :summary-method="getSummaries"
          :summary-cell-style="{
            fontWeight: 'bold'
          }"
          show-summary
          :header-cell-style="{
            background: 'var(--el-fill-color-light)',
            color: 'var(--el-text-color-primary)'
          }"
          :pagination="{
            pageSize: pagination.pageSize,
            currentPage: pagination.pageNum,
            total: unloadTotal
          }"
          @selection-change="handleSelectionChange"
          @page-size-change="handleSizeChange"
          @page-current-change="handleCurrentChange"
        >
          <template #expand="{ row }">
            <div class="bg-gray-100">
              <LogTable
                :key="row.localKey"
                :duId="row.duId"
                :isOperation="!isFinished"
              />
            </div>
          </template>
          <template #workType="{ row }">
            <Badge
              style="margin-left: 5px"
              :status="UnloadWorkTypeTagOptions[row.workType].type"
              :text="UnloadWorkTypeTagOptions[row.workType].label"
            />
          </template>
          <template #operation="{ row }">
            <!-- <el-button
              class="reset-margin"
              type="primary"
              :size="size"
              :disabled="row.workType === '2'"
              link
              :icon="useRenderIcon(EditPen)"
              @click="openUnloadingDialog(OptionPlanStatus.EDIT, row.duId)"
            >{{ transformI18n("imip.button.obj1") }}
            </el-button> -->
            <Dropdown :disabled="row.workType === '2'">
              <!-- <el-button
                class="reset-margin"
                color="#626aef"
                :size="size"
                link
                plain
                :disabled="row.workType === '2'"
                :icon="MenuIcon"
              >{{ transformI18n("imip.page1.obj2") }}
              </el-button> -->
              <template #overlay>
                <Menu>
                  <Menu.Item
                    v-for="item in handleMenu(row.workType)"
                    :key="item.key"
                    @click="
                      handleUnloadWork(row, item.value, () => {
                        refreshRow(row.duId);
                        getUnloadData();
                        getPlanDetail(planId);
                      })
                    "
                  >
                    <a
                      href="javascript:;"
                      :style="{
                        color: item.color,
                        display: 'flex',
                        alignItems: 'center'
                      }"
                      ><component
                        :is="item.icon"
                        style="width: 14px; height: 14px; margin-right: 5px"
                      />
                      {{ item.label }}</a
                    >
                  </Menu.Item>
                </Menu>
              </template>
            </Dropdown>
            <el-popconfirm
              :title='transformI18n("imip.button.obj6")'
              @confirm="() => deleteUnLoad(row.duId)"
            >
              <template #reference>
                <el-button
                  class="reset-margin"
                  type="danger"
                  :size="size"
                  :disabled="isFinished"
                  :icon="useRenderIcon(Delete)"
                  link
                >{{ transformI18n("imip.button.obj2") }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </pure-table>
      </template></PureTableBar
    >
    <UnloadingOrderDiaLog
      v-model:visible="unloadingVisible"
      :planId="planId"
      :pierType="planDetail?.pierType"
      :unloadId="currentUnloadId"
      :type="currentType"
      :cancel="
        () => {
          currentUnloadId = '';
        }
      "
      :submitSancel="
        () => {
          unloadingVisible = false;
          getUnloadData();
        }
      "
    />
  </div>
</template>

<script setup lang="ts">
import "element-plus/dist/index.css";
import { onMounted, ref, reactive, computed } from "vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import UnloadingOrderDiaLog from "./components/UnloadingOrderDiaLog/index.vue";
import { PureTableBar } from "@/components/RePureTableBar";
import { OptionPlanStatus } from "./utils/type";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Export from "@iconify-icons/ri/file-upload-line";
import AddFill from "@iconify-icons/ri/add-circle-line";
import Finished from "@iconify-icons/ri/check-fill";
import { Badge } from "ant-design-vue";
import { useRoute } from "vue-router";
import refresh from "@iconify-icons/ri/refresh-line";
import { Menu as MenuIcon } from "@element-plus/icons-vue";
import LogTable from "./components/LogTable/index.vue";
import {
  getUnloadList,
  deleteHarborUnload,
  finishHarborUnload,
  cancelCompleteHarborUnload
} from "@/api/plan/unloadingOrder";
import { ElMessage } from "element-plus";
import { UnloadStatusOptions, UnloadWorkTypeTagOptions } from "@/contans";
import { PlusSearch } from "plus-pro-components";
import "plus-pro-components/es/components/search/style/css";
import { useUnloadWork } from "./utils/unloadWork";
import { getHarborPlan } from "@/api/plan/planOrder";
import { TypographyText } from "ant-design-vue";
import { Menu, Dropdown } from "ant-design-vue";
import { transformI18n } from "@/plugins/i18n";
import { Colors } from "@/contans";
const ellipsis = ref(true);
const {
  unloadColums,
  formColumns,
  handleMenu,
  getDictOptions,
  exportWorkList,
  exportWorkLogList,
  handleUnloadWork
} = useUnloadWork();
const planStatusOptions = ref([]);
const unloadingVisible = ref(false);
const unloadData = ref([]);
const unloadTotal = ref(0);
const loading = ref(false);
const currentUnloadId = ref("");
const currentType = ref("");
const searchFormstate = ref({});
const planId = useRoute().query.planId as string;
const searchParams: any = ref({});
const tableRef = ref();
const multipleIds = ref([]);
const mapParams = ref({});
const planDetail: any = ref({});
const isFinished = ref(false);
const unloadStatus: any = computed(() => {
  if (planDetail.value.status) {
    return UnloadStatusOptions.find(
      item => item.value === planDetail.value.unloadStatus
    );
  }
  return UnloadStatusOptions[1];
});

const refreshRow = (duId: string) => {
  const index = unloadData.value.findIndex(item => item.duId === duId);
  if (index !== -1) {
    unloadData.value[index] = {
      ...unloadData.value[index], // 保持原有数据
      localKey: Date.now() + Math.random() // 生成唯一 key
    };
  }
};
const planInfoOptions = computed(() => {
  if (planDetail.value.id) {
    const { shipName, materialName, hbName, tonnage, operationTime, status } =
      planDetail.value;
    return [
      {
        label: "imip.page1.obj1",
        value: shipName
      },
      // {
      //   label: "imip.page1.obj3",
      //   value: materialName
      // },
      {
        label: "imip.page2.obj35",
        value: status
      },
      {
        label: "imip.page1.obj4",
        value: hbName
      },
      {
        label: "imip.page1.obj14",
        value: tonnage
      },
      {
        label: "imip.page1.obj21",
        value: operationTime
      }
    ];
  }
  return [];
});

const pagination = reactive({
  pageSize: 10,
  pageNum: 1
});
const handleSizeChange = size => {
  pagination.pageSize = size;
  getUnloadData();
};

const handleCurrentChange = page => {
  pagination.pageNum = page;
  searchParams.value.pageNum = page;
  getUnloadData();
};

const handleSelectionChange = val => {
  const ids = val.map(item => item.duId);
  multipleIds.value = ids;
};
const getSummaries = (param: any) => {
  const { columns } = param;
  const sums: string[] = [];
  // 字段映射到服务器返回的数据
  const fieldsMap = {
    unloadNum: "totalUnloadNum",
    avgDischargeRate: "totalAvgDischargeRate",
    effectiveRate: "totalEffectiveRate",
    pauseInterval: "totalPauseInterval",
    totalUnloadWeight: "totalUnloadWeight",
    effectiveTime: "totalEffectiveTime"
  };
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = transformI18n("imip.page3.obj22");
      return;
    }

    // 直接使用后端返回的数据
    const key = fieldsMap[column.property];
    sums[index] =
      key && mapParams?.value?.[key] !== undefined
        ? `${mapParams?.value?.[key]}`
        : "-";
  });

  return sums;
};

const openUnloadingDialog = (type, unloadId?: string) => {
  if (type === OptionPlanStatus.ADD) {
    currentUnloadId.value = "";
  } else {
    currentUnloadId.value = unloadId;
  }
  currentType.value = type;
  unloadingVisible.value = true;
};
const deleteUnLoad = async (ids: string | string[]) => {
  const res: any = await deleteHarborUnload(ids);
  if (res.code === 200) {
    ElMessage({
      message: transformI18n("imip.page3.obj11"),
      type: "success"
    });
    getUnloadData();
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
  getUnloadData();
};

const handleFinishWork = async (planId: string) => {
  const res: any = await finishHarborUnload(planId);
  if (res.code === 200) {
    ElMessage({
      message: transformI18n("imip.page3.obj12"),
      type: "success"
    });
    getUnloadData();
    getPlanDetail(planId);
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};
const handleCancelCompleteWork = async (planId: string) => {
  const res: any = await cancelCompleteHarborUnload(planId);
  if (res.code === 200) {
    ElMessage({
      message: transformI18n("imip.page3.obj13"),
      type: "success"
    });
    getUnloadData();
    getPlanDetail(planId);
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};
const getUnloadData = async () => {
  searchParams.value.planId = planId;
  loading.value = true;
  try {
    const res: any = await getUnloadList(searchParams.value);
    if (res.code === 200) {
      unloadData.value = res.rows;
      unloadTotal.value = res.total;
      mapParams.value = res.mapParams;
    } else {
      ElMessage({
        message: res.msg,
        type: "error"
      });
    }
    loading.value = false;
  } catch (error) {
    loading.value = false;
  }
};
const handleSearch = values => {
  Object.entries(values).forEach(([key, value]) => {
    if (key === "createTime") {
      searchParams.value["createdStartTime"] = value[0];
      searchParams.value["createdEndTime"] = value[1];
      return;
    }
    searchParams.value[key] = value;
  });
  getUnloadData();
};

const handleRest = () => {
  searchParams.value = {};
  getUnloadData();
};
const getPlanDetail = async id => {
  const res: any = await getHarborPlan(id);
  if (res.code === 200) {
    planDetail.value = res.data;
    isFinished.value = res.data.unloadStatus === "2";
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};

onMounted(async () => {
  getPlanDetail(planId);
  planStatusOptions.value = await getDictOptions("plan_status");
  getUnloadData();
});
</script>

<style lang="less" scoped>
.main {
  padding: 10px;
  ::v-deep(.ant-badge-status-dot) {
    width: 8px;
    height: 8px;
  }
  ::v-deep(.el-table__footer) {
    font-weight: bold;
  }
}

.cell-item {
  display: flex;
  align-items: center;
}
.cell-value {
  font-weight: bold;
  text-align: center;
}
.time {
  color: #409eff;
}
.margin-bottom {
  margin-bottom: 8px;
}
</style>
