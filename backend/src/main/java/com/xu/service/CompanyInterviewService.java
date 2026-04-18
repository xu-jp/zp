package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.CompanyInterviewResultDTO;
import com.xu.dto.CompanyInterviewUpdateDTO;
import com.xu.vo.CompanyInterviewVO;

public interface CompanyInterviewService {

    Page<CompanyInterviewVO> getCompanyInterviews(Long companyId, Integer status, Integer pageNum, Integer pageSize);

    CompanyInterviewVO getInterviewDetail(Long id, Long companyId);

    void updateInterview(CompanyInterviewUpdateDTO dto, Long companyId);

    void setInterviewResult(CompanyInterviewResultDTO dto, Long companyId);

    void completeInterview(Long id, Long companyId);

    void cancelInterview(Long id, Long companyId);
}
