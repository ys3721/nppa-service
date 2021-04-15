package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.http.request.AuthenticationQueryRequest;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import com.iceicelee.nppaservice.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 因为有需要去取的实名认证，当有在认证中的User时，
 * 会在他认证时间的每个小时取一次结果 其实渐进式等更好先这样吧
 *
 * @author yaoshuai
 * @date 2021-四月-13
 */
@Component
public class ScheduleAuthService {

    private IUserService userService;

    private AuthenticationService authService;

    @Autowired
    public ScheduleAuthService(IUserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
        this.start();
    }

    private void start() {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleWithFixedDelay(this::queryAuthResult, 15, 15, TimeUnit.SECONDS);
    }


    public void queryAuthResult() {
        Collection<User> users = userService.findUsersByStatus(AuthenticationStatus.UNDER_WAY);
        System.out.println("开始检查角色的实名状态");
        for (User user : users) {
            //如果认证时间超过48小时 直接失败，等下次再登陆吧
            Timestamp timestamp = user.getAuthTime();
            if (System.currentTimeMillis() > timestamp.getTime() + TimeUnit.DAYS.toMillis(2)) {
                user.setAuthStatus(AuthenticationStatus.FAIL);
                userService.saveOrUpdateUser(user);
            } else {
                AuthenticationStatus status = this.goNppaQueryResult(user);
                if (AuthenticationStatus.FAIL == status) {
                    //失败 标记失败
                    user.setAuthStatus(AuthenticationStatus.FAIL);
                    user.setAuthTime(new Timestamp(System.currentTimeMillis()));
                    userService.saveOrUpdateUser(user);
                }
                if (AuthenticationStatus.SUCCESS == status) {
                    user.setAuthStatus(AuthenticationStatus.SUCCESS);
                    user.setAuthTime(new Timestamp(System.currentTimeMillis()));
                    userService.saveOrUpdateUser(user);
                }
            }

        }

    }

    private AuthenticationStatus goNppaQueryResult(User user) {
        NppaCheckResp resp = authService.goNppaAuthQuery(user.getId() + "");
        if (resp.isErrorHappen()) {
            //日志
            return AuthenticationStatus.UNDER_WAY;
        } else {
            return AuthenticationStatus.codeOf(resp.getStatus());
        }
    }

}
