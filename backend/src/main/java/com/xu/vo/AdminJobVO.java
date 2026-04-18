package com.xu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminJobVO {

    private Long id;

    private Long companyId;

    private String companyName;

    private String title;

    private String category;

    private String description;

    private String requirement;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer experience;

    private String experienceText;

    private String education;

    private Integer status;

    private String statusText;

    private Integer auditStatus;

    private String auditStatusText;

    private String auditRemark;

    private String auditorName;

    private LocalDateTime auditTime;

    private Integer viewCount;

    private Integer applicationCount;

    private LocalDateTime createTime;
}
