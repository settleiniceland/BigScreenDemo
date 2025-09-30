package com.iwip.harbor.domain.screen;

import com.iwip.harbor.domain.DockBerth;
import com.iwip.harbor.domain.DockPlan;
import com.iwip.harbor.domain.vo.DockMaterialVo;
import lombok.Data;

import java.util.List;

/**
 * 计划单状态分布
 */
@Data
public class ScreenPlanStatusVo {

    private String status;

    private int count;


    private List<DockPlan> childrenPlan;   // 计划单信息
    private List<DockBerth> childrenBerth; // 泊位信息
    private List<DockMaterialVo> childrenMaterialVo; // 物资信息

}
