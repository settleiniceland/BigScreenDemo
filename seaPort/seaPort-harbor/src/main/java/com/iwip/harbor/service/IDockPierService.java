package com.iwip.harbor.service;

import java.util.List;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPier;
import com.iwip.harbor.domain.screen.ScreenPierVo;
import com.iwip.harbor.domain.vo.DockPierAndBerthVo;

/**
 * 码头信息Service接口
 * 
 * @author Fei
 * @date 2025-01-28
 */
public interface IDockPierService 
{
    /**
     * 查询码头信息
     * 
     * @param dpId 码头信息主键
     * @return 码头信息
     */
    public DockPier selectDockPierByDpId(Long dpId);

    /**
     * 查询码头信息列表
     * 
     * @param dockPier 码头信息
     * @return 码头信息集合
     */
    public List<DockPier> selectDockPierList(DockPier dockPier);

    /**
     * 新增码头信息
     * 
     * @param dockPier 码头信息
     * @return 结果
     */
    public int insertDockPier(DockPierAndBerthVo dockPier);

    /**
     * 修改码头信息
     * 
     * @param dockPier 码头信息
     * @return 结果
     */
    public int updateDockPier(DockPierAndBerthVo dockPier);

    /**
     * 批量删除码头信息
     * 
     * @param dpIds 需要删除的码头信息主键集合
     * @return 结果
     */
    public int deleteDockPierByDpIds(Long[] dpIds);

    /**
     * 删除码头信息信息
     * 
     * @param dpId 码头信息主键
     * @return 结果
     */
    public int deleteDockPierByDpId(Long dpId);

    List<DockPier> selectScreenPierList(DockPier dockPier);

    List<DockPier> selectList(DockPier dockPier);

    List<ScreenPierVo> selectScreenPierLeftBerthList(DockBerth dockBerth);
}
