package com.iwip.harbor.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.SecurityUtils;
import com.iwip.harbor.domain.DockUnloadWork;
import com.iwip.harbor.mapper.DockUnloadWorkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockUnloadDetailMapper;
import com.iwip.harbor.domain.DockUnloadDetail;
import com.iwip.harbor.service.IDockUnloadDetailService;
import org.springframework.transaction.annotation.Transactional;

import static com.iwip.common.utils.DateUtils.isWithinRange;

/**
 * 卸货子Service业务层处理
 *
 * @author Fei
 * @date 2025-01-28
 */
@Service
public class DockUnloadDetailServiceImpl implements IDockUnloadDetailService
{
    @Autowired
    private DockUnloadDetailMapper dockUnloadDetailMapper;
    @Autowired
    private DockUnloadWorkMapper dockUnloadWorkMapper;

    /**
     * 查询卸货子
     *
     * @param dudId 卸货子主键
     * @return 卸货子
     */
    @Override
    public DockUnloadDetail selectDockUnloadDetailByDudId(Long dudId)
    {
        return dockUnloadDetailMapper.selectDockUnloadDetailByDudId(dudId);
    }

    @Override
    public List<DockUnloadDetail> selectDockUnloadDetailByDuId(Long duId) {
        return dockUnloadDetailMapper.selectDockUnloadDetailByDuId(duId);
    }

    /**
     * 查询卸货子列表
     *
     * @param dockUnloadDetail 卸货子
     * @return 卸货子
     */
    @Override
    public List<DockUnloadDetail> selectDockUnloadDetailList(DockUnloadDetail dockUnloadDetail)
    {
        return dockUnloadDetailMapper.selectDockUnloadDetailList(dockUnloadDetail);
    }

    /**
     * 新增卸货子
     *
     * @param dockUnloadDetail 卸货子
     * @return 结果
     */
    @Override
    public int insertDockUnloadDetail(DockUnloadDetail dockUnloadDetail)
    {
        dockUnloadDetail.setCreateBy(SecurityUtils.getNickName());
        dockUnloadDetail.setCreateTime(LocalDateTime.now());
        return dockUnloadDetailMapper.insertDockUnloadDetail(dockUnloadDetail);
    }

