package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xu.aiservice.JobRecommendService;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.entity.Resume;
import com.xu.service.CompanyService;
import com.xu.service.JobService;
import com.xu.service.RecommendService;
import com.xu.service.ResumeService;
import com.xu.vo.JobVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final JobRecommendService jobRecommendService;
    private final JobService jobService;
    private final ResumeService resumeService;
    private final CompanyService companyService;
    private final ObjectMapper objectMapper;

    @Override
    public List<JobVO> getRecommendJobs(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        Resume resume = getDefaultResume(userId);
        
        List<Job> allJobs = getActiveJobs();
        
        if (allJobs.isEmpty()) {
            return new ArrayList<>();
        }

        List<JobWithScore> preFilteredJobs = calculateJobScores(allJobs, resume);
        preFilteredJobs.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<Job> candidateJobs = preFilteredJobs.stream()
                .limit(15)
                .collect(Collectors.toList());

        List<JobWithScore> scoredJobs;
        
        try {
            log.info("使用AI职位推荐 - 用户: {}, 简历: {}, 候选职位: {}", 
                    userId, resume != null ? resume.getId() : null, candidateJobs.size());
            scoredJobs = calculateJobScoresWithAI(candidateJobs, resume);
        } catch (Exception e) {
            log.warn("AI推荐失败，使用规则匹配: {}", e.getMessage());
            scoredJobs = preFilteredJobs;
        }
        
        scoredJobs.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<Job> recommendedJobs = scoredJobs.stream()
                .limit(limit)
                .collect(Collectors.toList());

        return convertToJobVOList(recommendedJobs);
    }

    private Resume getDefaultResume(Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId)
               .eq(Resume::getIsDefault, 1)
               .eq(Resume::getDeleted, 0);
        Resume resume = resumeService.getOne(wrapper);
        
        if (resume == null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Resume::getUserId, userId)
                   .eq(Resume::getDeleted, 0)
                   .orderByDesc(Resume::getUpdateTime)
                   .last("LIMIT 1");
            resume = resumeService.getOne(wrapper);
        }
        
        return resume;
    }

    private List<Job> getActiveJobs() {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1)
               .eq(Job::getAuditStatus, 1)
               .eq(Job::getDeleted, 0)
               .orderByDesc(Job::getCreateTime)
               .last("LIMIT 50");
        return jobService.list(wrapper);
    }

    private List<JobWithScore> calculateJobScoresWithAI(List<Job> jobs, Resume resume) {
        List<JobWithScore> result = new ArrayList<>();
        
        if (resume == null) {
            for (Job job : jobs) {
                result.add(new JobWithScore(job, Math.random() * 100, "基于规则的匹配结果"));
            }
            return result;
        }

        String prompt = buildRecommendationPrompt(resume, jobs);
        String aiResponse = jobRecommendService.recommendJobs(prompt);
        
        if (aiResponse != null && !aiResponse.isEmpty()) {
            try {
                return parseRecommendationResponse(aiResponse, jobs);
            } catch (Exception e) {
                log.error("解析AI推荐响应失败，使用规则匹配", e);
                return calculateJobScores(jobs, resume);
            }
        } else {
            log.warn("AI推荐响应为空，使用规则匹配");
            return calculateJobScores(jobs, resume);
        }
    }

    private List<JobWithScore> calculateJobScores(List<Job> jobs, Resume resume) {
        List<JobWithScore> result = new ArrayList<>();
        Random random = new Random();

        for (Job job : jobs) {
            double score = 0;

            if (resume != null) {
                if (resume.getEducation() != null && job.getEducation() != null) {
                    if (matchEducation(resume.getEducation(), job.getEducation())) {
                        score += 25;
                    }
                }

                if (resume.getLocation() != null && job.getLocation() != null) {
                    if (resume.getLocation().contains(job.getLocation()) || 
                        job.getLocation().contains(resume.getLocation())) {
                        score += 20;
                    }
                }

                if (resume.getSkills() != null && job.getRequirement() != null) {
                    score += calculateSkillMatch(resume.getSkills(), job.getRequirement()) * 30;
                }

                if (resume.getWorkExperience() != null && job.getRequirement() != null) {
                    score += calculateExperienceMatch(resume.getWorkExperience(), job.getRequirement()) * 15;
                }
            }

            score += random.nextDouble() * 10;

            result.add(new JobWithScore(job, score, "基于规则的匹配结果"));
        }

        return result;
    }

    private boolean matchEducation(String resumeEdu, String jobEdu) {
        Map<String, Integer> eduLevel = new HashMap<>();
        eduLevel.put("大专", 1);
        eduLevel.put("本科", 2);
        eduLevel.put("硕士", 3);
        eduLevel.put("博士", 4);

        Integer resumeLevel = eduLevel.getOrDefault(resumeEdu, 0);
        Integer jobLevel = eduLevel.getOrDefault(jobEdu, 0);

        return resumeLevel >= jobLevel;
    }

    private double calculateSkillMatch(String skills, String requirement) {
        String[] skillArray = skills.split("[,，、\\s]+");
        int matchCount = 0;
        for (String skill : skillArray) {
            if (skill.length() > 1 && requirement.toLowerCase().contains(skill.toLowerCase())) {
                matchCount++;
            }
        }
        return Math.min(1.0, (double) matchCount / Math.max(1, skillArray.length));
    }

    private double calculateExperienceMatch(String experience, String requirement) {
        if (experience.length() > 50 && requirement.contains("经验")) {
            return 1.0;
        }
        return 0.5;
    }

    private String buildRecommendationPrompt(Resume resume, List<Job> jobs) {
        List<Long> companyIds = jobs.stream()
                .map(Job::getCompanyId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Company> finalCompanyMap = companyMap;

        StringBuilder prompt = new StringBuilder();
        prompt.append("根据求职者简历推荐最匹配的职位，返回JSON格式结果。\n\n");
        
        prompt.append("【简历】\n");
        prompt.append("学历: ").append(resume.getEducation() != null ? resume.getEducation() : "不限").append("\n");
        prompt.append("学校: ").append(resume.getSchool() != null ? resume.getSchool() : "").append("\n");
        prompt.append("技能: ").append(resume.getSkills() != null ? resume.getSkills() : "").append("\n");
        prompt.append("经历: ").append(truncate(resume.getWorkExperience(), 200)).append("\n");
        prompt.append("城市: ").append(resume.getLocation() != null ? resume.getLocation() : "").append("\n");
        
        prompt.append("\n【职位列表】\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            Company company = job.getCompanyId() != null ? finalCompanyMap.get(job.getCompanyId()) : null;
            
            prompt.append(i + 1).append(". ID:").append(job.getId());
            prompt.append(" | ").append(job.getTitle());
            prompt.append(" | ").append(company != null ? company.getName() : "");
            prompt.append(" | ").append(job.getLocation() != null ? job.getLocation() : "");
            prompt.append(" | ").append(job.getSalaryMin() != null ? job.getSalaryMin() : 0).append("-").append(job.getSalaryMax() != null ? job.getSalaryMax() : 0).append("K");
            prompt.append(" | 学历:").append(job.getEducation() != null ? job.getEducation() : "不限");
            prompt.append(" | 经验:").append(getExperienceText(job.getExperience()));
            prompt.append(" | 要求:").append(truncate(job.getRequirement(), 100));
            prompt.append("\n");
        }
        
        prompt.append("\n【返回格式】\n");
        prompt.append("{\"recommendations\":[{\"jobId\":ID,\"matchScore\":分数,\"reason\":\"理由\"}]}\n");
        prompt.append("推荐5个最匹配职位，按匹配度排序。\n");
        
        return prompt.toString();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private List<JobWithScore> parseRecommendationResponse(String response, List<Job> jobs) {
        List<JobWithScore> result = new ArrayList<>();
        
        try {
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            } else if (response.contains("{")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            }
            
            JsonNode node = objectMapper.readTree(jsonStr);
            JsonNode recommendationsNode = node.path("recommendations");
            
            if (recommendationsNode.isArray()) {
                Map<Long, Job> jobMap = new HashMap<>();
                for (Job job : jobs) {
                    jobMap.put(job.getId(), job);
                }
                
                for (JsonNode recommendationNode : recommendationsNode) {
                    Long jobId = recommendationNode.path("jobId").asLong(0);
                    double matchScore = recommendationNode.path("matchScore").asDouble(0);
                    String reason = recommendationNode.path("reason").asText("");
                    
                    Job job = jobMap.get(jobId);
                    if (job != null) {
                        result.add(new JobWithScore(job, matchScore, reason));
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("解析AI推荐响应失败", e);
            throw new RuntimeException("解析 AI 推荐结果失败：" + e.getMessage());
        }
        
        return result;
    }

    private List<JobVO> convertToJobVOList(List<Job> jobs) {
        if (jobs.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> companyIds = jobs.stream()
                .map(Job::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Company> finalCompanyMap = companyMap;

        return jobs.stream()
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
                    vo.setViewCount(job.getViewCount());
                    vo.setApplicationCount(job.getApplicationCount());
                    vo.setCreateTime(job.getCreateTime());

                    if (job instanceof JobWithScore) {
                        JobWithScore jobWithScore = (JobWithScore) job;
                        vo.setMatchScore(jobWithScore.score);
                        vo.setMatchReason(jobWithScore.reason);
                    }

                    Company company = finalCompanyMap.get(job.getCompanyId());
                    if (company != null) {
                        vo.setCompanyId(company.getId());
                        vo.setCompanyName(company.getName());
                        vo.setCompanyLogo(company.getLogo());
                        vo.setCompanyIndustry(company.getIndustry());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }

    private String getExperienceText(Integer exp) {
        if (exp == null) return "不限";
        switch (exp) {
            case 0: return "经验不限";
            case 1: return "1年以下";
            case 2: return "1-3年";
            case 3: return "3-5年";
            case 4: return "5-10年";
            case 5: return "10年以上";
            default: return "不限";
        }
    }

    private static class JobWithScore extends Job {
        double score;
        String reason;

        JobWithScore(Job job, double score, String reason) {
            this.setId(job.getId());
            this.setCompanyId(job.getCompanyId());
            this.setTitle(job.getTitle());
            this.setCategory(job.getCategory());
            this.setLocation(job.getLocation());
            this.setSalaryMin(job.getSalaryMin());
            this.setSalaryMax(job.getSalaryMax());
            this.setExperience(job.getExperience());
            this.setEducation(job.getEducation());
            this.setStatus(job.getStatus());
            this.setAuditStatus(job.getAuditStatus());
            this.setViewCount(job.getViewCount());
            this.setApplicationCount(job.getApplicationCount());
            this.setCreateTime(job.getCreateTime());
            this.setDeleted(job.getDeleted());
            this.setRequirement(job.getRequirement());
            
            this.score = score;
            this.reason = reason;
        }
    }
}
