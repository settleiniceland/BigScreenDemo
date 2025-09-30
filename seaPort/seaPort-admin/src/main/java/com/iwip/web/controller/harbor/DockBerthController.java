package com.iwip.web.controller.harbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwip.common.utils.poi.ExcelUtil;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.UserBerth;
import com.iwip.harbor.domain.vo.DockPierAndBerthVo;
import com.iwip.harbor.mapper.UserBerthMapper;
import com.iwip.harbor.service.IDockBerthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iwip.common.annotation.Log;
import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.enums.BusinessType;
import com.iwip.common.core.page.TableDataInfo;

/**
 * 泊位信息Controller
 *
 * @author Fei
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/harbor/berth")
public class DockBerthController extends BaseController
{
    @Autowired
    private IDockBerthService dockBerthService;
    @Autowired
    private UserBerthMapper userBerthMapper;
    /**
     * 查询泊位信息列表
     */
    @PreAuthorize("@ss.hasPermi('public')")
    @GetMapping("/list")
    public TableDataInfo list(DockBerth dockBerth){
        List<DockBerth> list = dockBerthService.selectDockBerthList(dockBerth);
        UserBerth userBerth = new UserBerth();
        userBerth.setUserId(getUserId());
        List<UserBerth> userBerths = userBerthMapper.selectUserBerth(userBerth);
        if(userBerths.size()<=0){
            return getDataTable(list);
        }
        List<DockBerth> resultList=new ArrayList<>();
        Map<String,Integer> userBerthMap=new HashMap<>();
        userBerths.forEach(u->{
            userBerthMap.put(u.getBerthCode(),1);
        });
        list.forEach(l->{
            if(userBerthMap.containsKey(l.getBerthCode())){
                resultList.add(l);
            }
        });
        return getDataTable(resultList);
    }


    /**
     * 导出泊位信息列表
     */
    // @PreAuthorize("@ss.hasPermi('harbor:berth:export')")
    @Log(title = "泊位信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DockBerth dockBerth)
    {
        List<DockBerth> list = dockBerthService.selectDockBerthList(dockBerth);
        ExcelUtil<DockBerth> util = new ExcelUtil<DockBerth>(DockBerth.class);
        util.exportExcel(response, list, "泊位信息数据");
    }

    /**
     * 获取泊位信息详细信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:berth:query')")
    @GetMapping(value = "/{dbId}")
    public AjaxResult getInfo(@PathVariable("dbId") Long dbId)
    {
        return success(dockBerthService.selectDockBerthByDbId(dbId));
    }

    /**
     * 新增泊位信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:berth:add')")
    @Log(title = "泊位信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DockPierAndBerthVo dockPierAndBerthVo)
    {
        return toAjax(dockBerthService.add(dockPierAndBerthVo));
    }

    /**
     * 修改泊位信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:berth:edit')")
    @Log(title = "泊位信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DockPierAndBerthVo dockPierAndBerthVo)
    {
        return toAjax(dockBerthService.update(dockPierAndBerthVo));
    }

    /**
     * 删除泊位信息
     */
    // @PreAuthorize("@ss.hasPermi('harbor:berth:remove')")
    @Log(title = "泊位信息", businessType = BusinessType.DELETE)
	@PutMapping("/remove")
    public AjaxResult remove(@RequestBody DockPierAndBerthVo dockPierAndBerthVo)
    {
        return toAjax(dockBerthService.delete(dockPierAndBerthVo));
    }
}
