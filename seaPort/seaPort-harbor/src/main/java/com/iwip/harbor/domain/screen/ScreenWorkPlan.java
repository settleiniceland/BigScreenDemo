package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 大屏工作进度 (大码头、驳船码头)
 */
@Data
public class ScreenWorkPlan {


    private Long planId;
    /**
     * 码头类型
     */
    private String pierType;

    /**
     * 泊位名称
     */
    private String berthName;

    /**
     * 货物名称
     */
    private String materialName;

    /**
     * 进度
     */
    private String progress;

    /**
     * 平均卸率
     */
    private String avgDischargeRate;

    /**
     * 矿号
     */
    private String mineNumber;

    /**
     * 剩余重量
     */
    private String unloadWeight;

    /**
     * 总重量
     */
    private String tonnage;

    /**
     * 填写重量的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateWeightTime;

    /**
     * 作业开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /**
     * 异常布尔值 true 异常， false 正常
     */
    private Boolean exceptionFlag;

    /**
     * 异常原因
     */
    private String exceptionReason;

}
