package com.iwip.harbor.service.impl;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.iwip.harbor.mapper.ScheduledTasksMapper;
import com.iwip.harbor.domain.ScheduledTasks;
import com.iwip.harbor.service.IScheduledTasksService;

/**
 * 任务管理（用于重启之后自动恢复读取PLC）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-17
 */
@Service
public class ScheduledTasksServiceImpl implements IScheduledTasksService 
{
    @Autowired
    private ScheduledTasksMapper scheduledTasksMapper;

    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 任务管理（用于重启之后自动恢复读取PLC）
     */
    @Override
    public ScheduledTasks selectScheduledTasksById(Long id)
    {
        return scheduledTasksMapper.selectScheduledTasksById(id);
    }

    /**
     * 查询任务管理（用于重启之后自动恢复读取PLC）列表
     * 
     * @param scheduledTasks 任务管理（用于重启之后自动恢复读取PLC）
     * @return 任务管理（用于重启之后自动恢复读取PLC）
     */
    @Override
    public List<ScheduledTasks> selectScheduledTasksList(ScheduledTasks scheduledTasks)
    {
        return scheduledTasksMapper.selectScheduledTasksList(scheduledTasks);
    }

    /**
     * 新增任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param scheduledTasks 任务管理（用于重启之后自动恢复读取PLC）
     * @return 结果
     */
    @Override
    public int insertScheduledTasks(ScheduledTasks scheduledTasks)
    {
        return scheduledTasksMapper.insertScheduledTasks(scheduledTasks);
    }

    /**
     * 批量删除任务管理（用于重启之后自动恢复读取PLC）
     * 
     * @param ids 需要删除的任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 结果
     */
    @Override
    public int deleteScheduledTasksByIds(Long[] ids)
    {
        return scheduledTasksMapper.deleteScheduledTasksByIds(ids);
    }

    /**
     * 删除任务管理（用于重启之后自动恢复读取PLC）信息
     * 
     * @param id 任务管理（用于重启之后自动恢复读取PLC）主键
     * @return 结果
     */
    @Override
    public int deleteScheduledTasksById(Long id)
    {
        return scheduledTasksMapper.deleteScheduledTasksById(id);
    }

    @Override
    @Scheduled(fixedRate = 1000)
    public int testWebsocket() {
        Random random = new Random();
        int randomNumber = random.nextInt(100); // Generates a random number between 0 and 99
        return randomNumber;
    }
}
