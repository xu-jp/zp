package com.xu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.entity.VerificationCode;

public interface VerificationCodeService extends IService<VerificationCode> {
    
    String generateCode(String target, Integer type);

    boolean verifyCode(String target, String code, Integer type);
}
