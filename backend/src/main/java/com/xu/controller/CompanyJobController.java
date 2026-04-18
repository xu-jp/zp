package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.JobPublishDTO;
import com.xu.entity.Company;
import com.xu.service.CompanyJobService;
import com.xu.service.CompanyManageService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/job")
@RequiredArgsConstructor
public class CompanyJobController {

    private final CompanyJobService companyJobService;
    private final CompanyManageService companyManageService;

    private Long getCompanyId(Long userId) {
        Company company = companyManageService.getCompanyByUserId(userId);
        return company != null ? company.getId() : null;
    }

    @PostMapping("/publish")
    @RequireRole(2)
    public Result<Void> publishJob(@RequestBody JobPublishDTO dto, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("请先完善企业信息");
        }
        companyJobService.publishJob(dto, companyId);
        return Result.success();
    }

    @PutMapping("/update")
    @RequireRole(2)
    public Result<Void> updateJob(@RequestBody JobPublishDTO dto, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyJobService.updateJob(dto, companyId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole(2)
    public Result<Void> deleteJob(@PathVariable Long id, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyJobService.deleteJob(id, companyId);
        return Result.success();
    }

    @PutMapping("/toggle/{id}")
    @RequireRole(2)
    public Result<Void> toggleJobStatus(@PathVariable Long id, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyJobService.toggleJobStatus(id, companyId);
        return Result.success();
    }

    @GetMapping("/list")
    @RequireRole(2)
    public Result<Page<JobVO>> getCompanyJobs(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        Page<JobVO> page = companyJobService.getCompanyJobs(companyId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    @RequireRole(2)
    public Result<JobVO> getJobDetail(@PathVariable Long id, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        JobVO vo = companyJobService.getJobDetail(id, companyId);
        return Result.success(vo);
    }
}
