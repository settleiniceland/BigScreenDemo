package com.iwip.harbor.mapper;

import com.iwip.harbor.domain.DockStopClass;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DockStopClassMapper {
    List<DockStopClass> selectDockStopClassList(DockStopClass dockStopClass);
    Integer insertDockStopClass(DockStopClass dockStopClass);
    Integer updateDockStopClass(DockStopClass dockStopClass);
    Integer deleteDockStopClass(@Param("id") Long id);
}
