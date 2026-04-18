package com.xu.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResumeScreenDTO {

    private Long applicationId;

    private List<String> skillKeywords;

    private Integer minExperience;

    private String minEducation;

    private String jobRequirements;
}
