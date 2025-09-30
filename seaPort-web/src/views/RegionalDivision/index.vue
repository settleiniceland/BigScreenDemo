<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
      v-bind="$attrs"
    >
      <el-form-item :label='transformI18n("imip.page3.obj29")' prop="name">
        <el-input
          v-model="form.name"
          :placeholder='transformI18n("imip.page3.obj29")'
          class="!w-[200px]"
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("imip.page3.obj30")' prop="berthStatus">
        <el-select
          v-model="form.berthStatus"
          :placeholder='transformI18n("imip.page3.obj30")'
          clearable
          class="!w-[200px]"
        >
          <el-option
            v-for="item in BerthStatusOptions"
            :key="item.value"
            :label="transformI18n(item.label)"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label='transformI18n("imip.page3.obj31")' prop="pierType">
        <el-select
          v-model="form.pierType"
          :placeholder='transformI18n("imip.page3.obj31")'
          clearable
          class="!w-[200px]"
        >
          <el-option
            v-for="item in PireTypeOptions"
            :key="item.value"
            :label="transformI18n(item.label)"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="onSearch"
        >{{ transformI18n("imip.page3.obj32") }}
        </el-button>
        <el-button :icon="useRenderIcon(Refresh)" @click="resetForm(formRef)">
          {{ transformI18n("imip.button.obj38") }}
        </el-button>
      </el-form-item>
    </el-form>
    <PureTableBar
      :title='transformI18n("imip.page3.obj33")'
      :columns="columns"
      :tableRef="tableRef?.getTableRef()"
      @refresh="onSearch"
    >
      <template #buttons>
        <el-button
          type="primary"
          :icon="useRenderIcon(AddFill)"
          @click="openDialog()"
        >{{ transformI18n("imip.page3.obj34") }}
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          ref="tableRef"
          adaptive
          row-key="id"
          :default-expand-all="false"
          :expand-row-keys="expandedKeys"
          :loading="loading"
          :size="size"
          :data="treeData"
          :columns="dynamicColumns"
          border
          @expand-change="
            (row, expanded) =>
              expanded ? handleNodeExpand(row) : handleNodeCollapse(row)
          "
          :header-cell-style="{
            background: 'var(--el-fill-color-light)',
            color: 'var(--el-text-color-primary)'
          }"
        >
          <template #operation="{ row }" width="200px">
            <el-button
              class="reset-margin"
              type="primary"
              :size="size"
              :icon="useRenderIcon(EditPen)"
              @click="openDialog('imip.button.obj1', row, row.type)"
            >{{ transformI18n("imip.button.obj1") }}
            </el-button>
            <el-button
              v-if="row.type === 'dock'"
              class="reset-margin"
              color="#626aef"
              :size="size"
              :icon="useRenderIcon(AddFill)"
              @click="openDialog('imip.button.obj3', row, 'berth')"
            >{{ transformI18n("imip.page3.obj35") }}
            </el-button>
            <el-popconfirm
              :title='transformI18n("imip.page3.obj36")'
              @confirm="handleDelete(row)">
              <template #reference>
                <el-button
                  class="reset-margin"
                  type="danger"
                  :size="size"
                  :icon="useRenderIcon(Delete)"
                >{{ transformI18n("imip.button.obj2") }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
          <template #berthStatus="{ row }">
            <el-tag
              v-if="row.berthStatus"
              :type="BerthStatusTagOptions[row.berthStatus]?.type"
              >{{ BerthStatusTagOptions[row.berthStatus]?.label }}</el-tag
            >
          </template>
          <template #pierType="{ row }">
            <el-tag
              v-if="row.pierType"
              effect="plain"
              round
              :type="PireTypeOptionsObj[row.pierType]?.color"
              >{{ PireTypeOptionsObj[row.pierType]?.label }}</el-tag
            >
          </template>
          <template #remark="{ row }">
            <div v-if="row.type === 'dock'">{{ row.pierRemark }}</div>
            <div v-else>{{ row.remark }}</div>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>

<script setup lang="ts">
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { PureTableBar } from "@/components/RePureTableBar";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Refresh from "@iconify-icons/ep/refresh";
import AddFill from "@iconify-icons/ri/add-circle-line";
import "element-plus/dist/index.css";
import { defineComponent, onMounted, ref } from "vue";
import { useAreaManagement } from "./utils/hooks"; // Ensure the path is correct
import {
  BerthStatusOptions,
  BerthStatusTagOptions,
  PireTypeOptions,
  PireTypeOptionsObj
} from "@/contans";
import { transformI18n } from "@/plugins/i18n";
defineComponent({
  name: "AreaManagement",
  inheritAttrs: false
});
const tableRef = ref();
const formRef = ref();

// Use the useAreaManagement hook
const {
  form,
  loading,
  columns,
  treeData,
  expandedKeys,
  onSearch,
  resetForm,
  pagination,
  openDialog,
  handleDelete,
  handleNodeExpand,
  handleNodeCollapse
} = useAreaManagement();

// Fetch data when the component is mounted
onMounted(() => {
  onSearch();
});
</script>

<style lang="less" scoped>
.main {
  padding: 10px;
  background-color: var(--el-bg-color);
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  
  .search-form {
    margin-bottom: 15px;
    padding: 15px;
    border-radius: 4px;
  }
}
</style>
