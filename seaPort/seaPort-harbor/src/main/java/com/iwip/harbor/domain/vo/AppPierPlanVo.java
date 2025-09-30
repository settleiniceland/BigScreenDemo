package com.iwip.harbor.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author taoqz
 * @create 2025-04-14
 */
@Data
public class AppPierPlanVo {

    private Long planId;

    /**
     * 泊位名称
     */
    private String berthName;

    /**
     * 船名称
     */
    private String shipName;

    /**
     * 货物名称
     */
    private String materialName;

    /**
     * 矿号
     */
    private String mineNumber;

    /**
     * 码头类型
     */
    private String pierType;

    /**
     * 进度
     */
    private String progress;

    /**
     * 总重量
     */
    private String tonnage;

    /**
     * 剩余重量
     */
    private BigDecimal unloadWeight;

    /**
     * 剩余重量
     */
    private BigDecimal remainingWeight;

    /**
     * 平均卸率
     */
    private String avgDischargeRate;

    /**
     * 船舶状态
     */
    private String status;

    private String workType;

    private String reason;


    // 异常标识
    private Boolean exceptionFlag;

    // 异常原因
    private String exceptionReason;

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

}
