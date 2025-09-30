<template>
  <el-form ref="formRef" :inline="true" :model="formData" :rules="rules">
    <span v-if="isDock">
      <el-form-item :label='transformI18n("imip.page3.obj38")' prop="pierName" required>
        <el-input
          v-model="formData.pierName"
          class="!w-[210px]"
          :placeholder='transformI18n("imip.page3.obj38")'
        />
      </el-form-item>
      <el-form-item :label='transformI18n("imip.page3.obj39")' prop="pierCode" required>
        <el-input
          v-model="formData.pierCode"
          class="!w-[210px]"
          :placeholder='transformI18n("imip.page3.obj39")'
        />
      </el-form-item>
      <el-form-item :label='transformI18n("imip.page3.obj31")' prop="pierType">
        <el-select
          v-model="formData.pierType"
          :placeholder='transformI18n("imip.page3.obj39")'
          class="!w-[210px]"
        >
          <el-option
            v-for="item in PireTypeOptions"
            :key="item.value"
            :label="transformI18n(item.label)"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </span>
    <span v-else>
      <el-form-item :label='transformI18n("imip.page4.obj1")' prop="berthName" required>
        <el-input
          v-model="formData.berthName"
          class="!w-[210px]"
          :placeholder='transformI18n("imip.page2.obj6")'
        />
      </el-form-item>
      <el-form-item :label='transformI18n("imip.page4.obj3")' prop="berthCode" required>
        <el-input
          v-model="formData.berthCode"
          class="!w-[210px]"
          :placeholder='transformI18n("imip.page4.obj4")'
        />
      </el-form-item>

      <el-form-item :label='transformI18n("imip.page3.obj30")' prop="berthStatus">
        <el-select
          v-model="formData.berthStatus"
          :placeholder='transformI18n("imip.page4.obj5")'
          class="!w-[210px]"
        >
          <el-option
            v-for="item in BerthStatusOptions"
            :key="item.value"
            :disabled="item.value === '3'"
            :label="transformI18n(item.label)"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </span>
    <el-form-item
      :label='transformI18n("imip.page1.obj37")'
      prop="remark"
      :required="formData.berthStatus === '2'"
    >
      <el-input
        v-model="formData.remark"
        type="textarea"
        :placeholder='transformI18n("imip.page4.obj6")'
        class="!w-[400px]"
        :rows="1"
      />
    </el-form-item>
    <MapDrawer
      v-if="isMapReady"
      :geoJson="props.formInline.mapFeatures"
      :mapFeatures="props.formInline.mapFeatures"
      :dockData="dockDataWithGeoJson"
      :type="props.formInline.type"
      :action="props.formInline.action"
      :storedPierId="storedPierId"
      @update:geojson="handleGeoJsonUpdate"
    />
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, toRaw } from "vue";
import { ElMessage } from "element-plus";
import MapDrawer from "./components/MapDrawer.vue";
import { BerthStatusOptions, PireTypeOptions } from "@/contans";
import { transformI18n } from "@/plugins/i18n";
interface FormProps {
  formInline: any;
}
const props = withDefaults(defineProps<FormProps>(), {
  formInline: {
    geoJson: null,
    type: "dock",
    action: "新增"
  }
});
const isAdd = ref(props.formInline.action === "新增");

// 表单校验规则
const rules = reactive({
  pierName: [{ required: true, message: transformI18n("imip.page4.obj7"), trigger: "blur" }],
  berthName: [{ required: true, message: transformI18n("imip.page2.obj6"), trigger: "blur" }],
  pireCode: [
    { required: true, message: transformI18n("imip.page4.obj8"), trigger: "blur" }
  ],
  berthCode: [
    { required: true, message: transformI18n("imip.page4.obj4"), trigger: "blur" }
  ],
  pierType: [{ required: true, message: transformI18n("imip.page4.obj9"), trigger: "blur" }],
  berthStatus: [{ required: true, message: transformI18n("imip.page4.obj10"), trigger: "blur" }],
  remark: [
    { required: true, message: transformI18n("imip.page4.obj11"), trigger: "blur" }
  ]
});
const formData: any = reactive({
  ...props.formInline,
  remark: props.formInline.action === "新增" ? "" : props.formInline.remark,
  geoJson2: props.formInline.geoJson
});

const dockDataWithGeoJson = computed(() => {
  return {
    ...formData,
    geoJson:
      props.formInline.type === "berth" ? props.formInline.mapFeatures : "",
    geoJson2: null // 提交接口Json
  };
});

const storedPierId = ref(formData.pierId || formData.berthHpId);

const isDock = computed(() => props.formInline.type === "dock");
const isMapReady = ref(true);

const handleGeoJsonUpdate = (geoJson: GeoJSON.Feature | null) => {
  if (geoJson) {
    formData.geoJson2 = JSON.parse(JSON.stringify(toRaw(geoJson)));
  } else {
    formData.geoJson2 = null;
  }
};

const resetMap = () => {
  isMapReady.value = false;
  nextTick(() => {
    isMapReady.value = true;
  });
};

const formRef = ref(null);

const submitForm = (callback, actionType) => {
  formRef.value.validate(valid => {
    if (valid) {
      return new Promise((resolve, reject) => {
        if (!formData.geoJson2) {
          ElMessage.error("请绘制区域。");
          return reject("区域未绘制");
        }
        let sanitizedData: any = {
          pierId: formData.pierId,
          type: actionType,
          remark: formData.remark
        };

        if (actionType === "dock") {
          sanitizedData.pierName = formData.pierName;
          sanitizedData.pierType = formData.pierType;
          sanitizedData.pierGeoJson = JSON.stringify(formData.geoJson2);
          sanitizedData.pierCode = formData.pierCode;
        } else {
          sanitizedData.berthName = formData.berthName;
          sanitizedData.berthId = formData.berthId;
          sanitizedData.berthStatus = formData.berthStatus;
          sanitizedData.berthGeoJson = JSON.stringify(formData.geoJson2);
          sanitizedData.berthCode = formData.berthCode;
        }
        console.log(sanitizedData);

        callback(sanitizedData);
        resolve(sanitizedData); // 将数据传递出去
      });
    }
  });
};

resetMap();

defineExpose({
  submitForm
});
</script>
