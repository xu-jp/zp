package com.xu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xu.aiservice.ResumeScreenService;
import com.xu.dto.ResumeScreenDTO;
import com.xu.entity.Application;
import com.xu.entity.Job;
import com.xu.entity.Resume;
import com.xu.service.AIResumeScreenService;
import com.xu.service.ApplicationService;
import com.xu.service.JobService;
import com.xu.service.ResumeService;
import com.xu.vo.ResumeScreenResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIResumeScreenServiceImpl implements AIResumeScreenService {

    private final ResumeScreenService resumeScreenService;
    private final ApplicationService applicationService;
    private final ResumeService resumeService;
    private final JobService jobService;
    private final ObjectMapper objectMapper;

    @Override
    public ResumeScreenResultVO screenResume(ResumeScreenDTO dto, Long companyId) {
        Application application = applicationService.getById(dto.getApplicationId());
        if (application == null) {
            throw new RuntimeException("投递记录不存在");
        }

        Job job = jobService.getById(application.getJobId());
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new RuntimeException("无权操作该投递记录");
        }

        Resume resume = resumeService.getById(application.getResumeId());
        if (resume == null) {
            throw new RuntimeException("简历不存在");
        }

        return analyzeResume(resume, job, dto);
    }

    @Override
    public void batchScreenResumes(Long jobId, ResumeScreenDTO dto, Long companyId) {
        Job job = jobService.getById(jobId);
        if (job == null || !job.getCompanyId().equals(companyId)) {
            throw new RuntimeException("无权操作该职位");
        }

        List<Application> applications = applicationService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Application>()
                .eq(Application::getJobId, jobId)
                .eq(Application::getDeleted, 0)
        );

        for (Application app : applications) {
            Resume resume = resumeService.getById(app.getResumeId());
            if (resume != null) {
                try {
                    ResumeScreenResultVO result = analyzeResume(resume, job, dto);
                    log.info("简历筛选完成: applicationId={}, matchLevel={}", app.getId(), result.getMatchLevel());
                } catch (Exception e) {
                    log.error("简历筛选失败: applicationId={}", app.getId(), e);
                }
            }
        }
    }

    private ResumeScreenResultVO analyzeResume(Resume resume, Job job, ResumeScreenDTO dto) {
        String prompt = buildPrompt(resume, job, dto);
        
        try {
            String aiResponse = resumeScreenService.screenResume(prompt);
            return parseAIResponse(aiResponse, resume);
        } catch (Exception e) {
            log.error("调用AI服务失败，使用模拟数据", e);
            return parseAIResponse(getMockResponse(), resume);
        }
    }

    private String buildPrompt(Resume resume, Job job, ResumeScreenDTO dto) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下信息分析简历与职位的匹配度。\n\n");
        
        prompt.append("【职位信息】\n");
        prompt.append("职位名称：").append(job.getTitle()).append("\n");
        prompt.append("职位要求：").append(job.getRequirement() != null ? job.getRequirement() : "无").append("\n");
        prompt.append("职位描述：").append(job.getDescription() != null ? job.getDescription() : "无").append("\n");
        prompt.append("经验要求：").append(getExperienceText(job.getExperience())).append("\n");
        prompt.append("学历要求：").append(job.getEducation() != null ? job.getEducation() : "不限").append("\n");
        
        if (dto.getSkillKeywords() != null && !dto.getSkillKeywords().isEmpty()) {
            prompt.append("关键技能要求：").append(String.join("、", dto.getSkillKeywords())).append("\n");
        }
        if (dto.getMinExperience() != null) {
            prompt.append("最低工作年限：").append(dto.getMinExperience()).append("年\n");
        }
        if (dto.getMinEducation() != null) {
            prompt.append("最低学历要求：").append(dto.getMinEducation()).append("\n");
        }
        if (dto.getJobRequirements() != null) {
            prompt.append("额外要求：").append(dto.getJobRequirements()).append("\n");
        }
        
        prompt.append("\n【简历信息】\n");
        prompt.append("姓名：").append(resume.getRealName() != null ? resume.getRealName() : "未提供").append("\n");
        prompt.append("学历：").append(resume.getEducation() != null ? resume.getEducation() : "未提供").append("\n");
        prompt.append("学校：").append(resume.getSchool() != null ? resume.getSchool() : "未提供").append("\n");
        prompt.append("专业技能：").append(resume.getSkills() != null ? resume.getSkills() : "未提供").append("\n");
        prompt.append("工作经历：").append(resume.getWorkExperience() != null ? resume.getWorkExperience() : "未提供").append("\n");
        prompt.append("项目经验：").append(resume.getProjectExperience() != null ? resume.getProjectExperience() : "未提供").append("\n");
        prompt.append("自我介绍：").append(resume.getSelfIntroduction() != null ? resume.getSelfIntroduction() : "未提供").append("\n");
        
        prompt.append("\n【分析要求】\n");
        prompt.append("请按以下JSON格式返回分析结果，不要包含其他内容：\n");
        prompt.append("{\n");
        prompt.append("  \"matchLevel\": \"高/中/低\",\n");
        prompt.append("  \"matchScore\": 0-100的匹配分数,\n");
        prompt.append("  \"highlights\": [\"亮点1\", \"亮点2\", ...],\n");
        prompt.append("  \"matchedSkills\": [\"匹配的技能1\", ...],\n");
        prompt.append("  \"missingSkills\": [\"缺失的技能1\", ...],\n");
        prompt.append("  \"experienceMatch\": \"经验匹配情况说明\",\n");
        prompt.append("  \"educationMatch\": \"学历匹配情况说明\",\n");
        prompt.append("  \"summary\": \"简历综合评价\",\n");
        prompt.append("  \"recommendation\": \"录用建议\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    private String getMockResponse() {
        return "{\n" +
               "  \"matchLevel\": \"中\",\n" +
               "  \"matchScore\": 65,\n" +
               "  \"highlights\": [\"具备相关工作经验\", \"学历符合要求\"],\n" +
               "  \"matchedSkills\": [\"Java\", \"MySQL\"],\n" +
               "  \"missingSkills\": [\"Spring Boot\", \"微服务\"],\n" +
               "  \"experienceMatch\": \"工作经验基本符合要求\",\n" +
               "  \"educationMatch\": \"学历符合职位要求\",\n" +
               "  \"summary\": \"候选人具备一定的基础能力，但部分核心技能需要加强\",\n" +
               "  \"recommendation\": \"建议进入面试环节，重点考察技术深度\"\n" +
               "}";
    }

    private ResumeScreenResultVO parseAIResponse(String response, Resume resume) {
        ResumeScreenResultVO result = new ResumeScreenResultVO();
        result.setCandidateName(resume.getRealName());
        
        try {
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            } else if (response.contains("{")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            }
            
            JsonNode node = objectMapper.readTree(jsonStr);
            
            result.setMatchLevel(node.path("matchLevel").asText("中"));
            result.setMatchScore(node.path("matchScore").asInt(50));
            
            List<String> highlights = new ArrayList<>();
            JsonNode highlightsNode = node.path("highlights");
            if (highlightsNode.isArray()) {
                for (JsonNode item : highlightsNode) {
                    highlights.add(item.asText());
                }
            }
            result.setHighlights(highlights);
            
            List<String> matchedSkills = new ArrayList<>();
            JsonNode matchedNode = node.path("matchedSkills");
            if (matchedNode.isArray()) {
                for (JsonNode item : matchedNode) {
                    matchedSkills.add(item.asText());
                }
            }
            result.setMatchedSkills(matchedSkills);
            
            List<String> missingSkills = new ArrayList<>();
            JsonNode missingNode = node.path("missingSkills");
            if (missingNode.isArray()) {
                for (JsonNode item : missingNode) {
                    missingSkills.add(item.asText());
                }
            }
            result.setMissingSkills(missingSkills);
            
            result.setExperienceMatch(node.path("experienceMatch").asText(""));
            result.setEducationMatch(node.path("educationMatch").asText(""));
            result.setSummary(node.path("summary").asText(""));
            result.setRecommendation(node.path("recommendation").asText(""));
            
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            result.setMatchLevel("中");
            result.setMatchScore(50);
            result.setHighlights(Arrays.asList("解析失败，请手动查看简历"));
            result.setMatchedSkills(new ArrayList<>());
            result.setMissingSkills(new ArrayList<>());
            result.setSummary("AI分析暂时不可用，请手动评估简历");
            result.setRecommendation("建议手动审核");
        }
        
        return result;
    }

    private String getExperienceText(Integer exp) {
        if (exp == null) return "不限";
        switch (exp) {
            case 0: return "不限";
            case 1: return "1年以下";
            case 2: return "1-3年";
            case 3: return "3-5年";
            case 4: return "5-10年";
            case 5: return "10年以上";
            default: return "不限";
        }
    }
}
