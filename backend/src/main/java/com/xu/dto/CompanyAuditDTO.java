package com.xu.dto;

import lombok.Data;

@Data
public class CompanyAuditDTO {

    private Long companyId;

    private Integer auditStatus;

    private String auditRemark;
}
