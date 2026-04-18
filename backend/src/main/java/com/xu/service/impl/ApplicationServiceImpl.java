package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.ApplicationDTO;
import com.xu.entity.Application;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.entity.Resume;
import com.xu.common.BusinessException;
import com.xu.mapper.ApplicationMapper;
import com.xu.service.ApplicationService;
import com.xu.service.CompanyService;
import com.xu.service.JobService;
import com.xu.service.ResumeService;
import com.xu.vo.ApplicationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    private final JobService jobService;
    private final ResumeService resumeService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public void apply(ApplicationDTO dto, Long userId) {
        Job job = jobService.getById(dto.getJobId());
        if (job == null || job.getStatus() != 1) {
            throw new BusinessException("职位不存在或已下架");
        }

        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getUserId, userId)
                .eq(Application::getJobId, dto.getJobId())
                .eq(Application::getDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("您已投递过该职位");
        }

        Resume resume = resumeService.getById(dto.getResumeId());
        if (resume == null || !resume.getUserId().equals(userId)) {
            throw new BusinessException("简历不存在或无权使用");
        }

        Application application = new Application();
        application.setUserId(userId);
        application.setJobId(dto.getJobId());
        application.setResumeId(dto.getResumeId());
        application.setStatus(0);
        application.setRemark(dto.getRemark());
        this.save(application);

        job.setApplicationCount(job.getApplicationCount() + 1);
        jobService.updateById(job);
    }

    @Override
    public Page<ApplicationVO> getUserApplications(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Application> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getUserId, userId)
                .eq(Application::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Application::getStatus, status);
        }
        wrapper.orderByDesc(Application::getCreateTime);

        Page<Application> applicationPage = this.page(page, wrapper);

        List<Long> jobIds = applicationPage.getRecords().stream()
                .map(Application::getJobId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> resumeIds = applicationPage.getRecords().stream()
                .map(Application::getResumeId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Job> jobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<Job> jobs = jobService.listByIds(jobIds);
            jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, j -> j));
        }

        Map<Long, Resume> resumeMap = new HashMap<>();
        if (!resumeIds.isEmpty()) {
            List<Resume> resumes = resumeService.listByIds(resumeIds);
            resumeMap = resumes.stream().collect(Collectors.toMap(Resume::getId, r -> r));
        }

        List<Long> companyIds = jobMap.values().stream()
                .map(Job::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Job> finalJobMap = jobMap;
        final Map<Long, Resume> finalResumeMap = resumeMap;
        final Map<Long, Company> finalCompanyMap = companyMap;

        Page<ApplicationVO> voPage = new Page<>(applicationPage.getCurrent(), applicationPage.getSize(), applicationPage.getTotal());
        List<ApplicationVO> voList = applicationPage.getRecords().stream()
                .map(app -> {
                    ApplicationVO vo = new ApplicationVO();
                    vo.setId(app.getId());
                    vo.setJobId(app.getJobId());
                    vo.setResumeId(app.getResumeId());
                    vo.setStatus(app.getStatus());
                    vo.setStatusText(getStatusText(app.getStatus()));
                    vo.setRemark(app.getRemark());
                    vo.setViewTime(app.getViewTime());
                    vo.setFeedbackTime(app.getFeedbackTime());
                    vo.setCreateTime(app.getCreateTime());

                    Job job = finalJobMap.get(app.getJobId());
                    if (job != null) {
                        vo.setJobTitle(job.getTitle());
                        vo.setLocation(job.getLocation());
                        vo.setSalaryRange(formatSalary(job.getSalaryMin(), job.getSalaryMax()));

                        Company company = finalCompanyMap.get(job.getCompanyId());
                        if (company != null) {
                            vo.setCompanyName(company.getName());
                            vo.setCompanyLogo(company.getLogo());
                        }
                    }

                    Resume resume = finalResumeMap.get(app.getResumeId());
                    if (resume != null) {
                        vo.setResumeTitle(resume.getTitle());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ApplicationVO getApplicationDetail(Long id, Long userId) {
        Application application = this.getById(id);
        if (application == null || !application.getUserId().equals(userId)) {
            throw new BusinessException("投递记录不存在或无权查看");
        }

        ApplicationVO vo = new ApplicationVO();
        vo.setId(application.getId());
        vo.setJobId(application.getJobId());
        vo.setResumeId(application.getResumeId());
        vo.setStatus(application.getStatus());
        vo.setStatusText(getStatusText(application.getStatus()));
        vo.setRemark(application.getRemark());
        vo.setViewTime(application.getViewTime());
        vo.setFeedbackTime(application.getFeedbackTime());
        vo.setCreateTime(application.getCreateTime());

        Job job = jobService.getById(application.getJobId());
        if (job != null) {
            vo.setJobTitle(job.getTitle());
            vo.setLocation(job.getLocation());
            vo.setSalaryRange(formatSalary(job.getSalaryMin(), job.getSalaryMax()));

            Company company = companyService.getById(job.getCompanyId());
            if (company != null) {
                vo.setCompanyName(company.getName());
                vo.setCompanyLogo(company.getLogo());
            }
        }

        Resume resume = resumeService.getById(application.getResumeId());
        if (resume != null) {
            vo.setResumeTitle(resume.getTitle());
        }

        return vo;
    }

    @Override
    @Transactional
    public void cancelApplication(Long id, Long userId) {
        Application application = this.getById(id);
        if (application == null || !application.getUserId().equals(userId)) {
            throw new BusinessException("投递记录不存在或无权操作");
        }

        if (application.getStatus() > 1) {
            throw new BusinessException("当前状态无法取消投递");
        }

        application.setDeleted(1);
        this.updateById(application);
    }

    @Override
    public boolean checkApplied(Long jobId, Long userId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getUserId, userId)
                .eq(Application::getJobId, jobId)
                .eq(Application::getDeleted, 0);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<Application> page(Page<Application> page, LambdaQueryWrapper<Application> wrapper) {
        return super.page(page, wrapper);
    }

    @Override
    public Application getById(Long id) {
        return super.getById(id);
    }

    @Override
    public List<Application> list(LambdaQueryWrapper<Application> wrapper) {
        return super.list(wrapper);
    }

    @Override
    public boolean updateById(Application entity) {
        return super.updateById(entity);
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待查看";
            case 1: return "已查看";
            case 2: return "待面试";
            case 3: return "已录用";
            case 4: return "已拒绝";
            default: return "未知";
        }
    }

    private String formatSalary(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (min == null && max == null) return "面议";
        if (min == null) return max + "K以下";
        if (max == null) return min + "K以上";
        return min + "K-" + max + "K";
    }
}
