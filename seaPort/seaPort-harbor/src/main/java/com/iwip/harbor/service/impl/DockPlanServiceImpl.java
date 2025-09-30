package com.iwip.harbor.service.impl;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.iwip.common.annotation.DataScope;
import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.domain.BaseEntity;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.common.utils.bean.BeanUtils;
import com.iwip.common.utils.http.HttpUtils;
import com.iwip.harbor.domain.*;
import com.iwip.harbor.domain.excel.DockPlanExcel;
import com.iwip.harbor.domain.excel.DockPlanImportExcel;
import com.iwip.harbor.domain.excel.DockPlanRateExcel;
import com.iwip.harbor.domain.excel.DockUnloadWeighExcel;
import com.iwip.harbor.domain.screen.*;
import com.iwip.harbor.domain.vo.DockMaterialVo;
import com.iwip.harbor.domain.vo.ScreenProgressVo;
import com.iwip.harbor.mapper.*;
import com.iwip.harbor.plc.IPLCDataProcess;
import com.iwip.harbor.plc.PLCConstans;
import com.iwip.harbor.plc.PLCDataProcessContext;
import com.iwip.harbor.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import static com.iwip.common.utils.DateUtils.durationToHours;
import static com.iwip.harbor.service.impl.DockUnloadWorkServiceImpl.formatDuration;
import static com.iwip.harbor.service.impl.DockUnloadWorkServiceImpl.parsePauseInterval;
import static java.math.RoundingMode.HALF_UP;

/**
 * 计划单Service业务层处理
 *
 * @author Fei
 * @date 2025-01-28
 */
@Slf4j
@Service
public class DockPlanServiceImpl implements IDockPlanService {
    @Autowired
    private DockPlanMapper dockPlanMapper;

    @Autowired
    private DockBerthMapper dockBerthMapper;

    @Autowired
    private IDockUnloadWorkService dockUnloadWorkService;

    @Autowired
    private IDockUnloadDetailService dockUnloadDetailService;

    @Autowired
    private IDockMaterialService dockMaterialService;

    @Autowired
    private IDockPierService dockPierService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ScheduledTasksMapper scheduledTasksMapper;

    @Autowired
    private DockPlanAssistantMapper dockPlanAssistantMapper;

    @Autowired
    private IDockBerthService dockBerthService;

    @Autowired
    private IPLCScheduledTaskService plcTaskService;

    @Autowired
    private DockUnloadWorkMapper dockUnloadWorkMapper;

    @Autowired
    private DockWindowPeriodMapper dockWindowPeriodMapper;

    @Autowired
    private DockPlanUnloadWeightUpdateLogsMapper dpuwulm;
    private final String allowSubMaterial = "allowSubMaterial";
    private final String materialParams = "subMaterial";
    private final String collectFee = "collectFee";
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 查询计划单
     *
     * @param id 计划单主键
     * @return 计划单
     */
    @Override
    public DockPlan selectDockPlanById(Long id) {
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
        if (dockPlan == null) {
            return null;
        }
        // 通过 hbId 获取 DockBerth
        DockBerth berth = dockBerthMapper.selectDockBerthByDbId(dockPlan.getHbId());
        if (berth == null) {
            return dockPlan;
        }
        // 通过 berthHpId 获取 DockPier 并设置 pierType
        Optional.ofNullable(dockPierService.selectDockPierByDpId(berth.getBerthHpId()))
                .ifPresent(dockPier -> dockPlan.setPierType(dockPier.getPierType()));

        List<Long> planIds = new ArrayList<>();
        planIds.add(dockPlan.getId());
        List<DockPlanAssistant> dpas = dockPlanAssistantMapper.selectByBatchPlanIds(planIds);
        HashMap<String,Object> map = new HashMap<>();
        map.put(materialParams,dpas);
        dockPlan.setParams(map);
        return dockPlan;
    }

    /**
     * 查询计划单列表
     *
     * @param dockPlan 计划单
     * @return 计划单
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DockPlan> selectDockPlanList(DockPlan dockPlan) {
        List<DockPlan> dockPlans = dockPlanMapper.selectDockPlanLeftPierList(dockPlan);
        if(dockPlans.size()>0){
            List<Long> planIds = new ArrayList<>();
            for(DockPlan dp : dockPlans) {
                planIds.add(dp.getId());
            }
            List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectByBatchPlanIds(planIds);
            Map<Long,List<DockPlanAssistant>> DPAMap = new HashMap<>();
            for(DockPlanAssistant dockPlanAssistant : dockPlanAssistants) {
                if(!DPAMap.containsKey(dockPlanAssistant.getPlanId())) {
                    DPAMap.put(dockPlanAssistant.getPlanId(), new ArrayList<>());
                }
                DPAMap.get(dockPlanAssistant.getPlanId()).add(dockPlanAssistant);
            }
            dockPlans.forEach(plan -> {
                HashMap<String,Object> map = new HashMap<>();
                map.put(materialParams,DPAMap.get(plan.getId()));
                map.put(collectFee,calculateCollectFee(plan.getId(),plan.getDockingTime(),plan.getOutBerthTime(),plan.getContractFee()));
                plan.setParams(map);
            });
        }
        return dockPlans;
    }


    @Override
    @DataScope(deptAlias = "d")
    public Map<Object, Object> summaryCalculation(DockPlan dockPlan) {
        // 需要查询全部数据计算
        List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlan);

        Map<Object, Object> map = new HashMap<>();

        if (dockPlanList.isEmpty()) {
            return map;
        }
        // 船数
        long shiftCount = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9"))
                .map(DockPlan::getShipName)
                .count();
        // 总重量（排除状态为 抛泊 和 移泊）
        BigDecimal totalWeight = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9")) // 排除状态 8 和 9
                .map(DockPlan::getTonnage)
                .filter(StringUtils::isNotBlank)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 累计平均时间
        BigDecimal totalHours = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9") && x.getEndTime() != null) // 排除状态 8 和 9
                .map(x -> durationToHours(parsePauseInterval(DateUtils.timeDiff(x.getOperationTime(), x.getEndTime())))) // 计算时间差，返回时分秒格式字符串
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加

        // 累计有效时间
        BigDecimal totalEffectiveHours = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9") && x.getEffectiveTime() != null) // 排除状态 8 和 9
                .map(x -> durationToHours(parsePauseInterval(x.getEffectiveTime()))) // 计算时间差，返回时分秒格式字符串
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加

        // 滞期费
        BigDecimal totalDemurrageFee = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9")) // 排除状态 8 和 9
                .filter(x -> Objects.equals(x.getPlanType(), "1")) // 排除状态 8 和 9
                .map(DockPlan::getDemurrageFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 速遣费
        BigDecimal totalDispatchFee = dockPlanList.stream()
                .filter(x -> !StringUtils.equalsAny(x.getStatus(), "8", "9")) // 排除状态 8 和 9
                .filter(x -> Objects.equals(x.getPlanType(), "2")) // 排除状态 8 和 9
                .map(DockPlan::getDemurrageFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 平均卸率 = 总重量 / 累加总时间（避免除以 0）
        BigDecimal avgRate = totalHours.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalWeight.divide(totalHours, 2, RoundingMode.HALF_UP);

        // 有效卸率 = 总重量 / 累加有效时间（避免除以 0）
        BigDecimal effectiveRate = totalEffectiveHours.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalWeight.divide(totalEffectiveHours, 2, RoundingMode.HALF_UP);

        map.put("shipCount", shiftCount);
        map.put("totalWeight", totalWeight);
        map.put("avgRate", avgRate);
        map.put("effectiveRate", effectiveRate);
        map.put("totalDemurrageFee", totalDemurrageFee);
        map.put("totalDispatchFee", totalDispatchFee);

        return map;

    }

    @Override
    public List<ScreenWorkPlan> screenPierPlanList(String periType) {
        List<ScreenWorkPlan> workPlanList = dockPlanMapper.screenPierPlanList(periType);

        LocalDateTime updateWeightTime = LocalDateTime.now();
        for (ScreenWorkPlan screenWorkPlan : workPlanList) {
            String uploadSpeedUnit = getUploadSpeedUnit(screenWorkPlan.getMaterialName());
            // 计划单已卸重量
            BigDecimal unloadedWeightPlan = StringUtils.isNotBlank(screenWorkPlan.getUnloadWeight())
                    ? new BigDecimal(screenWorkPlan.getUnloadWeight())
                    : BigDecimal.ZERO;            // 总重量
            BigDecimal tonnage = new BigDecimal(StringUtils.isNotBlank(screenWorkPlan.getTonnage()) ? screenWorkPlan.getTonnage() : "0");
            // 归零
            BigDecimal currentWeight = BigDecimal.ZERO;

            // 1# 泊位加入读取皮带秤
            if (StringUtils.equals("1#", screenWorkPlan.getBerthName()) || StringUtils.equals("4#", screenWorkPlan.getBerthName())) {
                String redisKey = "PLC:" + screenWorkPlan.getPlanId() + "_" + screenWorkPlan.getBerthName();
                Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);

                // 显式转换
                Object value = redisMap.get(PLCConstans.CURRENT_WEIGHT);
                currentWeight = new BigDecimal(value != null ? value.toString() : "0");  // 如果 value 为 null，默认赋值 "0"
            }

            // 计算卸货进度
            // 已卸重量 = 计划单已卸重量 + PLC 归零重量

            BigDecimal unloadWeight = unloadedWeightPlan.add((currentWeight));
            if (unloadWeight.compareTo(tonnage) == 0 || unloadWeight.compareTo(tonnage) > 0) {
                screenWorkPlan.setProgress("100.00"); // 设置进度（字符串格式）
            } else {
                // 计算进度 (百分比)
                BigDecimal progress = unloadWeight.divide(tonnage, 2, HALF_UP)
                        .multiply(new BigDecimal("100")); // 转换为百分比
                screenWorkPlan.setProgress(progress.toPlainString()); // 设置进度（字符串格式）
            }

            // 计算平均效率 (吨/小时)
            LocalDateTime operationTime = screenWorkPlan.getOperationTime(); // 作业开始时间

            // 平均效率 = 已卸重量（总重量 - 剩余重量） /  用时（小时）
            if (operationTime != null) {
                // 计算时间差（单位：小时）
                long minutes = Duration.between(operationTime, updateWeightTime).toMinutes();
                BigDecimal hours = new BigDecimal(minutes).divide(new BigDecimal("60"), 4, HALF_UP);

                if (hours.compareTo(BigDecimal.ZERO) > 0) {
                    // 计算效率（吨/小时）
                    BigDecimal avgDischargeRate = unloadWeight
                            .divide(hours, 2, HALF_UP); // 保留2位小数
                    screenWorkPlan.setAvgDischargeRate(avgDischargeRate.toPlainString() + uploadSpeedUnit);
                } else {
                    screenWorkPlan.setAvgDischargeRate("0" + uploadSpeedUnit); // 避免除零错误
                }
            } else {
                screenWorkPlan.setAvgDischargeRate("0" + uploadSpeedUnit); // 无法计算时，设为0
            }
            /**
             * 处理大码头、驳船码头各个阶段低于标准维护原因逻辑
             */
            // 平均斜率
            BigDecimal avgDischargeRate = extractFirstNumber(screenWorkPlan.getAvgDischargeRate());
            // 进度
            BigDecimal progress = extractFirstNumber(screenWorkPlan.getProgress());

            boolean isBigPier = "1".equals(periType);
            boolean isBargePier = "2".equals(periType);
            String berthName = screenWorkPlan.getBerthName();
            Long planId = screenWorkPlan.getPlanId();

            if (isBigPier && ("1#".equals(berthName) || "4#".equals(berthName) || "8#".equals(berthName))) {
                handleBigPierLogic(avgDischargeRate, progress, planId, berthName, screenWorkPlan);
            }

