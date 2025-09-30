package com.iwip.harbor.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DockUnloadVo {


    // 卸货主键
    private Long duId;

    //暂停原因
    private String pauseReason;

    //暂停间隔
    private String pauseInterval;

    // 作业状态（0进行中 1暂停 1结束）
    private String workType;

    // 是否结束（0否 1是）
    private String isOver;

    // 卸货数量
    private Long unloadNum;
    private String remark;
    private String operateBy;

    // 作业机具
    private String workEquipment;

    // 装卸量（吨）
    private BigDecimal totalUnloadWeight;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public String getIsOver() {
        return isOver;
    }

    public void setIsOver(String isOver) {
        this.isOver = isOver;
    }

    public String getWorkEquipment() {
        return workEquipment;
    }

    public void setWorkEquipment(String workEquipment) {
        this.workEquipment = workEquipment;
    }

    public String getPauseInterval() {
        return pauseInterval;
    }

    public void setPauseInterval(String pauseInterval) {
        this.pauseInterval = pauseInterval;
    }

    public BigDecimal getTotalUnloadWeight() {
        return totalUnloadWeight;
    }

    public void setTotalUnloadWeight(BigDecimal totalUnloadWeight) {
        this.totalUnloadWeight = totalUnloadWeight;
    }

    public String getOperateBy() {
        return operateBy;
    }

    public void setOperateBy(String operateBy) {
        this.operateBy = operateBy;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getUnloadNum() {
        return unloadNum;
    }

    public void setUnloadNum(Long unloadNum) {
        this.unloadNum = unloadNum;
    }

    public Long getDuId() {
        return duId;
    }

    public void setDuId(Long duId) {
        this.duId = duId;
    }

    public String getPauseReason() {
        return pauseReason;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
