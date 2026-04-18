package com.xu.dto;

import lombok.Data;

@Data
public class InterviewStatusDTO {

    private Long interviewId;

    private Integer status;

    private String feedback;
}
