package com.iwip.harbor.domain.vo;

import lombok.Data;

@Data
public class ScreenProgressVo {


    // 计划单主键
    private Long id;

    private String type;

    // 原因
    private String reason;

    // 泊位名称
    private String berthName;

    // 进度
    private String progress;
}
