<template>
  <el-form ref="formRef" :model="formData" :rules="rules">
    <el-form-item
      :label="t('operationForm.screenStatusLabel')"
      prop="screenStatus"
    >
      <el-switch
        v-model="formData.screenStatus"
        :active-text="t('operationForm.screenStatusShow')"
        :inactive-text="t('operationForm.screenStatusHide')"
        inline-prompt
        :active-value="'0'"
        :inactive-value="'1'"
      />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import type { FormInstance } from "element-plus";
import { useI18n } from "vue-i18n";

// Define props and emit (if needed)
const { screenStatus } = defineProps<{
  screenStatus: string;
}>();
const formData = reactive({
  screenStatus: screenStatus || "0" // Default to false (hidden)
});

// Form reference for validation and submission
const formRef = ref<FormInstance | null>(null);

// Form validation rules
const { t } = useI18n();
const rules = {
  screenStatus: [
    {
      required: true,
      message: t("operationForm.screenStatusRule"),
      trigger: "change"
    }
  ]
};

// Expose submitForm method to parent
const submitForm = (callback: (data: typeof formData) => void) => {
  formRef.value?.validate(valid => {
    if (valid) {
      callback(formData);
    } else {
      console.error("Form validation failed");
    }
  });
};

// Expose formRef and submitForm to parent
defineExpose({
  formRef,
  submitForm
});
</script>

<style scoped>
.el-form {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-switch {
  height: 22px;
}
</style>
