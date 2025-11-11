<template>
  <div class="main">
    <SearchForm
      :loading="loading"
      :stausOptions="planStatusOptions"
      :getBerthList="getBerthList"
      :getPlanData="getPlanData"
    />

    <PureTableBar
      :title="transformI18n('imip.page2.obj19')"
      :columns="columns"
      @refresh="getPlanData">
      <template #title>
        <el-radio-group
          v-model="searchParams.pierType"
          @change="handlePierTypeChange"
        >
          <el-radio-button defaultValue="">{{
            transformI18n("imip.button.obj8")
          }}</el-radio-button>
          <!-- <el-radio-button value="1">{{
            transformI18n("imip.button.obj9")
          }}</el-radio-button>
          <el-radio-button value="2">{{
            transformI18n("imip.button.obj10")
          }}</el-radio-button> -->
        </el-radio-group>
      </template>
      <template #buttons>
        <el-button
          type="primary"
          :icon="useRenderIcon(AddFill)"
          @click="() => handleAddPlan()"
          >{{ transformI18n("imip.button.obj3") }}
        </el-button>

        <el-tooltip
          placement="top-start"
          effect="dark"
          :content="transformI18n('imip.button.obj11')"
        >
          <el-button
            type="info"
            :disabled="multipleIds?.length <= 0"
            :icon="useRenderIcon(Archive)"
            @click="handlePlanOperation(multipleIds, 'archive')"
            >{{ transformI18n("imip.button.obj4") }}
          </el-button>
        </el-tooltip>
        <!-- <Dropdown>
          <template #overlay>
            <Menu>
              <Menu.Item
                @click="
                  () => exportPlanOrders(multipleIds, ExportPlanType.Plan)
                "
                >{{ transformI18n("imip.button.obj5")
                }}{{ transformI18n(ExportPlanType.Plan) }}</Menu.Item
              >
              <Menu.Item
                @click="
                  () => exportPlanOrders(multipleIds, ExportPlanType.PlanRate)
                "
                >{{ transformI18n("imip.button.obj5")
                }}{{ transformI18n(ExportPlanType.PlanRate) }}</Menu.Item
              >
              <Menu.Item
                @click="
                  () =>
                    exportPlanOrders(
                      multipleIds,
                      ExportPlanType.PlantUnloadWeight
                    )
                "
                >{{ transformI18n("imip.button.obj5")
                }}{{
                  transformI18n(ExportPlanType.PlantUnloadWeight)
                }}</Menu.Item
              >
            </Menu>
          </template>
          <el-button type="success" :icon="useRenderIcon(Export)">
            {{ transformI18n("imip.button.obj5") }}
          </el-button>
        </Dropdown> -->
        <el-popconfirm
          :title="transformI18n('imip.button.obj15')"
          @confirm="() => deletePlanOrder(multipleIds)"
        >
          <template #reference>
            <el-button
              type="danger"
              :icon="useRenderIcon(Delete)"
              :disabled="!(multipleIds.length > 0)"
              >{{ transformI18n("imip.button.obj2") }}
            </el-button>
          </template>
        </el-popconfirm>
        <!-- <UploadDialog /> -->
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <div class="summary-container">
          <span
            v-for="key in Object.keys(SumMap)"
            :key="key"
            class="summary-item"
          >
            {{ transformI18n(SumMap[key]) }}:
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
            <el-button
              class="reset-margin"
              type="primary"
              :size="size"
              :icon="useRenderIcon(EditPen)"
              link
              :disabled="row.status === '9'"
              @click.stop="() => handleAddPlan(row.id)"
            >
              {{ transformI18n("imip.button.obj1") }}
            </el-button>

            <el-popconfirm
              :title="transformI18n('imip.button.obj16')"
              @confirm.stop="() => deletePlanOrder(row.id)"
            >
              <template #reference>
                <el-button
                  class="reset-margin"
                  type="danger"
                  :size="size"
                  style="margin-left: 4px"
                  :icon="useRenderIcon(Delete)"
                  link
                  @click.stop
                  >{{ transformI18n("imip.button.obj2") }}
                </el-button>
              </template>
            </el-popconfirm>
            <el-dropdown>
              <el-button
                class="ml-3 mt-[2px] reset-margin"
                color="#626aef"
                link
                style="margin-left: 4px"
                :size="size"
                :icon="Expand"
                plain
                >{{ transformI18n("imip.button.obj17") }}</el-button
              >
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="item in moreBtn"
                    :key="item.label"
                    :style="{ color: item.color }"
                    :disabled="getMoreBtn(row, item.key)"
                    :icon="useRenderIcon(item.icon)"
                    @click="() => item.click(row.id, row)"
                    >{{ transformI18n(item.label) }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
    <UnloadingOrderDiaLog
      v-model:visible="unloadingVisible"
      :planId="currentPlanId"
      :pierType="currentPlanPierType"
      :submitSancel="
        () => {
          unloadingVisible = false;
          getPlanData();
        }
      "
      :type="OptionPlanStatus.ADD"
    />
    <EditPlanForm1 ref="EditPlanForm1Ref" @success="getPlanData"/>
  </div>
</template>

<script setup lang="ts">
import "element-plus/dist/index.css";
import { onMounted, ref, reactive, provide, nextTick, computed, h } from "vue";
import { message, Tag } from "ant-design-vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import UnloadingOrderDiaLog from "./components/UnloadingOrderDiaLog/index.vue";
import { PureTableBar } from "@/components/RePureTableBar";
import { OptionPlanStatus } from "./utils/type";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Export from "@iconify-icons/ri/file-upload-line";
import AddFill from "@iconify-icons/ri/add-circle-line";
import { Expand } from "@element-plus/icons-vue";
import { useShipPlan } from "./utils/hooks";
import { useRouter } from "vue-router";
import SearchForm from "./components/SearchForm/index.vue";
import Eye from "@iconify-icons/ri/eye-line";
import Archive from "@iconify-icons/ri/archive-fill";
import Move from "@iconify-icons/ri/arrow-right-circle-line";
import UploadDialog from "./components/UploadDialog/index.vue";
import { Menu, Dropdown } from "ant-design-vue";
import { ElMessageBox } from "element-plus";
import { transformI18n } from "@/plugins/i18n";
import {
  Colors,
  PireTypeOptionsObj,
  PlanTypeOptionsObj,
  SumMap,
  ExportPlanType
} from "@/contans";
import { usePageSessionStorage } from "../hooks/usePageSessionStorage";
import { toPlanDocking } from "@/api/plan/planOrder";
import EditPlanForm1 from '@/views/shipPlan/EditPlanForm1.vue'
const router = useRouter();
const moreBtn = ref(null);
const unloadingVisible = ref(false);
const currentPlanId = ref("");
const currentPlanPierType = ref("");
const planStatusOptions = ref([]);
const { getPageSessionStorage, setPageSessionStorage } = usePageSessionStorage();
const EditPlanForm1Ref = ref();
const {
  columns,
  loading,
  getPlanData,
  planData,
  total,
  searchParams,
  deletePlanOrder,
  exportPlanOrders,
  getDictOptions,
  getBerthList,
  key,
  summaryData,
  handlePlanOperation,
  changeScreenStatus
} = useShipPlan();

// 提供 `loading` 和 `searchParams` 给子组件
provide("searchParams", searchParams);
const tableRef = ref();
const multipleIds = ref([]);

const handlePlanSessionStorage = () => {
  // 获取搜索参数
  const newSearchParams = getPageSessionStorage();
  if (newSearchParams) {
    pagination.pageNum = newSearchParams.pageNum;
    pagination.pageSize = newSearchParams.pageSize;
    searchParams.value = newSearchParams;
  }
};
moreBtn.value = [
  // {
  //   key: "1",
  //   label: "imip.button.obj18",
  //   icon: Move,
  //   color: "#E6A23C",
  //   click: (_, row: any) => handlePlanOperation(row, "move")
  // },
  // {
  //   key: "2",
  //   label: "imip.button.obj19",
  //   icon: AddFill,
  //   color: "#626aef",
  //   click: (id: string, row: any) => openUnloadingDialog(id, row)
  // },
  {
    key: "3",
    label: "imip.button.obj20",
    icon: Eye,
    color: "#409EFF",
    click: (id: string) => {
      setPageSessionStorage(searchParams.value);
      // handlePlanSessionStorage("add");
      router.push({
        path: "/unload",
        query: {
          planId: id
        }
      });
    }
  },
  // {
  //   key: "4",
  //   label: "imip.button.obj21",
  //   icon: Move,
  //   color: "#F1C40F",
  //   click: (id: string) => {
  //     handleDocking(id);
  //   }
  // }
];
const getMoreBtn = (row: any, key?: string) => {
  const statusNew = Number(row.status);
  // if (statusNew !== 2 && key === "4") {
  //   return true;
  // }
  // if (statusNew < 4 && key !== "4" && key !== "5") {
  //   return true;
  // }

  // if (statusNew === 8 && key === "1") {
  //   return false;
  // }
  // if (row.unloadStatus === "0" && key === "3") {
  //   return true;
  // }
  // if (row.unloadStatus === "2" && key === "2") {
  //   return true;
  // }
  // return false;
  if(statusNew>3){
    return false;
  }else{
    return true;
  }
};

const openUnloadingDialog = (planId: string, row: any) => {
  currentPlanId.value = planId;
  currentPlanPierType.value = row.pierType;
  nextTick(() => {
    unloadingVisible.value = true;
  });
};

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
const handleDocking = (id: string) => {
  ElMessageBox.confirm(
    transformI18n("imip.button.obj23"),
    transformI18n("imip.button.obj21"),
    {
      type: "warning"
    }
  ).then(async () => {
    const res: any = await toPlanDocking({ id });
    if (res.code === 200) {
      message.success(transformI18n("imip.button.obj24"));
      getPlanData();
    }
  });
};
const childColumns: any[] = [
  {
    label: "物料",
    prop: "materialName",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "装卸类型",
    prop: "remark01",
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
    label: "实际",
    prop: "tonnage",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "已作业量",
    prop: "unloadWeight",
    width: 500,
    align: "center",
    headerAlign: "center"
  },
  {
    label: "单位",
    prop: "remark02",
    width: 500,
    cellRenderer: ({ row }) => {
      if(row.remark02!==null&&
          row.remark02!==undefined&&
          row.remark02!==''){
        return row.remark02;
      }else{
        return "吨/T";
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
const handleSelectionChange = val => {
  const ids = val.map(item => item.id);
  multipleIds.value = ids;
};

const handleAddPlan = (rowId?: string) => {
  setPageSessionStorage(searchParams.value);
  // router.push({
  //   path: "/editPlan",
  //   query: rowId
  //     ? { id: rowId, type: "Edit" }
  //     : {
  //         type: "Add"
  //       }
  // });
  EditPlanForm1Ref.value.open(rowId);
};
const handleStatus = async () => {
  planStatusOptions.value = await getDictOptions("plan_status");
};
onMounted(async () => {
  handleStatus();
  // 获取搜索参数
  handlePlanSessionStorage();
  getPlanData();
});

</script>

<style lang="less" scoped>
.summary-container {
  display: flex;
  justify-content: center; /* 右对齐 */
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  background: #f0ebe4;
  // border-top: 1px solid #ddd;
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