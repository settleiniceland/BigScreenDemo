package com.iwip.harbor.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockHourUnloadLog;
import com.iwip.harbor.domain.DockOutputRange;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockUnloadWork;
import com.iwip.harbor.domain.screen.ScreenPlanVo;
import com.iwip.harbor.domain.vo.DockPlanEfficiencyHourVo;
import com.iwip.harbor.mapper.DockHourUnloadLogMapper;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.service.IDockHourUnloadLogService;
import com.iwip.harbor.service.IDockOutputRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.iwip.common.utils.SecurityUtils.*;
import static java.math.RoundingMode.HALF_UP;

/**
 * 记录每小时卸货的日志Service业务层处理
 *
 * @author @IWIP
 * @date 2025-04-17
 */
@Service
public class DockHourUnloadLogServiceImpl implements IDockHourUnloadLogService
{

    @Autowired
    private DockPlanMapper dockPlanMapper;

    @Autowired
    private DockHourUnloadLogMapper dockHourUnloadLogMapper;

    @Autowired
    private IDockOutputRangeService dockOutputRangeService;

    /**
     * 查询记录每小时卸货的日志
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 记录每小时卸货的日志
     */
    @Override
    public DockHourUnloadLog selectDockHourUnloadLogByDhuId(Long dhuId)
    {
        return dockHourUnloadLogMapper.selectDockHourUnloadLogByDhuId(dhuId);
    }

    /**
     * 查询记录每小时卸货的日志列表
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 记录每小时卸货的日志
     */
    @Override
    public List<DockHourUnloadLog> selectDockHourUnloadLogList(DockHourUnloadLog dockHourUnloadLog)
    {
        Long planId = dockHourUnloadLog.getPlanId();
        if (planId == null) {
            throw new ServiceException("planId must not be null！");
        }
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(planId);
        if (dockPlan == null) {
            throw new ServiceException("计划单不存在！");
        }
        return dockHourUnloadLogMapper.selectDockHourUnloadLogList(dockHourUnloadLog);
    }

    /**
     * 新增记录每小时卸货的日志
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 结果
     */
    @Override
    public int insertDockHourUnloadLog(DockHourUnloadLog dockHourUnloadLog)
    {

        checkInsertAndUpdate(dockHourUnloadLog);

        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockHourUnloadLog.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("计划单不存在！");
        }

        // 如果目前没有卸货日志，第一条的卸货日志的开始时间 需要是 计划单的作业开始时间 TODO 默认会在计划单编辑完作业时间之后添加一条每小时的卸货日志
        DockHourUnloadLog dockHourUnloadLogSelect = new DockHourUnloadLog();
        dockHourUnloadLogSelect.setPlanId(dockHourUnloadLog.getPlanId());
        List<DockHourUnloadLog> dockHourUnloadLogs = dockHourUnloadLogMapper.selectDockHourUnloadLogList(dockHourUnloadLogSelect);
        if (dockHourUnloadLogs.isEmpty()) {
            // if (!Objects.equals(dockHourUnloadLog.getStartTime(), dockPlan.getOperationTime())) {
            //     throw new ServiceException("第一条卸货日志的开始时间必须是计划单的作业时间！");
            // }
        } else {
            DockHourUnloadLog last = dockHourUnloadLogs.get(dockHourUnloadLogs.size() - 1);
            if (dockHourUnloadLog.getStartTime().isBefore(last.getEndTime())) {
                throw new ServiceException("本条卸货日志的开始时间不能小于之前卸货日志的结束时间！");
            }
            checkUnloadWeightAndNum(dockHourUnloadLog, dockHourUnloadLogs);
        }

