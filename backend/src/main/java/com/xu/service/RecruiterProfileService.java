package com.xu.service;

import com.xu.dto.ChangePasswordDTO;
import com.xu.dto.RecruiterProfileDTO;
import com.xu.entity.User;
import com.xu.vo.RecruiterProfileVO;

public interface RecruiterProfileService {

    RecruiterProfileVO getProfile(Long userId);

    void updateProfile(RecruiterProfileDTO dto, Long userId);

    void changePassword(ChangePasswordDTO dto, Long userId);

    void bindPhone(String phone, Long userId);

    void bindEmail(String email, Long userId);
}
