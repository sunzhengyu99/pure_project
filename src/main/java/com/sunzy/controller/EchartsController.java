package com.sunzy.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.Quarter;
import com.sunzy.common.R;
import com.sunzy.entity.User;
import com.sunzy.service.UserService;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Autowired
    private UserService userService;

    @GetMapping("/example")
    public R example(){

        Map<String, Object> map = new HashMap<>();
        map.put("x",  CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));


        return R.success(map);
    }


    @GetMapping("/members")
    public R members(){
        List<User> users = userService.list();
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;

        for (User user : users) {
            Date createTime = user.getCreateTime();

            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter){
                case Q1: q1 += 1; break;
                case Q2: q2 += 1; break;
                case Q3: q3 += 1; break;
                case Q4: q4 += 1; break;
                default: break;
            }
        }
        return R.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }
}
