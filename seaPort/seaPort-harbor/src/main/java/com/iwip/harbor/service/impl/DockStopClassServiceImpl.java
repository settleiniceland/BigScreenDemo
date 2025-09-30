package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockStopClass;
import com.iwip.harbor.mapper.DockStopClassMapper;
import com.iwip.harbor.service.DockStopClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DockStopClassServiceImpl implements DockStopClassService {
    @Autowired
    private DockStopClassMapper dockStopClassMapper;

    @Override
    public List<DockStopClass> getDockStopClassList(DockStopClass dockStopClass) {
        return dockStopClassMapper.selectDockStopClassList(dockStopClass);
    }

    @Override
    public Integer addDockStopClass(DockStopClass dockStopClass) {
        return dockStopClassMapper.insertDockStopClass(dockStopClass);
    }

    @Override
    public Integer updateDockStopClass(DockStopClass dockStopClass) {
        return dockStopClassMapper.updateDockStopClass(dockStopClass);
    }

    @Override
    public Integer deleteDockStopClass(Long id) {
        return dockStopClassMapper.deleteDockStopClass(id);
    }
}
