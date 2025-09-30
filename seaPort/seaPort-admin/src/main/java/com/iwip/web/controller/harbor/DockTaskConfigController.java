package com.iwip.web.controller.harbor;

import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.domain.DockTaskConfig;
import com.iwip.harbor.service.DockTaskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.iwip.common.core.domain.AjaxResult.success;
@RestController
@RequestMapping("/dockTaskConfig/manager")
public class DockTaskConfigController {
    @Autowired
    private DockTaskConfigService dockTaskConfigService;
    @PostMapping("/list")
    public AjaxResult list(@RequestBody DockTaskConfig dockTaskConfig) {
        return success(dockTaskConfigService.getDockTaskConfigList(dockTaskConfig));
    }
    @PostMapping("/add")
    public AjaxResult add(@RequestBody DockTaskConfig dockTaskConfig) {
        return success(dockTaskConfigService.addDockTaskConfig(dockTaskConfig));
    }
    @PostMapping("/update")
    public AjaxResult update(@RequestBody DockTaskConfig dockTaskConfig) {
        return success(dockTaskConfigService.updateDockTaskConfig(dockTaskConfig));
    }
    @GetMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {
        return success(dockTaskConfigService.deleteDockTaskConfig(id));
    }
}
