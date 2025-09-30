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
import com.iwip.harbor.domain.DockUnloadDetail;
import com.iwip.harbor.service.IDockUnloadDetailService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 卸货子Controller
 *
 * @author Fei
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/harbor/detail")
public class DockUnloadDetailController extends BaseController
{
    @Autowired
    private IDockUnloadDetailService dockUnloadDetailService;

    /**
     * 查询卸货子列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:list')")
    @GetMapping("/list")
    public AjaxResult list(DockUnloadDetail dockUnloadDetail)
    {
        List<DockUnloadDetail> list = dockUnloadDetailService.selectDockUnloadDetailList(dockUnloadDetail);
        return AjaxResult.success(list);
    }

    /**
     * 导出卸货子列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:export')")
    @Log(title = "卸货子", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,@RequestBody DockUnloadDetail dockUnloadDetail)
    {
        List<DockUnloadDetail> list = dockUnloadDetailService.export(dockUnloadDetail);
        ExcelUtil<DockUnloadDetail> util = new ExcelUtil<DockUnloadDetail>(DockUnloadDetail.class);
        util.exportExcel(response, list, "卸货子数据");
    }

    /**
     * 获取卸货子详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:query')")
    @GetMapping(value = "/{dudId}")
    public AjaxResult getInfo(@PathVariable("dudId") Long dudId)
    {
        return success(dockUnloadDetailService.selectDockUnloadDetailByDudId(dudId));
    }

    /**
     * 新增卸货子
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:add')")
    @Log(title = "卸货子", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockUnloadDetail dockUnloadDetail)
    {
        return toAjax(dockUnloadDetailService.insertDockUnloadDetail(dockUnloadDetail));
    }

    /**
     * 修改卸货子
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:edit')")
    @Log(title = "卸货子", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockUnloadDetail dockUnloadDetail)
    {
        return toAjax(dockUnloadDetailService.updateDockUnloadDetail(dockUnloadDetail));
    }

    @Log(title = "卸货子-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/updateRecord")
    public AjaxResult updateRecord(@RequestBody DockUnloadDetail dockUnloadDetail)
    {
        return toAjax(dockUnloadDetailService.updateRecord(dockUnloadDetail));
    }

    /**
     * 删除卸货子
     */
    // @PreAuthorize("@ss.hasPermi('harbor:detail:remove')")
    @Log(title = "卸货子", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dudIds}")
    public AjaxResult remove(@PathVariable Long[] dudIds)
    {
        return toAjax(dockUnloadDetailService.deleteDockUnloadDetailByDudIds(dudIds));
    }

    @DeleteMapping("/del/{dudId}")
    public AjaxResult del(@PathVariable("dudId") Long dudId){
        return toAjax(dockUnloadDetailService.deleteDockUnloadDetailByDudId(dudId));
    }
}
