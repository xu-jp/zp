package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.ChangePasswordDTO;
import com.xu.dto.UserProfileDTO;
import com.xu.service.UserProfileService;
import com.xu.vo.UserProfileVO;
import com.xu.vo.UserStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    @RequireRole(1)
    public Result<UserProfileVO> getProfile(@RequestAttribute Long userId) {
        UserProfileVO profile = userProfileService.getProfile(userId);
        return Result.success(profile);
    }

    @PutMapping("/profile")
    @RequireRole(1)
    public Result<Void> updateProfile(
            @RequestBody UserProfileDTO dto,
            @RequestAttribute Long userId) {
        userProfileService.updateProfile(dto, userId);
        return Result.success();
    }

    @PutMapping("/password")
    @RequireRole(1)
    public Result<Void> changePassword(
            @RequestBody ChangePasswordDTO dto,
            @RequestAttribute Long userId) {
        userProfileService.changePassword(dto, userId);
        return Result.success();
    }

    @PutMapping("/bind-phone")
    @RequireRole(1)
    public Result<Void> bindPhone(
            @RequestParam String phone,
            @RequestAttribute Long userId) {
        userProfileService.bindPhone(phone, userId);
        return Result.success();
    }

    @PutMapping("/bind-email")
    @RequireRole(1)
    public Result<Void> bindEmail(
            @RequestParam String email,
            @RequestAttribute Long userId) {
        userProfileService.bindEmail(email, userId);
        return Result.success();
    }

    @GetMapping("/stats")
    @RequireRole(1)
    public Result<UserStatsVO> getUserStats(@RequestAttribute Long userId) {
        UserStatsVO stats = userProfileService.getUserStats(userId);
        return Result.success(stats);
    }
}
