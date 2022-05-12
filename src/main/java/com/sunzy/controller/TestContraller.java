package com.sunzy.controller;


import com.sunzy.service.UserService;
import com.sunzy.entity.User;
import com.sunzy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//@RestController
//@RequestMapping("/user")
public class TestContraller {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> index(){
        List<User> list = userService.list();
        return list;
    }
}
