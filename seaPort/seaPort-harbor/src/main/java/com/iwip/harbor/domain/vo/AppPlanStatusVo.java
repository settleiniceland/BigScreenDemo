package com.iwip.harbor.domain.vo;

import com.iwip.harbor.domain.screen.ScreenPlanStatusVo;
import lombok.Data;

import java.util.List;

/**
 * @author taoqz
 * @create 2025-04-15
 */
@Data
public class AppPlanStatusVo {

    private int count;

    private List<ScreenPlanStatusVo> planStatusChildren;

}
