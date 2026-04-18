package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.service.DashboardService;
import com.xu.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表盘
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @RequireRole(3)
    public Result<DashboardVO> getDashboardData() {
        DashboardVO vo = dashboardService.getDashboardData();
        return Result.success(vo);
    }

    @GetMapping("/range")
    @RequireRole(3)
    public Result<DashboardVO> getDashboardDataByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        DashboardVO vo = dashboardService.getDashboardDataByDateRange(startDate, endDate);
        return Result.success(vo);
    }
}
