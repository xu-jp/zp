package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resume")
public class Resume extends BaseEntity {

    private Long userId;

    private String title;

    private String realName;

    private String gender;

    private LocalDate birthday;

    private String phone;

    private String email;

    private String location;

    private String education;

    private String school;

    private String major;

    private LocalDate graduationDate;

    private String workExperience;

    private String projectExperience;

    private String skills;

    private String selfIntroduction;

    private String attachments;

    private Integer isDefault;
}
