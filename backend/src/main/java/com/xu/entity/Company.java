package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_company")
public class Company extends BaseEntity {

    private String name;

    private String logo;

    private String industry;

    private String scale;

    private String address;

    private String description;

    private String businessLicense;

    private String legalPerson;

    private String contactPhone;

    private String contactEmail;

    private Integer auditStatus;

    private String auditRemark;

    private Long auditBy;

    private LocalDateTime auditTime;
}
