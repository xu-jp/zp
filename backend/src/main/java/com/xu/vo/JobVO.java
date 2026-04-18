package com.xu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobVO {

    private Long id;

    private Long companyId;

    private String companyName;

    private String companyLogo;

    private String companyIndustry;

    private String companyScale;

    private String title;

    private String category;

    private String description;

    private String requirement;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String salaryRange;

    private Integer experience;

    private String experienceName;

    private String education;

    private Integer status;

    private Integer auditStatus;

    private Integer viewCount;

    private Integer applicationCount;

    private LocalDateTime createTime;

    private Double matchScore;

    private String matchReason;
}
