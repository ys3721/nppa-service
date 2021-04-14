package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 因为有需要去取的实名认证，当有在认证中的User时，
 * 会在他认证时间的每个小时取一次结果 其实渐进式等更好先这样吧
 *
 * @author yaoshuai
 * @date 2021-四月-13
 */
public class ScheduleAuthService {

    private IUserService userService;

    @Autowired
    public ScheduleAuthService(IUserService userService) {
        this.userService = userService;
    }


    public void queryAuthResult() {
        Collection<User> users = userService.findUsersByStatus(AuthenticationStatus.UNDER_WAY);
        for (User user : users) {
            //如果认证时间超过48小时 直接失败，等下次再登陆吧
            Timestamp timestamp = user.getAuthTime();
            if (System.currentTimeMillis() > timestamp.getTime() + TimeUnit.DAYS.toDays(2)) {
                user.setAuthStatus(AuthenticationStatus.FAIL);
                userService.saveOrUpdateUser(user);
            } else {
                //xxxxxxxxxxxxxxx去取结果吧 成功标成功 失败标失败 
            }

        }

    }

}
