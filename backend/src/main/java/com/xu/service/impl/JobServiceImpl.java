package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.JobQueryDTO;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.mapper.JobMapper;
import com.xu.service.CompanyService;
import com.xu.service.JobService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final CompanyService companyService;

    private static final Map<Integer, String> EXPERIENCE_MAP = new HashMap<>() {{
        put(0, "不限");
        put(1, "1年以下");
        put(2, "1-3年");
        put(3, "3-5年");
        put(4, "5-10年");
        put(5, "10年以上");
    }};

    @Override
    public Page<JobVO> queryJobList(JobQueryDTO queryDTO) {
        Page<Job> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1)
               .eq(Job::getAuditStatus, 1)
               .eq(Job::getDeleted, 0);

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Job::getTitle, queryDTO.getKeyword())
                    .or().like(Job::getDescription, queryDTO.getKeyword())
                    .or().like(Job::getCategory, queryDTO.getKeyword()));
        }

        if (StringUtils.hasText(queryDTO.getCategory())) {
            wrapper.like(Job::getCategory, queryDTO.getCategory());
        }

        if (StringUtils.hasText(queryDTO.getLocation())) {
            wrapper.like(Job::getLocation, queryDTO.getLocation());
        }

        if (queryDTO.getSalaryMin() != null) {
            wrapper.ge(Job::getSalaryMin, queryDTO.getSalaryMin());
        }

        if (queryDTO.getSalaryMax() != null) {
            wrapper.le(Job::getSalaryMax, queryDTO.getSalaryMax());
        }

        if (queryDTO.getExperience() != null) {
            wrapper.le(Job::getExperience, queryDTO.getExperience());
        }

        if (StringUtils.hasText(queryDTO.getEducation())) {
            wrapper.eq(Job::getEducation, queryDTO.getEducation());
        }

        wrapper.orderByDesc(Job::getCreateTime);

        Page<Job> jobPage = this.page(page, wrapper);

        List<Long> companyIds = jobPage.getRecords().stream()
                .map(Job::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream()
                    .collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Company> finalCompanyMap = companyMap;


        Page<JobVO> voPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        List<JobVO> voList = jobPage.getRecords().stream()
                .map(job -> convertToVO(job, finalCompanyMap.get(job.getCompanyId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public JobVO getJobDetail(Long id) {
        Job job = this.getById(id);
        if (job == null || job.getDeleted() == 1) {
            return null;
        }

        Company company = companyService.getById(job.getCompanyId());
        return convertToVO(job, company);
    }

    @Override
    public void incrementViewCount(Long id) {
        Job job = this.getById(id);
        if (job != null) {
            job.setViewCount(job.getViewCount() + 1);
            this.updateById(job);
        }
    }

    private JobVO convertToVO(Job job, Company company) {
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(job, vo);

        if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
            vo.setSalaryRange(job.getSalaryMin().intValue() + "K-" + job.getSalaryMax().intValue() + "K");
        }

        vo.setExperienceName(EXPERIENCE_MAP.getOrDefault(job.getExperience(), "不限"));

        if (company != null) {
            vo.setCompanyName(company.getName());
            vo.setCompanyLogo(company.getLogo());
            vo.setCompanyIndustry(company.getIndustry());
            vo.setCompanyScale(company.getScale());
        }

        return vo;
    }
}
