package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {

    private Long id;

    private String username;

    private String phone;

    private String email;

    private String realName;

    private String avatar;

    private Integer userType;

    private String userTypeText;

    private Integer status;

    private String statusText;

    private Long companyId;

    private String companyName;

    private String remark;

    private LocalDateTime createTime;
}
