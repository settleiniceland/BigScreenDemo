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
import com.iwip.harbor.domain.DockOutputRange;
import com.iwip.harbor.service.IDockOutputRangeService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 百分比时间段对应产量Controller
 * 
 * @author ruoyi
 * @date 2025-05-05
 */
@RestController
@RequestMapping("/harbor/range")
public class DockOutputRangeController extends BaseController
{
    @Autowired
    private IDockOutputRangeService dockOutputRangeService;

    /**
     * 查询百分比时间段对应产量列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:list')")
    @GetMapping("/list")
    public TableDataInfo list(DockOutputRange dockOutputRange)
    {
        startPage();
        List<DockOutputRange> list = dockOutputRangeService.selectDockOutputRangeList(dockOutputRange);
        return getDataTable(list);
    }

    /**
     * 导出百分比时间段对应产量列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:export')")
    @Log(title = "百分比时间段对应产量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DockOutputRange dockOutputRange)
    {
        List<DockOutputRange> list = dockOutputRangeService.selectDockOutputRangeList(dockOutputRange);
        ExcelUtil<DockOutputRange> util = new ExcelUtil<DockOutputRange>(DockOutputRange.class);
        util.exportExcel(response, list, "百分比时间段对应产量数据");
    }

    /**
     * 获取百分比时间段对应产量详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dockOutputRangeService.selectDockOutputRangeById(id));
    }

    /**
     * 新增百分比时间段对应产量
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:add')")
    @Log(title = "百分比时间段对应产量", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockOutputRange dockOutputRange)
    {
        return toAjax(dockOutputRangeService.insertDockOutputRange(dockOutputRange));
    }

    /**
     * 修改百分比时间段对应产量
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:edit')")
    @Log(title = "百分比时间段对应产量", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockOutputRange dockOutputRange)
    {
        return toAjax(dockOutputRangeService.updateDockOutputRange(dockOutputRange));
    }

    /**
     * 删除百分比时间段对应产量
     */
    // @PreAuthorize("@ss.hasPermi('harbor:range:remove')")
    @Log(title = "百分比时间段对应产量", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dockOutputRangeService.deleteDockOutputRangeByIds(ids));
    }
}
