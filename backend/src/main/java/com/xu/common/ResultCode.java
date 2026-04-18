package com.xu.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "账号已被禁用"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),
    USER_EXIST(1006, "用户已存在"),
    PHONE_EXIST(1007, "手机号已注册"),
    EMAIL_EXIST(1008, "邮箱已注册"),
    COMPANY_NOT_FOUND(2001, "企业不存在"),
    COMPANY_AUDITING(2002, "企业正在审核中"),
    COMPANY_REJECTED(2003, "企业审核未通过"),
    JOB_NOT_FOUND(3001, "职位不存在"),
    JOB_OFFLINE(3002, "职位已下架"),
    RESUME_NOT_FOUND(4001, "简历不存在"),
    APPLICATION_EXIST(5001, "已投递过该职位"),
    APPLICATION_NOT_FOUND(5002, "投递记录不存在"),
    INTERVIEW_NOT_FOUND(6001, "面试记录不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
