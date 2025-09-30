package com.iwip.harbor.mapper;

import java.util.List;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPier;
import com.iwip.harbor.domain.screen.ScreenPierVo;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 码头信息Mapper接口
 * 
 * @author Fei
 * @date 2025-01-28
 */
public interface DockPierMapper 
{
    /**
     * 查询码头信息
     * 
     * @param dpId 码头信息主键
     * @return 码头信息
     */
    public DockPier selectDockPierByDpId(Long dpId);
    public DockPier selectDockPierByDpName(String pierName);
    public DockPier selectDockPierByDpCode(String pierCode);

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
    public int insertDockPier(DockPier dockPier);

    /**
     * 修改码头信息
     * 
     * @param dockPier 码头信息
     * @return 结果
     */
    public int updateDockPier(DockPier dockPier);

    /**
     * 删除码头信息
     * 
     * @param dpId 码头信息主键
     * @return 结果
     */
    public int deleteDockPierByDpId(Long dpId);

    /**
     * 批量删除码头信息
     * 
     * @param dpIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockPierByDpIds(Long[] dpIds);

    int removeDockPierByDpId(DockPier dockPier);

    List<DockPier> selectScreenPierList(DockPier dockPier);

    List<DockPier> selectList(DockPier dockPier);

    List<ScreenPierVo> selectScreenPierLeftBerthList(DockBerth dockBerth);

    /*👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇新大屏方法👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇*/
    List<DockPier> newScreen_SelectListByDeptIds(List<Long> deptIds);
}
