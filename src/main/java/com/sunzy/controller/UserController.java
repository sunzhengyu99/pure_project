package com.sunzy.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.service.UserService;
import com.sunzy.common.Constants;
import com.sunzy.common.R;
import com.sunzy.controller.dto.UserDto;
import com.sunzy.entity.User;
import com.sunzy.utils.TokenUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口")
public class UserController {



    @Autowired
    private UserService userService;


    /**
     * 用户登录
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        // 校验用户提交数据是否为空
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return R.error(Constants.CODE_400, "参数错误！");
        }
        UserDto dto = userService.login(userDto);
        return R.success(dto);
    }


    /**
     * 用户登录
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        String confirmPassword = userDto.getConfirmPassword();
        // 校验用户提交数据是否为空
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(confirmPassword) || password.equals(confirmPassword)){
            return R.error(Constants.CODE_400, "参数错误！");
        }
        User user = userService.register(userDto);

        return R.success(user);
    }

    @GetMapping("/username/{name}")
    public R gerInfo(@PathVariable String name){
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(name != null, User::getUsername, name);
        User one = userService.getOne(qw);
        return R.success(one);
    }

    /**
     * 获取用户列表
     * @return
     */
    @GetMapping("/page")
    public Page<User> list(@RequestParam int currentPage, @RequestParam int pageSize,
                           @RequestParam(defaultValue = "")  String username,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String address) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAddress(address);
        // 设置分页数据
        Page<User> userPage = new Page<>(currentPage, pageSize);
        // 添加查询条件  名称 邮箱 地址
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.like(user.getUsername() != null, User::getUsername, user.getUsername());
        qw.like(user.getEmail() !=null, User::getEmail, user.getEmail());
        qw.like(user.getAddress() !=null, User::getAddress, user.getAddress());
        qw.orderByDesc(User::getId);

        // 获取当前用户信息
        User currentUser = TokenUtils.getCurrentUser();


        userService.page(userPage, qw);
        return userPage;
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping
    public R save(@RequestBody User user){
        return R.success(userService.saveUser(user));
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @PutMapping
    public boolean update(User user){
        return userService.saveUser(user);
    }


    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable Integer id){
        return userService.removeById(id);
    }

    /**
     * 根据id删除用户
     * @param ids
     * @return
     */
    @PostMapping("/del/batch")
    public boolean deleteByIds(@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
    }


    /**
     * 名单导出
     * @param response
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<User> list = userService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器的响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");

        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }


    /**
     * 导入信息
     * @param file
     */
    @PostMapping("/import")
    public boolean imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
//        List<User> users = reader.readAll(User.class);
        // 方式一 通过javabean读取execel表格中的内容转换为java对象
        List<User> users = reader.read(0, 1, User.class);
        for (User user : users) {
            System.out.println(user);
        }
        userService.saveBatch(users);
        return true;
    }




}
