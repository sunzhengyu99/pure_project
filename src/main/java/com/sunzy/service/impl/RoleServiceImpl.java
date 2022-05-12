package com.sunzy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.entity.Role;
import com.sunzy.entity.RoleMenu;
import com.sunzy.mapper.RoleMapper;
import com.sunzy.mapper.RoleMenuMapper;
import com.sunzy.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2022-05-11
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        LambdaQueryWrapper<RoleMenu> qw = new LambdaQueryWrapper<>();
        qw.eq(RoleMenu::getRoleId, roleId);
        // 先删除用户已经绑定的菜单
        roleMenuMapper.delete(qw);
        // 添加新的绑定关系
        // 将menuId中是数据与roleId绑定
        for (Integer menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }


    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {

//        LambdaQueryWrapper<RoleMenu> qw = new LambdaQueryWrapper<>();
//        qw.eq(RoleMenu::getRoleId, roleId);
        return roleMenuMapper.getRoleMenu(roleId);
    }
}
