<script lang="ts" setup>
import { onMounted, ref } from "vue";
import {
  harborDetailList,
  updateRecordStatus
} from "@/api/plan/unloadingOrder";
import EditPen from "@iconify-icons/ep/edit-pen";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { useUnloadWork } from "../../utils/unloadWork";
import { ElMessage } from "element-plus";
const { updateUnloadLogDialog } = useUnloadWork();
const { duId, isOperation } = defineProps<{
  duId: string;
  isOperation: boolean;
}>();

const tableDataExpand = ref(null);
const loading = ref(false);

// 发送请求的方法，确保骨架屏至少展示 1 秒
const fetchData = async (duId: string) => {
  loading.value = true;
  const startTime = Date.now(); // 记录开始时间

  const { data } = (await harborDetailList({ duId })) as any;
  tableDataExpand.value = data;

  // 计算请求时间，确保至少0.1 秒后再关闭 loading
  const elapsedTime = Date.now() - startTime;
  const delay = Math.max(100 - elapsedTime, 0); // 计算还需要等待的时间

  setTimeout(() => {
    loading.value = false;
  }, delay);
};

// const handleDelete = (dudId: string) => {};
onMounted(() => {
  fetchData(duId);
});

const columns = [
  { label: "开始时间", prop: "startTime" },
  { label: "结束时间", prop: "endTime" },
  { label: "暂停间隔", prop: "pauseInterval" },
  {
    label: "暂停原因",
    prop: "reason"
  },
  {
    label: "是否记录",
    prop: "recordStatus",
    slot: "recordStatus"
  },
  {
    label: "操作",
    slot: "operation",
    fixed: "right"
  }
];
const handleRecordStatus = async (value, dudId: string) => {
  const data = {
    dudId,
    recordStatus: value
  };
  const res: any = await updateRecordStatus(data);
  if (res.code === 200) {
    ElMessage({
      message: res.msg,
      type: "success"
    });
  }
};
</script>

<template>
  <div class="table-container backdrop-blur-md">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item
          v-for="i in 4"
          :key="i"
          variant="text"
          style="height: 20px; margin-bottom: 10px"
        />
      </template>

      <template #default>
        <pure-table
          v-if="tableDataExpand && tableDataExpand.length > 0"
          :data="tableDataExpand"
          :columns="columns"
          class="bg-gray-100"
          :border="false"
          style="height: auto"
          :loading="loading"
        >
          <template #operation="{ row }">
            <el-button
              class="reset-margin"
              type="primary"
              :disabled="!isOperation"
              :icon="useRenderIcon(EditPen)"
              link
              @click="updateUnloadLogDialog(row, () => fetchData(duId))"
            >
              修改
            </el-button>
          </template>
          <template #recordStatus="{ row }">
            <el-switch
              v-model="row.recordStatus"
              active-value="1"
              size="small"
              inactive-value="0"
              @change="value => handleRecordStatus(value, row?.dudId)"
            />
          </template>
        </pure-table>
        <p v-else class="text-gray-500 text-center py-4">暂无数据</p>
      </template>
    </el-skeleton>
  </div>
</template>

<style scoped>
.table-container {
  max-height: 200px;
  overflow: auto;
  padding: 10px;
  width: 70%;
}
</style>
