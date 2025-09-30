package com.iwip.web.controller.harbor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iwip.common.annotation.RepeatSubmit;
import com.iwip.common.core.domain.BaseEntity;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.harbor.domain.DockPlanAssistant;
import com.iwip.harbor.domain.DockStartWorkVo;
import com.iwip.harbor.domain.excel.DockPlanExcel;
import com.iwip.harbor.domain.excel.DockPlanImportExcel;
import com.iwip.harbor.domain.excel.DockPlanRateExcel;
import com.iwip.harbor.domain.excel.DockUnloadWeighExcel;
import com.iwip.harbor.domain.vo.ScreenProgressVo;
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
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.service.IDockPlanService;
import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 计划单Controller
 */
@RestController
@RequestMapping("/harbor/plan")
public class DockPlanController extends BaseController
{
    private final IDockPlanService dockPlanService;
    private final WebSocketProcess webSocketProcess;

    @Autowired
    public DockPlanController(IDockPlanService dockPlanService, WebSocketProcess webSocketProcess) {
        this.dockPlanService = dockPlanService;
        this.webSocketProcess = webSocketProcess;
    }

    /**
     * 查询计划单列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('public')")
    public TableDataInfo list(DockPlan dockPlan)
    {

        Map<Object,Object> map = dockPlanService.summaryCalculation(dockPlan);
        startPage();
        List<DockPlan> dockPlanList = dockPlanService.selectDockPlanList(dockPlan);
        return getDataTable(dockPlanList,map);
    }

    @Log(title = "计划单导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file,boolean updateSupport) throws Exception
    {
        ExcelUtil<DockPlanImportExcel> util = new ExcelUtil<>(DockPlanImportExcel.class);
        List<DockPlanImportExcel> userList = util.importExcel(file.getInputStream());
//        List<String> validValues = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8");
        for(DockPlanImportExcel item : userList){
            item.setDeptId(SecurityUtils.getDeptId());
//            if (!validValues.contains(item.getStatus())) {
//                return error("未导入，请检查计划状态不能为空且必须书写规范");
//            }else if(item.getTonnage()==null||"".equals(item.getTonnage())){
//                return error("未导入，吨位不能为空");
//            }else if(item.getRemark01()==null||"".equals(item.getRemark01())){
//                return error("未导入，作业类型不能为空");
//            }
            if(item.getRemark01().trim().equals("装船")){
                item.setRemark01("装船 Memuat kapal");
            }
            if(item.getRemark01().trim().equals("卸船")){
                item.setRemark01("卸船 Membongkar kapal");
            }
        }
        String operName = getUsername();
        String message = dockPlanService.importShipPlan(userList, operName,updateSupport);
        return success(message);
    }

    /**
     * 导出计划单列表
     */
    @Log(title = "计划单", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('public')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,@RequestBody DockPlan dockPlan)
    {
        List<DockPlanExcel> list = dockPlanService.exportDockPlanList(dockPlan);
        ExcelUtil<DockPlanExcel> util = new ExcelUtil<>(DockPlanExcel.class);
        util.exportExcel(response, list, "计划单数据");
    }



