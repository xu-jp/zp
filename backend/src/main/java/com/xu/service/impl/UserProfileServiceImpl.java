package com.xu.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xu.dto.ChangePasswordDTO;
import com.xu.dto.UserProfileDTO;
import com.xu.entity.User;
import com.xu.service.UserService;
import com.xu.service.UserProfileService;
import com.xu.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserService userService;

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setAge(user.getAge());
        vo.setUserType(user.getUserType());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(UserProfileDTO dto, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        userService.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO dto, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        if (BCrypt.checkpw(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        user.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        userService.updateById(user);
    }

    @Override
    @Transactional
    public void bindPhone(String phone, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone)
               .ne(User::getId, userId);
        if (userService.count(wrapper) > 0) {
            throw new RuntimeException("该手机号已被使用");
        }

        user.setPhone(phone);
        userService.updateById(user);
    }

    @Override
    @Transactional
    public void bindEmail(String email, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email)
               .ne(User::getId, userId);
        if (userService.count(wrapper) > 0) {
            throw new RuntimeException("该邮箱已被使用");
        }

        user.setEmail(email);
        userService.updateById(user);
    }
}
