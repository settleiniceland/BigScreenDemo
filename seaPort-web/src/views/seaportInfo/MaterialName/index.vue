<script setup lang="ts">
import { useColumns } from "./columns";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import AddFill from "@iconify-icons/ep/plus";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Save from "@iconify-icons/ep/select";
import Close from "@iconify-icons/ep/close";
import { PureTableBar } from "@/components/RePureTableBar";
import Refresh from "@iconify-icons/ep/refresh";
import { reactive, ref, onMounted } from "vue";
import { transformI18n } from "@/plugins/i18n";
import { listData } from "@/api/system/dict/data";
const {
  columns,
  dataList,
  onAdd,
  onDel,
  editMap,
  onEdit,
  onCancel,
  onSave,
  fetchData,
  form,
  loading,
  resetForm,
  pagination,
  handleCurrentChange,
  handleSizeChange
} = useColumns();
const tableRef = ref();
const loadingTypeOptions = ref([]);
onMounted(async () => {
  // 获取装货类型字典
  const res = await listData({ dictType: "loading_type", pageSize: 50 });
  if (res.code === 200) {
    loadingTypeOptions.value = res.rows.map(item => ({
      label: item.dictLabel,
      value: item.dictValue
    }));
  }
});
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
      v-bind="$attrs"
    >
      <el-form-item :label="transformI18n('imip.page3.obj29')">
        <el-input
          v-model="form.materialName"
          :placeholder="transformI18n('imip.page3.obj29')"
          class="!w-[200px]"
          clearable
        />
      </el-form-item>
      <el-form-item :label="transformI18n('imip.page1.obj2')">
        <el-select
          v-model="form.materialStatus"
          :placeholder="transformI18n('imip.page1.obj2')"
          class="!w-[200px]"
          clearable
        >
          <el-option value="0" :label="transformI18n('imip.items.item3')" />
          <el-option value="1" :label="transformI18n('imip.items.item4')" />
        </el-select>
      </el-form-item>
      <el-form-item :label="transformI18n('imip.page1.loadingType')">
        <el-select
          v-model="form.remark01"
          :placeholder="transformI18n('imip.page1.loadingType')"
          class="!w-[200px]"
          clearable
        >
          <el-option
            v-for="item in loadingTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="fetchData"
          >{{ transformI18n("imip.page3.obj32") }}
        </el-button>
        <el-button :icon="useRenderIcon(Refresh)" @click="resetForm()">
          {{ transformI18n("imip.button.obj38") }}
        </el-button>
      </el-form-item>
    </el-form>
    <PureTableBar
      :title="transformI18n('imip.page1.obj3')"
      :columns="columns"
      :tableRef="tableRef?.getTableRef()"
      @refresh="fetchData"
    >
      <template #buttons>
        <el-button :icon="useRenderIcon(AddFill)" type="primary" @click="onAdd">
          {{ transformI18n("imip.page4.obj24") }}
        </el-button>
      </template>
      <template v-slot="{ dynamicColumns }">
        <div class="table-card">
          <pure-table
            row-key="id"
            :title="transformI18n('imip.page4.obj25')"
            align-whole="center"
            :header-cell-style="{
              background: 'var(--el-fill-color-light)',
              color: 'var(--el-text-color-primary)'
            }"
            :pagination="{
              pageSize: pagination.pageSize,
              currentPage: pagination.pageNum,
              total: pagination.total
            }"
            :data="dataList"
            :columns="dynamicColumns"
            @page-size-change="handleSizeChange"
            @page-current-change="handleCurrentChange"
          >
            <template #empty>
              <el-empty
                :description="transformI18n('imip.page4.obj26')"
                class="custom-empty"
              />
            </template>
            <template #append />
            <template #operation="{ row, $index }">
              <div v-if="!editMap[$index]?.editable">
                <el-button
                  class="reset-margin"
                  type="primary"
                  :icon="useRenderIcon(EditPen)"
                  @click="onEdit(row, $index)"
                  >{{ transformI18n("imip.button.obj1") }}
                </el-button>
                <el-popconfirm
                  :title="transformI18n('imip.page4.obj27')"
                  @confirm="onDel(row)"
                >
                  <template #reference>
                    <el-button type="danger" :icon="useRenderIcon(Delete)">
                      {{ transformI18n("imip.button.obj2") }}
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
              <div v-else>
                <el-button
                  class="reset-margin"
                  type="success"
                  :icon="useRenderIcon(Save)"
                  @click="onSave($index)"
                  >{{ transformI18n("imip.page4.obj28") }}
                </el-button>
                <el-button
                  class="reset-margin"
                  :icon="useRenderIcon(Close)"
                  @click="onCancel($index)"
                  >{{ transformI18n("imip.button.obj33") }}
                </el-button>
              </div>
            </template>
          </pure-table>
        </div>
      </template>
    </PureTableBar>
  </div>
</template>

<style scoped>
.custom-empty {
  padding: 20px;
  color: #999;
  font-size: 14px;
}
</style>
