package com.iwip.harbor.service.impl;

import com.iwip.common.annotation.DataScope;
import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.domain.BaseEntity;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.param.AppPierPlanParam;
import com.iwip.harbor.domain.screen.ScreenPlanStatusVo;
import com.iwip.harbor.domain.screen.ScreenShipArrivalVo;
import com.iwip.harbor.domain.vo.AppDockBerthStatusStatisticsVo;
import com.iwip.harbor.domain.vo.AppPierPlanVo;
import com.iwip.harbor.domain.vo.AppPlanStatusVo;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.plc.PLCConstans;
import com.iwip.harbor.service.IAppStatisticsService;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IDockPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.math.RoundingMode.HALF_UP;

/**
 * @author taoqz
 * @create 2025-04-14
 */
@Service
public class AppStatisticsServiceImpl implements IAppStatisticsService {

    @Autowired
    private IDockBerthService dockBerthService;

    @Autowired
    private IDockPlanService dockPlanService;

    @Autowired
    private DockPlanMapper dockPlanMapper;

    @Autowired
    private RedisCache redisCache;

    private static final Map<String, BigDecimal> RANGE_STANDARD_TYPE1MAP = new HashMap<>();   // 大码头 1号泊位
    private static final Map<String, BigDecimal> RANGE_STANDARD_TYPE2MAP = new HashMap<>();   // 驳船码头


    static {
        RANGE_STANDARD_TYPE1MAP.put("70-90", BigDecimal.valueOf(2300));     // 1、4、8号泊位暗仓阶段
        RANGE_STANDARD_TYPE1MAP.put("90-100", BigDecimal.valueOf(1300));    // 1、4、8号泊位清仓阶段
    }

    static {
        RANGE_STANDARD_TYPE2MAP.put("0-10", BigDecimal.valueOf(140));
        RANGE_STANDARD_TYPE2MAP.put("10-20", BigDecimal.valueOf(200));
        RANGE_STANDARD_TYPE2MAP.put("20-30", BigDecimal.valueOf(240));
        RANGE_STANDARD_TYPE2MAP.put("30-40", BigDecimal.valueOf(260));
        RANGE_STANDARD_TYPE2MAP.put("40-50", BigDecimal.valueOf(300));
        RANGE_STANDARD_TYPE2MAP.put("50-60", BigDecimal.valueOf(320));
        RANGE_STANDARD_TYPE2MAP.put("60-70", BigDecimal.valueOf(350));
        RANGE_STANDARD_TYPE2MAP.put("70-90", BigDecimal.valueOf(380));
//        RANGE_STANDARD_TYPE2MAP.put("80-90", BigDecimal.valueOf(380));
        RANGE_STANDARD_TYPE2MAP.put("90-100", BigDecimal.valueOf(370));
    }




