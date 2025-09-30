<script setup lang="ts">
import { ElMessage } from "element-plus";
import { computed, reactive, ref } from "vue";
import { InputNumber, Select } from "ant-design-vue";
import dayjs from "dayjs";

const formRef = ref();
// 声明 props 类型
export interface FormProps {
  id: string;
  label: string;
  classes: string;
  workType: string;
}
const options1 = [
  {
    label: "件",
    value: "1"
  },
  {
    label: "吨",
    value: "2"
  }
];

const { label, classes, workType } = defineProps<FormProps>();

const newFormInline = ref({
  pauseReason: "",
  unloadNum: "",
  remark: "",
  unit: "2",
  isOver: "",
  totalUnloadWeight: "",
  workEquipment: "",
  // pauseInterval: "00:00:00", // 默认值,
  endTime:
    classes === "白班"
      ? dayjs().hour(19).minute(0).second(0).format("YYYY-MM-DD HH:mm:ss")
      : dayjs()
          .add(1, "day")
          .hour(7)
          .hour(7)
          .minute(0)
          .second(0)
          .format("YYYY-MM-DD HH:mm:ss")
});

// 表单校验规则
const rules = reactive({
  unloadNum: [
    { required: true, message: "请输入当前班次卸货件数", trigger: "blur" }
  ],
  totalUnloadWeight: [
    { required: true, message: "请输入当前班次卸货吨数", trigger: "blur" }
  ],
  isOver: [
    { required: true, message: `请选择是否为最后一班,为识逗`, trigger: "blur" }
  ],
  endTime: [{ required: true, message: `请输入结束时间`, trigger: "blur" }],
  pauseReason: [
    {
      required: true,
      message: "请填写该卸货单暂停作业没有恢复就直接结束的原因",
      trigger: "blur"
    }
  ]
});
const submitForm = callback => {
  formRef.value.validate(valid => {
    if (valid) {
      return new Promise((resolve, reject) => {
        if (!newFormInline.value.unit) {
          ElMessage.error("请选择单位");
          return reject("单位未选择");
        }
        if (newFormInline.value.unit === "1") {
          newFormInline.value.totalUnloadWeight = "";
        } else {
          newFormInline.value.unloadNum = "";
        }
        callback(newFormInline.value);
        resolve(newFormInline.value); // 将数据传递出去
      });
    }
  });
};
const columnLabel = computed(() => {
  return {
    label: newFormInline.value.unit === "1" ? "当班卸货件数" : "当班卸货吨数",
    value: newFormInline.value.unit === "1" ? "unloadNum" : "totalUnloadWeight"
  };
});
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
    <el-form-item prop="endTime" :label="`${label}时间：`" required>
      <el-date-picker
        v-model="newFormInline.endTime"
        type="datetime"
        valueFormat="YYYY-MM-DD HH:mm:ss"
        placeholder="选择时间"
        class="!w-[100%]"
        clearable
      />
    </el-form-item>

    <el-form-item :prop="columnLabel.value" :label="columnLabel.label" required>
      <InputNumber
        v-model:value="newFormInline[columnLabel.value]"
        class="!w-[100%]"
        :placeholder="`请输入${columnLabel.label}`"
        :min="0"
      >
        <template #addonAfter>
          <Select
            v-model:value="newFormInline.unit"
            style="width: 90px"
            :options="options1"
            :getPopupContainer="triggerNode => triggerNode.parentNode"
          /> </template
      ></InputNumber>
    </el-form-item>
    <el-form-item prop="workEquipment" label="作业机具">
      <el-input
        v-model="newFormInline.workEquipment"
        class="!w-[100%]"
        placeholder="请输入作业机具"
      />
    </el-form-item>
    <!-- 是否为最后一班 -->
    <el-form-item prop="isOver" label="是否为最后一班：">
      <el-radio-group v-model="newFormInline.isOver">
        <el-radio label="0">否</el-radio>
        <el-radio label="1">是</el-radio>
      </el-radio-group>
    </el-form-item>
    <!-- <el-form-item prop="pauseInterval" label="累计暂停间隔：">
      <el-time-picker
        v-model="newFormInline.pauseInterval"
        class="!w-[100%]"
        placeholder="请选择累计暂停时间"
        format="HH:mm:ss"
        value-format="HH:mm:ss"
        clearable
      />
    </el-form-item>
    <el-form-item prop="pauseReason" label="暂停原因：">
      <el-input
        v-model="newFormInline.pauseReason"
        placeholder="请输入暂停原因"
        type="textarea"
        class="!w-[100%]"
      />
    </el-form-item> -->
    <el-form-item
      v-if="workType === '1' && newFormInline.isOver === '0'"
      prop="pauseReason"
      label="班次暂停原因："
    >
      <el-input
        v-model="newFormInline.pauseReason"
        placeholder="请输入该卸货单暂停作业没有恢复作业就直接结束的原因,可以和暂停原因保持一致"
        type="textarea"
        class="!w-[100%]"
      />
    </el-form-item>
    <el-form-item label="备注:">
      <el-input
        v-model="newFormInline.remark"
        class="!w-[100%]"
        type="textarea"
        placeholder="可补充其他"
      /> </el-form-item
  ></el-form>
</template>
