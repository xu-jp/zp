package com.xu.service;

import com.xu.dto.ResumeScreenDTO;
import com.xu.vo.ResumeScreenResultVO;

public interface AIResumeScreenService {

    ResumeScreenResultVO screenResume(ResumeScreenDTO dto, Long companyId);

    void batchScreenResumes(Long jobId, ResumeScreenDTO dto, Long companyId);
}
