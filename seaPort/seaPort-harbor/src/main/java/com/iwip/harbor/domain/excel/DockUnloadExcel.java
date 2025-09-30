package com.iwip.harbor.domain.excel;


import com.iwip.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class DockUnloadExcel {

    @Excel(name = "序号")
    private Integer sort;

    @Excel(name = "值班时间")
    private LocalDate classTime;

    @Excel(name = "班组")
    private String classes;

    @Excel(name = "负责人")
    private String leader;

    @Excel(name = "品名")
    private String materialName;

    @Excel(name = "卸货泊位")
    private String berthName;

    @Excel(name = "卸货机具")
    private String workEquipment;

    @Excel(name = "总重量（吨）")
    private String tonnage;

    @Excel(name = "总件数")
    private BigDecimal packageNum;

    @Excel(name = "当天卸货吨数")
    private BigDecimal totalUnloadWeight;

    @Excel(name = "当天卸货件数")
    private BigDecimal unloadNum;

    @Excel(name = "当天卸货件数（吨）")
    private BigDecimal unloadNumTonDay;

    @Excel(name = "当天卸货比重")
    private String unloadRatio;

    @Excel(name = "有效卸率(T|PCS)/H")
    private BigDecimal effectiveRate;

    @Excel(name = "平均卸率(T|PCS)/H")
    private BigDecimal avgDischargeRate;

    @Excel(name = "到港时间" , dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    @Excel(name = "靠泊时间" , dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dockingTime;

    @Excel(name = "作业开始时间" , dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    @Excel(name = "作业结束时间" , dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Excel(name = "离泊时间" , dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outBerthTime;

    @Excel(name = "作业有效时间")
    private String effectiveTime;

    @Excel(name = "暂停原因")
    private String pauseReason;

    @Excel(name = "备注")
    private String remark;

}
