package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.dto.ApplicationDTO;
import com.xu.common.Result;
import com.xu.service.ApplicationService;
import com.xu.vo.ApplicationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    @RequireRole(1)
    public Result<Void> apply(@RequestBody ApplicationDTO dto, @RequestAttribute Long userId) {
        applicationService.apply(dto, userId);
        return Result.success();
    }

    @GetMapping("/list")
    @RequireRole(1)
    public Result<Page<ApplicationVO>> getUserApplications(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ApplicationVO> page = applicationService.getUserApplications(userId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    @RequireRole(1)
    public Result<ApplicationVO> getApplicationDetail(@PathVariable Long id, @RequestAttribute Long userId) {
        ApplicationVO vo = applicationService.getApplicationDetail(id, userId);
        return Result.success(vo);
    }

    @DeleteMapping("/cancel/{id}")
    @RequireRole(1)
    public Result<Void> cancelApplication(@PathVariable Long id, @RequestAttribute Long userId) {
        applicationService.cancelApplication(id, userId);
        return Result.success();
    }

    @GetMapping("/check/{jobId}")
    @RequireRole(1)
    public Result<Boolean> checkApplied(@PathVariable Long jobId, @RequestAttribute Long userId) {
        boolean applied = applicationService.checkApplied(jobId, userId);
        return Result.success(applied);
    }
}
