package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwip.harbor.domain.DockPlan;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScreenBerthInfoVo {



    private String berthCode;
    private String berthGeoJson;
    private String berthId;
    private String berthName;
    private String berthStatus;
    private String remark;

    // 到港时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    // 计划靠泊时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planDockingTime;

    // 到港时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dockingTime;

    /** 计划单集合 在装卸 */
    private List<ScreenPlanVo> planInfoList;
    /** 计划单集合 等泊、计划、在途、待离港 */
    private List<DockPlan> dockPlanList; // 等泊、计划、在途、待离港



}
