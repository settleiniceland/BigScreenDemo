package com.iwip.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

/**
 * 用户Ip地址对象 system_ip
 * 
 * @author ruoyi
 * @date 2025-04-30
 */
public class SystemIp extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户主键 */
    @Excel(name = "用户主键")
    private Long userId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 备用字段01 */
    @Excel(name = "备用字段01")
    private String remark01;

    /** 备用字段02 */
    @Excel(name = "备用字段02")
    private String remark02;

    /** 备用字段03 */
    @Excel(name = "备用字段03")
    private String remark03;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
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
            .append("userId", getUserId())
            .append("name", getName())
            .append("address", getAddress())
            .append("remark", getRemark())
            .append("remark01", getRemark01())
            .append("remark02", getRemark02())
            .append("remark03", getRemark03())
            .toString();
    }
}
