<template>
  <el-dialog :title="transformI18n('imip.others.other1')" v-model="dialogVisible" width="1600px" top="5vh" :style="{height: '90vh'} " draggable>
    <div style="max-height: 75vh; overflow-y: auto; padding-right: 12px;">
      <el-form :model="formData" :rules="formDataRules" label-width="120px" ref="formRef">
        <el-card class="mb-4" :header="transformI18n('imip.others.other2')">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj42')" prop="shipRade">
                <el-select v-model="formData.shipRade" style="width: 100%">
                  <el-option
                    v-for="item in shipRadeDict"
                    :key="item" :label="item"
                    :value="item"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj1')" prop="shipName">
                <el-input v-model="formData.shipName" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj4')" prop="hbId">
                <el-select v-model="formData.hbId" @change="handleHbChange" style="width: 100%">
                  <el-option
                    v-for="item in hbData"
                    :key="item.berthId" :label="item.berthCode"
                    :value="item.berthId"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.others.other3')" prop="shipLength">
                <el-input-number v-model="formData.shipLength" style="width: 100%"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.others.other4')" prop="planTonnage">
                <el-input-number v-model="formData.planTonnage" style="width: 100%"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj29')" prop="contractFee">
                <el-input-number v-model="formData.contractFee" style="width: 100%"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item :label="transformI18n('imip.page1.obj37')" prop="remark">
                <el-input type="textarea" :row="7" v-model="formData.remark" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
        <el-card class="mb-4" :header="transformI18n('imip.others.other6')">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.others.other7')" prop="arrivalTime">
                <el-date-picker
                  v-model="formData.arrivalTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj19')" prop="planDockingTime">
                <el-date-picker
                  v-model="formData.planDockingTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj20')" prop="dockingTime">
                <el-date-picker
                  v-model="formData.dockingTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.others.other8')" prop="operationTime">
                <el-date-picker
                  v-model="formData.operationTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.others.other9')" prop="endTime">
                <el-date-picker
                  v-model="formData.endTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj23')" prop="outBerthTime">
                <el-date-picker
                  v-model="formData.outBerthTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item :label="transformI18n('imip.page1.obj24')" prop="leaveTime">
                <el-date-picker
                  v-model="formData.leaveTime"
                  type="datetime"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
        <el-card class="mb-4" :header="transformI18n('imip.others.other10')">
          <el-row :gutter="0">
            <el-col :span="3" style="text-align: left">
              <el-form-item :label="transformI18n('imip.others.other11')">
                <el-input :value="1" disabled input-style="text-align: center;"/>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="transformI18n('imip.others.other5')" prop="remark01">
                <el-select v-model="formData.remark01" style="width: 100%">
                  <el-option
                    v-for="item in loadTypeDict"
                    :key="item" :label="item"
                    :value="item"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="transformI18n('imip.others.other12')" prop="materialName">
                <el-select filterable clearable v-model="formData.materialName" @change="handleFdmaterialChange" style="width: 100%">
                  <el-option
                    v-for="item in materialData"
                    :key="item.id" :label="item.materialName"
                    :value="item.materialName"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="transformI18n('imip.page1.obj12')" prop="usageUnit">
                <el-input v-model="formData.usageUnit" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="transformI18n('imip.page1.obj14')+'('+formData.remark03+')'" prop="tonnage">
                <el-input-number v-model="formData.tonnage" style="width: 100%"/>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="transformI18n('imip.page1.obj13')" prop="unloadWeight">
                <el-input-number v-model="formData.unloadWeight" style="width: 100%"/>
              </el-form-item>
            </el-col>
            <el-col :span="1" style="text-align: right" :class="{ 'readonly-wrapper': !formData.params.allowSubMaterial }">
              <el-button type="primary" @click="addMaterial">{{ transformI18n('imip.others.other13') }}</el-button>
            </el-col>
          </el-row>
          <el-divider/>
          <div v-for="(item, index) in formData.params.subMaterial" :key="index">
            <el-row :gutter="0">
              <el-col :span="3" style="text-align: left">
                <el-form-item :label="transformI18n('imip.others.other11')">
                  <el-input v-model="item.loadSequence" disabled input-style="text-align: center;"/>
                </el-form-item>
              </el-col>
              <el-col :span="4">
              <el-form-item :label="transformI18n('imip.others.other5')" prop="remark01">
                <el-select v-model="item.remark01" style="width: 100%">
                  <el-option
                    v-for="val in loadTypeDict"
                    :key="val" :label="val"
                    :value="val"/>
                </el-select>
              </el-form-item>
            </el-col>
              <el-col :span="4">
                <el-form-item :label="transformI18n('imip.others.other12')">
                  <el-select filterable clearable v-model="item.materialName" @change="val => handleSubmaterialChange(val,item)" style="width: 100%">
                    <el-option
                      v-for="item in materialData"
                      :key="item.id" :label="item.materialName"
                      :value="item.materialName"/>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :label="transformI18n('imip.page1.obj12')">
                  <el-input v-model="item.usageUnit" />
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :label="transformI18n('imip.page1.obj14')+'('+item.remark02+')'">
                  <el-input-number v-model="item.tonnage" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item :label="transformI18n('imip.page1.obj13')">
                  <el-input-number v-model="item.unloadWeight" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="1" style="text-align: right" :class="{ 'readonly-wrapper': !formData.params.allowSubMaterial }">
                <el-button type="danger" @click="removeMaterial(index)">{{ transformI18n('imip.others.other14') }}</el-button>
              </el-col>
            </el-row>
            <el-divider/>
          </div>
        </el-card>
      </el-form>
    </div>
    <!-- 提交按钮 -->
    <div style="text-align: center; margin-top: 20px">
      <el-button type="primary" @click="submit">提交了</el-button>
    </div>
  </el-dialog>
