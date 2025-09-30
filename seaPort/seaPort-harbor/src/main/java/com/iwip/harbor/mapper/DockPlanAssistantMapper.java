package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockPlanAssistant;
import io.lettuce.core.dynamic.annotation.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DockPlanAssistantMapper {
    List<DockPlanAssistant> selectDockPlanAssistant(DockPlanAssistant dockPlanAssistant);
    Integer insertBatchDockPlanAssistant(@Param("list") List<DockPlanAssistant> dockPlanAssistants);
    Integer delByPlanId(@Param("planId") Long planId);
    List<DockPlanAssistant> selectByBatchPlanIds(@Param("list") List<Long> planIds);
    Integer updateById(DockPlanAssistant dockPlanAssistant);
    void submitUnloadWorkForPlan(DockPlan dockPlan);
    void submitUnloadWorkForPlanAssistant(DockPlanAssistant dockPlanAssistant);
    void updateUnloadWeightById(Long id, BigDecimal weightData);
}
