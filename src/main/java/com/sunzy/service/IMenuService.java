package com.sunzy.service;

import com.sunzy.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2022-05-11
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenu(String name);
}
