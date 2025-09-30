<script setup lang="ts">
import { computed, ref, watch } from "vue";
import "plus-pro-components/es/components/dialog-form/style/css";
import {
  type PlusColumn,
  type FieldValues,
  PlusDialogForm
} from "plus-pro-components";
import { OptionPlanStatus } from "@/views/shipPlan/utils/type";
import {
  addHarborUnload,
  updateHarborUnload,
  getHarborUnloadInfo
} from "@/api/plan/unloadingOrder";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";
import { transformI18n } from "@/plugins/i18n";
const { planId, type, unloadId, submitSancel, cancel, pierType } = defineProps<{
  planId: string;
  pierType: string;
  type: string;
  submitSancel?: Function;
  unloadId?: string;
  cancel?: Function;
}>();
const getTime = (type: any) => {
  if (type === "白班") {
    const time =
      pierType !== "2"
        ? dayjs().hour(7).minute(0).second(0).format("YYYY-MM-DD HH:mm:ss")
        : dayjs().hour(6).minute(30).second(0).format("YYYY-MM-DD HH:mm:ss");
    return time;
  }
  const time =
    pierType !== "2"
      ? dayjs().hour(19).minute(0).second(0).format("YYYY-MM-DD HH:mm:ss")
      : dayjs().hour(18).minute(30).second(0).format("YYYY-MM-DD HH:mm:ss");
  return time;
};

const initValues = {
  classTime: dayjs().format("YYYY-MM-DD"),
  classes: "白班",
  startTime: "",
  remark: "",
  leader: ""
};
const values = ref<FieldValues>(initValues);

const filteredColumns = computed(() => {
  return columns.filter(col => {
    if (col.prop === "reason") {
      return values.value.workType === "1"; // 仅在 workType 为 1 时显示 reason
    }
    if (col.prop === "stopTime") {
      return values.value.workType !== "0"; // 仅在 workType 为 1 时显示 reason
    }
    return true;
  });
});

const rules = {
  classTime: [
    {
      required: true,
      message: transformI18n("imip.page3.obj14")
    }
  ],
  classes: [
    {
      required: true,
      message: transformI18n("imip.page3.obj15")
    }
  ],
  startTime: [
    {
      required: true,
      message: transformI18n("imip.page3.obj16")
    }
  ],
  leader: [
    {
      required: true,
      message: transformI18n("imip.page3.obj17")
    }
  ]
};

const columns: PlusColumn[] = [
  {
    label: transformI18n("imip.page3.obj18"),
    prop: "classTime",
    valueType: "date-picker",
    fieldProps: {
      type: "date",
      valueFormat: "YYYY-MM-DD"
    }
  },

  {
    label: transformI18n("imip.page2.obj37"),
    prop: "classes",
    valueType: "radio",
    options: [
      {
        label: transformI18n("imip.page2.obj38"),
        value: "白班"
      },
      {
        label: transformI18n("imip.page2.obj39"),
        value: "夜班"
      }
    ]
  },
  {
    label: transformI18n("imip.page3.obj7"),
    prop: "startTime",
    valueType: "date-picker",
    fieldProps: { type: "datetime", valueFormat: "YYYY-MM-DD HH:mm:ss" }
  },
  {
    label: transformI18n("imip.page3.obj3"),
    prop: "leader"
  }
];
watch(
  () => pierType,
  newPierType => {
    if (newPierType) {
      values.value.startTime = getTime(values.value.classes);
    }
  }
);
watch(
  () => values.value.classes,
  newClass => {
    if (!unloadId) {
      values.value.startTime = getTime(newClass);
    }
  },
  { immediate: true }
);
const handleSubmit = async (values: FieldValues) => {
  values.planId = planId;
  if (type === OptionPlanStatus.EDIT) {
    values.duId = unloadId;
  }
  const res: any =
    type === OptionPlanStatus.ADD
      ? await addHarborUnload(values)
      : await updateHarborUnload(values);
  if (res.code === 200) {
    ElMessage({
      message: "success",
      type: "success"
    });
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
  submitSancel?.();
};
const handleReset = () => {
  values.value = {
    ...initValues
  };
};
const handleCancel = () => {
  handleReset();
  cancel?.();
};
const handleFormInfo = async () => {
  const res: any = await getHarborUnloadInfo(unloadId);
  if (res.code === 200) {
    values.value = res.data;
  } else {
    ElMessage({
      message: res.msg,
      type: "error"
    });
  }
};
watch(
  () => unloadId,
  newId => {
    if (newId) {
      handleFormInfo();
    }
  },
  { immediate: true } // 组件挂载时立即执行一次
);
</script>

<template>
  <PlusDialogForm
    v-model="values"
    width="800px"
    align-center
    :form="{
      columns: filteredColumns,
      rules,
      labelWidth: '80px',
      labelPosition: 'top',
      disabled: type === OptionPlanStatus.CHECK
    }"
    :title="`${transformI18n(type)+transformI18n('imip.page2.obj24')}`"
    @confirm="handleSubmit"
    @reset="handleReset"
    @cancel="handleCancel"
  >
    <template #dialog-footer="{ handleConfirm, handleCancel }">
      <el-button @click="handleCancel">{{ transformI18n("imip.button.obj33") }}</el-button>
      <el-button type="warning" @click="handleReset">{{ transformI18n("imip.button.obj38") }}</el-button>
      <el-button type="primary" @click="handleConfirm">{{ transformI18n("imip.button.obj34") }}</el-button>
    </template>
  </PlusDialogForm>
</template>
