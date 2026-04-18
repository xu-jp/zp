package com.xu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobPublishDTO {

    private Long id;

    private String title;

    private String category;

    private String description;

    private String requirement;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer experience;

    private String education;
}
