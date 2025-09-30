package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockHourUnloadLog;
import com.iwip.harbor.domain.vo.DockPlanEfficiencyHourVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 记录每小时卸货的日志Service接口
 *
 * @author @IWIP
 * @date 2025-04-17
 */
public interface IDockHourUnloadLogService
{
    /**
     * 查询记录每小时卸货的日志
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 记录每小时卸货的日志
     */
    public DockHourUnloadLog selectDockHourUnloadLogByDhuId(Long dhuId);

    /**
     * 查询记录每小时卸货的日志列表
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 记录每小时卸货的日志集合
     */
    public List<DockHourUnloadLog> selectDockHourUnloadLogList(DockHourUnloadLog dockHourUnloadLog);

    /**
     * 新增记录每小时卸货的日志
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 结果
     */
    public int insertDockHourUnloadLog(DockHourUnloadLog dockHourUnloadLog);

    /**
     * 修改记录每小时卸货的日志
     *
     * @param dockHourUnloadLog 记录每小时卸货的日志
     * @return 结果
     */
    public int updateDockHourUnloadLog(DockHourUnloadLog dockHourUnloadLog);

    /**
     * 批量删除记录每小时卸货的日志
     *
     * @param dhuIds 需要删除的记录每小时卸货的日志主键集合
     * @return 结果
     */
    public int deleteDockHourUnloadLogByDhuIds(Long[] dhuIds);

    /**
     * 删除记录每小时卸货的日志信息
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 结果
     */
    public int deleteDockHourUnloadLogByDhuId(Long dhuId);

    List<DockPlanEfficiencyHourVo> efficiencyByHour(DockHourUnloadLog dockHourUnloadLog);

    Map<String, BigDecimal> calculateUnloadingRate(DockHourUnloadLog dockHourUnloadLog);
}
