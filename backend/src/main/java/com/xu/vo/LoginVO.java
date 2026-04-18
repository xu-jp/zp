package com.xu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private Long userId;
    private String username;
    private String token;
    private Integer userType;
    private String avatar;
    private String realName;
}
