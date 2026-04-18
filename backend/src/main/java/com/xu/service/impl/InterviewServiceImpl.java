package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.InterviewStatusDTO;
import com.xu.entity.Application;
import com.xu.entity.Company;
import com.xu.entity.Interview;
import com.xu.entity.Job;
import com.xu.common.BusinessException;
import com.xu.mapper.InterviewMapper;
import com.xu.service.ApplicationService;
import com.xu.service.CompanyService;
import com.xu.service.InterviewService;
import com.xu.service.JobService;
import com.xu.vo.InterviewVO;
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
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    private final JobService jobService;
    private final CompanyService companyService;
    private final ApplicationService applicationService;

    @Override
    public Page<InterviewVO> getUserInterviews(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Interview> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interview::getUserId, userId)
               .eq(Interview::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Interview::getStatus, status);
        }
        wrapper.orderByDesc(Interview::getInterviewTime);

        Page<Interview> interviewPage = this.page(page, wrapper);

        List<Long> jobIds = interviewPage.getRecords().stream()
                .map(Interview::getJobId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> companyIds = interviewPage.getRecords().stream()
                .map(Interview::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Job> jobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<Job> jobs = jobService.listByIds(jobIds);
            jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, j -> j));
        }

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Job> finalJobMap = jobMap;
        final Map<Long, Company> finalCompanyMap = companyMap;

        Page<InterviewVO> voPage = new Page<>(interviewPage.getCurrent(), interviewPage.getSize(), interviewPage.getTotal());
        List<InterviewVO> voList = interviewPage.getRecords().stream()
                .map(interview -> convertToVO(interview, finalJobMap.get(interview.getJobId()), finalCompanyMap.get(interview.getCompanyId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public InterviewVO getInterviewDetail(Long id, Long userId) {
        Interview interview = this.getById(id);
        if (interview == null || !interview.getUserId().equals(userId)) {
            throw new BusinessException("面试记录不存在或无权查看");
        }

        Job job = jobService.getById(interview.getJobId());
        Company company = companyService.getById(interview.getCompanyId());

        return convertToVO(interview, job, company);
    }

    @Override
    @Transactional
    public void updateInterviewStatus(InterviewStatusDTO dto, Long userId) {
        Interview interview = this.getById(dto.getInterviewId());
        if (interview == null || !interview.getUserId().equals(userId)) {
            throw new BusinessException("面试记录不存在或无权操作");
        }

        if (interview.getStatus() != 0) {
            throw new BusinessException("当前面试状态无法修改");
        }

        if (dto.getStatus() == 1) {
            interview.setStatus(1);
        } else if (dto.getStatus() == 2) {
            interview.setStatus(2);
            Application application = applicationService.getById(interview.getApplicationId());
            if (application != null && application.getStatus() == 2) {
                application.setStatus(1);
                application.setFeedbackTime(LocalDateTime.now());
                applicationService.updateById(application);
            }
        } else {
            throw new BusinessException("无效的操作");
        }

        if (dto.getFeedback() != null) {
            interview.setFeedback(dto.getFeedback());
        }
        this.updateById(interview);
    }

    private InterviewVO convertToVO(Interview interview, Job job, Company company) {
        InterviewVO vo = new InterviewVO();
        vo.setId(interview.getId());
        vo.setApplicationId(interview.getApplicationId());
        vo.setJobId(interview.getJobId());
        vo.setCompanyId(interview.getCompanyId());
        vo.setInterviewTime(interview.getInterviewTime());
        vo.setLocation(interview.getLocation());
        vo.setInterviewType(interview.getInterviewType());
        vo.setInterviewTypeText(getInterviewTypeText(interview.getInterviewType()));
        vo.setOnlineLink(interview.getOnlineLink());
        vo.setInterviewerName(interview.getInterviewerName());
        vo.setInterviewerPhone(interview.getInterviewerPhone());
        vo.setRequirement(interview.getRequirement());
        vo.setStatus(interview.getStatus());
        vo.setStatusText(getStatusText(interview.getStatus()));
        vo.setResult(interview.getResult());
        vo.setFeedback(interview.getFeedback());
        vo.setCreateTime(interview.getCreateTime());

        if (job != null) {
            vo.setJobTitle(job.getTitle());
        }

        if (company != null) {
            vo.setCompanyName(company.getName());
            vo.setCompanyLogo(company.getLogo());
        }

        return vo;
    }

    private String getInterviewTypeText(Integer type) {
        return type != null && type == 1 ? "线上面试" : "线下面试";
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已取消";
            case 3: return "已完成";
            case 4: return "已通过";
            case 5: return "未通过";
            default: return "未知";
        }
    }
}
