package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockTaskConfig;
import com.iwip.harbor.mapper.DockTaskConfigMapper;
import com.iwip.harbor.service.DockTaskConfigService;
import com.iwip.harbor.task.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockTaskConfigServiceImpl implements DockTaskConfigService {
    @Autowired
    private DockTaskConfigMapper dockTaskConfigMapper;
    @Autowired
    private TaskManager taskManager;
    @Override
    public List<DockTaskConfig> getDockTaskConfigList(DockTaskConfig dockTaskConfig) {
        return dockTaskConfigMapper.selectDockTaskConfigList(dockTaskConfig);
    }

    @Override
    public Integer addDockTaskConfig(DockTaskConfig dockTaskConfig) {
        Integer i = dockTaskConfigMapper.insertDockTaskConfig(dockTaskConfig);
        if(i>0){
            taskManager.reloadTask(dockTaskConfig.getTaskName());
        }
        return i;
    }

    @Override
    public Integer updateDockTaskConfig(DockTaskConfig dockTaskConfig) {
        DockTaskConfig taskParams=new DockTaskConfig();
        taskParams.setId(dockTaskConfig.getId());
        DockTaskConfig delTaskConfig = dockTaskConfigMapper.selectDockTaskConfigList(taskParams).get(0);
        Integer i = dockTaskConfigMapper.updateDockTaskConfig(dockTaskConfig);
        if(i>0){
            taskManager.deleteTask(delTaskConfig.getTaskName());
            taskManager.reloadTask(dockTaskConfig.getTaskName());
        }
        return i;
    }

    @Override
    public Integer deleteDockTaskConfig(Long id) {
        DockTaskConfig taskParams=new DockTaskConfig();
        taskParams.setId(id);
        DockTaskConfig dockTaskConfig = dockTaskConfigMapper.selectDockTaskConfigList(taskParams).get(0);
        Integer i = dockTaskConfigMapper.deleteDockTaskConfig(id);
        if(i>0){
            taskManager.deleteTask(dockTaskConfig.getTaskName());
        }
        return i;
    }
}
