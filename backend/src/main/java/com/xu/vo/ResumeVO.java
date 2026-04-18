package com.xu.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ResumeVO {

    private Long id;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
