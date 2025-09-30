package com.iwip.harbor.domain;

import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DockPlanUnloadWeightUpdateLogs extends BaseEntity {
    private Long id;
    private Long planId;
    private Integer loadSequence;
    private String usageUnit;
    private String materialName;
    private Integer packageNum;
    private BigDecimal oldUnloadWeight;
    private BigDecimal newUnloadWeight;
    private Long userId;
    private String userName;
    private String nickName;
    private LocalDateTime refreshTime;
    private BigDecimal distanceLastTime;
    private String remark01;
    private String remark02;
    private String remark03;
}
