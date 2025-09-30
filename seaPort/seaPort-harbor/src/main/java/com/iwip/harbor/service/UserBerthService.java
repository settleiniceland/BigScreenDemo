package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.UserBerth;
import com.iwip.harbor.domain.dto.UserBerthDto;

import java.util.List;

public interface UserBerthService {
    List<UserBerth> getUserBerth(DockBerth db);
    List<UserBerth> getUserBerthList(UserBerth ub);
    void batchUpdateUserBerth(UserBerthDto ubd);
}
