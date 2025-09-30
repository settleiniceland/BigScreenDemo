<template>
  <div class="summaryView">
    <div class="stats-section">
      <div class="stat-card year-stat-card">
        <h3>年度累计吞吐量</h3>
        <p>{{ websocketStore.screen_yearThroughPut }}<span>T</span></p>
      </div>
    </div>
    <div class="stats-section">
      <div v-for="item in summaryOptions" :key="item.label" class="stat-card">
        <h3>{{ item.label }}</h3>
        <p>{{ item.value ?? 0 }}<span>T</span></p>
      </div>
    </div>
    <div class="shipSummary">
      <el-scrollbar>
        <div class="ship-details">
          <div
            v-for="day in dayDict"
            :key="day.id"
            class="day-section"
          >
            <h4>
              {{ day.lebel }}
              <span
                class="count-badge"
                :class="day.class"
              >{{ getList(day.id).length }}</span>
            </h4>
            <div class="material-list">
              <div
                v-for="item in getList(day.id)"
                class="material-item"
                :title="showDataTitle(item,day.id)">
                <span class="material-name">{{ item.hbName }}</span><br>
                <span 
                  v-for="(name, idx) in item.materialName.split(' | ')" 
                  :key="idx" 
                  class="material-name"
                >
                  {{ name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-scrollbar>
      <div class="log-box">
        <div v-for="(log, index) in taskLogs" :key="index" class="log-line">
          ☆→{{ log }}
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useWebSocketStore } from "@/store/modules/websocketStore";
import { computed } from "vue";
const websocketStore = useWebSocketStore();
const summaryOptions = computed(() => {
  return [
    {
      label: "昨日吞吐量",
      value: websocketStore.screen_yesterdayThrouhPut
    },
    {
      label: "本月累计吞吐量",
      value: websocketStore.screen_monthThroughPut
    }
  ];
});
const formatDate = (dateStr) => {
  return new Date(dateStr.replace(/-/g, '/')).getTime()
}
const today = new Date()
const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 0, 0, 0)
const todayEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59)
const tomorrow = new Date(today)
tomorrow.setDate(today.getDate() + 1)
const tomorrowStart = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate(), 0, 0, 0)
const tomorrowEnd = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate(), 23, 59, 59)
const dayDict = [
  {id:1,lebel:'今日到船',class:"today-count"},
  {id:2,lebel:'明日到船',class:"tomorrow-count"},
  {id:3,lebel:'今日离泊',class:"today-count"},
  {id:4,lebel:'明日离泊',class:"tomorrow-count"},
]
const todayArrive = computed(()=>{
  return websocketStore.screen_dockPlans1.filter((item)=>{
    if(!item.arrivalTime){
      return false;
    }else{
      return formatDate(item.arrivalTime)>=todayStart.getTime()&&formatDate(item.arrivalTime)<=todayEnd.getTime()
    }
  })
})
const todayLeave = computed(()=>{
  return websocketStore.screen_dockPlans1.filter((item)=>{
    if(!item.outBerthTime){
      return false;
    }else{
      return formatDate(item.outBerthTime)>=todayStart.getTime()&&formatDate(item.outBerthTime)<=todayEnd.getTime()
    }
  })
})
const tomorrowArrive = computed(()=>{
  return websocketStore.screen_dockPlans1.filter((item)=>{
    if(!item.arrivalTime){
      return false;
    }else{
      return formatDate(item.arrivalTime)>=tomorrowStart.getTime()&&formatDate(item.arrivalTime)<=tomorrowEnd.getTime()
    }
  })
})
const tomorrowLeave = computed(()=>{
  return websocketStore.screen_dockPlans1.filter((item)=>{
    if(!item.outBerthTime){
      return false;
    }else{
      return formatDate(item.outBerthTime)>=tomorrowStart.getTime()&&formatDate(item.outBerthTime)<=tomorrowEnd.getTime()
    }
  })
})
const taskLogs = computed(()=>{
  return websocketStore.screen_taskLogs;
})
const getList = (id) => {
  switch(id){
    case 1:
      return todayArrive.value;
    case 2:
      return tomorrowArrive.value;
    case 3:
      return todayLeave.value;
    case 4:
      return tomorrowLeave.value;
    default:
      return [];
  }
}
const showDataTitle = (item,id)=>{
  if(id>2){
    return item.shipName+"："+item.outBerthTime+"离泊"
  }else{
    return item.shipName+"："+item.arrivalTime+"到港"
  }
}
</script>
<style scoped lang="less">
.summaryView {
  flex: 1;
  padding: 6px;
  display: flex;
  flex-direction: column;
  .stats-section {
    display: flex;
    justify-content: space-around;
    align-items: center;
    padding: 5px 0;

    .stat-card {
      background-color: rgba(#003d66, 0.8);
      padding: 10px;
      border-radius: 8px;
      text-align: center;
      color: #fff;
      width: 49%;
      border: 1px solid rgba(255, 255, 255, 0.1);
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.25);
        background-color: rgba(#005f99, 0.9);
      }

      h3 {
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: 500;
        color: rgba(255, 255, 255, 0.9);
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      p {
        margin: 0;
        font-size: 22px;
        font-weight: 700;
        color: #ffffff;
        line-height: 1.2;

        span {
          font-size: 14px;
          font-weight: 400;
          opacity: 0.8;
          margin-left: 4px;
        }
      }
    }
    .year-stat-card {
      width: 100%;
      p {
        font-size: 24px;
      }
    }
  }

  .shipSummary {
    flex: 1;
    // height: 47vh;
    display: flex;
    flex-direction: column; /* 让内容上下排列 */
    background-color: rgba(#002b4d, 0.75); // Darker background for ship section
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    .ship-details {
      flex: 1;
      overflow-y: auto;
      scrollbar-width: thin;
      scrollbar-color: transparent transparent;
      height: 27vh;
      width: 100%;
      padding: 12px;
      display: flex;
      flex-direction: row;
      gap: 12px;
      justify-content: space-between;
      .day-section {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-width: 0;
        h4 {
          color: #fff;
          font-size: 12px;
          font-weight: 500;
          margin: 0 0 8px 0;
          padding-bottom: 4px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.25);
          white-space: nowrap;
          display: flex;
          align-items: center;
          gap: 3px;
          .count-badge {
            color: #fff;
            font-size: 12px;
            font-weight: 600;
            padding: 2px 3px;
            border-radius: 12px;
            line-height: 1.5;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
          }
          .tomorrow-count {
            background-color: #e67e22; // Orange for tomorrow
          }
          .today-count {
            background-color: #2ecc71; // Green for today
          }
        }
        .material-list {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 6px;
          .material-item {
            justify-content: space-between;
            padding: 8px 10px;
            background-color: rgba(255, 255, 255, 0.08);
            border-radius: 4px;
            transition: all 0.2s ease;
            &:hover {
              background-color: rgba(255, 255, 255, 0.12);
              transform: translateX(1px);
            }
            .material-name {
              color: #fff;
              font-size: 11px;
              font-weight: 200;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
      }
    }
    .log-box {
      width: 100%;
      max-height: 10vh;
      overflow-y: auto;
      background: rgba(0, 0, 0, 0.3);
      color: #00ffea;
      font-family: monospace;
      padding: 3px;
      border-radius: 16px;
      scrollbar-width: none;
      -ms-overflow-style: none;
      .log-box::-webkit-scrollbar {
        display: none;
      }
      .log-line {
        line-height: 1.6;
        white-space: pre-wrap; /* 避免长日志被压成一行 */
      }
    }
  }
}

</style>