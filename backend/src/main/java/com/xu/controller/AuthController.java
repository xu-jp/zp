package com.xu.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.xu.common.BusinessException;
import com.xu.common.Result;
import com.xu.common.ResultCode;
import com.xu.dto.LoginDTO;
import com.xu.dto.RegisterDTO;
import com.xu.dto.ResetPasswordDTO;
import com.xu.dto.SendCodeDTO;
import com.xu.entity.User;
import com.xu.service.UserService;
import com.xu.service.VerificationCodeService;
import com.xu.util.JwtUtil;
import com.xu.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        LoginVO loginVO = new LoginVO(
                user.getId(),
                user.getUsername(),
                token,
                user.getUserType(),
                user.getAvatar(),
                user.getRealName()
        );

        log.info("用户登录成功: {}, 角色: {}", user.getUsername(), user.getUserType());

        return Result.success(loginVO);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User existUser = userService.getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_EXIST);
        }

        if (registerDTO.getPhone() != null && !registerDTO.getPhone().isEmpty()) {
            existUser = userService.getByPhone(registerDTO.getPhone());
            if (existUser != null) {
                throw new BusinessException(ResultCode.PHONE_EXIST);
            }
        }

        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            existUser = userService.getByEmail(registerDTO.getEmail());
            if (existUser != null) {
                throw new BusinessException(ResultCode.EMAIL_EXIST);
            }
        }

        if (!verificationCodeService.verifyCode(registerDTO.getTarget(), registerDTO.getCode(), 1)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRealName(registerDTO.getRealName());
        user.setUserType(registerDTO.getUserType() != null ? registerDTO.getUserType() : 1);
        user.setStatus(0);

        userService.save(user);

        log.info("用户注册成功: {}, 角色: {}", user.getUsername(), user.getUserType());

        return Result.success();
    }

    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDTO sendCodeDTO) {
        Integer type = sendCodeDTO.getType();
        String target = sendCodeDTO.getTarget();

        if (type == 1) {
            User existUser = userService.getByPhone(target);
            if (existUser != null) {
                throw new BusinessException(ResultCode.PHONE_EXIST);
            }
            existUser = userService.getByEmail(target);
            if (existUser != null) {
                throw new BusinessException(ResultCode.EMAIL_EXIST);
            }
        } else if (type == 2) {
            User existUser = userService.getByPhone(target);
            if (existUser == null) {
                existUser = userService.getByEmail(target);
            }
            if (existUser == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }
        }

        String code = verificationCodeService.generateCode(target, type);

        log.info("模拟发送验证码 - 目标: {}, 类型: {}, 验证码: {}", target, type == 1 ? "注册" : "找回密码", code);

        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        String target = resetPasswordDTO.getTarget();

        User user = userService.getByPhone(target);
        if (user == null) {
            user = userService.getByEmail(target);
        }
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!verificationCodeService.verifyCode(target, resetPasswordDTO.getCode(), 2)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        user.setPassword(BCrypt.hashpw(resetPasswordDTO.getPassword()));
        userService.updateById(user);

        log.info("用户密码重置成功: {}", user.getUsername());

        return Result.success();
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        User user = userService.getByUsername(username);
        return Result.success(user == null);
    }

    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(@RequestParam String phone) {
        User user = userService.getByPhone(phone);
        return Result.success(user == null);
    }

    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        User user = userService.getByEmail(email);
        return Result.success(user == null);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            log.info("用户退出登录: userId={}, username={}", userId, username);
        }
        return Result.success();
    }

}
