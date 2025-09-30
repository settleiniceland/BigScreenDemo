package com.iwip.web.controller.harbor;

import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.domain.DockSlowDownWorkLogs;
import com.iwip.harbor.service.DockSlowDownWorkLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.iwip.common.core.domain.AjaxResult.success;

@RestController
@RequestMapping("/slowDownWork/manager")
public class DockSlowDownWorkLogsController {
    @Autowired
    private DockSlowDownWorkLogsService dockSlowDownWorkLogsService;
    @PostMapping("/list")
    public AjaxResult list(@RequestBody DockSlowDownWorkLogs sdwl) {
        return success(dockSlowDownWorkLogsService.getDockSlowDownWorkLogs(sdwl));
    }
    @PostMapping("/add")
    public AjaxResult add(@RequestBody DockSlowDownWorkLogs sdwl) {
        dockSlowDownWorkLogsService.addDockSlowDownWorkLogs(sdwl);
        return success();
    }
    @PostMapping("/update")
    public AjaxResult update(@RequestBody DockSlowDownWorkLogs sdwl) {
        dockSlowDownWorkLogsService.updateDockSlowDownWorkLogs(sdwl);
        return success();
    }

    @GetMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {
        dockSlowDownWorkLogsService.delDockSlowDownWorkLogs(id);
        return success();
    }
}
