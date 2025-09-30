package com.iwip.web.controller.harbor;

import com.iwip.common.core.controller.BaseController;
import com.iwip.common.core.domain.AjaxResult;
import com.iwip.common.core.domain.entity.SysUser;
import com.iwip.common.core.page.TableDataInfo;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.UserBerth;
import com.iwip.harbor.domain.dto.UserBerthDto;
import com.iwip.harbor.service.UserBerthService;
import com.iwip.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/userBerch/map")
public class UserBerthController extends BaseController {
    @Autowired
    private UserBerthService userBerthService;
    @Autowired
    private SysUserMapper userMapper;
    @PreAuthorize("@ss.hasPermi('public')")
    @GetMapping("/list")
    public AjaxResult list(DockBerth dockBerth) {
        return success(userBerthService.getUserBerth(dockBerth));
    }
    @GetMapping("/userBerchList")
    public AjaxResult userBerchList(UserBerth userBerth) {
        return success(userBerthService.getUserBerthList(userBerth));
    }
    @PostMapping("/batchUpdate")
    public AjaxResult batchUpdate(@RequestBody UserBerthDto ubd) {
        userBerthService.batchUpdateUserBerth(ubd);
        return success();
    }
    @GetMapping("/userList")
    public TableDataInfo list(SysUser user) {
        if (StringUtils.isBlank(user.getSearchValue()) || !StringUtils.equals("ALL",user.getSearchValue())) {
            startPage();
        }
        List<SysUser> list = userMapper.selectUserList(user);
        return getDataTable(list);
    }
}
