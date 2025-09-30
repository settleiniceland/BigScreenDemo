package com.iwip.web.controller.harbor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.harbor.domain.DockHourUnloadLog;
import com.iwip.harbor.domain.vo.DockPlanEfficiencyHourVo;
import com.iwip.harbor.service.IDockHourUnloadLogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 记录每小时卸货的日志Controller
 *
 * @author @IWIP
 * @date 2025-04-17
 */
@RestController
@RequestMapping("/harbor/unloadLog")
public class DockHourUnloadLogController extends BaseController
{
    @Autowired
    private IDockHourUnloadLogService dockHourUnloadLogService;

    /**
     * 查询记录每小时卸货的日志列表
     */
    // @RequiresPermissions("ticket:log:list")
    @GetMapping("/list")
    public AjaxResult list(DockHourUnloadLog dockHourUnloadLog)
    {
        List<DockHourUnloadLog> list = dockHourUnloadLogService.selectDockHourUnloadLogList(dockHourUnloadLog);
        return AjaxResult.success(list);
    }


    /**
     * 获取记录每小时卸货的日志详细信息
     */
    // @RequiresPermissions("ticket:log:query")
    @GetMapping(value = "/{dhuId}")
    public AjaxResult getInfo(@PathVariable("dhuId") Long dhuId)
    {
        return success(dockHourUnloadLogService.selectDockHourUnloadLogByDhuId(dhuId));
    }


    /**
     * 计算卸率
     * @param dockHourUnloadLog
     * @return
     */
    @PostMapping(value = "/calculateUnloadingRate")
    public AjaxResult calculateUnloadingRate(@RequestBody DockHourUnloadLog dockHourUnloadLog)
    {
        Map<String, BigDecimal> params = dockHourUnloadLogService.calculateUnloadingRate(dockHourUnloadLog);
        return success(params);
    }

    /**
     * 新增记录每小时卸货的日志
     */
    // @RequiresPermissions("ticket:log:add")
    // @Log(title = "记录每小时卸货的日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockHourUnloadLog dockHourUnloadLog)
    {
        return toAjax(dockHourUnloadLogService.insertDockHourUnloadLog(dockHourUnloadLog));
    }

    /**
     * 修改记录每小时卸货的日志
     */
    // @RequiresPermissions("ticket:log:edit")
    // @Log(title = "记录每小时卸货的日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockHourUnloadLog dockHourUnloadLog)
    {
        return toAjax(dockHourUnloadLogService.updateDockHourUnloadLog(dockHourUnloadLog));
    }

    /**
     * 删除记录每小时卸货的日志
     */
    // @RequiresPermissions("ticket:log:remove")
    // @Log(title = "记录每小时卸货的日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dhuIds}")
    public AjaxResult remove(@PathVariable Long[] dhuIds)
    {
        return toAjax(dockHourUnloadLogService.deleteDockHourUnloadLogByDhuIds(dhuIds));
    }

    /**
     * 导出记录每小时卸货的日志列表
     */
    // @RequiresPermissions("ticket:log:export")
    // @Log(title = "记录每小时卸货的日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DockHourUnloadLog dockHourUnloadLog)
    {
        List<DockHourUnloadLog> list = dockHourUnloadLogService.selectDockHourUnloadLogList(dockHourUnloadLog);
        ExcelUtil<DockHourUnloadLog> util = new ExcelUtil<DockHourUnloadLog>(DockHourUnloadLog.class);
        util.exportExcel(response, list, "记录每小时卸货的日志数据");
    }

    @GetMapping("/efficiencyByHour")
    public AjaxResult efficiencyByHour(DockHourUnloadLog dockHourUnloadLog) {
        List<DockPlanEfficiencyHourVo> result = dockHourUnloadLogService.efficiencyByHour(dockHourUnloadLog);
        return success(result);
    }


}
