package com.iwip.harbor.service.impl;

import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.ScheduledTasks;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.mapper.ScheduledTasksMapper;
import com.iwip.harbor.plc.IPLCDataProcess;
import com.iwip.harbor.plc.PLCConstans;
import com.iwip.harbor.plc.PLCDataProcessContext;
import com.iwip.harbor.service.IPLCScheduledTaskService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author taoqz
 * @create 2025-04-14
 */
@Slf4j
@Service
public class PLCScheduledTaskServiceImpl implements IPLCScheduledTaskService {

    @Autowired
    private DockPlanMapper dockPlanMapper;

    @Autowired
    private ScheduledTasksMapper scheduledTasksMapper;

    @Autowired
    private RedisCache redisCache;

    private ScheduledThreadPoolExecutor scheduler;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>(); // 任务管理

    @PostConstruct
    public void init() {
        scheduler = new ScheduledThreadPoolExecutor(
                0,  // 允许动态调整线程数
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // 任务过载时，直接在调用线程运行，保证不会丢失任务
        );
        scheduler.setRemoveOnCancelPolicy(true); // 任务取消后自动移除
        scheduler.allowCoreThreadTimeOut(true); // 允许核心线程回收，减少资源占用

        // **系统启动时恢复所有任务**
        List<ScheduledTasks> runningTasks = scheduledTasksMapper.getRunningTasks(); // 查询数据库中的任务
        if (!runningTasks.isEmpty()){
            List<Long> dockPlanIds = runningTasks.stream().map(ScheduledTasks::getPlanId).toList();
            List<Long> continuePlanIds = dockPlanMapper.selectDockPlanWithTaskByIds(dockPlanIds);
            for (ScheduledTasks task : runningTasks) {
                if (continuePlanIds.contains(task.getPlanId())) {
                    scheduledTasksMapper.updateScheduledTasks(task.getPlanId(), "FINISH");
                    // String redisKey = String.format(RedisKeyConstants.PLC_PLAN_REAL_WEIGHT, task.getPlanId(), task.getBerthName());
                    // redisCache.deleteObject(redisKey);
                    continue;
                }
                startTask(task.getPlanId(), task.getBerthName());
            }
        }

    }

    /**
     * 开始执行
     * @param planId 任务 ID
     */
    @Override
    public void startTask(Long planId, String berthName) {
        log.info("准备启动任务: 泊位：{} planId={} ", berthName, planId);
        // 如果已有任务，先停止
        if (taskMap.containsKey(planId)) {
            stopTask(planId,berthName);
            try {
                Thread.sleep(100); // 等待 100ms，确保线程池释放资源
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {
            try {

                IPLCDataProcess plcDataProcess = PLCDataProcessContext.getByBerthName(berthName);
                Map<String, BigDecimal> plcMap = plcDataProcess.readPlc();

                log.info("读取结果: 泊位：{} planId={} {}", berthName, planId, plcMap);


                if (plcMap != null) {
                    BigDecimal realTimeDate = plcMap.get(PLCConstans.REAL_TIME_DATE);
                    BigDecimal sumWeightDate = plcMap.get(PLCConstans.SUM_WEIGHT_DATE);

                    if (realTimeDate == null) {
                        log.info("没有读取到信息，结束方法。 泊位：{} planId={} {}", berthName, planId, plcMap);
                        return; // 没读到直接返回
                    }

                    String redisKey = "PLC:" + planId + "_" + berthName;
                    Map<String, Object> redisMap = redisCache.getCacheMap(redisKey);

                    if (realTimeDate.compareTo(BigDecimal.valueOf(50)) < 0) {
                        // 低于 50 说明皮带秤暂停，只记录实时流量，不累加归零吨重 然后返回
                        redisMap.put(PLCConstans.REAL_TIME_DATE, realTimeDate);
                        redisCache.setCacheMap(redisKey, redisMap);
                        return;
                    }

                    // 读取 Redis 缓存的旧值，初始化 map
                    redisMap = (redisMap == null || redisMap.isEmpty()) ? new HashMap<>() : redisMap;

                    // 读取上一条船的重量
                    String lastTimeWeightRedisKey = String.format(RedisKeyConstants.LAST_TIME_WEIGHT, berthName);
                    Object lastTimeWeight = redisCache.getCacheObject(lastTimeWeightRedisKey);
                    // 当前重量 = 上一次累计的重量 - 当前plc重量
                    BigDecimal currentWeight = sumWeightDate.subtract(BigDecimal.valueOf(Double.parseDouble(lastTimeWeight.toString())));
                    redisMap.put(PLCConstans.PLC_SUM_WEIGHT_DATE, sumWeightDate);
                    redisMap.put(PLCConstans.REAL_TIME_DATE, realTimeDate);
                    redisMap.put(PLCConstans.CURRENT_WEIGHT, currentWeight);
                    redisMap.put(PLCConstans.LAST_TIME_WEIGHT, lastTimeWeight);

                    redisCache.setCacheMap(redisKey, redisMap);

                }

            } catch (Exception e) {
                log.error("定时任务执行异常: ", e);
            }
        }, 0, 5, TimeUnit.SECONDS);

        taskMap.put(planId, future);
        log.info("任务启动成功: {}", taskMap.keySet());

        // **存入数据库**
        ScheduledTasks tasks = scheduledTasksMapper.selectScheduledTasksByDuIdAndName(planId,berthName);
        if (tasks == null){
            ScheduledTasks scheduledTasks = new ScheduledTasks();
            scheduledTasks.setPlanId(planId);
            scheduledTasks.setBerthName(berthName);
            scheduledTasks.setStatus("RUNNING");
            scheduledTasksMapper.insertScheduledTasks(scheduledTasks);
        }else {
            scheduledTasksMapper.updateScheduledTasks(planId,"RUNNING");
        }
        // **动态调整核心线程数，确保每个任务都有自己的线程**
        scheduler.setCorePoolSize(taskMap.size());
    }


    /**
     * 停止任务
     * @param planId 任务 ID
     */
    @Override
    public void stopTask(Long planId,String berthName) {


        ScheduledFuture<?> future = taskMap.remove(planId);
        if (future != null) {
            future.cancel(true);
            log.info("定时任务 duId={} 已停止", planId);

        } else {
            log.debug("未找到对应的定时任务: duId={}", planId);
        }
        ScheduledTasks scheduledTasks = scheduledTasksMapper.selectScheduledTasksByDuIdAndName(planId, berthName);
        if (scheduledTasks == null || StringUtils.equals("FINISH",scheduledTasks.getStatus())){
            return;
        }
        // **更新数据库状态**
        scheduledTasksMapper.updateScheduledTasks(planId,"FINISH");
        // **优化线程池管理**
        int newSize = Math.max(taskMap.size(), 2); // 至少保持 2 个核心线程
        scheduler.setCorePoolSize(newSize);
        // 如果所有任务都停止了，关闭线程池
        if (taskMap.isEmpty()) {
            scheduler.setCorePoolSize(1); // 降到最小线程数，而不是直接关闭
            log.info("所有定时任务已停止，线程池保持最低负载");
        }
    }

}
