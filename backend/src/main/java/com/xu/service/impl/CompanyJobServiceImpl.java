package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BusinessException;
import com.xu.dto.JobPublishDTO;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.service.CompanyJobService;
import com.xu.service.CompanyService;
import com.xu.service.JobService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyJobServiceImpl implements CompanyJobService {

    private final JobService jobService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public void publishJob(JobPublishDTO dto, Long companyId) {
        Company company = companyService.getById(companyId);
        if (company == null || company.getAuditStatus() != 1) {
            throw new BusinessException("企业未通过审核，无法发布职位");
        }

        Job job = new Job();
        job.setCompanyId(companyId);
        job.setTitle(dto.getTitle());
        job.setCategory(dto.getCategory());
        job.setDescription(dto.getDescription());
        job.setRequirement(dto.getRequirement());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setExperience(dto.getExperience());
        job.setEducation(dto.getEducation());
        job.setStatus(1);
        job.setAuditStatus(0);
        job.setViewCount(0);
        job.setApplicationCount(0);
        jobService.save(job);
    }

    @Override
    @Transactional
    public void updateJob(JobPublishDTO dto, Long companyId) {
        Job job = jobService.getById(dto.getId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权操作");
        }

        job.setTitle(dto.getTitle());
        job.setCategory(dto.getCategory());
        job.setDescription(dto.getDescription());
        job.setRequirement(dto.getRequirement());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setExperience(dto.getExperience());
        job.setEducation(dto.getEducation());
        job.setAuditStatus(0);
        jobService.updateById(job);
    }

    @Override
    @Transactional
    public void deleteJob(Long id, Long companyId) {
        Job job = jobService.getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权操作");
        }

        job.setDeleted(1);
        jobService.updateById(job);
    }

    @Override
    @Transactional
    public void toggleJobStatus(Long id, Long companyId) {
        Job job = jobService.getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权操作");
        }

        if (job.getAuditStatus() != 1) {
            throw new BusinessException("职位未通过审核，无法上架");
        }

        job.setStatus(job.getStatus() == 1 ? 0 : 1);
        jobService.updateById(job);
    }

    @Override
    public Page<JobVO> getCompanyJobs(Long companyId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Job> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getCompanyId, companyId)
               .eq(Job::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Job::getStatus, status);
        }
        wrapper.orderByDesc(Job::getCreateTime);

        Page<Job> jobPage = jobService.page(page, wrapper);

        Company company = companyService.getById(companyId);

        Page<JobVO> voPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        List<JobVO> voList = jobPage.getRecords().stream()
                .map(job -> {
                    JobVO vo = new JobVO();
                    vo.setId(job.getId());
                    vo.setTitle(job.getTitle());
                    vo.setCategory(job.getCategory());
                    vo.setLocation(job.getLocation());
                    vo.setSalaryMin(job.getSalaryMin());
                    vo.setSalaryMax(job.getSalaryMax());
                    vo.setExperience(job.getExperience());
                    vo.setEducation(job.getEducation());
                    vo.setStatus(job.getStatus());
                    vo.setAuditStatus(job.getAuditStatus());
                    vo.setViewCount(job.getViewCount());
                    vo.setApplicationCount(job.getApplicationCount());
                    vo.setCreateTime(job.getCreateTime());

                    vo.setCompanyId(companyId);
                    if (company != null) {
                        vo.setCompanyName(company.getName());
                        vo.setCompanyLogo(company.getLogo());
                        vo.setCompanyIndustry(company.getIndustry());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public JobVO getJobDetail(Long id, Long companyId) {
        Job job = jobService.getById(id);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("职位不存在或无权查看");
        }

        JobVO vo = new JobVO();
        vo.setId(job.getId());
        vo.setTitle(job.getTitle());
        vo.setCategory(job.getCategory());
        vo.setLocation(job.getLocation());
        vo.setSalaryMin(job.getSalaryMin());
        vo.setSalaryMax(job.getSalaryMax());
        vo.setExperience(job.getExperience());
        vo.setEducation(job.getEducation());
        vo.setStatus(job.getStatus());
        vo.setAuditStatus(job.getAuditStatus());
        vo.setViewCount(job.getViewCount());
        vo.setApplicationCount(job.getApplicationCount());
        vo.setCreateTime(job.getCreateTime());

        Company company = companyService.getById(companyId);
        if (company != null) {
            vo.setCompanyId(companyId);
            vo.setCompanyName(company.getName());
            vo.setCompanyLogo(company.getLogo());
            vo.setCompanyIndustry(company.getIndustry());
        }

        return vo;
    }
}
