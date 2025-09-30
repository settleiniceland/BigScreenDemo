<script setup lang="ts">
import { ref, computed, nextTick, onMounted, reactive, h } from "vue";
import { PureTableBar } from "@/components/RePureTableBar";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { deviceDetection } from "@pureadmin/utils";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import AddFill from "@iconify-icons/ri/add-circle-line";
import {
  delIp,
  listIp,
  addIp,
  updateIp,
  getIpInfo
} from "@/api/system/accessIP";
import { listUser } from "@/api/system/user";
import { ElMessage } from "element-plus";
import { addDialog } from "@/components/ReDialog";
import editForm from "./form.vue";

defineOptions({
  name: "AccessIp"
});
const form = ref({
  name: "",
  pageNum: 1,
  pageSize: 10
});
const loading = ref(true);
const formRef = ref();
const columns: any[] = reactive([
  //多选
  {
    type: "selection",
    align: "left"
  },
  {
    label: "用户ID",
    prop: "userId"
  },
  {
    label: "名称",
    prop: "name"
  },
  {
    label: "IP地址",
    prop: "address"
  },
  {
    label: "操作",
    fixed: "right",
    width: 180,
    slot: "operation"
  }
]);
const dataList = ref([]);
const userList = ref([]);

const multipleIds = ref([]);
const pagination = reactive<any>({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const handleSelectionChange = val => {
  const ids = val.map(item => item.id);
  multipleIds.value = ids;
};
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  form.value.pageSize = val;
  getIpList();
};
const handleCurrentChange = (val: number) => {
  pagination.currentPage = val;
  form.value.pageNum = val;
  getIpList();
};

const getUserList = async () => {
  const res: any = await listUser({
    searchValue: "ALL"
  });
  if (res.code === 200) {
    userList.value = res.rows.map(item => ({
      label: item.nickName,
      value: item.userId
    }));
  }
};

const getIpList = async () => {
  loading.value = true;
  const res: any = await listIp(form.value);
  if (res.code === 200) {
    dataList.value = res.rows;
    pagination.total = res.total;
  }
  loading.value = false;
};
const handleDelete = async (ids: string[]) => {
  const res: any = await delIp(ids.join(","));
  if (res.code === 200) {
    ElMessage({
      message: "删除成功",
      type: "success"
    });
    getIpList();
  }
};

const openDialog = (title = "新增", row?: any) => {
  addDialog({
    title: `${title}IP地址`,
    props: {
      formInline: row,
      userList: userList.value
    },
    width: "500",
    draggable: true,
    fullscreen: deviceDetection(),
    fullscreenIcon: true,
    closeOnClickModal: false,
    contentRenderer: () => h(editForm, { ref: formRef }),
    beforeSure: (done, { options }) => {
      const { submitForm } = formRef.value;
      function chores() {
        ElMessage({
          message: `${title}成功`,
          type: "success"
        });
        done(); // 关闭弹框
        getIpList(); // 刷新表格数据
      }
      submitForm(formData => {
        // 表单规则校验通过
        if (title === "新增") {
          addIp(formData).then((res: any) => {
            if (res.code === 200) {
              chores();
            }
          });
        } else {
          updateIp(formData).then((res: any) => {
            if (res.code === 200) {
              chores();
            }
          });
        }
      });
    }
  });
};
onMounted(() => {
  getIpList();
  getUserList();
});
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
    >
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入名称"
          clearable
          class="!w-[200px]"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="getIpList"
        >
          查询
        </el-button>
      </el-form-item>
    </el-form>

    <div ref="contentRef">
      <PureTableBar title="访问管理-IP" :columns="columns" @refresh="getIpList">
        <template #buttons>
          <el-button
            type="primary"
            :icon="useRenderIcon(AddFill)"
            @click="openDialog()"
          >
            新增
          </el-button>
          <el-popconfirm
            :title="`确认删除多个IP地址?`"
            @confirm="() => handleDelete(multipleIds)"
          >
            <template #reference>
              <el-button
                type="danger"
                :icon="useRenderIcon(Delete)"
                :disabled="!(multipleIds.length > 0)"
              >
                批量删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
        <template v-slot="{ size, dynamicColumns }">
          <pure-table
            ref="tableRef"
            align-whole="center"
            showOverflowTooltip
            table-layout="auto"
            :loading="loading"
            :size="size"
            adaptive
            :adaptiveConfig="{ offsetBottom: 108 }"
            :data="dataList"
            :columns="dynamicColumns"
            :pagination="pagination"
            :paginationSmall="size === 'small' ? true : false"
            :header-cell-style="{
              background: 'var(--el-fill-color-light)',
              color: 'var(--el-text-color-primary)'
            }"
            @selection-change="handleSelectionChange"
            @page-size-change="handleSizeChange"
            @page-current-change="handleCurrentChange"
          >
            <template #operation="{ row }">
              <el-button
                class="reset-margin"
                link
                type="primary"
                :size="size"
                :disabled="row.roleKey === 'admin'"
                :icon="useRenderIcon(EditPen)"
                @click="openDialog('修改', row)"
              >
                修改
              </el-button>
              <el-popconfirm
                :title="`是否确认删除名称为${row.name}的这条数据`"
                @confirm="handleDelete([row?.id])"
              >
                <template #reference>
                  <el-button
                    class="reset-margin"
                    link
                    type="danger"
                    :size="size"
                    :icon="useRenderIcon(Delete)"
                    :disabled="row.roleKey === 'admin'"
                  >
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </pure-table>
        </template>
      </PureTableBar>
    </div>
  </div>
</template>

<style scoped lang="scss">
:deep(.el-dropdown-menu__item i) {
  margin: 0;
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}
</style>
