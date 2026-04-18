package com.xu.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "openAiChatModel"
)
public interface ResumeScreenService {

    @SystemMessage("""
        你是一个专业的HR简历筛选助手，擅长分析简历与职位的匹配度。
        你必须严格按照用户要求的JSON格式返回结果，不要包含任何其他内容。
        JSON字符串中的所有字符串值必须正确转义，换行符必须使用 \\n 转义，不能包含实际的换行符。
        """)
    String screenResume(@UserMessage String prompt);
}
