package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_application")
public class Application extends BaseEntity {

    private Long userId;

    private Long jobId;

    private Long resumeId;

    private Integer status;

    private String remark;

    private LocalDateTime viewTime;

    private LocalDateTime feedbackTime;
}
