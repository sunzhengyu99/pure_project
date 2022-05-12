package com.sunzy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.common.Constants;
import com.sunzy.controller.dto.UserDto;
import com.sunzy.entity.Menu;
import com.sunzy.entity.Role;
import com.sunzy.entity.User;
import com.sunzy.exception.ServiceException;
import com.sunzy.mapper.RoleMapper;
import com.sunzy.mapper.RoleMenuMapper;
import com.sunzy.mapper.UserMapper;
import com.sunzy.service.IMenuService;
import com.sunzy.service.UserService;
import com.sunzy.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Log LOG = Log.get();

    @Autowired
    private IMenuService menuService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private RoleMapper roleMapper;
    
    
    @Override
    public boolean saveUser(User user) {
        if(user != null){
            saveOrUpdate(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDto login(UserDto userDto) {
        User one = getUserInfo(userDto);
        if (one != null) {
            BeanUtil.copyProperties(one, userDto, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDto.setToken(token);

            String role = one.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDto.setMenus(roleMenus);
            return userDto;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    private List<Menu> getRoleMenus(String role) {
        LambdaQueryWrapper<Role> qw = new LambdaQueryWrapper<>();
        qw.eq(role!=null, Role::getName, role);
        Role one = roleMapper.selectOne(qw);
        Integer roleId = one.getId();

        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.getRoleMenu(roleId);
        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenu("");
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;

    }

    @Override
    public User register(UserDto userDto) {
        User one = getUserInfo(userDto);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDto, one, true);
            // 默认一个普通用户的角色
//            one.setRole(RoleEnum.ROLE_USER.toString());
            save(one);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return one;
    }

    private User getUserInfo(UserDto userDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDto.getUsername());
        queryWrapper.eq("password", userDto.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }


}
