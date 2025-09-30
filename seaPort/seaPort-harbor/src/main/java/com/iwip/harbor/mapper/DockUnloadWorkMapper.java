package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockUnloadWork;
import com.iwip.harbor.domain.screen.ScreenUnloadVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 卸货单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-05
 */
public interface DockUnloadWorkMapper 
{
    /**
     * 查询卸货单
     * 
     * @param duId 卸货单主键
     * @return 卸货单
     */
    public DockUnloadWork selectDockUnloadWorkByDuId(Long duId);

    /**
     * 查询卸货单列表
     * 
     * @param dockUnloadWork 卸货单
     * @return 卸货单集合
     */
    public List<DockUnloadWork> selectDockUnloadWorkList(DockUnloadWork dockUnloadWork);

    /**
     * 新增卸货单
     * 
     * @param dockUnloadWork 卸货单
     * @return 结果
     */
    public int insertDockUnloadWork(DockUnloadWork dockUnloadWork);

    /**
     * 修改卸货单
     * 
     * @param dockUnloadWork 卸货单
     * @return 结果
     */
    public int updateDockUnloadWork(DockUnloadWork dockUnloadWork);

    /**
     * 删除卸货单
     * 
     * @param duId 卸货单主键
     * @return 结果
     */
    public int deleteDockUnloadWorkByDuId(Long duId);

    /**
     * 批量删除卸货单
     * 
     * @param duIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockUnloadWorkByDuIds(Long[] duIds);

    int removeDockPierByDpId(DockUnloadWork dockUnloadWork);

    DockUnloadWork selectUnloadWorkByTimeAndClass(@Param("planId") Long planId,@Param("classTime") LocalDate classTime, @Param("classes") String classes);

    List<DockUnloadWork> selectUnloadWorkListByPlanId(Long planId);

    List<ScreenUnloadVo> screenUnloadList();

    DockUnloadWork selectDockUnloadWorkDesc(Long planId);
    int getCountByPlanId(@Param("planId")Long planId);
    List<DockUnloadWork> selectByPlanIds(List<Long> planIds);
}
