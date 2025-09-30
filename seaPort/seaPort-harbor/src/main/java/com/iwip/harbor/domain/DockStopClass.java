package com.iwip.harbor.domain;

import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class DockStopClass extends BaseEntity {
    private Long id;
    private String detail;
    private Boolean avoidCollectFee;
    private Boolean avoidEfficiency;
    private String remark01;
    private String remark02;
    private String remark03;
}
