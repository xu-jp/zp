package com.xu.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationVO {

    private Long id;

    private Long jobId;

    private String jobTitle;

    private String companyName;

    private String companyLogo;

    private Long resumeId;

    private String resumeTitle;

    private Integer status;

    private String statusText;

    private String remark;

    private LocalDateTime viewTime;

    private LocalDateTime feedbackTime;

    private LocalDateTime createTime;

    private String location;

    private String salaryRange;
}
