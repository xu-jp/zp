package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.JobPublishDTO;
import com.xu.vo.JobVO;

public interface CompanyJobService {

    void publishJob(JobPublishDTO dto, Long companyId);

    void updateJob(JobPublishDTO dto, Long companyId);

    void deleteJob(Long id, Long companyId);

    void toggleJobStatus(Long id, Long companyId);

    Page<JobVO> getCompanyJobs(Long companyId, Integer status, Integer pageNum, Integer pageSize);

    JobVO getJobDetail(Long id, Long companyId);
}
