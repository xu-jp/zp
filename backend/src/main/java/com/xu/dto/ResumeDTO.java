package com.xu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ResumeDTO {

    private Long id;

    @NotBlank(message = "简历标题不能为空")
    private String title;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    private String location;

    private String education;

    private String school;

    private String major;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    private String workExperience;

    private String projectExperience;

    private String skills;

    private String selfIntroduction;

    private String attachments;

    private Integer isDefault;
}
