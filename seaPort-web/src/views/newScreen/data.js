import port1 from "@/assets/newScreen/port1.png"
import port2 from "@/assets/newScreen/port2.png"
import port3 from "@/assets/newScreen/port3.png"
import allport from "@/assets/newScreen/allport.png"
// export const showImg =new Map([
//   [101,allport],
//   [103,port2],
//   [221,port1],
//   [222,port3]
// ])
export const showImg = {
  101: allport,
  103: port2,
  221: port1,
  222: port3
}
export const statusMap = {
  0: "空闲",
  1: "占用",
  2: "维护",
  3: "暂停"
};
export const colors = ["#4caf50", "#e78c62ff", "#ff9800", "#9e9e9e"];
export const planDict = [
  {key:1,value:"今日到船"},{key:2,value:"明日到船"},
  {key:3,value:"今日离泊"},{key:4,value:"明日离泊"}
]
export const berth1Coordinate=new Map([
  ["F1",{x:0.041,y:0.249}],
  ["F2",{x:0.045,y:0.357}],
  ["F3",{x:0.05,y:0.4596}],
  ["F4",{x:0.074,y:0.576}],
  ["F5",{x:0.1257,y:0.7}],
  ["F6",{x:0.187,y:0.888}],
  ["F7",{x:0.2526,y:0.334}],
  ["F8",{x:0.2508813160987074,y:0.2644962191622602}],
  ["F9",{x:0.29,y:0.263}],
  ["F10",{x:0.361,y:0.321}],
  ["F11",{x:0.42,y:0.361}],
])
export const berth2Coordinate=new Map([
  ["L1",{x:0.347,y:0.904}],
  ["L2",{x:0.252,y:0.752}],
  ["L3",{x:0.192,y:0.65}],
  ["L4",{x:0.156,y:0.557}],
  ["L5",{x:0.16039952996474735,y:0.48048221472270847}],
  ["L6",{x:0.16803760282021152,y:0.39039179946220065}],
  ["L7",{x:0.17332549941245592,y:0.32571150132645144}],
  ["L8",{x:0.1827262044653349,y:0.2552561765714389}],
  ["L9",{x:0.186,y:0.187}],
])
export const berth3Coordinate=new Map([

])
export const getLoadingLedgerStatusName = (status)=>{
  switch(status){
    case "0":
      return {name:"进行中",color: "#31F54D"}
    case "1":
      return {name:"暂停",color: "#F16E02"}
    case "2":
      return {name:"结束",color: "#0DD3F6"}
  }
}
export const getWindowLogName = (item)=>{
  switch(item){
    case 0:
      return "到港时间-靠泊时间";
    case 1:
      return "靠泊时间-作业时间";
    case 2:
      return "结束时间-离泊时间";
  }
}
export const needCollectFee = (item)=>{
  switch(item){
    case 1:
      return "免滞期费";
    case 2:
      return "非免滞期费";
  }
}
export const stopLogTypeName = (item)=>{
  switch(item){
    case "1":
      return "主观原因";
    case "2":
      return "客观原因";
  }
}
export const tap1 = [
  {key:1,value:"空窗期日志"},
  {key:2,value:"作业量更改日志"}
]
export const tap2 = [
  {key:1,value:"暂停日志"},
  {key:2,value:"慢作业日志"}
]
export const tap3 = [
  {key:1,value:"作业中",color:"#00ff66"},
  {key:2,value:"到港",color:"#00ccff"},
  {key:3,value:"离港",color:"#ff3333"}
]
export const cycleRefulshSecond = 30 //定时周期（s）