package com.iwip.harbor.domain.vo;

/**
 * @author taoqz
 * @create 2025-04-11
 */
public class DockBerthStatusInfoVo {

    /** 泊位名称 */
    private String berthName;

    /** 泊位编码 */
    private String berthCode;

    /** 备注1 */
    private String remark01;

    public DockBerthStatusInfoVo() {
    }

    public DockBerthStatusInfoVo(String berthName, String berthCode, String remark01) {
        this.berthName = berthName;
        this.berthCode = berthCode;
        this.remark01 = remark01;
    }

    public String getBerthName() {
        return berthName;
    }

    public void setBerthName(String berthName) {
        this.berthName = berthName;
    }

    public String getBerthCode() {
        return berthCode;
    }

    public void setBerthCode(String berthCode) {
        this.berthCode = berthCode;
    }

    public String getRemark01() {
        return remark01;
    }

    public void setRemark01(String remark01) {
        this.remark01 = remark01;
    }
}
