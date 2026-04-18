package com.xu.controller;

import com.xu.annotation.RequireRole;
import com.xu.common.Result;
import com.xu.dto.ResumeDTO;
import com.xu.service.ResumeService;
import com.xu.vo.AIResumeOptimizeVO;
import com.xu.vo.ResumeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping("/list")
    @RequireRole(1)
    public Result<List<ResumeVO>> getUserResumes(@RequestAttribute Long userId) {
        List<ResumeVO> resumes = resumeService.getUserResumes(userId);
        return Result.success(resumes);
    }

    @GetMapping("/detail/{id}")
    @RequireRole(1)
    public Result<ResumeVO> getResumeDetail(@PathVariable Long id, @RequestAttribute Long userId) {
        ResumeVO resume = resumeService.getResumeDetail(id, userId);
        return Result.success(resume);
    }

    @PostMapping("/create")
    @RequireRole(1)
    public Result<Long> createResume(@RequestAttribute Long userId, @Valid @RequestBody ResumeDTO dto) {
        Long resumeId = resumeService.createResume(userId, dto);
        return Result.success(resumeId);
    }

    @PutMapping("/update")
    @RequireRole(1)
    public Result<Void> updateResume(@RequestAttribute Long userId, @Valid @RequestBody ResumeDTO dto) {
        resumeService.updateResume(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @RequireRole(1)
    public Result<Void> deleteResume(@PathVariable Long id, @RequestAttribute Long userId) {
        resumeService.deleteResume(id, userId);
        return Result.success();
    }

    @PutMapping("/set-default/{id}")
    @RequireRole(1)
    public Result<Void> setDefaultResume(@PathVariable Long id, @RequestAttribute Long userId) {
        resumeService.setDefaultResume(id, userId);
        return Result.success();
    }

    @PostMapping("/optimize/{id}")
    @RequireRole(1)
    public Result<AIResumeOptimizeVO> optimizeResume(@PathVariable Long id, @RequestAttribute Long userId) {
        AIResumeOptimizeVO result = resumeService.optimizeResume(id, userId);
        return Result.success(result);
    }

    @GetMapping(value = "/optimize-stream/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RequireRole(1)
    public SseEmitter optimizeResumeStream(@PathVariable Long id, @RequestAttribute Long userId) {
        SseEmitter emitter = new SseEmitter(180000L);
        
        executorService.execute(() -> {
            StringBuilder fullContent = new StringBuilder();
            AtomicInteger chunkCount = new AtomicInteger();
            try {
                log.info("开始处理流式输出");
                resumeService.optimizeResumeStream(id, userId)
                    .doOnNext(chunk -> {
                        chunkCount.getAndIncrement();
                        log.debug("收到第 {} 个chunk: {}", chunkCount, chunk != null ? chunk.substring(0, Math.min(50, chunk.length())) : "null");
                        fullContent.append(chunk != null ? chunk : "");
                        try {
                            emitter.send(SseEmitter.event()
                                .name("message")
                                .data(chunk));
                            log.debug("已发送chunk {} 到前端", chunkCount);
                        } catch (IOException e) {
                            log.error("发送SSE消息失败", e);
                            emitter.completeWithError(e);
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("流式输出完成，总chunk数: {}, 总内容长度: {}", chunkCount, fullContent.length());
                        log.debug("完整内容预览: {}", fullContent.length() > 100 ? fullContent.substring(0, 100) + "..." : fullContent);
                        try {
                            emitter.send(SseEmitter.event()
                                .name("complete")
                                .data("[DONE]"));
                            emitter.complete();
                            log.info("完成事件已发送");
                        } catch (IOException e) {
                            log.error("发送完成事件失败", e);
                            emitter.completeWithError(e);
                        }
                    })
                    .doOnError(error -> {
                        log.error("流式输出出错", error);
                        try {
                            emitter.send(SseEmitter.event()
                                .name("error")
                                .data("流式输出失败: " + error.getMessage()));
                        } catch (IOException e) {
                            log.error("发送错误事件失败", e);
                        }
                        emitter.completeWithError(error);
                    })
                    .subscribe();
            } catch (Exception e) {
                log.error("流式输出异常", e);
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("流式输出异常: " + e.getMessage()));
                } catch (IOException ioException) {
                    log.error("发送错误事件失败", ioException);
                }
                emitter.completeWithError(e);
            }
        });
        
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时");
            emitter.complete();
        });
        emitter.onError(e -> {
            log.error("SSE连接错误", e);
            emitter.complete();
        });
        
        return emitter;
    }
}
