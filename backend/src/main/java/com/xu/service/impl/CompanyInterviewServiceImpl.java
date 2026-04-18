package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BusinessException;
import com.xu.dto.CompanyInterviewResultDTO;
import com.xu.dto.CompanyInterviewUpdateDTO;
import com.xu.entity.Application;
import com.xu.entity.Interview;
import com.xu.entity.Job;
import com.xu.entity.User;
import com.xu.mapper.InterviewMapper;
import com.xu.service.ApplicationService;
import com.xu.service.CompanyInterviewService;
import com.xu.service.JobService;
import com.xu.service.UserService;
import com.xu.vo.CompanyInterviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyInterviewServiceImpl implements CompanyInterviewService {

    private final InterviewMapper interviewMapper;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;

    @Override
    public Page<CompanyInterviewVO> getCompanyInterviews(Long companyId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Interview> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Interview::getCompanyId, companyId)
                .eq(Interview::getDeleted, 0);
        if (status != null) {
            wrapper.eq(Interview::getStatus, status);
        }
        wrapper.orderByDesc(Interview::getInterviewTime);

        Page<Interview> interviewPage = interviewMapper.selectPage(page, wrapper);

        List<Long> userIds = interviewPage.getRecords().stream()
                .map(Interview::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> jobIds = interviewPage.getRecords().stream()
                .map(Interview::getJobId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        Map<Long, Job> jobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<Job> jobs = jobService.listByIds(jobIds);
            jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, j -> j));
        }

        final Map<Long, User> finalUserMap = userMap;
        final Map<Long, Job> finalJobMap = jobMap;

        Page<CompanyInterviewVO> voPage = new Page<>(interviewPage.getCurrent(), interviewPage.getSize(), interviewPage.getTotal());
        List<CompanyInterviewVO> voList = interviewPage.getRecords().stream()
                .map(i -> convertToVO(i, finalUserMap.get(i.getUserId()), finalJobMap.get(i.getJobId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public CompanyInterviewVO getInterviewDetail(Long id, Long companyId) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        if (!interview.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权查看该面试记录");
        }

        User user = userService.getById(interview.getUserId());
        Job job = jobService.getById(interview.getJobId());

        return convertToVO(interview, user, job);
    }

    @Override
    @Transactional
    public void updateInterview(CompanyInterviewUpdateDTO dto, Long companyId) {
        Interview interview = interviewMapper.selectById(dto.getId());
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        if (!interview.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该面试记录");
        }

        if (interview.getStatus() != 0) {
            throw new BusinessException("该面试已确认，无法修改");
        }

        if (dto.getInterviewTime() != null) {
            interview.setInterviewTime(dto.getInterviewTime());
        }
        if (dto.getLocation() != null) {
            interview.setLocation(dto.getLocation());
        }
        if (dto.getInterviewType() != null) {
            interview.setInterviewType(dto.getInterviewType());
        }
        if (dto.getOnlineLink() != null) {
            interview.setOnlineLink(dto.getOnlineLink());
        }
        if (dto.getInterviewerName() != null) {
            interview.setInterviewerName(dto.getInterviewerName());
        }
        if (dto.getInterviewerPhone() != null) {
            interview.setInterviewerPhone(dto.getInterviewerPhone());
        }
        if (dto.getRequirement() != null) {
            interview.setRequirement(dto.getRequirement());
        }

        interviewMapper.updateById(interview);
    }

    @Override
    @Transactional
    public void setInterviewResult(CompanyInterviewResultDTO dto, Long companyId) {
        Interview interview = interviewMapper.selectById(dto.getId());
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        if (!interview.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该面试记录");
        }

        if (interview.getStatus() != 1 && interview.getStatus() != 3) {
            throw new BusinessException("当前状态无法录入面试结果");
        }

        if (dto.getStatus() != 4 && dto.getStatus() != 5) {
            throw new BusinessException("无效的面试结果状态");
        }

        interview.setStatus(dto.getStatus());
        interview.setResult(dto.getResult());
        interviewMapper.updateById(interview);

        Application application = applicationService.getById(interview.getApplicationId());
        if (application != null) {
            if (dto.getStatus() == 4) {
                application.setStatus(3);
            } else {
                application.setStatus(4);
            }
            application.setFeedbackTime(LocalDateTime.now());
            applicationService.updateById(application);
        }
    }

    @Override
    @Transactional
    public void completeInterview(Long id, Long companyId) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        if (!interview.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该面试记录");
        }

        if (interview.getStatus() != 1) {
            throw new BusinessException("当前状态无法标记完成");
        }

        interview.setStatus(3);
        interviewMapper.updateById(interview);
    }

    @Override
    @Transactional
    public void cancelInterview(Long id, Long companyId) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试记录不存在");
        }

        if (!interview.getCompanyId().equals(companyId)) {
            throw new BusinessException("无权操作该面试记录");
        }

        if (interview.getStatus() != 0 && interview.getStatus() != 1) {
            throw new BusinessException("该面试已处理，无法取消");
        }

        interview.setStatus(2);
        interviewMapper.updateById(interview);

        Application application = applicationService.getById(interview.getApplicationId());
        if (application != null && application.getStatus() == 2) {
            application.setStatus(1);
            application.setFeedbackTime(LocalDateTime.now());
            applicationService.updateById(application);
        }
    }

    private CompanyInterviewVO convertToVO(Interview interview, User user, Job job) {
        CompanyInterviewVO vo = new CompanyInterviewVO();
        vo.setId(interview.getId());
        vo.setApplicationId(interview.getApplicationId());
        vo.setUserId(interview.getUserId());
        vo.setJobId(interview.getJobId());
        vo.setInterviewTime(interview.getInterviewTime());
        vo.setLocation(interview.getLocation());
        vo.setInterviewType(interview.getInterviewType());
        vo.setOnlineLink(interview.getOnlineLink());
        vo.setInterviewerName(interview.getInterviewerName());
        vo.setInterviewerPhone(interview.getInterviewerPhone());
        vo.setRequirement(interview.getRequirement());
        vo.setStatus(interview.getStatus());
        vo.setStatusText(getStatusText(interview.getStatus()));
        vo.setResult(interview.getResult());
        vo.setCreateTime(interview.getCreateTime());

        if (user != null) {
            vo.setCandidateName(user.getRealName());
            vo.setCandidatePhone(user.getPhone());
            vo.setCandidateEmail(user.getEmail());
        }

        if (job != null) {
            vo.setJobTitle(job.getTitle());
        }

        return vo;
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
