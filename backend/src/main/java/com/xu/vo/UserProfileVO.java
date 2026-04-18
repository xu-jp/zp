package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileVO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer gender;

    private Integer age;

    private Integer userType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
