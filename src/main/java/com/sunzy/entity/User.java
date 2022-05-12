package com.sunzy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

@Data
@TableName("sys_user")
public class User {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;
    private String nickname;
    private String password;
    private String email;
    private String address;

    private String phone;
    private Date createTime;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("角色")
    private String role;

//    @TableField(exist = false)
//    private List<Course> courses;
//
//    @TableField(exist = false)
//    private List<Course> stuCourses;

}
