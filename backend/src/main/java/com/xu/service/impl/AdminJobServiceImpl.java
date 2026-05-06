package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BusinessException;
import com.xu.dto.JobAuditDTO;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.entity.User;
import com.xu.mapper.CompanyMapper;
import com.xu.mapper.JobMapper;
import com.xu.mapper.UserMapper;
import com.xu.service.AdminJobService;
import com.xu.service.OperationLogService;
import com.xu.vo.AdminJobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminJobServiceImpl implements AdminJobService {

    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;

    @Override
    public Page<AdminJobVO> getJobList(Integer auditStatus, Integer status, String keyword, Integer pageNum, Integer pageSize) {
        Page<Job> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getDeleted, 0);

        if (auditStatus != null) {
            wrapper.eq(Job::getAuditStatus, auditStatus);
        }
        if (status != null) {
            wrapper.eq(Job::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Job::getTitle, keyword);
        }
        wrapper.orderByDesc(Job::getCreateTime);

        Page<Job> jobPage = jobMapper.selectPage(page, wrapper);

        List<Long> companyIds = jobPage.getRecords().stream()
                .map(Job::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = companyIds.isEmpty() ? Map.of() :
                companyMapper.selectBatchIds(companyIds).stream()
                        .collect(Collectors.toMap(Company::getId, c -> c));

        Page<AdminJobVO> voPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        List<AdminJobVO> voList = jobPage.getRecords().stream()
                .map(job -> convertToVO(job, companyMap.get(job.getCompanyId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void auditJobs(JobAuditDTO dto, Long operatorId) {
        if (dto.getJobIds() == null || dto.getJobIds().isEmpty()) {
            throw new BusinessException("请选择要审核的职位");
        }

        LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Job::getId, dto.getJobIds())
                .set(Job::getAuditStatus, dto.getAuditStatus())
                .set(Job::getAuditRemark, dto.getAuditRemark())
                .set(Job::getAuditBy, operatorId)
                .set(Job::getAuditTime, LocalDateTime.now());
        jobMapper.update(null, updateWrapper);

        User operator = userMapper.selectById(operatorId);
        String operation = dto.getAuditStatus() == 1 ? "批量审核通过职位" : "批量审核拒绝职位";
        operationLogService.log(operatorId, operator != null ? operator.getUsername() : null,
                operation, "auditJobs", "jobIds=" + dto.getJobIds() + ",status=" + dto.getAuditStatus(),
                0, null);
    }

    @Override
    @Transactional
    public void offlineJob(Long jobId, Long operatorId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }

        LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Job::getId, jobId)
                .set(Job::getStatus, 0);
        jobMapper.update(null, updateWrapper);

        User operator = userMapper.selectById(operatorId);
        operationLogService.log(operatorId, operator != null ? operator.getUsername() : null,
                "违规下架职位", "offlineJob", "jobId=" + jobId, 0, null);
    }

    @Override
    public AdminJobVO getJobDetail(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }

        Company company = companyMapper.selectById(job.getCompanyId());
        return convertToVO(job, company);
    }

    private AdminJobVO convertToVO(Job job, Company company) {
        AdminJobVO vo = new AdminJobVO();
        vo.setId(job.getId());
        vo.setCompanyId(job.getCompanyId());
        vo.setTitle(job.getTitle());
        vo.setCategory(job.getCategory());
        vo.setDescription(job.getDescription());
        vo.setRequirement(job.getRequirement());
        vo.setLocation(job.getLocation());
        vo.setSalaryMin(job.getSalaryMin());
        vo.setSalaryMax(job.getSalaryMax());
        vo.setExperience(job.getExperience());
        vo.setExperienceText(getExperienceText(job.getExperience()));
        vo.setEducation(job.getEducation());
        vo.setStatus(job.getStatus());
        vo.setStatusText(job.getStatus() == 0 ? "已下架" : "已上架");
        vo.setAuditStatus(job.getAuditStatus());
        vo.setAuditStatusText(getAuditStatusText(job.getAuditStatus()));
        vo.setAuditRemark(job.getAuditRemark());
        vo.setAuditTime(job.getAuditTime());
        vo.setViewCount(job.getViewCount());
        vo.setApplicationCount(job.getApplicationCount());
        vo.setCreateTime(job.getCreateTime());

        if (company != null) {
            vo.setCompanyName(company.getName());
        }

        if (job.getAuditBy() != null) {
            User auditor = userMapper.selectById(job.getAuditBy());
            if (auditor != null) {
                vo.setAuditorName(auditor.getRealName());
            }
        }

        return vo;
    }

    private String getExperienceText(Integer experience) {
        if (experience == null) return "不限";
        switch (experience) {
            case 0: return "不限";
            case 1: return "1年以下";
            case 2: return "1-3年";
            case 3: return "3-5年";
            case 4: return "5-10年";
            case 5: return "10年以上";
            default: return "不限";
        }
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
