package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.harbor.domain.DockUnloadWork;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 泊位列表
 */

public class ScreenPlanBerthVo {

    /** 泊位主键 */
    private String berthId;

    /** 泊位名称 */
    private String berthName;

    /** 泊位状态 */
    private String berthStatus;

    /** 货物名称 */
    private String materialName;

    /** 吨重 */
    private String tonnage;

    /** 计划状态 */
    private String status;

    /** 暂停原因 */
    private String reason;

    /** 卸货状态 */
    private String workType;

    /** 计划状态 */
    private Long planId;

    /** 靠泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dockingTime;

    /** 作业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /** 耗时 */
    private String expendTime;

    /** 卸货单列表 */
    private List<DockUnloadWork> unloadWorkList;

    public String getBerthId() {
        return berthId;
    }

    public void setBerthId(String berthId) {
        this.berthId = berthId;
    }

    public String getBerthName() {
        return berthName;
    }

    public void setBerthName(String berthName) {
        this.berthName = berthName;
    }

    public String getBerthStatus() {
        return berthStatus;
    }

    public void setBerthStatus(String berthStatus) {
        this.berthStatus = berthStatus;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getTonnage() {
        return tonnage;
    }

    public void setTonnage(String tonnage) {
        this.tonnage = tonnage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDateTime getDockingTime() {
        return dockingTime;
    }

    public void setDockingTime(LocalDateTime dockingTime) {
        this.dockingTime = dockingTime;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(String expendTime) {
        this.expendTime = expendTime;
    }

    public List<DockUnloadWork> getUnloadWorkList() {
        return unloadWorkList;
    }

    public void setUnloadWorkList(List<DockUnloadWork> unloadWorkList) {
        this.unloadWorkList = unloadWorkList;
    }
}
