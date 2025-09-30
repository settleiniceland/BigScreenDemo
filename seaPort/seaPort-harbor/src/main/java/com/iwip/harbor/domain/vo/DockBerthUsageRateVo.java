package com.iwip.harbor.domain.vo;

import com.iwip.common.annotation.Excel;
import lombok.Data;

/**
 * @author taoqz
 * @create 2025-04-17
 */
@Data
public class DockBerthUsageRateVo {

    /**
     * 泊位名称
     */
    @Excel(name = "泊位名称")
    private String berthName;

    /**
     * 使用率
     */
    @Excel(name = "泊位使用率")
    private String rate;

    private String actualRate;

}
