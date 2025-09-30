package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockSlowDownWorkLogs;
import com.iwip.harbor.mapper.DockSlowDownWorkLogsMapper;
import com.iwip.harbor.service.DockSlowDownWorkLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockSlowDownWorkLogsServiceImpl implements DockSlowDownWorkLogsService {
    @Autowired
    private DockSlowDownWorkLogsMapper dockSlowDownWorkLogsMapper;

    @Override
    public List<DockSlowDownWorkLogs> getDockSlowDownWorkLogs(DockSlowDownWorkLogs dockSlowDownWorkLogs) {
        return dockSlowDownWorkLogsMapper.selectSDWL(dockSlowDownWorkLogs);
    }
    @Override
    public Integer updateDockSlowDownWorkLogs(DockSlowDownWorkLogs sdwl) {
        return dockSlowDownWorkLogsMapper.updateSDWL(sdwl);
    }
    @Override
    public Integer addDockSlowDownWorkLogs(DockSlowDownWorkLogs sdwl) {
        return dockSlowDownWorkLogsMapper.insertSDWL(sdwl);
    }

    @Override
    public Integer delDockSlowDownWorkLogs(Long id) {
        return dockSlowDownWorkLogsMapper.deleteSDWL(id);
    }
}
