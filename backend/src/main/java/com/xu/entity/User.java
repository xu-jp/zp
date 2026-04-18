package com.xu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;

    private String password;

    private String phone;

    private String email;

    @TableField("real_name")
    private String realName;

    private String avatar;

    private Integer gender;

    private Integer age;

    @TableField("user_type")
    private Integer userType;

    private Integer status;

    @TableField("company_id")
    private Long companyId;

    private String remark;
}
