package com.iwip.harbor.mapper;

import java.time.LocalDateTime;
import java.util.List;
import com.iwip.harbor.domain.DockLoadRate;
import com.iwip.harbor.domain.DockUnloadWork;
import org.apache.ibatis.annotations.Param;

/**
 * 装卸率Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-04
 */
public interface DockLoadRateMapper 
{
    /**
     * 查询装卸率
     * 
     * @param id 装卸率主键
     * @return 装卸率
     */
    public DockLoadRate selectDockLoadRateById(Long id);

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
     * 删除装卸率
     * 
     * @param id 装卸率主键
     * @return 结果
     */
    public int deleteDockLoadRateById(Long id);

    /**
     * 批量删除装卸率
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDockLoadRateByIds(Long[] ids);

    List<DockLoadRate> selectRateListBetweenTime(@Param("startTime") LocalDateTime startTime,
                                                 @Param("stopTime") LocalDateTime stopTime,
                                                 @Param("notStartTime") LocalDateTime notStartTime,
                                                 @Param("notEndTime") LocalDateTime notEndTime
    );

    List<DockLoadRate> selectDockLoadRateListByIdAndName(@Param("planId")Long duId,@Param("berthName")String berthName);
}
