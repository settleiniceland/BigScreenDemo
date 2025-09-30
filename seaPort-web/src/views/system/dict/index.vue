<template>
  <div class="app-container">
    <el-form
      v-show="showSearch"
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="字典名称" prop="dictName">
        <el-input
          v-model="queryParams.dictName"
          placeholder="请输入字典名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="dictType">
        <el-input
          v-model="queryParams.dictType"
          placeholder="请输入字典类型"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="字典状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd"
          >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport"
          >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Refresh"
          @click="handleRefreshCache"
          >刷新缓存
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="typeList"
      @selectionChange="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" align="center" prop="dictId" />
      <el-table-column
        label="字典名称"
        align="center"
        prop="dictName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="字典类型"
        align="center"
        :show-overflow-tooltip="true"
      >
        <template #default="scope">
          <!-- <router-link :to="'/system/dict-data/index/' + scope.row.dictId" class="link-type"> -->
          <span class="link-type" @click="typeDataClick(scope.row)">{{
            scope.row.dictType
          }}</span>
          <!-- </router-link> -->
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="备注"
        align="center"
        prop="remark"
        :show-overflow-tooltip="true"
      />
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
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="Edit"
            @click="handleUpdate(scope.row)"
            >修改
          </el-button>
          <el-button
            link
            type="primary"
            :icon="Delete"
            @click="handleDelete(scope.row)"
            >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      class="pagination"
      :total="total"
      @pagination="getList"
    />

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="dictRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
              >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Dict" lang="ts">
import Pagination from "@/components/Pagination/index.vue";
import DictTag from "@/components/DictTag/index.vue";
import useDictStore from "@/store/modules/dict";
import {
  listType,
  getType,
  delType,
  addType,
  updateType,
  refreshCache
} from "@/api/system/dict/type";
import { parseTime } from "@/utils/ruoyi";
import {
  getCurrentInstance,
  ComponentInternalInstance,
  ref,
  reactive,
  toRefs
} from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Plus,
  Delete,
  Edit,
  Download,
  Search,
  Refresh
} from "@element-plus/icons-vue";

defineOptions({
  name: "Dict"
});
const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// const { sys_normal_disable } = proxy!.useDict('sys_normal_disable');
const sys_normal_disable = ref([
  { label: "正常", value: "0" },
  { label: "停用", value: "1" }
]);

const typeList = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref<any>([]);
import { useRoute, useRouter } from "vue-router";

const router = useRouter();

const data = reactive<{
  form: any;
  queryParams: any;
  rules: any;
}>({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictName: undefined,
    dictType: undefined,
    status: undefined
  },
  rules: {
    dictName: [
      { required: true, message: "字典名称不能为空", trigger: "blur" }
    ],
    dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

// 类型点击
const typeDataClick = (row: any) => {
  router.push({ path: `/system/type_data`, query: { dictId: row.dictId } });
};

/** 查询字典类型列表 */
function getList() {
  loading.value = true;
  listType(proxy!.addDateRange(queryParams.value, dateRange.value)).then(
    (response: any) => {
      typeList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    }
  );
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined
  };
  proxy!.resetForm("dictRef");
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

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加字典类型";
}

/** 多选框选中数据 */
function handleSelectionChange(selection: any[]) {
  ids.value = selection.map(item => item.dictId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 修改按钮操作 */
function handleUpdate(row: any) {
  reset();
  const dictId = row.dictId || ids.value;
  getType(dictId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典类型";
  });
}

/** 提交按钮 */
function submitForm() {
  (proxy?.$refs["dictRef"] as any).validate((valid: any) => {
    if (valid) {
      if (form.value.dictId !== undefined) {
        updateType(form.value).then(response => {
          // proxy!.$modal.msgSuccess('修改成功');
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addType(form.value).then(response => {
          // proxy!.$modal.msgSuccess('新增成功');
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row: any) {
  const dictIds = row.dictId || ids.value;
  const confirm = ElMessageBox.confirm(
    '是否确认删除字典编号为"' + dictIds + '"的数据项？',
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  );
  confirm
    .then(() => {
      delType(dictIds).then(() => {
        ElMessage.success("删除成功");
        getList();
      });
    })
    .catch(() => {
      ElMessage.warning("删除取消");
    });
}

/** 导出按钮操作 */
function handleExport() {
  proxy!.download(
    "system/dict/type/export",
    {
      ...queryParams.value
    },
    `dict_${new Date().getTime()}.xlsx`
  );
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    // proxy!.$modal.msgSuccess('刷新成功');
    ElMessage.success("刷新成功");
    useDictStore().cleanDict();
  });
}

getList();
</script>

<style scoped>
.app-container {
  padding: 16px;
  background-color: #f9f9f9;
}

.el-form {
  margin-bottom: 16px;
  background: #ffffff;
  padding: 12px;
}

.el-form-item {
  margin-bottom: 8px;
}

.el-button {
  margin-right: 8px;
}

.el-row {
  margin-bottom: 12px;
}

.el-table {
  background: #ffffff;
  border: 1px solid #e0e0e0;
}

.el-table th,
.el-table td {
  text-align: center;
  padding: 10px;
}

.el-table tr:nth-child(even) {
  background-color: #f7f7f7;
}

.link-type {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}

.link-type:hover {
  color: #66b1ff;
}

.pagination {
  margin-top: 12px;
  display: flex;
  justify-content: end;
}
</style>
