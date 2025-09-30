<script setup lang="ts">
import { reactive, ref, watch, computed } from "vue";
import { useI18n } from "vue-i18n";

const formRef = ref();

// 声明 props 类型
export interface FormProps {
  berthListOptions?: any;
  moveBerthTime?: string;
  type?: string;
  ids?: string | string[];
}

const { type, ids, berthListOptions, moveBerthTime } = defineProps<FormProps>();
const now = new Date();
const { t } = useI18n();

const newFormInline = ref({
  moveBerthTime: moveBerthTime ?? "",
  hbId: "",
  hbName: "",
  dockingTime: "",
  operationTime: "",
  monthQuery: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`
});
// 表单校验规则
const rules = reactive({
  moveBerthTime: [
    {
      required: true,
      message: t("operationForm.moveBerthTimeRule"),
      trigger: "blur"
    }
  ],
  dockingTime: [
    {
      required: true,
      message: t("operationForm.dockingTimeRule"),
      trigger: "blur"
    }
  ],
  monthQuery: [
    {
      required: true,
      message: t("operationForm.archiveMonthRule"),
      trigger: "blur"
    }
  ]
});

const submitForm = callback => {
  formRef.value.validate(valid => {
    if (valid) {
      if (newFormInline.value.hbId) {
        const hbName = berthListOptions.find(
          item => item.value === newFormInline.value?.hbId
        )?.label;
        newFormInline.value.hbName = hbName;
      }
      return new Promise((resolve, reject) => {
        callback(newFormInline.value);
        resolve(newFormInline.value); // 将数据传递出去
      });
    }
  });
};
defineExpose({
  submitForm
});

const planCountHtml = computed(() => {
  // t('operationForm.planCount') 里是 '共{count}条计划单'
  return t("operationForm.planCount", {
    count: `<strong>${ids?.length}</strong>`
  });
});
</script>

<template>
  <el-form
    ref="formRef"
    :model="newFormInline"
    labelPosition="top"
    :rules="rules"
  >
    <span v-if="type === 'archive'">
      <div class="text-red-500 font-bold mb-2">
        <span v-html="planCountHtml"></span>
      </div>
      <el-form-item
        prop="monthQuery"
        :label="t('operationForm.archiveMonth')"
        required
      >
        <el-date-picker
          v-model="newFormInline.monthQuery"
          class="!w-[350px]"
          :placeholder="t('operationForm.archiveMonthPlaceholder')"
          clearable
          valueFormat="YYYY-MM"
          type="month"
        />
      </el-form-item>
    </span>
    <span v-else>
      <el-form-item
        prop="moveBerthTime"
        :label="t('operationForm.moveBerthTime')"
        required
      >
        <el-date-picker
          v-model="newFormInline.moveBerthTime"
          type="datetime"
          valueFormat="YYYY-MM-DD HH:mm:ss"
          :placeholder="t('operationForm.moveBerthTimePlaceholder')"
          class="!w-[350px]"
          clearable
        />
      </el-form-item>
      <el-form-item prop="hbId" :label="t('operationForm.berth')">
        <el-select
          v-model="newFormInline.hbId"
          class="!w-[350px]"
          :placeholder="t('operationForm.berthPlaceholder')"
          clearable
        >
          <el-option
            v-for="item in berthListOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <span v-if="newFormInline.hbId">
        <el-form-item
          prop="dockingTime"
          :label="t('operationForm.dockingTime')"
          required
        >
          <el-date-picker
            v-model="newFormInline.dockingTime"
            type="datetime"
            valueFormat="YYYY-MM-DD HH:mm:ss"
            :placeholder="t('operationForm.dockingTimePlaceholder')"
            class="!w-[350px]"
            clearable
          />
        </el-form-item>
        <el-form-item
          prop="operationTime"
          :label="t('operationForm.operationTime')"
        >
          <el-date-picker
            v-model="newFormInline.operationTime"
            type="datetime"
            valueFormat="YYYY-MM-DD HH:mm:ss"
            :placeholder="t('operationForm.operationTimePlaceholder')"
            class="!w-[350px]"
            clearable
          />
        </el-form-item>
      </span>
    </span>
  </el-form>
</template>
