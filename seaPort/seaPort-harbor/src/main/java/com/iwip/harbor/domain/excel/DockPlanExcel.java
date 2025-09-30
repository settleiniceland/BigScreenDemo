package com.iwip.harbor.domain.excel;
import com.iwip.common.annotation.Excel;
import java.math.BigDecimal;
import java.util.Date;


public class DockPlanExcel {

    @Excel(name = "序号")
    private Integer sort;

    @Excel(name = "内/外贸船")
    private String shipRade;

    @Excel(name = "船名")
    private String shipName;

    public String getRemark01() {
        return remark01;
    }

    public void setRemark01(String remark01) {
        this.remark01 = remark01;
    }

    @Excel(name = "作业类型(装船/卸船)")
    private String remark01;

    @Excel(name = "国际海事组织")
    private String imo;

    @Excel(name = "船长度(米)",cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal shipLength;

//    @Excel(name = "总吨",cellType = Excel.ColumnType.NUMERIC)
//    private BigDecimal weight;

    @Excel(name = "使用单位")
    private String usageUnit;

    @Excel(name = "矿号")
    private String mineNumber;

    @Excel(name = "货物名称")
    private String materialName;

    @Excel(name = "吨位/件数")
    private String tonnage;

    @Excel(name = "计划吨位/件数")
    private String planTonnage;

    @Excel(name = "到港时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalTime;

    @Excel(name = "泊位")
    private String hbName;

    @Excel(name = "状态", readConverterExp = "0=计划,1=在途,2=等泊,3=靠泊,4=在装卸,5=待离泊,6=待离港,7=完成,8=待回靠,9=移泊,10=靠泊中")
    private String status;

    @Excel(name = "卡数")
    private String cardCount;

    @Excel(name = "靠泊时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dockingTime;

    @Excel(name = "作业时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @Excel(name = "结束时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "离泊时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date outBerthTime;

    @Excel(name = "离港时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;

    @Excel(name = "上一港")
    private String lastPort;

    @Excel(name = "下一港")
    private String nextPort;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "船代理公司")
    private String shipAgency;

    @Excel(name = "水尺")
    private String draft;

    @Excel(name = "用时")
    private String effectiveTime;

    @Excel(name = "卸率")
    private String avgDischargeRate;

    @Excel(name = "滞期费")
    private String demurrageFee;

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getAvgDischargeRate() {
        return avgDischargeRate;
    }

    public void setAvgDischargeRate(String avgDischargeRate) {
        this.avgDischargeRate = avgDischargeRate;
    }

    public String getDemurrageFee() {
        return demurrageFee;
    }

    public void setDemurrageFee(String demurrageFee) {
        this.demurrageFee = demurrageFee;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getShipRade() {
        return shipRade;
    }

    public void setShipRade(String shipRade) {
        this.shipRade = shipRade;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getImo() {
        return imo;
    }

    public void setImo(String imo) {
        this.imo = imo;
    }

    public BigDecimal getShipLength() {
        return shipLength;
    }

    public void setShipLength(BigDecimal shipLength) {
        this.shipLength = shipLength;
    }

    public String getUsageUnit() {
        return usageUnit;
    }

    public void setUsageUnit(String usageUnit) {
        this.usageUnit = usageUnit;
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

    public String getPlanTonnage() {
        return planTonnage;
    }

    public void setPlanTonnage(String planTonnage) {
        this.planTonnage = planTonnage;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getHbName() {
        return hbName;
    }

    public void setHbName(String hbName) {
        this.hbName = hbName;
    }

    public String getCardCount() {
        return cardCount;
    }

    public void setCardCount(String cardCount) {
        this.cardCount = cardCount;
    }

    public Date getDockingTime() {
        return dockingTime;
    }

    public void setDockingTime(Date dockingTime) {
        this.dockingTime = dockingTime;
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

    public Date getOutBerthTime() {
        return outBerthTime;
    }

    public void setOutBerthTime(Date outBerthTime) {
        this.outBerthTime = outBerthTime;
    }

    public Date getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getLastPort() {
        return lastPort;
    }

    public void setLastPort(String lastPort) {
        this.lastPort = lastPort;
    }

    public String getNextPort() {
        return nextPort;
    }

    public void setNextPort(String nextPort) {
        this.nextPort = nextPort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getShipAgency() {
        return shipAgency;
    }

    public void setShipAgency(String shipAgency) {
        this.shipAgency = shipAgency;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public String getMineNumber() {
        return mineNumber;
    }

    public void setMineNumber(String mineNumber) {
        this.mineNumber = mineNumber;
    }

//    public BigDecimal getWeight() {
//        return weight;
//    }
//
//    public void setWeight(BigDecimal weight) {
//        this.weight = weight;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
