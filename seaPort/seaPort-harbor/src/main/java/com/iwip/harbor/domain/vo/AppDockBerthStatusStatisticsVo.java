package com.iwip.harbor.domain.vo;

import com.iwip.harbor.domain.DockBerth;

import java.util.List;

/**
 * app 泊位状态统计
 * @author taoqz
 * @create 2025-04-11
 */
public class AppDockBerthStatusStatisticsVo {

    /**
     * 泊位状态
     * 0：空闲 1：占用 2：维护 3：暂停
     */
    private String berthStatus;

    private String berthStatusLabel;

    /**
     * 数量
     */
    private int count;

    /**
     * 泊位信息
     */
    private List<DockBerthStatusInfoVo> dockBerthInfoChildren;

    public String getBerthStatus() {
        return berthStatus;
    }

    public void setBerthStatus(String berthStatus) {
        this.berthStatus = berthStatus;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBerthStatusLabel() {
        return berthStatusLabel;
    }

    public void setBerthStatusLabel(String berthStatusLabel) {
        this.berthStatusLabel = berthStatusLabel;
    }

    public List<DockBerthStatusInfoVo> getDockBerthInfoChildren() {
        return dockBerthInfoChildren;
    }

    public void setDockBerthInfoChildren(List<DockBerthStatusInfoVo> dockBerthInfoChildren) {
        this.dockBerthInfoChildren = dockBerthInfoChildren;
    }
}
