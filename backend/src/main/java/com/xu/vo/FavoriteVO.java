package com.xu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {

    private Long id;

    private Long jobId;

    private String jobTitle;

    private String companyName;

    private String companyLogo;

    private String category;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String salaryRange;

    private String experience;

    private String education;

    private String remark;

    private Integer jobStatus;

    private LocalDateTime createTime;

    private LocalDateTime jobUpdateTime;
}
