package com.iwip.harbor.domain.vo;

import lombok.Data;

/**
 * @author taoqz
 * @create 2025-04-18
 */
@Data
public class DockPlanEfficiencyHourVo {

    private String time;

    private String rate;

    private String unloadWeight;

    private String unloadNum;

    private String exceptionReason;

    private String exceptionStatus;

}
