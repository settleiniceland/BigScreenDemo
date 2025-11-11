package com.iwip.web.controller.websocket;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson2.JSONObject;
import com.iwip.common.core.domain.entity.SysDept;
import com.iwip.common.core.domain.entity.SysUser;
import com.iwip.harbor.domain.*;
import com.iwip.harbor.mapper.*;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IDockPlanService;
import com.iwip.harbor.task.InMemoryTaskLogStore;
import com.iwip.system.mapper.SysDeptMapper;
import com.iwip.system.mapper.SysUserMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{userId}")
public class WebSocketProcess {
    /*
     * 持有每个webSocket对象，以key-value存储到线程安全ConcurrentHashMap，
     */
    private static ConcurrentHashMap<Long, WebSocketProcess> concurrentHashMap = new ConcurrentHashMap<>(12);
    private static final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * 会话对象
     **/
    private Session session;
    private Long id; // 存储当前 WebSocket 实例的 id
    private static DockMaterialMapper dockMaterialMapper;
    private static SysUserMapper sysUserMapper;
    private static SysDeptMapper sysDeptMapper;
    private static DockPierMapper dockPierMapper;
    private static DockBerthMapper dockBerthMapper;
    private static DockPlanMapper dockPlanMapper;
    private static DockPlanAssistantMapper dockPlanAssistantMapper;
    private static DockPlanUnloadWeightUpdateLogsMapper dockPlanUnloadWeightUpdateLogsMapper;
    private static DockWindowPeriodMapper dockWindowPeriodMapper;
    private static DockUnloadWorkMapper dockUnloadWorkMapper;
    private static DockUnloadDetailMapper dockUnloadDetailMapper;
    private static DockSlowDownWorkLogsMapper dockSlowDownWorkLogsMapper;
    // 内存记录
    @Autowired
    public void setDockMaterialMapper(DockMaterialMapper dockMaterialMapper){
        WebSocketProcess.dockMaterialMapper = dockMaterialMapper;
    }
    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        WebSocketProcess.sysUserMapper = sysUserMapper;
    }
    @Autowired
    public void setSysDeptMapper(SysDeptMapper sysDeptMapper) {
        WebSocketProcess.sysDeptMapper = sysDeptMapper;
    }
    @Autowired
    public void setDockPierMapper(DockPierMapper dockPierMapper) {
        WebSocketProcess.dockPierMapper = dockPierMapper;
    }
    @Autowired
    public void setDockBerthMapper(DockBerthMapper dockBerthMapper) {
        WebSocketProcess.dockBerthMapper = dockBerthMapper;
    }
    @Autowired
    public void setDockPlanMapper(DockPlanMapper dockPlanMapper) {
        WebSocketProcess.dockPlanMapper = dockPlanMapper;
    }
    @Autowired
    public void setDockPlanAssistantMapper(DockPlanAssistantMapper dockPlanAssistantMapper) {
        WebSocketProcess.dockPlanAssistantMapper = dockPlanAssistantMapper;
    }
    @Autowired
    public void setDockPlanUnloadWeightUpdateLogsMapper(DockPlanUnloadWeightUpdateLogsMapper dockPlanUnloadWeightUpdateLogsMapper) {
        WebSocketProcess.dockPlanUnloadWeightUpdateLogsMapper = dockPlanUnloadWeightUpdateLogsMapper;
    }
    @Autowired
    public void setDockWindowPeriodMapper(DockWindowPeriodMapper dockWindowPeriodMapper) {
        WebSocketProcess.dockWindowPeriodMapper = dockWindowPeriodMapper;
    }
    @Autowired
    private void setDockUnloadWorkMapper(DockUnloadWorkMapper dockUnloadWorkMapper) {
        WebSocketProcess.dockUnloadWorkMapper = dockUnloadWorkMapper;
    }
    @Autowired
    private void setDockUnloadDetailMapper(DockUnloadDetailMapper dockUnloadDetailMapper) {
        WebSocketProcess.dockUnloadDetailMapper = dockUnloadDetailMapper;
    }
    @Autowired
    private void setDockSlowDownWorkLogsMapper(DockSlowDownWorkLogsMapper dockSlowDownWorkLogsMapper) {
        WebSocketProcess.dockSlowDownWorkLogsMapper = dockSlowDownWorkLogsMapper;
    }
    private static long websocketPeriod;
    @Value("${websocket.period}")
    public void setWebsocketPeriod(long period) {
        WebSocketProcess.websocketPeriod = period;
    }
    /*
     * 客户端创建连接时触发
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        if (userId == null) {
            log.error("WebSocket 连接时 userId 为空，sessionId={}", session.getId());
            try {
                session.close();
            } catch (IOException e) {
                log.error("关闭 WebSocket 失败", e);
            }
            return;
        }
        this.session = session;
        this.id = userId;
        concurrentHashMap.put(userId, this);
        log.info("Open a websocket. id={}", userId);
        SysUser sysUser = sysUserMapper.selectUserById(userId);
        startScheduledTask(userId,sysUser.getDeptId());
    }
    /**
     * 接收到客户端消息时触发
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        try{
            JSONObject json = JSONObject.parseObject(message);
            Long deptId = json.containsKey("deptId") ? json.getLong("deptId") : -1L;
            if(deptId==-1L){
                deptId = sysUserMapper.selectUserById(userId).getDeptId();
            }
            startScheduledTask(userId,deptId);
        }catch(Exception e){
            log.error("解析WebSocket部门信息失败", e);
        }
    }
    /**
     * 客户端连接关闭时触发
     **/
    @OnClose
    public void onClose() {
        if (id != null) {
            concurrentHashMap.remove(id);
            stopScheduledTask(id);
            log.info("WebSocket连接关闭，去除该连接和其定时任务 id={}", id);
        } else {
            log.warn("WebSocket连接关闭失败，id={}",id);
        }
    }
    /**
     * 连接发生异常时候触发
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket连接异常onError={}",error);
    }

    /**
    * 发送消息到指定客户端
    *  @param id
    *  @param message
    * */
    public void sendMessage(long id, String message) throws Exception {
       //根据id,从map中获取存储的webSocket对象
        WebSocketProcess webSocketProcess = concurrentHashMap.get(id);
        if (!ObjectUtils.isEmpty(webSocketProcess)) {
            //当客户端是Open状态时，才能发送消息
            if (webSocketProcess.session.isOpen()) {
                webSocketProcess.session.getBasicRemote().sendText(message);
            } else {
                log.error("websocket session={} is closed ", id);
            }
        } else {
             log.error("websocket session={} is not exit ", id);
        }
    }

    @Async
    public void startScheduledTask(Long userId,Long deptId) {
        stopScheduledTask(userId);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                Map<String, Object> screenMap = selectScreenAllDate(deptId);
                String message = new JSONObject(screenMap).toString();
                sendMessage(id, message);
                System.out.println("发送成功："+DateTime.now()+"id:"+id);
            } catch (Exception e) {
                log.error("WebSocket定时任务异常={}", e);
            }
        }, 0, websocketPeriod, TimeUnit.SECONDS);
        scheduledTasks.put(userId,future);
    }
    private void stopScheduledTask(Long userId) {
        ScheduledFuture<?> future = scheduledTasks.remove(userId);
        if (future != null) {
            future.cancel(true);
        }
    }
    /**
     * 全部数据
     * @return
     */
    private Map<String, Object> selectScreenAllDate(Long deptId){
        Map<String, Object> screenMap = new HashMap<>();
        //👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆
        List<Long> deptIds = sysDeptMapper.newScreen_SelectListByAncestors(deptId);
        deptIds.add(deptId);
        List<SysDept> sysDepts = sysDeptMapper.selectDeptList(new SysDept());
        screenMap.put("depts", sysDepts);//所有部门信息
        List<DockPier> dockPiers = dockPierMapper.newScreen_SelectListByDeptIds(deptIds);
        screenMap.put("dockPiers", dockPiers);//所有码头信息
        List<DockBerth> dockBerths = dockBerthMapper.newScreen_SelectListByDeptIds(deptIds);
        screenMap.put("dockBerths", dockBerths);//所有泊位信息
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        Year year = Year.now();
        LocalDate firstDay = year.atDay(1); // 今年的第一天
        LocalDate lastDay = year.atDay(year.length()); // 今年的最后一天
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDateTime todayStart = today.atStartOfDay(); // 今天 00:00:00
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX); // 今天 23:59:59.999999999
        LocalDateTime tomorrowStart = tomorrow.atStartOfDay(); // 明天 00:00:00
        LocalDateTime tomorrowEnd = tomorrow.atTime(LocalTime.MAX); // 明天 23:59:59.999999999
        LocalDateTime yesterdayStart = yesterday.atStartOfDay(); // 昨天 00:00:00
        LocalDateTime yesterdayEnd = yesterday.atTime(LocalTime.MAX); // 昨天 23:59:59.999999999
        LocalDateTime yearStart = firstDay.atStartOfDay(); // 今年 1月1日 00:00:00
        LocalDateTime yearEnd = lastDay.atTime(LocalTime.MAX); // 今年 12月31日 23:59:59.999999999
        LocalDateTime monthStart = firstDayOfMonth.atStartOfDay(); // 本月 1日 00:00:00
        LocalDateTime monthEnd = lastDayOfMonth.atTime(LocalTime.MAX); // 本月最后一天 23:59:59.999999999
        List<DockPlan> dockPlansForMap = dockPlanMapper.newScreen_SelectPlanListForMap(deptIds);
        if(dockPlansForMap.size()>0){
            List<Long> dockPlanIds = dockPlansForMap.stream().map(DockPlan::getId).toList();
            List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectByBatchPlanIds(dockPlanIds);
            Map<Long,List<String>> littleTool = new HashMap<>();
            for(DockPlanAssistant dpa : dockPlanAssistants){
                if(!littleTool.containsKey(dpa.getPlanId())){
                    littleTool.put(dpa.getPlanId(),new ArrayList<>());
                }
                littleTool.get(dpa.getPlanId()).add(dpa.getMaterialName());
            }
            dockPlansForMap.forEach(plan->{
                if(littleTool.containsKey(plan.getId())){
                    for(String materialName : littleTool.get(plan.getId())){
                        plan.setMaterialName(plan.getMaterialName()+" | "+materialName);
                    }
                }
            });
        }
        screenMap.put("dockPlansForMap", dockPlansForMap);
        List<DockPlan> dockPlans1 = dockPlanMapper.newScreen_SelectPlanList1(todayStart, todayEnd, tomorrowStart, tomorrowEnd,deptIds);
        if(dockPlans1.size()>0){
            List<Long> dockPlanIds = dockPlans1.stream().map(DockPlan::getId).toList();
            List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectByBatchPlanIds(dockPlanIds);
            Map<Long,List<String>> littleTool = new HashMap<>();
            for(DockPlanAssistant dpa : dockPlanAssistants){
                if(!littleTool.containsKey(dpa.getPlanId())){
                    littleTool.put(dpa.getPlanId(),new ArrayList<>());
                }
                littleTool.get(dpa.getPlanId()).add(dpa.getMaterialName());
            }
            dockPlans1.forEach(plan->{
                if(littleTool.containsKey(plan.getId())){
                    for(String materialName : littleTool.get(plan.getId())){
                        plan.setMaterialName(plan.getMaterialName()+" | "+materialName);
                    }
                }
            });
        }
        screenMap.put("dockPlans1", dockPlans1);//本部门及以下部门的: 计划的到港日期或离泊时间在今天或者明天的计划
        List<DockPlan> dockPlans2 = dockPlanMapper.newScreen_SelectPlanList2(deptIds);
        screenMap.put("dockPlans2", addCompleMaterialName(dockPlans2));//本部门及以下部门的: 有到港日期，计划靠泊时间， 没有靠港日期的计划
        BigDecimal yearThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(yearStart,yearEnd,deptIds);
        BigDecimal monthThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(monthStart,monthEnd,deptIds);
        BigDecimal yesterdayThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(yesterdayStart,yesterdayEnd,deptIds);
        screenMap.put("yearThroughPut",yearThrouhPut==null?BigDecimal.ZERO:yearThrouhPut);
        screenMap.put("monthThroughPut",monthThrouhPut==null?BigDecimal.ZERO:monthThrouhPut);
        screenMap.put("yesterdayThrouhPut",yesterdayThrouhPut==null?BigDecimal.ZERO:yesterdayThrouhPut);
        screenMap.put("taskLogs",InMemoryTaskLogStore.getAll().values().stream().collect(Collectors.toList()));//皮带秤日志
        /*planList3★★★★★★
        planList3 的 params 属性
        ├─ 计划附表
        │  ├─ updateLogs:已作业量更改日志
        │  ├─ 作业效率
		│  └─ 卸货单
		│  		├─ 暂停日志
        │    	└─ 慢作业日志
        ├─ 空窗期日志
        ├─ 卸货单
        │  ├─ 暂停日志
        │  └─ 慢作业日志
        ├─ 已作业量更改日志
        ├─ 作业效率
        └─ 滞期费 */
        List<DockPlan> dockPlans3 = dockPlanMapper.newScreen_SelectPlanList3(deptIds);//计划3主对象
        List<Long> dockPlans3Ids = dockPlans3.stream().map(DockPlan::getId).toList();
        if(dockPlans3Ids.size()<1){
            screenMap.put("dockPlans3", dockPlans3);
            return screenMap;
        }
        List<DockMaterial> dockMaterials = dockMaterialMapper.selectDockMaterialList(new DockMaterial());
        Map<String,String> materialMap = new HashMap<>();
        dockMaterials.forEach(dockMaterial -> {
            materialMap.put(dockMaterial.getMaterialName(),dockMaterial.getRemark02());
        });
        List<DockPlanUnloadWeightUpdateLogs> undateLogs = dockPlanUnloadWeightUpdateLogsMapper.getByPlanIds(dockPlans3Ids);//获取所有plan3的已作业更改日志
        List<DockPlanAssistant> dockPlans3_Assistant = dockPlanAssistantMapper.selectByBatchPlanIds(dockPlans3Ids);//获取所有plan3的物料附表
        List<DockWindowPeriod> dockPlans3_WindowPeriods = dockWindowPeriodMapper.selectByPlanIds(dockPlans3Ids);//获取所有plan3的空窗日志表
        List<DockUnloadWork> dockPlans3_UnloadWorks = dockUnloadWorkMapper.selectByPlanIds(dockPlans3Ids);//获取所有plan3的装卸单
        List<Long> dockPlans3_UnloadWorksIds = dockPlans3_UnloadWorks.stream().map(DockUnloadWork::getDuId).toList();
        List<DockUnloadDetail> dockPlans3_UnloadDetails = new ArrayList<>();
        if(dockPlans3_UnloadWorksIds.size()>1){
            dockPlans3_UnloadDetails = dockUnloadDetailMapper.selectByDuIds(dockPlans3_UnloadWorksIds);//获取所有plan3的装卸单的暂停日志
        }
        List<DockSlowDownWorkLogs> dockPlans3_SlowDownWorkLogs = dockSlowDownWorkLogsMapper.selectByPlanIds(dockPlans3Ids);//获取所有plan3的装卸单的慢作业日志
        Map<Long,Map<Integer,List<DockPlanUnloadWeightUpdateLogs>>> logSplitTool=new HashMap<>();//已作业日志map工具
        undateLogs.forEach(log->{
            if(!logSplitTool.containsKey(log.getPlanId())){
                logSplitTool.put(log.getPlanId(),new HashMap<>());
            }
            if(!(logSplitTool.get(log.getPlanId()).containsKey(log.getLoadSequence()))){
                logSplitTool.get(log.getPlanId()).put(log.getLoadSequence(),new ArrayList<>());
            }
            logSplitTool.get(log.getPlanId()).get(log.getLoadSequence()).add(log);
        });
        Map<Long,List<DockPlanAssistant>> assSplitTool=new HashMap<>();//物料附表map工具
        dockPlans3_Assistant.forEach(item->{
            if(!assSplitTool.containsKey(item.getPlanId())){
                assSplitTool.put(item.getPlanId(),new ArrayList<>());
            }
            assSplitTool.get(item.getPlanId()).add(item);
        });
        Map<Long,List<DockWindowPeriod>> windowSplitTool=new HashMap<>();//空窗日志map工具
        dockPlans3_WindowPeriods.forEach(item->{
            if(!windowSplitTool.containsKey(item.getPlanId())){
                windowSplitTool.put(item.getPlanId(),new ArrayList<>());
            }
            windowSplitTool.get(item.getPlanId()).add(item);
        });
        Map<Long,Map<Integer,List<DockUnloadWork>>> unloadSplitToolForEfficiency=new HashMap<>();//装卸货单map工具2号，
        dockPlans3_UnloadWorks.forEach(item->{
            if(!unloadSplitToolForEfficiency.containsKey(item.getPlanId())){
                unloadSplitToolForEfficiency.put(item.getPlanId(),new HashMap<>());
            }
            Integer loadSequence = 1;
            String remark01 = item.getRemark01();
            if(remark01!=null && !"".equals(remark01)){
                loadSequence = Integer.parseInt(remark01);
            }
            if(!(unloadSplitToolForEfficiency.get(item.getPlanId()).containsKey(loadSequence))){
                unloadSplitToolForEfficiency.get(item.getPlanId()).put(loadSequence,new ArrayList<>());
            }
            unloadSplitToolForEfficiency.get(item.getPlanId()).get(loadSequence).add(item);
        });
        Map<Long,List<DockUnloadDetail>> unloadDetailSplitTool=new HashMap<>();//装卸单暂停日志map工具
        dockPlans3_UnloadDetails.forEach(item->{
            if(!unloadDetailSplitTool.containsKey(item.getDuId())){
                unloadDetailSplitTool.put(item.getDuId(),new ArrayList<>());
            }
            unloadDetailSplitTool.get(item.getDuId()).add(item);
        });
        Map<Long,List<DockSlowDownWorkLogs>> slowDownWorkSplitTool=new HashMap<>();//装卸单慢作业日志map工具
        dockPlans3_SlowDownWorkLogs.forEach(item->{
            if(!slowDownWorkSplitTool.containsKey(item.getUnloadWorkId())){
                slowDownWorkSplitTool.put(item.getUnloadWorkId(),new ArrayList<>());
            }
            slowDownWorkSplitTool.get(item.getUnloadWorkId()).add(item);
        });
        dockPlans3.forEach(item->{
            Map<String,Object> planParams=new HashMap<>();
//      ├─ 计划附表
//      │  ├─ updateLogs:已作业量更改日志
//      │  ├─ 作业效率
//		│  └─ 卸货单
//		│  		├─ 暂停日志
//      │    	└─ 慢作业日志
            for(DockPlanAssistant assistant:assSplitTool.getOrDefault(item.getId(),Collections.emptyList())){
                Map<String,Object> params=new HashMap<>();
                params.put("updateLogs",logSplitTool.getOrDefault(assistant.getPlanId(),Collections.emptyMap()).getOrDefault(assistant.getLoadSequence(),Collections.emptyList()));//已作业量更改日志
                BigDecimal efficiencyTime = BigDecimal.ZERO;//有效总时间
                BigDecimal efficiencyWorkLoad = BigDecimal.ZERO;//有效总工作量
                for(DockUnloadWork unloadWork:
                        unloadSplitToolForEfficiency
                        .getOrDefault(assistant.getPlanId(),Collections.emptyMap())
                        .getOrDefault(assistant.getLoadSequence(),Collections.emptyList())){
                    //算卸货效率
                    if(unloadWork.getStartTime()!=null && unloadWork.getEndTime()!=null){
//                        if(assistant.getPackageNum()==2){//件
//                            efficiencyWorkLoad = efficiencyWorkLoad.add(BigDecimal.valueOf(unloadWork.getUnloadNum()));
//                        }else if(assistant.getPackageNum()==1){//吨
//                            efficiencyWorkLoad = efficiencyWorkLoad.add(unloadWork.getTotalUnloadWeight());
//                        }else{
                            efficiencyWorkLoad = efficiencyWorkLoad
                                    .add(BigDecimal.valueOf(unloadWork.getUnloadNum()==null?0:unloadWork.getUnloadNum()))
                                    .add(unloadWork.getTotalUnloadWeight()==null?BigDecimal.ZERO:unloadWork.getTotalUnloadWeight());
//                        }
                        BigDecimal workTime=BigDecimal.valueOf(Duration.between(unloadWork.getStartTime(),unloadWork.getEndTime()).toMinutes());//分钟
                        for(DockUnloadDetail workTimeDetail:unloadDetailSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList())){
                            if("2".equals(workTimeDetail.getRemark()) && workTimeDetail.getStartTime()!=null && workTimeDetail.getEndTime()!=null){
                                workTime=workTime.subtract(//减去客观原因
                                        BigDecimal.valueOf(
                                                Duration.between(
                                                        workTimeDetail.getStartTime(),workTimeDetail.getEndTime()).toMinutes()));
                            }
                        }
                        efficiencyTime=efficiencyTime.add(workTime.divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_HALF_UP));//小时
                    }
                    //自己的东西
                    Map<String,Object> innerParams=new HashMap<>();
                    innerParams.put("unloadWorkDetail",unloadDetailSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList()));//计划附表的卸货单的暂停日志
                    innerParams.put("slowDownWork",slowDownWorkSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList()));//计划附表的卸货单的慢作业日志
                    unloadWork.setParams(innerParams);
                }
                String efficiency;
                if(efficiencyTime.compareTo(BigDecimal.ZERO)==0 || efficiencyWorkLoad.compareTo(BigDecimal.ZERO)==0){
                    efficiency = "0";
                }else {
                    efficiency = efficiencyWorkLoad.divide(efficiencyTime,2,BigDecimal.ROUND_HALF_UP).toString();
                }
                efficiency+=materialMap.get(assistant.getMaterialName())+"每小时";
                params.put("efficiency",efficiency);//作业效率
                params.put("unloadWorkList",unloadSplitToolForEfficiency.getOrDefault(assistant.getPlanId(),Collections.emptyMap()).getOrDefault(assistant.getLoadSequence(),Collections.emptyList()));
                assistant.setParams(params);
            }
            planParams.put("assistantList",assSplitTool.getOrDefault(item.getId(),Collections.emptyList()));
