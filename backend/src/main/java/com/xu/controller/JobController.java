package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.JobQueryDTO;
import com.xu.service.JobService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/list")
    public Result<Page<JobVO>> queryJobList(JobQueryDTO queryDTO) {
        Page<JobVO> page = jobService.queryJobList(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<JobVO> getJobDetail(@PathVariable Long id) {
        jobService.incrementViewCount(id);
        JobVO job = jobService.getJobDetail(id);
        return Result.success(job);
    }
}
