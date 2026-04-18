package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyVO {

    private Long id;

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

    private String auditStatusText;

    private String auditRemark;

    private LocalDateTime auditTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
