<template>
  <div class="main">
    <PlusForm
      ref="plusFormRef"
      v-model="formData"
      v-loading="queryLoading"
      :group="group"
      :rules="rules"
      :row-props="{ gutter: 20 }"
      :col-props="{
        span: 8
      }"
      footerAlign="center"
      label-width="120px"
      label-position="top"
      :submit-loading="submitPlanLoading"
      @submit="handleSubmit"
      @submit-error="handleSubmitError"
    >
      <template #group-header="{ title, icon }">
        <div class="custom-group-header">
          <span>
            {{ title }}
            <el-icon> <component :is="icon" /> </el-icon>
          </span>
          <!-- <el-button type="primary"> 添加</el-button> -->
        </div>
      </template>
      <!-- 自定义提交按钮 -->
      <template #footer="{ handleSubmit, handleReset }">
        <div style="margin: 0 auto" class="fixed-footer">
          <el-button
            class="mr-4"
            type="info"
            size="large"
            @click="handleBack"
            >{{ transformI18n("login.pureBack") }}</el-button
          >
          <span v-if="path === '/editPlan'">
            <el-button type="primary" size="large" @click="handleSubmit">{{
              transformI18n("imip.button.obj34")
            }}</el-button>
            <el-button size="large" type="warning" @click="handleReset">{{
              transformI18n("imip.button.obj38")
            }}</el-button>
          </span>
          <span v-else>
            <el-button
              v-auth="['harbor:plan:archivedUpdate']"
              type="primary"
              size="large"
              @click="handleSubmit"
              >{{ transformI18n("imip.button.obj34") }}</el-button
            >
            <el-button
              v-auth="['harbor:plan:archivedUpdate']"
              size="large"
              type="warning"
              @click="handleReset"
              >{{ transformI18n("imip.button.obj38") }}</el-button
            >
          </span>
        </div>
      </template>
    </PlusForm>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, computed } from "vue";
