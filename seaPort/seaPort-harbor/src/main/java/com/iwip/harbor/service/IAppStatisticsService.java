package com.iwip.harbor.service;

import com.iwip.harbor.domain.param.AppPierPlanParam;
import com.iwip.harbor.domain.screen.ScreenPlanStatusVo;
import com.iwip.harbor.domain.screen.ScreenShipArrivalVo;
import com.iwip.harbor.domain.screen.ScreenWorkPlan;
import com.iwip.harbor.domain.vo.AppDockBerthStatusStatisticsVo;
import com.iwip.harbor.domain.vo.AppPierPlanVo;
import com.iwip.harbor.domain.vo.AppPlanStatusVo;

import java.util.List;
import java.util.Map;

/**
 * @author taoqz
 * @create 2025-04-14
 */
public interface IAppStatisticsService {

    List<AppDockBerthStatusStatisticsVo> appBerthStatusStatistics();

    List<AppPierPlanVo> selectPierPlanList(AppPierPlanParam appPierPlanParam);

    AppPlanStatusVo selectPlanStatusList();

    List<ScreenShipArrivalVo> shipArrivalStatistics();

    Map<String, String> throughputStatistics();

}
