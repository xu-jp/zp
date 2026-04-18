package com.xu.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResumeScreenResultVO {

    private Long applicationId;

    private String candidateName;

    private String matchLevel;

    private Integer matchScore;

    private List<String> highlights;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private String experienceMatch;

    private String educationMatch;

    private String summary;

    private String recommendation;
}
