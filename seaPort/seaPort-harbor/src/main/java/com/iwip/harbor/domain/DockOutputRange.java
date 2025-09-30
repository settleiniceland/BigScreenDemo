package com.iwip.harbor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 百分比时间段对应产量对象 dock_output_range
 * 
 * @author ruoyi
 * @date 2025-05-05
 */
public class DockOutputRange extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 计划单主键 */
    @Excel(name = "码头主键")
    private Long pierId;

    @Excel(name = "码头名称")
    private String pierName;


    /** 起始百分比（例如 0） */
    @Excel(name = "起始百分比", readConverterExp = "例=如,0=")
    private BigDecimal startPercent;

    /** 结束百分比（例如 20） */
    @Excel(name = "结束百分比", readConverterExp = "例=如,2=0")
    private BigDecimal endPercent;

    /** 卸率（吨/小时） */
    @Excel(name = "卸率", readConverterExp = "吨=/小时")
    private BigDecimal unloadRate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public String getPierName() {
        return pierName;
    }

    public void setPierName(String pierName) {
        this.pierName = pierName;
    }

    public Long getPierId() {
        return pierId;
    }

    public void setPierId(Long pierId) {
        this.pierId = pierId;
    }

    public BigDecimal getStartPercent() {
        return startPercent;
    }

    public void setStartPercent(BigDecimal startPercent) {
        this.startPercent = startPercent;
    }

    public BigDecimal getEndPercent() {
        return endPercent;
    }

    public void setEndPercent(BigDecimal endPercent) {
        this.endPercent = endPercent;
    }

    public BigDecimal getUnloadRate() {
        return unloadRate;
    }

    public void setUnloadRate(BigDecimal unloadRate) {
        this.unloadRate = unloadRate;
    }

    @Override
    public String toString() {
        return "DockOutputRange{" +
                "id=" + id +
                ", pierId=" + pierId +
                ", startPercent=" + startPercent +
                ", endPercent=" + endPercent +
                ", unloadRate=" + unloadRate +
                '}';
    }
}
