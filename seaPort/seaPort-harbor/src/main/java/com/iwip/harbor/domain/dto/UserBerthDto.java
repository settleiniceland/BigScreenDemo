package com.iwip.harbor.domain.dto;

import com.iwip.harbor.domain.UserBerth;
import lombok.Data;
import java.util.List;

@Data
public class UserBerthDto {
    private String berthCode;
    private List<UserBerth> userBerth;
}
