package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyInterviewVO {

    private Long id;

    private Long applicationId;

    private Long userId;

    private Long jobId;

    private String candidateName;

    private String candidatePhone;

    private String candidateEmail;

    private String jobTitle;

    private LocalDateTime interviewTime;

    private String location;

    private Integer interviewType;

    private String onlineLink;

    private String interviewerName;

    private String interviewerPhone;

    private String requirement;

    private Integer status;

    private String statusText;

    private String result;

    private LocalDateTime createTime;
}
