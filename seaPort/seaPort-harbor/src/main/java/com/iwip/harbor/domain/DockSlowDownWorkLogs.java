package com.iwip.harbor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DockSlowDownWorkLogs extends BaseEntity {
    private Long id;
    private Long planId;
    private Long unloadWorkId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private String remark;
    private String remark01;
    private String remark02;
    private String remark03;
}