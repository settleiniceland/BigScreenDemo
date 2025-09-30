<template>
  <IconifyIconOffline
    :icon="isFullscreen ? ExitFullscreen : Fullscreen"
    class="icon"
    @click="toggleFullscreen"
  />
  <div id="echarts" ref="chartInstance" class="chart-container" />
  <PlanStatusDialog ref="dialog" :data="clickedData" />
</template>

<script setup>
import * as echarts from "echarts";
import { onMounted, onUnmounted, ref, watch, computed } from "vue";
import { useWebSocketStore } from "@/store/modules/websocketStore";
import { Colors } from "@/contans";
import ExitFullscreen from "@iconify-icons/ri/fullscreen-exit-fill.js";
import Fullscreen from "@iconify-icons/ri/fullscreen-fill.js";
import { isEqual } from "lodash";
import PlanStatusDialog from "./PlanStatusDialog.vue"; // 引入自定义弹窗组件

const websocketStore = useWebSocketStore();
const props = defineProps({
  xAxisData: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: "计划单状态分布"
  },
  containerRef: Object,
  enterFullscreen: Function,
  exitFullscreen: Function,
  isFullscreen: Boolean
});

const data = computed(() => {
  return websocketStore.screenPlanStatusListData?.map(item => {
    const statusLabel =
      props.xAxisData.find(opt => opt.value === item.status)?.label ||
      item.status;
    return {
      value: item.count,
      name: statusLabel,
      childrenMaterialVo: item.childrenMaterialVo,
      itemStyle: { color: Colors[item.status] }
    };
  });
});

const chartInstance = ref(null);
const dialog = ref(null); // 弹窗组件的 ref
const clickedData = ref(null); // 存储点击的数据
let myChart = null;
let lastData = null;

const getMaxValue = data => {
  const max = Math.max(...data.map(item => item.value));
  if (max < 10) return 10;
  const magnitude = Math.pow(10, Math.floor(Math.log10(max)));
  return Math.ceil(max / magnitude) * magnitude;
};

const resizeHandler = () => {
  myChart?.resize();
};

const openDialog = clickedDataValue => {
  clickedData.value = clickedDataValue;
  dialog.value.show(); // 调用弹窗组件的 show 方法
};
const handleZrClick = params => {
  if (!myChart) return;

  const pointInPixel = [params.offsetX, params.offsetY];

  // ✅ 1. 限制在 grid 区域内点击才触发
  const gridComponent = myChart.getModel().getComponent("grid", 0);
  const gridRect = gridComponent.coordinateSystem.getRect();

  if (
    params.offsetX < gridRect.x ||
    params.offsetX > gridRect.x + gridRect.width ||
    params.offsetY < gridRect.y ||
    params.offsetY > gridRect.y + gridRect.height
  ) {
    return; // 不在 grid 区域内，直接返回
  }

  // ✅ 2. 把像素坐标转为 x 轴索引
  const pointInGrid = myChart.convertFromPixel({ gridIndex: 0 }, pointInPixel);
  if (!pointInGrid || pointInGrid[0] == null) return;

  const xIndex = Math.round(pointInGrid[0]);
  const xAxisData = props.xAxisData;
  if (xIndex < 0 || xIndex >= xAxisData.length) return;

  const label = xAxisData[xIndex]?.label;

  const matchedData = (data.value ?? []).find(d => d.name === label);

  const clickData = matchedData || {
    name: label,
    value: 0,
    childrenMaterialVo: [],
    itemStyle: { color: "#999" }
  };

  // ✅ 3. 有子项再弹窗
  if (matchedData.childrenMaterialVo?.length) {
    openDialog(clickData);
  }
};

const initChart = () => {
  if (!chartInstance.value) return;
  myChart =
    echarts.getInstanceByDom(chartInstance.value) ||
    echarts.init(chartInstance.value);
  updateChart();
  myChart?.getZr().on("click", handleZrClick);
};

const updateChart = () => {
  if (!myChart) return;

  const option = {
    title: {
      text: props.title,
      left: "center",
      top: 5,
      textStyle: { fontSize: 18, color: "#fff", fontWeight: "bold" }
    },
    tooltip: { trigger: "axis" },
    legend: {
      data: ["状态"],
      bottom: "2%",
      textStyle: { fontSize: 14, color: "#fff" }
    },
    xAxis: {
      type: "category",
      data: props.xAxisData.map(item => item.label),
      axisLabel: { color: "#fff", interval: 0, rotate: 30 }
    },
    yAxis: {
      type: "value",
      max: getMaxValue(data.value ?? []),
      minInterval: 1,
      axisLabel: { color: "#fff" },
      splitLine: { show: true, lineStyle: { color: "#265C79" } }
    },
    grid: {
      left: "5%",
      right: "5%",
      top: "20%",
      bottom: "0%",
      containLabel: true
    },
    series: [
      {
        name: "数量",
        type: "bar",
        data: data.value,
        barWidth: "50%"
      }
    ]
  };

  myChart.setOption(option);
};

onMounted(() => {
  initChart();
  window.addEventListener("resize", resizeHandler);
});

onUnmounted(() => {
  window.removeEventListener("resize", resizeHandler);
  myChart?.dispose();
  myChart?.getZr()?.off("click", handleZrClick);

  myChart = null;
});

watch(
  () => websocketStore.screenPlanStatusListData,
  newData => {
    if (!isEqual(newData, lastData)) {
      lastData = JSON.parse(JSON.stringify(newData));
      updateChart();
    }
  },
  { deep: true }
);

const toggleFullscreen = () => {
  if (props.isFullscreen) {
    props.exitFullscreen();
  } else {
    props.enterFullscreen();
  }
  myChart?.resize();
};
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 100%;
  width: 100%;
}
.icon {
  position: absolute;
  top: 8px;
  right: 10px;
  cursor: pointer;
  font-size: 22px;
  color: white;
  z-index: 9999;
}
</style>
