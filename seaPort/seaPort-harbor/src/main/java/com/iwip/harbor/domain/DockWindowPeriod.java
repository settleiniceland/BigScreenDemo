package com.iwip.harbor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DockWindowPeriod extends BaseEntity {
    private Long id;
    private Long planId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer periodType;
    private Long stopClassId;
    private String stopClassDetail;
    private Boolean avoidCollectFee;
    private String remark;
    private String remark1;
    private String remark2;
    private String remark3;
}