//        ├─ 空窗期日志
            planParams.put("windowPeriodList",windowSplitTool.getOrDefault(item.getId(),Collections.emptyList()));
//        ├─ 卸货单
//        │  ├─ 暂停日志
//        │  └─ 慢作业日志
//        ├─ 作业效率
            BigDecimal mainGoodEfficiencyTime = BigDecimal.ZERO;
            BigDecimal mainGoodEfficiencyWorkLoad = BigDecimal.ZERO;
            for(DockUnloadWork unloadWork:unloadSplitToolForEfficiency.getOrDefault(item.getId(),Collections.emptyMap()).getOrDefault(1,Collections.emptyList())){
                Map<String,Object> params=new HashMap<>();
                //卸货单相关
                params.put("unloadWorkDetail",unloadDetailSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList()));
                params.put("slowDownWork",slowDownWorkSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList()));
                unloadWork.setParams(params);
                //效率相关
                if(unloadWork.getStartTime()!=null && unloadWork.getEndTime()!=null){
//                    if(item.getPackageNum()!=null && item.getPackageNum()==2){//件
//                        mainGoodEfficiencyWorkLoad = mainGoodEfficiencyWorkLoad.add(BigDecimal.valueOf(unloadWork.getUnloadNum()==null?0:unloadWork.getUnloadNum()));
//                    }else if(item.getPackageNum()!=null && item.getPackageNum()==1){//吨
//                        mainGoodEfficiencyWorkLoad = mainGoodEfficiencyWorkLoad.add(unloadWork.getTotalUnloadWeight()==null?BigDecimal.ZERO:unloadWork.getTotalUnloadWeight());
//                    }else {
                        mainGoodEfficiencyWorkLoad = mainGoodEfficiencyWorkLoad
                                .add(unloadWork.getTotalUnloadWeight()==null?BigDecimal.ZERO:unloadWork.getTotalUnloadWeight())
                                .add(BigDecimal.valueOf(unloadWork.getUnloadNum()==null?0:unloadWork.getUnloadNum()));
//                    }
                    BigDecimal workTime=BigDecimal.valueOf(Duration.between(unloadWork.getStartTime(),unloadWork.getEndTime()).toMinutes());//分钟
                    for(DockUnloadDetail workTimeDetail:unloadDetailSplitTool.getOrDefault(unloadWork.getDuId(),Collections.emptyList())){
                        if("2".equals(workTimeDetail.getRemark()) && workTimeDetail.getStartTime()!=null && workTimeDetail.getEndTime()!=null){
                            workTime=workTime.subtract(//减去客观原因的时间
                                    BigDecimal.valueOf(
                                            Duration.between(workTimeDetail.getStartTime(),workTimeDetail.getEndTime()).toMinutes()));
                        }
                    }
                    mainGoodEfficiencyTime=mainGoodEfficiencyTime.add(workTime.divide(BigDecimal.valueOf(60),2,BigDecimal.ROUND_HALF_UP));//小时
                }
            }
            planParams.put("unloadWorkList",unloadSplitToolForEfficiency.getOrDefault(item.getId(),Collections.emptyMap()).getOrDefault(1,Collections.emptyList()));
            String efficiency;
            if(mainGoodEfficiencyWorkLoad.compareTo(BigDecimal.ZERO)==0 || mainGoodEfficiencyTime.compareTo(BigDecimal.ZERO)==0){
                efficiency = "0";
            }else {
                efficiency = mainGoodEfficiencyWorkLoad.divide(mainGoodEfficiencyTime,2,BigDecimal.ROUND_HALF_UP).toString();
            }
            efficiency+=materialMap.get(item.getMaterialName())+"每小时";
            planParams.put("efficiency",efficiency);//作业效率
