package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 首先如果user对象不存在的话那么就创建一个
 * 这个类要去nppa的服务器把实名认证作了
 *
 * 认证相应有三种结果，成功，失败，和认证中 三个结果
 *
 * @author: Yao Shuai
 * @date: 2021/4/6 20:08
 */
@Component
public class AuthenticationService {

    private UserDao userDao;

    @Autowired
    public AuthenticationService(UserDao userDao) {
        this.userDao = userDao;
    }


}
