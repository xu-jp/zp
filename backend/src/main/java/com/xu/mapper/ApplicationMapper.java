package com.xu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    @Select("SELECT j.category, COUNT(*) as count FROM sys_application a " +
            "JOIN sys_job j ON a.job_id = j.id " +
            "WHERE a.deleted = 0 " +
            "GROUP BY j.category " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectApplicationByCategory();

    @Select("SELECT c.industry, COUNT(*) as count FROM sys_application a " +
            "JOIN sys_job j ON a.job_id = j.id " +
            "JOIN sys_company c ON j.company_id = c.id " +
            "WHERE a.deleted = 0 " +
            "GROUP BY c.industry " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectApplicationByIndustry();
}
