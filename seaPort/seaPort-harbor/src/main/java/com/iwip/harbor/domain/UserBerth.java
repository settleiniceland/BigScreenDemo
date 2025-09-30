package com.iwip.harbor.domain;

import lombok.Data;

@Data
public class UserBerth {
    private Long id;
    private Long userId;
    private String userName;
    private String nickName;
    private String berthCode;
}
