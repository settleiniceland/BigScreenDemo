package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockWindowPeriod;

import java.util.List;

public interface DockWindowPeriodService {
    List<DockWindowPeriod> getDockWindowPeriodList(DockWindowPeriod dwp);
    List<Integer> getLackWindowPeriodListMark(DockPlan dp);
    Integer addDockWindowPeriod(DockWindowPeriod dwp);
    Integer delDockWindowPeriod(Long id);
    Integer updateDockWindowPeriod(DockWindowPeriod dwp);
}
