package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xu.entity.Application;
import com.xu.entity.Company;
import com.xu.entity.Job;
import com.xu.entity.User;
import com.xu.mapper.ApplicationMapper;
import com.xu.mapper.CompanyMapper;
import com.xu.mapper.JobMapper;
import com.xu.mapper.UserMapper;
import com.xu.service.DashboardService;
import com.xu.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;
    private final JobMapper jobMapper;
    private final ApplicationMapper applicationMapper;

    @Override
    public DashboardVO getDashboardData() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return buildDashboardData(todayStart, null);
    }

    @Override
    public DashboardVO getDashboardDataByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(LocalTime.MAX);
        return buildDashboardData(start, end);
    }

    private DashboardVO buildDashboardData(LocalDateTime start, LocalDateTime end) {
        DashboardVO vo = new DashboardVO();

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeleted, 0);
        vo.setUserCount(userMapper.selectCount(userWrapper));

        LambdaQueryWrapper<Company> companyWrapper = new LambdaQueryWrapper<>();
        companyWrapper.eq(Company::getDeleted, 0);
        vo.setCompanyCount(companyMapper.selectCount(companyWrapper));

        LambdaQueryWrapper<Job> jobWrapper = new LambdaQueryWrapper<>();
        jobWrapper.eq(Job::getDeleted, 0);
        vo.setJobCount(jobMapper.selectCount(jobWrapper));

        LambdaQueryWrapper<Application> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(Application::getDeleted, 0);
        vo.setApplicationCount(applicationMapper.selectCount(appWrapper));

        LambdaQueryWrapper<Application> hiredWrapper = new LambdaQueryWrapper<>();
        hiredWrapper.eq(Application::getDeleted, 0)
                .eq(Application::getStatus, 3);
        vo.setHiredCount(applicationMapper.selectCount(hiredWrapper));

        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.eq(User::getDeleted, 0)
                .ge(User::getCreateTime, start);
        vo.setTodayUserCount(userMapper.selectCount(todayUserWrapper));

        LambdaQueryWrapper<Application> todayAppWrapper = new LambdaQueryWrapper<>();
        todayAppWrapper.eq(Application::getDeleted, 0)
                .ge(Application::getCreateTime, start);
        vo.setTodayApplicationCount(applicationMapper.selectCount(todayAppWrapper));

        vo.setApplicationByCategory(getApplicationByCategory());
        vo.setApplicationByIndustry(getApplicationByIndustry());
        vo.setApplicationByDate(getApplicationByDate(7));
        vo.setUserTrend(getUserTrend(7));

        return vo;
    }

    private List<DashboardVO.ChartDataVO> getApplicationByCategory() {
        List<Map<String, Object>> results = applicationMapper.selectApplicationByCategory();
        return results.stream().map(m -> {
            DashboardVO.ChartDataVO vo = new DashboardVO.ChartDataVO();
            vo.setName((String) m.get("category"));
            vo.setValue(((Number) m.get("count")).longValue());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<DashboardVO.ChartDataVO> getApplicationByIndustry() {
        List<Map<String, Object>> results = applicationMapper.selectApplicationByIndustry();
        return results.stream().map(m -> {
            DashboardVO.ChartDataVO vo = new DashboardVO.ChartDataVO();
            vo.setName((String) m.get("industry"));
            vo.setValue(((Number) m.get("count")).longValue());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<DashboardVO.ChartDataVO> getApplicationByDate(int days) {
        List<DashboardVO.ChartDataVO> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Application::getDeleted, 0)
                    .between(Application::getCreateTime, dayStart, dayEnd);
            Long count = applicationMapper.selectCount(wrapper);

            DashboardVO.ChartDataVO vo = new DashboardVO.ChartDataVO();
            vo.setName(date.format(formatter));
            vo.setValue(count);
            list.add(vo);
        }
        return list;
    }

    private List<DashboardVO.ChartDataVO> getUserTrend(int days) {
        List<DashboardVO.ChartDataVO> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getDeleted, 0)
                    .between(User::getCreateTime, dayStart, dayEnd);
            Long count = userMapper.selectCount(wrapper);

            DashboardVO.ChartDataVO vo = new DashboardVO.ChartDataVO();
            vo.setName(date.format(formatter));
            vo.setValue(count);
            list.add(vo);
        }
        return list;
    }
}