import { FieldValues, PlusForm, PlusFormGroupRow } from "plus-pro-components";
import { CreditCard, Calendar, Soccer, Ship } from "@element-plus/icons-vue";
import "plus-pro-components/es/components/search/style/css";
import { useShipPlan } from "./utils/hooks";
import { useRoute, useRouter } from "vue-router";
import { getHarborPlan } from "@/api/plan/planOrder";
import { ElMessage } from "element-plus";
import type { FormRules } from "element-plus";
import { transformI18n } from "@/plugins/i18n";
import { useI18n } from "vue-i18n";
const {
  updatePlanOrder,
  submitPlanLoading,
  getBerthList,
  berthListOptions,
  fetchMaterialName
} = useShipPlan();
const path = computed(() => useRoute().path);
const queryLoading = ref(false);
const formData: any = ref({});
const plusFormRef = ref<InstanceType<typeof PlusForm> | null>(null);
const router = useRouter();
const route = useRoute();
const planId = route.query.id;
const { locale } = useI18n();
const getPlanDetail = async id => {
  queryLoading.value = true;
  const res: any = await getHarborPlan(id);
  if (res.code === 200) {
    formData.value = res.data;
    queryLoading.value = false;
  } else {
    queryLoading.value = false;
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};
const handleSubmit = (values: FieldValues) => {
  values.hbName = berthListOptions.value?.find(
    item => item.value === values?.hbId
  )?.label;
  updatePlanOrder(values, planId);
};
const handleSubmitError = (err: any) => {
  console.error(err, "err");
};

const handleBack = () => {
  if (path.value === "/checkPlan") {
    router.push({
      path: "/archivePlan"
    });
    return;
  }
  router.push({
    path: "/shipPlan"
  });
};
const rules = computed<FormRules>(() => {
  const ruleObj: FormRules = {
    shipRade: [
      {
        required: true,
        message: transformI18n("imip.page2.obj1"),
        trigger: "blur"
      }
    ],
    shipName: [
      {
        required: true,
        message: transformI18n("imip.page2.obj2"),
        trigger: "blur"
      }
    ],
    shipLength: [
      {
        required: true,
        message: transformI18n("imip.page2.obj3"),
        trigger: "blur"
      }
    ],
    usageUnit: [
      {
        required: true,
        message: transformI18n("imip.page2.obj4"),
        trigger: "blur"
      }
    ],
    materialName: [
      {
        required: true,
        message: transformI18n("imip.page2.obj5"),
        trigger: "blur"
      }
    ],
    hbId: [
      {
        required: true,
        message: transformI18n("imip.page2.obj6"),
        trigger: "blur"
      }
    ],
    remark01: [
      {
        required: true,
        message: transformI18n("imip.items.item6"),
        trigger: "blur"
      }
    ],
    tonnage: [
      {
        required: true,
        message: transformI18n("imip.items.item7"),
        trigger: "blur"
      }
    ]
  };
  timeFields.forEach((field, index) => {
    if (field === "dockingTime") {
      ruleObj[field] = [
        {
          validator: (_rule, value, callback) => {
            const arrivalTime = formData.value["arrivalTime"];
            if (
              value &&
              arrivalTime &&
              new Date(value) <= new Date(arrivalTime)
            ) {
              return callback(new Error(transformI18n("imip.page4.obj32")));
            }
            callback();
          },
          trigger: ["blur", "change"]
        }
      ];
      return;
    }
    const prevField = timeFields[index - 1];
    ruleObj[field] = [
      {
        validator: (_rule, value, callback) => {
          const prevValue = prevField ? formData.value[prevField] : null;
          if (value && prevField && !prevValue) {
            const prevlabel = fieldToI18nLabelMap[prevField];
            return callback(
              new Error(
                `${transformI18n("imip.page4.obj31")}：“${transformI18n(prevlabel)}”`
              )
            );
          }
          if (
            value &&
            prevField &&
            prevValue &&
            new Date(value) <= new Date(prevValue)
          ) {
            return callback(new Error(transformI18n("imip.page4.obj32")));
          }
          callback();
        },
        trigger: ["blur", "change"]
      }
    ];
  });
  return ruleObj;
});
// const rules = computed(() => ({
//   shipRade: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj1")
//     }
//   ],
//   shipName: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj2")
//     }
//   ],
//   shipLength: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj3")
//     }
//   ],
//   usageUnit: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj4")
//     }
//   ],
//   materialName: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj5")
//     }
//   ],
//   hbId: [
//     {
//       required: true,
//       message: transformI18n("imip.page2.obj6")
//     }
//   ]
// }));

const group = computed((): PlusFormGroupRow[] => [
  {
    title: transformI18n("imip.page2.obj7"),
    icon: CreditCard,
    columns: [
      {
        label: transformI18n("imip.page1.obj42"),
        width: 120,
        prop: "shipRade",
        valueType: "select",
        options: [
          { label: "内", value: "内" },
          { label: "外", value: "外" }
        ]
      },
      {
        label: transformI18n("imip.page1.obj1"),
        prop: "shipName"
      },
      {
        label: transformI18n("imip.page1.obj9"),
        prop: "mineNumber"
      },
      {
        label: transformI18n("imip.page1.obj10"),
        prop: "imo"
      },
      {
        label: transformI18n("imip.page1.obj3"),
        prop: "materialName",
        valueType: "select",
        fieldProps: {
          filterable: true // 允许搜索
        },
        options: materialOptions.value
      },
      {
        label: transformI18n("imip.page1.obj12"),
        prop: "usageUnit"
      },
      {
        label: transformI18n("imip.page1.obj4"),
        width: 120,
        prop: "hbId",
        valueType: "select",
        fieldProps: {
          filterable: true // 允许搜索
        },
        options: () => getBerthList()
      },
      {
        label: transformI18n("imip.page1.obj11"),
        prop: "shipLength",
        valueType: "input-number"
      },
      {
        label: transformI18n("imip.page1.obj22"),
        prop: "planTonnage",
        valueType: "input-number"
      },
      {
        label: tonnageLabel.value,
        prop: "tonnage",
        valueType: "input-number"
      },
      // packageNum 字段已移除，不在表单渲染，仅在 formData 里存储
      {
        label: transformI18n("imip.page1.obj13"),
        prop: "unloadWeight",
        valueType: "input-number"
      },
      {
        label: transformI18n("imip.items.item5"),
        width: 120,
        prop: "remark01",
        valueType: "select",
        options: [
          { label: "装船 Memuat kapal", value: "装船 Memuat kapal" },
          { label: "卸船 Membongkar kapal", value: "卸船 Membongkar kapal" }
        ]
      },
      {
        label: transformI18n("imip.page1.obj37"),
        prop: "remark",
        valueType: "textarea"
      }
    ]
  },
  {
    title: transformI18n("imip.page2.obj8"),
    icon: Calendar,
    columns: [
      {
        label: transformI18n("imip.page1.obj18"),
        prop: "arrivalTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj19"),
        prop: "planDockingTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj20"),
        prop: "dockingTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj21"),
        prop: "operationTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj7"),
        prop: "endTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj23"),
        prop: "outBerthTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      },
      {
        label: transformI18n("imip.page1.obj24"),
        prop: "leaveTime",
        valueType: "date-picker",
        fieldProps: {
          type: "datetime",
          valueFormat: "YYYY-MM-DD HH:mm:ss"
        }
      }
    ]
  },
  {
    title: transformI18n("imip.page2.obj9"),
    icon: Soccer,
    columns: [
      {
        label: transformI18n("imip.page1.obj34"),
        prop: "shipAgency"
      },
      {
        label: transformI18n("imip.page2.obj12"),
        prop: "loadingPort"
      },
      {
        label: transformI18n("imip.page1.obj32"),
        prop: "lastPort"
      },
      {
        label: transformI18n("imip.page1.obj33"),
        prop: "nextPort"
      },
      {
        label: transformI18n("imip.page1.obj35"),
        prop: "draft"
      },
      {
        label: transformI18n("imip.page1.obj16"),
        prop: "weight",
        valueType: "input-number"
      },
      {
        label: transformI18n("imip.page1.obj15"),
        prop: "cardCount"
      }
    ]
  },
  {
    title: transformI18n("imip.page2.obj10"),
    icon: Ship,
    columns: [
      {
        label: transformI18n("imip.page1.obj30"),
        prop: "contractRate"
      },
      {
        label: transformI18n("imip.page1.obj31"),
        prop: "contractFee"
      },
      {
        label: transformI18n("imip.page1.obj29"),
        prop: "demurrageFee",
        fieldProps: {
          disabled: true
        }
      },
      {
        label: transformI18n("imip.page2.obj13"),
        prop: "orderNo"
      },
      {
        label: transformI18n("imip.page2.obj14"),
        prop: "contractNo"
      },
      {
        label: transformI18n("imip.page2.obj15"),
        prop: "handledBy"
      },
      {
        label: transformI18n("imip.page2.obj16"),
        prop: "suplierName"
      },
      {
        label: transformI18n("imip.page2.obj17"),
        prop: "batchNumber"
      },
      {
        label: transformI18n("imip.page2.obj18"),
        prop: "inspectionCompany"
      }
    ]
  }
]);
watch(
  () => route.query.id,
  id => {
    if (id && id !== "") {
      // 明确排除空字符串
      getPlanDetail(id);
    } else {
      formData.value = {};
    }
  },
  { immediate: true }
);
const timeFields = [
  "arrivalTime",
  "planDockingTime",
  "dockingTime",
  "operationTime",
  "endTime",
  "outBerthTime",
  "leaveTime"
];
const fieldToI18nLabelMap: Record<string, string> = {
  arrivalTime: "imip.page1.obj18",
  planDockingTime: "imip.page1.obj19",
  dockingTime: "imip.page1.obj20",
  operationTime: "imip.page1.obj21",
  endTime: "imip.page1.obj7",
  outBerthTime: "imip.page1.obj23",
  leaveTime: "imip.page1.obj24"
};

// 物料下拉选项
const materialOptions = ref<any[]>([]);
fetchMaterialName().then(list => {
  materialOptions.value = list;
});

// 监听materialName变化，自动赋值remark01到packageNum
watch(
  () => formData.value.materialName,
  val => {
    const selected = materialOptions.value.find(item => item.value === val);
    formData.value.packageNum = selected ? selected.remark01 : "";
  }
);

// 控制显示
const showTonnage = computed(
  () => formData.value.packageNum === "1" || formData.value.packageNum === "3"
);
const showCount = computed(() => formData.value.packageNum === "2");
const tonnageLabel = computed(() => {
  if (formData.value.packageNum === "2") {
    return transformI18n("imip.page1.obj43"); // "实际件数"的国际化key
  }
  return transformI18n("imip.page1.obj14"); // "实际吨位"的国际化key
});
</script>

<style lang="scss" scoped>
.custom-group-header {
  display: flex;
  align-items: center;
  color: var(--el-color-primary);
  justify-content: space-between;
  .el-icon {
    margin-right: 5px;
  }
}
.fixed-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  padding: 10px 20px;
  display: flex;
  justify-content: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}
</style>
