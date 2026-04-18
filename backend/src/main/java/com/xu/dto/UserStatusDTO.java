package com.xu.dto;

import lombok.Data;

@Data
public class UserStatusDTO {

    private Long userId;

    private Integer status;

    private String remark;
}
