package com.iwip.harbor.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author taoqz
 * @create 2025-04-18
 */
@Data
public class DockPlanDockingVo {

    /** 计划单主键 */
    private Long id;

    /** 泊位名称 */
    private String hbName;

    /** 靠泊时间 */
    private LocalDateTime dockingTime;

    /** 离泊时间 */
    private LocalDateTime outBerthTime;

    /** 移泊时间 */
    private LocalDateTime moveBerthTime;

    /** 船名 */
    private String shipName;

    /** 物料名称 */
    private String materialName;

    /** 泊位主键 */
    private Long hbId;

    /** 状态（0计划 1在途 2到港 3等泊 4靠泊 5离泊 6装卸货） */
    private String status;

    /** 是否归档（0否 1是） */
    private String isArchived;

}
