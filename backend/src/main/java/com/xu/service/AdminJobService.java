package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.JobAuditDTO;
import com.xu.vo.AdminJobVO;

public interface AdminJobService {

    Page<AdminJobVO> getJobList(Integer auditStatus, Integer status, String keyword, Integer pageNum, Integer pageSize);

    void auditJobs(JobAuditDTO dto, Long operatorId);

    void offlineJob(Long jobId, Long operatorId);

    AdminJobVO getJobDetail(Long id);
}
