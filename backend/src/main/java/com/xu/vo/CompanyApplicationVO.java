package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyApplicationVO {

    private Long id;

    private Long userId;

    private String userName;

    private String userPhone;

    private String userEmail;

    private Long jobId;

    private String jobTitle;

    private Long resumeId;

    private String resumeTitle;

    private String resumeRealName;

    private String resumeEducation;

    private String resumeSchool;

    private String resumeSkills;

    private String resumeWorkExperience;

    private String resumeSelfIntroduction;

    private Integer status;

    private String statusText;

    private String remark;

    private LocalDateTime viewTime;

    private LocalDateTime feedbackTime;

    private LocalDateTime createTime;
}