    @Log(title = "计划单-导出", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('public')")
    @PostMapping("/effectiveRateExport")
    public void effectiveRateExport(HttpServletResponse response)
    {
        List<DockPlanRateExcel> list = dockPlanService.effectiveRateExport(new BaseEntity());
        ExcelUtil<DockPlanRateExcel> util = new ExcelUtil<>(DockPlanRateExcel.class);
        util.exportExcel(response, list, "计划单数据");
    }

    @Log(title = "计划单导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importEffectiveRate")
    public AjaxResult importEffectiveRate(MultipartFile file) throws Exception
    {
        ExcelUtil<DockPlanRateExcel> util = new ExcelUtil<>(DockPlanRateExcel.class);
        List<DockPlanRateExcel> planRateExcelList = util.importExcel(file.getInputStream());
        Pattern pattern = Pattern.compile("\\b(\\d{1,2}):(\\d{1,2}):(\\d{1,2})\\b");
        for(DockPlanRateExcel item : planRateExcelList){
            Matcher matcher = pattern.matcher(item.getEffectiveTime());
            if(item!=null&&matcher.find()){
                String timePart = matcher.group();
                item.setEffectiveTime(timePart);
            }else {
                return error("导入失败，有效时间格式有误");
            }
        }
        return AjaxResult.success(dockPlanService.importEffectiveRate(planRateExcelList));
    }


    /**
     * 导入模板
     */
    @Log(title = "计划单", businessType = BusinessType.EXPORT)
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        List<DockPlanImportExcel> excelArrayList = new ArrayList<>();
        DockPlanImportExcel planExcel = new DockPlanImportExcel();
        planExcel.setSort(1);
        planExcel.setShipRade("外");
        planExcel.setShipName("船名");
        planExcel.setRemark01("卸船");
        planExcel.setMineNumber("矿号");
        planExcel.setShipLength(BigDecimal.TEN);
//        planExcel.setStatus("1");
        planExcel.setUsageUnit("使用单位");
        planExcel.setMaterialName("货物名称");
        planExcel.setTonnage("100000");
        planExcel.setPlanTonnage("10000");
        planExcel.setArrivalTime(DateUtils.getNowDate());
        planExcel.setHbName("L5");
//        planExcel.setStatus(null);
        planExcel.setCardCount("卡数");
        planExcel.setPlanDockingTime(DateUtils.getNowDate());
        planExcel.setDockingTime(DateUtils.getNowDate());
        planExcel.setOperationTime(DateUtils.getNowDate());
        planExcel.setEndTime(DateUtils.getNowDate());
        planExcel.setOutBerthTime(DateUtils.getNowDate());
        planExcel.setLeaveTime(DateUtils.getNowDate());
        excelArrayList.add(planExcel);
        ExcelUtil<DockPlanImportExcel> util = new ExcelUtil<>(DockPlanImportExcel.class);
        util.exportExcel(response,excelArrayList, "船舶信息");
    }

