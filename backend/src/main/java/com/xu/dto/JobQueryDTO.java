package com.xu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobQueryDTO {

    private String keyword;

    private String industry;

    private String category;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String location;

    private Integer experience;

    private String education;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