    /**
     * 修改卸货子
     *
     * @param dockUnloadDetail 卸货子
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDockUnloadDetail(DockUnloadDetail dockUnloadDetail) {

        if (dockUnloadDetail.getStartTime().isAfter(dockUnloadDetail.getEndTime())) {
            throw new ServiceException("本暂停日志的开始时间不能大于本暂停日志的结束时间！");
        }

        // 查询当前数据
        DockUnloadDetail unloadDetail = dockUnloadDetailMapper.selectDockUnloadDetailByDudId(dockUnloadDetail.getDudId());
        if (unloadDetail == null) {
            throw new ServiceException("操作失败，没有查询到卸货日志");
        }

        // 查询对应的卸货作业
        DockUnloadWork unloadWork = dockUnloadWorkMapper.selectDockUnloadWorkByDuId(unloadDetail.getDuId());
//        if (StringUtils.equals("2", unloadWork.getWorkType())) {
//            throw new ServiceException("班次已结束，不可修改日志");
//        }

        // 查询该作业的所有卸货详情，并按 createTime 排序
        List<DockUnloadDetail> unloadDetailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(unloadWork.getDuId());
        if (!unloadDetailList.isEmpty()){

            for (DockUnloadDetail thisDetail : unloadDetailList) {
                if (thisDetail.getEndTime() != null){

                    if ((!Objects.isNull(unloadWork.getEndTime()) && !Objects.isNull(dockUnloadDetail.getEndTime()))
                            && dockUnloadDetail.getEndTime().isAfter(unloadWork.getEndTime())) {
                        throw new ServiceException("结束时间不能大于卸货单的结束时间！");
                    }

                    boolean validate = isWithinRange(dockUnloadDetail.getEndTime(), thisDetail.getStartTime(), thisDetail.getEndTime());
                    // 不包括修改当前这条数据
                    if (!Objects.equals(dockUnloadDetail.getDudId(), thisDetail.getDudId())){
                        if (validate){
                            throw new ServiceException("暂停时间不能在日志的暂停时间范围之内！");
                        }
                    }

                }
            }

            if (dockUnloadDetail.getStartTime() != null) {

                if (dockUnloadDetail.getStartTime().isBefore(unloadWork.getStartTime())) {
                    throw new ServiceException("开始时间不能小于卸货单的开始时间！");
                }

                boolean flag = false;
                for (DockUnloadDetail thisDetail : unloadDetailList) {
                    if (Objects.equals(thisDetail.getDudId(), dockUnloadDetail.getDudId())) {
                        flag = true;
                    } else {
                        // 开始时间必须大于等于本卸货单之前的卸货单所有的结束时间
                        if (!flag) {
                            if (Objects.nonNull(thisDetail.getEndTime()) &&
                                    dockUnloadDetail.getStartTime().isBefore(thisDetail.getEndTime())) {
                                throw new ServiceException("开始时间不能大于本暂停日志之前所有暂停日志的结束时间！");
                            }
                            // 开始时间必须小于本卸货单之后的所有卸货单的开始时间 并且 开始时间要小于等于本卸货单的结束时间
                        } else {
                            if (Objects.nonNull(thisDetail.getEndTime()) &&
                                    !dockUnloadDetail.getStartTime().isBefore(thisDetail.getEndTime())) {
                                throw new ServiceException("开始时间不能大于本暂停日志之后所有暂停日志的开始时间！");
                            }
                        }
                    }
                }
            }
        }
        unloadDetail.setReason(dockUnloadDetail.getReason());
        unloadDetail.setStartTime(dockUnloadDetail.getStartTime());
        unloadDetail.setEndTime(dockUnloadDetail.getEndTime());
        String pauseInterval = DateUtils.timeDiff(unloadDetail.getStartTime(), dockUnloadDetail.getEndTime());
        unloadDetail.setPauseInterval(pauseInterval);
        unloadDetail.setUpdateBy(SecurityUtils.getNickName());
        unloadDetail.setUpdateTime(LocalDateTime.now());
        unloadDetail.setRemark(dockUnloadDetail.getRemark());
        return dockUnloadDetailMapper.updateDockUnloadDetail(unloadDetail);
    }

    /**
     * 批量删除卸货子
     *
     * @param dudIds 需要删除的卸货子主键
     * @return 结果
     */
    @Override
    public int deleteDockUnloadDetailByDudIds(Long[] dudIds)
    {
        if (dudIds.length == 0){
            throw new ServiceException("参数为空，请选择参数之后进行删除操作！");
        }

        return dockUnloadDetailMapper.deleteDockUnloadDetailByDudIds(dudIds);
    }

    /**
     * 删除卸货子信息
     *
     * @param dudId 卸货子主键
     * @return 结果
     */
    @Override
    public int deleteDockUnloadDetailByDudId(Long dudId)
    {
        return dockUnloadDetailMapper.deleteDockUnloadDetailByDudId(dudId);
    }

    @Override
    public DockUnloadDetail selectDetailDescByDuId(Long duId) {
        return dockUnloadDetailMapper.selectDetailDescByDuId(duId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecord(DockUnloadDetail dockUnloadDetail) {
        // 查询当前数据
        DockUnloadDetail unloadDetail = dockUnloadDetailMapper.selectDockUnloadDetailByDudId(dockUnloadDetail.getDudId());
        if (unloadDetail == null) {
            throw new ServiceException("操作失败，没有查询到卸货日志");
        }
        unloadDetail.setRecordStatus(dockUnloadDetail.getRecordStatus());
        unloadDetail.setUpdateTime(LocalDateTime.now());
        unloadDetail.setUpdateBy(SecurityUtils.getNickName());
        return dockUnloadDetailMapper.updateDockUnloadDetail(unloadDetail);
    }

    @Override
    public List<DockUnloadDetail> export(DockUnloadDetail dockUnloadDetail) {
        List<DockUnloadWork> unloadWorkList = dockUnloadWorkMapper.selectUnloadWorkListByPlanId(dockUnloadDetail.getPlanId());
        List<DockUnloadDetail> excelList = new ArrayList<>();
        for (DockUnloadWork unloadWork : unloadWorkList) {
            List<DockUnloadDetail> unloadDetailList = dockUnloadDetailMapper.selectDockUnloadDetailByDuId(unloadWork.getDuId());
            if (!unloadDetailList.isEmpty()){
                excelList.addAll(unloadDetailList);
            }
        }
        return excelList;
    }
}
