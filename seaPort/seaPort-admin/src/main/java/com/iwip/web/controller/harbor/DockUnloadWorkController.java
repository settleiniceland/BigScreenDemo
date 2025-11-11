package com.iwip.web.controller.harbor;

import com.iwip.common.annotation.RepeatSubmit;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.harbor.domain.DockUnloadWork;
import com.iwip.harbor.domain.excel.DockUnloadExcel;
import com.iwip.harbor.domain.vo.DockUnloadVo;
import com.iwip.harbor.service.IDockUnloadWorkService;
import com.iwip.web.controller.websocket.WebSocketProcess;
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
import com.iwip.common.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 卸货单Controller
 *
 * @author ruoyi
 * @date 2025-02-05
 */
@RestController
@RequestMapping("/harbor/unloadWork")
public class DockUnloadWorkController extends BaseController {
    @Autowired
    private IDockUnloadWorkService dockUnloadWorkService;

    @Autowired
    private WebSocketProcess webSocketProcess;

    /**
     * 查询卸货单列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:list')")
    @PreAuthorize("@ss.hasPermi('public')")
    @RequestMapping("/list")
    public TableDataInfo list(DockUnloadWork dockUnloadWork) {
        Map<Object,Object> map = dockUnloadWorkService.summaryCalculation(dockUnloadWork);
        startPage();
        List<DockUnloadWork> list = dockUnloadWorkService.selectDockUnloadWorkList(dockUnloadWork);
        return getDataTable(list,map);
    }

    /**
     * 导出卸货单列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:export')")
    @Log(title = "卸货单", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('public')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody DockUnloadWork dockUnloadWork) {
        List<DockUnloadExcel> list = dockUnloadWorkService.exportDockUnloadWorkList(dockUnloadWork);
        ExcelUtil<DockUnloadExcel> util = new ExcelUtil<DockUnloadExcel>(DockUnloadExcel.class);
        util.exportExcel(response, list, "卸货单数据");
    }

    /**
     * 获取卸货单详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:query')")
    @GetMapping(value = "/{duId}")
    public AjaxResult getInfo(@PathVariable("duId") Long duId) {
        return success(dockUnloadWorkService.selectDockUnloadWorkByDuId(duId));
    }

    /**
     * 新增卸货单
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:add')")
    @Log(title = "卸货单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockUnloadWork dockUnloadWork) {
        int i = dockUnloadWorkService.insertDockUnloadWork(dockUnloadWork);
        return toAjax(i);
    }

    /**
     * 修改卸货单
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:edit')")
    @Log(title = "卸货单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockUnloadWork dockUnloadWork) {
        int i = dockUnloadWorkService.updateDockUnloadWork(dockUnloadWork);
//        try {
//            webSocketProcess.updatePushAll();
//        }catch (Exception e){
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(i);
    }

    /**
     * 删除卸货单
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:remove')")
    @Log(title = "卸货单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{duIds}")
    public AjaxResult remove(@PathVariable Long[] duIds) {
        return toAjax(dockUnloadWorkService.deleteDockUnloadWorkByDuIds(duIds));
    }


    /**
     * 暂停
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:stop')")
    @Log(title = "卸货单", businessType = BusinessType.UPDATE)
    @PutMapping("/stop")
    @RepeatSubmit
    public AjaxResult stop(@RequestBody DockUnloadVo dockUnloadVo) {
        dockUnloadVo.setOperateBy(SecurityUtils.getUsername());
        int stop = dockUnloadWorkService.stop(dockUnloadVo);
//        try {
//            webSocketProcess.updatePushAll();
//        }catch (Exception e){
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(stop);
    }


    /**
     * 恢复
     */
    // @PreAuthorize("@ss.hasPermi('harbor:unloadWork:recover')")
    @Log(title = "卸货单", businessType = BusinessType.UPDATE)
    @PutMapping("/recover")
    @RepeatSubmit
    public AjaxResult recover(@RequestBody DockUnloadVo dockUnloadVo) {
        dockUnloadVo.setOperateBy(SecurityUtils.getUsername());
        int recover = dockUnloadWorkService.recover(dockUnloadVo);
//        try {
//            webSocketProcess.updatePushAll();
//        }catch (Exception e){
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(recover);
    }



    @Log(title = "卸货单作业结束", businessType = BusinessType.UPDATE)
    @PutMapping("/jobOver")
    @RepeatSubmit
    public AjaxResult jobOver(@RequestBody DockUnloadVo dockUnloadVo) {
        dockUnloadVo.setOperateBy(SecurityUtils.getUsername());
        int i = dockUnloadWorkService.jobOver(dockUnloadVo);
        return toAjax(i);
    }

    @Log(title = "卸货单作业完成", businessType = BusinessType.UPDATE)
    @PutMapping("/jobComplete/{planId}")
    public AjaxResult jobComplete(@PathVariable("planId") Long planId) {
        int i = dockUnloadWorkService.jobComplete(planId);
//        try {
//            webSocketProcess.updatePushAll();
//        }catch (Exception e){
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(i);
    }


    @Log(title = "手机App-修改卸货单", businessType = BusinessType.UPDATE)
    @PutMapping("/appUpdateUnload")
    @RepeatSubmit
    public AjaxResult appUpdateUnload(@RequestBody DockUnloadVo dockUnloadVo) {
        int i = dockUnloadWorkService.appUpdateUnload(dockUnloadVo);
        return toAjax(i);
    }

    @Log(title = "卸货单作业取消完成", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('harbor:unloadWork:cancelComplete')")
    @PutMapping("/cancelComplete/{planId}")
    public AjaxResult cancelComplete(@PathVariable("planId") Long planId)  {
        return toAjax(dockUnloadWorkService.cancelComplete(planId));
    }
}
