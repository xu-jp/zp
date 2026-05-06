package com.xu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.InterviewStatusDTO;
import com.xu.entity.Interview;
import com.xu.vo.InterviewVO;

public interface InterviewService {

    Page<InterviewVO> getUserInterviews(Long userId, Integer status, Integer pageNum, Integer pageSize);

    InterviewVO getInterviewDetail(Long id, Long userId);

    void updateInterviewStatus(InterviewStatusDTO dto, Long userId);

    long count(LambdaQueryWrapper<Interview> wrapper);
}
