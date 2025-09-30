package com.iwip.harbor.domain.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 坐标
 */
@Data
public class ScreenGeoJsonVo {

    /**
     * 船名
     */
    private String shipName;
    /**
     * 国际海事组织
     */
    private String imo;
    /**
     * 物资名称
     */
    private String materialName;
    /**
     * 吨重
     */
    private String tonnage;
    /**
     * 泊位
     */
    private String hbName;


    /**
     * 状态
     */
    private String status;

    /**
     * 经度
     */
    private String lat;

    /**
     * 维度
     */
    private String lon;

    /**
     * 船迹向
     */
    private String cog;
    /**
     * 船首向
     */
    private String hdg;
    /**
     * 船向率
     */
    private String rot;

    /**
     * 船类型
     */
    private String shipType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dockingTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

}
