package com.xu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BusinessException;
import com.xu.dto.UserStatusDTO;
import com.xu.entity.Company;
import com.xu.entity.User;
import com.xu.mapper.UserMapper;
import com.xu.service.AdminUserService;
import com.xu.service.CompanyService;
import com.xu.service.OperationLogService;
import com.xu.vo.AdminUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;
    private final CompanyService companyService;
    private final OperationLogService operationLogService;

    @Override
    public Page<AdminUserVO> getUserList(Integer userType, Integer status, String keyword, Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getEmail, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        List<Long> companyIds = userPage.getRecords().stream()
                .filter(u -> u.getUserType() == 2 && u.getCompanyId() != null)
                .map(User::getCompanyId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Company> companyMap = new HashMap<>();
        if (!companyIds.isEmpty()) {
            List<Company> companies = companyService.listByIds(companyIds);
            companyMap = companies.stream().collect(Collectors.toMap(Company::getId, c -> c));
        }

        final Map<Long, Company> finalCompanyMap = companyMap;

        Page<AdminUserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<AdminUserVO> voList = userPage.getRecords().stream()
                .map(user -> convertToVO(user, finalCompanyMap.get(user.getCompanyId())))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void updateUserStatus(UserStatusDTO dto, Long operatorId) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getUserType() == 3) {
            throw new BusinessException("不能修改管理员状态");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, dto.getUserId())
                .set(User::getStatus, dto.getStatus())
                .set(User::getRemark, dto.getRemark());
        userMapper.update(null, updateWrapper);

        User operator = userMapper.selectById(operatorId);
        String operation = dto.getStatus() == 1 ? "封禁用户" : "解封用户";
        operationLogService.log(operatorId, operator != null ? operator.getUsername() : null,
                operation, "updateUserStatus", "userId=" + dto.getUserId() + ",status=" + dto.getStatus(),
                0, null);
    }

    @Override
    public AdminUserVO getUserDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Company company = null;
        if (user.getUserType() == 2 && user.getCompanyId() != null) {
            company = companyService.getById(user.getCompanyId());
        }

        return convertToVO(user, company);
    }

    private AdminUserVO convertToVO(User user, Company company) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setUserType(user.getUserType());
        vo.setUserTypeText(getUserTypeText(user.getUserType()));
        vo.setStatus(user.getStatus());
        vo.setStatusText(user.getStatus() == 0 ? "正常" : "禁用");
        vo.setCompanyId(user.getCompanyId());
        vo.setRemark(user.getRemark());
        vo.setCreateTime(user.getCreateTime());

        if (company != null) {
            vo.setCompanyName(company.getName());
        }

        return vo;
    }

    private String getUserTypeText(Integer userType) {
        switch (userType) {
            case 1: return "求职者";
            case 2: return "招聘者";
            case 3: return "管理员";
            default: return "未知";
        }
    }
}
