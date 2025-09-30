package com.iwip.web.controller.harbor;

import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.harbor.domain.excel.DockBerthUsaDetailExcel;
import com.iwip.harbor.domain.param.DockBerthUsageParam;
import com.iwip.harbor.domain.vo.DockBerthUsageRateVo;
import com.iwip.harbor.service.IDockStatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 统计接口
 *
 * @author taoqz
 * @create 2025-04-17
 */
@RestController
@RequestMapping("/harbor/statistics")
public class DockStatisticsController {

    @Autowired
    private IDockStatisticsService dockStatisticsService;


    /**
     * 各泊位各月使用率
     *
     * @param dockBerthUsageParam
     * @return
     */
    @GetMapping("/berthUsageRate")
    public AjaxResult berthUsageRate(DockBerthUsageParam dockBerthUsageParam) {
        if (Objects.isNull(dockBerthUsageParam.getYearMonth())) {
            throw new ServiceException("参数不能为空！");
        }
        List<DockBerthUsageRateVo> list =  dockStatisticsService.berthUsageRate(dockBerthUsageParam);
        return AjaxResult.success(list);
    }


    /**
     * 各泊位各月使用率-导出
     * @param response
     * @param dockBerthUsageParam
     */
    @PostMapping("/exportBerthUsageRate")
    public void exportBerthUsageRate(HttpServletResponse response, @RequestBody DockBerthUsageParam dockBerthUsageParam) {
        if (Objects.isNull(dockBerthUsageParam.getYearMonth())) {
            throw new ServiceException("参数不能为空！");
        }
        List<DockBerthUsageRateVo> list =  dockStatisticsService.berthUsageRate(dockBerthUsageParam);
        ExcelUtil<DockBerthUsageRateVo> util = new ExcelUtil<DockBerthUsageRateVo>(DockBerthUsageRateVo.class);
        util.exportExcel(response, list, "泊位使用率");
    }


    /**
     * 各泊位各月使用率-导出
     * @param response
     * @param dockBerthUsageParam
     */
    @PostMapping("/exportBerthUsageDetail")
    public void exportBerthUsageDetail(HttpServletResponse response, @RequestBody DockBerthUsageParam dockBerthUsageParam) {
        if (Objects.isNull(dockBerthUsageParam.getYearMonth())) {
            throw new ServiceException("参数不能为空！");
        }
        List<DockBerthUsaDetailExcel> list =  dockStatisticsService.exportBerthUsageDetail(dockBerthUsageParam);
        ExcelUtil<DockBerthUsaDetailExcel> util = new ExcelUtil<DockBerthUsaDetailExcel>(DockBerthUsaDetailExcel.class);
        util.exportExcel(response, list, "泊位使用率");
    }

}
