package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockUnloadWork;
import com.iwip.harbor.domain.excel.DockUnloadExcel;
import com.iwip.harbor.domain.screen.ScreenUnloadVo;
import com.iwip.harbor.domain.vo.DockUnloadVo;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 卸货单Service接口
 * 
 * @author ruoyi
 * @date 2025-02-05
 */
public interface IDockUnloadWorkService 
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
     * 批量删除卸货单
     * 
     * @param duIds 需要删除的卸货单主键集合
     * @return 结果
     */
    public int deleteDockUnloadWorkByDuIds(Long[] duIds);

    /**
     * 删除卸货单信息
     * 
     * @param duId 卸货单主键
     * @return 结果
     */
    public int deleteDockUnloadWorkByDuId(Long duId);


    /**
     * 暂停
     * @param dockUnloadVo 卸货单
     * @return 结果
     */
    int stop(DockUnloadVo dockUnloadVo);

    /**
     * 恢复
     * @param dockUnloadVo 卸货单
     * @return 结果
     */
    int recover(DockUnloadVo dockUnloadVo);


    int jobOver(DockUnloadVo dockUnloadVo);


    List<DockUnloadWork> selectUnloadWorkListByPlanId(Long planId);

    List<DockUnloadExcel> exportDockUnloadWorkList(DockUnloadWork dockUnloadWork);

    List<ScreenUnloadVo> screenUnloadList();

    DockUnloadWork selectDockUnloadWorkDesc(Long id);

    Map<Object, Object> summaryCalculation(DockUnloadWork dockUnloadWork);

    int jobComplete(Long planId);

    int cancelComplete(Long planId);


    void createUnloadWork(DockUnloadWork unloadWork);

    int appUpdateUnload(DockUnloadVo dockUnloadVo);
}
