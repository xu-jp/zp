package com.xu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.entity.User;

public interface UserService extends IService<User> {
    User getByUsername(String username);

    User getByPhone(String phone);

    User getByEmail(String email);
}
