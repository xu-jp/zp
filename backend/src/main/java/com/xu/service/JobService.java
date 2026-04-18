package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.JobQueryDTO;
import com.xu.entity.Job;
import com.xu.vo.JobVO;

public interface JobService extends IService<Job> {

    Page<JobVO> queryJobList(JobQueryDTO queryDTO);

    JobVO getJobDetail(Long id);

    void incrementViewCount(Long id);
}
