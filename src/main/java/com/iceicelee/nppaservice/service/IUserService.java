package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.pojo.User;

/**
 * @author: Yao Shuai
 * @date: 2021/4/9 19:46
 */
public interface IUserService {

    User findUserByPassportId(Long id);

}
