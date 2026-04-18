package com.xu.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CompanyStatisticsVO {

    private Long totalApplications;

    private Long pendingApplications;

    private Long interviewApplications;

    private Long hiredApplications;

    private Long totalJobs;

    private Long activeJobs;

    private List<Map<String, Object>> applicationTrend;

    private List<Map<String, Object>> jobApplicationCount;

    private Double conversionRate;
}
