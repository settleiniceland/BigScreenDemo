package com.iwip.web.controller.harbor;

import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockWindowPeriod;
import com.iwip.harbor.service.DockWindowPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/windowperiod/manager")
public class DockWindowPeriodController extends BaseController {
    @Autowired
    private DockWindowPeriodService dockWindowPeriodService;
    @PostMapping("/list")
    public AjaxResult list(@RequestBody DockWindowPeriod dwp) {
        return success(dockWindowPeriodService.getDockWindowPeriodList(dwp));
    }
    @PostMapping("/getLackList")
    public AjaxResult getLackList(@RequestBody DockPlan dp) {
        List<Integer> periodTypeList = dockWindowPeriodService.getLackWindowPeriodListMark(dp);
        return success(periodTypeList);
    }
    @PostMapping("/add")
    public AjaxResult add(@RequestBody DockWindowPeriod dwp) {
        dockWindowPeriodService.addDockWindowPeriod(dwp);
        return success();
    }
    @GetMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {
        dockWindowPeriodService.delDockWindowPeriod(id);
        return success();
    }
    @PostMapping("/update")
    public AjaxResult update(@RequestBody DockWindowPeriod dwp) {
        dockWindowPeriodService.updateDockWindowPeriod(dwp);
        return success();
    }
}
