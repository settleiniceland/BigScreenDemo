package com.iwip.harbor.service;

import java.util.List;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.screen.ScreenBerthInfoVo;
import com.iwip.harbor.domain.screen.ScreenPierVo;
import com.iwip.harbor.domain.screen.ScreenPlanStatusVo;
import com.iwip.harbor.domain.screen.ScreenWorkPlan;
import com.iwip.harbor.domain.vo.AppDockBerthStatusStatisticsVo;
import com.iwip.harbor.domain.vo.DockPierAndBerthVo;

/**
 * 泊位信息Service接口
 *
 * @author Fei
 * @date 2025-01-28
 */
public interface IDockBerthService
{
    /**
     * 查询泊位信息
     *
     * @param dbId 泊位信息主键
     * @return 泊位信息
     */
    public DockBerth selectDockBerthByDbId(Long dbId);

    /**
     * 查询泊位信息列表
     *
     * @param dockBerth 泊位信息
     * @return 泊位信息集合
     */
    public List<DockBerth> selectDockBerthList(DockBerth dockBerth);


    /**
     * 新增泊位信息
     *
     * @param dockPierAndBerthVo 泊位信息
     * @return 结果
     */
    public int insertDockBerth(DockPierAndBerthVo dockPierAndBerthVo);

    /**
     * 修改泊位信息
     *
     * @param dockPierAndBerthVo 泊位信息
     * @return 结果
     */
    public int updateDockBerth(DockPierAndBerthVo dockPierAndBerthVo);

    public int updateDockBerthStatus(DockBerth dockBerth);

    /**
     * 批量删除泊位信息
     *
     * @param dbIds 需要删除的泊位信息主键集合
     * @return 结果
     */
    public int deleteDockBerthByDbIds(Long[] dbIds);

    /**
     * 删除泊位信息信息
     *
     * @param dbId 泊位信息主键
     * @return 结果
     */
    public int deleteDockBerthByDbId(Long dbId);

    int add(DockPierAndBerthVo dockPierAndBerthVo);

    int update(DockPierAndBerthVo dockPierAndBerthVo);

    int delete(DockPierAndBerthVo dockPierAndBerthVo);

    List<ScreenPlanStatusVo> screenBerthStatusList();

    List<ScreenPierVo> screenPierBerthPlanList();

    // 大屏按码头类型查询计划单
    List<ScreenWorkPlan> screenPierPlanList(String periType);

    List<AppDockBerthStatusStatisticsVo> appBerthStatusStatistics(DockBerth dockBerth);

}
