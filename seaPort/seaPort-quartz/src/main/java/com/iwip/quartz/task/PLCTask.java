package com.iwip.quartz.task;


import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.harbor.domain.ScheduledTasks;
import com.iwip.harbor.mapper.ScheduledTasksMapper;
import com.iwip.harbor.plc.IPLCDataProcess;
import com.iwip.harbor.plc.PLCConstans;
import com.iwip.harbor.plc.PLCDataProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("plcTask")
public class PLCTask {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ScheduledTasksMapper scheduledTasksMapper;


    public void readPLC() {
        log.info("执行 PLC 每小时统计任务");

        List<ScheduledTasks> runningTasks = scheduledTasksMapper.getRunningTasks();
        if (runningTasks == null || runningTasks.isEmpty()) {
            log.info("暂无执行中的计划任务");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String currentHourKey = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        for (ScheduledTasks task : runningTasks) {
            String berthName = task.getBerthName();
            Long planId = task.getPlanId();

            // 处理标识，防止重复执行
            String markKey = String.format("plc:mark:%s:%s", planId, currentHourKey);
            if (Boolean.TRUE.equals(redisCache.getCacheObject(markKey))) {
                log.info("计划单{} 在 {} 已处理过，跳过", planId, currentHourKey);
                continue;
            }

            IPLCDataProcess plcDataProcess = PLCDataProcessContext.getByBerthName(berthName);
            Map<String, BigDecimal> plcMap = plcDataProcess.readPlc();

            if (plcMap == null || plcMap.isEmpty()) {
                log.warn("读取 PLC 数据为空，berthName={}", berthName);
                continue;
            }

            BigDecimal sumWeightDate = plcMap.getOrDefault(PLCConstans.SUM_WEIGHT_DATE, BigDecimal.ZERO);


            String lastTimeWeightKey = String.format(RedisKeyConstants.LAST_TIME_WEIGHT, berthName);
            String hourWeightKey = String.format(RedisKeyConstants.PLC_HOUR_PLAN_REAL_WEIGHT, planId, berthName);

            Map<String, Object> hourWeightMap = redisCache.getCacheMap(hourWeightKey);
            if (hourWeightMap == null) {
                hourWeightMap = new HashMap<>();
            }

            Object cached = redisCache.getCacheObject(lastTimeWeightKey);
            BigDecimal initWeight = new BigDecimal(cached != null ? cached.toString() : "0");

            BigDecimal totalHourWeight = BigDecimal.ZERO;
            for (Object value : hourWeightMap.values()) {
                if (value instanceof Number || value.toString().matches("^-?\\d+(\\.\\d+)?$")) {
                    totalHourWeight = totalHourWeight.add(new BigDecimal(value.toString()));
                }
            }

            BigDecimal hourWeight = sumWeightDate.subtract(initWeight).subtract(totalHourWeight);
            if (hourWeight.compareTo(BigDecimal.ZERO) <= 0) {
                String err = String.format("连接异常, 数据异常。累计吨重=%s, 初始吨重=%s, 本小时卸货量=%s",
                        sumWeightDate, initWeight, hourWeight);
                hourWeightMap.put(currentHourKey, BigDecimal.ZERO);
                log.warn("计划单{} 异常小时卸货记录：{}", planId, err);
            } else {
                hourWeightMap.put(currentHourKey, hourWeight);
                log.info("计划单{} 正常小时卸货记录：{} 吨", planId, hourWeight);
            }

            // TreeMap 确保顺序
            Map<String, Object> sortedMap = new TreeMap<>(hourWeightMap);
            redisCache.setCacheMap(hourWeightKey, sortedMap, 7, TimeUnit.DAYS);

            // 设置标志位避免重复
            redisCache.setCacheObject(markKey, true, 2, TimeUnit.HOURS);
        }
    }




}
