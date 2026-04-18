package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.dto.UserStatusDTO;
import com.xu.vo.AdminUserVO;

public interface AdminUserService {

    Page<AdminUserVO> getUserList(Integer userType, Integer status, String keyword, Integer pageNum, Integer pageSize);

    void updateUserStatus(UserStatusDTO dto, Long operatorId);

    AdminUserVO getUserDetail(Long id);
}
