package com.iwip.harbor.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author taoqz
 * @create 2025-04-04
 */
public class DockMaterialVo {

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 数量
     */
    private int count;

    /**
     * 状态
     */
    @JsonIgnore
    private String status;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
