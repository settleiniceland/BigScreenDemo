package com.iwip.harbor.service.impl;

import com.iwip.common.core.domain.entity.SysDept;
import com.iwip.harbor.domain.*;
import com.iwip.harbor.mapper.*;
import com.iwip.harbor.service.NewScreenService;
import com.iwip.system.mapper.SysDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class NewScreenServiceImpl implements NewScreenService {
    @Autowired
    private SysDeptMapper deptMapper;
    @Autowired
    private DockBerthMapper dockBerthMapper;
    @Autowired
    private DockPlanMapper dockPlanMapper;
    @Autowired
    private DockPlanAssistantMapper dockPlanAssistantMapper;
    @Autowired
    private DockPlanUnloadWeightUpdateLogsMapper dockPlanUnloadWeightUpdateLogsMapper;
    @Autowired
    private DockWindowPeriodMapper dockWindowPeriodMapper;
    @Autowired
    private DockUnloadWorkMapper dockUnloadWorkMapper;
    @Autowired
    private DockUnloadDetailMapper dockUnloadDetailMapper;
    @Autowired
    private DockSlowDownWorkLogsMapper dockSlowDownWorkLogsMapper;

    private final static String TODAY_START="todayStart";
    private final static String TODAY_END="todayEnd";
    private static final String TOMORROW_START = "tomorrowStart";
    private static final String TOMORROW_END = "tomorrowEnd";
    private static final String YESTERDAY_START = "yesterdayStart";
    private static final String YESTERDAY_END = "yesterdayEnd";
    private static final String YEAR_START = "yearStart";
    private static final String YEAR_END = "yearEnd";
    private static final String MONTH_START = "monthStart";
    private static final String MONTH_END = "monthEnd";
    @Override
    public List<SysDept> getAllDeptList() {
        return deptMapper.selectDeptList(new SysDept());
    }

    @Override
    public List<DockBerth> getBerthListByDeptIds(List<Long> deptIds) {
        return dockBerthMapper.newScreen_SelectListByDeptIds(deptIds);
    }

    @Override
    public Map<String, BigDecimal> getStatisticsData(List<Long> deptIds) {
        Map<String, LocalDateTime> dateMapTool = getDateMapTool();
        Map<String, BigDecimal> screenMap = new HashMap<>();
        BigDecimal yearThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(dateMapTool.get(YEAR_START),dateMapTool.get(YEAR_END),deptIds);
        BigDecimal monthThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(dateMapTool.get(MONTH_START),dateMapTool.get(MONTH_END),deptIds);
        BigDecimal yesterdayThrouhPut = dockPlanUnloadWeightUpdateLogsMapper.getThroughput(dateMapTool.get(YESTERDAY_START),dateMapTool.get(YESTERDAY_END),deptIds);
        screenMap.put("yearThroughPut",yearThrouhPut==null?BigDecimal.ZERO:yearThrouhPut);
        screenMap.put("monthThroughPut",monthThrouhPut==null?BigDecimal.ZERO:monthThrouhPut);
        screenMap.put("yesterdayThrouhPut",yesterdayThrouhPut==null?BigDecimal.ZERO:yesterdayThrouhPut);
        return screenMap;
    }

    @Override
    public List<DockPlan> getSuchArrivePlanByDeptIds(List<Long> deptIds) {
        Map<String, LocalDateTime> dateMapTool = getDateMapTool();
        List<DockPlan> dockPlans1 = dockPlanMapper.newScreen_SelectPlanList1(dateMapTool.get(TODAY_START),
                dateMapTool.get(TODAY_END), dateMapTool.get(TOMORROW_START), dateMapTool.get(TOMORROW_END),deptIds);
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
        return dockPlans1;
    }

    @Override
    public List<DockPlan> getWorkingPlanByDeptIds(List<Long> deptIds) {
        return getOldPlan3AsYouKonw(deptIds);
    }

    @Override
    public List<DockPlan> getPlanPopDetailByPlanId(List<Long> deptIds,String berchCode) {
        List<DockPlan> dockPlansForMap = dockPlanMapper.newNewScreen_SelectPlanListForPup(berchCode,deptIds);
        if(dockPlansForMap.size()>0){
            List<Long> dockPlanIds = dockPlansForMap.stream().map(DockPlan::getId).toList();
            List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectByBatchPlanIds(dockPlanIds);
            Map<Long,List<DockPlanAssistant>> littleTool = new HashMap<>();
            for(DockPlanAssistant dpa : dockPlanAssistants){
                if(!littleTool.containsKey(dpa.getPlanId())){
                    littleTool.put(dpa.getPlanId(),new ArrayList<>());
                }
                littleTool.get(dpa.getPlanId()).add(dpa);
            }
            dockPlansForMap.forEach(plan->{
                List<DockPlanAssistant> dpss = new ArrayList<>();
                String completeName = plan.getMaterialName();
                String completeAchieveWork = (plan.getUnloadWeight()!=null?plan.getUnloadWeight():0)+"/"+plan.getTonnage();
                if(littleTool.containsKey(plan.getId())){
                    for(DockPlanAssistant dps : littleTool.get(plan.getId())){
                        dpss.add(dps);
                        completeName += " | "+dps.getMaterialName();
                        completeAchieveWork += " | "+(dps.getUnloadWeight()!=null?dps.getUnloadWeight():0)+"/"+dps.getTonnage();
                    }
                }
                Map<String,Object> map = new HashMap<>();
                map.put("completeName",completeName);
                map.put("completeAchieveWork",completeAchieveWork);
                map.put("dpss",dpss);
                plan.setParams(map);
            });
        }
        return dockPlansForMap;
    }

    @Override
    public List<DockPlan> getPlan2(List<Long> deptIds) {
        List<DockPlan> dockPlans2 = dockPlanMapper.newScreen_SelectPlanList2(deptIds);
        return addCompleMaterialName(dockPlans2);
    }
    private Map<String,LocalDateTime> getDateMapTool(){
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        Year year = Year.now();
        LocalDate firstDay = year.atDay(1); // 今年的第一天
        LocalDate lastDay = year.atDay(year.length()); // 今年的最后一天
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        Map<String,LocalDateTime> mac=new HashMap<>();
        mac.put(TODAY_START, today.atStartOfDay()); // 今天 00:00:00
        mac.put(TODAY_END, today.atTime(LocalTime.MAX)); // 今天 23:59:59.999999999
        mac.put(TOMORROW_START, tomorrow.atStartOfDay()); // 明天 00:00:00
        mac.put(TOMORROW_END, tomorrow.atTime(LocalTime.MAX)); // 明天 23:59:59.999999999
        mac.put(YESTERDAY_START, yesterday.atStartOfDay()); // 昨天 00:00:00
        mac.put(YESTERDAY_END, yesterday.atTime(LocalTime.MAX)); // 昨天 23:59:59.999999999
        mac.put(YEAR_START, firstDay.atStartOfDay()); // 今年 1月1日 00:00:00
        mac.put(YEAR_END, lastDay.atTime(LocalTime.MAX)); // 今年 12月31日 23:59:59.999999999
        mac.put(MONTH_START, firstDayOfMonth.atStartOfDay()); // 本月 1日 00:00:00
        mac.put(MONTH_END, lastDayOfMonth.atTime(LocalTime.MAX)); // 本月最后一天 23:59:59.999999999
        return mac;
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
    private List<DockPlan> getOldPlan3AsYouKonw(List<Long> deptIds) {
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
            return dockPlans3;
        }
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
                                .add(BigDecimal.valueOf(unloadWork.getUnloadNum()))
                                .add(unloadWork.getTotalUnloadWeight());
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
                if(assistant.getPackageNum()==2){
                    efficiency+=" PCS/H";
                }else if(assistant.getPackageNum()==1){
                    efficiency+=" T/H";
                }else{
                    efficiency+=" PCS或T/H";
                }
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
            if(item.getPackageNum()!=null && item.getPackageNum()==2){//件
                efficiency+=" PCS/H";
            }else if(item.getPackageNum()!=null && item.getPackageNum()==1){
                efficiency+=" T/H";
            }else{
                efficiency+=" PCS或T/H";
            }
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
        return dockPlans3;
    }
    private final DateTimeFormatter FORMATTERCOPY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime parseStrict(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            // 先解析
            LocalDateTime dt = LocalDateTime.parse(value, FORMATTERCOPY);
            // 再格式化回来比对，避免 "2025-8-8 10:3:0" 这种被解析成功
            if (!FORMATTERCOPY.format(dt).equals(value)) {
                return null;
            }
            return dt;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
