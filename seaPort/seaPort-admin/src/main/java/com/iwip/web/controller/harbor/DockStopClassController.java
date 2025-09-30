package com.iwip.web.controller.harbor;

import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.domain.DockStopClass;
import com.iwip.harbor.service.DockStopClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.iwip.common.core.domain.AjaxResult.success;

@RestController
@RequestMapping("/dockStopClass/manager")
public class DockStopClassController {
    @Autowired
    private DockStopClassService dockStopClassService;
    @PostMapping("/list")
    public AjaxResult list(@RequestBody DockStopClass dockStopClass) {
        return success(dockStopClassService.getDockStopClassList(dockStopClass));
    }
    @PostMapping("/add")
    public AjaxResult add(@RequestBody DockStopClass dockStopClass) {
        dockStopClassService.addDockStopClass(dockStopClass);
        return success();
    }
    @PostMapping("/update")
    public AjaxResult update(@RequestBody DockStopClass dockStopClass) {
        dockStopClassService.updateDockStopClass(dockStopClass);
        return success();
    }
    @GetMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {
        dockStopClassService.deleteDockStopClass(id);
        return success();
    }
}
