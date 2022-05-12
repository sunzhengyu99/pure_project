package com.sunzy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.service.UserService;
import com.sunzy.common.Constants;
import com.sunzy.controller.dto.UserDto;
import com.sunzy.entity.User;
import com.sunzy.exception.ServiceException;
import com.sunzy.mapper.UserMapper;
import com.sunzy.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public static final Log LOG = Log.get();
    @Autowired
    private UserMapper userMapper;

//    private TokenUtil tokenUtil;


    @Override
    public boolean saveUser(User user) {
        return saveOrUpdate(user);
    }

    @Override
    public UserDto login(UserDto userDto) {


        User one = getUserInfo(userDto);

        if(one != null) {
            BeanUtil.copyProperties(one, userDto, true);
            String token = TokenUtils.genToken(one.getUsername(), one.getPassword());
            userDto.setToken(token);
            return userDto;
        }else{
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误！");
        }
    }

    @Override
    public User register(UserDto userDto) {
        User one = getUserInfo(userDto);

        if(one == null) {
            one = new User();
            BeanUtil.copyProperties(userDto, one, true);
            save(one);
        }else{
            throw new ServiceException(Constants.CODE_600, "用户已存在！");
        }
        return one;
    }

    private User getUserInfo(UserDto userDto){
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, userDto.getUsername());
        qw.eq(User::getPassword, userDto.getPassword());
        User one;
        try {
            one = this.getOne(qw);
        }catch (Exception e){
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }


}
