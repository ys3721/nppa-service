package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.pojo.User;

import java.util.Collection;

/**
 * @author: Yao Shuai
 * @date: 2021/4/9 19:46
 */
public interface IUserService {

    User findUserByPassportId(Long id);

    void saveOrUpdateUser(User user);

    Collection<User> findUsersByStatus(AuthenticationStatus underWay);

}
