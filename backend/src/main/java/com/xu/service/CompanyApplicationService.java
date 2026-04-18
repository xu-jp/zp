package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.ApplicationHandleDTO;
import com.xu.dto.InterviewCreateDTO;
import com.xu.vo.CompanyApplicationVO;
import com.xu.vo.CompanyStatisticsVO;

public interface CompanyApplicationService {

    Page<CompanyApplicationVO> getApplicationList(Long companyId, Integer status, Integer pageNum, Integer pageSize);

    CompanyApplicationVO getApplicationDetail(Long id, Long companyId);

    void handleApplication(ApplicationHandleDTO dto, Long companyId);

    void createInterview(InterviewCreateDTO dto, Long companyId);

    CompanyStatisticsVO getStatistics(Long companyId);
}
