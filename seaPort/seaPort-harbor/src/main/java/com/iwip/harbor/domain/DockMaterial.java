package com.iwip.harbor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

/**
 * 物资信息对象 dock_material
 * 
 * @author ruoyi
 * @date 2025-02-25
 */
public class DockMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 物资名称主键 */
    private Long id;
    private Long sort;


    /** 物资名称 */
    @Excel(name = "物资名称")
    private String materialName;

    /** 物资状态（0启用 1禁用） */
    @Excel(name = "物资状态", readConverterExp = "0=启用,1=禁用")
    private String materialStatus;

    /** 删除（0未删除 1已删除） */
    private String delFlag;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark01;

    /** 备注2 */
    @Excel(name = "备注2")
    private String remark02;

    /** 备注3 */
    @Excel(name = "备注3")
    private String remark03;

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMaterialName(String materialName) 
    {
        this.materialName = materialName;
    }

    public String getMaterialName() 
    {
        return materialName;
    }
    public void setMaterialStatus(String materialStatus) 
    {
        this.materialStatus = materialStatus;
    }

    public String getMaterialStatus() 
    {
        return materialStatus;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    public void setRemark01(String remark01) 
    {
        this.remark01 = remark01;
    }

    public String getRemark01() 
    {
        return remark01;
    }
    public void setRemark02(String remark02) 
    {
        this.remark02 = remark02;
    }

    public String getRemark02() 
    {
        return remark02;
    }
    public void setRemark03(String remark03) 
    {
        this.remark03 = remark03;
    }

    public String getRemark03() 
    {
        return remark03;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("materialName", getMaterialName())
            .append("materialStatus", getMaterialStatus())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .append("remark01", getRemark01())
            .append("remark02", getRemark02())
            .append("remark03", getRemark03())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
