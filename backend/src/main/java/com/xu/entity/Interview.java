package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_interview")
public class Interview extends BaseEntity {

    private Long applicationId;

    private Long userId;

    private Long jobId;

    private Long companyId;

    private LocalDateTime interviewTime;

    private String location;

    private Integer interviewType;

    private String onlineLink;

    private String interviewerName;

    private String interviewerPhone;

    private String requirement;

    private Integer status;

    private String result;

    private String feedback;
}
