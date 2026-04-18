package com.xu.service;

import com.xu.dto.CompanyDTO;
import com.xu.entity.Company;
import com.xu.vo.CompanyVO;

public interface CompanyManageService {

    CompanyVO getCompanyInfo(Long userId);

    void updateCompanyInfo(CompanyDTO dto, Long userId);

    void submitAudit(Long userId);

    Company getCompanyByUserId(Long userId);
}
