
<template>
  <div class="main">
    <el-form
      inline
      ref="formRef"
      :model="queryForm"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto">
      <el-form-item :label='transformI18n("原因")' prop="detail">
        <el-input
          v-model="queryForm.detail"
          :placeholder='transformI18n("原因")'
          class="!w-[200px]"
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("是否免滞期费")' prop="avoidCollectFee">
        <el-select
          v-model="queryForm.avoidCollectFee"
          :placeholder="transformI18n('是否免滞期费')"
          clearable
          class="!w-[200px]">
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item>
      <!-- <el-form-item :label='transformI18n("是否不计入效率")' prop="avoidEfficiency">
        <el-select
          v-model="queryForm.avoidEfficiency"
          :placeholder="transformI18n('是否不计入效率')"
          clearable
          class="!w-[200px]">
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item> -->
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
import { useColumns } from "./columns";
import { ref,onMounted } from "vue";
import { transformI18n } from "@/plugins/i18n";
import editForm from "./editForm.vue";
import { getStopClassList,delStopClass } from "@/api/stopClass";
import { ElMessageBox, ElMessage } from 'element-plus'
const {
  columns,
} = useColumns();
const editFormRef = ref()
const formRef = ref();
const dataList = ref([]);
const openDialog = (id: number) =>{
  editFormRef.value.open(id)
}
const deleteItem =async (row: any) =>{
  ElMessageBox.confirm(
    '确定删除这个停止原因：<'+row.detail+'>?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async ()=>{
    const res = await delStopClass(row.id)
    if(res.code===200){
      ElMessage.success('删除成功')
      reset()
    }
  })
}
const queryForm = ref({
  detail: undefined,
  avoidCollectFee: undefined,
  avoidEfficiency: undefined
})
const commonDict = ref([
  {label:'是|Ya',value:true},
  {label:'否|Tidak',value:false}
])
const reset = () => {
  queryForm.value={
    detail: undefined,
    avoidCollectFee: undefined,
    avoidEfficiency: undefined
  }
  initData()
}
const initData =async () => {
  try {
    const { data } = (await getStopClassList(queryForm.value)) as any;
    dataList.value = data
  } catch (error) {}
};
onMounted(initData);
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