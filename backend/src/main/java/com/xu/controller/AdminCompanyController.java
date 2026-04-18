package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.CompanyAuditDTO;
import com.xu.service.AdminCompanyService;
import com.xu.vo.AdminCompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    @GetMapping
    @RequireRole(3)
    public Result<Page<AdminCompanyVO>> getCompanyList(
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<AdminCompanyVO> page = adminCompanyService.getCompanyList(auditStatus, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @RequireRole(3)
    public Result<AdminCompanyVO> getCompanyDetail(@PathVariable Long id) {
        AdminCompanyVO vo = adminCompanyService.getCompanyDetail(id);
        return Result.success(vo);
    }

    @PutMapping("/audit")
    @RequireRole(3)
    public Result<Void> auditCompany(
            @RequestBody CompanyAuditDTO dto,
            @RequestAttribute Long userId) {
        adminCompanyService.auditCompany(dto, userId);
        return Result.success();
    }

    @PutMapping("/permission/{companyId}")
    @RequireRole(3)
    public Result<Void> toggleRecruitPermission(
            @PathVariable Long companyId,
            @RequestParam Integer enabled) {
        adminCompanyService.toggleRecruitPermission(companyId, enabled);
        return Result.success();
    }
}
