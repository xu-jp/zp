package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.ChangePasswordDTO;
import com.xu.dto.RecruiterProfileDTO;
import com.xu.service.RecruiterProfileService;
import com.xu.vo.RecruiterProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @GetMapping("/profile")
    @RequireRole(2)
    public Result<RecruiterProfileVO> getProfile(@RequestAttribute Long userId) {
        RecruiterProfileVO profile = recruiterProfileService.getProfile(userId);
        return Result.success(profile);
    }

    @PutMapping("/profile")
    @RequireRole(2)
    public Result<Void> updateProfile(
            @RequestBody RecruiterProfileDTO dto,
            @RequestAttribute Long userId) {
        recruiterProfileService.updateProfile(dto, userId);
        return Result.success();
    }

    @PutMapping("/password")
    @RequireRole(2)
    public Result<Void> changePassword(
            @RequestBody ChangePasswordDTO dto,
            @RequestAttribute Long userId) {
        recruiterProfileService.changePassword(dto, userId);
        return Result.success();
    }

    @PutMapping("/bind-phone")
    @RequireRole(2)
    public Result<Void> bindPhone(
            @RequestParam String phone,
            @RequestAttribute Long userId) {
        recruiterProfileService.bindPhone(phone, userId);
        return Result.success();
    }

    @PutMapping("/bind-email")
    @RequireRole(2)
    public Result<Void> bindEmail(
            @RequestParam String email,
            @RequestAttribute Long userId) {
        recruiterProfileService.bindEmail(email, userId);
        return Result.success();
    }
}
