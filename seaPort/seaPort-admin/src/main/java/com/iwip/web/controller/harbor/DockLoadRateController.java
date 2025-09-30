package com.iwip.web.controller.harbor;


import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.service.IDockLoadRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harbor/dockLoad")
public class DockLoadRateController extends BaseController {

    @Autowired
    private IDockLoadRateService dockLoadRateService;




}
