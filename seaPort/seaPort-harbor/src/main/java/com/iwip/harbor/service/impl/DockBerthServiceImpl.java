package com.iwip.harbor.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.iwip.common.annotation.DataScope;
import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.enums.BerthStatusEnum;
import com.iwip.harbor.domain.screen.*;
import com.iwip.harbor.domain.vo.AppDockBerthStatusStatisticsVo;
import com.iwip.harbor.domain.vo.DockBerthStatusInfoVo;
import com.iwip.harbor.domain.vo.DockPierAndBerthVo;
import com.iwip.harbor.service.IDockHourUnloadLogService;
import com.iwip.harbor.service.IDockPierService;
import com.iwip.harbor.service.IDockPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockBerthMapper;
import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.service.IDockBerthService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 泊位信息Service业务层处理
 *
 * @author Fei
 * @date 2025-01-28
 */
@Service
public class DockBerthServiceImpl implements IDockBerthService {
    @Autowired
    private DockBerthMapper dockBerthMapper;

    @Autowired
    private IDockPierService dockPierService;

    @Autowired
    private IDockPlanService dockPlanService;

    @Autowired
    private IDockHourUnloadLogService dockHourUnloadLogService;

    /**
     * 查询泊位信息
     *
     * @param dbId 泊位信息主键
     * @return 泊位信息
     */
    @Override
    public DockBerth selectDockBerthByDbId(Long dbId) {
        return dockBerthMapper.selectDockBerthByDbId(dbId);
    }

    /**
     * 查询泊位信息列表
     *
     * @param dockBerth 泊位信息
     * @return 泊位信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DockBerth> selectDockBerthList(DockBerth dockBerth) {
        List<DockBerth> dockBerthList = dockBerthMapper.selectDockBerthList(dockBerth);
        return dockBerthList;
    }

    /**
     * 新增泊位信息
     *
     * @param dockPierAndBerthVo 泊位信息
     * @return 结果
     */
    @Override
    public int insertDockBerth(DockPierAndBerthVo dockPierAndBerthVo) {

        DockBerth isBerthNameFlag = dockBerthMapper.selectDockBerthByName(dockPierAndBerthVo.getBerthName());
        if (isBerthNameFlag != null) {
            throw new ServiceException("泊位名称" + dockPierAndBerthVo.getBerthName() + "已存在，不可新增！");
        }

        DockBerth isBerthCodeFlag = dockBerthMapper.selectDockBerthByCode(dockPierAndBerthVo.getBerthCode());
        if (isBerthCodeFlag != null) {
            throw new ServiceException("泊位编码" + dockPierAndBerthVo.getBerthName() + "已存在，不可新增！");
        }

        if (StringUtils.isBlank(dockPierAndBerthVo.getBerthGeoJson())) {
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }
        if (StringUtils.equals("[]", dockPierAndBerthVo.getBerthGeoJson())) {
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }

        DockBerth dockBerth = new DockBerth();
        dockBerth.setBerthHpId(dockPierAndBerthVo.getPierId());
        dockBerth.setBerthName(dockPierAndBerthVo.getBerthName());
        dockBerth.setBerthStatus(dockPierAndBerthVo.getBerthStatus());
        dockBerth.setBerthCode(dockPierAndBerthVo.getBerthCode());
        dockBerth.setUserId(SecurityUtils.getUserId());
        dockBerth.setCreateTime(LocalDateTime.now());
        dockBerth.setCreateBy(SecurityUtils.getNickName());
        dockBerth.setDelFlag("0");
        dockBerth.setRemark(dockPierAndBerthVo.getRemark());
        dockBerth.setDeptId(SecurityUtils.getDeptId());
        dockBerth.setBerthGeoJson(dockPierAndBerthVo.getBerthGeoJson());
        return dockBerthMapper.insertDockBerth(dockBerth);
    }

