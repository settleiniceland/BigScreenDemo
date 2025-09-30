<template>
  <div class="app-container">
    <el-form
      v-show="showSearch"
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="任务名称" prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          placeholder="请输入任务名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务组名" prop="jobGroup">
        <el-select
          v-model="queryParams.jobGroup"
          placeholder="请选择任务组名"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in sys_job_group"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="执行状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择执行状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in sys_common_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="执行时间" style="width: 308px">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery"
          >搜索</el-button
        >
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['monitor:job:remove']"
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['monitor:job:remove']"
          type="danger"
          plain
          :icon="Delete"
          @click="handleClean"
          >清空</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['monitor:job:export']"
          type="warning"
          plain
          :icon="Download"
          @click="handleExport"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Close" @click="handleClose"
          >关闭</el-button
        >
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="jobLogList"
      @selectionChange="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        label="日志编号"
        width="80"
        align="center"
        prop="jobLogId"
      />
      <el-table-column
        label="任务名称"
        align="center"
        prop="jobName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="任务组名"
        align="center"
        prop="jobGroup"
        :show-overflow-tooltip="true"
      >
        <template #default="scope">
          <dict-tag :options="sys_job_group" :value="scope.row.jobGroup" />
        </template>
      </el-table-column>
      <el-table-column
        label="调用目标字符串"
        align="center"
        prop="invokeTarget"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="日志信息"
        align="center"
        prop="jobMessage"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="执行状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_common_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="执行时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            v-hasPermi="['monitor:job:query']"
            link
            type="primary"
            :icon="View"
            @click="handleView(scope.row)"
            >详细</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      :total="total"
      @pagination="getList"
    />

    <!-- 调度日志详细 -->
    <el-dialog v-model="open" title="调度日志详细" width="700px" append-to-body>
      <el-form :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志序号：">{{ form.jobLogId }}</el-form-item>
            <el-form-item label="任务名称：">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务分组：">{{ form.jobGroup }}</el-form-item>
            <el-form-item label="执行时间：">{{
              form.createTime
            }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用方法：">{{
              form.invokeTarget
            }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="日志信息：">{{
              form.jobMessage
            }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行状态：">
              <div v-if="form.status == 0">正常</div>
              <div v-else-if="form.status == 1">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.status == 1" label="异常信息：">{{
              form.exceptionInfo
            }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="JobLog" lang="ts">
import { getJob } from "@/api/monitor/job";
import { listJobLog, delJobLog, cleanJobLog } from "@/api/monitor/jobLog";
import { parseTime } from "@/utils/ruoyi";
import { oneOf } from "@zeronejs/utils";
import {
  Refresh,
  Delete,
  Edit,
  Plus,
  Close,
  View,
  Search,
  Download
} from "@element-plus/icons-vue";
import {
  getCurrentInstance,
  ComponentInternalInstance,
  ref,
  reactive,
  toRefs
} from "vue";
import { useRoute } from "vue-router";
import router from "@/router";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { ElMessage, ElMessageBox } from "element-plus";

const { proxy } = getCurrentInstance() as any;
// const { sys_common_status, sys_job_group } = proxy!.useDict('sys_common_status', 'sys_job_group');
const sys_common_status = ref([
  { label: "正常", value: 0 },
  { label: "失败", value: 1 }
]);
const sys_job_group = ref([
  { label: "系统任务", value: "1" },
  { label: "用户任务", value: "0" }
]);

const jobLogList = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref<any>([]);
const route = useRoute();

const data = reactive<{
  form: any;
  queryParams: any;
}>({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictName: undefined,
    dictType: undefined,
    status: undefined
  }
});

const { queryParams, form } = toRefs(data);

/** 查询调度日志列表 */
function getList() {
  loading.value = true;
  listJobLog(proxy!.addDateRange(queryParams.value, dateRange.value)).then(
    (response: any) => {
      jobLogList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    }
  );
}
function onCloseTags() {
  const currentPath = router.currentRoute.value.path;
  useMultiTagsStoreHook().handleTags("splice", currentPath);
}
// 返回按钮
function handleClose() {
  // const obj = { path: "/monitor/job" };
  // proxy!.$tab.closeOpenPage(obj);
  onCloseTags();
  setTimeout(() => {
    router.go(-1);
  }, 300);
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy!.resetForm("queryRef");
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map(item => item.jobLogId);
  multiple.value = !selection.length;
}
/** 详细按钮操作 */
function handleView(row: any) {
  open.value = true;
  form.value = row;
}
/** 删除按钮操作 */
function handleDelete(row: any) {
  const confirm = ElMessageBox.confirm(
    '是否确认删除调度日志编号为"' + ids.value + '"的数据项？',
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  );
  confirm
    .then(() => {
      return delJobLog(ids.value);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    })
    .catch(() => {
      ElMessage.warning("删除取消");
    });

  //   .catch(() => {});
}
/** 清空按钮操作 */
function handleClean() {
  const confirm = ElMessageBox.confirm(
    "是否确认清空所有调度日志数据项？",
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  );
  confirm
    .then(() => {
      return cleanJobLog();
    })
    .then(() => {
      getList();
      ElMessage.success("清空成功");
    });
}
/** 导出按钮操作 */
function handleExport() {
  proxy!.download(
    "monitor/jobLog/export",
    {
      ...queryParams.value
    },
    `job_log_${new Date().getTime()}.xlsx`
  );
}

(() => {
  const jobId = oneOf(route.query.jobId);
  if (jobId !== undefined && jobId !== "0") {
    getJob(jobId).then((response: any) => {
      queryParams.value.jobName = response.data.jobName;
      queryParams.value.jobGroup = response.data.jobGroup;
      getList();
    });
  } else {
    getList();
  }
})();

getList();
</script>

<style scoped>
.app-container {
  padding: 16px;
  background-color: #f9f9f9;
}
.search-form {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.el-button-group .el-button {
  margin-right: 8px;
}
.el-table {
  margin-top: 15px;
}
</style>
