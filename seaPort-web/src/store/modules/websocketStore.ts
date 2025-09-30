import { defineStore } from "pinia";
import {ref,shallowRef} from "vue";
import { message } from "ant-design-vue";
import { useUserStore } from "./user";

export const useWebSocketStore = defineStore("websocket", () => {
  const ws = ref(null);
  const messageData = shallowRef([]); // 使用 shallowRef 避免过度响应
  // const screenBerthListData = shallowRef([]);
  // const screenDockListData = shallowRef([]);
  // const screenShipArrivalListData = shallowRef([]);
  // const screenThroughputData: any = shallowRef({});
  // const screenPlanStatusListData = shallowRef([]);
  // const screenCountMapData = shallowRef([]);
  // const screenBerthStatusListData = shallowRef([]);
  // const screenBargeListData = shallowRef([]);
  // const screenBigPeriListData = shallowRef([]);
  // const screenPierList = shallowRef([]);
  // const screenGeoJsonVoList = shallowRef([]);
  const userStore = useUserStore();
  /*👉👉👉👉👉👉👉👉👇👈👈👈👈👈👈👈👈*/
  const screen_depts = ref([])
  const screen_dockBerths = ref([])
  const screen_dockPiers = ref([])
  const screen_dockPlansForMap = ref([])
  const screen_dockPlans1 = ref([])
  const screen_dockPlans2 = ref([])
  const screen_dockPlans3 = ref([])
  const screen_monthThroughPut = ref()
  const screen_yearThroughPut = ref()
  const screen_yesterdayThrouhPut = ref()
  const screen_taskLogs = ref([])
  /*👉👉👉👉👉👉👉👉👆👈👈👈👈👈👈👈👈*/
  const initWebSocket = (deptId?: number) => {
    ws.value = new WebSocket(
      `${import.meta.env.VITE_WS_API}/${userStore.userId}`
    );
    console.info("开始连接WebSocket", import.meta.env.VITE_WS_API);
    ws.value.onopen = () => {
      console.info("WebSocket已连接" + import.meta.env.VITE_WS_API);
    };
    ws.value.onmessage = event => {
      try {
        const data = JSON.parse(event.data);
        console.info("WebSocket接收数据",data);
        if (data.type == "error") {
          message.error(data.message ?? "WebSocket 错误");
          return;
        }
        const {
          // screenBerthList,
          // screenDockList,
          // screenPlanStatusList,
          // screenBerthStatusList,
          // screenBargeList,
          // screenBigPeriList,
          // screenShipArrivalList,
          // screenThroughput,
          screenPierVoList,
          /*👉👉👉👉👉👉👉👉👇👈👈👈👈👈👈👈👈*/
          depts,
          dockBerths,
          dockPiers,
          dockPlans1,
          dockPlans2,
          dockPlans3,
          monthThroughPut,
          yearThroughPut,
          yesterdayThrouhPut,
          taskLogs,
          dockPlansForMap
          /*👉👉👉👉👉👉👉👉👆👈👈👈👈👈👈👈👈*/
        } = data;
        // screenBerthListData.value = screenBerthList;
        // screenDockListData.value = screenDockList;
        // screenPlanStatusListData.value = screenPlanStatusList;
        // screenBerthStatusListData.value = screenBerthStatusList;
        // screenBargeListData.value = screenBargeList;
        // screenBigPeriListData.value = screenBigPeriList;
        // screenShipArrivalListData.value = screenShipArrivalList;
        // screenThroughputData.value = screenThroughput;
        // screenPierList.value = screenPierVoList;
        /*👉👉👉👉👉👉👉👉👇👈👈👈👈👈👈👈👈*/
        screen_depts.value = depts;
        screen_dockBerths.value = dockBerths;
        screen_dockPiers.value = dockPiers;
        screen_dockPlans1.value = dockPlans1;
        screen_dockPlans2.value = dockPlans2;
        screen_dockPlans3.value = dockPlans3;
        screen_monthThroughPut.value = monthThroughPut;
        screen_yearThroughPut.value = yearThroughPut;
        screen_yesterdayThrouhPut.value = yesterdayThrouhPut;
        screen_taskLogs.value = taskLogs;
        screen_dockPlansForMap.value = dockPlansForMap;
        /*👉👉👉👉👉👉👉👉👆👈👈👈👈👈👈👈👈*/
        console.log("部门数据更新完成：screen_depts", screen_depts.value);
        console.log("泊位数据更新完成：screen_dockBerths", screen_dockBerths.value);
        console.log("码头数据更新完成：screen_dockPiers", screen_dockPiers.value);
        console.log("一号船计划更新完成：screen_dockPlans1", screen_dockPlans1.value);
        console.log("二号船计划更新完成：screen_dockPlans2", screen_dockPlans2.value);
        console.log("三号船计划更新完成：screen_dockPlans3", screen_dockPlans3.value);
        console.log("月吞吐量数据更新完成：screen_monthThroughPut", screen_monthThroughPut.value);
        console.log("年吞吐量数据更新完成：screen_yearThroughPut", screen_yearThroughPut.value);
        console.log("昨日吞吐量数据更新完成：screen_yesterdayThrouhPut", screen_yesterdayThrouhPut.value);
        console.log("皮带秤数采数据：screen_taskLogs",screen_taskLogs.value)
        console.log("新增数据为screen_dockPlansForMap",screen_dockPlansForMap.value)
        // console.log("数据更新完成：screenBerthListData", screenBerthListData.value);
        // console.log("数据更新完成：screenDockListData", screenDockListData.value);
        // console.log("数据更新完成：screenPlanStatusListData", screenPlanStatusListData.value);
        // console.log("数据更新完成：screenBerthStatusListData", screenBerthStatusListData.value);
        // console.log("数据更新完成：screenBargeListData", screenBargeListData.value);
        // console.log("数据更新完成：screenBigPeriListData", screenBigPeriListData.value);
        // console.log("数据更新完成：screenShipArrivalListData", screenShipArrivalListData.value);
        // console.log("数据更新完成：screenThroughputData", screenThroughputData.value);
        // console.log("数据更新完成：screenPierList", screenPierList.value);
      } catch (error) {
        console.warn("非 JSON 数据：", error, event.data);
      }
    };
    ws.value.onclose = () => {
      console.log("WebSocket 已关闭");
    };

    ws.value.onerror = error => {
      console.error("WebSocket 错误:", error);
      message.error(error.message ?? "WebSocket 错误");
    };
  };
  const sendMessage = (msg, { onSuccess, onError }: any) => {
    msg.userId = userStore.userId;
    const handleMessage = event => {
      try {
        const data = JSON.parse(event.data);
        ws.value.removeEventListener("message", handleMessage); // 解除监听
        if (data.type === "error") {
          onError?.(new Error(data.message ?? "WebSocket 处理失败"));
        } else {
          onSuccess?.(data);
        }
      } catch (error) {
        onError?.(error);
      }
    };

    ws.value.addEventListener("message", handleMessage);
    ws.value.send(JSON.stringify(msg));
  };

  const closeWebSocket = () => {
    if (ws.value) {
      ws.value.close();
      ws.value = null;
    }
  };
  return {
    messageData,
    initWebSocket,
    closeWebSocket,
    sendMessage,
    // screenGeoJsonVoList,
    // screenBerthListData,
    // screenDockListData,
    // screenPlanStatusListData,
    // screenCountMapData,
    // screenBerthStatusListData,
    // screenBargeListData,
    // screenBigPeriListData,
    // screenShipArrivalListData,
    // screenThroughputData,
    // screenPierList,
    /*👉👉👉👉👉👉👉👉👇👈👈👈👈👈👈👈👈*/
    screen_depts,
    screen_dockBerths,
    screen_dockPiers,
    screen_dockPlans1,
    screen_dockPlans2,
    screen_dockPlans3,
    screen_monthThroughPut,
    screen_yearThroughPut,
    screen_yesterdayThrouhPut,
    screen_taskLogs,
    screen_dockPlansForMap,
    /*👉👉👉👉👉👉👉👉👆👈👈👈👈👈👈👈👈*/
    ws // 暴露 ws 实例
  };
});
