package com.iwip.harbor.domain.excel;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.annotation.Excel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class DockPlanRateExcel {


    @Excel(name = "唯一标识")
    private Long id;

    @Excel(name = "泊位名称")
    private String hbName;

    @Excel(name = "作业类型")
    private String remark01;

    @Excel(name = "船名")
    private String shipName;

    @Excel(name = "矿号")
    private String mineNumber;

    @Excel(name = "靠泊时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dockingTime;

    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "有效时间", cellType = Excel.ColumnType.STRING)
    private String effectiveTime;
}
