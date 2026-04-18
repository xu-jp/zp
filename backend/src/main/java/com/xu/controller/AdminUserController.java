package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.UserStatusDTO;
import com.xu.service.AdminUserService;
import com.xu.vo.AdminUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @RequireRole(3)
    public Result<Page<AdminUserVO>> getUserList(
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<AdminUserVO> page = adminUserService.getUserList(userType, status, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @RequireRole(3)
    public Result<AdminUserVO> getUserDetail(@PathVariable Long id) {
        AdminUserVO vo = adminUserService.getUserDetail(id);
        return Result.success(vo);
    }

    @PutMapping("/status")
    @RequireRole(3)
    public Result<Void> updateUserStatus(
            @RequestBody UserStatusDTO dto,
            @RequestAttribute Long userId) {
        adminUserService.updateUserStatus(dto, userId);
        return Result.success();
    }
}