    /**
     * 获取计划单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dockPlanService.selectDockPlanById(id));
    }

    /**
     * 新增计划单
     */
    @Log(title = "计划单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockPlan dockPlan)
    {
        return toAjax(dockPlanService.insertDockPlan(dockPlan));
    }

    /**
     * 新增-新版
     */
    @PostMapping("/add")
    public AjaxResult newAdd(@RequestBody DockPlan dockPlan){
        dockPlanService.newInsertDockPlan(dockPlan);
        return success();
    }
    /**
     * 根据id查询-新版
     */
    @GetMapping("/getById/{id}")
    public AjaxResult getById(@PathVariable("id") Long id){
        DockPlan dockPlan = dockPlanService.selectById(id);
        return success(dockPlan);
    }
    /**
     * 更新-新版
     */
    @PostMapping("/update")
    public AjaxResult newUpdate(@RequestBody DockPlan dockPlan){
        dockPlanService.newUpdateDockPlan(dockPlan);
        return success();
    }
    /**
     * 上报作业量-新版
     */
    @PreAuthorize("@ss.hasPermi('public')")
    @PostMapping("/submitUnloadWork")
    public AjaxResult submitUnloadWork(@RequestBody DockPlanAssistant dpa){
        dockPlanService.submitUnloadWork(dpa);
        return success();
    }
    /**
     * 归档
     */
    @Log(title = "计划单归档", businessType = BusinessType.INSERT)
    @PutMapping("/archived/{ids}/{archivedMonth}")
    public AjaxResult archived(@PathVariable Long[] ids,@PathVariable("archivedMonth") String archivedMonth)
    {
        return toAjax(dockPlanService.archived(ids,archivedMonth));
    }

    /**
     * 取消归档
     */
    @Log(title = "计划单-取消归档归档", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('harbor:plan:cancelArchived')")
    @PutMapping("/cancelArchived/{ids}")
    public AjaxResult cancelArchived(@PathVariable Long[] ids)
    {
        return toAjax(dockPlanService.cancelArchived(ids));
    }

    /**
     * 归档修改
     */
     @PreAuthorize("@ss.hasPermi('harbor:plan:archivedUpdate')")
    @Log(title = "计划单归档-编辑", businessType = BusinessType.UPDATE)
    @PutMapping("/archived/update")
    public AjaxResult archivedUpdate(@RequestBody DockPlan dockPlan)
    {
        return toAjax(dockPlanService.updateDockPlan(dockPlan));
    }

    /**
     * 修改计划单
     */
    @Log(title = "计划单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockPlan dockPlan)
    {
        return toAjax(dockPlanService.updateDockPlan(dockPlan));
    }


    /**
     * 修改计划单
     */
    @Log(title = "计划单", businessType = BusinessType.UPDATE)
    @PutMapping("/appEdit")
    public AjaxResult appEdit(@RequestBody DockPlan dockPlan)
    {
        return toAjax(dockPlanService.appEdit(dockPlan));
    }

    /**
     * 修改计划单
     */
    // @PreAuthorize("@ss.hasPermi('harbor:plan:edit')")
    @Log(title = "App-计划单开始作业", businessType = BusinessType.UPDATE)
    @PutMapping("/startWork")
    public AjaxResult startWork(@RequestBody DockStartWorkVo dockStartWorkVo)
    {
        return toAjax(dockPlanService.startWork(dockStartWorkVo));
    }

    /**
     * 删除计划单
     */
    @Log(title = "计划单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dockPlanService.deleteDockPlanByIds(ids));
    }


    /**
     * 移泊
     */
    @Log(title = "移泊", businessType = BusinessType.UPDATE)
    @PostMapping("/moveBerth")
    @RepeatSubmit
    public AjaxResult moveBerth(@RequestBody DockPlan dockPlan)
    {
        int i = dockPlanService.moveBerth(dockPlan);
//        try {
//            webSocketProcess.updatePushAll();
//        } catch (Exception e) {
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(i);
    }

    /**
     * 靠泊中
     */
    @Log(title = "靠泊中", businessType = BusinessType.UPDATE)
    @PostMapping("/toDocking")
    public AjaxResult toDocking(@RequestBody DockPlan dockPlan)
    {
        int i = dockPlanService.toDocking(dockPlan);
//        try {
//            webSocketProcess.updatePushAll();
//        } catch (Exception e) {
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(i);
    }




    /**
     * 计划靠泊
     */
    @Log(title = "计划靠泊", businessType = BusinessType.UPDATE)
    @PutMapping("/planDocking")
    public AjaxResult planDocking(@RequestBody DockPlan dockPlan)
    {
        int i = dockPlanService.planDocking(dockPlan);
//        try {
//            webSocketProcess.updatePushAll();
//        } catch (Exception e) {
//            System.out.println("websocket推送异常："+e.getMessage());
//        }
        return toAjax(i);
    }


    /**
     * 批量导出已作业量
     */
    @Log(title = "批量导出已作业量", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('public')")
    @PostMapping("/exportUnloadWeight")
    public void exportUnloadWeight(HttpServletResponse response,@RequestBody DockPlan dockPlan)
    {
        List<DockUnloadWeighExcel> list = dockPlanService.exportUnloadWeight(dockPlan);
        ExcelUtil<DockUnloadWeighExcel> util = new ExcelUtil<>(DockUnloadWeighExcel.class);
        util.exportExcel(response, list, "计划单数据");
    }

    /**
     * 批量导入已作业量
     */
    @Log(title = "批量导入已作业量", businessType = BusinessType.UPDATE)
    @PostMapping("/batchUpdateUnloadWeight")
    public AjaxResult batchUpdateUnloadWeight(MultipartFile file)  throws Exception
    {
        ExcelUtil<DockUnloadWeighExcel> util = new ExcelUtil<>(DockUnloadWeighExcel.class);
        List<DockUnloadWeighExcel> planRateExcelList = util.importExcel(file.getInputStream());
        return AjaxResult.success(dockPlanService.batchUpdateUnloadWeight(planRateExcelList));
    }

    @Log(title = "计划单修改大屏状态", businessType = BusinessType.UPDATE)
    @PutMapping("/updateScreenStatus/{id}/{screenStatus}")
    public AjaxResult updateScreenStatus(@PathVariable Long id,@PathVariable String screenStatus)
    {
        return toAjax(dockPlanService.updateScreenStatus(id,screenStatus));
    }


    @Log(title = "App进度汇总-手动维护原因", businessType = BusinessType.UPDATE)
    @PutMapping("/updateReason")
    public AjaxResult updateReason(@RequestBody ScreenProgressVo screenProgressVo)
    {
        return toAjax(dockPlanService.updateReason(screenProgressVo));
    }


}
