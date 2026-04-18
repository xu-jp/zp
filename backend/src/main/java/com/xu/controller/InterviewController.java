package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.annotation.RequireRole;
import com.xu.dto.InterviewStatusDTO;
import com.xu.common.Result;
import com.xu.service.InterviewService;
import com.xu.vo.InterviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/list")
    @RequireRole(1)
    public Result<Page<InterviewVO>> getUserInterviews(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<InterviewVO> page = interviewService.getUserInterviews(userId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    @RequireRole(1)
    public Result<InterviewVO> getInterviewDetail(@PathVariable Long id, @RequestAttribute Long userId) {
        InterviewVO vo = interviewService.getInterviewDetail(id, userId);
        return Result.success(vo);
    }

    @PutMapping("/status")
    @RequireRole(1)
    public Result<Void> updateInterviewStatus(@RequestBody InterviewStatusDTO dto, @RequestAttribute Long userId) {
        interviewService.updateInterviewStatus(dto, userId);
        return Result.success();
    }
}