    /**
     * 修改泊位信息
     *
     * @param dockPierAndBerthVo 泊位信息
     * @return 结果
     */
    @Override
    public int updateDockBerth(DockPierAndBerthVo dockPierAndBerthVo) {
        DockBerth isBerthNameFlag = dockBerthMapper.selectDockBerthByName(dockPierAndBerthVo.getBerthName());
        if (isBerthNameFlag != null) {
            if (!isBerthNameFlag.getBerthId().equals(dockPierAndBerthVo.getBerthId())) {
                throw new ServiceException("泊位名称" + dockPierAndBerthVo.getBerthName() + "已存在，不可修改！");
            }
        }

        DockBerth isBerthCodeFlag = dockBerthMapper.selectDockBerthByCode(dockPierAndBerthVo.getBerthCode());
        if (isBerthCodeFlag != null) {
            if (!isBerthCodeFlag.getBerthId().equals(dockPierAndBerthVo.getBerthId())) {
                throw new ServiceException("泊位编码" + dockPierAndBerthVo.getBerthName() + "已存在，不可修改！");
            }
        }

        if (StringUtils.isBlank(dockPierAndBerthVo.getBerthGeoJson())) {
            throw new ServiceException("区域没有划分，请划分区域之后再操作新增");
        }


        DockBerth dockBerth = new DockBerth();
        dockBerth.setBerthId(dockPierAndBerthVo.getBerthId());
        dockBerth.setBerthName(dockPierAndBerthVo.getBerthName());
        dockBerth.setBerthStatus(dockPierAndBerthVo.getBerthStatus());
        dockBerth.setBerthCode(dockPierAndBerthVo.getBerthCode());
        dockBerth.setBerthGeoJson(dockPierAndBerthVo.getBerthGeoJson());
        dockBerth.setRemark(dockPierAndBerthVo.getRemark());
        dockBerth.setUpdateBy(SecurityUtils.getUsername());
        dockBerth.setUpdateTime(LocalDateTime.now());
        return dockBerthMapper.updateDockBerth(dockBerth);
    }

    /**
     * 修改状态
     *
     * @param dockBerth
     * @return
     */
    @Override
    public int updateDockBerthStatus(DockBerth dockBerth) {
        return dockBerthMapper.updateDockBerthStatus(dockBerth);
    }

    /**
     * 批量删除泊位信息
     *
     * @param dbIds 需要删除的泊位信息主键
     * @return 结果
     */
    @Override
    public int deleteDockBerthByDbIds(Long[] dbIds) {
        if (dbIds.length == 0) {
            throw new ServiceException("参数为空，请选择参数之后进行删除操作！");
        }
        int row = 0;
        for (Long dbId : dbIds) {
            DockBerth dockBerth = dockBerthMapper.selectDockBerthByDbId(dbId);
            if (StringUtils.equals(dockBerth.getDelFlag(), "1")) {
                throw new ServiceException("删除失败，该条数据已被删除！");
            }
            if (!StringUtils.equals(dockBerth.getBerthStatus(), "1")) {
                throw new ServiceException("删除失败，泊位：【" + dockBerth.getBerthName() + "】正在占用或者维护中！");
            }
            dockBerth.setDelFlag("1");
            row += dockBerthMapper.removeDockBerthByDbId(dockBerth);
        }
        return row;
    }

