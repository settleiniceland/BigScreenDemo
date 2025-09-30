package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.annotation.Excel;
import com.iwip.harbor.domain.vo.DockPlanEfficiencyHourVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ScreenPlanVo {

    /**
     * 船名
     */
    private Long planId;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 矿号
     */
    private String mineNumber;
    /**
     * 物料名称
     */
    private String materialName;
    /**
     * 吨位
     */
    private String grossWeight;
    /**
     * 总件数
     */
    private String packageNum;
    /**
     * 已卸重量
     */
    private String unloadedWeight;

    /**
     * 平均卸率
     */
    private String avgDischargeRate;
    /**
     * 作业状态（0进行中 1暂停 2结束）
     */
    private String status;

    /**
     * 暂停原因
     */
    private String reason;

    /**
     * 作业状态（0进行中 1暂停 2结束）
     */
    private String workType;

    /**
     * 泊位主键
     */
    private Long berthId;

    /**
     * 泊位状态
     */
    private String berthStatus;

    /**
     * 泊位名称
     */
    private String berthName;

    /**
     * 剩余重量
     */
    private String residueWeight;
    private String  plcRealTime;
    private BigDecimal plcSumWeight;

    /** 填写剩余重量时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateWeightTime;

    /** 作业开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /** 作业结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    // 到港时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    // 计划靠泊时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planDockingTime;

    // 到港时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dockingTime;

    /**
     * 小时卸率
     */
    private List<DockPlanEfficiencyHourVo> efficiencyByHour;

}
