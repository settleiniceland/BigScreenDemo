import { ref, onMounted, onUnmounted } from "vue";

/**
 * @description 获取本地时间
 */
export const useCurrentTime = () => {
  const year = ref(0); // 年份
  const month = ref(0); // 月份
  const week = ref(""); // 星期几
  const day = ref(0); // 天数
  const hour = ref(0); // 小时
  const minute = ref(0); // 分钟
  const second = ref(0); // 秒
  const nowTime = ref(""); // 当前时间
  let timer = null; // 存储定时器 ID

  // 更新时间
  const updateTime = () => {
    const date = new Date();
    year.value = date.getFullYear();
    month.value = date.getMonth() + 1;
    week.value = "日一二三四五六".charAt(date.getDay());
    day.value = date.getDate();
    hour.value = (date.getHours() + "").padStart(2, "0");
    minute.value = (date.getMinutes() + "").padStart(2, "0");
    second.value = (date.getSeconds() + "").padStart(2, "0");
    nowTime.value = `${year.value}-${month.value}-${day.value} ${hour.value}:${minute.value}:${second.value}`;
  };

  // 组件挂载时启动定时器
  onMounted(() => {
    updateTime(); // 先更新一次时间
    timer = setInterval(updateTime, 1000); // 每秒更新
  });

  // 组件卸载时清除定时器
  onUnmounted(() => {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  });

  return { year, month, day, hour, minute, second, week, nowTime };
};
