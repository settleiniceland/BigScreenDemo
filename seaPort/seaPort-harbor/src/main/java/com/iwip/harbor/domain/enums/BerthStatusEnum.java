package com.iwip.harbor.domain.enums;

import java.util.Objects;

/**
 * 泊位状态枚举
 *
 * @author taoqz
 * @create 2025-04-11
 */
public enum BerthStatusEnum {

    FREE("0", "空闲"),
    OCCUPIED("1", "占用中"),
    MAINTENANCE("2", "维护"),
    PAUSED("3", "暂停");

    private final String code;
    private final String label;

    BerthStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    // 根据 code 获取枚举
    public static BerthStatusEnum fromCode(String code) {
        for (BerthStatusEnum status : values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null;
    }
}
