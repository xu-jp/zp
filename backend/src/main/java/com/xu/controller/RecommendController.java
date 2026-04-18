package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.service.RecommendService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/jobs")
    @RequireRole(1)
    public Result<List<JobVO>> getRecommendJobs(
            @RequestAttribute Long userId,
            @RequestParam(defaultValue = "5") Integer limit) {
        List<JobVO> jobs = recommendService.getRecommendJobs(userId, limit);
        return Result.success(jobs);
    }
}
