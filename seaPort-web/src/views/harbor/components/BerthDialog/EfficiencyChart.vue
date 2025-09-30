<template>
  <div class="chart-wrapper" :style="{ height }">
    <div ref="chartRef" class="chart-container" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from "vue";
import * as echarts from "echarts";

interface EfficiencyData {
  rate: string;
  time: string;
  unloadWeight: string;
  unloadNum: string | null;
  exceptionReason: string | null;
  exceptionStatus: string | null;
}

const RED_COLOR = "#F56C6C";

// 统一样式定义
const TOOLTIP_STYLES = {
  container:
    "padding: 4px; font-size: 13px; max-width: 180px; white-space: normal; word-break: break-all;",
  reason:
    "display: inline-block; white-space: normal; word-break: break-all; font-weight: bold;"
};

const { efficiencyData, height = "160px" } = defineProps<{
  efficiencyData: EfficiencyData[] | null;
  height: string;
}>();

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
const pendingRenderData = ref<EfficiencyData[] | null>(null);

// 提取 tooltip 格式化逻辑
const formatTooltip = (item: EfficiencyData, redColor: string) => `
  <div style="${TOOLTIP_STYLES.container}">
    <strong>时间: ${item.time}:00</strong><br/>
    卸率: ${item.rate}(T|PCS)/H<br/>
    作业量: <span style="font-weight: bold; color: ${item.exceptionStatus === "2" ? redColor : "#fff"}">${item.unloadWeight}T</span><br/>
    ${
      item.exceptionStatus === "2"
        ? `<div style="${TOOLTIP_STYLES.reason}">
             偏低原因: <span style="color: ${redColor}">${item.exceptionReason ?? "未填写"}</span>
           </div>`
        : ""
    }
  </div>
`;

// 初始化图表或更新数据
const renderChart = async (data: EfficiencyData[]) => {
  if (!chartRef.value || chartRef.value.offsetWidth === 0) {
    pendingRenderData.value = data;
    return;
  }

  pendingRenderData.value = null;
  await nextTick();

  if (!chartInstance) {
    chartInstance =
      echarts.getInstanceByDom(chartRef.value) || echarts.init(chartRef.value);
    window.addEventListener("resize", resizeChart);
  }

  const times = data.map(item => item.time);
  const seriesData = data.map(item => ({
    value: item.rate,
    itemStyle: {
      color: item.exceptionStatus === "2" ? RED_COLOR : "#e2df3d"
    }
  }));

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: "axis",
      confine: true,
      formatter: (params: any) => {
        const item = data[params[0].dataIndex];
        return formatTooltip(item, RED_COLOR);
      },
      backgroundColor: "rgba(0, 95, 153, 0.8)",
      borderColor: "#fff",
      textStyle: { color: "#fff" }
    },
    grid: {
      left: "15%",
      right: "5%",
      top: "5%",
      bottom: "13%"
    },
    xAxis: {
      type: "category",
      data: times,
      nameTextStyle: { color: "#fff" },
      axisLabel: { color: "#fff" },
      axisLine: { lineStyle: { color: "#fff" } }
    },
    yAxis: {
      type: "value",
      nameTextStyle: { color: "#fff" },
      axisLabel: { color: "#fff" },
      splitLine: { lineStyle: { color: "rgba(255, 255, 255, 0.2)" } }
    },
    series: [
      {
        type: "bar",
        data: seriesData,
        barWidth: "40%",
        itemStyle: {
          borderRadius: [16, 16, 0, 0]
        }
      }
    ]
  };

  chartInstance.setOption(option);
};

// 自动 resize
const resizeChart = () => {
  setTimeout(() => {
    if (chartRef.value?.offsetWidth) {
      chartInstance?.resize();
    }
  }, 100);
};

// 销毁图表
const cleanupChart = () => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
  window.removeEventListener("resize", resizeChart);
};

// 监听数据变化
watch(
  () => efficiencyData?.map(d => JSON.stringify(d)).join(""),
  async () => {
    if (efficiencyData?.length > 0) {
      await renderChart(efficiencyData);
    }
  },
  { immediate: true }
);

// 挂载后监听图表容器尺寸
onMounted(() => {
  const observer = new ResizeObserver(() => {
    if (chartRef.value?.offsetWidth && pendingRenderData.value) {
      renderChart(pendingRenderData.value);
    }
  });
  if (chartRef.value) observer.observe(chartRef.value);
});

// 卸载时清理资源
onUnmounted(cleanupChart);
</script>

<style scoped lang="less">
@text-color: #fff;

.chart-wrapper {
  margin-top: 10px;
  height: 100%;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>
