package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xu.dto.CompanyDTO;
import com.xu.entity.Company;
import com.xu.entity.User;
import com.xu.common.BusinessException;
import com.xu.service.CompanyManageService;
import com.xu.service.CompanyService;
import com.xu.service.UserService;
import com.xu.vo.CompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CompanyManageServiceImpl implements CompanyManageService {

    private final CompanyService companyService;
    private final UserService userService;

    @Override
    public CompanyVO getCompanyInfo(Long userId) {
        Company company = getCompanyByUserId(userId);
        if (company == null) {
            return null;
        }
        return convertToVO(company);
    }

    @Override
    @Transactional
    public void updateCompanyInfo(CompanyDTO dto, Long userId) {
        Company company = getCompanyByUserId(userId);
        
        if (company == null) {
            company = new Company();
            company.setName(dto.getName());
            company.setLogo(dto.getLogo());
            company.setIndustry(dto.getIndustry());
            company.setScale(dto.getScale());
            company.setAddress(dto.getAddress());
            company.setDescription(dto.getDescription());
            company.setBusinessLicense(dto.getBusinessLicense());
            company.setLegalPerson(dto.getLegalPerson());
            company.setContactPhone(dto.getContactPhone());
            company.setContactEmail(dto.getContactEmail());
            company.setAuditStatus(0);
            companyService.save(company);

            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                        .set(User::getCompanyId, company.getId());
            userService.update(updateWrapper);
        } else {
            company.setName(dto.getName());
            company.setLogo(dto.getLogo());
            company.setIndustry(dto.getIndustry());
            company.setScale(dto.getScale());
            company.setAddress(dto.getAddress());
            company.setDescription(dto.getDescription());
            company.setBusinessLicense(dto.getBusinessLicense());
            company.setLegalPerson(dto.getLegalPerson());
            company.setContactPhone(dto.getContactPhone());
            company.setContactEmail(dto.getContactEmail());
            companyService.updateById(company);
        }
    }

    @Override
    @Transactional
    public void submitAudit(Long userId) {
        Company company = getCompanyByUserId(userId);
        if (company == null) {
            throw new BusinessException("请先填写企业信息");
        }

        if (company.getAuditStatus() == 1) {
            throw new BusinessException("企业已通过审核，无需重复提交");
        }

        if (company.getName() == null || company.getAddress() == null || 
            company.getContactPhone() == null || company.getBusinessLicense() == null) {
            throw new BusinessException("请完善企业必填信息后再提交审核");
        }

        company.setAuditStatus(0);
        companyService.updateById(company);
    }

    @Override
    public Company getCompanyByUserId(Long userId) {
        User user = userService.getById(userId);
        if (user == null || user.getCompanyId() == null) {
            return null;
        }
        return companyService.getById(user.getCompanyId());
    }

    private CompanyVO convertToVO(Company company) {
        CompanyVO vo = new CompanyVO();
        vo.setId(company.getId());
        vo.setName(company.getName());
        vo.setLogo(company.getLogo());
        vo.setIndustry(company.getIndustry());
        vo.setScale(company.getScale());
        vo.setAddress(company.getAddress());
        vo.setDescription(company.getDescription());
        vo.setBusinessLicense(company.getBusinessLicense());
        vo.setLegalPerson(company.getLegalPerson());
        vo.setContactPhone(company.getContactPhone());
        vo.setContactEmail(company.getContactEmail());
        vo.setAuditStatus(company.getAuditStatus());
        vo.setAuditStatusText(getAuditStatusText(company.getAuditStatus()));
        vo.setAuditRemark(company.getAuditRemark());
        vo.setAuditTime(company.getAuditTime());
        vo.setCreateTime(company.getCreateTime());
        vo.setUpdateTime(company.getUpdateTime());
        return vo;
    }

    private String getAuditStatusText(Integer status) {
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未知";
        }
    }
}
