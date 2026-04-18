package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.CompanyInterviewResultDTO;
import com.xu.dto.CompanyInterviewUpdateDTO;
import com.xu.entity.Company;
import com.xu.service.CompanyInterviewService;
import com.xu.service.CompanyManageService;
import com.xu.vo.CompanyInterviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/interviews")
@RequiredArgsConstructor
public class CompanyInterviewController {

    private final CompanyInterviewService companyInterviewService;
    private final CompanyManageService companyManageService;

    private Long getCompanyId(Long userId) {
        Company company = companyManageService.getCompanyByUserId(userId);
        return company != null ? company.getId() : null;
    }

    @GetMapping
    @RequireRole(2)
    public Result<Page<CompanyInterviewVO>> getInterviewList(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        Page<CompanyInterviewVO> page = companyInterviewService.getCompanyInterviews(companyId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @RequireRole(2)
    public Result<CompanyInterviewVO> getInterviewDetail(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        CompanyInterviewVO vo = companyInterviewService.getInterviewDetail(id, companyId);
        return Result.success(vo);
    }

    @PutMapping
    @RequireRole(2)
    public Result<Void> updateInterview(
            @RequestBody CompanyInterviewUpdateDTO dto,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyInterviewService.updateInterview(dto, companyId);
        return Result.success();
    }

    @PutMapping("/result")
    @RequireRole(2)
    public Result<Void> setInterviewResult(
            @RequestBody CompanyInterviewResultDTO dto,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyInterviewService.setInterviewResult(dto, companyId);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @RequireRole(2)
    public Result<Void> completeInterview(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyInterviewService.completeInterview(id, companyId);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @RequireRole(2)
    public Result<Void> cancelInterview(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyInterviewService.cancelInterview(id, companyId);
        return Result.success();
    }
}
