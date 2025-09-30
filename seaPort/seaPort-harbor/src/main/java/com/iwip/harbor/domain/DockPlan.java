package com.iwip.harbor.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 计划单对象 dock_plan
 *
 * @author Fei
 * @date 2025-02-03
 */
public class DockPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 计划单主键 */
    private Long id;
    private List<Long> ids;

    // 泊位主键数组
    private List<Long> hbIds;

    // 码头ID
    private Long pierId;

    /** 内/外贸船 */
    @Excel(name = "内/外贸船")
    private String shipRade;

    /** 船名 */
    @Excel(name = "船名")
    private String shipName;

    /** 国际海事组织 */
    @Excel(name = "国际海事组织")
    private String imo;

    /** 船长度(米) */
    @Excel(name = "船长度(米)")
    private BigDecimal shipLength;

    /** 使用单位 */
    @Excel(name = "使用单位")
    private String usageUnit;

    /** 物料名称 */
    @Excel(name = "物料名称")
    private String materialName;

    @Excel(name = "计划(吨位/件数)")
    private String planTonnage;

    @Excel(name = "实际(吨位/件数)")
    private String tonnage;

    /** 泊位主键 */
    @Excel(name = "泊位主键")
    private Long hbId;

    /** 泊位名称 */
    @Excel(name = "泊位名称")
    private String hbName;

    /** 状态（0计划 1在途 2到港 3等泊 4靠泊 5离泊 6装卸货） */
    @Excel(name = "状态", readConverterExp = "0=计划,1=在途,2=到港,3=等泊,4=靠泊,5=离泊,6=装卸货")
    private String status;


    /** 状态（0计划 1在途 2到港 3等泊 4靠泊 5离泊 6装卸货） */
    @Excel(name = "是否卸货", readConverterExp = "0=否,1=是")
    private String unloadStatus;

    /** 状态（0计划 1在途 2到港 3等泊 4靠泊 5离泊 6装卸货） */
    @Excel(name = "大屏状态（0显示 1不显示）")
    private String screenStatus;

    /** 是否归档（0否 1是） */
    @Excel(name = "是否归档（0否 1是）")
    private String isArchived;

    /** 归档月份 */
    @Excel(name = "归档月份")
    private String archivedMonth;

    /** 卸货数量 */
    @Excel(name = "卸货数量")
    private Long packageNum;

    /** 等泊原因 */
    @Excel(name = "等泊原因")
    private String reason;

    /** 上一港 */
    @Excel(name = "上一港")
    private String lastPort;

    /** 下一港 */
    @Excel(name = "下一港")
    private String nextPort;

    /** 船代理公司 */
    @Excel(name = "船代理公司")
    private String shipAgency;

    /** 合同费率/天 */
    @Excel(name = "合同费率/天")
    private BigDecimal contractRate;


    /** 合同卸率/天 */
    @Excel(name = "合同卸率/天")
    private BigDecimal contractFee;

    /** 计划单类型（1滞期 2速遣） */
    @Excel(name = "计划单类型（1滞期 2速遣）")
    private String planType;

    /** 水尺 */
    @Excel(name = "水尺")
    private String draft;

    /** 请购单号 */
    @Excel(name = "请购单号")
    private String orderNo;

    /** 合同号 */
    @Excel(name = "合同号")
    private String contractNo;

    /** 供应商名称 */
    @Excel(name = "供应商名称")
    private String suplierName;

    /** 批次 */
    @Excel(name = "批次")
    private String batchNumber;

    /** 已装货量 */
    @Excel(name = "已装货量")
    private String unloadWeight;

    /** 总吨 */
    @Excel(name = "总吨",cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal weight;

    /** 重量 */
    @Excel(name = "矿号")
    private String mineNumber;

    /** 卡数 */
    @Excel(name = "卡数")
    private String cardCount;

    @Excel(name = "部门主键")
    private Long deptId;

    /** 靠泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "靠泊时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime dockingTime;

    /** 计划靠泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划靠泊时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime planDockingTime;

    /** 作业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "作业时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime operationTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime endTime;

    /** 离港时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "离港时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime leaveTime;

    /** 到港日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到港日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime arrivalLocalDateTime;

    /** 离上港时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到港时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime arrivalTime;

    /** 计划时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime planTime;

    /** 离泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "离泊时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime outBerthTime;

    /** 移泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "移泊时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime moveBerthTime;

    /** 检测公司 */
    @Excel(name = "检测公司")
    private String inspectionCompany;

    /** 装货港 */
    @Excel(name = "装货港")
    private String loadingPort;

    /** 剩余重量 */
    @Excel(name = "剩余重量")
    private String remainingWeight;

    /** 卸率/吨 */
    @Excel(name = "平均卸率/吨")
    private String avgDischargeRate;

    /** 滞期费 */
    @Excel(name = "滞期费")
    private BigDecimal demurrageFee;

    /** 滞期费 */
    @Excel(name = "滞期费")
    private BigDecimal plcRealTime;

    /** 滞期费 */
    @Excel(name = "滞期费")
    private BigDecimal plcSumWeight;

    /** 滞期费 */
    @Excel(name = "滞期费")
    private BigDecimal plcLastWeight;

    /** 合同执行人员 */
    @Excel(name = "合同执行人员")
    private String handledBy;

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

    /** 来源 */
    @Excel(name = "来源")
    private String sourceType;

    @Excel(name = "来源ID")
    private Long sourceId;

    @Excel(name = "码头分类")
    private String pierType;

    /** 卸率/吨 */
    @Excel(name = "有效时间")
    private String effectiveTime;

    /** 卸率/吨 */
    @Excel(name = "有效卸率")
    private BigDecimal effectiveRate;

    // 填写剩余重量时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateWeightTime;


    // 开始时间范围查询
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endEndTime;

    private Long year;

    public LocalDateTime getPlanDockingTime() {
        return planDockingTime;
    }

    public void setPlanDockingTime(LocalDateTime planDockingTime) {
        this.planDockingTime = planDockingTime;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getScreenStatus() {
        return screenStatus;
    }

    public void setScreenStatus(String screenStatus) {
        this.screenStatus = screenStatus;
    }

    public BigDecimal getPlcRealTime() {
        return plcRealTime;
    }

    public void setPlcRealTime(BigDecimal plcRealTime) {
        this.plcRealTime = plcRealTime;
    }

    public BigDecimal getPlcSumWeight() {
        return plcSumWeight;
    }

    public void setPlcSumWeight(BigDecimal plcSumWeight) {
        this.plcSumWeight = plcSumWeight;
    }

    public BigDecimal getPlcLastWeight() {
        return plcLastWeight;
    }

    public void setPlcLastWeight(BigDecimal plcLastWeight) {
        this.plcLastWeight = plcLastWeight;
    }

    public List<Long> getHbIds() {
        return hbIds;
    }

    public void setHbIds(List<Long> hbIds) {
        this.hbIds = hbIds;
    }

    public String getArchivedMonth() {
        return archivedMonth;
    }

    public void setArchivedMonth(String archivedMonth) {
        this.archivedMonth = archivedMonth;
    }

    public Long getPierId() {
        return pierId;
    }

    public void setPierId(Long pierId) {
        this.pierId = pierId;
    }

    public String getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(String isArchived) {
        this.isArchived = isArchived;
    }

    public String getUnloadStatus() {
        return unloadStatus;
    }

    public void setUnloadStatus(String unloadStatus) {
        this.unloadStatus = unloadStatus;
    }

    public LocalDateTime getUpdateWeightTime() {
        return updateWeightTime;
    }

    public void setUpdateWeightTime(LocalDateTime updateWeightTime) {
        this.updateWeightTime = updateWeightTime;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public LocalDateTime getMoveBerthTime() {
        return moveBerthTime;
    }

    public void setMoveBerthTime(LocalDateTime moveBerthTime) {
        this.moveBerthTime = moveBerthTime;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public BigDecimal getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(BigDecimal effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public String getMineNumber() {
        return mineNumber;
    }

    public void setMineNumber(String mineNumber) {
        this.mineNumber = mineNumber;
    }


    public String getPierType() {
        return pierType;
    }

    public void setPierType(String pierType) {
        this.pierType = pierType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }




    public String getRemainingWeight() {
        return remainingWeight;
    }

    public void setRemainingWeight(String remainingWeight) {
        this.remainingWeight = remainingWeight;
    }

    public BigDecimal getContractRate() {
        return contractRate;
    }

    public void setContractRate(BigDecimal contractRate) {
        this.contractRate = contractRate;
    }

    public BigDecimal getContractFee() {
        return contractFee;
    }

    public void setContractFee(BigDecimal contractFee) {
        this.contractFee = contractFee;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Long getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Long packageNum) {
        this.packageNum = packageNum;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getPlanTonnage() {
        return planTonnage;
    }

    public void setPlanTonnage(String planTonnage) {
        this.planTonnage = planTonnage;
    }

    public LocalDateTime getEndStartTime() {
        return endStartTime;
    }

    public void setEndStartTime(LocalDateTime endStartTime) {
        this.endStartTime = endStartTime;
    }

    public void setEndEndTime(LocalDateTime endEndTime) {
        this.endEndTime = endEndTime;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setShipRade(String shipRade)
    {
        this.shipRade = shipRade;
    }

    public String getShipRade()
    {
        return shipRade;
    }
    public void setShipName(String shipName)
    {
        this.shipName = shipName;
    }

    public String getShipName()
    {
        return shipName;
    }
    public void setImo(String imo)
    {
        this.imo = imo;
    }

    public String getImo()
    {
        return imo;
    }
    public void setShipLength(BigDecimal shipLength)
    {
        this.shipLength = shipLength;
    }

    public BigDecimal getShipLength()
    {
        return shipLength;
    }
    public void setUsageUnit(String usageUnit)
    {
        this.usageUnit = usageUnit;
    }

    public String getUsageUnit()
    {
        return usageUnit;
    }
    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setTonnage(String tonnage)
    {
        this.tonnage = tonnage;
    }

    public String getTonnage()
    {
        return tonnage;
    }

    public Long getHbId() {
        return hbId;
    }

    public void setHbId(Long hbId) {
        this.hbId = hbId;
    }

    public void setHbName(String hbName)
    {
        this.hbName = hbName;
    }

    public String getHbName()
    {
        return hbName;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setLastPort(String lastPort)
    {
        this.lastPort = lastPort;
    }

    public String getLastPort()
    {
        return lastPort;
    }
    public void setNextPort(String nextPort)
    {
        this.nextPort = nextPort;
    }

    public String getNextPort()
    {
        return nextPort;
    }
    public void setShipAgency(String shipAgency)
    {
        this.shipAgency = shipAgency;
    }

    public String getShipAgency()
    {
        return shipAgency;
    }
    public void setDraft(String draft)
    {
        this.draft = draft;
    }

    public String getDraft()
    {
        return draft;
    }
    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }
    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public String getContractNo()
    {
        return contractNo;
    }
    public void setSuplierName(String suplierName)
    {
        this.suplierName = suplierName;
    }

    public String getSuplierName()
    {
        return suplierName;
    }
    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public String getUnloadWeight() {
        return unloadWeight;
    }

    public void setUnloadWeight(String unloadWeight) {
        this.unloadWeight = unloadWeight;
    }

    public void setCardCount(String cardCount)
    {
        this.cardCount = cardCount;
    }

    public String getCardCount()
    {
        return cardCount;
    }
    public void setDockingTime(LocalDateTime dockingTime)
    {
        this.dockingTime = dockingTime;
    }

    public LocalDateTime getDockingTime()
    {
        return dockingTime;
    }
    public void setOperationTime(LocalDateTime operationTime)
    {
        this.operationTime = operationTime;
    }

    public LocalDateTime getOperationTime()
    {
        return operationTime;
    }
    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime()
    {
        return endTime;
    }
    public void setLeaveTime(LocalDateTime leaveTime)
    {
        this.leaveTime = leaveTime;
    }

    public LocalDateTime getLeaveTime()
    {
        return leaveTime;
    }
    public void setArrivalLocalDateTime(LocalDateTime arrivalLocalDateTime)
    {
        this.arrivalLocalDateTime = arrivalLocalDateTime;
    }

    public LocalDateTime getArrivalLocalDateTime()
    {
        return arrivalLocalDateTime;
    }

    public void setPlanTime(LocalDateTime planTime)
    {
        this.planTime = planTime;
    }

    public LocalDateTime getPlanTime()
    {
        return planTime;
    }
    public void setOutBerthTime(LocalDateTime outBerthTime)
    {
        this.outBerthTime = outBerthTime;
    }

    public LocalDateTime getOutBerthTime()
    {
        return outBerthTime;
    }
    public void setInspectionCompany(String inspectionCompany)
    {
        this.inspectionCompany = inspectionCompany;
    }

    public String getInspectionCompany()
    {
        return inspectionCompany;
    }
    public void setLoadingPort(String loadingPort)
    {
        this.loadingPort = loadingPort;
    }

    public String getLoadingPort()
    {
        return loadingPort;
    }

    public String getAvgDischargeRate() {
        return avgDischargeRate;
    }

    public void setAvgDischargeRate(String avgDischargeRate) {
        this.avgDischargeRate = avgDischargeRate;
    }

    public void setDemurrageFee(BigDecimal demurrageFee)
    {
        this.demurrageFee = demurrageFee;
    }

    public BigDecimal getDemurrageFee()
    {
        return demurrageFee;
    }
    public void setHandledBy(String handledBy)
    {
        this.handledBy = handledBy;
    }

    public String getHandledBy()
    {
        return handledBy;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("shipRade", getShipRade())
            .append("shipName", getShipName())
            .append("imo", getImo())
            .append("shipLength", getShipLength())
            .append("usageUnit", getUsageUnit())
            .append("materialName", getMaterialName())
            .append("tonnage", getTonnage())
            .append("hbId", getHbId())
            .append("hbName", getHbName())
            .append("status", getStatus())
            .append("lastPort", getLastPort())
            .append("nextPort", getNextPort())
            .append("shipAgency", getShipAgency())
            .append("draft", getDraft())
            .append("orderNo", getOrderNo())
            .append("contractNo", getContractNo())
            .append("suplierName", getSuplierName())
            .append("batchNumber", getBatchNumber())
            .append("unloadWeight", getUnloadWeight())
            .append("weight", getWeight())
            .append("cardCount", getCardCount())
            .append("dockingTime", getDockingTime())
            .append("operationTime", getOperationTime())
            .append("endTime", getEndTime())
            .append("leaveTime", getLeaveTime())
            .append("arrivalLocalDateTime", getArrivalLocalDateTime())
            .append("planTime", getPlanTime())
            .append("outBerthTime", getOutBerthTime())
            .append("inspectionCompany", getInspectionCompany())
            .append("loadingPort", getLoadingPort())
            .append("avgDischargeRate", getAvgDischargeRate())
            .append("demurrageFee", getDemurrageFee())
            .append("handledBy", getHandledBy())
            .append("delFlag", getDelFlag())
            .append("userId", getUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark01", getRemark01())
            .append("remark02", getRemark02())
            .append("remark03", getRemark03())
            .toString();
    }
}
