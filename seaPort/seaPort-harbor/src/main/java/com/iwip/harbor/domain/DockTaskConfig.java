package com.iwip.harbor.domain;

import com.iwip.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class DockTaskConfig extends BaseEntity {
    private Long id;
    private String taskName;
    private String hbName;
    private String targetUrl;
    private Long frequencyMs;
    private Boolean enabled;
    private String remark;
    private String remark1;
    private String remark2;
    private String remark3;
}
