package com.iwip.harbor.domain.vo;

import lombok.Data;

/**
 * 泊位码头实体类
 */

public class DockPierAndBerthVo {

    /** 类型 dock or berth */
    private String type;

    /** 码头主键 */
    private Long pierId;

    /** 码头名称 */
    private String pierName;

    /** 码头地理位置 */
    private String pierGeoJson;

    /** 泊位主键 */
    private Long berthId;

    /** 泊位名称 */
    private String berthName;

    /** 泊位状态（0：空闲 1：占用 2：维护） */
    private String berthStatus;

    /** 泊位地理位置 */
    private String berthGeoJson;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    private Long deptId;

    private String remark;

    private String pierType;

    private Long unloadNum;

    private String pierCode;
    private String berthCode;

    public String getPierCode() {
        return pierCode;
    }

    public void setPierCode(String pierCode) {
        this.pierCode = pierCode;
    }

    public String getBerthCode() {
        return berthCode;
    }

    public void setBerthCode(String berthCode) {
        this.berthCode = berthCode;
    }

    public Long getUnloadNum() {
        return unloadNum;
    }

    public void setUnloadNum(Long unloadNum) {
        this.unloadNum = unloadNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPierType() {
        return pierType;
    }

    public void setPierType(String pierType) {
        this.pierType = pierType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPierId() {
        return pierId;
    }

    public void setPierId(Long pierId) {
        this.pierId = pierId;
    }

    public String getPierName() {
        return pierName;
    }

    public void setPierName(String pierName) {
        this.pierName = pierName;
    }

    public String getPierGeoJson() {
        return pierGeoJson;
    }

    public void setPierGeoJson(String pierGeoJson) {
        this.pierGeoJson = pierGeoJson;
    }

    public Long getBerthId() {
        return berthId;
    }

    public void setBerthId(Long berthId) {
        this.berthId = berthId;
    }

    public String getBerthName() {
        return berthName;
    }

    public void setBerthName(String berthName) {
        this.berthName = berthName;
    }

    public String getBerthStatus() {
        return berthStatus;
    }

    public void setBerthStatus(String berthStatus) {
        this.berthStatus = berthStatus;
    }

    public String getBerthGeoJson() {
        return berthGeoJson;
    }

    public void setBerthGeoJson(String berthGeoJson) {
        this.berthGeoJson = berthGeoJson;
    }


}