            if (isBargePier) {
                handleBargePierLogic(avgDischargeRate, progress, planId, berthName, screenWorkPlan);
            }
        }
        return workPlanList;
    }

    // 通用异常设置方法1
    private void setExceptionIfNeeded(boolean condition, String redisType, Long planId, String berthName, String range, ScreenWorkPlan vo) {
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
    private void handleBigPierLogic(BigDecimal rate, BigDecimal progress, Long planId, String berthName, ScreenWorkPlan vo) {
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
    private void handleBargePierLogic(BigDecimal rate, BigDecimal progress, Long planId, String berthName, ScreenWorkPlan vo) {
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


    private static final String API_URL = "http://api.shipxy.com/apicall/GetAreaShip";
    private static final String API_KEY = "d23ec035147440efa2ee3bfe95bff3fd";


    public List<ScreenGeoJsonVo> selectGeoJsonList() {
        String genJson = redisCache.getCacheObject("screenGeoJson");
        String url = API_URL + "?v=2&k=" + API_KEY + "&enc=1&scode=0&xy=" + genJson;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String response = HttpUtils.sendGet(url);
            log.info("船讯网 API 响应: {}", response);
            Map<String, Object> map = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            String status = String.valueOf(map.get("status"));
            if (!"0".equals(status)) {
                throw new ServiceException("没有成功调用船讯网" + map);
            } else {
                return parseResponse(map);
            }

        } catch (Exception e) {
            log.error("调用船讯网 API 发生异常", e);
            throw new ServiceException("调用船讯网 API 发生异常" + e);
        }
    }

    /**
     * 归档
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int archived(Long[] ids, String archivedMonth) {
        for (Long id : ids) {
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
            if (dockPlan == null) {
                throw new ServiceException("操作失败，没有查询到数据");
            }
//            if (!StringUtils.equals("2",dockPlan.getUnloadStatus())){
//                throw new ServiceException("卸货单未完成，不可以操作归档！");
//            }
            dockPlan.setIsArchived("1"); // 归档
            dockPlan.setArchivedMonth(archivedMonth);
            dockPlanMapper.updateDockPlan(dockPlan);
        }
        return 1;

    }

    @Override
    public int cancelArchived(Long[] ids) {
        for (Long id : ids) {
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
            if (dockPlan == null) {
                throw new ServiceException("操作失败，没有查询到数据");
            }
            dockPlan.setIsArchived("0"); // 归档
            dockPlan.setArchivedMonth(null);
            dockPlanMapper.updateDockPlan(dockPlan);
        }
        return 1;
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<DockPlanRateExcel> effectiveRateExport(BaseEntity baseEntity) {
        List<DockPlan> dockPlanList = dockPlanMapper.selectEffectiveRateList(baseEntity);
        return dockPlanList.stream().map(x -> {
            DockPlanRateExcel rateExcel = new DockPlanRateExcel();
            BeanUtils.copyProperties(x, rateExcel);
            rateExcel.setEffectiveTime(null);
            return rateExcel;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importEffectiveRate(List<DockPlanRateExcel> planRateExcelList) {
        if (planRateExcelList.isEmpty()) {
            throw new ServiceException("导入失败，没有查询到数据；");
        }

        int count = 1;
        int row = 0;
        for (DockPlanRateExcel rateExcel : planRateExcelList) {
            count++;

            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(rateExcel.getId());
            if (dockPlan == null) {
                throw new ServiceException("导入失败，第" + count + "行没有查询到数据；");
            }

            if (rateExcel.getEffectiveTime() == null) {
                continue;
            }

            Duration duration = parsePauseInterval(rateExcel.getEffectiveTime());


            BigDecimal effectiveTime = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);

            BigDecimal tonnage = new BigDecimal(dockPlan.getTonnage());
            if (tonnage.compareTo(BigDecimal.ZERO) == 0 || effectiveTime.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            BigDecimal effectiveRate = tonnage.divide(effectiveTime, 2, HALF_UP);
            dockPlan.setEffectiveRate(effectiveRate);
            String effectiveTim = formatDuration(duration);
            dockPlan.setEffectiveTime(effectiveTim);

            // 修改有效时间有效卸率
            row += dockPlanMapper.updateDockPlanRate(dockPlan);
        }
        return row;

    }

    @Override
    public int updateDockPlanRate(DockPlan dockPlan) {
        return dockPlanMapper.updateDockPlanRate(dockPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int appEdit(DockPlan dockPlan) {

        DockPlan plan = dockPlanMapper.selectDockPlanById(dockPlan.getId());
        if (plan == null) {
            throw new ServiceException("操作失败，没有查询到计划单！");
        }


        boolean updatePlcSumWeightDateFlag = false;

        if (StringUtils.isNotBlank(dockPlan.getHbName())) {
            plan.setHbName(dockPlan.getHbName());
        }
        if (StringUtils.isNotBlank(dockPlan.getUnloadWeight())) {
            plan.setUnloadWeight(dockPlan.getUnloadWeight());
        }
        if (dockPlan.getArrivalTime() != null) {
            plan.setArrivalTime(dockPlan.getArrivalTime());
        }
        if (dockPlan.getDockingTime() != null) {
            plan.setDockingTime(dockPlan.getDockingTime());
        }
        if (dockPlan.getOperationTime() != null) {
            plan.setOperationTime(dockPlan.getOperationTime());
        }
        if (dockPlan.getEndTime() != null) {
            plan.setEndTime(dockPlan.getEndTime());
            updatePlcSumWeightDateFlag = true;
        }
        if (dockPlan.getOutBerthTime() != null) {
            plan.setOutBerthTime(dockPlan.getOutBerthTime());
            plan.setIsArchived("1");
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            plan.setArchivedMonth(currentDate.format(formatter));
            plan.setLeaveTime(dockPlan.getOutBerthTime().plusMinutes(1));
        }
        if (dockPlan.getLeaveTime() != null) {
            plan.setLeaveTime(dockPlan.getLeaveTime());
        }
        if (dockPlan.getPlanDockingTime() != null) {
            plan.setPlanDockingTime(dockPlan.getPlanDockingTime());
        }
        // 更新计划状态和泊位状态
        updatePlanStatus(plan);
        updateBerthStatus(plan);

        // TODO 1# 4# 8#泊位
        Set<Long> VALID_HB_IDS = Set.of(9L, 12L);
        // 待离泊状态结束读取皮带秤 记录当前皮带秤总累计量
        if (updatePlcSumWeightDateFlag && VALID_HB_IDS.contains(plan.getHbId())) {
            IPLCDataProcess plcDataProcess = PLCDataProcessContext.getByBerthName(dockPlan.getHbName());
            plcDataProcess.setLastTimeWeight(dockPlan.getHbName());
        }


        /**
         * 卸货校验
         */
        String newWeight = dockPlan.getUnloadWeight();
        String oldWeight = plan.getUnloadWeight();
        if (StringUtils.isBlank(newWeight)) {

        } else if (!Objects.equals(newWeight, oldWeight)) {

            // 如果新增的卸货数量不为空，redis也存入这条信息
            if (StringUtils.isNotBlank(dockPlan.getUnloadWeight())) {
                String redisKey = "PLC:" + dockPlan.getId() + "_" + dockPlan.getHbName();
                Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);
                if (redisMap != null && !redisMap.isEmpty()) {
                    redisMap.put("unloadWeight", BigDecimal.valueOf(Double.parseDouble(dockPlan.getUnloadWeight())));
                    redisCache.setCacheMap(redisKey, redisMap);
                }
            }
        }
        refreshWindowPeriodLog(plan);
        return dockPlanMapper.updateDockPlan(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startWork(DockStartWorkVo dockStartWorkVo) {

        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockStartWorkVo.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("操作失败，没有查询到计划单");
        }
        dockPlan.setUnloadStatus("1");
        dockPlan.setOperationTime(dockStartWorkVo.getOperationTime());
        // 更新计划状态和泊位状态
        updatePlanStatus(dockPlan);
        updateBerthStatus(dockPlan);

        // TODO 1# 4# 8#泊位
        Set<Long> VALID_HB_IDS = Set.of(9L, 12L);
        // 自动生成卸货单
        if ("4".equals(dockPlan.getStatus())) {

            // 需要触发装卸的状态集合
            // 如果是在装卸状态，开始读取PLC
//            if (dockPlan.getHbId() != null && VALID_HB_IDS.contains(dockPlan.getHbId())) {
//                plcTaskService.startTask(dockPlan.getId(), dockPlan.getHbName());
//            }

            List<DockUnloadWork> unloadWorkList = dockUnloadWorkService.selectUnloadWorkListByPlanId(dockPlan.getId());
            if (unloadWorkList.isEmpty()) {
                DockUnloadWork unloadWork = new DockUnloadWork();
                unloadWork.setStartTime(dockPlan.getOperationTime());
                unloadWork.setClasses(dockStartWorkVo.getClasses());
                unloadWork.setClassTime(dockStartWorkVo.getClassTime());
                unloadWork.setLeader(dockStartWorkVo.getLeader());
                unloadWork.setPlanId(dockPlan.getId());
                unloadWork.setWorkType("0"); // 进行中
                unloadWork.setCreateTime(LocalDateTime.now());
                unloadWork.setCreateBy(SecurityUtils.getUsername());
                unloadWork.setDeptId(SecurityUtils.getDeptId());
                unloadWork.setDelFlag("0");
                unloadWork.setRemark01("1");
                dockUnloadWorkService.createUnloadWork(unloadWork);
            }
        }
        // 待离泊状态结束读取皮带秤（状态为 5、6、7 且为指定泊位 ID）
//        if (Arrays.asList("5", "6", "7").contains(dockPlan.getStatus())
//                && VALID_HB_IDS.contains(dockPlan.getHbId())) {
//
//            plcTaskService.stopTask(dockPlan.getId(), dockPlan.getHbName());
//
//            // 结束之后删除 redis 并且把 PLC 数据存到数据库中
//            removeCacheEditPlc(dockPlan);
//        }


        return dockPlanMapper.updateDockPlan(dockPlan);
    }


    public void removeCacheEditPlc(DockPlan dockPlan) {
        String redisKey = "PLC:" + dockPlan.getId() + "_" + dockPlan.getHbName();
        Map<String, Object> redisMap = redisCache.getCacheMap(redisKey);
        BigDecimal realTimeDate;
//      // 读取旧值并转换  累计重量
        Object realTimeObject = redisMap.get(PLCConstans.REAL_TIME_DATE);
        if (realTimeObject != null) {
            realTimeDate = realTimeObject instanceof BigDecimal
                    ? (BigDecimal) realTimeObject
                    : new BigDecimal(realTimeObject.toString());
        } else {
            realTimeDate = BigDecimal.ZERO;
        }
        // 归零数据
        BigDecimal plcSumWeightDate;
        Object plcSumWeightDateObject = redisMap.get(PLCConstans.PLC_SUM_WEIGHT_DATE);
        if (plcSumWeightDateObject != null) {
            plcSumWeightDate = (plcSumWeightDateObject instanceof BigDecimal)
                    ? (BigDecimal) plcSumWeightDateObject
                    : new BigDecimal(plcSumWeightDateObject.toString());
        } else {
            plcSumWeightDate = BigDecimal.ZERO;
        }
        if (plcSumWeightDate.compareTo(BigDecimal.ZERO) > 0) {
            dockPlan.setPlcRealTime(realTimeDate);
            dockPlan.setPlcSumWeight(plcSumWeightDate);
        }

        boolean b = redisCache.deleteObject(redisKey);
    }


    private static final Map<String, String> SHIP_TYPE_MAP = new LinkedHashMap<>();

    static {
        // 按优先顺序定义关键字和对应类型
        SHIP_TYPE_MAP.put("MV", "0");       // 母船
        SHIP_TYPE_MAP.put("MT", "0");       // 母船
        SHIP_TYPE_MAP.put("KM", "0");       // 母船
        SHIP_TYPE_MAP.put("SPOB", "0");     // 母船
        SHIP_TYPE_MAP.put("LCT", "1");      // 自航船
        SHIP_TYPE_MAP.put("SPB", "1");      // 自航船
        SHIP_TYPE_MAP.put("BG", "2");       // 驳船
    }

    private List<ScreenGeoJsonVo> parseResponse(Map<String, Object> geoMap) {


        List<ScreenGeoJsonVo> planList = dockPlanMapper.screenGeoJsonPlanList();
        Object data = geoMap.get("data");
        if (data instanceof List) {
            List<Object> dataList = (List<Object>) data;

            List<Map<String, String>> resultList = dataList.stream()
                    .filter(obj -> obj instanceof Map) // 确保 obj 是 Map 类型
                    .map(obj -> {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        return map.entrySet().stream()
                                .filter(entry -> Arrays.asList("imo", "lat", "lon", "cog", "hdg", "rot").contains(entry.getKey()))
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue().toString(), // 将值转换为 String
                                        (v1, v2) -> v1 // 处理键冲突
                                ));
                    })
                    .toList();

            List<ScreenGeoJsonVo> geoList = new ArrayList<>();
            for (Map<String, String> map : resultList) {
                String imo = map.get("imo"); // 国际海事组织
                String lat = map.get("lat"); // 经度
                String lon = map.get("lon"); // 维度
                String cog = map.get("cog"); // 船痕向
                String hdg = map.get("hdg"); // 船首向
                String rot = map.get("rot"); // 船向率
                if ("9192686".equals(imo)) {
                    System.out.println("111");
                }

                List<ScreenGeoJsonVo> findGeoPlan = planList.stream().filter(x -> StringUtils.equals(x.getImo(), imo)).toList();
                if (!findGeoPlan.isEmpty()) {
                    ScreenGeoJsonVo geoJsonVo = new ScreenGeoJsonVo();

                    String upperName = findGeoPlan.get(0).getShipName().toUpperCase();
                    for (Map.Entry<String, String> entry : SHIP_TYPE_MAP.entrySet()) {
                        if (upperName.contains(entry.getKey())) {
                            geoJsonVo.setShipType(entry.getValue());  // 船类型
                        }
                    }

                    geoJsonVo.setTonnage(findGeoPlan.get(0).getTonnage()); // 重量
                    geoJsonVo.setHbName(findGeoPlan.get(0).getHbName());    // 泊位名称
                    geoJsonVo.setShipName(findGeoPlan.get(0).getShipName()); // 船名
                    geoJsonVo.setMaterialName(findGeoPlan.get(0).getMaterialName());  // 材料名称
                    geoJsonVo.setDockingTime(findGeoPlan.get(0).getDockingTime());  // 靠泊时间
                    geoJsonVo.setStatus(findGeoPlan.get(0).getStatus());  // 到港时间
                    geoJsonVo.setCog(cog);  // 船痕向
                    geoJsonVo.setHdg(hdg);  // 船首向
                    geoJsonVo.setRot(rot);  // 船向率
                    //  经纬度乘以 100 万 后得到的，所以要还原成标准的经纬度，只需要除以 1000000（即 10⁶）
                    double latitude = Double.parseDouble(lat) / 1_000_000.0;
                    double longitude = Double.parseDouble(lon) / 1_000_000.0;
                    geoJsonVo.setLat(latitude + ""); // 经度
                    geoJsonVo.setLon(longitude + ""); // 维度
                    geoList.add(geoJsonVo);
                }
            }
            return geoList;
        }
        return null;
    }


    /**
     * 新增计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDockPlan(DockPlan dockPlan) {
        LocalDateTime now = LocalDateTime.now();

        // 处理重量信息
        handleWeight(dockPlan);
        // 设置默认值
        dockPlan.setDelFlag("0");
        dockPlan.setSourceType("0");
        dockPlan.setUserId(SecurityUtils.getUserId());
        dockPlan.setCreateBy(SecurityUtils.getNickName());
        dockPlan.setDeptId(SecurityUtils.getDeptId());
        dockPlan.setCreateTime(now);
        // 修改计划单和泊位状态
        updatePlanStatus(dockPlan);
        updateBerthStatus(dockPlan);
        // 缓存 status，避免重复调用
        String status = dockPlan.getStatus();
        if ("4".equals(status)) {
            dockPlan.setUnloadStatus("1");
        }
        // 插入计划
        int result = dockPlanMapper.insertDockPlan(dockPlan);
        // TODO 1# 4# 8#泊位
        Set<Long> VALID_HB_IDS = Set.of(9L, 12L);

        // 自动生成卸货单
        if ("4".equals(status)) {
            // 如果是在装卸状态，开始读取PLC
            // 需要触发装卸的状态集合
            if (dockPlan.getHbId() != null && VALID_HB_IDS.contains(dockPlan.getHbId())) {
                plcTaskService.startTask(dockPlan.getId(), dockPlan.getHbName());

                // 如果新增的卸货数量不为空，redis也存入这条信息
                if (StringUtils.isNotBlank(dockPlan.getUnloadWeight())) {
                    String redisKey = "PLC:" + dockPlan.getId() + "_" + dockPlan.getHbName();
                    Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);
                    if (redisMap != null && !redisMap.isEmpty()) {
                        // redisMap.put("currentWeight", BigDecimal.ZERO);
                        redisMap.put("unloadWeight", BigDecimal.valueOf(Double.parseDouble(dockPlan.getUnloadWeight())));
                        redisCache.setCacheMap(redisKey, redisMap);
                    }
                }
            }

            DockUnloadWork unloadWork = new DockUnloadWork();
            unloadWork.setStartTime(dockPlan.getOperationTime());
            unloadWork.setPlanId(dockPlan.getId());
            unloadWork.setWorkType("0"); // 进行中
            unloadWork.setCreateTime(now);
            unloadWork.setDelFlag("0");
            unloadWork.setDeptId(SecurityUtils.getDeptId());
            dockUnloadWorkService.createUnloadWork(unloadWork);
        }

        return result;
    }

    /**
     * 处理重量信息
     */
    private void handleWeight(DockPlan dockPlan) {
        if (dockPlan.getUnloadWeight() != null && StringUtils.isNotBlank(dockPlan.getTonnage())) {
            BigDecimal totalWeight = new BigDecimal(dockPlan.getTonnage());
            BigDecimal unloadWeight = StringUtils.isNotBlank(dockPlan.getUnloadWeight()) ? new BigDecimal(dockPlan.getUnloadWeight()) : BigDecimal.ZERO;
            // 校验剩余重量不能超过总重量
            if (unloadWeight.compareTo(totalWeight) > 0) {
                throw new IllegalArgumentException("剩余重量不能超过总重量！");
            }
        }
    }


    /**
     * 修改计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDockPlan(DockPlan dockPlan) {
        LocalDateTime now = LocalDateTime.now();
        // 获取计划数据
        DockPlan planVo = dockPlanMapper.selectDockPlanById(dockPlan.getId());

        String newWeight = dockPlan.getUnloadWeight();
        String oldWeight = planVo.getUnloadWeight();

        // 处理重量更新逻辑
        if (StringUtils.isBlank(newWeight)) {
        } else if (!Objects.equals(newWeight, oldWeight)) {
            handleWeight(dockPlan);
        }

        // 更新操作人和更新时间
        dockPlan.setUpdateBy(SecurityUtils.getNickName());
        dockPlan.setUpdateTime(now);

        // 更新计划状态和泊位状态
        updatePlanStatus(dockPlan);
        updateBerthStatus(dockPlan);
        // TODO 1# 4# 8#泊位
        Set<Long> VALID_HB_IDS = Set.of(9L, 12L);

        // 自动生成卸货单
        if ("4".equals(dockPlan.getStatus())) {
            // 需要触发装卸的状态集合
            // 如果是在装卸状态，开始读取PLC
            if (dockPlan.getHbId() != null && VALID_HB_IDS.contains(dockPlan.getHbId())) {

                // PLC 逻辑处理
                plcTaskService.startTask(dockPlan.getId(), dockPlan.getHbName());
                // 1、在redis上获取 该泊位上的上次的总累计值 PLC_1#_lastTimeWeight
                Object lastTimeWeightObj = redisCache.getCacheObject(String.format(RedisKeyConstants.LAST_TIME_WEIGHT, dockPlan.getHbName()));
                // 2、编辑船舶计划单的作业开始时间时，开始读取plc 拿到plc的数据sumWeightDate - PLC_1#_lastTimeWeight 等于当前总累计
                // 3、现在累计的重量 = 当前皮带秤的总重量 - 该泊位上的上次的总累计值
                IPLCDataProcess plcDataProcess = PLCDataProcessContext.getByBerthName(dockPlan.getHbName());
                Map<String, BigDecimal> plcData = plcDataProcess.readPlc();

                BigDecimal plcSumWeightDate = plcData.get(PLCConstans.SUM_WEIGHT_DATE);

                String plcDataRedisKey = "PLC:" + dockPlan.getId() + "_" + dockPlan.getHbName();
                if (plcSumWeightDate != null && plcSumWeightDate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal lastTimeWeight = new BigDecimal(lastTimeWeightObj.toString());
                    BigDecimal currentWeight = plcSumWeightDate.subtract(lastTimeWeight);
                    // lastTimeWeight、plcSumWeightDate、currentWeight
                    Map<String, BigDecimal> map = new HashMap<>();
                    map.put(PLCConstans.LAST_TIME_WEIGHT, lastTimeWeight);
                    map.put(PLCConstans.PLC_SUM_WEIGHT_DATE, plcSumWeightDate);
                    map.put(PLCConstans.CURRENT_WEIGHT, currentWeight);
                    redisCache.setCacheMap(plcDataRedisKey, map);
                }


                if (StringUtils.isBlank(dockPlan.getUnloadWeight())) {

                } else if (!Objects.equals(newWeight, oldWeight)) {
                    // 如果填了卸货重量，把读取PLC的计算值归0，重新累加
                    if (StringUtils.isNotBlank(dockPlan.getUnloadWeight())) {
                        Map<String, BigDecimal> redisMap = redisCache.getCacheMap(plcDataRedisKey);
                        if (redisMap != null && !redisMap.isEmpty()) {
                            redisMap.put("unloadWeight", BigDecimal.valueOf(Double.parseDouble(dockPlan.getUnloadWeight())));
                            redisCache.setCacheMap(plcDataRedisKey, redisMap);
                        }
                    }
                }
            }
            List<DockUnloadWork> unloadWorkList = dockUnloadWorkService.selectUnloadWorkListByPlanId(dockPlan.getId());
            if (unloadWorkList.isEmpty()) {
                dockPlan.setUnloadStatus("1");
                DockUnloadWork unloadWork = new DockUnloadWork();
                unloadWork.setStartTime(dockPlan.getOperationTime());
                unloadWork.setPlanId(dockPlan.getId());
                unloadWork.setWorkType("0"); // 进行中
                unloadWork.setCreateTime(now);
                unloadWork.setDelFlag("0");
                dockUnloadWorkService.createUnloadWork(unloadWork);
            }
        }

        // 待离泊状态结束读取皮带秤 记录当前皮带秤总累计量
        if (!StringUtils.equals("5", planVo.getStatus()) && StringUtils.equals("5", dockPlan.getStatus()) && VALID_HB_IDS.contains(dockPlan.getHbId())) {
            IPLCDataProcess plcDataProcess = PLCDataProcessContext.getByBerthName(dockPlan.getHbName());
            plcDataProcess.setLastTimeWeight(dockPlan.getHbName());
        }
        // 待离泊状态结束读取皮带秤（状态为 5、6、7 且为指定泊位 ID）
        if (Arrays.asList("5", "6", "7").contains(dockPlan.getStatus())
                && VALID_HB_IDS.contains(dockPlan.getHbId())) {

            plcTaskService.stopTask(dockPlan.getId(), dockPlan.getHbName());

            // 结束之后删除 redis 并且把 PLC 数据存到数据库中
            removeCacheEditPlc(dockPlan);
        }

        return dockPlanMapper.updateDockPlan(dockPlan);
    }


    /**
     * 滞期费逻辑
     * 结束作业时间填写之后调用方法
     *
     * @param dockPlan
     */
    public void demurrage(DockPlan dockPlan) {
        /**
         * 1. 若（靠泊时间 - 到港时间）> 12h：
         *    滞期时间 = (结束作业时间 - 到港时间 - 12h) * 合同费率（美金/天）
         * 2. 若（靠泊时间 - 到港时间）≤ 12h：
         *    滞期时间 = (结束作业时间 - 作业开始时间) * 合同费率（美金/天）
         * 3. 计算合同时间：
         *    合同时间 = 总重量 / 合同卸率
         * 4. 计算速遣/滞期：
         *    - 若（合同时间 - 滞期时间）≥ 0：速遣，费用 = (合同时间 - 滞期时间) * 合同费率 * 0.5
         *    - 若（合同时间 - 滞期时间）< 0：滞期，费用 = (合同时间 - 滞期时间) * 合同费率
         *    按照这种算不通 =，=
         */

        // 如果是移泊或者移泊多次的情况，时间用第一次的数据,递归查询
        DockPlan firstDockPlan = getFirstDockPlan(dockPlan);
//        DockPlan firstDockPlan = dockPlanMapper.selectDockPlanById(dockPlan.getSourceId());
        // 获取时间字段
        LocalDateTime dockingTime = firstDockPlan.getDockingTime();
        LocalDateTime arrivalTime = firstDockPlan.getArrivalTime();
        LocalDateTime operationTime = firstDockPlan.getOperationTime();
        LocalDateTime endTime = dockPlan.getEndTime();


        if (BigDecimal.ZERO.equals(dockPlan.getContractFee())
                || BigDecimal.ZERO.equals(dockPlan.getContractRate())
                || StringUtils.isBlank(dockPlan.getTonnage())) {
            return;
        }

        // 获取合同相关数据
        BigDecimal contractRate = dockPlan.getContractRate(); // 合同卸率
        BigDecimal contractFee = dockPlan.getContractFee();   // 合同费率
        BigDecimal totalTonnage = new BigDecimal(dockPlan.getTonnage()); // 总重量

        // 参数校验
        if (Objects.isNull(contractRate) || contractRate.signum() == 0) {
            return;
        }
        if (Objects.isNull(contractFee) || contractFee.signum() == 0) {
            return;
        }
        if (Objects.isNull(totalTonnage) || totalTonnage.signum() == 0) {
            return;
        }
        /**
         * 完全按照excel公式来
         */

        // 是否超出12小时
        BigDecimal hoursSign = BigDecimal.ZERO;

        // 开始-结束时间（小时） - 一小时（测水尺用时）
        BigDecimal endHours_startHours = new BigDecimal(Duration.between(operationTime, endTime).toMinutes())
                .divide(BigDecimal.valueOf(60), 2, HALF_UP)
                .subtract(BigDecimal.ONE);
        // 到港靠泊时间（小时）
        BigDecimal arrivalHours_dockingHours = new BigDecimal(Duration.between(arrivalTime, dockingTime).toMinutes()).divide(BigDecimal.valueOf(60), 2, HALF_UP);

        // 到港-靠泊超出12小时扣除0.5天（小时）
        if (arrivalHours_dockingHours.compareTo(BigDecimal.valueOf(12)) >= 0) {
            hoursSign = BigDecimal.valueOf(12);
        }

        // 到港-结束时间（小时） 工作时长
        BigDecimal arrivalHours_endHours = new BigDecimal(Duration.between(arrivalTime, endTime)
                .toMinutes())
                .divide(BigDecimal.valueOf(60), 2, HALF_UP)
                .subtract(BigDecimal.ONE)
                .subtract(hoursSign);

        // 标准工作时长
        BigDecimal contractHour = totalTonnage.divide(contractRate, 10, HALF_UP) // 保持高精度
                .multiply(BigDecimal.valueOf(24))
                .setScale(2, HALF_UP); // 最后保留 2 位小数

        // 滞期/速谴(小时)
        BigDecimal demurrage_dispatch;
        if (hoursSign.compareTo(BigDecimal.valueOf(12)) >= 0) {
            demurrage_dispatch = contractHour.subtract(arrivalHours_endHours);
        } else {
            demurrage_dispatch = contractHour.subtract(endHours_startHours);
        }

        // 滞期/速谴(小时) 正数是速遣（需要乘以0.5）  负数是滞期
        if (demurrage_dispatch.signum() >= 0) {
            // 滞期/速谴(小时) / 24 * 合同费率 * 0.5
            BigDecimal demurrageFee = demurrage_dispatch.divide(BigDecimal.valueOf(24), 10, HALF_UP).multiply(contractFee).multiply(BigDecimal.valueOf(0.5));
            dockPlan.setPlanType("2"); // 速遣
            dockPlan.setDemurrageFee(demurrageFee);
        } else {
            // 滞期/速谴(小时) / 24 * 合同费率
            BigDecimal demurrageFee = demurrage_dispatch.divide(BigDecimal.valueOf(24), 10, HALF_UP).multiply(contractFee);
            dockPlan.setPlanType("1"); // 滞期
            dockPlan.setDemurrageFee(demurrageFee);
        }
    }


    /**
     * 递归查询原始数据
     *
     * @param dockPlan
     * @return
     */
    public DockPlan getFirstDockPlan(DockPlan dockPlan) {
        if (dockPlan == null || dockPlan.getSourceId() == null) {
            return dockPlan; // 如果本身没有来源，直接返回自己
        }

        DockPlan currentPlan = dockPlanMapper.selectDockPlanById(dockPlan.getSourceId());

        // 防止 sourceId 关联的数据查不到，避免 NPE
        if (currentPlan == null) {
            return dockPlan;
        }

        while (currentPlan.getSourceId() != null) {
            DockPlan nextPlan = dockPlanMapper.selectDockPlanById(currentPlan.getSourceId());
            if (nextPlan == null) {
                break; // sourceId 关联的 DockPlan 不存在，停止循环
            }
            currentPlan = nextPlan;
        }

        return currentPlan;
    }


    /**
     * 修改时间
     *
     * @param dockPlan
     */
    public void updatePlanStatus(DockPlan dockPlan) {
        /**
         * 离上港时间出来后，船舶从计划变为在途；
         * 到港时间确定后，船舶由在途变为等泊；
         * 靠泊时间确定后，船舶由等泊变为靠泊；
         * 开始作业时间确定后，船舶由靠泊变为在装/卸；
         * 结束作业时间确定后，状态变为待离泊；
         * 离泊时间确定后，状态为待离港；
         * 离港时间确定后，本航次完成；
         */

        // 如果状态是 8（移泊）或 9（抛出），无需更新
        if ("8".equals(dockPlan.getStatus()) || "9".equals(dockPlan.getStatus())) {
            return;
        }
        validateSequentialTimes(
                dockPlan.getDockingTime(),
                dockPlan.getOperationTime(),
                dockPlan.getEndTime(),
                dockPlan.getOutBerthTime(),
                dockPlan.getLeaveTime());
        if (dockPlan.getLeaveTime() != null) {
            dockPlan.setStatus("7"); // 完成
            // 计算滞期费
//            demurrage(dockPlan);
            // 计算平均效率
//            calculateAvgEfficiency(dockPlan);
        } else if (dockPlan.getOutBerthTime() != null) {
            dockPlan.setStatus("6"); // 待离港
//            demurrage(dockPlan);
//            calculateAvgEfficiency(dockPlan);
        } else if (dockPlan.getEndTime() != null) {
            dockPlan.setStatus("5"); // 待离泊
//            demurrage(dockPlan);
//            calculateAvgEfficiency(dockPlan);
        } else if (dockPlan.getOperationTime() != null) {
            dockPlan.setStatus("4"); // 在装卸
        } else if (dockPlan.getDockingTime() != null) {
            dockPlan.setStatus("3"); // 靠泊
        } else if (dockPlan.getArrivalTime() != null) {
            // 如果原本的状态是靠泊中，不做任何处理
            if (Objects.equals(dockPlan.getStatus(), "10")) {
                // 保持原状态
            } else {
                // 到港时间和当前时间做对比，如果是大于当前时间是在途，小于等于当前时间是等泊
                dockPlan.setStatus(dockPlan.getArrivalTime().isBefore(LocalDateTime.now()) ? "2" : "1");
            }
        } else {
            dockPlan.setStatus("0"); // 计划
        }
    }


    private void updateBerthStatus(DockPlan dockPlan) {
        // 1. 查询泊位信息
        DockBerth dockBerth = dockBerthMapper.selectDockBerthByNameOrCode(dockPlan.getHbName());
        if (dockBerth == null) {
            return;
        }
        dockPlan.setHbId(dockBerth.getBerthId());
        // 2. 查询当前泊位是否有其他 3、4、5 状态的计划单
        DockPlan plan = new DockPlan();
        plan.setHbId(dockPlan.getHbId());
        plan.setId(dockPlan.getId());

        List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanByStatus(plan);
        boolean statusFlag = dockPlanList != null && !dockPlanList.isEmpty();

        // 查询有没有暂停的
        boolean stopFlag = dockPlanList.stream()
                .map(planFilter -> dockUnloadWorkService.selectUnloadWorkListByPlanId(planFilter.getId()))
                .flatMap(List::stream)
                .anyMatch(work -> "1".equals(work.getWorkType()));


        String currentStatus = dockPlan.getStatus();

        // 如果是3靠泊 4在装卸 5待离泊 并且是暂停就改为暂停，如果不是暂停就是进行中
        if ("3".equals(currentStatus) || "4".equals(currentStatus) || "5".equals(currentStatus)) {
            dockBerth.setBerthStatus(stopFlag ? "3" : "1");
        } else {
            // 如果是6待离港 7完成 8移泊，有其他计划单 暂停就是暂停 否则就是就是进行中 没有就是空闲
            if (stopFlag) {
                dockBerth.setBerthStatus("3");
            } else {
                dockBerth.setBerthStatus(statusFlag ? "1" : "0");
            }
        }

        int i = dockBerthMapper.updateDockBerth(dockBerth);
    }

    /**
     * 计算平均效率
     *
     * @param dockPlan
     */
    private void calculateAvgEfficiency(DockPlan dockPlan) {
        // 作业时间
        LocalDateTime operationTime = dockPlan.getOperationTime();
        LocalDateTime endTime = dockPlan.getEndTime();

        // 剩余重量
        String unloadWeightStr = dockPlan.getUnloadWeight();
        BigDecimal unloadWeight = (unloadWeightStr != null
                && !unloadWeightStr.trim().isEmpty()
                && !"null".equalsIgnoreCase(unloadWeightStr.trim()))
                ? new BigDecimal(unloadWeightStr.trim())
                : BigDecimal.ZERO;
        // 计算作业时长（小时）
        Duration duration = Duration.between(operationTime, endTime);
        BigDecimal hours = BigDecimal.valueOf(duration.toSeconds()).divide(BigDecimal.valueOf(3600), 4, HALF_UP);

        // 避免除零错误
        if (hours.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("作业时长不能为 0！");
        }

        // 计算平均卸货效率（吨/小时）
        BigDecimal avgEfficiency = unloadWeight.divide(hours, 2, HALF_UP);

        // 设置到对象
        dockPlan.setAvgDischargeRate(avgEfficiency.toPlainString());
    }


    /**
     * 验证时间校验必填，避免状态异常流转
     */
    public static void validateSequentialTimes(
            LocalDateTime dockingTime,
            LocalDateTime operationTime, LocalDateTime endTime,
            LocalDateTime outBerthTime, LocalDateTime leaveTime) {
        LocalDateTime[] times = {dockingTime, operationTime, endTime, outBerthTime, leaveTime};
        String[] labels = {"靠泊时间",
                "作业时间", "结束时间", "离泊时间", "离港时间"};

        // 校验时间依赖性：如果填了某个时间，前一个时间不能为 null
        for (int i = 1; i < times.length; i++) {
            if (times[i] != null && times[i - 1] == null) {
                throw new ServiceException("填写了 " + labels[i] + "，但 " + labels[i - 1] + " 不能为空");
            }
        }

        // 校验时间顺序：时间必须严格递增
        for (int i = 1; i < times.length; i++) {
            if (times[i] != null && times[i - 1] != null && !times[i - 1].isBefore(times[i])) {
                throw new ServiceException(labels[i - 1] + " 必须早于 " + labels[i] + " ");
            }
        }
    }


    /**
     * 批量删除计划单
     *
     * @param ids 需要删除的计划单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDockPlanByIds(Long[] ids) {
        if (ids.length == 0) {
            throw new ServiceException("参数为空，请选择参数之后进行删除操作！");
        }
        int row = 0;
        for (Long id : ids) {
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
            if (StringUtils.equals(dockPlan.getDelFlag(), "1")) {
                throw new ServiceException("删除失败，该条数据已被删除！");
            }
            if (StringUtils.equals(dockPlan.getUnloadStatus(), "2")) {
                throw new ServiceException("删除失败，计划单已完成卸货，不可删除！");
            }
            // 待离泊状态结束读取皮带秤（状态为 45、6、7 且为指定泊位 ID）
            // TODO 1# 4# 8#泊位
            Set<Long> VALID_HB_IDS = Set.of(9L, 12L);
            if (Arrays.asList("4", "5", "6", "7").contains(dockPlan.getStatus())
                    && VALID_HB_IDS.contains(dockPlan.getHbId())) {

                plcTaskService.stopTask(dockPlan.getId(), dockPlan.getHbName());

                // 结束之后删除 redis 并且把 PLC 数据存到数据库中
                removeCacheEditPlc(dockPlan);
            }
            dockPlan.setDelFlag("1");
            row += dockPlanMapper.removeDockPlanByDbId(dockPlan);

            if (dockPlan.getHbId() != null) {
                // 根据泊位主键查询计划单是靠泊、在装卸、待离泊状态，查询到泊位是工作 没有就是空闲
                List<ScreenPlanVo> planVoList = dockPlanMapper.selectPlanByHbId(dockPlan.getHbId());
                DockBerth dockBerth = dockBerthMapper.selectDockBerthByDbId(dockPlan.getHbId());

                if (planVoList.isEmpty()) {
                    dockBerth.setBerthStatus("0");
                } else {
                    dockBerth.setBerthStatus("1");
                }
                int i = dockBerthMapper.updateDockBerth(dockBerth);
            }


        }
        return row;
    }

    @Override
    public int updateStatus(DockPlan dockPlan) {
        return dockPlanMapper.updateDockPlan(dockPlan);
    }


    /**
     * @param userList      数据集
     * @param operName      当前登录人
     * @param updateSupport 是否支持更新
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importShipPlan(List<DockPlanImportExcel> userList, String operName, boolean updateSupport) {
        if (userList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }
        int importNum = 0;
        int failureNum = 0;
        // 计划状态
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        // TODO 1# 4# 8#泊位
        Set<Long> VALID_HB_IDS = Set.of(9L, 12L);

        for (DockPlanImportExcel thisPlanExcel : userList) {
            importNum++;

            boolean flag = false;
            try {
                // 修改
                if (updateSupport) {
                    if (StringUtils.isBlank(thisPlanExcel.getShipName()) ||
                            StringUtils.isBlank(thisPlanExcel.getMineNumber()) ||
                            StringUtils.isBlank(thisPlanExcel.getHbName()) ||
                            StringUtils.isBlank(thisPlanExcel.getShipName())) {
                        flag = true;
                    }

                    /**
                     * 根据船名、矿号、泊位、物资查询计划单
                     * 如果查到就修改，查不到就跳过，弹窗提示没有导入进来
                     */
                    DockPlan planParams = new DockPlan();
                    planParams.setShipName(thisPlanExcel.getShipName()); // 船名
                    planParams.setMineNumber(thisPlanExcel.getMineNumber()); // 矿号
                    planParams.setHbName(thisPlanExcel.getHbName()); // 泊位
                    planParams.setMaterialName(thisPlanExcel.getMaterialName()); // 物资
                    DockPlan dockPlan = dockPlanMapper.selectDockPlanByParams(planParams);
                    if (dockPlan == null) {
                        failureMsg.append("<br/> 【第一行，没有查询到对应计划单，修改失败】");
                        flag = true;
                    } else {
                        dockPlan.setArrivalTime(DateUtils.toLocalDateTime(thisPlanExcel.getArrivalTime()));
                        dockPlan.setDockingTime(DateUtils.toLocalDateTime(thisPlanExcel.getDockingTime()));
                        dockPlan.setOperationTime(DateUtils.toLocalDateTime(thisPlanExcel.getOperationTime()));
                        dockPlan.setEndTime(DateUtils.toLocalDateTime(thisPlanExcel.getEndTime()));
                        dockPlan.setOutBerthTime(DateUtils.toLocalDateTime(thisPlanExcel.getOutBerthTime()));
                        dockPlan.setLeaveTime(DateUtils.toLocalDateTime(thisPlanExcel.getLeaveTime()));
                        // dockPlan.setRemark(thisPlanExcel.getRemark());
                        // 修改码头
                        updatePlanStatus(dockPlan);
                        // 修改泊位
                        updateBerthStatus(dockPlan);

                        // 自动生成卸货单
                        if ("4".equals(dockPlan.getStatus())) {
                            // 如果是在装卸状态，开始读取PLC
                            // 需要触发装卸的状态集合
                            if (dockPlan.getHbId() != null && VALID_HB_IDS.contains(dockPlan.getHbId())) {
                                plcTaskService.startTask(dockPlan.getId(), dockPlan.getHbName());
                            }
                            DockUnloadWork unloadWork = new DockUnloadWork();
                            unloadWork.setStartTime(dockPlan.getOperationTime());
                            unloadWork.setPlanId(dockPlan.getId());
                            unloadWork.setWorkType("0"); // 进行中
                            unloadWork.setCreateTime(LocalDateTime.now());
                            unloadWork.setDelFlag("0");
                            unloadWork.setDeptId(SecurityUtils.getDeptId());
                            dockUnloadWorkService.createUnloadWork(unloadWork);
                        }
                        // 待离泊状态结束读取皮带秤（状态为 5、6、7 且为指定泊位 ID）
                        if (Arrays.asList("5", "6", "7").contains(dockPlan.getStatus())
                                && VALID_HB_IDS.contains(dockPlan.getHbId())) {

                            plcTaskService.stopTask(dockPlan.getId(), dockPlan.getHbName());

                            // 结束之后删除 redis 并且把 PLC 数据存到数据库中
                            removeCacheEditPlc(dockPlan);
                        }
                        int i = dockPlanMapper.updateDockPlan(dockPlan);
                    }
                    if (flag) {
                        failureNum++;
                    }

                } else {
                    // 物资查询
                    DockMaterial dockMaterial = dockMaterialService.selectDockMaterialByName(thisPlanExcel.getMaterialName());
                    if (dockMaterial == null) {
                        flag = true;
                        failureMsg.append("<br/> 船名：" + thisPlanExcel.getShipName() + " 导入失败：没有在系统种找到对应的物资：【" + thisPlanExcel.getMaterialName() + "】");
                    }
                    if (flag) {
                        failureNum++;
                    }
                    List<DockBerth> dockBerthList = dockBerthMapper.selectDockBerthList(new DockBerth());

                    DockPlan dockPlan = new DockPlan();
                    BeanUtils.copyProperties(thisPlanExcel, dockPlan);
                    for (DockBerth dockBerth : dockBerthList) {
                        if (dockPlan.getHbName().equals(dockBerth.getBerthName())){
                            dockPlan.setHbId(dockBerth.getBerthId());}
                    }
                    if (dockPlan.getHbId() == null){
                        flag = true;
                        failureMsg.append("<br/> 船名：" + thisPlanExcel.getShipName() + " 导入失败：没有在系统种找到对应的泊位：【" + thisPlanExcel.getHbName() + "】");
                    }

                    if (flag) {
                        failureNum++;
                    }
                    // 去掉换行符
                    String shipName = Optional.ofNullable(dockPlan.getShipName())
                            .orElse("")
                            .replaceAll("[\\r\\n]", ""); // 去除换行符
                    dockPlan.setShipName(shipName);
                    dockPlan.setId(null);
                    dockPlan.setDelFlag("0");
                    dockPlan.setCreateBy(SecurityUtils.getNickName());
                    dockPlan.setCreateTime(LocalDateTime.now());
                    dockPlan.setDeptId(SecurityUtils.getDeptId());
                    dockPlan.setSourceType("1");
                    if (dockMaterial != null) {
                        dockPlan.setPackageNum(Long.parseLong(dockMaterial.getRemark01()));
                    }
                    dockPlan.setRemark(thisPlanExcel.getRemark());
                    dockPlan.setArrivalTime(DateUtils.toLocalDateTime(thisPlanExcel.getArrivalTime()));
                    dockPlan.setDockingTime(DateUtils.toLocalDateTime(thisPlanExcel.getDockingTime()));
                    dockPlan.setOperationTime(DateUtils.toLocalDateTime(thisPlanExcel.getOperationTime()));
                    dockPlan.setEndTime(DateUtils.toLocalDateTime(thisPlanExcel.getEndTime()));
                    dockPlan.setOutBerthTime(DateUtils.toLocalDateTime(thisPlanExcel.getOutBerthTime()));
                    dockPlan.setLeaveTime(DateUtils.toLocalDateTime(thisPlanExcel.getLeaveTime()));
                    dockPlan.setPlanDockingTime(DateUtils.toLocalDateTime(thisPlanExcel.getPlanDockingTime()));
                    updatePlanStatus(dockPlan);
                    // 如果导入的泊位有值的话 根据名称查询泊位，查到修改状态，查不到不管他 因为导入的泊位名称可能是null 可能是和泊位列表不对应，也可能是正确的
                    if (StringUtils.isNotBlank(thisPlanExcel.getHbName())) {
                        updateBerthStatus(dockPlan);
                    }
                    if ("4".equals(dockPlan.getStatus())) {
                        dockPlan.setUnloadStatus("1");
                    }
                    dockPlanMapper.insertDockPlan(dockPlan);
                    // 自动生成卸货单
                    if ("4".equals(dockPlan.getStatus())) {
                        // 如果是在装卸状态，开始读取PLC
                        // 需要触发装卸的状态集合
                        if (dockPlan.getHbId() != null && VALID_HB_IDS.contains(dockPlan.getHbId())) {
                            plcTaskService.startTask(dockPlan.getId(), dockPlan.getHbName());
                        }

                        DockUnloadWork unloadWork = new DockUnloadWork();
                        unloadWork.setStartTime(dockPlan.getOperationTime());
                        unloadWork.setPlanId(dockPlan.getId());
                        unloadWork.setWorkType("0"); // 进行中
                        unloadWork.setCreateTime(LocalDateTime.now());
                        unloadWork.setDelFlag("0");
                        unloadWork.setDeptId(SecurityUtils.getDeptId());
                        dockUnloadWorkService.createUnloadWork(unloadWork);
                    }
                    // 待离泊状态结束读取皮带秤（状态为 5、6、7 且为指定泊位 ID）
                    if (Arrays.asList("5", "6", "7").contains(dockPlan.getStatus())
                            && VALID_HB_IDS.contains(dockPlan.getHbId())) {

                        plcTaskService.stopTask(dockPlan.getId(), dockPlan.getHbName());

                        // 结束之后删除 redis 并且把 PLC 数据存到数据库中
                        removeCacheEditPlc(dockPlan);
                    }

                    successMsg.append("<br/> 船名：" + thisPlanExcel.getShipName() + " 导入成功");
                }

            } catch (Exception e) {
                if (!flag) {
                    failureNum++;
                }
                String msg = "<br/> 第" + importNum + "行计划单信息，导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }


        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + importNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<DockPlanExcel> exportDockPlanList(DockPlan dockPlan) {
        List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlan);
        AtomicInteger rowCount = new AtomicInteger();
        return dockPlanList.stream().map(x -> {
            DockPlanExcel dockPlanExcel = new DockPlanExcel();
            BeanUtils.copyProperties(x, dockPlanExcel);
            rowCount.getAndIncrement();
            dockPlanExcel.setSort(rowCount.get());
//            dockPlanExcel.setPlanTime(DateUtils.toDate(x.getPlanTime()));
            dockPlanExcel.setDockingTime(DateUtils.toDate(x.getDockingTime()));
            dockPlanExcel.setOperationTime(DateUtils.toDate(x.getOperationTime()));
            dockPlanExcel.setEndTime(DateUtils.toDate(x.getEndTime()));
            dockPlanExcel.setOutBerthTime(DateUtils.toDate(x.getOutBerthTime()));
            dockPlanExcel.setLeaveTime(DateUtils.toDate(x.getLeaveTime()));
            dockPlanExcel.setArrivalTime(DateUtils.toDate(x.getArrivalTime()));
            if (x.getDemurrageFee() != null) {
                dockPlanExcel.setDemurrageFee(x.getDemurrageFee().toPlainString());  // 卸率
            }
            // 结束时间不为空计算用时
            if (x.getEndTime() != null) {
                String formart = DateUtils.timeDiff(x.getOperationTime(), x.getEndTime());
                dockPlanExcel.setEffectiveTime(formart);
            }


            return dockPlanExcel;
        }).toList();
    }

    @Override
    public List<ScreenPlanBerthVo> screenPlanBerthList() {
        List<ScreenPlanBerthVo> planBerthVoList = dockPlanMapper.screenPlanBerthList();

        LocalDateTime dateNowTime = LocalDateTime.now();
        for (ScreenPlanBerthVo planBerthVo : planBerthVoList) {

//            DockUnloadWork unloadWork = dockUnloadWorkService.selectDockUnloadWorkDesc(planBerthVo.getPlanId());
            DockUnloadWork unloadWork = new DockUnloadWork();
            unloadWork.setPlanId(planBerthVo.getPlanId());

            List<DockUnloadWork> unloadWorkList = dockUnloadWorkService.selectDockUnloadWorkList(unloadWork);
            if (!unloadWorkList.isEmpty()) {
                // 根据创建时间取最新一条
                DockUnloadWork latestWork = Collections.max(unloadWorkList, Comparator.comparing(DockUnloadWork::getCreateTime));
                planBerthVo.setWorkType(latestWork.getWorkType());

                DockUnloadDetail dockUnloadDetail = dockUnloadDetailService.selectDetailDescByDuId(latestWork.getDuId());
                if (dockUnloadDetail != null) {
                    planBerthVo.setReason(dockUnloadDetail.getReason());
                }

                for (DockUnloadWork dockUnloadWork : unloadWorkList) {
                    List<DockUnloadDetail> unloadDetailList = dockUnloadDetailService.selectDockUnloadDetailByDuId(dockUnloadWork.getDuId());
                    if (!unloadDetailList.isEmpty()) {
                        dockUnloadWork.setDockUnloadDetailList(unloadDetailList);
                    } else {
                        dockUnloadWork.setDockUnloadDetailList(new ArrayList<>());
                    }
                }
                // 卸货列表
                planBerthVo.setUnloadWorkList(unloadWorkList);
            } else {
                planBerthVo.setUnloadWorkList(new ArrayList<>());
            }
            // 作业时间
            if (planBerthVo.getOperationTime() != null) {
                LocalDateTime operationTime = planBerthVo.getOperationTime();
                String expendTime = DateUtils.timeDiff(operationTime, dateNowTime);
                planBerthVo.setExpendTime(expendTime);
            }
        }
        return planBerthVoList;
    }

    @Override
    public List<ScreenWaitBerthVo> screenWaitBerthList() {
        List<ScreenWaitBerthVo> berthVoList = dockPlanMapper.screenWaitBerthList();
        for (ScreenWaitBerthVo waitBerthVo : berthVoList) {
            LocalDateTime arrivalTime = waitBerthVo.getArrivalTime();
            String waitBerthTime = DateUtils.timeDiff(arrivalTime);

            LocalDateTime planDockingTime = waitBerthVo.getPlanDockingTime();
            if (planDockingTime != null) {
                String planWaitTimeStr = DateUtils.timeDiff(arrivalTime, planDockingTime);

                // 实际等泊时间
                Duration waitBerthTimeDuration = parsePauseInterval(waitBerthTime);
                // 计划等泊时间
                Duration planWaitTimeDuration = parsePauseInterval(planWaitTimeStr);


                Duration diffTime = waitBerthTimeDuration.minus(planWaitTimeDuration);

                // 计算差值 (计划时间 - 实际时间)
                Duration diff = planWaitTimeDuration.minus(waitBerthTimeDuration);
                String planWaitTime = formatDuration(diff);
                waitBerthVo.setPlanWaitTime(planWaitTime);

                if (diffTime.isNegative()) {
                    waitBerthVo.setColorFlag("0"); // 绿色
                } else {
                    if (diffTime.toHours() < 12) {
                        waitBerthVo.setColorFlag("0"); // 绿色
                    } else if (diffTime.toHours() < 24) {
                        waitBerthVo.setColorFlag("1"); // 橙色
                    } else {
                        waitBerthVo.setColorFlag("2"); // 红色
                    }
                }
            }
            waitBerthVo.setWaitBerthTime(waitBerthTime);
        }
        return berthVoList;
    }

    /**
     * 查询计划单状态分布
     *
     * @return
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<ScreenPlanStatusVo> screenPlanStatusList(BaseEntity baseEntity) {
        List<ScreenPlanStatusVo> screenPlanStatusVos = dockPlanMapper.screenPlanStatusList(baseEntity);
        List<DockMaterialVo> dockMaterialVos = dockPlanMapper.selectDockMaterialList(baseEntity);
        Map<String, List<DockMaterialVo>> groupByStatus = dockMaterialVos.stream().collect(Collectors.groupingBy(DockMaterialVo::getStatus));
        for (ScreenPlanStatusVo screenPlanStatusVo : screenPlanStatusVos) {
            List<DockMaterialVo> dockMaterialVoList = groupByStatus.get(screenPlanStatusVo.getStatus());
            if (CollectionUtils.isEmpty(dockMaterialVoList)) {
                screenPlanStatusVo.setChildrenMaterialVo(Collections.emptyList());
            } else {
                screenPlanStatusVo.setChildrenMaterialVo(dockMaterialVoList);
            }
        }
        return screenPlanStatusVos;
    }


    @Override
    public Map<String, String> screenCountMap() {

        Map<String, String> map = new HashMap<>();
        DockPlan dockPlan = new DockPlan();

        List<DockPlan> dockingList = dockPlanMapper.screenPlanTimeList(dockPlan);
        // 统计 dockingTime 和 outBerthTime 不为空的条目数
        long dockingCount = dockingList.stream().filter(p -> p.getDockingTime() != null).count();
        long outBerthCount = dockingList.stream().filter(p -> p.getOutBerthTime() != null).count();

        map.put("dockingList", dockingCount + "");
        map.put("outBerthList", outBerthCount + "");
        return map;
    }

    /**
     * 大屏修改泊位方法
     *
     * @param dockPlan
     * @return
     */
    @Override
    public int updatePlanBerth(DockPlan dockPlan) {

        DockPlan plan = dockPlanMapper.selectDockPlanById(dockPlan.getId());
        if (!StringUtils.equals("2", plan.getStatus())) {
            throw new ServiceException("计划单不是等泊状态，不可修改");
        }
        // 靠泊
        if (dockPlan.getDockingTime() != null) {
            dockPlan.setStatus("3"); // 靠泊
        }
        int i = dockPlanMapper.updatePlanBerth(dockPlan);
        return i;
    }


    @Override
    public List<ScreenPlanVo> selectPlanScreenWorkList(DockPlan dockPlan) {


        LocalDateTime dateTime = LocalDateTime.now();


        List<ScreenPlanVo> planVoList = dockPlanMapper.selectPlanScreenWorkList(dockPlan);

        for (ScreenPlanVo planVo : planVoList) {
            String uploadSpeedUnit = getUploadSpeedUnit(planVo.getMaterialName());
            DockBerth dockBerth = dockBerthMapper.selectDockBerthByDbId(planVo.getBerthId());
            if (dockBerth != null) {
                planVo.setBerthStatus(dockBerth.getBerthStatus());
            }

            DockUnloadWork unloadWork = dockUnloadWorkService.selectDockUnloadWorkDesc(planVo.getPlanId());
            if (unloadWork != null) {
                planVo.setWorkType(unloadWork.getWorkType());
                DockUnloadDetail dockUnloadDetail = dockUnloadDetailService.selectDetailDescByDuId(unloadWork.getDuId());
                if (dockUnloadDetail != null) {
                    planVo.setReason(dockUnloadDetail.getReason());
                }
            }

            if (planVo.getOperationTime() == null) {
                continue;
            }
            if (planVo.getGrossWeight() == null) {
                continue;
            }
            // 总重量
            String tonnage = planVo.getGrossWeight();
            BigDecimal grossWeight = new BigDecimal(tonnage);

            // 计划单卸货重量
            BigDecimal unloadedWeightPlan = StringUtils.isNotBlank(planVo.getUnloadedWeight()) ? new BigDecimal(planVo.getUnloadedWeight()) : BigDecimal.ZERO;

            String berthName = planVo.getBerthName();
            // 1# 4# 8# 码头读取皮带秤
            if (StringUtils.equals(berthName, "1#") || StringUtils.equals(berthName, "4#")) {
                String redisKey = "PLC:" + planVo.getPlanId() + "_" + berthName;
                Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);
                // 归零数据：PLC读取的累计吨重
                Object value1 = redisMap.get(PLCConstans.CURRENT_WEIGHT);
                BigDecimal currentWeight = new BigDecimal(value1 != null ? value1.toString() : "0");  // 如果 value 为 null，默认赋值 "0"

                // 实时流量
                Object value2 = redisMap.get(PLCConstans.REAL_TIME_DATE);
                BigDecimal realTimeDate = new BigDecimal(value2 != null ? value2.toString() : "0");  // 如果 value 为 null，默认赋值 "0"

                // 卸货重量 = 计划单卸货重量 + PLC累计重量

                // 已卸重量+皮带秤数据
                BigDecimal addRate = unloadedWeightPlan.add(currentWeight);
                // 已卸重量
                planVo.setUnloadedWeight(addRate.toPlainString());
                // 剩余重量 = 总重量 - 已卸重量
                BigDecimal bigDecimal = grossWeight.subtract(addRate);
                // 剩余重量
                planVo.setResidueWeight(bigDecimal.toPlainString());

                // 实时流量
                planVo.setPlcRealTime(realTimeDate.setScale(2, HALF_UP).toPlainString() + uploadSpeedUnit);
                // 平均卸率
                // 如果是在装卸状态，已作业量 / 当前时间-作业时间
                // 如果是待离泊状态，已作业量 / 结束时间-当前时间
                BigDecimal avgTime;
                if (StringUtils.equals("4", planVo.getStatus())) {
                    avgTime = BigDecimal.valueOf(
                            Duration.between(planVo.getOperationTime(), dateTime).toMinutes()
                    ).divide(BigDecimal.valueOf(60), 2, HALF_UP);
                } else {
                    avgTime = BigDecimal.valueOf(
                            Duration.between(planVo.getOperationTime(), planVo.getEndTime()).toMinutes()
                    ).divide(BigDecimal.valueOf(60), 2, HALF_UP);
                }

                BigDecimal divide = addRate.divide(avgTime, 2, HALF_UP);
                planVo.setAvgDischargeRate(divide.toPlainString() + uploadSpeedUnit); // 平均卸率
            } else {
                BigDecimal avgTime;
                if (StringUtils.equals("4", planVo.getStatus())) {
                    avgTime = BigDecimal.valueOf(
                            Duration.between(planVo.getOperationTime(), dateTime).toMinutes()
                    ).divide(BigDecimal.valueOf(60), 2, HALF_UP);
                } else {
                    avgTime = BigDecimal.valueOf(
                            Duration.between(planVo.getOperationTime(), planVo.getEndTime()).toMinutes()
                    ).divide(BigDecimal.valueOf(60), 2, HALF_UP);
                }
                BigDecimal bigDecimal = grossWeight.subtract(unloadedWeightPlan);
                // 剩余重量
                planVo.setResidueWeight(bigDecimal.toPlainString());
                if (avgTime.compareTo(BigDecimal.ZERO) == 0) {
                    avgTime = BigDecimal.ONE;
                }
                // 已卸重量 /
                // 计算每小时的平均卸率（单位重量/小时）
                BigDecimal averageUnloadingRate = unloadedWeightPlan.divide(avgTime, 2, HALF_UP);
                planVo.setAvgDischargeRate(averageUnloadingRate.toPlainString() + uploadSpeedUnit); // 平均卸率
                // 已卸重量
                planVo.setUnloadedWeight(unloadedWeightPlan.toPlainString());
            }

        }

        planVoList.sort(Comparator.comparingInt(vo -> {
            if ("1".equals(vo.getWorkType())) return 0; // 暂停
            if ("5".equals(vo.getStatus())) return 1; // 待离泊
            if ("2".equals(vo.getWorkType())) return 2; // 在作业
            if ("4".equals(vo.getStatus())) return 3; // 在作业
            if ("10".equals(vo.getStatus())) return 4; // 靠泊中
            return 10; // "4" 最后
        }));
        return planVoList;
    }

    /**
     * 移泊
     *
     * @param planParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveBerth(DockPlan planParam) {

        DockPlan sourceDockPlan = dockPlanMapper.selectDockPlanById(planParam.getId());
        List<DockUnloadWork> unloadWorkList = dockUnloadWorkService.selectUnloadWorkListByPlanId(sourceDockPlan.getId());
        // 查询卸货单有没有未完成的单据
        List<DockUnloadWork> unloadCheckList = unloadWorkList.stream()
                .filter(x -> StringUtils.equals(x.getWorkType(), "0") || StringUtils.equals(x.getWorkType(), "1")).toList();

        if (planParam.getMoveBerthTime() == null) {
            throw new ServiceException("操作失败：移泊时间没有选择");
        }
        switch (sourceDockPlan.getStatus()) {
            case "0", "1", "2", "3" ->
                    throw new ServiceException("操作失败：计划单是在计划、在途、等泊、靠泊状态不可以操作");
        }

        if (!unloadCheckList.isEmpty()) {
            throw new ServiceException("卸货单有未完成的单据，请先结束再操作此按钮");
        }

        // 抛泊逻辑
        if (planParam.getHbId() == null) {
            sourceDockPlan.setStatus("8");                                 // 待回靠
            sourceDockPlan.setMoveBerthTime(planParam.getMoveBerthTime()); // 移泊时间

            DockBerth dockBerth = dockBerthService.selectDockBerthByDbId(sourceDockPlan.getHbId());
            // 结束，判断泊位下是否有多个卸货单，如果有多个卸货单在卸货，泊位状态就是进行中 如果只有一个在卸货，并且是待离港之后就是空闲
            DockPlan plan = new DockPlan();
            plan.setHbId(sourceDockPlan.getHbId());
            plan.setId(sourceDockPlan.getId());
            List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanByStatus(plan);
            boolean statusFlag = dockPlanList != null && !dockPlanList.isEmpty();
            if (statusFlag) {
                dockBerth.setBerthStatus("1"); // 进行中
            } else {
                // 如果是待离泊之后 不执行泊位判断
                if (List.of("6", "7", "8", "9").contains(sourceDockPlan.getStatus())) {
                    // 只有不在 6~9 范围内的状态才修改泊位状态
                    dockBerth.setBerthStatus("0"); // 空闲
                } else {
                    dockBerth.setBerthStatus("1"); // 空闲
                }
            }
            dockBerthService.updateDockBerthStatus(dockBerth);

            return dockPlanMapper.updateDockPlan(sourceDockPlan);
        } else {
            sourceDockPlan.setMoveBerthTime(planParam.getMoveBerthTime()); // 移泊时间
            sourceDockPlan.setStatus("9"); // 移泊
            int i = dockPlanMapper.updateDockPlan(sourceDockPlan);
            // 移泊逻辑
            if (Objects.equals(planParam.getHbId(), sourceDockPlan.getHbId())) {
                throw new ServiceException("操作失败：原计划但泊位和要移泊的泊位重复");
            }
            DockPlan insertPlan = new DockPlan();
            BeanUtils.copyProperties(sourceDockPlan, insertPlan);
            insertPlan.setId(null);
            insertPlan.setStatus(null);
            insertPlan.setRemainingWeight(null);
            insertPlan.setMoveBerthTime(null);
            insertPlan.setEffectiveTime(null);
            insertPlan.setEffectiveRate(null);
            insertPlan.setAvgDischargeRate(null);
            insertPlan.setOutBerthTime(null);
            insertPlan.setLeaveTime(null);
            insertPlan.setEndTime(null);
            insertPlan.setContractRate(null);
            insertPlan.setContractFee(null);
            insertPlan.setDemurrageFee(null);
            insertPlan.setDockingTime(planParam.getDockingTime());
            insertPlan.setOperationTime(planParam.getOperationTime());
            insertPlan.setSourceId(sourceDockPlan.getId());
            insertPlan.setSourceType("2");
            insertPlan.setTonnage(sourceDockPlan.getTonnage());
            insertPlan.setUnloadWeight(sourceDockPlan.getUnloadWeight());
            insertPlan.setHbId(planParam.getHbId());
            insertPlan.setHbName(planParam.getHbName());


//            BigDecimal unloadWeight = StringUtils.isNotBlank(sourceDockPlan.getUnloadWeight()) ? new BigDecimal(sourceDockPlan.getUnloadWeight()) : BigDecimal.ZERO;

            /**
             *  移泊生成新数据，源数据的已卸货量就是新数据的总重量
             */
            // 已卸货量
