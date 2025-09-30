package com.iwip.harbor.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

/**
 * 装卸率对象 dock_load_rate
 * 
 * @author ruoyi
 * @date 2025-02-04
 */
public class DockLoadRate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 装卸率主键 */
    private Long id;

    /** 计划单主键 */
    @Excel(name = "计划单主键")
    private Long planId;
    private String berthName;


    /** 装卸率 */
    @Excel(name = "装卸率")
    private BigDecimal rate;

    @Excel(name = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;


    public String getBerthName() {
        return berthName;
    }

    public void setBerthName(String berthName) {
        this.berthName = berthName;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setRate(BigDecimal rate)
    {
        this.rate = rate;
    }

    public BigDecimal getRate() 
    {
        return rate;
    }

    @Override
    public String toString() {
        return "DockLoadRate{" +
                "id=" + id +
                ", planId=" + planId +
                ", rate=" + rate +
                ", endTime=" + endTime +
                '}';
    }
}
