package com.iwip.harbor.domain.excel;

import com.iwip.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

public class DockBerthUsaDetailExcel {

    @Excel(name = "船名")
    private String shipName;


    @Excel(name = "泊位名称")
    private String hbName;

    @Excel(name = "作业时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @Excel(name = "结束时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "靠泊时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dockingTime;

    @Excel(name = "离泊时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outBerthTime;

    @Excel(name = "耗时")
    private String pauseInterval;


    public String getHbName() {
        return hbName;
    }

    public void setHbName(String hbName) {
        this.hbName = hbName;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getDockingTime() {
        return dockingTime;
    }

    public void setDockingTime(Date dockingTime) {
        this.dockingTime = dockingTime;
    }

    public Date getOutBerthTime() {
        return outBerthTime;
    }

    public void setOutBerthTime(Date outBerthTime) {
        this.outBerthTime = outBerthTime;
    }

    public String getPauseInterval() {
        return pauseInterval;
    }

    public void setPauseInterval(String pauseInterval) {
        this.pauseInterval = pauseInterval;
    }
}