//            insertPlan.setTonnage(unloadWeight.toPlainString()); // 总重量

            return insertDockPlan(insertPlan);
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateUnloadWeight(List<DockUnloadWeighExcel> planRateExcelList) {
        /**
         * 1、校验
         * 2、判断当前计划单有没有执行定时任务并且是运行中，如果有，重置redis归零数据
         * 3、修改卸货量
         */

        if (planRateExcelList.isEmpty()) {
            throw new ServiceException("操作失败，没有查询到数据");
        }

        int count = 1;
        int row = 0;
        for (DockUnloadWeighExcel dockUnloadWeighExcel : planRateExcelList) {

            count++;

            /**
             * 1、校验
             */
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockUnloadWeighExcel.getId());
            if (dockPlan == null) {
                throw new ServiceException("操作失败，第" + count + "行，没有查询到对应数据");
            }

            if (StringUtils.isBlank(dockUnloadWeighExcel.getUnloadWeight())) {
                throw new ServiceException("操作失败，第" + count + "行，已卸货量是空的");
            }
            /**
             * 2、判断当前计划单有没有执行定时任务并且是运行中，如果有，重置redis归零数据
             */
            ScheduledTasks scheduledTasks = scheduledTasksMapper.selectScheduledTasksByDuIdAndName(dockPlan.getId(), dockPlan.getHbName());
            if (scheduledTasks != null && StringUtils.equals(scheduledTasks.getStatus(), "RUNNING")) {
                String redisKey = "PLC:" + dockPlan.getId() + "_" + dockPlan.getHbName();
                Map<String, BigDecimal> redisMap = redisCache.getCacheMap(redisKey);
                if (redisMap != null && !redisMap.isEmpty()) {
                    // redisMap.put("currentWeight", BigDecimal.ZERO);
                    redisMap.put("unloadWeight", BigDecimal.valueOf(Double.parseDouble(dockPlan.getUnloadWeight())));
                    redisCache.setCacheMap(redisKey, redisMap);
                }
            }
            /**
             * 3、修改卸货量
             */
            row += dockPlanMapper.updateUnloadWeigh(dockUnloadWeighExcel);
        }
        return row;
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<DockUnloadWeighExcel> exportUnloadWeight(DockPlan dockPlan) {
        dockPlan.setStatus("4");    // 在装卸
        List<DockPlan> dockPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlan);
        return dockPlanList.stream().map(item -> {
            DockUnloadWeighExcel weighExcel = new DockUnloadWeighExcel();
            BeanUtils.copyProperties(item, weighExcel);
            weighExcel.setUnloadWeight(null);
            return weighExcel;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatusToDocking(DockPlan dockPlan) {
        return dockPlanMapper.updateStatus(dockPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int toDocking(DockPlan planParam) {
        DockPlan sourceDockPlan = dockPlanMapper.selectDockPlanById(planParam.getId());
        if (!Objects.equals("2", sourceDockPlan.getStatus())) {
            throw new ServiceException("只有在等泊状态下才可以改为靠泊中");
        }

        DockPlan dockPlan = new DockPlan();
        dockPlan.setId(planParam.getId());
        dockPlan.setStatus("10");
        dockPlan.setUpdateBy(SecurityUtils.getNickName());
        dockPlan.setUpdateTime(LocalDateTime.now());

        return updateStatusToDocking(dockPlan);
    }

    @Override
    public int planDocking(DockPlan planParam) {
        DockPlan sourceDockPlan = dockPlanMapper.selectDockPlanById(planParam.getId());
        if (!Objects.equals("2", sourceDockPlan.getStatus())) {
            throw new ServiceException("只有在等泊状态下才可以操作计划靠泊");
        }

        DockPlan dockPlan = new DockPlan();
        dockPlan.setId(planParam.getId());
        dockPlan.setPlanDockingTime(planParam.getPlanDockingTime()); // 计划靠泊时间
        dockPlan.setUpdateBy(SecurityUtils.getNickName());
        dockPlan.setUpdateTime(LocalDateTime.now());
        return dockPlanMapper.updateStatus(dockPlan);
    }

    @Override
    public List<ScreenShipArrivalVo> screenShipArrivalStatistics() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS);

        Date today = new Date();
        List<DockMaterialVo> todayList = dockPlanMapper
                .shipArrivalStatistics(DateUtil.format(DateUtil.beginOfDay(today), pattern), DateUtil.format(DateUtil.endOfDay(today), pattern));

        DateTime tomorrow = DateUtil.tomorrow();
        List<DockMaterialVo> tomorrowList = dockPlanMapper
                .shipArrivalStatistics(DateUtil.format(DateUtil.beginOfDay(tomorrow), pattern), DateUtil.format(DateUtil.endOfDay(tomorrow), pattern));

        ScreenShipArrivalVo todayShipArrival = new ScreenShipArrivalVo();
        todayShipArrival.setKey("today");
        todayShipArrival.setChildrenMaterialVo(todayList);
        todayShipArrival.setCount(todayList.stream().mapToInt(DockMaterialVo::getCount).sum());

        ScreenShipArrivalVo tomorrowShipArrival = new ScreenShipArrivalVo();
        tomorrowShipArrival.setKey("tomorrow");
        tomorrowShipArrival.setChildrenMaterialVo(tomorrowList);
        tomorrowShipArrival.setCount(tomorrowList.stream().mapToInt(DockMaterialVo::getCount).sum());

        return List.of(todayShipArrival, tomorrowShipArrival);
    }

    @Override
    @DataScope(deptAlias = "d")
    public Map<String, String> screenThroughput(DockPlan dockPlanMonth) {

        Date today = new Date();
//        dockPlanMonth = new DockPlan();
        dockPlanMonth.setEndStartTime(DateUtil.beginOfMonth(today).toLocalDateTime());
        dockPlanMonth.setEndEndTime(DateUtil.endOfMonth(today).toLocalDateTime());

        List<DockPlan> monthDPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlanMonth);

        // 本月吞吐
        // 总重量（排除状态为 抛泊 和 移泊）
        BigDecimal monthTotalWeight = monthDPlanList.stream()
                .filter(x -> StringUtils.equalsAny(x.getStatus(), "5", "6", "7")) // 待离泊、待离港、已完成
                .map(DockPlan::getTonnage)
                .filter(StringUtils::isNotBlank)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // 昨日吞吐
        DateTime yesterday = DateUtil.yesterday();
//        dockPlanMonth = new DockPlan();
        dockPlanMonth.setEndStartTime(DateUtil.beginOfDay(yesterday).toLocalDateTime());
        dockPlanMonth.setEndEndTime(DateUtil.endOfDay(yesterday).toLocalDateTime());

        List<DockPlan> yesterdayDPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlanMonth);
        BigDecimal yesterdayTotalWeight = yesterdayDPlanList.stream()
                .filter(x -> StringUtils.equalsAny(x.getStatus(), "5", "6", "7")) // 待离泊、待离港、已完成
                .map(DockPlan::getTonnage)
                .filter(StringUtils::isNotBlank)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

//        dockPlanMonth = new DockPlan();
        dockPlanMonth.setEndStartTime(null);
        dockPlanMonth.setEndEndTime(null);
        dockPlanMonth.setYear(1L);
        List<DockPlan> yearPlanList = dockPlanMapper.selectDockPlanLeftPierList(dockPlanMonth);
        BigDecimal yearTotalWeight = yearPlanList.stream()
                .filter(x -> StringUtils.equalsAny(x.getStatus(), "5", "6", "7")) // 待离泊、待离港、已完成
                .map(DockPlan::getTonnage)
                .filter(StringUtils::isNotBlank)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return Map.of("yesterday", yesterdayTotalWeight.toString(),
                "month", monthTotalWeight.toString(),
                "year", yearTotalWeight.toString()
        );
    }

    @Override
    public int updateScreenStatus(Long id, String screenStatus) {
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
        if (dockPlan == null) {
            throw new ServiceException("操作失败，没有查询到计划单，请联系管理员");
        }
        dockPlan.setScreenStatus(screenStatus);
        return dockPlanMapper.updateScreenStatus(dockPlan);
    }


    private static final Map<String, BigDecimal> RANGE_STANDARD_TYPE1MAP = new HashMap<>();
    private static final Map<String, BigDecimal> RANGE_STANDARD_TYPE2MAP = new HashMap<>();


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
    public int updateReason(ScreenProgressVo screenProgressVo) {

        Long id = screenProgressVo.getId();
        String berthName = screenProgressVo.getBerthName();
        String progressStr = screenProgressVo.getProgress();
        String reason = screenProgressVo.getReason();
        String type = screenProgressVo.getType();

        String exceptionReasonKey = String.format(RedisKeyConstants.PROGRESS_EXCEPTION_REASON, type, id, berthName);
//        String exceptionReasonKey = String.format(RedisKeyConstants.PROGRESS_EXCEPTION_REASON,id, berthName);

        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(screenProgressVo.getId());
        if (dockPlan == null) {
            throw new ServiceException("操作失败，没有查询到计划单");
        }
        BigDecimal number = extractFirstNumber(progressStr);


        // 大码头
        if (StringUtils.equals("type1", type)) {
            if (number.compareTo(BigDecimal.ZERO) >= 0 && number.compareTo(new BigDecimal("70")) < 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("0-70", reason);
                redisCache.setCacheMap(exceptionReasonKey, map, 7, TimeUnit.DAYS);
            } else {
                for (Map.Entry<String, BigDecimal> entry : RANGE_STANDARD_TYPE1MAP.entrySet()) {
                    String range = entry.getKey();

                    String[] bounds = range.split("-");
                    BigDecimal lower = new BigDecimal(bounds[0]);
                    BigDecimal upper = new BigDecimal(bounds[1]);

                    if (number.compareTo(lower) >= 0 && number.compareTo(upper) < 0) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(range, reason);
                        redisCache.setCacheMap(exceptionReasonKey, map, 7, TimeUnit.DAYS);
                        break;
                    }
                }
            }


        } else if (StringUtils.equals("type2", type)) {

            for (Map.Entry<String, BigDecimal> entry : RANGE_STANDARD_TYPE2MAP.entrySet()) {
                String range = entry.getKey();
                BigDecimal standard = entry.getValue();

                String[] bounds = range.split("-");
                BigDecimal lower = new BigDecimal(bounds[0]);
                BigDecimal upper = new BigDecimal(bounds[1]);

                if (number.compareTo(lower) >= 0 && number.compareTo(upper) < 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(range, reason);
                    redisCache.setCacheMap(exceptionReasonKey, map, 7, TimeUnit.DAYS);
                    break;
                }
            }

        }


        return 1;
    }

    @Override
    public List<DockPlan> selectScreenDockByBerthId(Long hbId) {
        return dockPlanMapper.selectScreenDockByBerthId(hbId);
    }

    private BigDecimal extractFirstNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return new BigDecimal(matcher.group());
        }
        return BigDecimal.ZERO;
    }
    @Autowired
    private DockMaterialMapper dockMaterialMapper;
    private String getUploadSpeedUnit(String goodName){
        List<DockMaterial> dms = dockMaterialMapper.selectDockMaterialList(new DockMaterial());
        Map<String,String> resultToolMap =new HashMap<>();
        for(DockMaterial dm : dms){
            resultToolMap.put(dm.getMaterialName(),dm.getRemark01());
        }
        if("2".equals(resultToolMap.get(goodName))){
            return "PSC/H";
        }else {
            return "T/H";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int newInsertDockPlan(DockPlan dockPlan) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<DockPlanAssistant> subMaterials = objectMapper.convertValue(
                dockPlan.getParams().get(materialParams),
                new TypeReference<List<DockPlanAssistant>>() {}
        );
        dockPlan.setDelFlag("0");
        dockPlan.setSourceType("0");
        dockPlan.setUserId(SecurityUtils.getUserId());
        dockPlan.setDeptId(SecurityUtils.getDeptId());
        dockPlan.setCreateBy(SecurityUtils.getNickName());
        dockPlan.setCreateTime(LocalDateTime.now());
        updatePlanStatus(dockPlan);
        updateBerthStatus(dockPlan);
        if(subMaterials.size()>0){
            dockPlan.setRemark02(String.valueOf(subMaterials.size()));
        }
        int i = dockPlanMapper.insertDockPlan(dockPlan);
        if(subMaterials.size()>0){
            for(DockPlanAssistant dpa:subMaterials){
                dpa.setPlanId(dockPlan.getId());
            }
            dockPlanAssistantMapper.insertBatchDockPlanAssistant(subMaterials);
        }
        return i;
    }

    @Override
    public DockPlan selectById(Long id) {
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(id);
        DockPlanAssistant dpa=new DockPlanAssistant();
        dpa.setPlanId(id);
        List<DockPlanAssistant> dockPlanAssistants = dockPlanAssistantMapper.selectDockPlanAssistant(dpa);
        HashMap<String,Object> paramsMap = new HashMap<>();
        paramsMap.put(materialParams,dockPlanAssistants);
        int i = dockUnloadWorkMapper.getCountByPlanId(dockPlan.getId());
        if(i==0){
            paramsMap.put(allowSubMaterial,true);
        }else{
            paramsMap.put(allowSubMaterial,false);
        }
        dockPlan.setParams(paramsMap);
        return dockPlan;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer newUpdateDockPlan(DockPlan dockPlan) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<DockPlanAssistant> subMaterials = objectMapper.convertValue(
                dockPlan.getParams().get(materialParams),
                new TypeReference<List<DockPlanAssistant>>() {}
        );
        dockPlan.setUpdateBy(SecurityUtils.getNickName());
        dockPlan.setUpdateTime(LocalDateTime.now());
        updatePlanStatus(dockPlan);
        updateBerthStatus(dockPlan);
//        if("4".equals(dockPlan.getStatus())){
//            dockPlan.setUnloadStatus("1");
//        }
        if(subMaterials.size()>0){
            dockPlan.setRemark02(String.valueOf(subMaterials.size()));
        }else{
            dockPlan.setRemark02(null);
        }
        refreshWindowPeriodLog(dockPlan);
        Integer i = dockPlanMapper.updateDockPlan(dockPlan);
        Boolean allowUpdatePlanAssistant = (Boolean) dockPlan.getParams().get(allowSubMaterial);
        if(allowUpdatePlanAssistant){
            dockPlanAssistantMapper.delByPlanId(dockPlan.getId());
            if(subMaterials.size()>0){
                for(DockPlanAssistant dpa:subMaterials){
                    dpa.setPlanId(dockPlan.getId());
                }
                dockPlanAssistantMapper.insertBatchDockPlanAssistant(subMaterials);
            }
        }
        return i;
    }

    @Override
    @Transactional
    public void submitUnloadWork(DockPlanAssistant dpa) {
        DockPlanUnloadWeightUpdateLogs dpuwul = new DockPlanUnloadWeightUpdateLogs();
        dpuwul.setPlanId(dpa.getPlanId());
        dpuwul.setLoadSequence(dpa.getLoadSequence());
        dpuwul.setUserId(SecurityUtils.getUserId());
        dpuwul.setUserName(SecurityUtils.getUsername());
        dpuwul.setNickName(SecurityUtils.getNickName());
        dpuwul.setRefreshTime(LocalDateTime.now());
        DockPlanUnloadWeightUpdateLogs newestItem = dpuwulm.getNewestItem(dpa.getPlanId());
        if(newestItem!=null){
            Long seconds = Duration.between(newestItem.getRefreshTime(), dpuwul.getRefreshTime()).getSeconds();
            BigDecimal hours = new BigDecimal(seconds)
                    .divide(new BigDecimal(3600), 2, BigDecimal.ROUND_HALF_UP);
            dpuwul.setDistanceLastTime(hours);
        }else{
            dpuwul.setDistanceLastTime(BigDecimal.ZERO);
        }
        dpuwul.setNewUnloadWeight(dpa.getUnloadWeight());
        if(dpa.getLoadSequence()==1){
            DockPlan dockPlan = dpuwulm.selectMainOldUnloadWeight(dpa.getPlanId());
            dpuwul.setOldUnloadWeight(dockPlan.getUnloadWeight()!=null?new BigDecimal(dockPlan.getUnloadWeight()):BigDecimal.ZERO);
            dpuwul.setUsageUnit(dockPlan.getUsageUnit());
            dpuwul.setMaterialName(dockPlan.getMaterialName());
            dpuwul.setPackageNum(dockPlan.getPackageNum()==null?1:dockPlan.getPackageNum().intValue());
            DockPlan dockplan=new DockPlan();
            dockplan.setId(dpa.getPlanId());
            dockplan.setUnloadWeight(String.valueOf(dpa.getUnloadWeight()));
            dockPlanAssistantMapper.submitUnloadWorkForPlan(dockplan);
        }else{
            DockPlanAssistant dockPlanAssistant = dpuwulm.selectOtherOldUnloadWeight(dpa.getPlanId(), dpa.getLoadSequence());
            dpuwul.setOldUnloadWeight(dockPlanAssistant.getUnloadWeight()!=null?dockPlanAssistant.getUnloadWeight():BigDecimal.ZERO);
            dpuwul.setUsageUnit(dockPlanAssistant.getUsageUnit());
            dpuwul.setMaterialName(dockPlanAssistant.getMaterialName());
            dpuwul.setPackageNum(dockPlanAssistant.getPackageNum());
            dockPlanAssistantMapper.submitUnloadWorkForPlanAssistant(dpa);
        }
        dpuwulm.inertItem(dpuwul);
    }
    //新版--计算收费
    private BigDecimal calculateCollectFee(Long planId,LocalDateTime dockingTime,LocalDateTime outBerthTime,BigDecimal contractFee){
        LocalDateTime now = LocalDateTime.now();
        if(contractFee==null || dockingTime==null || dockingTime.isAfter(now)){//停泊时间不存在或停泊时间在将来
            return BigDecimal.ZERO;
        }else{
            Long differenceValueSecond;
            if(outBerthTime==null||outBerthTime.isAfter(now)){//离泊时间不存在或离泊时间在将来，那拿现在作为结束时间
                differenceValueSecond = Duration.between(dockingTime,now).getSeconds();
            }else{//停泊时间为开始时间，离泊时间为结束时间
                differenceValueSecond = Duration.between(dockingTime,outBerthTime).getSeconds();
            }
            DockWindowPeriod dwp=new DockWindowPeriod();
            dwp.setPlanId(planId);
            dwp.setAvoidCollectFee(true);
            List<DockWindowPeriod> dockWindowPeriods = dockWindowPeriodMapper.selectDockWindowPeriodList(dwp);
            Long sumFreeSounds=43200L;
            for(DockWindowPeriod period:dockWindowPeriods){
                if(period.getAvoidCollectFee()){
                    LocalDateTime t1 = parseStrict(period.getRemark1());
                    LocalDateTime t2 = parseStrict(period.getRemark2());
                    if(t1!=null&&t2!=null){
                        Long diffSeconds = Duration.between(t1,t2).getSeconds();
                        sumFreeSounds += diffSeconds;
                    }
                }
            }
            if(differenceValueSecond<sumFreeSounds){
                differenceValueSecond = 0L;
            }else {
                differenceValueSecond-=sumFreeSounds;
            }
            BigDecimal differenceValueDays = BigDecimal.valueOf(differenceValueSecond).divide(BigDecimal.valueOf(86400),2, HALF_UP);
            return contractFee.multiply(differenceValueDays);
        }
    }
    //新版--工具方法的子方法
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
    /**：pc,app端修改联动空窗期日志的操作
     *PC端APP端修改（不同方法）时都校验一下：
     * 	1、查到该计划下所有空窗期日志；
     * 	2、校验所有有时间的：
     * 		2.1、（有时间有日志）时间不同：直接修改空窗期日志时间
     * 		2.2、（有时间有日志）时间相同：不管
     * 		2.3、有时间没日志：后面校验去填（不管）
     * 		2.4、没时间有日志：删掉这个日志
     */
    private void refreshWindowPeriodLog(DockPlan dockPlan){
        DockWindowPeriod dwp=new DockWindowPeriod();
        dwp.setPlanId(dockPlan.getId());
        List<DockWindowPeriod> dockWindowPeriods = dockWindowPeriodMapper.selectDockWindowPeriodList(dwp);
        dockWindowPeriods.forEach(period->{
            if(period.getPeriodType()==0){
                if(dockPlan.getArrivalTime()==null || dockPlan.getDockingTime()==null){
                    dockWindowPeriodMapper.deleteDockWindowPeriod(period.getId());
                }else {
                    if(!dockPlan.getArrivalTime().equals(period.getStartTime()) || !dockPlan.getDockingTime().equals(period.getEndTime())){
                        period.setStartTime(dockPlan.getArrivalTime());
                        period.setEndTime(dockPlan.getDockingTime());
                        dockWindowPeriodMapper.updateDockWindowPeriod(period);
                    }
                }
            } else if (period.getPeriodType()==1) {
                if(dockPlan.getDockingTime()==null || dockPlan.getOperationTime()==null){
                    dockWindowPeriodMapper.deleteDockWindowPeriod(period.getId());
                }else {
                    if(!dockPlan.getDockingTime().equals(period.getStartTime()) || !dockPlan.getOperationTime().equals(period.getEndTime())){
                        period.setStartTime(dockPlan.getDockingTime());
                        period.setEndTime(dockPlan.getOperationTime());
                        dockWindowPeriodMapper.updateDockWindowPeriod(period);
                    }
                }
            } else if (period.getPeriodType()==2) {
                if(dockPlan.getEndTime()==null || dockPlan.getOutBerthTime()==null){
                    dockWindowPeriodMapper.deleteDockWindowPeriod(period.getId());
                }else {
                    if(!dockPlan.getEndTime().equals(period.getStartTime()) || !dockPlan.getOutBerthTime().equals(period.getEndTime())){
                        period.setStartTime(dockPlan.getEndTime());
                        period.setEndTime(dockPlan.getOutBerthTime());
                        dockWindowPeriodMapper.updateDockWindowPeriod(period);
                    }
                }
            }
        });
    }
}