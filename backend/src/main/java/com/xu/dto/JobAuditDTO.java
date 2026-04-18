package com.xu.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobAuditDTO {

    private List<Long> jobIds;

    private Integer auditStatus;

    private String auditRemark;
}
