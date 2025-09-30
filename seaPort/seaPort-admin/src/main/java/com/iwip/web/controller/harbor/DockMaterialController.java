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
import com.iwip.harbor.domain.DockMaterial;
import com.iwip.harbor.service.IDockMaterialService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 物资信息Controller
 * 
 * @author ruoyi
 * @date 2025-02-25
 */
@RestController
@RequestMapping("/harbor/material")
public class DockMaterialController extends BaseController
{
    @Autowired
    private IDockMaterialService dockMaterialService;

    /**
     * 查询物资信息列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(DockMaterial dockMaterial)
    {
        startPage();
        List<DockMaterial> list = dockMaterialService.selectDockMaterialList(dockMaterial);
        return getDataTable(list);
    }


    @GetMapping("/enableList")
    public TableDataInfo enableList(DockMaterial dockMaterial)
    {
        List<DockMaterial> list = dockMaterialService.enableList(dockMaterial);
        return getDataTable(list);
    }

    /**
     * 导出物资信息列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:export')")
    @Log(title = "物资信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DockMaterial dockMaterial)
    {
        List<DockMaterial> list = dockMaterialService.selectDockMaterialList(dockMaterial);
        ExcelUtil<DockMaterial> util = new ExcelUtil<DockMaterial>(DockMaterial.class);
        util.exportExcel(response, list, "物资信息数据");
    }

    /**
     * 获取物资信息详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dockMaterialService.selectDockMaterialById(id));
    }

    /**
     * 新增物资信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:add')")
    @Log(title = "物资信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockMaterial dockMaterial)
    {
        return toAjax(dockMaterialService.insertDockMaterial(dockMaterial));
    }

    /**
     * 修改物资信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:edit')")
    @Log(title = "物资信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockMaterial dockMaterial)
    {
        return toAjax(dockMaterialService.updateDockMaterial(dockMaterial));
    }

    @Log(title = "物资信息启用", businessType = BusinessType.UPDATE)
    @PutMapping("/controls/{id}/{materialStatus}")
    public AjaxResult controls(@PathVariable("id") Long id,@PathVariable("materialStatus") String materialStatus)
    {
        return toAjax(dockMaterialService.controls(id,materialStatus));
    }


    /**
     * 删除物资信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:material:remove')")
    @Log(title = "物资信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dockMaterialService.deleteDockMaterialByIds(ids));
    }
}
