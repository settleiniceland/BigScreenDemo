package com.iwip.harbor.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.iwip.harbor.domain.DockLoadRate;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.DockUnloadWork;

/**
 * 装卸率Service接口
 *
 * @author ruoyi
 * @date 2025-02-04
 */
public interface IDockLoadRateService
{
    /**
     * 查询装卸率
     *
     * @param id 装卸率主键
     * @return 装卸率
     */
    public DockLoadRate selectDockLoadRateById(Long id);
    public List<DockLoadRate> selectDockLoadRateListByIdAndName(Long duId,String berthName);





    /**
     * 查询装卸率列表
     *
     * @param dockLoadRate 装卸率
     * @return 装卸率集合
     */
    public List<DockLoadRate> selectDockLoadRateList(DockLoadRate dockLoadRate);

    /**
     * 新增装卸率
     *
     * @param dockLoadRate 装卸率
     * @return 结果
     */
    public int insertDockLoadRate(DockLoadRate dockLoadRate);

    /**
     * 修改装卸率
     *
     * @param dockLoadRate 装卸率
     * @return 结果
     */
    public int updateDockLoadRate(DockLoadRate dockLoadRate);

    /**
     * 批量删除装卸率
     *
     * @param ids 需要删除的装卸率主键集合
     * @return 结果
     */
    public int deleteDockLoadRateByIds(Long[] ids);

    /**
     * 删除装卸率信息
     *
     * @param id 装卸率主键
     * @return 结果
     */
    public int deleteDockLoadRateById(Long id);
}
