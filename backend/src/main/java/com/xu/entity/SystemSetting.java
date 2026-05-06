package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_setting")
public class SystemSetting extends BaseEntity {

    private String settingKey;

    private String settingValue;

    private String description;

    private Integer isPublic;
}
