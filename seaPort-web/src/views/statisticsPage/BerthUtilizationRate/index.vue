<template>
  <div class="page-container flex">
    <div
      class="flex flex-col flex-1 bg-white/95 rounded-2xl shadow-lg px-8 py-6 mb-6 ring-1 ring-slate-200 animate-fade-in"
    >
      <h1
        class="text-2xl md:text-3xl font-bold text-center text-[#6E59A5] mb-2 tracking-tight drop-shadow-sm"
      >{{ transformI18n("imip.page3.obj23") }}
      </h1>
      <p class="text-center text-gray-500 mb-6 text-sm">
        {{ transformI18n("imip.page3.obj24") }}
      </p>
      <div class="flex justify-center mb-4 space-x-2">
        <el-date-picker
          v-model="yearMonth"
          type="month"
          :placeholder='transformI18n("imip.page3.obj25")'
          :default-value="new Date()"
          value-format="YYYY-MM"
          @change="getBerthUsageRateData"
        />
        <el-button
          type="primary"
          :icon="useRenderIcon(Export)"
          @click="() => exportTo('usageRate')"
          >{{ transformI18n("imip.button.obj5") }}</el-button
        >
        <el-button
          type="warning"
          :icon="useRenderIcon(Export)"
          @click="() => exportTo('berthTimeDetail')"
          >{{ transformI18n("imip.page3.obj26") }}</el-button
        >
      </div>
      <div id="berthChart" />
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  exportHarborBerthUsageRate,
  getHarborBerthUsageRate,
  exportHarborBerthTime
} from "@/api/pier";
import { onMounted, onUnmounted, ref, watch } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import { downloadFun } from "@/utils/downloadBlob";
import Export from "@iconify-icons/ri/file-upload-line";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { transformI18n } from "@/plugins/i18n";
const berthData = ref([]);
const yearMonth = ref<string>(new Date().toISOString().slice(0, 7));
let chartInstance: echarts.ECharts | null = null;

const getBerthUsageRateData = async () => {
  try {
    const res: any = await getHarborBerthUsageRate({
      yearMonth: yearMonth.value
    });
    if (res.code === 200) {
      berthData.value = res.data;
      updateChart();
    }
  } catch (error) {
    console.error("查询泊位使用率报错 ", error);
  }
};

const initChart = () => {
  const chartDom = document.getElementById("berthChart");
  if (chartDom) {
    chartInstance = echarts.init(chartDom);
    updateChart();
  }
};

const updateChart = () => {
  if (!chartInstance) return;

  const option = {
    title: {
      text: `${transformI18n("imip.page2.obj23")+yearMonth.value}`,
      left: "center"
    },
    tooltip: {
      trigger: "axis"
    },
    xAxis: {
      type: "category",
      name: transformI18n("imip.page1.obj4"),
      data: berthData.value.map((item: any) => item.berthName),
      axisLabel: {
        rotate: 45, // Rotate labels for better readability
        interval: 0 // Show all labels
      }
    },
    yAxis: {
      type: "value",
      name: transformI18n("imip.page3.obj27"),
      axisLabel: {
        formatter: "{value}%"
      },
      max: 100 // Max utilization rate is 100%
    },
    series: [
      {
        name: transformI18n("imip.page3.obj23"),
        type: "bar",
        data: berthData.value.map((item: any) =>
          parseFloat(item.rate).toFixed(2)
        ),
        itemStyle: {
          color: "#5470C6"
        },
        barMaxWidth: 30 // Limit bar width for better appearance
      }
    ],
    grid: {
      left: "2%",
      right: "4%",
      bottom: "2%",
      containLabel: true
    }
  };

  chartInstance.setOption(option, true);
};
const exportTo = async (type: string = "usageRate") => {
  try {
    const res: any =
      type === "usageRate"
        ? await exportHarborBerthUsageRate({
            yearMonth: yearMonth.value
          })
        : await exportHarborBerthTime({
            yearMonth: yearMonth.value
          });
    // 尝试解析 Blob 内容
    const isJson = res.type.includes("application/json");
    // 尝试解析 Blob 内容
    if (isJson) {
      const text = await res.text();
      try {
        const json = JSON.parse(text);
        if (json.code !== 200) {
          ElMessage({
            message: json.msg,
            type: "error"
          });
          throw new Error(json.msg || "导出失败");
        }
      } catch {
        // 如果解析失败，说明可能是正常的二进制数据
      }
      return;
    }
    const title = type === "usageRate" ? transformI18n("imip.page3.obj23") : transformI18n("imip.page3.obj28");
    downloadFun(res, `${title}_${yearMonth.value}.xlsx`);
    ElMessage({
      message: "success",
      type: "success"
    });
  } catch (error) {
    console.error("导出失败", error);
  }
};

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

onMounted(() => {
  getBerthUsageRateData();
  initChart();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});
</script>

<style lang="less" scoped>
.page-container {
  width: 100%;
  height: 100%;
  padding: 20px 20px 0;
  #berthChart {
    flex: 1;
    min-height: 400px;
  }
}
</style>
