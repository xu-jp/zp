package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.CompanyDTO;
import com.xu.entity.Company;
import com.xu.service.CompanyManageService;
import com.xu.vo.CompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyManageController {

    private final CompanyManageService companyManageService;

    @GetMapping("/info")
    @RequireRole(2)
    public Result<CompanyVO> getCompanyInfo(@RequestAttribute Long userId) {
        CompanyVO vo = companyManageService.getCompanyInfo(userId);
        return Result.success(vo);
    }

    @PostMapping("/save")
    @RequireRole(2)
    public Result<Void> saveCompanyInfo(@RequestBody CompanyDTO dto, @RequestAttribute Long userId) {
        companyManageService.updateCompanyInfo(dto, userId);
        return Result.success();
    }

    @PostMapping("/submit-audit")
    @RequireRole(2)
    public Result<Void> submitAudit(@RequestAttribute Long userId) {
        companyManageService.submitAudit(userId);
        return Result.success();
    }
}