    @Override
    public List<AppDockBerthStatusStatisticsVo> appBerthStatusStatistics() {
        return dockBerthService.appBerthStatusStatistics(new DockBerth());
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<AppPierPlanVo> selectPierPlanList(AppPierPlanParam appPierPlanParam) {
       List<AppPierPlanVo> appPierPlanVoList = dockPlanMapper.selectAppPierPlanList(appPierPlanParam);

        LocalDateTime updateWeightTime = LocalDateTime.now();
        for (AppPierPlanVo appPierPlanVo : appPierPlanVoList) {
            // 计划单已卸重量
            BigDecimal unloadedWeightPlan = appPierPlanVo.getUnloadWeight() != null
                    ? new BigDecimal(appPierPlanVo.getUnloadWeight().toString())
                    : BigDecimal.ZERO;            // 总重量
            BigDecimal tonnage = new BigDecimal(StringUtils.isNotBlank(appPierPlanVo.getTonnage()) ? appPierPlanVo.getTonnage() : "0");
            // 归零
            BigDecimal currentWeight = BigDecimal.ZERO;

            // 1# 泊位加入读取皮带秤
            if (StringUtils.equals("1#", appPierPlanVo.getBerthName()) || StringUtils.equals("4#", appPierPlanVo.getBerthName())) {
                String redisKey = "PLC:" + appPierPlanVo.getPlanId() + "_" + appPierPlanVo.getBerthName();
                Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);

                // 显式转换
                Object value = redisMap.get(PLCConstans.CURRENT_WEIGHT);
                currentWeight = new BigDecimal(value != null ? value.toString() : "0");  // 如果 value 为 null，默认赋值 "0"
            }

            // 计算卸货进度
            // 已卸重量 = 计划单已卸重量 + PLC 归零重量

            BigDecimal unloadWeight = unloadedWeightPlan.add((currentWeight));
            // 已卸重量
            appPierPlanVo.setUnloadWeight(unloadWeight);
            // 剩余重量
            appPierPlanVo.setRemainingWeight(tonnage.subtract(unloadWeight));

            if (unloadWeight.compareTo(tonnage) == 0 || unloadWeight.compareTo(tonnage) > 0) {
                appPierPlanVo.setProgress("100.00"); // 设置进度（字符串格式）
            }else {
                // 计算进度 (百分比)
                BigDecimal progress = unloadWeight.divide(tonnage, 2, HALF_UP)
                        .multiply(new BigDecimal("100")); // 转换为百分比
                appPierPlanVo.setProgress(progress.toPlainString()); // 设置进度（字符串格式）
            }

            // 计算平均效率 (吨/小时)
            LocalDateTime operationTime = appPierPlanVo.getOperationTime(); // 作业开始时间

            // 平均效率 = 已卸重量（总重量 - 剩余重量） /  用时（小时）
            if (operationTime != null) {
                // 计算时间差（单位：小时）
                long minutes = Duration.between(operationTime, updateWeightTime).toMinutes();
                BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal("60"), 4, HALF_UP);

                if (hours.compareTo(BigDecimal.ZERO) > 0) {
                    // 计算效率（吨/小时）
                    BigDecimal avgDischargeRate = unloadWeight
                            .divide(hours, 2, HALF_UP); // 保留2位小数
                    appPierPlanVo.setAvgDischargeRate(avgDischargeRate.toPlainString());
                } else {
                    appPierPlanVo.setAvgDischargeRate("0"); // 避免除零错误
                }
            } else {
                appPierPlanVo.setAvgDischargeRate("0"); // 无法计算时，设为0
            }

            /**
             * 处理大码头、驳船码头各个阶段低于标准维护原因逻辑
             */

            // 平均斜率
            BigDecimal avgDischargeRate = extractFirstNumber(appPierPlanVo.getAvgDischargeRate());
            // 进度
            BigDecimal progress = extractFirstNumber(appPierPlanVo.getProgress());

            boolean isBigPier = "1".equals(appPierPlanParam.getPierType());
            boolean isBargePier = "2".equals(appPierPlanParam.getPierType());
            String berthName = appPierPlanVo.getBerthName();
            Long  planId = appPierPlanVo.getPlanId();

            if (isBigPier && ("1#".equals(berthName) || "4#".equals(berthName) || "8#".equals(berthName))) {
                handleBigPierLogic(avgDischargeRate, progress, planId, berthName, appPierPlanVo);
            }

            if (isBargePier) {
                handleBargePierLogic(avgDischargeRate, progress, planId, berthName, appPierPlanVo);
            }
        }
       return appPierPlanVoList;
    }

    // 通用异常设置方法1
    private void setExceptionIfNeeded(boolean condition, String redisType, Long  planId, String berthName, String range, AppPierPlanVo vo) {
        if (condition) {
            vo.setExceptionFlag(true);
            String key = String.format(RedisKeyConstants.PROGRESS_EXCEPTION_REASON, redisType, planId, berthName);
            Map<String, Object> cacheMap = redisCache.getCacheMap(key);
            if (cacheMap != null && !cacheMap.isEmpty()) {
                Object reason = cacheMap.get(range);
                if (reason != null) {
                    vo.setExceptionReason(reason.toString());
                }
            }
        } else {
            vo.setExceptionFlag(false);
        }
    }
    // 大码头异常设置方法2
    private void handleBigPierLogic(BigDecimal rate, BigDecimal progress, Long planId, String berthName, AppPierPlanVo vo) {
        if (progress.compareTo(BigDecimal.ZERO) >= 0 && progress.compareTo(new BigDecimal("70")) < 0) {
            BigDecimal threshold = "1#".equals(berthName) ? BigDecimal.valueOf(3500) : BigDecimal.valueOf(3000);
            setExceptionIfNeeded(rate.compareTo(threshold) < 0, "type1", planId, berthName, "0-70", vo);
        } else {
            for (Map.Entry<String, BigDecimal> entry : RANGE_STANDARD_TYPE2MAP.entrySet()) {
                String range = entry.getKey();
                BigDecimal maxValue = entry.getValue();
                String[] bounds = range.split("-");
                BigDecimal lower = new BigDecimal(bounds[0]);
                BigDecimal upper = new BigDecimal(bounds[1]);
                if (progress.compareTo(lower) >= 0 && progress.compareTo(upper) < 0) {
                    setExceptionIfNeeded(rate.compareTo(maxValue) < 0, "type2", planId, berthName, range, vo);
                    break;
                }
            }
        }
    }
    // 驳船码头异常设置方法3
    private void handleBargePierLogic(BigDecimal rate, BigDecimal progress, Long planId, String berthName, AppPierPlanVo vo) {
        for (Map.Entry<String, BigDecimal> entry : RANGE_STANDARD_TYPE1MAP.entrySet()) {
            String range = entry.getKey();
            BigDecimal maxValue = entry.getValue();
            String[] bounds = range.split("-");
            BigDecimal lower = new BigDecimal(bounds[0]);
            BigDecimal upper = new BigDecimal(bounds[1]);
            if (progress.compareTo(lower) >= 0 && progress.compareTo(upper) < 0) {
                setExceptionIfNeeded(rate.compareTo(maxValue) < 0, "type2", planId, berthName, range, vo);
                break;
            }
        }
    }


    @Override
    public AppPlanStatusVo selectPlanStatusList() {
        List<ScreenPlanStatusVo> screenPlanStatusVos = dockPlanService.screenPlanStatusList(new BaseEntity());
        AppPlanStatusVo appPlanStatusVo = new AppPlanStatusVo();
        appPlanStatusVo.setCount(screenPlanStatusVos.stream().mapToInt(ScreenPlanStatusVo::getCount).sum());
        appPlanStatusVo.setPlanStatusChildren(screenPlanStatusVos);
        return appPlanStatusVo; // 计划单状态分布
    }

    @Override
    public List<ScreenShipArrivalVo> shipArrivalStatistics() {
        return dockPlanService.screenShipArrivalStatistics(); // 今日/明日到船（物资）统计
    }

    @Override
    public Map<String, String> throughputStatistics() {
        return dockPlanService.screenThroughput(new DockPlan()); // 当日/当月累计吞吐量
    }



    private BigDecimal extractFirstNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return new BigDecimal(matcher.group());
        }
        return BigDecimal.ZERO;
    }

}
