package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xu.aiservice.ResumeOptimizeService;
import com.xu.common.BusinessException;
import com.xu.dto.ResumeDTO;
import com.xu.entity.Resume;
import com.xu.mapper.ResumeMapper;
import com.xu.service.ResumeService;
import com.xu.vo.AIResumeOptimizeVO;
import com.xu.vo.ResumeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private final ResumeOptimizeService resumeOptimizeService;
    private final ObjectMapper objectMapper;

    @Override
    public List<ResumeVO> getUserResumes(Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, userId)
               .eq(Resume::getDeleted, 0)
               .orderByDesc(Resume::getIsDefault)
               .orderByDesc(Resume::getUpdateTime);

        List<Resume> resumes = this.list(wrapper);
        return resumes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ResumeVO getResumeDetail(Long id, Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getId, id)
               .eq(Resume::getUserId, userId)
               .eq(Resume::getDeleted, 0);

        Resume resume = this.getOne(wrapper);
        if (resume == null) {
            throw new BusinessException(404, "简历不存在");
        }

        return convertToVO(resume);
    }

    @Override
    @Transactional
    public Long createResume(Long userId, ResumeDTO dto) {
        Resume resume = new Resume();
        BeanUtils.copyProperties(dto, resume);
        resume.setUserId(userId);
        resume.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);

        if (resume.getIsDefault() == 1) {
            clearDefaultResume(userId);
        }

        this.save(resume);

        log.info("用户 {} 创建简历: {}", userId, resume.getId());

        return resume.getId();
    }

    @Override
    @Transactional
    public void updateResume(Long userId, ResumeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(400, "简历ID不能为空");
        }

        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getId, dto.getId())
               .eq(Resume::getUserId, userId)
               .eq(Resume::getDeleted, 0);

        Resume resume = this.getOne(wrapper);
        if (resume == null) {
            throw new BusinessException(404, "简历不存在");
        }

        BeanUtils.copyProperties(dto, resume, "id", "userId", "createTime");

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefaultResume(userId);
        }

        this.updateById(resume);

        log.info("用户 {} 更新简历: {}", userId, resume.getId());
    }

    @Override
    @Transactional
    public void deleteResume(Long id, Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getId, id)
               .eq(Resume::getUserId, userId)
               .eq(Resume::getDeleted, 0);

        Resume resume = this.getOne(wrapper);
        if (resume == null) {
            throw new BusinessException(404, "简历不存在");
        }

        resume.setDeleted(1);
        this.updateById(resume);

        log.info("用户 {} 删除简历: {}", userId, id);
    }

    @Override
    @Transactional
    public void setDefaultResume(Long id, Long userId) {
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getId, id)
               .eq(Resume::getUserId, userId)
               .eq(Resume::getDeleted, 0);

        Resume resume = this.getOne(wrapper);
        if (resume == null) {
            throw new BusinessException(404, "简历不存在");
        }

        clearDefaultResume(userId);

        resume.setIsDefault(1);
        this.updateById(resume);

        log.info("用户 {} 设置默认简历: {}", userId, id);
    }

    @Override
    public AIResumeOptimizeVO optimizeResume(Long resumeId, Long userId) {
        ResumeVO resume = getResumeDetail(resumeId, userId);

        log.info("调用AI简历优化 - 用户: {}, 简历: {}", userId, resumeId);

        String prompt = buildOptimizePrompt(resume);
        
        try {
            String aiResponse = resumeOptimizeService.optimizeResume(prompt);
            
            if (aiResponse != null && !aiResponse.isEmpty()) {
                return parseOptimizeResponse(aiResponse, resume);
            } else {
                log.warn("AI响应为空，使用模拟数据");
                return generateMockOptimizeResult(resume);
            }
        } catch (Exception e) {
            log.error("调用AI服务失败，使用模拟数据", e);
            return generateMockOptimizeResult(resume);
        }
    }

    @Override
    public Flux<String> optimizeResumeStream(Long resumeId, Long userId) {
        ResumeVO resume = getResumeDetail(resumeId, userId);

        log.info("调用AI简历优化(流式) - 用户: {}, 简历: {}", userId, resumeId);

        String prompt = buildOptimizePrompt(resume);
        
        Flux<String> flux = resumeOptimizeService.optimizeResumeStream(prompt);
        
        return flux
            .doOnSubscribe(subscription -> log.debug("开始订阅流式输出"))
            .doOnNext(chunk -> {
                log.debug("收到流式chunk，长度: {}, 内容: {}", chunk != null ? chunk.length() : 0, 
                    chunk != null && chunk.length() > 100 ? chunk.substring(0, 100) + "..." : chunk);
            })
            .doOnComplete(() -> log.info("流式输出正常完成"))
            .doOnError(error -> log.error("流式输出出错", error))
            .map(chunk -> {
                if (chunk == null) {
                    return "";
                }
                return chunk;
            });
    }

    private void clearDefaultResume(Long userId) {
        LambdaUpdateWrapper<Resume> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Resume::getUserId, userId)
                     .eq(Resume::getDeleted, 0)
                     .set(Resume::getIsDefault, 0);
        this.update(updateWrapper);
    }

    private ResumeVO convertToVO(Resume resume) {
        ResumeVO vo = new ResumeVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }

    private String buildOptimizePrompt(ResumeVO resume) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下简历信息，从5个维度进行分析并给出优化建议。\n\n");
        
        prompt.append("【简历信息】\n");
        prompt.append("姓名：").append(resume.getRealName() != null ? resume.getRealName() : "未提供").append("\n");
        prompt.append("学历：").append(resume.getEducation() != null ? resume.getEducation() : "未提供").append("\n");
        prompt.append("学校：").append(resume.getSchool() != null ? resume.getSchool() : "未提供").append("\n");
        prompt.append("专业技能：").append(resume.getSkills() != null ? resume.getSkills() : "未提供").append("\n");
        prompt.append("工作经历：").append(resume.getWorkExperience() != null ? resume.getWorkExperience() : "未提供").append("\n");
        prompt.append("项目经验：").append(resume.getProjectExperience() != null ? resume.getProjectExperience() : "未提供").append("\n");
        prompt.append("自我介绍：").append(resume.getSelfIntroduction() != null ? resume.getSelfIntroduction() : "未提供").append("\n");
        prompt.append("所在城市：").append(resume.getLocation() != null ? resume.getLocation() : "未提供").append("\n");
        
        prompt.append("\n【分析维度】\n");
        prompt.append("1. 格式规范：检查简历排版、结构、字体等格式问题\n");
        prompt.append("2. 完整性：评估简历内容的完整性，是否缺少关键信息\n");
        prompt.append("3. 关键词匹配：分析简历关键词与目标职位的匹配度\n");
        prompt.append("4. 经历描述：评估工作经历和项目经验的描述质量\n");
        prompt.append("5. 技能展示：检查技能部分的展示效果和专业性\n");
        
        prompt.append("\n【输出要求】\n");
        prompt.append("请按以下JSON格式返回分析结果，不要包含其他内容：\n");
        prompt.append("{\n");
        prompt.append("  \"suggestions\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"dimension\": \"维度名称\",\n");
        prompt.append("      \"score\": 0-100的分数,\n");
        prompt.append("      \"level\": \"优秀/良好/中等/较差\",\n");
        prompt.append("      \"description\": \"该维度的总体评价\",\n");
        prompt.append("      \"suggestions\": [\"建议1\", \"建议2\", \"建议3\"]\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"overallScore\": \"总分（格式：XX分）\",\n");
        prompt.append("  \"summary\": \"综合评价（100字以内）\"\n");
        prompt.append("}\n");
        
        return prompt.toString();
    }

    private AIResumeOptimizeVO parseOptimizeResponse(String response, ResumeVO resume) {
        try {
            String jsonStr = response;
            if (response.contains("```json")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            } else if (response.contains("{")) {
                jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            }
            
            JsonNode node = objectMapper.readTree(jsonStr);
            
            List<AIResumeOptimizeVO.OptimizeSuggestion> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = node.path("suggestions");
            if (suggestionsNode.isArray()) {
                for (JsonNode suggestionNode : suggestionsNode) {
                    String dimension = suggestionNode.path("dimension").asText("");
                    int score = suggestionNode.path("score").asInt(0);
                    String level = suggestionNode.path("level").asText("中等");
                    String description = suggestionNode.path("description").asText("");
                    
                    List<String> suggestionsList = new ArrayList<>();
                    JsonNode suggestionsArr = suggestionNode.path("suggestions");
                    if (suggestionsArr.isArray()) {
                        for (JsonNode item : suggestionsArr) {
                            suggestionsList.add(item.asText());
                        }
                    }
                    
                    AIResumeOptimizeVO.OptimizeSuggestion suggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
                        dimension, score, level, description, suggestionsList
                    );
                    suggestions.add(suggestion);
                }
            }
            
            String overallScore = node.path("overallScore").asText("0分");
            String summary = node.path("summary").asText("");
            
            return new AIResumeOptimizeVO(suggestions, overallScore, summary);
            
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return generateMockOptimizeResult(resume);
        }
    }

    private AIResumeOptimizeVO generateMockOptimizeResult(ResumeVO resume) {
        Random random = new Random();

        AIResumeOptimizeVO.OptimizeSuggestion formatSuggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
            "格式规范",
            75 + random.nextInt(20),
            "良好",
            "简历整体格式较为规范，结构清晰，便于阅读。",
            Arrays.asList(
                "建议使用更专业的简历模板",
                "各模块之间可以增加适当的分隔线",
                "标题字体可以适当加粗突出"
            )
        );

        AIResumeOptimizeVO.OptimizeSuggestion completenessSuggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
            "完整性",
            70 + random.nextInt(25),
            "良好",
            "简历内容基本完整，涵盖了主要信息。",
            Arrays.asList(
                "建议补充项目经验的具体成果和数据",
                "可以添加更多技能证书信息",
                "工作经历建议按时间倒序排列"
            )
        );

        AIResumeOptimizeVO.OptimizeSuggestion keywordSuggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
            "关键词匹配",
            65 + random.nextInt(30),
            "中等",
            "简历包含一定的行业关键词，但可以进一步优化。",
            Arrays.asList(
                "建议根据目标职位添加更多相关关键词",
                "技能描述可以使用更专业的术语",
                "工作经历中可以突出与目标职位相关的关键词"
            )
        );

        AIResumeOptimizeVO.OptimizeSuggestion experienceSuggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
            "经历描述",
            70 + random.nextInt(25),
            "良好",
            "工作经历描述较为详细，但可以更加突出成果。",
            Arrays.asList(
                "建议使用STAR法则描述工作经历",
                "添加具体的项目成果和数据支撑",
                "突出个人在项目中的贡献和角色"
            )
        );

        AIResumeOptimizeVO.OptimizeSuggestion skillsSuggestion = new AIResumeOptimizeVO.OptimizeSuggestion(
            "技能展示",
            60 + random.nextInt(35),
            "中等",
            "技能描述较为简洁，建议补充更多细节。",
            Arrays.asList(
                "建议将技能按熟练程度分类展示",
                "添加技能相关的项目经验或证书",
                "突出与目标职位匹配的核心技能"
            )
        );

        int totalScore = (formatSuggestion.getScore() + completenessSuggestion.getScore() +
                keywordSuggestion.getScore() + experienceSuggestion.getScore() +
                skillsSuggestion.getScore()) / 5;

        String overallScore = totalScore + "分";
        String summary = String.format("您的简历整体评分为%s，处于%s水平。主要优势在于格式规范和内容完整，" +
                "建议重点优化关键词匹配和技能展示部分，以提高简历与目标职位的匹配度。",
                overallScore, totalScore >= 80 ? "优秀" : totalScore >= 60 ? "良好" : "一般");

        return new AIResumeOptimizeVO(
            Arrays.asList(formatSuggestion, completenessSuggestion, keywordSuggestion,
                    experienceSuggestion, skillsSuggestion),
            overallScore,
            summary
        );
    }
}
