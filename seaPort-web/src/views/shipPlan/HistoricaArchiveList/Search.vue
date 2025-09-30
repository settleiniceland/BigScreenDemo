<script setup lang="ts">
import { inject, onMounted, ref,computed } from "vue";
import "plus-pro-components/es/components/search/style/css";
import { type PlusColumn, PlusSearch } from "plus-pro-components";
import { transformI18n } from "@/plugins/i18n";
import { PlanType } from "@/contans";
// import { PlanListParams } from "../utils/type";
import { useShipPlan } from "../utils/hooks";
import { getHarborPierList } from "@/api/pier";
// import { usePageSessionStorage } from "@/views/hooks/usePageSessionStorage";
// 使用 inject 代替 props
const { fetchMaterialName, getDictOptions } = useShipPlan();
// const { isUpdatePageSessionStorage } = usePageSessionStorage();
const { loading, getBerthList } = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  stausOptions: {
    type: Array,
    default: () => []
  },
  getBerthList: {
    type: Function,
    default: () => []
  }
});
const searchParams: any = inject("searchParams");
const getPlanData: () => void = inject("getPlanData");
// const state: any = ref({});
const columns = computed(() => [
  {
    label: transformI18n("imip.page2.obj25"),
    prop: "archivedMonth",
    valueType: "date-picker",
    fieldProps: {
      type: "month",
      valueFormat: "YYYY-MM"
    }
  },
  {
    label: transformI18n("imip.items.item5"),
    width: 120,
    prop: "remark01",
    valueType: "select",
    options: [
      {label:'装船 Memuat kapal',value:'装船 Memuat kapal'},
      {label:'卸船 Membongkar kapal',value:'卸船 Membongkar kapal'}]
  },
  {
    label: transformI18n("imip.page1.obj1"),
    prop: "shipName"
  },
  {
    label: transformI18n("imip.page1.obj2"),
    prop: "status",
    valueType: "select",
    options: async () => {
      return await getDictOptions("plan_status");
    }
  },
  {
    label: transformI18n("imip.page1.obj3"),
    prop: "materialName",
    valueType: "select",
    fieldProps: {
      filterable: true
    },
    filterable: true, // 允许搜索

    //开启搜索
    options: () => fetchMaterialName()
  },
  {
    label: transformI18n("imip.page1.obj4"),
    prop: "hbId",
    valueType: "select",
    fieldProps: {
      filterable: true
    },
    options: getBerthList()
  },

  {
    label: transformI18n("imip.page1.obj5"),
    prop: "planType",
    valueType: "select",
    options: PlanType,
    fieldProps: {
      placeholder: transformI18n("imip.page2.obj26")
    }
  },
  {
    label: transformI18n("imip.page1.obj6"),
    prop: "pierId",
    valueType: "select",
    options: async () => {
      const { data } = (await getHarborPierList()) as any;
      return data.map(item => ({ label: item.pierName, value: item.pierId }));
    }
  },
  {
    label: transformI18n("imip.page1.obj7"),
    prop: "endTime",
    valueType: "date-picker",
    fieldProps: {
      type: "datetimerange",
      startPlaceholder: "start",
      endPlaceholder: "end",
      valueFormat: "YYYY-MM-DD HH:mm:ss"
    }
  },

  {
    label: transformI18n("imip.page1.obj12"),
    prop: "usageUnit"
  }
]);

const handleSearch = values => {
  Object.entries(values).forEach(([key, value]) => {
    searchParams.value[key] = value;
  });
  getPlanData();
};
const handleRest = () => {
  searchParams.value = {
    pageSize: 20
  };
  getPlanData();
};
const initValue = () => {
  const now = new Date();
  const month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
  searchParams.value.archivedMonth = month;
};
onMounted(() => {
  initValue();
});
</script>

<template>
  <PlusSearch
    v-model="searchParams"
    class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] pb-[12px] overflow-auto"
    :columns="columns"
    :show-number="8"
    label-width="90"
    label-position="right"
    :colProps="{
      span: 5
    }"
    :search-loading="loading"
    @search="handleSearch"
    @reset="handleRest"
  />
</template>
