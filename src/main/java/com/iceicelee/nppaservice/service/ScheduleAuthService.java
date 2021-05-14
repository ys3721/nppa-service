package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import com.iceicelee.nppaservice.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    final static Logger logger = LoggerFactory.getLogger(ScheduleAuthService.class);

    @Autowired
    public ScheduleAuthService(IUserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
        this.start();
    }

    private void start() {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleWithFixedDelay(this::queryAuthResult, 2, 2, TimeUnit.MINUTES);
    }


    public void queryAuthResult() {
        Collection<User> users = userService.findUsersByStatus(AuthenticationStatus.UNDER_WAY);
        logger.info("数据库共有{}个飞豆用户需要去nppa中宣部平台取实名制结果...", users.size());
        for (User user : users) {
            try {
                //如果认证时间超过48小时 直接失败，等下次再登陆吧
                Timestamp timestamp = user.getAuthTime();
                if (System.currentTimeMillis() > timestamp.getTime() + TimeUnit.DAYS.toMillis(2)) {
                    user.setAuthStatus(AuthenticationStatus.FAIL);
                    userService.saveOrUpdateUser(user);
                } else {
                    NppaCheckResp resp = this.goNppaQueryResult(user);
                    AuthenticationStatus status = AuthenticationStatus.UNDER_WAY;
                    if (resp != null && !resp.isErrorHappen()) {
                        status = AuthenticationStatus.codeOf(resp.getStatus());
                    }
                    if (AuthenticationStatus.FAIL == status) {
                        //失败 标记失败
                        user.setAuthStatus(AuthenticationStatus.FAIL);
                        user.setAuthTime(new Timestamp(System.currentTimeMillis()));
                        userService.saveOrUpdateUser(user);
                    }
                    if (AuthenticationStatus.SUCCESS == status) {
                        user.setAuthStatus(AuthenticationStatus.SUCCESS);
                        user.setAuthTime(new Timestamp(System.currentTimeMillis()));
                        user.setPi(resp.getPi());
                        userService.saveOrUpdateUser(user);
                    }
                }
            } catch (Exception e) {
                logger.error("Schedule error", e);
                e.printStackTrace();
            }
        }
    }

    private NppaCheckResp goNppaQueryResult(User user) {
        NppaCheckResp resp = authService.goNppaAuthQuery(user.getId() + "");
        return resp;
    }

}
