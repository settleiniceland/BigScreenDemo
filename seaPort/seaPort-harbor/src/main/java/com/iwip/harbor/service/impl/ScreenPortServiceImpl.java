package com.iwip.harbor.service.impl;


import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockHourUnloadLog;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.screen.ScreenBerthInfoVo;

import com.iwip.harbor.domain.screen.ScreenBerthVo;
import com.iwip.harbor.domain.screen.ScreenPierVo;
import com.iwip.harbor.domain.screen.ScreenPlanVo;
import com.iwip.harbor.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ScreenPortServiceImpl implements IScreenPortService {

    @Autowired
    private IDockPlanService dockPlanService;

    @Autowired
    private IDockBerthService dockBerthService;

    @Autowired
    private IDockPierService dockPierService;

    @Autowired
    private IDockHourUnloadLogService dockHourUnloadLogService;


    @Override
    public ScreenBerthInfoVo screenBerthDetalByBerthName(Long berthId) {

        ScreenBerthInfoVo berthInfoVo = new ScreenBerthInfoVo();


        DockBerth dockBerth = new DockBerth();
        dockBerth.setBerthId(berthId);
        List<ScreenPierVo> pierList = dockPierService.selectScreenPierLeftBerthList(dockBerth);
        DockPlan dockPlan = new DockPlan();
        dockPlan.setHbId(berthId);
        List<ScreenPlanVo> planVoList = dockPlanService.selectPlanScreenWorkList(dockPlan);

        // ** 1：按泊位名称分组，提高查找效率**
        Map<String, List<ScreenPlanVo>> berthPlanMap = planVoList.stream()
                .collect(Collectors.groupingBy(ScreenPlanVo::getBerthName));


        for (ScreenPierVo screenPierVo : pierList) {


            List<ScreenBerthVo> childrenList = screenPierVo.getBerthChildrenList();
            for (ScreenBerthVo screenBerthVo : childrenList) {

                BeanUtils.copyProperties(screenBerthVo,berthInfoVo);
                berthInfoVo.setBerthGeoJson(null);

                // ** 2：直接从 `Map` 取出泊位对应的计划，提高查找效率**
                List<ScreenPlanVo> planList = berthPlanMap.getOrDefault(screenBerthVo.getBerthName(), Collections.emptyList());
                for (ScreenPlanVo screenPlanVo : planList) {
                    // 如果是在装卸，把时间填进去
                    if (StringUtils.equals("4",screenPlanVo.getStatus())){
                        berthInfoVo.setArrivalTime(screenPlanVo.getArrivalTime());
                        berthInfoVo.setPlanDockingTime(screenPlanVo.getPlanDockingTime());
                        berthInfoVo.setDockingTime(screenPlanVo.getDockingTime());
                    }

                    DockHourUnloadLog dockHourUnloadLog = new DockHourUnloadLog();
                    dockHourUnloadLog.setPlanId(screenPlanVo.getPlanId());
                    screenPlanVo.setEfficiencyByHour(dockHourUnloadLogService.efficiencyByHour(dockHourUnloadLog));
                }
                List<DockPlan> dockPlanList = dockPlanService.selectScreenDockByBerthId(berthId);
                // ** 4：减少不必要的对象创建**
                berthInfoVo.setPlanInfoList(planList.isEmpty() ? Collections.emptyList() : planList);
                berthInfoVo.setDockPlanList(dockPlanList.isEmpty() ? Collections.emptyList() : dockPlanList);
            }
        }
        return berthInfoVo;
    }

}
