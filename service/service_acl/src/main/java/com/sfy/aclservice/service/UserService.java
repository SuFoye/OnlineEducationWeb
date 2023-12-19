package com.sfy.aclservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sfy.aclservice.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author testjava
 *
 */
public interface UserService extends IService<User> {

    // 从数据库中取出用户信息
    com.sfy.aclservice.entity.User selectByUsername(String username);
}
