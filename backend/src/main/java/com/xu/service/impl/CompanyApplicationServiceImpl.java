package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.ApplicationHandleDTO;
import com.xu.dto.InterviewCreateDTO;
import com.xu.entity.Application;
import com.xu.entity.Company;
import com.xu.entity.Interview;
import com.xu.entity.Job;
import com.xu.entity.Resume;
import com.xu.entity.User;
import com.xu.common.BusinessException;
import com.xu.mapper.InterviewMapper;
import com.xu.service.*;
import com.xu.vo.CompanyApplicationVO;
import com.xu.vo.CompanyStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyApplicationServiceImpl implements CompanyApplicationService {

    @Autowired
    private ApplicationService applicationService;
    private final JobService jobService;
    private final ResumeService resumeService;
    private final UserService userService;
    private final CompanyService companyService;
    private final InterviewMapper interviewMapper;

    @Override
    public Page<CompanyApplicationVO> getApplicationList(Long companyId, Integer status, Integer pageNum, Integer pageSize) {
        List<Job> jobs = getCompanyJobs(companyId);
        if (jobs.isEmpty()) {
            Page<CompanyApplicationVO> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }

        List<Long> jobIds = jobs.stream().map(Job::getId).collect(Collectors.toList());

        Page<Application> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Application::getJobId, jobIds)
                .eq(Application::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Application::getStatus, status);
        }
        wrapper.orderByDesc(Application::getCreateTime);

        Page<Application> applicationPage = applicationService.page(page, wrapper);

        Map<Long, Job> jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, j -> j));

        List<Long> resumeIds = applicationPage.getRecords().stream()
                .map(Application::getResumeId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Resume> resumeMap = new HashMap<>();
        if (!resumeIds.isEmpty()) {
            List<Resume> resumes = resumeService.listByIds(resumeIds);
            resumeMap = resumes.stream().collect(Collectors.toMap(Resume::getId, r -> r));
        }

        List<Long> userIds = applicationPage.getRecords().stream()
                .map(Application::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        final Map<Long, Resume> finalResumeMap = resumeMap;
        final Map<Long, User> finalUserMap = userMap;

        Page<CompanyApplicationVO> voPage = new Page<>(applicationPage.getCurrent(), applicationPage.getSize(), applicationPage.getTotal());
        List<CompanyApplicationVO> voList = applicationPage.getRecords().stream()
                .map(app -> convertToVO(app, jobMap.get(app.getJobId()), finalResumeMap.get(app.getResumeId()), finalUserMap.get(app.getUserId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public CompanyApplicationVO getApplicationDetail(Long id, Long companyId) {
        Application application = applicationService.getById(id);
        if (application == null) {
            throw new BusinessException("投递记录不存在");
        }

        Job job = jobService.getById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权查看该投递记录");
        }

        if (application.getStatus() == 0) {
            application.setStatus(1);
            application.setViewTime(LocalDateTime.now());
            applicationService.updateById(application);
        }

        Resume resume = resumeService.getById(application.getResumeId());
        User user = userService.getById(application.getUserId());

        return convertToVO(application, job, resume, user);
    }

    @Override
    @Transactional
    public void handleApplication(ApplicationHandleDTO dto, Long companyId) {
        Application application = applicationService.getById(dto.getId());
        if (application == null) {
            throw new BusinessException("投递记录不存在");
        }

        Job job = jobService.getById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该投递记录");
        }

        if (application.getStatus() == 3 || application.getStatus() == 4) {
            throw new BusinessException("该投递已处理完成，无法修改");
        }

        application.setStatus(dto.getStatus());
        if (dto.getRemark() != null) {
            application.setRemark(dto.getRemark());
        }
        application.setFeedbackTime(LocalDateTime.now());

        if (application.getViewTime() == null) {
            application.setViewTime(LocalDateTime.now());
        }

        applicationService.updateById(application);
    }

    @Override
    @Transactional
    public void createInterview(InterviewCreateDTO dto, Long companyId) {
        Application application = applicationService.getById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("投递记录不存在");
        }

        Job job = jobService.getById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该投递记录");
        }

        if (application.getStatus() != 1 && application.getStatus() != 2) {
            throw new BusinessException("当前投递状态无法安排面试");
        }

        if (application.getStatus() != 2) {
            application.setStatus(2);
            application.setFeedbackTime(LocalDateTime.now());
            applicationService.updateById(application);
        }

        Interview interview = new Interview();
        interview.setApplicationId(dto.getApplicationId());
        interview.setUserId(application.getUserId());
        interview.setJobId(application.getJobId());
        interview.setCompanyId(companyId);
        interview.setInterviewTime(dto.getInterviewTime());
        interview.setLocation(dto.getLocation());
        interview.setInterviewType(dto.getInterviewType());
        interview.setOnlineLink(dto.getOnlineLink());
        interview.setInterviewerName(dto.getInterviewerName());
        interview.setInterviewerPhone(dto.getInterviewerPhone());
        interview.setRequirement(dto.getRequirement());
        interview.setStatus(0);
        interviewMapper.insert(interview);
    }

    @Override
    public CompanyStatisticsVO getStatistics(Long companyId) {
        CompanyStatisticsVO vo = new CompanyStatisticsVO();

        List<Job> jobs = getCompanyJobs(companyId);
        vo.setTotalJobs((long) jobs.size());
        vo.setActiveJobs(jobs.stream().filter(j -> j.getStatus() == 1).count());

        if (!jobs.isEmpty()) {
            List<Long> jobIds = jobs.stream().map(Job::getId).collect(Collectors.toList());

            LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Application::getJobId, jobIds)
                    .eq(Application::getDeleted, 0);
            List<Application> applications = applicationService.list(wrapper);

            vo.setTotalApplications((long) applications.size());
            vo.setPendingApplications(applications.stream().filter(a -> a.getStatus() == 0 || a.getStatus() == 1).count());
            vo.setInterviewApplications(applications.stream().filter(a -> a.getStatus() == 2).count());
            vo.setHiredApplications(applications.stream().filter(a -> a.getStatus() == 3).count());

            if (vo.getTotalApplications() > 0) {
                vo.setConversionRate(vo.getHiredApplications().doubleValue() / vo.getTotalApplications().doubleValue() * 100);
            } else {
                vo.setConversionRate(0.0);
            }

            vo.setApplicationTrend(generateApplicationTrend(applications));
            vo.setJobApplicationCount(generateJobApplicationCount(jobs, applications));
        } else {
            vo.setTotalApplications(0L);
            vo.setPendingApplications(0L);
            vo.setInterviewApplications(0L);
            vo.setHiredApplications(0L);
            vo.setConversionRate(0.0);
            vo.setApplicationTrend(new ArrayList<>());
            vo.setJobApplicationCount(new ArrayList<>());
        }

        return vo;
    }

    private List<Job> getCompanyJobs(Long companyId) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getCompanyId, companyId)
                .eq(Job::getDeleted, 0);
        return jobService.list(wrapper);
    }

    private CompanyApplicationVO convertToVO(Application app, Job job, Resume resume, User user) {
        CompanyApplicationVO vo = new CompanyApplicationVO();
        vo.setId(app.getId());
        vo.setUserId(app.getUserId());
        vo.setJobId(app.getJobId());
        vo.setResumeId(app.getResumeId());
        vo.setStatus(app.getStatus());
        vo.setStatusText(getStatusText(app.getStatus()));
        vo.setRemark(app.getRemark());
        vo.setViewTime(app.getViewTime());
        vo.setFeedbackTime(app.getFeedbackTime());
        vo.setCreateTime(app.getCreateTime());

        if (job != null) {
            vo.setJobTitle(job.getTitle());
        }

        if (user != null) {
            vo.setUserName(user.getRealName());
            vo.setUserPhone(user.getPhone());
            vo.setUserEmail(user.getEmail());
        }

        if (resume != null) {
            vo.setResumeTitle(resume.getTitle());
            vo.setResumeRealName(resume.getRealName());
            vo.setResumeEducation(resume.getEducation());
            vo.setResumeSchool(resume.getSchool());
            vo.setResumeSkills(resume.getSkills());
            vo.setResumeWorkExperience(resume.getWorkExperience());
            vo.setResumeSelfIntroduction(resume.getSelfIntroduction());
        }

        return vo;
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

    private List<Map<String, Object>> generateApplicationTrend(List<Application> applications) {
        Map<String, Long> trendMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            String key = date.format(formatter);
            trendMap.put(key, 0L);
        }

        for (Application app : applications) {
            if (app.getCreateTime() != null) {
                String key = app.getCreateTime().format(formatter);
                if (trendMap.containsKey(key)) {
                    trendMap.put(key, trendMap.get(key) + 1);
                }
            }
        }

        return trendMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> generateJobApplicationCount(List<Job> jobs, List<Application> applications) {
        Map<Long, Long> countMap = applications.stream()
                .collect(Collectors.groupingBy(Application::getJobId, Collectors.counting()));

        return jobs.stream()
                .sorted((a, b) -> Long.compare(countMap.getOrDefault(b.getId(), 0L), countMap.getOrDefault(a.getId(), 0L)))
                .limit(5)
                .map(job -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("jobTitle", job.getTitle());
                    item.put("count", countMap.getOrDefault(job.getId(), 0L));
                    return item;
                })
                .collect(Collectors.toList());
    }
}