        dockHourUnloadLog.setCreateBy(getNickName());
        dockHourUnloadLog.setUserId(getUserId());
        dockHourUnloadLog.setCreateTime(LocalDateTime.now());
        dockHourUnloadLog.setDeptId(SecurityUtils.getDeptId());
        return dockHourUnloadLogMapper.insertDockHourUnloadLog(dockHourUnloadLog);
    }

    /**
     * 修改记录每小时卸货的日志
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 结果
     */
    @Override
    public int updateDockHourUnloadLog(DockHourUnloadLog dockHourUnloadLog)
    {
        if (dockHourUnloadLog.getDhuId() == null) {
            throw new ServiceException("dhuId must not be null！");
        }

        DockHourUnloadLog dockHourUnloadLogByDhuId = dockHourUnloadLogMapper.selectDockHourUnloadLogByDhuId(dockHourUnloadLog.getDhuId());

        if (dockHourUnloadLogByDhuId == null) {
            throw new ServiceException("该卸货日志不存在！");
        }

        checkInsertAndUpdate(dockHourUnloadLog);

        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockHourUnloadLog.getPlanId());
        if (dockPlan == null) {
            throw new ServiceException("计划单不存在！");
        }

        // 如果目前没有卸货日志，第一条的卸货日志的开始时间 需要是 计划单的作业开始时间
        DockHourUnloadLog dockHourUnloadLogSelect = new DockHourUnloadLog();
        dockHourUnloadLogSelect.setPlanId(dockHourUnloadLog.getPlanId());
        List<DockHourUnloadLog> dockHourUnloadLogs = dockHourUnloadLogMapper.selectDockHourUnloadLogList(dockHourUnloadLogSelect);

        if (!dockHourUnloadLogs.isEmpty()) {
            checkUnloadWeightAndNum(dockHourUnloadLog, dockHourUnloadLogs);
            // 如果修改的是第一条
            // DockHourUnloadLog dockHourUnloadLogNOOne = dockHourUnloadLogs.get(0);
            // if (Objects.equals(dockHourUnloadLogNOOne.getDhuId(), dockHourUnloadLog.getDhuId())
            //         && !Objects.equals(dockHourUnloadLog.getStartTime(), dockPlan.getOperationTime())) {
            //    throw new ServiceException("第一条卸货日志的开始时间必须是计划单的作业时间！");
            // }
            if (!DateUtils.isWholeHour(dockHourUnloadLog.getStartTime()) || !DateUtils.isWholeHour(dockHourUnloadLog.getEndTime())) {
                throw new ServiceException("开始时间和结束时间必须为整点！");
            }
        }

        dockHourUnloadLog.setUpdateBy(getNickName());
        dockHourUnloadLog.setUpdateTime(LocalDateTime.now());
        return dockHourUnloadLogMapper.updateDockHourUnloadLog(dockHourUnloadLog);
    }

    /**
     * 批量删除记录每小时卸货的日志
     *
     * @param dhuIds 需要删除的记录每小时卸货的日志主键
     * @return 结果
     */
    @Override
    public int deleteDockHourUnloadLogByDhuIds(Long[] dhuIds)
    {
        for (Long dhuId : dhuIds) {
            DockHourUnloadLog dockHourUnloadLogByDhuId = this.dockHourUnloadLogMapper.selectDockHourUnloadLogByDhuId(dhuId);
            if (dockHourUnloadLogByDhuId == null) {
                throw new ServiceException("卸货日志不存在！");
            }
            DockPlan dockPlan = dockPlanMapper.selectDockPlanById(dockHourUnloadLogByDhuId.getPlanId());
            if (dockPlan == null) {
                throw new ServiceException("计划单不存在！");
            }

            // DockHourUnloadLog dockHourUnloadLog = new DockHourUnloadLog();
            // dockHourUnloadLog.setPlanId(dockHourUnloadLogByDhuId.getPlanId());
            // List<DockHourUnloadLog> dockHourUnloadLogs = dockHourUnloadLogMapper.selectDockHourUnloadLogList(dockHourUnloadLog);
            // if (Objects.equals(dhuId, dockHourUnloadLogs.get(0).getDhuId())) {
            //    throw new ServiceException("第一条卸货日志不可删除，只能编辑！");
            //}
        }
        return dockHourUnloadLogMapper.deleteDockHourUnloadLogByDhuIds(dhuIds);
    }

    /**
     * 删除记录每小时卸货的日志信息
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 结果
     */
    @Override
    public int deleteDockHourUnloadLogByDhuId(Long dhuId)
    {
        return dockHourUnloadLogMapper.deleteDockHourUnloadLogByDhuId(dhuId);
    }

    @Override
    public List<DockPlanEfficiencyHourVo> efficiencyByHour(DockHourUnloadLog dockHourUnloadLog) {
        if (dockHourUnloadLog.getPlanId() == null) {
            throw new ServiceException("planId must not be null！");
        }
        List<DockPlanEfficiencyHourVo> screenHourUnloadLogList = dockHourUnloadLogMapper.screenHourUnloadLogList(dockHourUnloadLog.getPlanId());
        return getHourlyRates(screenHourUnloadLogList);
    }

    public List<DockPlanEfficiencyHourVo> getHourlyRates(List<DockPlanEfficiencyHourVo> records) {
        List<DockPlanEfficiencyHourVo> efficiencyHourVos = new ArrayList<>();

        for (DockPlanEfficiencyHourVo record : records) {
            BigDecimal total = record.getUnloadWeight() != null ? new BigDecimal(record.getUnloadWeight()) : new BigDecimal(record.getUnloadNum());
            if (total.compareTo(BigDecimal.ZERO) == 0){
                continue;
            }
            DockPlanEfficiencyHourVo dockPlanEfficiencyHourVo = new DockPlanEfficiencyHourVo();
            dockPlanEfficiencyHourVo.setTime(record.getTime());
            dockPlanEfficiencyHourVo.setRate(total.toString());
            dockPlanEfficiencyHourVo.setUnloadWeight(total.toString());
            dockPlanEfficiencyHourVo.setExceptionReason(record.getExceptionReason());
            dockPlanEfficiencyHourVo.setExceptionStatus(record.getExceptionStatus());
            efficiencyHourVos.add(dockPlanEfficiencyHourVo);
        }
        return efficiencyHourVos;
    }

    @Override
    public Map<String, BigDecimal> calculateUnloadingRate(DockHourUnloadLog dockHourUnloadLog) {

        Map<String, BigDecimal> map = new HashMap<>();

        checkInsertAndUpdate(dockHourUnloadLog);

        Long planId = dockHourUnloadLog.getPlanId();
        DockPlan dockPlan = dockPlanMapper.selectDockPlanById(planId);
        List<DockOutputRange> dockOutputRangeList = dockOutputRangeService.selectRangeLeftBerthByPierId(dockPlan.getHbId());
        // 总吨数
        BigDecimal tonnageTotal = new BigDecimal(dockPlan.getTonnage());

        // 获取所有记录并加上当前记录
        List<DockHourUnloadLog> unloadLogList = dockHourUnloadLogMapper.selectDockHourUnloadLogByPlanId(planId);
        if (dockHourUnloadLog.getDhuId() == null){
            unloadLogList.add(dockHourUnloadLog);
        }else {
            // 删除原有记录，假设根据 dhuId 来删除
            unloadLogList.removeIf(log -> log.getDhuId().equals(dockHourUnloadLog.getDhuId()));
            // 添加更新后的记录
            unloadLogList.add(dockHourUnloadLog);
        }

        // 已卸吨数累加
        BigDecimal sumUnloadWeight = unloadLogList.stream()
                .map(log -> log.getUnloadWeight() != null ? log.getUnloadWeight() : log.getUnloadNum())
                .filter(weight -> weight != null && weight.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 当前进度百分比
        BigDecimal progress = sumUnloadWeight.divide(tonnageTotal, 4, HALF_UP)
                .multiply(new BigDecimal("100"));

        // 每小时效率
        BigDecimal unloadWeight = dockHourUnloadLog.getUnloadWeight();

        map.put("hourRate",unloadWeight);
        map.put("progress",progress);

        // 判断在哪个卸率区间，并比较卸率大小
        boolean matched = false;

        for (DockOutputRange range : dockOutputRangeList) {
            if (progress.compareTo(range.getStartPercent()) >= 0 &&
                    progress.compareTo(range.getEndPercent()) < 0) {
                matched = true;
                int compare = unloadWeight.compareTo(range.getUnloadRate());
                if (compare >= 0) {
                    // 正常：高于标准
                    map.put("result", BigDecimal.valueOf(1));
                } else {
                    // 异常：低于标准
                    map.put("result", BigDecimal.valueOf(2));
                }
                break; // 找到一个区间后就可以退出循环
            }
        }
        if (!matched) {
            map.put("result", BigDecimal.valueOf(3));
        }

        return map;
    }





    private void checkInsertAndUpdate(DockHourUnloadLog dockHourUnloadLog) {
        if (dockHourUnloadLog.getPlanId() == null) {
            throw new ServiceException("planId must not be null！");
        }

        if (Objects.isNull(dockHourUnloadLog.getStartTime())) {
            throw new ServiceException("startTime must not be null！");
        }

        if (Objects.isNull(dockHourUnloadLog.getEndTime())) {
            throw new ServiceException("endTime must not be null！");
        }

        if (dockHourUnloadLog.getStartTime().isAfter(dockHourUnloadLog.getEndTime())) {
            throw new ServiceException("开始时间不能大于结束时间！");
        }

        if (!DateUtils.isWholeHour(dockHourUnloadLog.getStartTime()) || !DateUtils.isWholeHour(dockHourUnloadLog.getEndTime())) {
            throw new ServiceException("开始时间和结束时间必须为整点！");
        }

        if (Objects.isNull(dockHourUnloadLog.getUnloadWeight()) && Objects.isNull(dockHourUnloadLog.getUnloadNum())) {
            throw new ServiceException("很抱歉，同一计划单下的所有卸货日志只能填写件数或吨数，不能同时填写两者。");
        }
    }

    private void checkUnloadWeightAndNum(DockHourUnloadLog dockHourUnloadLog, List<DockHourUnloadLog> dockHourUnloadLogs) {
        boolean hasUnloadNum = dockHourUnloadLogs.stream().anyMatch(x -> x.getUnloadNum() != null);
        boolean hasTotalUnloadWeight = dockHourUnloadLogs.stream().anyMatch(x -> x.getUnloadWeight() != null);

        if (dockHourUnloadLog.getUnloadWeight() != null && hasUnloadNum) {
            throw new ServiceException("很抱歉，同一计划单下的所有卸货日志只能填写件数或吨数，不能同时填写两者。");
        }

        if (dockHourUnloadLog.getUnloadNum() != null && hasTotalUnloadWeight) {
            throw new ServiceException("很抱歉，同一计划单下的所有卸货日志只能填写件数或吨数，不能同时填写两者。");
        }
    }

}
