<script setup lang="ts">
import dayjs from "dayjs";
import { reactive, ref } from "vue";
const formRef = ref();
// 声明 props 类型
export interface FormProps {
  id: string;
  type: string;
  label: string;
}

const { type, label } = defineProps<FormProps>();

const newFormInline = ref({
  pauseReason: "",
  // 如果class是白班，则默认为当前时间，夜班日期+1天

  time: dayjs().second(0).format("YYYY-MM-DD HH:mm:ss")
});
// 表单校验规则
const rules = reactive({
  time: [{ required: true, message: `请输入${label}时间`, trigger: "blur" }],
  pauseReason: [{ required: true, message: "请填写停止原因", trigger: "blur" }]
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
    labelPosition="top"
    :rules="rules"
  >
    <el-form-item prop="time" :label="`${label}时间：`" required>
      <el-date-picker
        v-model="newFormInline.time"
        type="datetime"
        valueFormat="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[350px]"
        clearable
      />
    </el-form-item>
    <el-form-item v-if="type === 'stop'" prop="pauseReason" label="暂停原因：">
      <el-input
        v-model="newFormInline.pauseReason"
        placeholder="请输入暂停原因"
        type="textarea"
        class="!w-[100%]"
      /> </el-form-item
  ></el-form>
</template>
