package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_verification_code")
public class VerificationCode extends BaseEntity {

    private String target;

    private String code;

    private Integer type;

    private LocalDateTime expireTime;

    private Integer used;
}
