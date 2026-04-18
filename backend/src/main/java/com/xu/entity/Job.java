package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
public class Job extends BaseEntity {

    private Long companyId;

    private String title;

    private String category;

    private String description;

    private String requirement;

    private String location;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer experience;

    private String education;

    private Integer status;

    private Integer auditStatus;

    private String auditRemark;

    private Long auditBy;

    private LocalDateTime auditTime;

    private Integer viewCount;

    private Integer applicationCount;
}