</template>

<script setup>
import {ref,onMounted,reactive} from "vue";
import { ElMessage } from 'element-plus'
import { transformI18n } from "@/plugins/i18n";
import { getMaterialStatusList } from "@/api/pier/material";
import { getHarborBerthList } from "@/api/pier";
import { addNewPlan,getPlan,updateNewPlan } from '@/api/plan/newApi'
const dialogVisible = ref(false)
const formRef = ref()
const emit = defineEmits(['success'])
const formData = reactive({
  id: undefined,
  shipRade: undefined,  //内/外贸船
  shipName: undefined,  //船名
  hbId: undefined,      //泊位主键
  hbName: undefined,    //泊位名称
  shipLength: undefined,//船长度(米)
  planTonnage: undefined,//计划（吨/件）
  remark01: undefined,  //装卸船类型
  remark03: undefined, //按吨计，按件计标识
  materialName: undefined,//物料名
  usageUnit: undefined,   //使用单位
  tonnage: undefined,   //实际（吨/件）
  unloadWeight: undefined,//已作业量
  arrivalTime: undefined, //到港日期
  planDockingTime: undefined, //计划靠泊时间
  dockingTime: undefined,   //靠泊时间
  operationTime: undefined, //开始作业时间
  endTime: undefined,       //作业结束时间
  outBerthTime: undefined,  //离泊时间
  leaveTime: undefined,     //离港时间
  remark: undefined,     //备注
  params: {
    subMaterial: []
  },
  contractFee: undefined, //滞期费  $/day
})
const formDataRules = {
  shipRade: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  shipName: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  hbId: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  hbName: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  shipLength: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  planTonnage: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  remark01: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  materialName: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  usageUnit: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }],
  tonnage: [{ required: true, message: transformI18n('不能为空'), trigger: 'blur' }]
}
const shipRadeDict = ref(['外','内'])
const loadTypeDict = ref(['装船 Memuat kapal','卸船 Membongkar kapal'])
const hbData = ref([])
const materialData = ref([])
const initFormData = () => {
  Object.assign(formData, {
    id: undefined,
    shipRade: undefined,
    shipName: undefined,
    hbId: undefined,
    hbName: undefined,
    shipLength: undefined,
    planTonnage: undefined,
    remark01: undefined,
    remark03: undefined,
    materialName: undefined,
    usageUnit: undefined,
    tonnage: undefined,
    unloadWeight: undefined,
    arrivalTime: undefined,
    planDockingTime: undefined,
    dockingTime: undefined,
    operationTime: undefined,
    endTime: undefined,
    outBerthTime: undefined,
    leaveTime: undefined,
    remark: undefined,
    params: {
      subMaterial: [],
      allowSubMaterial: undefined,
    },
  })
}
onMounted(()=>{
  getData()
})
const getData =async () =>{
  const resp = await getMaterialStatusList();
  materialData.value = resp.rows;
  const hbResp = await getHarborBerthList();
  hbData.value = hbResp.rows;
}
const open = async (planId)=>{
  initFormData()
  if(planId){
    const resp = await getPlan(planId);
    Object.assign(formData,resp.data)
  }else{
    formData.params.allowSubMaterial = true
  }
  dialogVisible.value = true
}
const addMaterial = () => {
  const sequenceNum = formData.params.subMaterial.length
  formData.params.subMaterial.push({
    materialName: undefined,
    remark03: undefined,
    usageUnit: undefined,
    tonnage: undefined,
    unloadWeight: undefined,
    loadSequence: sequenceNum+2
  });
};
const handleHbChange = (val) => {
  const selected = hbData.value.find(item => item.berthId === val)
  if(selected){
    formData.hbName = selected.berthCode
  }
}
const handleFdmaterialChange = (val) => {
  const selected = materialData.value.find(item => item.materialName === val)
  if(selected){
    formData.remark03 = selected.remark02
  }
}
const handleSubmaterialChange = (val, currentItem) => {
  const selected = materialData.value.find(item => item.materialName === val)
  if(selected){
    currentItem.remark02 = selected.remark02
  }
}
const removeMaterial = (index) => {
  formData.params.subMaterial.splice(index, 1);
  if(formData.params.subMaterial.length>0){
    for(let i=0;i<formData.params.subMaterial.length;i++){
      formData.params.subMaterial[i].loadSequence = i+2;
    }
  }
};
const validMaterials = () => {
  for (let i = 0; i < formData.params.subMaterial.length; i++) {
    const item = formData.params.subMaterial[i];
    if(!item.loadSequence){
      ElMessage.error(`第 ${i + 2} 个物料装卸排序不能为空`);
      return false;
    }
    if (!item.materialName) {
      ElMessage.error(`第 ${i + 2} 个物料名称不能为空`);
      return false;
    }
    if (!item.usageUnit) {
      ElMessage.error(`第 ${i + 2} 个使用单位不能为空`);
      return false;
    }
    if (!item.tonnage) {
      ElMessage.error(`第 ${i + 2} 个实际(吨/件)不能为空`);
      return false;
    }
  }
  return true;
}
const formatToLocalDateTimeStr = (input) => {
  if (!input) return ''
  const date = typeof input === 'string' ? new Date(input) : input
  if (!(date instanceof Date) || isNaN(date.getTime())) return ''
  const pad = (n) => n.toString().padStart(2, '0')
  const year = date.getFullYear()
  const month = pad(date.getMonth() + 1)
  const day = pad(date.getDate())
  const hours = pad(date.getHours())
  const minutes = pad(date.getMinutes())
  const seconds = pad(date.getSeconds())
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
const submit = () => {
  formRef.value.validate((valid)=>{
    if(valid&&validMaterials()){
      formData.arrivalTime=formatToLocalDateTimeStr(formData.arrivalTime)
      formData.planDockingTime=formatToLocalDateTimeStr(formData.planDockingTime)
      formData.dockingTime=formatToLocalDateTimeStr(formData.dockingTime)
      formData.operationTime=formatToLocalDateTimeStr(formData.operationTime)
      formData.endTime=formatToLocalDateTimeStr(formData.endTime)
      formData.outBerthTime=formatToLocalDateTimeStr(formData.outBerthTime)
      formData.leaveTime=formatToLocalDateTimeStr(formData.leaveTime)
      if(formData.id){
        updateNewPlan(formData).then((resp)=>{
          if(resp.code === 200){
            ElMessage.success("修改成功")
            emit('success')
            dialogVisible.value = false
          }
        })
      }else{
        addNewPlan(formData).then((resp)=>{
          if(resp.code === 200){
            ElMessage.success("新增成功")
            emit('success')
            dialogVisible.value = false
          }
        })
      }
    }
  })
  
}
defineExpose({ open })
</script>
<style scoped>
.readonly-wrapper {
  pointer-events: none;
  opacity: 0.6;
  filter: grayscale(0.4);
}
</style>