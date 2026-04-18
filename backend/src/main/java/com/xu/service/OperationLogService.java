package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.vo.OperationLogVO;

public interface OperationLogService {

    void log(Long userId, String username, String operation, String method, String params, String ip, Integer status, String errorMsg);

    Page<OperationLogVO> getLogList(String operation, Long userId, String startDate, String endDate, Integer pageNum, Integer pageSize);
}
