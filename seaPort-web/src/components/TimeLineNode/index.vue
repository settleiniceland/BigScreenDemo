<script setup>
import { markRaw, ref } from "vue";
import { randomGradient } from "@pureadmin/utils";
import { useRenderFlicker } from "@/components/ReFlicker";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Iphone from "@iconify-icons/ep/iphone";
const lastBuildTime = "2025-01-28 11:47:07";
const { data } = defineProps({
  data: {
    type: Array,
    default: () => []
  }
});
const timesLineData = ref([]);

const activities = [
  {
    content: "支持圆点发光",
    timestamp: lastBuildTime,
    icon: markRaw(useRenderFlicker())
  },
  {
    content: "支持方形发光",
    timestamp: lastBuildTime,
    icon: markRaw(useRenderFlicker({ borderRadius: 0, background: "#67C23A" }))
  },
  {
    content: "支持渐变发光",
    timestamp: lastBuildTime,
    icon: markRaw(
      useRenderFlicker({
        background: randomGradient({
          randomizeHue: true
        })
      })
    )
  },
  {
    content: "支持默认颜色",
    timestamp: lastBuildTime
  },
  {
    content: "支持自定义颜色",
    timestamp: lastBuildTime,
    color: "#F56C6C"
  },
  {
    content: "支持自定义图标",
    timestamp: lastBuildTime,
    color: "transparent",
    icon: useRenderIcon(Iphone, {
      color: "#0bbd87"
    })
  }
];
</script>
<template>
  <el-timeline>
    <el-timeline-item
      v-for="(activity, index) in data"
      :key="index"
      :icon="activity.value ? markRaw(useRenderFlicker()) : ''"
      :color="activity.color"
      :timestamp="activity.value"
    >
      {{ activity.label }}
    </el-timeline-item>
  </el-timeline>
</template>
