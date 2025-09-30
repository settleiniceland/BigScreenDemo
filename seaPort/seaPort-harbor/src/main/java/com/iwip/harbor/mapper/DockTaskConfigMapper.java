package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockTaskConfig;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DockTaskConfigMapper {
    List<DockTaskConfig> selectDockTaskConfigList(DockTaskConfig dockTaskConfig);
    Integer insertDockTaskConfig(DockTaskConfig dockTaskConfig);
    Integer updateDockTaskConfig(DockTaskConfig dockTaskConfig);
    Integer deleteDockTaskConfig(@Param("id") Long id);
}
