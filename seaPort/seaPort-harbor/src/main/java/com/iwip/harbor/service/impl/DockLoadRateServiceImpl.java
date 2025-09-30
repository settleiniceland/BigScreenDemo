package com.iwip.harbor.service.impl;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.DockLoadRateMapper;
import com.iwip.harbor.domain.DockLoadRate;
import com.iwip.harbor.service.IDockLoadRateService;

/**
 * 装卸率Service业务层处理
 *
 * @author ruoyi
 * @date 2025-02-04
 */
@Slf4j
@Service
public class DockLoadRateServiceImpl implements IDockLoadRateService
{
    @Autowired
    private DockLoadRateMapper dockLoadRateMapper;

    /**
     * 查询装卸率
     *
     * @param id 装卸率主键
     * @return 装卸率
     */
    @Override
    public DockLoadRate selectDockLoadRateById(Long id)
    {
        return dockLoadRateMapper.selectDockLoadRateById(id);
    }

    /**
     *
     * @param duId
     * @param berthName
     * @return
     */
    @Override
    public List<DockLoadRate> selectDockLoadRateListByIdAndName(Long duId,String berthName) {
        return dockLoadRateMapper.selectDockLoadRateListByIdAndName(duId,berthName);
    }


    /**
     * 查询装卸率列表
     *
     * @param dockLoadRate 装卸率
     * @return 装卸率
     */
    @Override
    public List<DockLoadRate> selectDockLoadRateList(DockLoadRate dockLoadRate)
    {
        return dockLoadRateMapper.selectDockLoadRateList(dockLoadRate);
    }

    /**
     * 新增装卸率
     *
     * @param dockLoadRate 装卸率
     * @return 结果
     */
    @Override
    public int insertDockLoadRate(DockLoadRate dockLoadRate)
    {
        return dockLoadRateMapper.insertDockLoadRate(dockLoadRate);
    }

    /**
     * 修改装卸率
     *
     * @param dockLoadRate 装卸率
     * @return 结果
     */
    @Override
    public int updateDockLoadRate(DockLoadRate dockLoadRate)
    {
        return dockLoadRateMapper.updateDockLoadRate(dockLoadRate);
    }

    /**
     * 批量删除装卸率
     *
     * @param ids 需要删除的装卸率主键
     * @return 结果
     */
    @Override
    public int deleteDockLoadRateByIds(Long[] ids)
    {
        return dockLoadRateMapper.deleteDockLoadRateByIds(ids);
    }

    /**
     * 删除装卸率信息
     *
     * @param id 装卸率主键
     * @return 结果
     */
    @Override
    public int deleteDockLoadRateById(Long id)
    {
        return dockLoadRateMapper.deleteDockLoadRateById(id);
    }
}
