package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.ResumeScreenDTO;
import com.xu.entity.Company;
import com.xu.service.AIResumeScreenService;
import com.xu.service.CompanyManageService;
import com.xu.vo.ResumeScreenResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/ai")
@RequiredArgsConstructor
public class AIResumeScreenController {

    private final AIResumeScreenService aiResumeScreenService;
    private final CompanyManageService companyManageService;

    private Long getCompanyId(Long userId) {
        Company company = companyManageService.getCompanyByUserId(userId);
        return company != null ? company.getId() : null;
    }

    @PostMapping("/screen")
    @RequireRole(2)
    public Result<ResumeScreenResultVO> screenResume(
            @RequestBody ResumeScreenDTO dto,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        ResumeScreenResultVO result = aiResumeScreenService.screenResume(dto, companyId);
        return Result.success(result);
    }

    @PostMapping("/batch-screen/{jobId}")
    @RequireRole(2)
    public Result<Void> batchScreenResumes(
            @PathVariable Long jobId,
            @RequestBody ResumeScreenDTO dto,
            @RequestAttribute Long userId) {
        Long companyId = getCompanyId(userId);
        if (companyId == null) {
            return Result.error("企业信息不存在");
        }
        aiResumeScreenService.batchScreenResumes(jobId, dto, companyId);
        return Result.success();
    }
}
