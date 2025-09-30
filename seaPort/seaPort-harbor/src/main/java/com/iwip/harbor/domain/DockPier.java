package com.iwip.harbor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 码头信息对象 dock_pier
 * 
 * @author ruoyi
 * @date 2025-02-05
 */
public class DockPier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 码头主键 */
    private Long pierId;

    /** 码头名称 */
    @Excel(name = "码头名称")
    private String pierName;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 地理位置 */
    @Excel(name = "地理位置")
    private String pierGeoJson;

    /** 码头名称 */
    @Excel(name = "码头名称")
    private String pierType;


    /** 码头名称 */
    @Excel(name = "码头名称")
    private String pierCode;


    /** 删除状态(0未删除 1已删除) */
    private String delFlag;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark01;

    /** 备注2 */
    @Excel(name = "备注2")
    private String remark02;

    /** 备注3 */
    @Excel(name = "备注3")
    private String remark03;

    private String type;

    @Excel(name = "名称")
    private String name;
    private String berthStatus;
    private String pierRemark;


    public String getPierCode() {
        return pierCode;
    }

    public void setPierCode(String pierCode) {
        this.pierCode = pierCode;
    }

    public String getPierRemark() {
        return pierRemark;
    }

    public void setPierRemark(String pierRemark) {
        this.pierRemark = pierRemark;
    }

    public String getPierType() {
        return pierType;
    }

    public void setPierType(String pierType) {
        this.pierType = pierType;
    }

    public String getBerthStatus() {
        return berthStatus;
    }

    public void setBerthStatus(String berthStatus) {
        this.berthStatus = berthStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private List<DockBerth> children;

    public List<DockBerth> getChildren() {
        return children;
    }

    public void setChildren(List<DockBerth> children) {
        this.children = children;
    }

    public void setPierId(Long pierId)
    {
        this.pierId = pierId;
    }

    public Long getPierId() 
    {
        return pierId;
    }
    public void setPierName(String pierName) 
    {
        this.pierName = pierName;
    }

    public String getPierName() 
    {
        return pierName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setPierGeoJson(String pierGeoJson) 
    {
        this.pierGeoJson = pierGeoJson;
    }

    public String getPierGeoJson() 
    {
        return pierGeoJson;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
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
            .append("pierId", getPierId())
            .append("pierName", getPierName())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("pierGeoJson", getPierGeoJson())
            .append("delFlag", getDelFlag())
            .append("userId", getUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("remark01", getRemark01())
            .append("remark02", getRemark02())
            .append("remark03", getRemark03())
            .toString();
    }
}
