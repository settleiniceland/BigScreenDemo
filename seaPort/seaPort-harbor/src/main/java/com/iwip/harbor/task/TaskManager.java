package com.iwip.harbor.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwip.harbor.domain.DockMaterial;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockPlanAssistant;
import com.iwip.harbor.domain.DockTaskConfig;
import com.iwip.harbor.mapper.DockMaterialMapper;
import com.iwip.harbor.mapper.DockPlanAssistantMapper;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.mapper.DockTaskConfigMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class TaskManager {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DockTaskConfigMapper taskConfigMapper;
    @Autowired
    private DockPlanMapper dockPlanMapper;
    @Autowired
    private DockMaterialMapper dockMaterialMapper;
    @Autowired
    private DockPlanAssistantMapper dockPlanAssistantMapper;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(20);
    private Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void initTask(){
        DockTaskConfig config=new DockTaskConfig();
        List<DockTaskConfig> dockTaskConfigList = taskConfigMapper.selectDockTaskConfigList(config);
        for(DockTaskConfig dockTaskConfig:dockTaskConfigList){
            startTask(dockTaskConfig);
        }
        log.info("初始化加载完所有定时任务: " + dockTaskConfigList.size());
    }

    public void reloadTask(String taskName) {
        DockTaskConfig config=new DockTaskConfig();
        config.setTaskName(taskName);
        List<DockTaskConfig> dockTaskConfigs =taskConfigMapper.selectDockTaskConfigList(config);
        startTask(dockTaskConfigs.get(0));
    }

    public void deleteTask(String taskName) {
        InMemoryTaskLogStore.clear();
        stopTask(taskName);
    }

    public synchronized void startTask(DockTaskConfig taskConfig) {
        stopTask(taskConfig.getTaskName());
        if(taskConfig==null||!taskConfig.getEnabled()){
            log.info("⏹ 任务未开启: " + (taskConfig != null ? taskConfig.getTaskName() : "null"));
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(()->{
            /*
            0、查询dock_material物料表中所有remark属性值为1的物料，计为物料数组x；
                如果物料数组X为空，直接退出❌
            1、根据DockTaskConfig.hbName在计划表中搜索：
                是这个泊位的、状态是4的，未归档的，物料属于上面物料数组x的计划，
                如果没有这样的计划，直接退出❌
                如果有这样的计划的话，取get(0)，别的不管（正常只会有1个）
             */
            List<DockPlan> plans = getNeededPlanList(taskConfig.getHbName());
            if(plans==null||plans.size()==0){
                log.info("{}泊位定时任务未更新，没有符合条件的计划",taskConfig.getHbName());
                String logMsg = String.format("%s:%s泊位定时任务未更新，没有符合条件的计划",LocalDateTime.now().format(formatter),taskConfig.getHbName());
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            String[] urls = taskConfig.getTargetUrl().split("@");
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("tagids", urls[1]);
            requestBody.put("tagname", "");
            requestBody.put("charset", "gbk");
            requestBody.put("archived", 0);
            /*
            2、取该计划的作业开始时间，以该时间往前推30min，至该时间，以一个区间，
                去查该区间的数采总累积量，取时间最晚的那条数组作为初始重量
                取不到直接退出❌
             */
            requestBody.put("btime",plans.get(0).getOperationTime().minusMinutes(15).format(formatter));
            requestBody.put("etime",plans.get(0).getOperationTime().format(formatter));
            Object originBtime = requestBody.get("btime");
            Object originEtime = requestBody.get("etime");
            BigDecimal originData = sentRequest(requestBody, urls[0]);
            if(originData==null){
                log.info("{}泊位定时任务未更新，初始时间<{}至{}>采不到数",taskConfig.getHbName(),originBtime,originEtime);
                String logMsg = String.format("%s:%s泊位定时任务未更新，初始时间<%s至%s>采不到数",LocalDateTime.now().format(formatter),taskConfig.getHbName(),originBtime,originEtime);
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            3、取当前时间，以当前时间往前推30min,至该时间，以一个区间，去查该区间的数采总累积量，取时间最晚的那条数组作为现在重量
	            取不到直接退出❌
             */
            requestBody.put("btime",(LocalDateTime.now()).minusMinutes(10).format(formatter));
            requestBody.put("etime",(LocalDateTime.now()).plusMinutes(2).format(formatter));
            Object nowBtime = requestBody.get("btime");
            Object nowEtime = requestBody.get("etime");
            BigDecimal nowData = sentRequest(requestBody, urls[0]);
            if(nowData==null){
                log.info("{}泊位定时任务未更新，当前时间<{}至{}>采不到数",taskConfig.getHbName(),nowBtime,nowEtime);
                String logMsg = String.format("%s:%s泊位定时任务未更新，当前时间<%s至%s>采不到数",LocalDateTime.now().format(formatter),taskConfig.getHbName(),nowBtime,nowEtime);
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            4、计算出差值：差值<=0直接退出❌
             */
            BigDecimal weightData = nowData.subtract(originData);
            if(weightData.compareTo(BigDecimal.ZERO)<=0){
                log.info("{}泊位定时任务未更新，区间差值小于等于0-->{}-{}区间取数{}，{}-{}区间取数{}",taskConfig.getHbName(),originBtime,originEtime,originData,nowBtime,nowEtime,nowData);
                String logMsg = String.format("%s:%s泊位定时任务未更新，区间差值小于等于0-->%s-%s区间取数%s，%s-%s区间取数%s",LocalDateTime.now().format(formatter),taskConfig.getHbName(),originBtime,originEtime,originData,nowBtime,nowEtime,nowData);
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            5、如果差值小于计划第一个物料的实际吨量(tonnage)
	            更新第一个物料的已作业量为差值，结束✔
             */
            BigDecimal tonnage = new BigDecimal(plans.get(0).getTonnage());
            if(weightData.compareTo(tonnage)<0){
                dockPlanMapper.updatePlanUnloadWeightById(plans.get(0).getId(),weightData.toString());
                log.info("{}泊位定时任务更新✔，更新第一个物料",taskConfig.getHbName());
                String logMsg = String.format("%s:%s泊位定时任务更新✔，更新第一个物料",LocalDateTime.now().format(formatter),taskConfig.getHbName());
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            5.5、如果该计划的实际重量与已作业量不一致，更新已作业量为实际重量
             */
            if(!plans.get(0).getTonnage().equals(plans.get(0).getUnloadWeight())){
                dockPlanMapper.updatePlanUnloadWeightById(plans.get(0).getId(),plans.get(0).getTonnage());
            }
            /*
            6、如果差值大于计划第一个物料的实际吨量(tonnage)
	            如果该计划的remark02为空或者为0，直接退出❌
             */
            if(plans.get(0).getRemark02()==null||"0".equals(plans.get(0).getRemark02())){
                log.info("{}泊位定时任务未更新，第一个物料已满，无其他物料，且数采重量大于第一个物料实际吨重",taskConfig.getHbName());
                String logMsg = String.format("%s:%s泊位定时任务未更新，第一个物料已满，无其他物料，且数采重量大于第一个物料实际吨重",LocalDateTime.now().format(formatter),taskConfig.getHbName());
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            7、否则：
                1、差值 -= 第一个物料实际重量
                2、查该计划的所有计划附表，也就是其他物料表
             */
            weightData = weightData.subtract(tonnage);
            DockPlanAssistant dpa=new DockPlanAssistant();
            dpa.setPlanId(plans.get(0).getId());
            List<DockPlanAssistant> dockPlanAssistantList = dockPlanAssistantMapper.selectDockPlanAssistant(dpa);
            if(dockPlanAssistantList==null||dockPlanAssistantList.size()==0){
                log.info("{}泊位定时任务未更新，排除数据不一致情况",taskConfig.getHbName());
                String logMsg = String.format("%s:%s泊位定时任务未更新，排除数据不一致情况",LocalDateTime.now().format(formatter),taskConfig.getHbName());
                InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                return;
            }
            /*
            8、根据其他物料表做一个循环，
                1、进行判断，如果差值＜该物料的实际吨重，该物料的已作业量改满，直接return结束✔
                2、如果差值>该物料的实际吨重，差值 -= 改物料实际重量，并把该物料的已作业量赋满（赋值为实际吨重），之后接着循环
             */
            for(DockPlanAssistant dockPlanAssistant:dockPlanAssistantList){
                BigDecimal assiatantTonnage = dockPlanAssistant.getTonnage();
                if(weightData.compareTo(assiatantTonnage)<0){
                    dockPlanAssistantMapper.updateUnloadWeightById(dockPlanAssistant.getId(),weightData);
                    log.info("{}泊位定时任务更新成功✔，更新第{}个物料",taskConfig.getHbName(),dockPlanAssistant.getLoadSequence());
                    String logMsg = String.format("%s:%s泊位定时任务更新成功✔，更新第s%个物料",LocalDateTime.now().format(formatter),taskConfig.getHbName(),dockPlanAssistant.getLoadSequence());
                    InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
                    return;
                }else {
                    if(assiatantTonnage.compareTo(dockPlanAssistant.getUnloadWeight())>0){
                        dockPlanAssistantMapper.updateUnloadWeightById(dockPlanAssistant.getId(),dockPlanAssistant.getTonnage());
                    }
                    weightData = weightData.subtract(assiatantTonnage);
                }
            }
            log.info("{}泊位定时任务未更新，该船所有物料都已装卸完毕",taskConfig.getHbName());
            String logMsg = String.format("%s:%s泊位定时任务未更新，该船所有物料都已装卸完毕",LocalDateTime.now().format(formatter),taskConfig.getHbName());
            InMemoryTaskLogStore.put(taskConfig.getHbName(), logMsg);
        },1000,taskConfig.getFrequencyMs(), TimeUnit.MILLISECONDS);
        tasks.put(taskConfig.getTaskName(), future);
    }


    /*定时任务的方法*/
    private synchronized void stopTask(String taskName) {
        ScheduledFuture<?> future = tasks.remove(taskName);
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
            log.info("🛑 停止任务: " + taskName);
        }
    }
    /**👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇工具方法**************/
    //查询需要改的计划
    private List<DockPlan> getNeededPlanList(String hbName){
        DockMaterial dmParam=new DockMaterial();
        dmParam.setRemark("1");
        List<DockMaterial> dmList = dockMaterialMapper.selectDockMaterialList(dmParam);
        if(dmList==null||dmList.size()==0){
            return null;
        }
        DockPlan dpParam=new DockPlan();
        dpParam.setStatus("4");
        dpParam.setIsArchived("0");
        dpParam.setHbName(hbName);
        Map<String, Object> params = new HashMap<>();
        params.put("dmParam", dmList);
        dpParam.setParams(params);
        List<DockPlan> plans=dockPlanMapper.selectPlanListForTask(dpParam);
        return plans;
    }
    //👨‍🦳《老数采》--发送请求取数
    private BigDecimal sentRequest(Map<String, Object> requestBody,String url){
        BigDecimal returnData=null;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(java.util.Collections.singletonList(java.nio.charset.StandardCharsets.UTF_8));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if(response.getStatusCode()==HttpStatus.OK){
                DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd HH:mm:ss")
                        .optionalStart()
                        .appendLiteral('.')
                        .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, false)
                        .optionalEnd()
                        .toFormatter();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode lastestItem = root
                        .path("data")
                        .path("values").get(0)
                        .path("items")
                        .findValuesAsText("tm").stream()
                        .map(tm -> LocalDateTime.parse(tm, formatter1))
                        .max(Comparator.naturalOrder())
                        .map(latestTime -> {
                            // 找到 tm 最大的对象
                            for (JsonNode item : root.path("data").path("values").get(0).path("items")) {
                                if (item.get("tm").asText().equals(latestTime.format(formatter1))) {
                                    return item;
                                }
                            }
                            return null;
                        }).orElse(null);
                if(lastestItem!=null){
                    JsonNode pv = lastestItem.get("pv");
                    log.info("最新的皮带秤数据是"+pv+"吨；数采时间："+lastestItem.get("tm"));
                    if(pv!=null && !pv.isNull()){
                        if(pv.isNumber()){
                            returnData = pv.decimalValue();
                        }else {
                            returnData = new BigDecimal(pv.asText());
                        }
                    }
                }else{
                    log.info("采不到数:"+requestBody.get("btime")+"-->"+requestBody.get("etime"));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnData;
        }
    }
}
