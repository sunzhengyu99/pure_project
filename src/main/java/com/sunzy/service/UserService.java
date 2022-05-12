package com.sunzy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunzy.controller.dto.UserDto;
import com.sunzy.entity.User;

public interface UserService extends IService<User> {


    public boolean saveUser(User user);

    public UserDto login(UserDto userDto);

    public User register(UserDto userDto);
}
