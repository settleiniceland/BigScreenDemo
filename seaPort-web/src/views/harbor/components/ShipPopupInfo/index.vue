<template>
  <div class="ship-popup-container">
    <div class="ship-popup-header">
      <h3>{{ props.shipName }}</h3>
    </div>
    <div class="ship-popup-content">
      <!-- 特殊字段：船只状态 -->
      <div class="info-row">
        <div class="info-label"><strong>计划状态:</strong></div>
        <ElTag
          :style="{
            color: props.color,
            border: `1px solid ${props.color}`
          }"
          effect="plain"
        >
          {{ props.shipStatus }}
        </ElTag>
      </div>
      <!-- 动态字段：通过 fieldMap 渲染 -->
      <div v-for="[key, field] in fieldMap" :key="key" class="info-row">
        <div class="info-label">
          <strong>{{ field.label }}:</strong>
        </div>
        <div v-if="key === 'shipType'" class="info-value">
          <ElTag>
            {{
              ShipTypeOptions.find(item => item.value === props.shipType)?.label
            }}
          </ElTag>
        </div>
        <div v-else class="info-value">
          {{ props[key] ?? field.defaultValue }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElTag } from "element-plus";

// 定义字段配置类型
interface FieldConfig {
  label: string;
  defaultValue: string | number;
}
//0 母船 1 自航船 2驳船
const ShipTypeOptions = [
  { label: "母船", value: "0" },
  { label: "自航船", value: "1" },
  { label: "驳船", value: "2" }
];
// 定义 props 类型
const props = defineProps<{
  shipName: string;
  tonnage: string | number;
  materialName: string;
  lat: string | number;
  lon: string | number;
  shipStatus: string;
  color: string;
  shipType: string;
}>();

// 使用 Map 定义字段映射
const fieldMap = new Map<keyof typeof props, FieldConfig>([
  ["shipType", { label: "船只类型", defaultValue: "N/A" }],
  ["tonnage", { label: "吨位", defaultValue: "N/A" }],
  ["materialName", { label: "货物类型", defaultValue: "N/A" }],
  ["lat", { label: "纬度", defaultValue: 0 }],
  ["lon", { label: "经度", defaultValue: 0 }]
]);
</script>

<style scoped lang="scss">
// 定义颜色变量
$header-border-color: #ffffff;
$text-color: #fff;
$label-color: #ffffff;

.ship-popup-container {
  min-width: 220px;
  padding: 12px;
  font-family:
    system-ui,
    -apple-system,
    BlinkMacSystemFont,
    "Segoe UI",
    Roboto,
    sans-serif;
  color: $text-color;
  border-radius: 8px;
}

.ship-popup-header {
  border-bottom: 1px solid $header-border-color;
  margin-bottom: 12px;
  padding-bottom: 8px;

  h3 {
    margin: 0;
    font-size: 18px;
    color: $label-color;
  }
}

.ship-popup-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-weight: 600;
  font-size: 16px;
  color: $label-color;
}

.info-value {
  font-weight: 600;
  font-size: 16px;
  color: $text-color;
}
</style>
