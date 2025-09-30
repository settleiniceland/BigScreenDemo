package com.iwip.harbor.service;

import java.util.List;
import com.iwip.harbor.domain.ScheduledTasks;

/**
 * 任务管理（用于重启之后自动恢复读取PLC）Service接口
 * 
 * @author ruoyi
 * @date 2025-02-17
 */
public interface IScheduledTasksService 
{
    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 任务管理（用于重启之后自动恢复读取PLC）
     */
    public ScheduledTasks selectScheduledTasksById(Long id);

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
     * 批量删除任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param ids 需要删除的任务管理（用于重启之后自动恢复读取PLC）主键集合
     * @return 结果
     */
    public int deleteScheduledTasksByIds(Long[] ids);

    /**
     * 删除任务管理（用于重启之后自动恢复读取PLC）信息
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 结果
     */
    public int deleteScheduledTasksById(Long id);
    public int testWebsocket();
}
