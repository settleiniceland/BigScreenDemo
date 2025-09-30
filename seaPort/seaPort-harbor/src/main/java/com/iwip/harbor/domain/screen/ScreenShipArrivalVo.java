package com.iwip.harbor.domain.screen;

import com.iwip.harbor.domain.vo.DockMaterialVo;

import java.util.List;

/**
 *
 * 到船统计
 *
 * @author taoqz
 * @create 2025-04-06
 */
public class ScreenShipArrivalVo {

    private String key;

    private int count;

    private String status;

    private List<DockMaterialVo> childrenMaterialVo; // 物资信息

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public List<DockMaterialVo> getChildrenMaterialVo() {
        return childrenMaterialVo;
    }

    public void setChildrenMaterialVo(List<DockMaterialVo> childrenMaterialVo) {
        this.childrenMaterialVo = childrenMaterialVo;
    }
}
