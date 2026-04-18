package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.FavoriteDTO;
import com.xu.entity.Company;
import com.xu.entity.Favorite;
import com.xu.entity.Job;
import com.xu.common.BusinessException;
import com.xu.mapper.FavoriteMapper;
import com.xu.service.CompanyService;
import com.xu.service.FavoriteService;
import com.xu.service.JobService;
import com.xu.vo.FavoriteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final JobService jobService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public void addFavorite(FavoriteDTO dto, Long userId) {
        Job job = jobService.getById(dto.getJobId());
        if (job == null || job.getStatus() != 1) {
            throw new BusinessException("职位不存在或已下架");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getJobId, dto.getJobId())
               .eq(Favorite::getDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("您已收藏过该职位");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setJobId(dto.getJobId());
        favorite.setRemark(dto.getRemark());
        this.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long jobId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getJobId, jobId)
               .eq(Favorite::getDeleted, 0);
        Favorite favorite = this.getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException("收藏记录不存在");
        }
        favorite.setDeleted(1);
        this.updateById(favorite);
    }

    @Override
    @Transactional
    public void updateRemark(Long id, String remark, Long userId) {
        Favorite favorite = this.getById(id);
        if (favorite == null || !favorite.getUserId().equals(userId)) {
            throw new BusinessException("收藏记录不存在或无权操作");
        }
        favorite.setRemark(remark);
        this.updateById(favorite);
    }

    @Override
    public Page<FavoriteVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getDeleted, 0)
               .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> favoritePage = this.page(page, wrapper);

        List<Long> jobIds = favoritePage.getRecords().stream()
                .map(Favorite::getJobId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Job> jobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            List<Job> jobs = jobService.listByIds(jobIds);
            jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, j -> j));
        }

        List<Long> companyIds = jobMap.values().stream()
                .map(Job::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Job> finalJobMap = jobMap;
        final Map<Long, Company> finalCompanyMap = companyMap;

        Page<FavoriteVO> voPage = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
        List<FavoriteVO> voList = favoritePage.getRecords().stream()
                .map(fav -> {
                    FavoriteVO vo = new FavoriteVO();
                    vo.setId(fav.getId());
                    vo.setJobId(fav.getJobId());
                    vo.setRemark(fav.getRemark());
                    vo.setCreateTime(fav.getCreateTime());

                    Job job = finalJobMap.get(fav.getJobId());
                    if (job != null) {
                        vo.setJobTitle(job.getTitle());
                        vo.setCategory(job.getCategory());
                        vo.setLocation(job.getLocation());
                        vo.setSalaryMin(job.getSalaryMin());
                        vo.setSalaryMax(job.getSalaryMax());
                        vo.setSalaryRange(formatSalary(job.getSalaryMin(), job.getSalaryMax()));
                        vo.setExperience(getExperienceText(job.getExperience()));
                        vo.setEducation(job.getEducation());
                        vo.setJobStatus(job.getStatus());
                        vo.setJobUpdateTime(job.getUpdateTime());

                        Company company = finalCompanyMap.get(job.getCompanyId());
                        if (company != null) {
                            vo.setCompanyName(company.getName());
                            vo.setCompanyLogo(company.getLogo());
                        }
                    } else {
                        vo.setJobStatus(0);
                        vo.setJobTitle("职位已下架");
                    }

                    return vo;
                })
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public boolean isFavorited(Long jobId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getJobId, jobId)
               .eq(Favorite::getDeleted, 0);
        return this.count(wrapper) > 0;
    }

    private String formatSalary(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (min == null && max == null) return "面议";
        if (min == null) return max + "K以下";
        if (max == null) return min + "K以上";
        return min + "K-" + max + "K";
    }

    private String getExperienceText(Integer experience) {
        if (experience == null) return "不限";
        switch (experience) {
            case 0: return "不限";
            case 1: return "1年以下";
            case 2: return "1-3年";
            case 3: return "3-5年";
            case 4: return "5-10年";
            case 5: return "10年以上";
            default: return "不限";
        }
    }
}
