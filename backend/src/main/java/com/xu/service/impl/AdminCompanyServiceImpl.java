package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BusinessException;
import com.xu.dto.CompanyAuditDTO;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.entity.User;
import com.xu.mapper.CompanyMapper;
import com.xu.mapper.JobMapper;
import com.xu.mapper.UserMapper;
import com.xu.service.AdminCompanyService;
import com.xu.service.OperationLogService;
import com.xu.vo.AdminCompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompanyServiceImpl implements AdminCompanyService {

    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;
    private final OperationLogService operationLogService;

    @Override
    public Page<AdminCompanyVO> getCompanyList(Integer auditStatus, String keyword, Integer pageNum, Integer pageSize) {
        Page<Company> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getDeleted, 0);

        if (auditStatus != null) {
            wrapper.eq(Company::getAuditStatus, auditStatus);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Company::getName, keyword);
        }
        wrapper.orderByDesc(Company::getCreateTime);

        Page<Company> companyPage = companyMapper.selectPage(page, wrapper);

        Page<AdminCompanyVO> voPage = new Page<>(companyPage.getCurrent(), companyPage.getSize(), companyPage.getTotal());
        List<AdminCompanyVO> voList = companyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void auditCompany(CompanyAuditDTO dto, Long operatorId) {
        Company company = companyMapper.selectById(dto.getCompanyId());
        if (company == null) {
            throw new BusinessException("企业不存在");
        }

        if (company.getAuditStatus() == 1) {
            throw new BusinessException("该企业已审核通过，无法重复审核");
        }

        LambdaUpdateWrapper<Company> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Company::getId, dto.getCompanyId())
                .set(Company::getAuditStatus, dto.getAuditStatus())
                .set(Company::getAuditRemark, dto.getAuditRemark())
                .set(Company::getAuditBy, operatorId)
                .set(Company::getAuditTime, LocalDateTime.now());
        companyMapper.update(null, updateWrapper);

        User operator = userMapper.selectById(operatorId);
        String operation = dto.getAuditStatus() == 1 ? "审核通过企业" : "审核拒绝企业";
        operationLogService.log(operatorId, operator != null ? operator.getUsername() : null,
                operation, "auditCompany", "companyId=" + dto.getCompanyId() + ",status=" + dto.getAuditStatus(),
                null, 0, null);
    }

    @Override
    public AdminCompanyVO getCompanyDetail(Long id) {
        Company company = companyMapper.selectById(id);
        if (company == null) {
            throw new BusinessException("企业不存在");
        }
        return convertToVO(company);
    }

    @Override
    @Transactional
    public void toggleRecruitPermission(Long companyId, Integer enabled) {
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new BusinessException("企业不存在");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getCompanyId, companyId)
                .eq(User::getUserType, 2)
                .set(User::getStatus, enabled == 1 ? 0 : 1);
        userMapper.update(null, updateWrapper);
    }

    private AdminCompanyVO convertToVO(Company company) {
        AdminCompanyVO vo = new AdminCompanyVO();
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

        if (company.getAuditBy() != null) {
            User auditor = userMapper.selectById(company.getAuditBy());
            if (auditor != null) {
                vo.setAuditorName(auditor.getRealName());
            }
        }

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getCompanyId, company.getId())
                .eq(User::getUserType, 2)
                .eq(User::getDeleted, 0);
        vo.setRecruiterCount(Math.toIntExact(userMapper.selectCount(userWrapper)));

        LambdaQueryWrapper<Job> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(Job::getCompanyId, company.getId())
                .eq(Job::getDeleted, 0);
        vo.setJobCount(Math.toIntExact(jobMapper.selectCount(jobWrapper)));

        return vo;
    }

    private String getAuditStatusText(Integer auditStatus) {
        switch (auditStatus) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未知";
        }
    }
}
