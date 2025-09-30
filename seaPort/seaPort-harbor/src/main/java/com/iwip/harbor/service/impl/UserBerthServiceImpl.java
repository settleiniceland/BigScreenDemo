package com.iwip.harbor.service.impl;

import com.iwip.common.annotation.DataScope;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.UserBerth;
import com.iwip.harbor.domain.dto.UserBerthDto;
import com.iwip.harbor.mapper.UserBerthMapper;
import com.iwip.harbor.service.UserBerthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBerthServiceImpl implements UserBerthService {
    @Autowired
    private UserBerthMapper ubm;

    @Override
    @DataScope(deptAlias = "d")
    public List<UserBerth> getUserBerth(DockBerth db) {
        return ubm.getBerthAndUser(db);
    }
    @Override
    public List<UserBerth> getUserBerthList(UserBerth ub) {
        return ubm.selectUserBerth(ub);
    }

    @Override
    @Transactional
    public void batchUpdateUserBerth(UserBerthDto ubd) {
        ubm.delByBerthCode(ubd.getBerthCode());
        if(ubd.getUserBerth().size()>0){
            ubm.insertBatch(ubd.getUserBerth());
        }
    }
}
