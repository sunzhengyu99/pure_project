package com.sunzy.controller.dto;

import com.sunzy.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * 接收用户数据
 */
@Data
public class UserDto {

    private String username;

    private String password;
    private String confirmPassword;
    private String nickname;
    private String avatarUrl;

    private String token;
    private String role;

    private List<Menu> menus;
}
