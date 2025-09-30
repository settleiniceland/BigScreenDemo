package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockTaskConfig;

import java.util.List;

public interface DockTaskConfigService {
    List<DockTaskConfig> getDockTaskConfigList(DockTaskConfig dockTaskConfig);
    Integer addDockTaskConfig(DockTaskConfig dockTaskConfig);
    Integer updateDockTaskConfig(DockTaskConfig dockTaskConfig);
    Integer deleteDockTaskConfig(Long id);
}
