<template>
  <div class="main">
    <el-form
      inline
      ref="formRef"
      :model="queryForm"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto">
      <el-form-item :label='transformI18n("任务名")' prop="taskName">
        <el-input
          v-model="queryForm.taskName"
          :placeholder='transformI18n("任务名")'
          class="!w-[200px]"
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("泊位")' prop="hbName">
        <el-input
          v-model="queryForm.hbName"
          :placeholder='transformI18n("泊位")'
          class="!w-[200px]"
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("是否开启")' prop="enabled">
        <el-select
          v-model="queryForm.enabled"
          :placeholder="transformI18n('是否开启')"
          clearable
          class="!w-[200px]">
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="initData">
          {{ transformI18n("imip.page3.obj32") }}
        </el-button>
        <el-button  @click="reset">
          {{ transformI18n("imip.button.obj38") }}
        </el-button>
      </el-form-item>
      <el-form-item style="margin-left: auto">
        <el-button type="success" @click="openDialog(undefined)">
          {{ transformI18n("新增") }}
        </el-button>
      </el-form-item> 
    </el-form>
    <pure-table
      row-key="id"
      :title="transformI18n('空窗期原因维护')"
      align-whole="center"
      :header-cell-style="{
        background: 'var(--el-fill-color-light)',
        color: 'var(--el-text-color-primary)'}"
      border
      :data="dataList"
      :columns="columns"
      :pagination="false">
      <template #operation="{ row }">
        <el-button
          class="reset-margin"
          link :size="size" type="primary"
          @click="openDialog(row.id)">
          {{ transformI18n('修改') }}
        </el-button>
        <el-button
          class="reset-margin"
          link :size="size" type="warning"
          @click="deleteItem(row)">
          {{ transformI18n('删除') }}
        </el-button>
      </template>
    </pure-table>
    <edit-form ref="editFormRef" @success="initData"/>
  </div>
</template>
<script setup lang="ts">
import editForm from "./editForm.vue";
import { ref,onMounted } from "vue";
import { transformI18n } from "@/plugins/i18n";
import { ElMessageBox, ElMessage } from 'element-plus'
import {getTaskConfigList,delTaskConfig} from '@/api/taskConfig'
const editFormRef = ref()
const formRef = ref();
const dataList = ref([]);
const queryForm = ref({
  taskName: undefined,
  hbName: undefined,
  enabled: undefined,
})
const commonDict = ref([
  {label:'是|Ya',value:true},
  {label:'否|Tidak',value:false}
])
const openDialog = (id: number) =>{
  editFormRef.value.open(id)
}
const initData = async () => {
  try{
    const {data} = (await getTaskConfigList(queryForm.value)) as any;
    dataList.value = data
  } catch (error) {}
}
const reset = () => {
  queryForm.value={
    taskName: undefined,
    hbName: undefined,
    enabled: undefined,
  }
  initData();
}
const deleteItem =async (row: any) =>{
  ElMessageBox.confirm(
    '确定删除这个定时任务：<'+row.taskName+'>?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async ()=>{
    const res = await delTaskConfig(row.id)
    if(res.code===200){
      ElMessage.success('删除成功')
      reset()
    }
  })
}
onMounted(initData);
const columns = [
  {
    label: transformI18n("是否开启"),
    sortable: false,
    prop: "enabled",
  },
  {
    label: transformI18n("任务名"),
    sortable: false,
    prop: "taskName",
  },
  {
    label: transformI18n("泊位"),
    sortable: false,
    prop: "hbName",
  },
  {
    label: transformI18n("数据源URL"),
    sortable: false,
    width: 350,
    prop: "targetUrl",
  },
  {
    label: transformI18n("间隔（毫秒）"),
    sortable: false,
    prop: "frequencyMs",
  },
  {
    label: transformI18n("备注"),
    sortable: false,
    prop: "remark",
  },
  {
    label: transformI18n("imip.page1.obj38"),
    fixed: "right",
    width: 150,
    slot: "operation"
  }
]
</script>
<style scoped>
.main {
  padding: 10px;
  background-color: var(--el-bg-color);
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  .search-form {
    margin-bottom: 15px;
    padding: 15px;
    border-radius: 4px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
  }
}
</style>