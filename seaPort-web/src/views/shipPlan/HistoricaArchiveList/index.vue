<template>
  <div class="main">
    <SearchForm :loading="loading" :getBerthList="getBerthList" />

    <PureTableBar
      :title='transformI18n("imip.page2.obj20")'
      :columns="columns"
      @refresh="getPlanData"
    >
      <template #title>
        <el-radio-group
          v-model="searchParams.pierType"
          @change="handlePierTypeChange"
        >
          <el-radio-button defaultValue="">{{transformI18n("imip.button.obj8")}}</el-radio-button>
          <el-radio-button value="1">{{transformI18n("imip.button.obj9")}}</el-radio-button>
          <el-radio-button value="2">{{transformI18n("imip.button.obj10")}}</el-radio-button>
        </el-radio-group>
      </template>
      <template #buttons>
        <el-tooltip
          placement="top-start"
          effect="dark"
          :content='transformI18n("imip.page2.obj21")'
        >
          <el-button
            v-auth="['harbor:plan:cancelArchived']"
            type="warning"
            :disabled="multipleIds?.length <= 0"
            :icon="useRenderIcon(refresh)"
            @click="handleCancelArchived(multipleIds)"
          >{{ transformI18n("imip.page2.obj22") }}
          </el-button>
        </el-tooltip>
        <el-button
          type="success"
          :icon="useRenderIcon(Export)"
          @click="() => exportPlanOrders(multipleIds, ExportPlanType.Plan)"
        >{{ transformI18n("imip.button.obj5") }}
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <div class="summary-container">
          <span
            v-for="key in Object.keys(SumMap_Archive)"
            :key="key"
            class="summary-item"
          >
            {{ transformI18n(SumMap_Archive[key]) }}:
            <span class="summary-value">{{ summaryData[key] }}</span>
          </span>
        </div>
        <pure-table
          ref="tableRef"
          adaptive
          row-key="id"
          :loading="loading"
          :size="size"
          :data="planData"
          showOverflowTooltip
          :columns="dynamicColumns"
          border
          :header-cell-style="{
            background: 'var(--el-fill-color-light)',
            color: 'var(--el-text-color-primary)'
          }"
          :pagination="{
            pageSize: pagination.pageSize,
            pageSizes: [10, 20, 50],
            currentPage: pagination.pageNum,
            total
          }"
          @selection-change="handleSelectionChange"
          @page-size-change="handleSizeChange"
          @page-current-change="handleCurrentChange">
          <template #expand="{ row }">
            <pure-table
              :data="row.params.subMaterial"
              :columns="childColumns"
              border/>
          </template>
          <template #pierType="{ row }">
            <el-tag
              v-if="row.pierType"
              effect="plain"
              round
              :type="PireTypeOptionsObj[row.pierType]?.color"
              >{{ PireTypeOptionsObj[row.pierType]?.label }}</el-tag
            >
          </template>
          <template #status="{ row }">
            <el-tag
              :style="{
                color: Colors[row?.status],
                border: `1px solid ${Colors[row?.status]}`
              }"
              effect="plain"
            >
              {{
                planStatusOptions.find(item => item.value === row.status)?.label
              }}
            </el-tag>
          </template>
          <template #planType="{ row }">
            <Tag
              v-if="row.planType"
              :color="PlanTypeOptionsObj[row.planType]?.type"
              >{{ PlanTypeOptionsObj[row.planType]?.label }}</Tag
            >
          </template>
          <template #operation="{ row }">
            <div class="flex justify-around">
              <el-button
                v-if="!isAllowEdit"
                class="reset-margin"
                type="primary"
                :size="size"
                :icon="useRenderIcon(Eyes)"
                link
                @click.stop="() => handleAddPlan(row.id, 'check')"
              >{{ transformI18n("imip.page2.obj23") }}
              </el-button>
              <!-- <el-button
                v-else
                class="reset-margin"
                type="primary"
                :size="size"
                style="margin-left: 4px"
                :icon="useRenderIcon(EditPen)"
                link
                @click.stop="() => handleAddPlan(row.id)"
              >{{ transformI18n("imip.button.obj1") }}
              </el-button> -->
              <el-button
                class="reset-margin"
                link
                color="#626aef"
                :size="size"
                style="margin-left: 4px"
                :icon="useRenderIcon(List)"
                plain
                @click.stop="() => goUnload(row.id)"
              >{{ transformI18n("imip.page2.obj24") }}
              </el-button>
            </div>
          </template>
        </pure-table>
      </template></PureTableBar
    >
  </div>
</template>

