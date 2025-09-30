package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockWindowPeriod;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DockWindowPeriodMapper {
    List<DockWindowPeriod> selectDockWindowPeriodList(DockWindowPeriod dockWindowPeriod);
    Integer insertDockWindowPeriod(DockWindowPeriod dockWindowPeriod);
    Integer updateDockWindowPeriod(DockWindowPeriod dockWindowPeriod);
    Integer deleteDockWindowPeriod(@Param("id") Long id);
    List<DockWindowPeriod> selectByPlanIds(List<Long> planIds);
}
