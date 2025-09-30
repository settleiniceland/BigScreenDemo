<script setup lang="ts">
import { useColumns } from "./columns";
import { ref } from "vue";
import { transformI18n } from "@/plugins/i18n";
import users from "@/views/userBerth/users.vue"
const {
  columns,
  dataList,
  fetchData,
} = useColumns();
const usersRef = ref();
const openDialog = (berthCode) =>{
  usersRef.value.open(berthCode)
}
</script>

<template>
  <div class="table-card">
    <pure-table
      row-key="id"
      :title="transformI18n('imip.items.item9')"
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
          @click="openDialog(row.berthCode)">
          {{ transformI18n('imip.button.obj1') }}
        </el-button>
      </template>
    </pure-table>
    <users ref="usersRef" @success="fetchData"/>
  </div>
</template>
<style scoped>
</style>
