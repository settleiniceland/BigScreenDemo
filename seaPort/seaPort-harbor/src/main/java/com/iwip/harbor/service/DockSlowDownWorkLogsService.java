package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockSlowDownWorkLogs;

import java.util.List;

public interface DockSlowDownWorkLogsService {
    List<DockSlowDownWorkLogs> getDockSlowDownWorkLogs(DockSlowDownWorkLogs dockSlowDownWorkLogs);
    Integer updateDockSlowDownWorkLogs(DockSlowDownWorkLogs sdwl);
    Integer addDockSlowDownWorkLogs(DockSlowDownWorkLogs sdwl);
    Integer delDockSlowDownWorkLogs(Long id);
}
