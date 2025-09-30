<template>
  <el-button
    type="warning"
    :icon="useRenderIcon(Download)"
    @click="centerDialogVisible = true"
  >{{transformI18n("imip.button.obj7")}}
  </el-button>
  <el-dialog
    v-model="centerDialogVisible"
    :title='transformI18n("imip.button.obj7")'
    width="500"
    align-center
    @close="onClose"
  >
    <el-radio-group v-model="type">
      <el-radio-button label="0">{{ transformI18n("imip.button.obj25") }}</el-radio-button>
      <el-radio-button label="1">{{ transformI18n("imip.button.obj26") }}</el-radio-button>
      <el-radio-button label="2">{{ transformI18n("imip.button.obj27") }}</el-radio-button>
    </el-radio-group>

    <el-upload
      ref="uploadRef"
      :limit="1"
      style="margin-top: 20px"
      accept=".xlsx, .xls"
      :disabled="upload.isUploading"
      :on-progress="handleFileUploadProgress"
      :on-change="handleFileChange"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :auto-upload="false"
      drag
    >
      <el-icon class="el-icon--upload">
        <upload-filled />
      </el-icon>
      <div class="el-upload__text">{{ transformI18n("imip.button.obj28") }}<em>{{ transformI18n("imip.button.obj29") }}</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <!-- <div>
            <el-checkbox
              v-model="updateSupport"
              :label='transformI18n("imip.button.obj32")'
              size="large"
            />
          </div> -->
          <span>{{ transformI18n("imip.button.obj30") }}</span>
          <el-link
            v-if="type === '0'"
            type="primary"
            :underline="false"
            style="font-size: 12px; vertical-align: baseline"
            @click="downloadPlanTemplate()"
            >{{transformI18n("imip.button.obj31")}}</el-link
          >
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false">{{transformI18n("imip.button.obj33")}}</el-button>
        <el-button type="primary" @click="handleSubmit">{{transformI18n("imip.button.obj34")}}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Download from "@iconify-icons/ep/download";
import { UploadFilled } from "@element-plus/icons-vue";
import { ElMessage, genFileId } from "element-plus";
import { ref, reactive, inject } from "vue";
import { useShipPlan } from "../../utils/hooks";
import { transformI18n } from "@/plugins/i18n";
import {
  importHarborPlan,
  importHarborRatePlan,
  importUnloadWeight
} from "@/api/plan/planOrder";
import type { UploadInstance, UploadProps, UploadRawFile } from "element-plus";
const getPlanData: () => void = inject("getPlanData");
const type = ref("0");
const updateSupport = ref(false);

const { downloadPlanTemplate } = useShipPlan();
const centerDialogVisible = ref(false);
const fileList = ref<File | null>(null);

const upload = reactive({
  isUploading: false
});
const uploadRef: any = ref<UploadInstance>();

const handleFileChange = file => {
  fileList.value = file.raw || null;
};

const handleFileUploadProgress = () => {
  upload.isUploading = true;
};
const onClose = () => {
  centerDialogVisible.value = false;
  type.value = "0";
  uploadRef.value!.clearFiles();
};
const handleUploadError = (error: any) => {
  upload.isUploading = false;
  ElMessage.error(`${transformI18n("imip.button.obj35")}: ${error}`);
};

const handleExceed: UploadProps["onExceed"] = files => {
  uploadRef.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  uploadRef.value!.handleStart(file);
};
const handleSubmit = async () => {
  // uploadRef.value.submit();
  if (!fileList.value) {
    ElMessage.warning(transformI18n("imip.button.obj36"));
    return;
  }

  // 构造 FormData
  const formData = new FormData();
  formData.append("file", fileList.value);
  if (type.value === "0") {
    formData.append("updateSupport", updateSupport.value as any);
  }
  try {
    upload.isUploading = true;
    const response: any =
      type.value === "0"
        ? await importHarborPlan(formData)
        : type.value === "1"
          ? await importHarborRatePlan(formData)
          : await importUnloadWeight(formData);
    upload.isUploading = false;
    uploadRef.value.clearFiles();

    ElMessage({
      dangerouslyUseHTMLString: true,
      type: response.code === 200 ? "success" : "error",
      message: `<div></div><div style='overflow: auto; max-height: 70vh;'>${response.msg}</div>`
    });
    // getPlanData();
    centerDialogVisible.value = false;
  } catch (error) {
    upload.isUploading = false;
    ElMessage.error(
      `${transformI18n("imip.button.obj35")}`+"："+ (error.response?.data?.message || error.message)
    );
  }
};
</script>
