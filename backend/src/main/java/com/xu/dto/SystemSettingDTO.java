package com.xu.dto;

import lombok.Data;

@Data
public class SystemSettingDTO {

    private Long id;

    private String settingKey;

    private String settingValue;

    private String description;

    private Integer isPublic;
}
