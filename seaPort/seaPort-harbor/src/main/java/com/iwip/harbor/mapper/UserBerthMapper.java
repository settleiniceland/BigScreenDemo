package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.UserBerth;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;

public interface UserBerthMapper {
    int insertBatch(@Param("list") List<UserBerth> userBerths);
    int delByBerthCode(String berthCode);
    List<UserBerth> selectUserBerth(UserBerth userBerth);
    List<UserBerth> getBerthAndUser(DockBerth db);
}
