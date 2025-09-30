package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockPlanAssistant;
import com.iwip.harbor.domain.DockPlanUnloadWeightUpdateLogs;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DockPlanUnloadWeightUpdateLogsMapper {
    Integer inertItem(DockPlanUnloadWeightUpdateLogs dpuwul);
    DockPlanUnloadWeightUpdateLogs getNewestItem(@Param("planId") Long planId);
    DockPlan selectMainOldUnloadWeight(@Param("id")Long id);
    DockPlanAssistant selectOtherOldUnloadWeight(Long planId, Integer loadSequence);
    List<DockPlanUnloadWeightUpdateLogs> getByPlanIds(List<Long> planIds);
    //计划mapper里面一堆老代码，太拥挤了，故放这
    BigDecimal getThroughput(@Param("st")LocalDateTime st,@Param("et")LocalDateTime et,@Param("deptIds")List<Long> deptIds);
}
