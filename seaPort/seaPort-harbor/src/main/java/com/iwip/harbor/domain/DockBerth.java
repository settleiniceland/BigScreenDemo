package com.iwip.harbor.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.iwip.common.annotation.Excel;
import com.iwip.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 泊位信息对象 dock_berth
 * 
 * @author ruoyi
 * @date 2025-02-05
 */
public class DockBerth extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 泊位主键 */
    private Long berthId;

    /** 码头信息 */
    @Excel(name = "码头信息")
    private Long berthHpId;

    /** 泊位名称 */
    @Excel(name = "泊位名称")
    private String berthName;

    /** 泊位状态（0：空闲 1：占用 2：维护） */
    @Excel(name = "泊位状态", readConverterExp = "0=：空闲,1=：占用,2=：维护")
    private String berthStatus;

    /** 排序 */
    @Excel(name = "排序")
    private Long berthSort;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 地理位置 */
    @Excel(name = "地理位置")
    private String berthGeoJson;

    /** 分类（1大码头 2驳船码头） */
    @Excel(name = "分类（1大码头 2驳船码头）")
    private String berthType;


    /** 泊位编码 */
    @Excel(name = "泊位编码")
    private String berthCode;

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

    public String getBerthCode() {
        return berthCode;
    }

    public void setBerthCode(String berthCode) {
        this.berthCode = berthCode;
    }

    public void setBerthSort(Long berthSort) {
        this.berthSort = berthSort;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    private List<DockPier> dockPierList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DockPier> getDockPierList() {
        return dockPierList;
    }

    public void setDockPierList(List<DockPier> dockPierList) {
        this.dockPierList = dockPierList;
    }

    public void setBerthId(Long berthId)
    {
        this.berthId = berthId;
    }

    public Long getBerthId() 
    {
        return berthId;
    }

    public Long getBerthHpId() {
        return berthHpId;
    }

    public void setBerthHpId(Long berthHpId) {
        this.berthHpId = berthHpId;
    }

    public void setBerthName(String berthName)
    {
        this.berthName = berthName;
    }

    public String getBerthName() 
    {
        return berthName;
    }
    public void setBerthStatus(String berthStatus) 
    {
        this.berthStatus = berthStatus;
    }

    public String getBerthStatus() 
    {
        return berthStatus;
    }

    public Long getBerthSort() {
        return berthSort;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setBerthGeoJson(String berthGeoJson) 
    {
        this.berthGeoJson = berthGeoJson;
    }

    public String getBerthGeoJson() 
    {
        return berthGeoJson;
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
            .append("berthId", getBerthId())
            .append("berthHpId", getBerthHpId())
            .append("berthName", getBerthName())
            .append("berthStatus", getBerthStatus())
            .append("berthSort", getBerthSort())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("berthGeoJson", getBerthGeoJson())
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
