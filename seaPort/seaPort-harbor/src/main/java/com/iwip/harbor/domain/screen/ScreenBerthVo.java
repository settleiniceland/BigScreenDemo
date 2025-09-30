package com.iwip.harbor.domain.screen;

import lombok.Data;

import java.util.List;

@Data
public class ScreenBerthVo {

    /** 泊位主键 */
    private Long berthId;
    /** 泊位名称 */
    private String berthName;
    /** 泊位编码 */
    private String berthCode;
    /** 泊位坐标 */
    private String berthGeoJson;
    /** 泊位状态 */
    private String berthStatus;
    /** 暂停原因 */
    private String remark;
    /** 计划单集合 */
    private List<ScreenPlanVo> planInfoList;


}
