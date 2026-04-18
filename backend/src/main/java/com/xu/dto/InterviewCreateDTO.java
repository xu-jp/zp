package com.xu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewCreateDTO {

    private Long applicationId;

    private LocalDateTime interviewTime;

    private String location;

    private Integer interviewType;

    private String onlineLink;

    private String interviewerName;

    private String interviewerPhone;

    private String requirement;
}
