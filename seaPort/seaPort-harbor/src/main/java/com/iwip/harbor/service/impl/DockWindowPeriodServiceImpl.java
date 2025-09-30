package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockWindowPeriod;
import com.iwip.harbor.mapper.DockWindowPeriodMapper;
import com.iwip.harbor.service.DockWindowPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class DockWindowPeriodServiceImpl implements DockWindowPeriodService {
    @Autowired
    private DockWindowPeriodMapper windowPeriodMapper;
    @Override
    public List<DockWindowPeriod> getDockWindowPeriodList(DockWindowPeriod dwp) {
        return windowPeriodMapper.selectDockWindowPeriodList(dwp);
    }

    @Override
    public List<Integer> getLackWindowPeriodListMark(DockPlan dp) {
        DockWindowPeriod dwp = new DockWindowPeriod();
        dwp.setPlanId(dp.getId());
        List<DockWindowPeriod> dockWindowPeriods = windowPeriodMapper.selectDockWindowPeriodList(dwp);
        HashSet<Integer> setTool=new HashSet<>();
        dockWindowPeriods.forEach(item->{
            setTool.add(item.getPeriodType());
        });//查到已有的所有空窗类型
        List<Integer> lackType=new ArrayList<>();
//        if(dp.getArrivalTime()!=null&&dp.getDockingTime()!=null&&!setTool.contains(0)){
//            lackType.add(0);
//        }
        if(dp.getDockingTime()!=null&&dp.getOperationTime()!=null&&!setTool.contains(1)){
            lackType.add(1);
        }
        if(dp.getEndTime()!=null&&dp.getOutBerthTime()!=null&&!setTool.contains(2)){
            lackType.add(2);
        }
        return lackType;
    }

    @Override
    public Integer addDockWindowPeriod(DockWindowPeriod dwp) {
        return windowPeriodMapper.insertDockWindowPeriod(dwp);
    }

    @Override
    public Integer delDockWindowPeriod(Long id) {
        return windowPeriodMapper.deleteDockWindowPeriod(id);
    }

    @Override
    public Integer updateDockWindowPeriod(DockWindowPeriod dwp) {
        return windowPeriodMapper.updateDockWindowPeriod(dwp);
    }
}
