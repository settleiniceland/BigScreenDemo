package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockHourUnloadLog;
import com.iwip.harbor.domain.vo.DockPlanEfficiencyHourVo;

import java.util.List;

/**
 * 记录每小时卸货的日志Mapper接口
 *
 * @author @IWIP
 * @date 2025-04-17
 */
public interface DockHourUnloadLogMapper
{
    /**
     * 查询记录每小时卸货的日志
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 记录每小时卸货的日志
     */
    public DockHourUnloadLog selectDockHourUnloadLogByDhuId(Long dhuId);


    /**
     * 查询记录每小时卸货的日志
     *
     * @param planId 记录每小时卸货的日志主键
     * @return 记录每小时卸货的日志
     */
    public List<DockHourUnloadLog> selectDockHourUnloadLogByPlanId(Long planId);

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
     * 删除记录每小时卸货的日志
     *
     * @param dhuId 记录每小时卸货的日志主键
     * @return 结果
     */
    public int deleteDockHourUnloadLogByDhuId(Long dhuId);

    /**
     * 批量删除记录每小时卸货的日志
     *
     * @param dhuIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockHourUnloadLogByDhuIds(Long[] dhuIds);

    List<DockPlanEfficiencyHourVo> screenHourUnloadLogList(Long planId);
}
