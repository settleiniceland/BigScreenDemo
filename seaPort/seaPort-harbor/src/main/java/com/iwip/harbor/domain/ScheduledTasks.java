package com.iwip.harbor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

/**
 * 任务管理（用于重启之后自动恢复读取PLC）对象 scheduled_tasks
 * 
 * @author ruoyi
 * @date 2025-02-17
 */
public class ScheduledTasks extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long planId;

    /** 任务状态(RUNNING/STOPPED) */
    @Excel(name = "任务状态(RUNNING/STOPPED)")
    private String status;
    private String berthName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getBerthName() {
        return berthName;
    }

    public void setBerthName(String berthName) {
        this.berthName = berthName;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("planId", getPlanId())
            .append("status", getStatus())
            .toString();
    }
}
