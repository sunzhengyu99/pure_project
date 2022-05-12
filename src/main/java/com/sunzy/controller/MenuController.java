package com.sunzy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.common.R;
import com.sunzy.entity.Dict;
import com.sunzy.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.sunzy.service.IMenuService;
import com.sunzy.entity.Menu;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2022-05-11
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private DictMapper dictMapper;

    @Resource
    private IMenuService menuService;

    // 新增或者更新
    @PostMapping
    public R save(@RequestBody Menu menu) {
        return R.success(menuService.saveOrUpdate(menu));
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return menuService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return menuService.removeByIds(ids);
    }

    @GetMapping
    public R findAll(@RequestParam(defaultValue = "") String name) {
        return R.success(menuService.findMenu(name));
    }

    @GetMapping("/{id}")
    public Menu findOne(@PathVariable Integer id) {
        return menuService.getById(id);
    }

    @GetMapping("/page")
    public Page<Menu> findPage(
            @RequestParam String name,
            @RequestParam Integer currentPage,
            @RequestParam Integer pageSize) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Menu::getName, name);
        queryWrapper.orderByDesc(Menu::getId);
        return menuService.page(new Page<>(currentPage, pageSize), queryWrapper);
    }


    /**
     * 获取icons数据
     * @return
     */
    @RequestMapping("/ids")
    public R icons(){
        List<Dict> dicts = dictMapper.selectList(null);
        return R.success(dicts);
    }

}

