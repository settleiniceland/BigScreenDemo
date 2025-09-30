package com.iwip.harbor.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

/**
 * 卸货子对象 dock_unload_detail
 * 
 * @author Fei
 * @date 2025-01-28
 */
public class DockUnloadDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 卸货子表主键 */
    private Long dudId;
    private Long planId;

    /** 卸货主键 */
    private Long duId;


    /** 装货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "暂停开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /** 卸货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "暂停恢复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /** 有效时间 */
    @Excel(name = "有效时间")
    private String pauseInterval;


    /** 记录标识（0未记录 1已记录 */
    @Excel(name = "记录标识", readConverterExp = "0=未记录,1=已记录,2=未知")
    private String recordStatus;


    /** 原因 */
    @Excel(name = "原因")
    private String reason;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 删除状态(0未删除 1已删除) */
    private String delFlag;

    /** 用户id */
    private Long userId;

    /** 备注1 */
    private String remark01;

    /** 备注2 */
    private String remark02;

    /** 备注3 */
    private String remark03;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getDudId() {
        return dudId;
    }

    public void setDudId(Long dudId) {
        this.dudId = dudId;
    }

    public Long getDuId() {
        return duId;
    }

    public void setDuId(Long duId) {
        this.duId = duId;
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

    public String getPauseInterval() {
        return pauseInterval;
    }

    public void setPauseInterval(String pauseInterval) {
        this.pauseInterval = pauseInterval;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getReason() 
    {
        return reason;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRemark01(String remark01) 
    {
        this.remark01 = remark01;
    }

    public String getRemark01() 
    {
        return remark01;
    }
    public void setRemark02(String remark02) 
    {
        this.remark02 = remark02;
    }

    public String getRemark02() 
    {
        return remark02;
    }
    public void setRemark03(String remark03) 
    {
        this.remark03 = remark03;
    }

    public String getRemark03() 
    {
        return remark03;
    }


    @Override
    public String toString() {
        return "DockUnloadDetail{" +
                "dudId=" + dudId +
                ", duId=" + duId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", effectiveTime='" + pauseInterval + '\'' +
                ", reason='" + reason + '\'' +
                ", deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", userId=" + userId +
                ", remark01='" + remark01 + '\'' +
                ", remark02='" + remark02 + '\'' +
                ", remark03='" + remark03 + '\'' +
                '}';
    }
}
