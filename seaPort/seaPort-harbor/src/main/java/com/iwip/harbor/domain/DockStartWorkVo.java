package com.iwip.harbor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.annotation.Excel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DockStartWorkVo {

    /** 计划单主键 */
    private Long planId;

    /** 作业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /** 负责人 */
    private String leader;

    /** 班次时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate classTime;

    /** 班次 */
    private String classes;

}
