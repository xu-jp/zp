package com.xu.service;

import com.xu.dto.ChangePasswordDTO;
import com.xu.dto.UserProfileDTO;
import com.xu.vo.UserProfileVO;
import com.xu.vo.UserStatsVO;

public interface UserProfileService {

    UserProfileVO getProfile(Long userId);

    void updateProfile(UserProfileDTO dto, Long userId);

    void changePassword(ChangePasswordDTO dto, Long userId);

    void bindPhone(String phone, Long userId);

    void bindEmail(String email, Long userId);

    UserStatsVO getUserStats(Long userId);
}
