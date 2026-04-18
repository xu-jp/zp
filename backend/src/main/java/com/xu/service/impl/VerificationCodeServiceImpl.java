package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.entity.VerificationCode;
import com.xu.mapper.VerificationCodeMapper;
import com.xu.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements VerificationCodeService {

    @Override
    public String generateCode(String target, Integer type) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        verificationCode.setUsed(0);

        save(verificationCode);

        log.info("验证码已生成 - 目标: {}, 类型: {}, 验证码: {}", target, type, code);

        return code;
    }

    @Override
    public boolean verifyCode(String target, String code, Integer type) {
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerificationCode::getTarget, target)
                .eq(VerificationCode::getCode, code)
                .eq(VerificationCode::getType, type)
                .eq(VerificationCode::getUsed, 0)
                .gt(VerificationCode::getExpireTime, LocalDateTime.now());

        VerificationCode verificationCode = getOne(wrapper);
        if (verificationCode == null) {
            return false;
        }

        verificationCode.setUsed(1);
        updateById(verificationCode);

        return true;
    }
}
