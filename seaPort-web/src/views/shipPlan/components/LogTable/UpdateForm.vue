<script setup lang="ts">
import { reactive, ref, watch } from "vue";
const formRef = ref();
// 声明 props 类型
export interface FormProps {
  data: any;
}

const { data } = defineProps<FormProps>();

const newFormInline = ref({
  reason: "",
  // 如果class是白班，则默认为当前时间，夜班日期+1天
  endTime: "",
  startTime: ""
});
// 表单校验规则
const rules = reactive({
  endTime: [{ required: true, message: `请输入暂停时间`, trigger: "blur" }],
  startTime: [{ required: true, message: `请输入恢复时间`, trigger: "blur" }],
  reason: [{ required: true, message: "请填写停止原因", trigger: "blur" }]
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
watch(
  () => data,
  newData => {
    if (newData) {
      newFormInline.value.endTime = newData.endTime;
      newFormInline.value.reason = newData.reason;
      newFormInline.value.startTime = newData.startTime;
    }
  },
  { immediate: true } // 组件挂载时立即执行一次
);
defineExpose({
  submitForm
});
</script>

<template>
  <el-form
    ref="formRef"
    :model="newFormInline"
    labelPosition="top"
    :rules="rules"
  >
    <el-form-item prop="startTime" label="暂停时间" required>
      <el-date-picker
        v-model="newFormInline.startTime"
        type="datetime"
        valueFormat="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[100%]"
        clearable
      />
    </el-form-item>
    <el-form-item prop="endTime" label="恢复时间" required>
      <el-date-picker
        v-model="newFormInline.endTime"
        type="datetime"
        valueFormat="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[100%]"
        clearable
      />
    </el-form-item>
    <el-form-item prop="reason" label="暂停原因：">
      <el-input
        v-model="newFormInline.reason"
        placeholder="请输入暂停原因"
        type="textarea"
        class="!w-[100%]"
      /> </el-form-item
  ></el-form>
</template>
