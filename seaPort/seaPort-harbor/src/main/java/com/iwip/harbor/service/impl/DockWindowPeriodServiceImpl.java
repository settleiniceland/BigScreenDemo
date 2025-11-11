package com.iwip.harbor.service.impl;

import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockWindowPeriod;
import com.iwip.harbor.mapper.DockPlanMapper;
import com.iwip.harbor.mapper.DockWindowPeriodMapper;
import com.iwip.harbor.service.DockWindowPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class DockWindowPeriodServiceImpl implements DockWindowPeriodService {
    @Autowired
    private DockWindowPeriodMapper windowPeriodMapper;
    @Autowired
    private DockPlanMapper planMapper;
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
    @Transactional
    public Integer addDockWindowPeriod(DockWindowPeriod dwp) {
        if(dwp.getPeriodType()==2){//如果是加最后那个日志，判断是否需要添加他的离港
            DockPlan dockPlan = planMapper.selectDockPlanById(dwp.getPlanId());
            if(dockPlan==null){
                throw new RuntimeException("空窗期竟然没有所属计划？");
            }
            if(dockPlan.getOutBerthTime()!=null&&dockPlan.getLeaveTime()==null){
                dockPlan.setIsArchived("1");
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                dockPlan.setArchivedMonth(currentDate.format(formatter));
                dockPlan.setLeaveTime(dockPlan.getOutBerthTime().plusMinutes(1));
                dockPlan.setStatus("7");
                planMapper.updateDockPlan(dockPlan);
            }
        }
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
