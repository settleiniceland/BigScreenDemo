package com.iwip.harbor.domain.screen;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 等泊信息
 */
@Data
public class ScreenWaitBerthVo {

    /** 计划单主键 */
    private Long planId;

    /** 等泊时间 */
    private String waitBerthTime;

    /** 计划等待时间 */
    private String planWaitTime;

    /** 暂定泊位主键 */
    private Long berthId;

    /** 暂定泊位 */
    private String berthName;

    /** 船名 */
    private String shipName;

    /** 到港时间 */
    private LocalDateTime arrivalTime;

    /** 靠泊时间 */
    private LocalDateTime planDockingTime;

    /** 暂定泊位 */
    private String materialName;

    /** 船长度 */
    private String shipLength;

    /** 实际吨位 */
    private String tonnage;

    /** 颜色标识 */
    private String colorFlag;







}
