package com.xu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;

    private String email;

    private String realName;

    private Integer userType;

    @NotBlank(message = "验证目标不能为空")
    private String target;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
