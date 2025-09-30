package com.iwip.harbor.service;

import com.iwip.common.core.domain.BaseEntity;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockPlanAssistant;
import com.iwip.harbor.domain.DockStartWorkVo;
import com.iwip.harbor.domain.excel.DockPlanExcel;
import com.iwip.harbor.domain.excel.DockPlanImportExcel;
import com.iwip.harbor.domain.excel.DockPlanRateExcel;
import com.iwip.harbor.domain.excel.DockUnloadWeighExcel;
import com.iwip.harbor.domain.screen.*;
import com.iwip.harbor.domain.vo.ScreenProgressVo;

import java.util.List;
import java.util.Map;

/**
 * 计划单Service接口
 *
 * @author Fei
 * @date 2025-01-28
 */
public interface IDockPlanService
{
    /**
     * 查询计划单
     *
     * @param id 计划单主键
     * @return 计划单
     */
    DockPlan selectDockPlanById(Long id);

    /**
     * 查询计划单列表
     *
     * @param dockPlan 计划单
     * @return 计划单集合
     */
    List<DockPlan>  selectDockPlanList(DockPlan dockPlan);

    /**
     * 新增计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    int insertDockPlan(DockPlan dockPlan);

    /**
     * 修改计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    int updateDockPlan(DockPlan dockPlan);

    /**
     * 批量删除计划单
     *
     * @param ids 需要删除的计划单主键集合
     * @return 结果
     */
    int deleteDockPlanByIds(Long[] ids);

    int updateStatus(DockPlan dockPlan);


    String importShipPlan(List<DockPlanImportExcel> userList, String operName,boolean updateSupport);

    List<DockPlanExcel> exportDockPlanList(DockPlan dockPlan);

    /**
     * 查询大屏列表
     */
    List<ScreenPlanBerthVo> screenPlanBerthList();


    /**
     * 大屏查看所有等泊的计划单
     */
    List<ScreenWaitBerthVo> screenWaitBerthList();


    List<ScreenPlanStatusVo> screenPlanStatusList(BaseEntity baseEntity);


    Map<String, String> screenCountMap();


    int updatePlanBerth(DockPlan dockPlan);



    List<ScreenPlanVo> selectPlanScreenWorkList(DockPlan dockPlan);

    int moveBerth(DockPlan dockPlan);

    Map<Object, Object> summaryCalculation(DockPlan dockPlan);

    List<ScreenWorkPlan> screenPierPlanList(String periType);

    /**
     * 查询船讯网数据
     * @return
     */
    List<ScreenGeoJsonVo> selectGeoJsonList();

    int archived(Long[] ids, String archivedMonth);

    int cancelArchived(Long[] ids);

    List<DockPlanRateExcel> effectiveRateExport(BaseEntity baseEntity);

    int importEffectiveRate(List<DockPlanRateExcel> planRateExcelList);

    int updateDockPlanRate(DockPlan dockPlan);

    int appEdit(DockPlan dockPlan);

    int startWork(DockStartWorkVo dockStartWorkVo);

    int batchUpdateUnloadWeight(List<DockUnloadWeighExcel> planRateExcelList);

    List<DockUnloadWeighExcel> exportUnloadWeight(DockPlan dockPlan);

    int updateStatusToDocking(DockPlan dockPlan);

    int toDocking(DockPlan dockPlan);

    int planDocking(DockPlan dockPlan);


    List<ScreenShipArrivalVo> screenShipArrivalStatistics();

    Map<String, String> screenThroughput(DockPlan dockPlanMonth);

    int updateScreenStatus(Long id,String screenStatus);

    int updateReason(ScreenProgressVo screenProgressVo);

    List<DockPlan> selectScreenDockByBerthId(Long berthId);

    int newInsertDockPlan(DockPlan dockPlan);

    DockPlan selectById(Long id);

    Integer newUpdateDockPlan(DockPlan dockPlan);

    void submitUnloadWork(DockPlanAssistant dpa);
}
