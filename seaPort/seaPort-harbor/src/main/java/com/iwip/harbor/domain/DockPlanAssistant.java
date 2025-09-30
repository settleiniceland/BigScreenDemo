package com.iwip.harbor.domain;

import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DockPlanAssistant extends BaseEntity {
    private Long id;
    private Long planId;
    private Integer packageNum;
    private BigDecimal tonnage;
    private BigDecimal unloadWeight;
    private String usageUnit;
    private String materialName;
    private Integer loadSequence;
    private Integer isFinish;
    private String remark01;
    private String remark02;
    private String remark03;
}
