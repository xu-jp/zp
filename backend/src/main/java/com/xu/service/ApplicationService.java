package com.xu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.ApplicationDTO;
import com.xu.entity.Application;
import com.xu.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService {

    void apply(ApplicationDTO dto, Long userId);

    Page<ApplicationVO> getUserApplications(Long userId, Integer status, Integer pageNum, Integer pageSize);

    ApplicationVO getApplicationDetail(Long id, Long userId);

    void cancelApplication(Long id, Long userId);

    boolean checkApplied(Long jobId, Long userId);

    Page<Application> page(Page<Application> page, LambdaQueryWrapper<Application> wrapper);

    Application getById(Long id);

    boolean updateById(Application application);

    List<Application> list(LambdaQueryWrapper<Application> wrapper);

    long count(LambdaQueryWrapper<Application> wrapper);
}
