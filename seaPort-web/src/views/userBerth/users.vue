<template>
  <el-dialog :title="transformI18n('imip.items.item17')" v-model="dialogVisible" width="1600px" top="5vh" :style="{ height: '90vh' }" draggable>
    <div class="table-wrapper">
      <div class="left-panel">
        <div class="left-header">
          <div class="left-header-title">
            <h3>{{transformI18n("imip.items.item11")}}：</h3>
            <el-button type="danger" round @click="clearSelected">{{transformI18n("imip.items.item14")}}</el-button>
          </div>
          <p>{{transformI18n("imip.items.item12")}}：{{ berthCode }}</p>
          <h3>{{transformI18n("imip.items.item13")}}：</h3>
        </div>
        <div class="left-scroll text-list">
          <p
            v-for="(item, index) in selectedUser"
            :key="index"
            style="margin-bottom: 10px;"
          >
            {{ item.userName + ":" + item.nickName }}
            <hr v-if="index !== selectedUser.length - 1" />
          </p>
        </div>
      </div>
      <div class="right-panel">
        <div class="right-header">
          <el-form
            class="-mb-15px"
            :model="queryParams"
            :inline="true"
            style="margin-top: 6px;"
            label-width="68px">
            <el-form-item :label="transformI18n('imip.items.item15')" prop="userName">
              <el-input
                v-model="queryParams.userName"
                :placeholder="transformI18n('imip.items.item15')"
                clearable
                class="!w-240px"
                @blur="handleQuery"/>
            </el-form-item>
            <el-form-item :label="transformI18n('imip.items.item16')" prop="nickName">
              <el-input
                v-model="queryParams.nickName"
                :placeholder="transformI18n('imip.items.item16')"
                clearable
                class="!w-240px"
                @blur="handleQuery"/>
            </el-form-item>
            <el-form-item>
              <el-button @click="handleQuery" type="primary" plain>{{ transformI18n('imip.page3.obj32') }}</el-button>
              <el-button @click="handleResetQuery" type="warning" plain>{{ transformI18n('imip.button.obj38') }}</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="right-scroll">
          <el-table
            ref="userListRef"
            :data="userData"
            row-key="userId"
            @rowClick="clickRow"
            @selectionChange="handleSelectionChange">
            <el-table-column type="selection" :reserve-selection="true" width="55" />
            <el-table-column :label="transformI18n('imip.items.item15')" align="center" prop="userName" />
            <el-table-column :label="transformI18n('imip.items.item16')" align="center" prop="nickName" />
            <el-table-column :label="transformI18n('imip.page1.obj37')" align="center" prop="remark" />
          </el-table>
        </div>
      </div>
      <el-button type="success" round @click="submit">{{ transformI18n('imip.button.obj34') }}</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref,onMounted,reactive} from "vue";
import { transformI18n } from "@/plugins/i18n";
import { getSelectBerthUserList, batchUpdateUserBerth, listUser } from "@/api/userBerth";
// 获取路由中的 query 参数
const userListRef =ref<any>();
const berthCode = ref<string | null>(null);
const userData = ref<any[]>([]);
const dialogVisible = ref(false)
const selectedUser = ref<any[]>([]);
const emit = defineEmits(['success'])
const queryParams = reactive({
  searchValue: "ALL",
  userName: undefined,
  nickName: undefined,
})
onMounted(()=>{
  handleResetQuery();
})
const handleQuery = async ()=>{
  const res: any = await listUser(queryParams);
  if (res.code === 200) {
    userData.value = res.rows;
  }
}
const resetQueryParams = ()=>{
  Object.assign(queryParams, {
    searchValue: "ALL",
    userName: undefined,
    nickName: undefined,
  });
}
const handleResetQuery = ()=>{
  resetQueryParams();
  handleQuery();
}
const clickRow = (row: any)=>{
  userListRef.value.toggleRowSelection(row);
}
const handleSelectionChange = (selection: any[])=>{
  selectedUser.value = selection
}
const clearSelected = ()=>{
  userListRef.value.clearSelection()
}
const open = async (code: string)=>{
  selectedUser.value = [];
  dialogVisible.value = true
  berthCode.value = code
  const res = await getSelectBerthUserList({ berthCode: code });
  userListRef.value.clearSelection();
  const userMap = new Map<number, any>();
  userData.value.forEach((row: any)=>{
    userMap.set(row.userId,row);
  })
  const rowsToSelect = res.data.map((row: any) => userMap.get(row.userId)).filter(Boolean);
  for (const row of rowsToSelect) {
    userListRef.value.toggleRowSelection(row, true);
  }
  selectedUser.value = [...rowsToSelect]
}
defineExpose({ open })
const submit = async()=>{
  const submitUserBerth = [];
  selectedUser.value.forEach((item)=>{
    submitUserBerth.push({
      userId: item.userId,
      userName: item.userName,
      nickName: item.nickName,
      berthCode: berthCode.value
    })
  })
  const res = await batchUpdateUserBerth(submitUserBerth,berthCode.value)
  if(res.code === 200){
    handleResetQuery();
    emit('success')
    dialogVisible.value = false
  }
}
</script>
<style scoped>
.table-wrapper {
  width: 100%;
  height: 83vh;
  display: flex;
  gap: 20px;
}
.left-panel {
  flex: 2;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eee;
  padding-right: 10px;
  overflow: hidden;
}
.left-header {
  flex-shrink: 0;
}
.left-header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.left-scroll {
  flex: 1;
  overflow-y: auto;
}
.right-panel {
  flex: 5;
  display: flex;
  flex-direction: column;
  padding-left: 10px;
  border: 1px solid #ccc;
  overflow: hidden;
}
.right-header {
  flex-shrink: 0;
}
.right-scroll {
  flex: 1;
  overflow-y: auto;
}
.text-list {
  font-size: 16px;
  color: #333;
}
</style>