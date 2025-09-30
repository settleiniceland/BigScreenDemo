<script setup lang="ts">
import { reactive, ref } from "vue";

const formRef = ref();
// 声明 props 类型
export interface FormProps {
  formInline: {
    pierId: string;
    startPercent: number;
    endPercent: number;
    unloadRate: number;
  };
  pierList: any;
}

const { formInline, pierList } = defineProps<FormProps>();

const newFormInline = ref({
  ...formInline
});

// 表单校验规则
const rules = reactive({
  pierId: [
    {
      required: true,
      message: "请选择码头名称",
      trigger: "blur"
    }
  ],
  startPercent: [
    {
      required: true,
      message: "请输入起始百分比",
      trigger: "blur"
    },
    {
      validator: (rule, value, callback) => {
        const endPercent = Number(newFormInline.value.endPercent);
        const startPercent = Number(value);
        if (!value && value !== 0) {
          callback(new Error("请输入起始百分比"));
        } else if (startPercent < 0 || startPercent > 100) {
          callback(new Error("起始百分比必须在 0 到 100 之间"));
        } else if (endPercent && startPercent > endPercent) {
          callback(new Error("起始百分比不能大于结束百分比"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  endPercent: [
    {
      required: true,
      message: "请输入结束百分比",
      trigger: "blur"
    },
    {
      validator: (rule, value, callback) => {
        const startPercent = Number(newFormInline.value.startPercent);
        const endPercent = Number(value);
        if (!value && value !== 0) {
          callback(new Error("请输入结束百分比"));
        } else if (endPercent < 0 || endPercent > 100) {
          callback(new Error("结束百分比必须在 0 到 100 之间"));
        } else if (startPercent && startPercent > endPercent) {
          callback(new Error("结束百分比不能小于起始百分比"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  unloadRate: [
    {
      required: true,
      message: "请输入卸率（吨/小时）",
      trigger: "blur"
    }
  ]
});

const submitForm = callback => {
  formRef.value.validate(valid => {
    if (valid) {
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
</script>

<template>
  <el-form
    ref="formRef"
    :model="newFormInline"
    class="pier-hour-rate-form"
    labelPosition="top"
    :rules="rules"
  >
    <el-form-item prop="pierId" label="码头名称">
      <el-select
        v-model="newFormInline.pierId"
        placeholder="请选择码头名称"
        class="!w-[100%]"
        filterable
        clearable
      >
        <el-option
          v-for="item in pierList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="区间范围(%)" required>
      <el-row :gutter="10">
        <el-col :span="24">
          <el-form-item prop="startPercent">
            <el-input
              v-model="newFormInline.startPercent"
              placeholder="起始百分比"
              :min="0"
              :max="100"
              type="number"
              class="!w-[100%]"
            />
          </el-form-item>
        </el-col>
        <el-col :span="1">-</el-col>
        <el-col :span="24">
          <el-form-item prop="endPercent">
            <el-input
              v-model="newFormInline.endPercent"
              placeholder="结束百分比"
              :min="0"
              :max="100"
              type="number"
              class="!w-[100%]"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form-item>
    <el-form-item prop="unloadRate" label="卸率(吨/H)|(件/H)">
      <el-input
        v-model="newFormInline.unloadRate"
        type="number"
        placeholder="请输入卸率(吨/H)|(件/H)"
        class="!w-[100%]"
      />
    </el-form-item>
  </el-form>
</template>

<style scoped>
/* 确保输入框样式一致 */
.pier-hour-rate-form {
  ::v-deep(.el-row) {
    flex-wrap: nowrap;
  }
}
</style>
