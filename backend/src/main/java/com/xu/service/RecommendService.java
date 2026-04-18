package com.xu.service;

import com.xu.vo.JobVO;

import java.util.List;

public interface RecommendService {

    List<JobVO> getRecommendJobs(Long userId, Integer limit);
}
