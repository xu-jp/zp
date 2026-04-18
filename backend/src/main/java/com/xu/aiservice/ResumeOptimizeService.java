package com.xu.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;


@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        streamingChatModel = "openAiStreamingChatModel"
)
public interface ResumeOptimizeService {

    @SystemMessage("""
        你是一个专业的简历优化专家，擅长从格式规范、完整性、关键词匹配、经历描述、技能展示 5 个维度分析简历并给出优化建议。
        你必须严格按照用户要求的JSON格式返回结果，不要包含任何其他内容。
        JSON字符串中的所有字符串值必须正确转义，换行符必须使用 \\n 转义，不能包含实际的换行符。
        """)
    String optimizeResume(@UserMessage String resumeInfo);

    @SystemMessage("""
        你是一个专业的简历优化专家，擅长从格式规范、完整性、关键词匹配、经历描述、技能展示 5 个维度分析简历并给出优化建议。
        你必须严格按照用户要求的JSON格式返回结果，不要包含任何其他内容。
        JSON字符串中的所有字符串值必须正确转义，换行符必须使用 \\n 转义，不能包含实际的换行符。
        """)
    Flux<String> optimizeResumeStream(@UserMessage String resumeInfo);
}
