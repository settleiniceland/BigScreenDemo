package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 前端回调参数
 */
@Data
public class ScreenJsonVo {

    /** 类型 0 读取进度 1 修改进度状态 2 修改泊位状态 4 靠泊  6 靠泊中*/
    private String type;

    /** 主键 */
    private Long duId;
    /** 用户主键 */
    private Long userId;
    /** 原因 */
    private String pauseReason;
    /** 备注 */
    private String remark;
    /** 作业状态（0进行中 1暂停 1结束） */
    private String workType;
    /** 卸货数量 */
    private Long unloadNum;
    /** 暂停时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /** 恢复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /** 靠泊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dockingTime;
    /** 靠泊时间 */
    private String planDockingTime;
    /** 计划主键 */
    private Long planId;
    /** 泊位主键 */
    private Long hbId;
    /** 泊位名称 */
    private String hbName;
    /** 泊位名称 */
    private String operateBy;
    /** 是否结束（0否1是） */
    private String isOver;
    /** 卸货重量 */
    private BigDecimal totalUnloadWeight;
    /** 作业机具 */
    private String workEquipment;



}
