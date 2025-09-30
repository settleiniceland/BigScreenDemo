package com.iwip.web.controller.harbor;

import com.iwip.common.core.domain.AjaxResult;
import com.iwip.harbor.domain.screen.ScreenBerthInfoVo;
import com.iwip.harbor.service.IDockBerthService;
import com.iwip.harbor.service.IScreenPortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/harbor/screenArea")
public class ScreenPortController {


    @Autowired
    private IScreenPortService screenPortService;
    /**
     * 根据泊位查询区域详情
     */
    @GetMapping("/selectDetailByBerthName/{berthId}")
    public AjaxResult selectDetailByBerthName(@PathVariable("berthId") Long berthId){

        ScreenBerthInfoVo screenBerthInfoVo = screenPortService.screenBerthDetalByBerthName(berthId);
        return AjaxResult.success(screenBerthInfoVo);

    }
}
