<template>
  <IconifyIconOffline
    :icon="isFullscreen ? ExitFullscreen : Fullscreen"
    class="icon"
    @click="toggleFullscreen"
  />
  <div id="echarts" ref="chartInstance" class="chart-container" />
</template>

<script setup>
import * as echarts from "echarts";
import { onMounted, onUnmounted, ref, watch } from "vue";
import { useHarborHook } from "../../utils/hooks";
import { useWebSocketStore } from "@/store/modules/websocketStore";
import ExitFullscreen from "@iconify-icons/ri/fullscreen-exit-fill";
import Fullscreen from "@iconify-icons/ri/fullscreen-fill";
import { isEqual } from "lodash";

const websocketStore = useWebSocketStore();
const { getBerthStatusOptions } = useHarborHook();

const props = defineProps({
  title: { type: String, default: "" },
  containerRef: Object,
  enterFullscreen: Function,
  exitFullscreen: Function,
  isFullscreen: Boolean
});

const chartInstance = ref(null);
let myChart = null;
let lastData = null; // 用于存储上一次的数据，防止重复更新

// 初始化图表
const initChart = () => {
  if (!chartInstance.value) return;
  myChart =
    echarts.getInstanceByDom(chartInstance.value) ||
    echarts.init(chartInstance.value);
  updateChart();
};

// 更新图表
const updateChart = () => {
  if (!myChart) return;

  const option = {
    title: {
      text: props.title,
      left: "40%", // 标题右移
      top: 5,
      textStyle: {
        fontSize: 18,
        color: "#fff",
        fontWeight: "bold"
      }
    },
    tooltip: {
      trigger: "item"
    },
    legend: {
      top: "10%",
      orient: "vertical",
      left: "left",
      textStyle: {
        fontSize: 14,
        color: "#fff"
      }
    },
    series: [
      {
        name: "状态",
        type: "pie",
        radius: ["40%", "70%"], // 缩小饼图
        center: ["60%", "55%"], // 让饼图往下移动
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: "center"
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        },
        labelLine: {
          show: false
        },
        itemStyle: {
          borderRadius: 5
        },
        data: getBerthStatusOptions(websocketStore.screen_dockBerths)
      }
    ]
  };

  myChart.setOption(option);
};

// 监听数据变化，防止重复渲染
watch(
  () => websocketStore.screen_dockBerths,
  newData => {
    if (!isEqual(newData, lastData)) {
      lastData = JSON.parse(JSON.stringify(newData)); // 深拷贝存储
      updateChart();
    }
  },
  { deep: true }
);

// 处理窗口缩放
const resizeHandler = () => {
  myChart?.resize();
};

// 组件挂载 & 事件监听
onMounted(() => {
  initChart();
  window.addEventListener("resize", resizeHandler);
});

// 组件卸载时清理事件 & 释放 ECharts 实例
onUnmounted(() => {
  window.removeEventListener("resize", resizeHandler);
  myChart?.dispose();
  myChart = null;
});

// 切换全屏
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
