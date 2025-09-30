package com.iwip.harbor.domain.screen;

import lombok.Data;

@Data
public class ScreenUnloadVo {


    /** 卸货单主键 */
    private Long duId;
    /** 计划单主键 */
    private Long planId;
    /** 班次时间 */
    private String classTime;
    /** 班次(白班夜班) */
    private String classes;
    /** 作业状态（0进行中 1暂停 2结束） */
    private String workType;
    /** 作业有效时间(作业总暂停时间) */
    private String effectiveTime;
    /** 负责人 */
    private String leader;
    /** 负责人 */
    private String shipName;
    /** 负责人 */
    private String tonnage;
    /** 船泊名称 */
    private String hbName;
    /** 船泊主键 */
    private Long hbId;

}
