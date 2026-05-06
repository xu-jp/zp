package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemSettingVO {

    private Long id;

    private String settingKey;

    private String settingValue;

    private String description;

    private Integer isPublic;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
