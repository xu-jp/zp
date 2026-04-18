package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.CompanyAuditDTO;
import com.xu.vo.AdminCompanyVO;

public interface AdminCompanyService {

    Page<AdminCompanyVO> getCompanyList(Integer auditStatus, String keyword, Integer pageNum, Integer pageSize);

    void auditCompany(CompanyAuditDTO dto, Long operatorId);

    AdminCompanyVO getCompanyDetail(Long id);

    void toggleRecruitPermission(Long companyId, Integer enabled);
}
