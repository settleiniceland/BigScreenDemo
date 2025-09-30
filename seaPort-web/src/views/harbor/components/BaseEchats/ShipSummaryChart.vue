<template>
  <div class="summary-chart-wrapper">
    <div ref="combinedChart" class="summary-chart" />
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch, onUnmounted } from "vue";
import * as echarts from "echarts";
import { useWebSocketStore } from "@/store/modules/websocketStore";
const props = defineProps<{
  containerRef: any;
  enterFullscreen: () => void;
  exitFullscreen: () => void;
  isFullscreen: boolean;
}>();

const combinedChart = ref<HTMLDivElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
const websocketStore = useWebSocketStore();

const renderChart = (
  dom: HTMLDivElement,
  todayData: { materialName: string; count: number }[],
  tomorrowData: { materialName: string; count: number }[]
) => {
  const instance = echarts.init(dom);

  const allMaterials = Array.from(
    new Set([
      ...todayData.map(m => m.materialName),
      ...tomorrowData.map(m => m.materialName)
    ])
  );

  const option: echarts.EChartsOption = {
    title: {
      left: "center",
      textStyle: {
        color: "#fff",
        fontSize: 16
      }
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow"
      }
    },
    legend: {
      data: ["今日到船", "明日到船"],
      textStyle: {
        color: "#fff"
      },
      top: "5%"
    },
    grid: {
      left: "2%", // Adjusted for horizontal bars
      right: "2%",
      top: "15%",
      bottom: "5%", // Make room for x-axis labels
      containLabel: true // Changed to true since labels are now on bottom
    },
    xAxis: {
      // Now at bottom with categories
      type: "category",
      data: allMaterials,
      axisLabel: {
        color: "#fff",
        interval: 0,
        formatter: (value: string) => {
          const maxLength = 15;
          if (value.length > maxLength) {
            return value
              .match(new RegExp(`.{1,${maxLength}}`, "g"))!
              .join("\n");
          }
          return value;
        },
        rotate: 60 // Optional: rotate labels if they're too long
      },
      axisLine: { lineStyle: { color: "#ccc" } }
    },
    yAxis: {
      // Now shows values
      type: "value",
      minInterval: 1,
      axisLabel: { color: "#fff" },
      axisLine: { lineStyle: { color: "#ccc" } },
      splitLine: { show: true, lineStyle: { color: "#265C79" } }
    },
    series: [
      {
        name: "今日到船",
        type: "bar",
        stack: "total",
        data: allMaterials.map(material => {
          const item = todayData.find(m => m.materialName === material);
          return item ? item.count : 0;
        }),
        itemStyle: {
          color: "#2ecc71"
        },
        barWidth: 20
      },
      {
        name: "明日到船",
        type: "bar",
        stack: "total",
        data: allMaterials.map(material => {
          const item = tomorrowData.find(m => m.materialName === material);
          return item ? item.count : 0;
        }),
        itemStyle: {
          color: "#e67e22"
        },
        barWidth: 20
      }
    ]
  };

  instance.setOption(option);
  return instance;
};

const updateChart = async () => {
  await nextTick();
  const todayData =
    websocketStore.screenShipArrivalListData.find(d => d.key === "today")
      ?.childrenMaterialVo || [];
  const tomorrowData =
    websocketStore.screenShipArrivalListData.find(d => d.key === "tomorrow")
      ?.childrenMaterialVo || [];

  if (combinedChart.value) {
    if (!chartInstance) {
      chartInstance = renderChart(combinedChart.value, todayData, tomorrowData);
    } else {
      const allMaterials = Array.from(
        new Set([
          ...todayData.map(m => m.materialName),
          ...tomorrowData.map(m => m.materialName)
        ])
      );
      chartInstance.setOption({
        yAxis: { data: allMaterials },
        series: [
          {
            data: allMaterials.map(material => {
              const item = todayData.find(m => m.materialName === material);
              return item ? item.count : 0;
            })
          },
          {
            data: allMaterials.map(material => {
              const item = tomorrowData.find(m => m.materialName === material);
              return item ? item.count : 0;
            })
          }
        ]
      });
    }
  }
};

let resizeObserver: ResizeObserver | null = null;

const setupResizeObserver = () => {
  if (!combinedChart.value) return;

  resizeObserver = new ResizeObserver(() => {
    if (chartInstance) {
      chartInstance.resize();
    }
  });

  resizeObserver.observe(combinedChart.value);
};

onMounted(() => {
  if (websocketStore.screenShipArrivalListData.length) {
    updateChart();
  }

  nextTick(() => {
    setupResizeObserver();
  });
});

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }

  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
});

watch(
  [() => websocketStore.screenShipArrivalListData, () => props.isFullscreen],
  ([newData, isFullscreen]) => {
    updateChart();
    nextTick(() => {
      if (chartInstance) {
        chartInstance.resize();
      }
    });
  },
  { deep: true }
);
</script>

<style scoped>
.summary-chart-wrapper {
  width: 100%; /* Full width of parent container */
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-chart {
  width: 100%; /* Full width */
  height: 100%;
  min-width: 0;
}
</style>