<script setup lang="ts">
import "element-plus/dist/index.css";
import { onMounted, ref, reactive, provide, computed } from "vue";
import { Tag } from "ant-design-vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { PureTableBar } from "@/components/RePureTableBar";
import EditPen from "@iconify-icons/ep/edit-pen";
import Eyes from "@iconify-icons/ri/eye-line";
import List from "@iconify-icons/ri/list-check";
import refresh from "@iconify-icons/ri/refresh-line";
import Export from "@iconify-icons/ri/file-upload-line";
import { useShipPlan } from "../utils/hooks";
import { useRouter } from "vue-router";
import SearchForm from "./Search.vue";
import {
  Colors,
  PireTypeOptionsObj,
  PlanTypeOptionsObj,
  SumMap_Archive,
  ExportPlanType
} from "@/contans";
import { useUserStore } from "@/store/modules/user";
import { usePageSessionStorage } from "@/views/hooks/usePageSessionStorage";
import { ElMessage } from "element-plus";
import { cancelArchivedPlan } from "@/api/plan/planOrder";
import { transformI18n } from "@/plugins/i18n";

const router = useRouter();
const planStatusOptions = ref([]);
const {
  columns,
  loading,
  getPlanData,
  planData,
  total,
  searchParams,
  exportPlanOrders,
  getDictOptions,
  getBerthList,
  key,
  summaryData
} = useShipPlan();
const userStore = useUserStore();
const permissions = userStore.permissions;
const { getPageSessionStorage, setPageSessionStorage } =
  usePageSessionStorage();

const isAllowEdit = computed(() => {
  return (
    permissions.includes("harbor:plan:archivedUpdate") ||
    permissions.includes("*:*:*")
  );
});
// 提供 `loading` 和 `searchParams` 给子组件
provide("searchParams", searchParams);
provide("getPlanData", getPlanData);
const tableRef = ref();
const multipleIds = ref([]);
const pagination = reactive({
  pageSize: 50,
  pageNum: 1
});
const handleSizeChange = size => {
  pagination.pageSize = size;
  searchParams.value.pageSize = size;
  getPlanData();
};
const handlePierTypeChange = value => {
  searchParams.value.pierType = value;
  getPlanData();
};

const handleCurrentChange = page => {
  pagination.pageNum = page;
  searchParams.value.pageNum = page;
  getPlanData();
};

const handleSelectionChange = val => {
  const ids = val.map(item => item.id);
  multipleIds.value = ids;
};

const handleAddPlan = (rowId?: string, type?: string) => {
  setPageSessionStorage(searchParams.value);
  if (type === "check") {
    router.push({
      path: "/checkPlan",
      query: {
        id: rowId
      }
    });
    return;
  }
  router.push({
    path: "/checkPlan",
    query: {
      id: rowId
    }
  });
};
const goUnload = (id: string) => {
  setPageSessionStorage(searchParams.value);
  router.push({
    path: "/unload",
    query: {
      planId: id
    }
  });
};
const handleCancelArchived = async (ids: string | string[]) => {
  const res: any = await cancelArchivedPlan(ids);
  if (res.code === 200) {
    ElMessage({
      message: transformI18n("imip.button.obj39"),
      type: "success"
    });
    getPlanData();
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};
const handleStatus = async () => {
  planStatusOptions.value = await getDictOptions("plan_status");
  // key.value++;
};
const handlePlanSessionStorage = () => {
  // 获取搜索参数
  const newSearchParams = getPageSessionStorage();
  if (newSearchParams) {
    pagination.pageNum = newSearchParams.pageNum;
    pagination.pageSize = newSearchParams.pageSize;
    searchParams.value = newSearchParams;
  }
};
onMounted(async () => {
  handleStatus();
  handlePlanSessionStorage();
  getPlanData();
});
const childColumns: any[] = [
  {
    label: "物料",
    prop: "materialName",
    width: 500,
    align: "center",
    headerAlign: "center" 
  },
  {
    label: "使用单位",
    prop: "usageUnit",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "实际(吨/件)",
    prop: "tonnage",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "已作业量(吨/件)",
    prop: "unloadWeight",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "单位",
    prop: "packageNum",
    width: 500,
    cellRenderer: ({ row }) => {
      const p = row?.packageNum;
      if (p==1) {
        return "按吨";
      }else if(p==2){
        return "按件";
      }else{
        return "不确定"
      }
    },
    align: "center",
    headerAlign: "center"
  },
  // {
  //   label: "是否装卸完毕",
  //   prop: "isFinish",
  //   width: 500,
  //   cellRenderer: ({ row }) => {
  //     const i = row?.isFinish;
  //     if (i==1) {
  //       return "已装卸完";
  //     }else{
  //       return "未装卸完"
  //     }
  //   },
  //   align: "center",
  //   headerAlign: "center"
  // }
]
</script>

<style lang="less" scoped>
.summary-container {
  display: flex;
  justify-content: center; /* 右对齐 */
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  background: #f0ebe4;
}

.summary-item {
  margin-left: 20px;
  color: #666;
  .summary-value {
    color: #333;
    font-weight: bold;
    margin-left: 10px;
    font-size: 16px;
  }
}
</style>
