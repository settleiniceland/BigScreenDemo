package com.iwip.harbor.service;

import com.iwip.common.core.domain.entity.SysDept;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPlan;

import javax.print.Doc;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface NewScreenService {
    List<SysDept> getAllDeptList();//所有部门
    List<DockBerth> getBerthListByDeptIds(List<Long> deptIds);//泊位
    Map<String, BigDecimal> getStatisticsData(List<Long> deptIds);//统计量（3个吞吐量）
    List<DockPlan> getSuchArrivePlanByDeptIds(List<Long> deptIds);//今日到船，明日到船，今日离泊，明日离泊这些
    List<DockPlan> getPlanPopDetailByPlanId(List<Long> deptIds,String berchCode);//地图弹窗数据
    List<DockPlan> getPlan2(List<Long> deptIds);//等泊计划
    List<DockPlan> getWorkingPlanByDeptIds(List<Long> deptIds);//装卸中计划(依然老对象，超大对象)
}
