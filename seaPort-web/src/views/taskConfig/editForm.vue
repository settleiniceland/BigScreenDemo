<template>
  <el-dialog 
    :title="transformI18n('题目')" 
    v-model="dialogVisible" 
    width="80vh" 
    top="5vh" 
    :style="{ height: '58vh' }" 
    draggable>
    <el-form :model="formData" :rules="rules" ref="formRef">
      <el-form-item :label='transformI18n("任务名称")' prop="taskName">
        <el-input
          v-model="formData.taskName"
          :placeholder='transformI18n("任务名称")'
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("泊位名称")' prop="hbName">
        <el-input
          v-model="formData.hbName"
          :placeholder='transformI18n("泊位名称")'
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("数据来源URL")' prop="targetUrl">
        <el-input
          v-model="formData.targetUrl"
          :placeholder='transformI18n("数据来源URL")'
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("间隔(毫秒)")' prop="frequencyMs">
        <el-input
          type="number"
          :min="10000"
          v-model.number="formData.frequencyMs"
          :placeholder='transformI18n("间隔(毫秒)")'
          @change="val => formData.frequencyMs = Math.max(10000, val)"
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("是否启动")' prop="enabled">
        <el-select
          v-model="formData.enabled"
          :placeholder="transformI18n('是否启动')"
          clearable>
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item :label='transformI18n("备注")' prop="detail">
        <el-input
          type="textarea"
          :rows="5"
          v-model="formData.remark"
          clearable
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div style="display: flex; justify-content: flex-end;">
        <el-button type="success" @click="submit()">
          {{ transformI18n("提交") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import {ref} from "vue";
import { transformI18n } from "@/plugins/i18n";
import {getTaskConfigList,addTaskConfig,updateTaskConfig} from '@/api/taskConfig'
const dialogVisible = ref(false)
const formRef = ref()
const emit = defineEmits(['success'])
const formData = ref({
  id: undefined,
  taskName: undefined,
  hbName: undefined,
  targetUrl: undefined,
  frequencyMs: undefined,
  enabled: undefined,
  remark: undefined
})
const rules = {
  taskName: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  hbName: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  targetUrl: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  frequencyMs: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  enabled: [{required: true, message: transformI18n(''), trigger: 'blur'}],
}
const commonDict = ref([
  {label:'是|Ya',value:true},
  {label:'否|Tidak',value:false}
])
const open = async (id: number)=>{
  dialogVisible.value = true;
  formData.value = {
    id: undefined,
    taskName: undefined,
    hbName: undefined,
    targetUrl: undefined,
    frequencyMs: undefined,
    enabled: undefined,
    remark: undefined
  }
  if(id!==null&&id!==undefined){
    const res = await getTaskConfigList({id:id});
    formData.value = res.data[0];
  }
}
const submit =async ()=>{
  formRef.value.validate(async (vaid)=>{
    if(vaid){
      let res;
      if(formData.value.id){
        res = await updateTaskConfig(formData.value)
      }else{
        res = await addTaskConfig(formData.value)
      }
      if(res.code === 200){
        emit("success")
        dialogVisible.value = false;
      }
    }
  })
}
defineExpose({ open })
</script>
<style scoped>
</style>