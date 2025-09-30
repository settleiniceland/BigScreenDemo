package com.iwip.web.controller.newScreen;

import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.entity.SysDept;
import com.iwip.common.core.page.TableDataInfo;
import com.iwip.harbor.domain.*;
import com.iwip.harbor.service.NewScreenService;
import com.iwip.system.mapper.SysDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/newScreen")
public class NewScreenController extends BaseController {
    @Autowired
    private NewScreenService newScreenService;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @GetMapping("/allDept")
    public TableDataInfo allDept() {
        List<SysDept> allDeptList = newScreenService.getAllDeptList();
        return getDataTable(allDeptList);
    }
    @GetMapping("/getBerchs/{deptId}")
    public TableDataInfo getPorts(@PathVariable Long deptId) {
        List<DockBerth> berths = newScreenService.getBerthListByDeptIds(getDeptIds(deptId));
        return getDataTable(berths);
    }
    @GetMapping("/getStatisticsData/{deptId}")
    public Map<String, BigDecimal> getStatisticsData(@PathVariable Long deptId) {
        Map<String, BigDecimal> sd = newScreenService.getStatisticsData(getDeptIds(deptId));
        return sd;
    }
    @GetMapping("/getArriveLeavingPlan/{deptId}")
    public TableDataInfo getArriveLeavingPlan(@PathVariable Long deptId) {
        List<DockPlan> plan1 = newScreenService.getSuchArrivePlanByDeptIds(getDeptIds(deptId));
        return getDataTable(plan1);
    }
    @GetMapping("/getPlan2/{deptId}")
    public TableDataInfo getPlan2(@PathVariable Long deptId) {
        List<DockPlan> plan2 = newScreenService.getPlan2(getDeptIds(deptId));
        return getDataTable(plan2);
    }
    @GetMapping("/getWorkingPlan/{deptId}")
    public TableDataInfo getWorkingPlan(@PathVariable Long deptId) {
        List<DockPlan> plan3 = newScreenService.getWorkingPlanByDeptIds(getDeptIds(deptId));
        return getDataTable(plan3);
    }
    @GetMapping("/getPopWindowData/{deptId}/{berchCode}")
    public TableDataInfo getPopWindowData(@PathVariable Long deptId,@PathVariable String berchCode) {
        List<DockPlan> dps = newScreenService.getPlanPopDetailByPlanId(getDeptIds(deptId),berchCode);
        return getDataTable(dps);
    }
    private List<Long> getDeptIds(Long deptId) {
        List<Long> deptIds = sysDeptMapper.newScreen_SelectListByAncestors(deptId);
        deptIds.add(deptId);
        return deptIds;
    }
}