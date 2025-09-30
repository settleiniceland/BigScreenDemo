package com.iwip.web.controller.harbor;

import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.core.page.TableDataInfo;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.param.AppPierPlanParam;
import com.iwip.harbor.domain.vo.AppPierPlanVo;
import com.iwip.harbor.service.IAppStatisticsService;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IDockPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



/**
 * APP端 统计相关接口
 *
 * @author taoqz
 * @create 2025-04-11
 */
@RestController
@RequestMapping("/app/statistics")
public class AppStatisticsController extends BaseController {
    @Autowired
    private IDockBerthService dockBerthService;
    @Autowired
    private IAppStatisticsService appStatisticsService;
    @Autowired
    private IDockPlanService dockPlanService;
    /**
     * 泊位状态统计
     * @return
     */
    @GetMapping("/berthStatus")
    @PreAuthorize("@ss.hasPermi('public')")
    public AjaxResult berthStatus() {
//        return AjaxResult.success(appStatisticsService.appBerthStatusStatistics());
        return AjaxResult.success(dockBerthService.appBerthStatusStatistics(new DockBerth()));
    }


    /**
     *  船舶进度统计
     *  大码头、驳船码头
     * @param appPierPlanParam
     * @return
     */
    @PreAuthorize("@ss.hasPermi('public')")
    @GetMapping("/pierPlanList")
    public AjaxResult pierPlanList(AppPierPlanParam appPierPlanParam) {
        // startPage();
        List<AppPierPlanVo> list = appStatisticsService.selectPierPlanList(appPierPlanParam);
        return AjaxResult.success(list);
    }


    /**
     * 计划单状态分布
     * @return
     */
    @PreAuthorize("@ss.hasPermi('public')")
    @GetMapping("/planStatusList")
    public AjaxResult planStatusList() {
        return AjaxResult.success(appStatisticsService.selectPlanStatusList());
    }


    /**
     * 今日/明日到船（物资）统计
     * @return
     */
    @GetMapping("/shipArrival")
    public AjaxResult shipArrivalStatistics() {
        return AjaxResult.success(appStatisticsService.shipArrivalStatistics());
    }


    /**
     * 昨日/当月/当年累计吞吐量
     * @return
     */
    @GetMapping("/throughput")
    @PreAuthorize("@ss.hasPermi('public')")
    public AjaxResult throughputStatistics() {
//        return AjaxResult.success(appStatisticsService.throughputStatistics());
        return AjaxResult.success(dockPlanService.screenThroughput(new DockPlan()));
    }
}
