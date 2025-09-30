<script setup lang="ts">
import dayjs from "dayjs";
import { reactive, ref } from "vue";

const formRef = ref();
// 声明 props 类型
export interface FormProps {
  formInline: {
    name: string;
    address: string;
    userId: string;
    id: string;
  };
  userList: any;
}

const { formInline, userList } = defineProps<FormProps>();

const newFormInline = ref({
  ...formInline
});

// 表单校验规则
const rules = reactive({
  name: [
    {
      required: true,
      message: "请输入名称",
      trigger: "blur"
    }
  ],
  address: [
    {
      required: true,
      message: "请输入IP地址",
      trigger: "blur"
    }
  ],
  userId: [
    {
      required: true,
      message: "请选择用户",
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
    labelPosition="top"
    :rules="rules"
  >
    <el-form-item prop="userId" label="用户">
      <el-select
        v-model="newFormInline.userId"
        class="!w-[100%]"
        placeholder="请选择用户"
        clearable
        filterable
      >
        <el-option
          v-for="item in userList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item prop="name" label="名称">
      <el-input
        v-model="newFormInline.name"
        placeholder="请输入名称"
        class="!w-[100%]"
      />
    </el-form-item>
    <el-form-item prop="address" label="IP地址">
      <el-input
        v-model="newFormInline.address"
        placeholder="请输入名称"
        class="!w-[100%]"
      /> </el-form-item
  ></el-form>
</template>
