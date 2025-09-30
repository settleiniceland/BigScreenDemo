package com.iwip.harbor.mapper;

import java.util.List;
import com.iwip.harbor.domain.ScheduledTasks;
import org.apache.ibatis.annotations.Param;

/**
 * 任务管理（用于重启之后自动恢复读取PLC）Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-17
 */
public interface ScheduledTasksMapper 
{
    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 任务管理（用于重启之后自动恢复读取PLC）
     */
    public ScheduledTasks selectScheduledTasksById(Long id);

    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）
     *
     * @param
     * @return 任务管理（用于重启之后自动恢复读取PLC）
     */
    public List<ScheduledTasks> getRunningTasks();

    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）列表
     * 
     * @param scheduledTasks 任务管理（用于重启之后自动恢复读取PLC）
     * @return 任务管理（用于重启之后自动恢复读取PLC）集合
     */
    public List<ScheduledTasks> selectScheduledTasksList(ScheduledTasks scheduledTasks);

    /**
     * 新增任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param scheduledTasks 任务管理（用于重启之后自动恢复读取PLC）
     * @return 结果
     */
    public int insertScheduledTasks(ScheduledTasks scheduledTasks);



    /**
     * 删除任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 结果
     */
    public int deleteScheduledTasksById(Long id);

    /**
     * 批量删除任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteScheduledTasksByIds(Long[] ids);

    ScheduledTasks selectScheduledTasksByDuId(Long planId);
    ScheduledTasks selectScheduledTasksByDuIdAndName(@Param("planId") Long planId,@Param("berthName") String berthName);

    int updateScheduledTasks(@Param("planId") Long planId,@Param("status") String status);
}
