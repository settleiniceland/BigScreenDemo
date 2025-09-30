<script setup lang="ts">
import { reactive, ref } from "vue";
import { BerthOperationType } from "../../utils/hooks";

const formRef = ref();

const { type, formInline, berthList } = defineProps({
  type: {
    type: String,
    required: true,
    default: ""
  },
  formInline: {
    type: Object,
    required: true,
    default: () => {}
  },
  berthList: {
    type: Array,
    required: true,
    default: () => []
  }
});

const formData: any = reactive({
  ...formInline,
  dockingTime: ""
});
const submitForm = callback => {
  const hbName = berthList.find(
    (item: any) => item.value == formData.hbId
  )?.label;
  formRef.value.validate(valid => {
    if (valid) {
      return new Promise((resolve, reject) => {
        formData.hbName = hbName;
        callback(formData);
        resolve(formData); // 将数据传递出去
      });
    }
  });
};
const rules = reactive({
  dockingTime: [
    {
      required: true,
      message: "请选择靠泊时间",
      trigger: ["blur", "change"]
    }
  ],
  planDockingTime: [
    {
      required: true,
      message: "请选择计划靠泊时间",
      trigger: ["blur", "change"]
    }
  ],
  hbId: [
    {
      required: true,
      message: "请选择泊位",
      trigger: ["blur", "change"]
    }
  ]
});

defineExpose({
  submitForm
});
</script>

<template>
  <el-form ref="formRef" :model="formData" label-position="top" :rules="rules">
    <el-form-item
      v-if="type === BerthOperationType.CHANGE_BERTH"
      label="选择泊位："
      prop="hbId"
    >
      <el-select
        v-model="formData.hbId"
        class="!w-[350px]"
        placeholder="选择泊位"
        clearable
      >
        <el-option
          v-for="item in berthList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item
      v-else-if="type === BerthOperationType.PREDICT_BERTHING"
      label="预计靠泊时间:"
      prop="planDockingTime"
    >
      <el-date-picker
        v-model="formData.planDockingTime"
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[350px]"
        clearable
      />
    </el-form-item>
    <el-form-item v-else label="靠泊时间:" prop="dockingTime">
      <el-date-picker
        v-model="formData.dockingTime"
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[350px]"
        clearable
      />
    </el-form-item>
  </el-form>
</template>
