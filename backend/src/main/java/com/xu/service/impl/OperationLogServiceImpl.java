package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.entity.OperationLog;
import com.xu.mapper.OperationLogMapper;
import com.xu.service.OperationLogService;
import com.xu.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public void log(Long userId, String username, String operation, String method, String params, Integer status, String errorMsg) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setCreateTime(LocalDateTime.now());
        log.setDeleted(0);
        operationLogMapper.insert(log);
    }

    @Override
    public Page<OperationLogVO> getLogList(String operation, Long userId, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(operation)) {
            wrapper.like(OperationLog::getOperation, operation);
        }
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        if (StringUtils.hasText(startDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
            wrapper.ge(OperationLog::getCreateTime, start);
        }
        if (StringUtils.hasText(endDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);
            wrapper.le(OperationLog::getCreateTime, end);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> logPage = operationLogMapper.selectPage(page, wrapper);

        Page<OperationLogVO> voPage = new Page<>(logPage.getCurrent(), logPage.getSize(), logPage.getTotal());
        List<OperationLogVO> voList = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    private OperationLogVO convertToVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setUserId(log.getUserId());
        vo.setUsername(log.getUsername());
        vo.setOperation(log.getOperation());
        vo.setMethod(log.getMethod());
        vo.setParams(log.getParams());
        vo.setStatus(log.getStatus());
        vo.setStatusText(log.getStatus() == 0 ? "成功" : "失败");
        vo.setErrorMsg(log.getErrorMsg());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
