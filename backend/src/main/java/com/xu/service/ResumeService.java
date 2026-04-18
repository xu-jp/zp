package com.xu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.ResumeDTO;
import com.xu.entity.Resume;
import com.xu.vo.AIResumeOptimizeVO;
import com.xu.vo.ResumeVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ResumeService extends IService<Resume> {

    List<ResumeVO> getUserResumes(Long userId);

    ResumeVO getResumeDetail(Long id, Long userId);

    Long createResume(Long userId, ResumeDTO dto);

    void updateResume(Long userId, ResumeDTO dto);

    void deleteResume(Long id, Long userId);

    void setDefaultResume(Long id, Long userId);

    AIResumeOptimizeVO optimizeResume(Long resumeId, Long userId);

    Flux<String> optimizeResumeStream(Long resumeId, Long userId);
}