//        ├─ 已作业量更改日志
            planParams.put("updateLogs",logSplitTool.getOrDefault(item.getId(),Collections.emptyMap()).getOrDefault(1,Collections.emptyList()));
//        └─ 滞期费 */
            BigDecimal collectFee = BigDecimal.ZERO;
            LocalDateTime now = LocalDateTime.now();
            if(item.getContractFee()!=null && item.getDockingTime()!=null && item.getDockingTime().isBefore(now)){
                BigDecimal differenceValueMinute;//总间隔时间
                if(item.getOutBerthTime()==null || item.getOutBerthTime().isAfter(now)){//还没有离泊时间或离泊时间在将来
                    differenceValueMinute = BigDecimal.valueOf(Duration.between(item.getDockingTime(),now).toMinutes());
                }else{//已有离泊时间且离泊时间在过去
                    differenceValueMinute = BigDecimal.valueOf(Duration.between(item.getDockingTime(),item.getOutBerthTime()).toMinutes());
                }
                BigDecimal sumFreeMinutes = BigDecimal.valueOf(720L);//总免除的时间（默认是12小时）
                for(DockWindowPeriod period:windowSplitTool.getOrDefault(item.getId(),Collections.emptyList())){
                    if(period.getAvoidCollectFee()){
                        LocalDateTime t1 = parseStrict(period.getRemark1());
                        LocalDateTime t2 = parseStrict(period.getRemark2());
                        if(t1!=null&&t2!=null){
                            sumFreeMinutes=sumFreeMinutes.add(BigDecimal.valueOf(Duration.between(t1,t2).toMinutes()));
                        }
                    }
                }
                if(differenceValueMinute.compareTo(sumFreeMinutes)>0){//总时间大于减免时间
                    differenceValueMinute = differenceValueMinute.subtract(sumFreeMinutes);
                }
                collectFee = differenceValueMinute
                        .divide(BigDecimal.valueOf(1440), 2, RoundingMode.HALF_UP)
                        .multiply(item.getContractFee());
            }
            planParams.put("collectFee",collectFee.setScale(0, RoundingMode.CEILING).intValue()+"$");
            item.setParams(planParams);
        });
        screenMap.put("dockPlans3", dockPlans3);//本部门及以下部门的作业中计划plan3大对象
        return screenMap;
    }
    /*
    工具方法：string类型时间转LocalDateTime类型
     */
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime parseStrict(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            // 先解析
            LocalDateTime dt = LocalDateTime.parse(value, FORMATTER);
            // 再格式化回来比对，避免 "2025-8-8 10:3:0" 这种被解析成功
            if (!FORMATTER.format(dt).equals(value)) {
                return null;
            }
            return dt;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    private List<DockPlan> addCompleMaterialName(List<DockPlan> dockPlans){
        if(dockPlans!=null&&dockPlans.size()>0){
            List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectByBatchPlanIds(dockPlans.stream().map(DockPlan::getId).toList());
            Map<Long,List<String>> mapTool = new HashMap<>();
            dockPlanAssistants.forEach(dockPlanAssistant -> {
                if(!mapTool.containsKey(dockPlanAssistant.getPlanId())){
                    mapTool.put(dockPlanAssistant.getPlanId(),new ArrayList<>());
                }
                mapTool.get(dockPlanAssistant.getPlanId()).add(dockPlanAssistant.getMaterialName());
            });
            dockPlans.forEach(plan -> {
                Map<String,Object> params = new HashMap<>();
                String compleMaterialName = plan.getMaterialName();
                if(mapTool.containsKey(plan.getId())){
                    for(String name:mapTool.get(plan.getId())){
                        compleMaterialName+=" | "+name;
                    }
                    plan.setMaterialName(compleMaterialName);
                }
            });
        }
        return dockPlans;
    }
    /**************🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮***半废弃******🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮🚮***********/
    /**
     * 根据用户ID发送错误数据给前端
     * @param userId
     * @param errorMessage
     */
    public void sendErrorMessage(long userId, String errorMessage) {
        try {
            WebSocketProcess webSocketProcess = concurrentHashMap.get(userId);
            if (webSocketProcess != null && webSocketProcess.session.isOpen()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("type", "error");
                errorResponse.put("message", errorMessage);
                webSocketProcess.session.getBasicRemote().sendText(errorResponse.toJSONString());
            } else {
                log.error("websocket session={} is not available", userId);
            }
        } catch (IOException e) {
            log.error("Failed to send error message to session={}: {}", userId, e.getMessage());
        }
    }
//    public void updatePushAll() {
//        Map<String, Object> screenMap = selectScreenAllDate();
//        String message = new JSONObject(screenMap).toString();
//
//        Iterator<Map.Entry<Long, WebSocketProcess>> iterator = concurrentHashMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Long, WebSocketProcess> entry = iterator.next();
//            Long key = entry.getKey();
//            WebSocketProcess client = entry.getValue();
//            try {
//                if (client.session.isOpen()) {
//                    client.sendMessage(key, message);
//                } else {
//                    sendErrorMessage(key, "WebSocket 连接已关闭，无法接收数据");
//                    iterator.remove(); // 通过 Iterator 安全移除
//                }
//            } catch (Exception e) {
//                sendErrorMessage(key, "WebSocket 发送消息失败: " + e.getMessage());
//                iterator.remove(); // 通过 Iterator 安全移除
//                log.error("WebSocket 消息发送失败，userId={}，错误={}", key, e.getMessage(), e);
//            }
//        }
//    }
}