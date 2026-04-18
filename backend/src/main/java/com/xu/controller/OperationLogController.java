package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.service.OperationLogService;
import com.xu.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @RequireRole(3)
    public Result<Page<OperationLogVO>> getLogList(
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OperationLogVO> page = operationLogService.getLogList(operation, userId, startDate, endDate, pageNum, pageSize);
        return Result.success(page);
    }
}
