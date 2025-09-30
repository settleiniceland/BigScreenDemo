package com.iwip.harbor.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.iwip.common.exception.ServiceException;
import com.iwip.common.utils.DateUtils;
import com.iwip.common.utils.StringUtils;
import com.iwip.harbor.domain.DockPier;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.service.IDockPierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockOutputRangeMapper;
import com.iwip.harbor.domain.DockOutputRange;
import com.iwip.harbor.service.IDockOutputRangeService;

/**
 * 百分比时间段对应产量Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-05
 */
@Service
public class DockOutputRangeServiceImpl implements IDockOutputRangeService 
{
    @Autowired
    private DockOutputRangeMapper dockOutputRangeMapper;

    @Autowired
    private IDockPierService dockPierService;

    /**
     * 查询百分比时间段对应产量
     * 
     * @param id 百分比时间段对应产量主键
     * @return 百分比时间段对应产量
     */
    @Override
    public DockOutputRange selectDockOutputRangeById(Long id)
    {
        return dockOutputRangeMapper.selectDockOutputRangeById(id);
    }

    /**
     * 查询百分比时间段对应产量列表
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 百分比时间段对应产量
     */
    @Override
    public List<DockOutputRange> selectDockOutputRangeList(DockOutputRange dockOutputRange)
    {
        List<DockOutputRange> outputRangeList = dockOutputRangeMapper.selectDockOutputRangeList(dockOutputRange);
        for (DockOutputRange outputRange : outputRangeList) {
            DockPier dockPier = dockPierService.selectDockPierByDpId(outputRange.getPierId());
            if (dockPier != null){
                outputRange.setPierName(dockPier.getPierName());
            }
        }
        return outputRangeList;
    }

    /**
     * 新增百分比时间段对应产量
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 结果
     */
    @Override
    public int insertDockOutputRange(DockOutputRange dockOutputRange)
    {

        if (dockOutputRange.getPierId() == null){
            throw new ServiceException("操作失败，没有关联码头！");
        }
        DockPier dockPier = dockPierService.selectDockPierByDpId(dockOutputRange.getPierId());
        if (dockPier == null){
            throw new ServiceException("操作失败，没有查询到码头！");
        }

        // 校验区间是否重叠
        List<DockOutputRange> existingRanges = dockOutputRangeMapper.selectDockOutputRangeByPierId(dockOutputRange.getPierId());

        BigDecimal newStart = dockOutputRange.getStartPercent();
        BigDecimal newEnd = dockOutputRange.getEndPercent();

        // 新增判断：结束值不能小于开始值
        if (newEnd.compareTo(newStart) < 0) {
            throw new ServiceException("操作失败：区间结束百分比不能小于开始百分比！");
        }

        for (DockOutputRange existing : existingRanges) {
            BigDecimal existStart = existing.getStartPercent();
            BigDecimal existEnd = existing.getEndPercent();

            // 判断是否重叠： !(newEnd < existStart || newStart > existEnd)
            boolean isOverlap = newEnd.compareTo(existStart) >= 0 && newStart.compareTo(existEnd) <= 0;
            if (isOverlap) {
                throw new ServiceException("操作失败：配置区间与已有区间（" +
                        existStart + "% - " + existEnd + "%）存在重叠！");
            }
        }

        dockOutputRange.setCreateTime(LocalDateTime.now());
        return dockOutputRangeMapper.insertDockOutputRange(dockOutputRange);

    }

    /**
     * 修改百分比时间段对应产量
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 结果
     */
    @Override
    public int updateDockOutputRange(DockOutputRange dockOutputRange)
    {
        if (dockOutputRange.getPierId() == null) {
            throw new ServiceException("操作失败，没有关联码头！");
        }

        DockPier dockPier = dockPierService.selectDockPierByDpId(dockOutputRange.getPierId());
        if (dockPier == null) {
            throw new ServiceException("操作失败，没有查询到码头！");
        }

        // 查询该码头下所有配置（包含自己）
        List<DockOutputRange> allRanges = dockOutputRangeMapper.selectDockOutputRangeByPierId(dockOutputRange.getPierId());

        BigDecimal newStart = dockOutputRange.getStartPercent();
        BigDecimal newEnd = dockOutputRange.getEndPercent();

        // 新增判断：结束值不能小于开始值
        if (newEnd.compareTo(newStart) < 0) {
            throw new ServiceException("操作失败：区间结束百分比不能小于开始百分比！");
        }

        for (DockOutputRange existing : allRanges) {

            // 跳过自己
            if (Objects.equals(existing.getId(), dockOutputRange.getId())) {
                continue;
            }

            BigDecimal existStart = existing.getStartPercent();
            BigDecimal existEnd = existing.getEndPercent();

            // 判断是否重叠： !(newEnd < existStart || newStart > existEnd)
            boolean isOverlap = newEnd.compareTo(existStart) >= 0 && newStart.compareTo(existEnd) <= 0;
            if (isOverlap) {
                throw new ServiceException("操作失败：配置区间与已有区间（" +
                        existStart + "% - " + existEnd + "%）存在重叠！");
            }
        }

        dockOutputRange.setUpdateTime(LocalDateTime.now());
        return dockOutputRangeMapper.updateDockOutputRange(dockOutputRange);
    }

    /**
     * 批量删除百分比时间段对应产量
     * 
     * @param ids 需要删除的百分比时间段对应产量主键
     * @return 结果
     */
    @Override
    public int deleteDockOutputRangeByIds(Long[] ids)
    {
        return dockOutputRangeMapper.deleteDockOutputRangeByIds(ids);
    }

    /**
     * 删除百分比时间段对应产量信息
     * 
     * @param id 百分比时间段对应产量主键
     * @return 结果
     */
    @Override
    public int deleteDockOutputRangeById(Long id)
    {
        return dockOutputRangeMapper.deleteDockOutputRangeById(id);
    }

    @Override
    public List<DockOutputRange> selectRangeLeftBerthByPierId(Long hbId) {
        return dockOutputRangeMapper.selectRangeLeftBerthByPierId(hbId);

    }
}
