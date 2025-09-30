package com.iwip.harbor.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 记录每小时卸货的日志对象 dock_hour_unload_log
 *
 * @author @IWIP
 * @date 2025-04-17
 */
public class DockHourUnloadLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long dhuId;

    /** 计划单id */
    @Excel(name = "计划单id")
    private Long planId;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /** 卸货重量 吨数 */
    @Excel(name = "卸货重量")
    private BigDecimal unloadWeight;

    /** 卸货重量 件数*/
    @Excel(name = "卸货件数")
    private BigDecimal unloadNum;

    /** 卸货重量 件数*/
    @Excel(name = "异常状态（0正常 1异常）")
    private String exceptionStatus;

    /** 卸货重量 件数*/
    @Excel(name = "异常原因")
    private String exceptionReason;

    /** 删除状态(0未删除 1已删除) */
    private String delFlag;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark01;

    /** 备注2 */
    @Excel(name = "备注2")
    private String remark02;

    /** 备注3 */
    @Excel(name = "备注3")
    private String remark03;

    public String getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(String exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public Long getDhuId() {
        return dhuId;
    }

    public void setDhuId(Long dhuId) {
        this.dhuId = dhuId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
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

    public BigDecimal getUnloadWeight() {
        return unloadWeight;
    }

    public void setUnloadWeight(BigDecimal unloadWeight) {
        this.unloadWeight = unloadWeight;
    }

    public BigDecimal getUnloadNum() {
        return unloadNum;
    }

    public void setUnloadNum(BigDecimal unloadNum) {
        this.unloadNum = unloadNum;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getRemark01() {
        return remark01;
    }

    public void setRemark01(String remark01) {
        this.remark01 = remark01;
    }

    public String getRemark02() {
        return remark02;
    }

    public void setRemark02(String remark02) {
        this.remark02 = remark02;
    }

    public String getRemark03() {
        return remark03;
    }

    public void setRemark03(String remark03) {
        this.remark03 = remark03;
    }
}
