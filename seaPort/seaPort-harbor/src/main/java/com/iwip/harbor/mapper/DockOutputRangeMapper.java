package com.iwip.harbor.mapper;

import java.util.List;
import com.iwip.harbor.domain.DockOutputRange;

/**
 * 百分比时间段对应产量Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-05
 */
public interface DockOutputRangeMapper 
{
    /**
     * 查询百分比时间段对应产量
     * 
     * @param id 百分比时间段对应产量主键
     * @return 百分比时间段对应产量
     */
    public DockOutputRange selectDockOutputRangeById(Long id);

    /**
     * 查询百分比时间段对应产量列表
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 百分比时间段对应产量集合
     */
    public List<DockOutputRange> selectDockOutputRangeList(DockOutputRange dockOutputRange);

    /**
     * 新增百分比时间段对应产量
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 结果
     */
    public int insertDockOutputRange(DockOutputRange dockOutputRange);

    /**
     * 修改百分比时间段对应产量
     * 
     * @param dockOutputRange 百分比时间段对应产量
     * @return 结果
     */
    public int updateDockOutputRange(DockOutputRange dockOutputRange);

    /**
     * 删除百分比时间段对应产量
     * 
     * @param id 百分比时间段对应产量主键
     * @return 结果
     */
    public int deleteDockOutputRangeById(Long id);

    /**
     * 批量删除百分比时间段对应产量
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockOutputRangeByIds(Long[] ids);

    List<DockOutputRange> selectDockOutputRangeByPierId(Long pierId);

    List<DockOutputRange> selectRangeLeftBerthByPierId(Long hbId);
}
