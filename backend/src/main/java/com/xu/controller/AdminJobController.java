package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.JobAuditDTO;
import com.xu.service.AdminJobService;
import com.xu.vo.AdminJobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final AdminJobService adminJobService;

    @GetMapping
    @RequireRole(3)
    public Result<Page<AdminJobVO>> getJobList(
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<AdminJobVO> page = adminJobService.getJobList(auditStatus, status, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @RequireRole(3)
    public Result<AdminJobVO> getJobDetail(@PathVariable Long id) {
        AdminJobVO vo = adminJobService.getJobDetail(id);
        return Result.success(vo);
    }

    @PutMapping("/audit")
    @RequireRole(3)
    public Result<Void> auditJobs(
            @RequestBody JobAuditDTO dto,
            @RequestAttribute Long userId) {
        adminJobService.auditJobs(dto, userId);
        return Result.success();
    }

    @PutMapping("/offline/{id}")
    @RequireRole(3)
    public Result<Void> offlineJob(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        adminJobService.offlineJob(id, userId);
        return Result.success();
    }
}
