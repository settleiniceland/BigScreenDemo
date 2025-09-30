package com.iwip.harbor.service;

/**
 * @author taoqz
 * @create 2025-04-14
 */
public interface IPLCScheduledTaskService {

    public void startTask(Long planId,String berthName);

    public void stopTask(Long planId,String berthName);

}
