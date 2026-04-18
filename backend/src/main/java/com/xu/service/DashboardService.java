package com.xu.service;

import com.xu.vo.DashboardVO;

public interface DashboardService {

    DashboardVO getDashboardData();

    DashboardVO getDashboardDataByDateRange(String startDate, String endDate);
}
