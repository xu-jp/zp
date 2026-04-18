package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.ApplicationHandleDTO;
import com.xu.dto.InterviewCreateDTO;
import com.xu.entity.Company;
import com.xu.service.CompanyApplicationService;
import com.xu.service.CompanyManageService;
import com.xu.vo.CompanyApplicationVO;
import com.xu.vo.CompanyStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/application")
@RequiredArgsConstructor
public class CompanyApplicationController {

    private final CompanyApplicationService companyApplicationService;
    private final CompanyManageService companyManageService;

    private Long getCompanyId(Long userId) {
        Company company = companyManageService.getCompanyByUserId(userId);
        return company != null ? company.getId() : null;
    }

    @GetMapping("/list")
    @RequireRole(2)
    public Result<Page<CompanyApplicationVO>> getApplicationList(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        Page<CompanyApplicationVO> page = companyApplicationService.getApplicationList(companyId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    @RequireRole(2)
    public Result<CompanyApplicationVO> getApplicationDetail(@PathVariable Long id, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        CompanyApplicationVO vo = companyApplicationService.getApplicationDetail(id, companyId);
        return Result.success(vo);
    }

    @PutMapping("/handle")
    @RequireRole(2)
    public Result<Void> handleApplication(@RequestBody ApplicationHandleDTO dto, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyApplicationService.handleApplication(dto, companyId);
        return Result.success();
    }

    @PostMapping("/interview")
    @RequireRole(2)
    public Result<Void> createInterview(@RequestBody InterviewCreateDTO dto, @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        companyApplicationService.createInterview(dto, companyId);
        return Result.success();
    }

    @GetMapping("/statistics")
    @RequireRole(2)
    public Result<CompanyStatisticsVO> getStatistics(@RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        CompanyStatisticsVO vo = companyApplicationService.getStatistics(companyId);
        return Result.success(vo);
    }
}
