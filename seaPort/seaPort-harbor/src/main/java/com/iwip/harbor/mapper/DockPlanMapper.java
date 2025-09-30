package com.iwip.harbor.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.iwip.common.core.domain.BaseEntity;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.excel.DockBerthUsaDetailExcel;
import com.iwip.harbor.domain.excel.DockUnloadWeighExcel;
import com.iwip.harbor.domain.param.AppPierPlanParam;
import com.iwip.harbor.domain.screen.*;
import com.iwip.harbor.domain.vo.AppPierPlanVo;
import com.iwip.harbor.domain.vo.DockMaterialVo;
import com.iwip.harbor.domain.vo.DockPlanDockingVo;
import jakarta.websocket.server.PathParam;
import org.apache.ibatis.annotations.Param;

/**
 * 计划单Mapper接口
 *
 * @author Fei
 * @date 2025-01-28
 */
public interface DockPlanMapper
{
    /**
     * 查询计划单
     *
     * @param id 计划单主键
     * @return 计划单
     */
    public DockPlan selectDockPlanById(Long id);

    /**
     * 查询计划单列表
     *
     * @param dockPlan 计划单
     * @return 计划单集合
     */
    public List<DockPlan> selectDockPlanList(DockPlan dockPlan);
    public List<DockPlan> selectDockPlanLeftPierList(DockPlan dockPlan);

    /**
     * 新增计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    public int insertDockPlan(DockPlan dockPlan);

    /**
     * 修改计划单
     *
     * @param dockPlan 计划单
     * @return 结果
     */
    public int updateDockPlan(DockPlan dockPlan);

    /**
     * 删除计划单
     *
     * @param id 计划单主键
     * @return 结果
     */
    public int deleteDockPlanById(Long id);

    /**
     * 批量删除计划单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockPlanByIds(Long[] ids);

    int removeDockPlanByDbId(DockPlan dockPlan);

    // 查询工作中的泊位信息
    List<ScreenPlanBerthVo> screenPlanBerthList();


    // 查询大屏等泊信息
    List<ScreenWaitBerthVo> screenWaitBerthList();

    // 查询计划单状态
    List<ScreenPlanStatusVo> screenPlanStatusList(BaseEntity baseEntity);

    List<DockPlan> screenPlanTimeList(DockPlan dockPlan);

    /** 查询等泊状态根据到港时间排序取第一条 等泊最久的一条 */
    List<ScreenPlanVo> selectPlanByHbId(Long berthId);


    int updatePlanBerth(DockPlan dockPlan);
    int updatePlanByPLC(DockPlan dockPlan);


    List<DockPlan> selectDockPlanByStatus(DockPlan dockPlan);

    List<ScreenWorkPlan> screenPierPlanList(String periType);

    List<ScreenGeoJsonVo> screenGeoJsonPlanList();

    int updateDockPlanRate(DockPlan dockPlan);

    List<DockPlan> selectEffectiveRateList(BaseEntity baseEntity);

    List<ScreenPlanVo> selectPlanScreenWorkList(DockPlan dockPlan);

    int updateUnloadWeigh(DockUnloadWeighExcel dockUnloadWeighExcel);

    DockPlan selectDockPlanByParams(DockPlan dockPlan);

    int updateStatus(DockPlan dockPlan);

    List<DockMaterialVo> selectDockMaterialList(BaseEntity baseEntity);

    List<DockMaterialVo> shipArrivalStatistics(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<AppPierPlanVo> selectAppPierPlanList(@Param("param") AppPierPlanParam appPierPlanParam);

    List<Long> selectDockPlanWithTaskByIds(@Param("ids") List<Long> ids);

    List<DockPlanDockingVo> selectListByDockingTimeAndOutBerthTime(@Param("dockingStartTime") LocalDateTime dockingStartTime, @Param("dockingEndTime") LocalDateTime dockingEndTime, @Param("outBerthStartTime") LocalDateTime outBerthStartTime, @Param("outBerthEndTime") LocalDateTime outBerthEndTime);

    int updateScreenStatus(DockPlan dockPlan);

    List<DockBerthUsaDetailExcel> selectListByDockingTime(String yearMonth);

    List<DockPlan> selectScreenDockByBerthId(Long berthId);

    List<DockPlan> selectPlanListForTask(DockPlan dpParam);

    void updatePlanUnloadWeightById(Long id, String string);

    /*👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇新大屏方法👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇*/
    List<DockPlan> newScreen_SelectPlanList1(@Param("todayStart") LocalDateTime todayStart,
                                             @Param("todayEnd") LocalDateTime todayEnd,
                                             @Param("tomorrowStart")LocalDateTime tomorrowStart,
                                             @Param("tomorrowEnd")LocalDateTime tomorrowEnd,
                                             @Param("deptIds")List<Long> deptIds);
    List<DockPlan> newScreen_SelectPlanList2(List<Long> deptIds);
    List<DockPlan> newScreen_SelectPlanList3(List<Long> deptIds);
    List<DockPlan> newScreen_SelectPlanListForMap(List<Long> deptIds);
    /*👇👇👇第二版大屏方法👇👇👇👇*/
    List<DockPlan> newNewScreen_SelectPlanListForPup(@Param("berchCode")String berchCode,@Param("list")List<Long> deptIds);
}
