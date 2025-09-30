package com.iwip.harbor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * 卸货单对象 dock_unload_work
 *
 * @author ruoyi
 * @date 2025-02-05
 */
public class DockUnloadWork extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private List<Long> ids;


    /** 卸货单主键 */
    private Long duId;

    /** 计划单主键 */
    @Excel(name = "计划单主键")
    private Long planId;


    /** 班次 */
    @Excel(name = "作业状态（0进行中 1暂停 2结束）")
    private String workType;

    /** 班次 */
    @Excel(name = "班次")
    private String classes;


    /** 暂停原因 */
    @Excel(name = "暂停原因")
    private String pauseReason;

    /** 作业有效时间 */
    @Excel(name = "作业有效时间")
    private String effectiveTime;

    /** 作业机具 */
    @Excel(name = "作业机具")
    private String workEquipment;

    /** 班次总卸重量 */
    @Excel(name = "班次总卸重量")
    private BigDecimal totalUnloadWeight;

    @Excel(name = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;


    /** 班次时间 */
    @Excel(name = "班次时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate classTime;
    /** 停止时间 */
    @Excel(name = "停止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 删除状态(0未删除 1已删除) */
    private String delFlag;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark01;

    /** 备注2 */
    @Excel(name = "备注2")
    private String remark02;

    /** 备注3 */
    @Excel(name = "备注3")
    private String remark03;

    /** 负责人 */
    @Excel(name = "负责人")
    private String leader;

    /** 卸货数 */
    @Excel(name = "卸货数")
    private String packageNum;

    /** 当班卸货件数 */
    @Excel(name = "当班卸货件数")
    private Long unloadNum;


    // 创建时间范围查询
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdStartTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdEndTime;

    /** 有效卸率 */
    @Excel(name = "有效卸率")
    private BigDecimal effectiveRate;


    @Excel(name = "船名")
    private String shipName;

    @Excel(name = "暂停间隔")
    private String pauseInterval;

    @Excel(name = "平均卸率")
    private BigDecimal avgDischargeRate;

    @Excel(name = "暂停次数")
    private int stopCount;


    /** 卸货单在结束时是否为暂停态（0否 1是）*/
    private String pausedAtEnd;

    private List<DockUnloadDetail> dockUnloadDetailList;


    public List<DockUnloadDetail> getDockUnloadDetailList() {
        return dockUnloadDetailList;
    }

    public void setDockUnloadDetailList(List<DockUnloadDetail> dockUnloadDetailList) {
        this.dockUnloadDetailList = dockUnloadDetailList;
    }

    public int getStopCount() {
        return stopCount;
    }

    public void setStopCount(int stopCount) {
        this.stopCount = stopCount;
    }

    public String getWorkEquipment() {
        return workEquipment;
    }

    public void setWorkEquipment(String workEquipment) {
        this.workEquipment = workEquipment;
    }

    public String getPauseReason() {
        return pauseReason;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    public String getPauseInterval() {
        return pauseInterval;
    }

    public void setPauseInterval(String pauseInterval) {
        this.pauseInterval = pauseInterval;
    }

    public BigDecimal getAvgDischargeRate() {
        return avgDischargeRate;
    }

    public void setAvgDischargeRate(BigDecimal avgDischargeRate) {
        this.avgDischargeRate = avgDischargeRate;
    }

    public BigDecimal getTotalUnloadWeight() {
        return totalUnloadWeight;
    }

    public void setTotalUnloadWeight(BigDecimal totalUnloadWeight) {
        this.totalUnloadWeight = totalUnloadWeight;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    public Long getUnloadNum() {
        return unloadNum;
    }

    public void setUnloadNum(Long unloadNum) {
        this.unloadNum = unloadNum;
    }


    public BigDecimal getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(BigDecimal effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public LocalDateTime getCreatedStartTime() {
        return createdStartTime;
    }

    public void setCreatedStartTime(LocalDateTime createdStartTime) {
        this.createdStartTime = createdStartTime;
    }

    public LocalDateTime getCreatedEndTime() {
        return createdEndTime;
    }

    public void setCreatedEndTime(LocalDateTime createdEndTime) {
        this.createdEndTime = createdEndTime;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public LocalDate getClassTime() {
        return classTime;
    }

    public void setClassTime(LocalDate classTime) {
        this.classTime = classTime;
    }

    public void setDuId(Long duId)
    {
        this.duId = duId;
    }

    public Long getDuId()
    {
        return duId;
    }
    public void setPlanId(Long planId)
    {
        this.planId = planId;
    }

    public Long getPlanId()
    {
        return planId;
    }
    public void setClasses(String classes)
    {
        this.classes = classes;
    }

    public String getClasses()
    {
        return classes;
    }
    public void setEffectiveTime(String effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }

    public String getEffectiveTime()
    {
        return effectiveTime;
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

    public String getPausedAtEnd() {
        return pausedAtEnd;
    }

    public void setPausedAtEnd(String pausedAtEnd) {
        this.pausedAtEnd = pausedAtEnd;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
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
        return "DockUnloadWork{" +
                "duId=" + duId +
                ", planId=" + planId +
                ", workType='" + workType + '\'' +
                ", classes='" + classes + '\'' +
                ", effectiveTime='" + effectiveTime + '\'' +
                ", startTime=" + startTime +
                ", classTime=" + classTime +
                ", endTime=" + endTime +
                ", deptId='" + deptId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", userId=" + userId +
                ", remark01='" + remark01 + '\'' +
                ", remark02='" + remark02 + '\'' +
                ", remark03='" + remark03 + '\'' +
                ", createdStartTime=" + createdStartTime +
                ", createdEndTime=" + createdEndTime +
                '}';
    }
}