    /**
     * 删除泊位信息信息
     *
     * @param dbId 泊位信息主键
     * @return 结果
     */
    @Override
    public int deleteDockBerthByDbId(Long dbId) {
        DockBerth dockBerth = dockBerthMapper.selectDockBerthByDbId(dbId);
        if (StringUtils.equals(dockBerth.getDelFlag(), "1")) {
            throw new ServiceException("删除失败，该条数据已被删除！");
        }
        if (StringUtils.equals(dockBerth.getBerthStatus(), "1")) {
            throw new ServiceException("删除失败，泊位：【" + dockBerth.getBerthName() + "】正在占用或者维护中！");
        }
        dockBerth.setDelFlag("1");
        int row = dockBerthMapper.removeDockBerthByDbId(dockBerth);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(DockPierAndBerthVo dockPierAndBerthVo) {
        String type = dockPierAndBerthVo.getType();
        // 新增码头
        if (StringUtils.equals("dock", type)) {
            return dockPierService.insertDockPier(dockPierAndBerthVo);
            // 新增泊位
        } else if (StringUtils.equals("berth", type)) {
            return insertDockBerth(dockPierAndBerthVo);
        } else {
            throw new ServiceException("参数错误，请检查参数！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(DockPierAndBerthVo dockPierAndBerthVo) {
        String type = dockPierAndBerthVo.getType();
        // 修改码头
        if (StringUtils.equals("dock", type)) {
            return dockPierService.updateDockPier(dockPierAndBerthVo);
            // 修改泊位
        } else if (StringUtils.equals("berth", type)) {
            return updateDockBerth(dockPierAndBerthVo);
        } else {
            throw new ServiceException("参数错误，请检查参数！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(DockPierAndBerthVo dockPierAndBerthVo) {
        String type = dockPierAndBerthVo.getType();
        // 删除码头
        if (StringUtils.equals("dock", type)) {
            return dockPierService.deleteDockPierByDpId(dockPierAndBerthVo.getPierId());
            // 删除泊位
        } else if (StringUtils.equals("berth", type)) {
            return deleteDockBerthByDbId(dockPierAndBerthVo.getBerthId());
        } else {
            throw new ServiceException("参数错误，请检查参数！");
        }
    }

    @Override
    public List<ScreenPlanStatusVo> screenBerthStatusList() {
        return dockBerthMapper.screenBerthStatusList();
    }

    @Override
    public List<ScreenPierVo> screenPierBerthPlanList() {
        DockBerth dockBerth = new DockBerth();
        List<ScreenPierVo> pierList = dockPierService.selectScreenPierLeftBerthList(dockBerth);

        DockPlan dockPlan = new DockPlan();
        List<ScreenPlanVo> planVoList = dockPlanService.selectPlanScreenWorkList(dockPlan);

        // ** 1：按泊位名称分组，提高查找效率**
        Map<String, List<ScreenPlanVo>> berthPlanMap = planVoList.stream()
                .collect(Collectors.groupingBy(ScreenPlanVo::getBerthName));

        for (ScreenPierVo screenPierVo : pierList) {
            List<ScreenBerthVo> childrenList = screenPierVo.getBerthChildrenList();
            for (ScreenBerthVo screenBerthVo : childrenList) {

                // ** 2：直接从 `Map` 取出泊位对应的计划，提高查找效率**
                List<ScreenPlanVo> planList = berthPlanMap.getOrDefault(screenBerthVo.getBerthName(), Collections.emptyList());
                /**
                 * 用不上了
                  */
//                for (ScreenPlanVo screenPlanVo : planList) {
//                    DockHourUnloadLog dockHourUnloadLog = new DockHourUnloadLog();
//                    dockHourUnloadLog.setPlanId(screenPlanVo.getPlanId());
//                    screenPlanVo.setEfficiencyByHour(dockHourUnloadLogService.efficiencyByHour(dockHourUnloadLog));
//                }

                // ** 4：减少不必要的对象创建**
                screenBerthVo.setPlanInfoList(planList.isEmpty() ? Collections.emptyList() : planList);
            }
        }
        return pierList;
    }

    @Override
    public List<ScreenWorkPlan> screenPierPlanList(String periType) {
        return dockPlanService.screenPierPlanList(periType);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<AppDockBerthStatusStatisticsVo> appBerthStatusStatistics(DockBerth dockBerth) {
        List<DockBerth> dockBerthList = dockBerthMapper.selectDockBerthList(dockBerth);
        return dockBerthList.stream()
                .collect(Collectors.groupingBy(berth -> {
                    String status = berth.getBerthStatus();
                    // 占用中、暂停
                    // 自定义分组：状态1和3统一归为“占用中”，其他状态保持原样或根据枚举转换
                    if (Objects.equals(BerthStatusEnum.OCCUPIED.getCode(), status) || Objects.equals(BerthStatusEnum.PAUSED.getCode(), status)) {
                        return BerthStatusEnum.OCCUPIED.getLabel() + "_" + BerthStatusEnum.PAUSED.getLabel();
                    } else {
                        return status;
                    }
                }))
                .entrySet().stream()
                .map(entry -> {
                    String berthStatusKey = entry.getKey();
                    List<DockBerth> groupList = entry.getValue();

                    List<DockBerthStatusInfoVo> children = groupList.stream()
                            .map(berth -> new DockBerthStatusInfoVo(
                                    berth.getBerthName(),
                                    berth.getBerthCode(),
                                    berth.getRemark()
                            ))
                            .collect(Collectors.toList());

                    AppDockBerthStatusStatisticsVo vo = new AppDockBerthStatusStatisticsVo();

                    if ((BerthStatusEnum.OCCUPIED.getLabel() + "_" + BerthStatusEnum.PAUSED.getLabel()).equals(berthStatusKey)) {
                        vo.setBerthStatus(BerthStatusEnum.OCCUPIED.getCode());
                        vo.setBerthStatusLabel(BerthStatusEnum.OCCUPIED.getLabel());
                    } else {
                        vo.setBerthStatus(berthStatusKey);
                        BerthStatusEnum statusEnum = BerthStatusEnum.fromCode(berthStatusKey);
                        vo.setBerthStatusLabel(statusEnum != null ? statusEnum.getLabel() : "未知");
                    }

                    vo.setCount(children.size());
                    vo.setDockBerthInfoChildren(children);

                    return vo;
                })
                .collect(Collectors.toList());
    }


}
