package com.sunzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunzy.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("select menu_id from sys_role_menu where role_id = #{roleId}")
    List<Integer> getRoleMenu(@Param("roleId") Integer roleId);

//    List<Integer> selectByRoleId(Integer roleId);
}
