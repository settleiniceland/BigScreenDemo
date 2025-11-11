<template>
  <el-dialog 
    :title="transformI18n('暂停原因维护')" 
    v-model="dialogVisible" 
    width="80vh" 
    top="12vh" 
    :style="{ height: '39vh' }" 
    draggable>
    <el-form :model="formData" :rules="rules" ref="formRef">
      <el-form-item :label='transformI18n("是否免滞期费")' prop="avoidCollectFee">
        <el-select
          v-model="formData.avoidCollectFee"
          :placeholder="transformI18n('是否免滞期费')"
          clearable>
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item>
      <!-- <el-form-item :label='transformI18n("是否不计入效率")' prop="avoidEfficiency">
        <el-select
          v-model="formData.avoidEfficiency"
          :placeholder="transformI18n('是否不计入效率')"
          clearable>
          <el-option
            v-for="item in commonDict"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
        </el-select>
      </el-form-item> -->
      <el-form-item :label='transformI18n("原因")' prop="detail">
        <el-input
          type="textarea"
          :rows="3"
          v-model="formData.detail"
          :placeholder='transformI18n("原因")'
          clearable
        />
      </el-form-item>
      <el-form-item :label='transformI18n("原因(印尼文)")' prop="detail">
        <el-input
          type="textarea"
          :rows="3"
          v-model="formData.remark01"
          :placeholder='transformI18n("原因(印尼文)")'
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
import {getStopClassList,addStopClass,updateStopClass} from "@/api/stopClass";
const dialogVisible = ref(false)
const formRef = ref()
const emit = defineEmits(['success'])
const formData = ref({
  id: undefined,
  detail: undefined,
  avoidCollectFee: undefined,
  remark01: undefined
})
const rules = {
  detail: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  avoidCollectFee: [{required: true, message: transformI18n(''), trigger: 'blur'}],
  remark01: [{required: true, message: transformI18n(''), trigger: 'blur'}],
}
const commonDict = ref([
  {label:'是|Ya',value:true},
  {label:'否|Tidak',value:false}
])
const open = async (id: number)=>{
  dialogVisible.value = true;
  formData.value = {
    id: undefined,
    detail: undefined,
    avoidCollectFee: undefined,
    remark01: undefined
  }
  if(id!==null&&id!==undefined){
    const res = await getStopClassList({id:id});
    formData.value = res.data[0];
  }
}
const submit = async ()=>{
  formRef.value.validate(async (vaid)=>{
    if(vaid){
      let res;
      if(formData.value.id !== null && formData.value.id !== undefined && formData.value.id !== ''){
        res = await updateStopClass(formData.value)
      }else{
        res = await addStopClass(formData.value)
      } 
      if(res.code === 200){
        emit('success')
        dialogVisible.value = false;
      }
    }
  })
}
defineExpose({ open })
</script>
<style scoped>
</style>