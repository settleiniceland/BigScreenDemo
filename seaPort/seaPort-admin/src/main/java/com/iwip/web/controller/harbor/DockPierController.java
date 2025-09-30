package com.iwip.web.controller.harbor;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iwip.common.annotation.Log;
import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.enums.BusinessType;
import com.iwip.harbor.domain.DockPier;
import com.iwip.harbor.service.IDockPierService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 码头信息Controller
 *
 * @author Fei
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/harbor/pier")
public class DockPierController extends BaseController
{
    @Autowired
    private IDockPierService dockPierService;

    /**
     * 查询码头信息列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:pier:list')")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('public')")
    public TableDataInfo list(DockPier dockPier)
    {
        List<DockPier> list = dockPierService.selectDockPierList(dockPier);
        return getDataTable(list);
    }

    /**
     * 下拉列表
     * @param dockPier
     * @return
     */
    @GetMapping("/selectList")
    @PreAuthorize("@ss.hasPermi('public')")
    public AjaxResult selectList(DockPier dockPier)
    {
        List<DockPier> list = dockPierService.selectList(dockPier);
        return success(list);
    }


    /**
     * 导出码头信息列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:pier:export')")
    @Log(title = "码头信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DockPier dockPier)
    {
        List<DockPier> list = dockPierService.selectDockPierList(dockPier);
        ExcelUtil<DockPier> util = new ExcelUtil<DockPier>(DockPier.class);
        util.exportExcel(response, list, "码头信息数据");
    }

    /**
     * 获取码头信息详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:pier:query')")
    @GetMapping(value = "/{dpId}")
    public AjaxResult getInfo(@PathVariable("dpId") Long dpId)
    {
        return success(dockPierService.selectDockPierByDpId(dpId));
    }

    /**
     * 删除码头信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:pier:remove')")
    @Log(title = "码头信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dpIds}")
    public AjaxResult remove(@PathVariable Long[] dpIds)
    {
        return toAjax(dockPierService.deleteDockPierByDpIds(dpIds));
    }
}
