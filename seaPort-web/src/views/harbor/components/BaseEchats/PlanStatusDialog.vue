<template>
  <el-dialog
    v-model="visible"
    :title="`${data?.name} (总数: ${data?.value})`"
    width="500px"
    custom-class="plan-status-dialog"
    style="background-color:"
    append-to-body
  >
    <div class="dialog-content">
      <!-- <p
        :style="{
          color: data.itemStyle?.color || '#fff',
          marginBottom: '10px'
        }"
      >
        各物资分类（{{ data?.name }}）
      </p> -->
      <el-table
        v-if="data.childrenMaterialVo?.length"
        :data="data.childrenMaterialVo"
        style="width: 100%"
        :header-cell-style="{ background: '#1a2a44', color: '#fff' }"
        :cell-style="{ background: '#1a2a44', color: '#fff' }"
      >
        <el-table-column label="物资" prop="materialName" />
        <el-table-column label="数量" prop="count" align="center" />
      </el-table>
      <p v-else class="no-data">暂无子项数据</p>
    </div>
    <template #footer>
      <el-button type="primary" @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      value: 0,
      name: "",
      childrenMaterialVo: [],
      itemStyle: { color: "#fff" }
    })
  }
});

const visible = ref(false);

defineExpose({
  show: () => (visible.value = true)
});
</script>

<style scoped lang="less">
.dialog-content {
  max-height: 500px;
  overflow-y: auto;
  color: #fff;
}

.dialog-content p {
  font-weight: bold;
  margin-bottom: 10px;
}

.no-data {
  text-align: center;
  color: #ccc;
}
</style>
