package com.iwip.web.controller.system;

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
import com.iwip.system.domain.SystemIp;
import com.iwip.system.service.ISystemIpService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 用户Ip地址Controller
 * 
 * @author ruoyi
 * @date 2025-04-30
 */
@RestController
@RequestMapping("/system/address")
public class SystemIpController extends BaseController
{
    @Autowired
    private ISystemIpService systemIpService;

    /**
     * 查询用户Ip地址列表
     */
    // @PreAuthorize("@ss.hasPermi('system:address:list')")
    @GetMapping("/list")
    public TableDataInfo list(SystemIp systemIp)
    {
        startPage();
        List<SystemIp> list = systemIpService.selectSystemIpList(systemIp);
        return getDataTable(list);
    }

    /**
     * 导出用户Ip地址列表
     */
    // @PreAuthorize("@ss.hasPermi('system:address:export')")
    @Log(title = "用户Ip地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SystemIp systemIp)
    {
        List<SystemIp> list = systemIpService.selectSystemIpList(systemIp);
        ExcelUtil<SystemIp> util = new ExcelUtil<SystemIp>(SystemIp.class);
        util.exportExcel(response, list, "用户Ip地址数据");
    }

    /**
     * 获取用户Ip地址详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:address:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(systemIpService.selectSystemIpById(id));
    }

    /**
     * 新增用户Ip地址
     */
    // @PreAuthorize("@ss.hasPermi('system:address:add')")
    @Log(title = "用户Ip地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SystemIp systemIp)
    {
        return toAjax(systemIpService.insertSystemIp(systemIp));
    }

    /**
     * 修改用户Ip地址
     */
    // @PreAuthorize("@ss.hasPermi('system:address:edit')")
    @Log(title = "用户Ip地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SystemIp systemIp)
    {
        return toAjax(systemIpService.updateSystemIp(systemIp));
    }

    /**
     * 删除用户Ip地址
     */
    // @PreAuthorize("@ss.hasPermi('system:address:remove')")
    @Log(title = "用户Ip地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(systemIpService.deleteSystemIpByIds(ids));
    }



    /**
     * 删除用户Ip地址
     */
    // @PreAuthorize("@ss.hasPermi('system:address:remove')")
    @Log(title = "用户Ip地址", businessType = BusinessType.DELETE)
    @GetMapping("testIp")
    public AjaxResult test()
    {
        return toAjax(systemIpService.test());
    }
}
