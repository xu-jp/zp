package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewVO {

    private Long id;

    private Long applicationId;

    private Long jobId;

    private String jobTitle;

    private Long companyId;

    private String companyName;

    private String companyLogo;

    private LocalDateTime interviewTime;

    private String location;

    private Integer interviewType;

    private String interviewTypeText;

    private String onlineLink;

    private String interviewerName;

    private String interviewerPhone;

    private String requirement;

    private Integer status;

    private String statusText;

    private String result;

    private String feedback;

    private LocalDateTime createTime;
}
