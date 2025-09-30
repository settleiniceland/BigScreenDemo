package com.iwip.harbor.service;

import com.iwip.harbor.domain.DockStopClass;

import java.util.List;

public interface DockStopClassService {
    List<DockStopClass> getDockStopClassList(DockStopClass dockStopClass);
    Integer addDockStopClass(DockStopClass dockStopClass);
    Integer updateDockStopClass(DockStopClass dockStopClass);
    Integer deleteDockStopClass(Long id);
}
