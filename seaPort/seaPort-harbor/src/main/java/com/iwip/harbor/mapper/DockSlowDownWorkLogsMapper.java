package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockSlowDownWorkLogs;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DockSlowDownWorkLogsMapper {
    List<DockSlowDownWorkLogs> selectSDWL(DockSlowDownWorkLogs dockSlowDownWorkLogs);
    Integer insertSDWL(DockSlowDownWorkLogs dockSlowDownWorkLogs);
    Integer updateSDWL(DockSlowDownWorkLogs dockSlowDownWorkLogs);
    Integer deleteSDWL(@Param("id")Long id);
    List<DockSlowDownWorkLogs> selectByPlanIds(List<Long> planIds);
}
