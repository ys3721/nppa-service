package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

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

    // FIXME: 2021/4/7 先放这儿吧
    public static final String AUTH_URL = "https://wlc.nppa.gov.cn/test/authentication/";

    private UserDao userDao;

    private IHttpClient httpClient;

    @Autowired
    public AuthenticationService(UserDao userDao, IHttpClient httpClient) {
        this.userDao = userDao;
        this.httpClient = httpClient;
    }


    public UserDao getUserDao() {
        return userDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * @// FIXME: 2021/4/7  这个地方其实nppa验证不通过不需要存库 省的来一大堆没用的非法数据
     * 按照id找这个人 如果没有的话就初始化一个并且把他存
     *
     * @param userId
     * @param gameId
     * @param name
     * @param idCard
     * @return
     */
    public User findOrBuildUser(long userId, int gameId, String name, String idCard) {
        User user = userDao.findById(userId);
        if (user != null) {
            return user;
        } else {
            user = this.buildInitializeUser(userId, gameId, name, idCard);
            this.userDao.save(user);
        }
        return user;
    }

    /**
     * 创建一个没有认证的默认的user
     *
     */
    private User buildInitializeUser(long userId, int gameId, String name, String idCard) {
        User welcomeUser = new User();
        welcomeUser.setAuthStatus((byte) AuthenticationStatus.INITIALIZE.getCode());
        welcomeUser.setId(userId);
        welcomeUser.setGameId(gameId);
        welcomeUser.setRealName(name);
        welcomeUser.setIdNumber(idCard);
        welcomeUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
        this.userDao.save(welcomeUser);
        return welcomeUser;
    }

    /**
     * 去平台认证一下这个人的实名是不是真的
     * String urlStr, Map<String, String> requestProperty, String postData
     * @param user
     */
    public void goNppaAuthCheck(User user) {
        String url = AUTH_URL + "/check/";
    }

    public void goNppaAuthQuery(User user) {

    }



}
