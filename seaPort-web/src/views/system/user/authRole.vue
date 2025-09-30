<template>
  <div class="app-container">
    <h4 class="form-header h4">基本信息</h4>
    <el-form :model="form" label-width="80px">
      <el-row>
        <el-col :span="8" :offset="2">
          <el-form-item label="用户昵称" prop="nickName">
            <el-input v-model="form.nickName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="2">
          <el-form-item label="登录账号" prop="userName">
            <el-input v-model="form.userName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <h4 class="form-header h4">角色信息</h4>
    <el-table
      ref="roleRef"
      v-loading="loading"
      :row-key="getRowKey"
      :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
      @rowClick="clickRow"
      @selectionChange="handleSelectionChange"
    >
      <el-table-column label="序号" width="55" type="index" align="center">
        <template #default="scope">
          <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column type="selection" :reserve-selection="true" width="55" />
      <el-table-column label="角色编号" align="center" prop="roleId" />
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="权限字符" align="center" prop="roleKey" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      v-model:page="pageNum"
      v-model:limit="pageSize"
      :total="total"
    />

    <el-form label-width="100px">
      <div style="text-align: center; margin-left: -120px; margin-top: 30px">
        <el-button type="primary" @click="submitForm()">提交</el-button>
        <el-button @click="close()">返回</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup name="AuthRole" lang="ts">
import { getAuthRole, updateAuthRole } from "@/api/system/user";
import { parseTime } from "@/utils/ruoyi";
import {
  getCurrentInstance,
  ComponentInternalInstance,
  ref,
  nextTick
} from "vue";
import { useRoute, useRouter } from "vue-router";
import { message } from "@/utils/message";

const route = useRoute();
const router = useRouter();

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const loading = ref(true);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const roleIds = ref<any[]>([]);
const roles = ref<any[]>([]);
const form = ref({
  nickName: undefined,
  userName: undefined,
  userId: undefined
});
const roleRef = ref<any>();
/** 单击选中行数据 */
function clickRow(row: any) {
  roleRef.value.toggleRowSelection(row);
}
/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  roleIds.value = selection.map(item => item.roleId);
}
/** 保存选中的数据编号 */
function getRowKey(row: any) {
  return row.roleId;
}
/** 关闭按钮 */
function close() {
  router.go(-1);
}
/** 提交按钮 */
function submitForm() {
  const userId = form.value.userId;
  const rIds = roleIds.value.join(",");
  updateAuthRole({ userId: userId, roleIds: rIds }).then(response => {
    message("授权成功", {
      type: "success"
    });
    close();
  });
}

(() => {
  const userId = route.query && route.query.userId;
  if (userId) {
    loading.value = true;
    getAuthRole(userId).then((response: any) => {
      form.value = response.user;
      roles.value = response.roles;
      total.value = roles.value.length;
      nextTick(() => {
        roles.value.forEach(row => {
          if (row.flag) {
            (proxy?.$refs["roleRef"] as any).toggleRowSelection(row);
          }
        });
      });
      loading.value = false;
    });
  }
})();
</script>
<style scoped>
.app-container {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-header {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.form-container {
  margin-bottom: 30px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.table-container {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.action-form {
  margin-top: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
