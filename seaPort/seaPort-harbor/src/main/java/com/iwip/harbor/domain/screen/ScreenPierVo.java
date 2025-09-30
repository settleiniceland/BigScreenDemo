package com.iwip.harbor.domain.screen;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ScreenPierVo {


    /** 码头主键 */
    private Long pierId;
    /** 码头名称 */
    private String pierName;
    /** 码头编码 */
    private String pierCode;
    /** 码头坐标 */
    private String pierGeoJson;
    /** 暂停原因 */
    private String reason;
    /** 已卸重量 */
    private BigDecimal unloadYetNum;
    /** 剩余重量 */
    private BigDecimal surplusNum;
    /** 平均卸率 */
    private BigDecimal avgUnloadRate;
    /** 泊位信息 */
    private List<ScreenBerthVo> berthChildrenList;


}
