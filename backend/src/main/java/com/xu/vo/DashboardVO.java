package com.xu.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {

    private Long userCount;

    private Long companyCount;

    private Long jobCount;

    private Long applicationCount;

    private Long hiredCount;

    private Long todayUserCount;

    private Long todayApplicationCount;

    private List<ChartDataVO> applicationByCategory;

    private List<ChartDataVO> applicationByIndustry;

    private List<ChartDataVO> applicationByDate;

    private List<ChartDataVO> userTrend;

    @Data
    public static class ChartDataVO {
        private String name;
        private Long value;
    }
}
