package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.service.AuthenticationService;
import com.iceicelee.nppaservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Yao Shuai
 * @date: 2021/4/6 20:06
 */
@RestController
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private AuthenticationService authService;

    private UserService userService;

    @Autowired
    public AuthenticationController(AuthenticationService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * 实名认证验证接口
     * timestamp	Int	标准时间戳（精确到秒），例如：1270605064。
     * userid	Int(20)	用户ID（userid）。
     * gameid	Int(4)	表示游戏编号。
     * name	String	用户真实姓名。
     * idcard	String	用户身份证号。
     * sign	Varchar(32)	md5(timestamp+idcard+gameid+userid+key)注意顺序。
     *
     * @return
     */
    @GetMapping("/local.faceid.php")
    public String greeting(@RequestParam(name="timesTamp") int timesTamp,
                           @RequestParam(name="userId") long userId,
                           @RequestParam(name="gameId") int gameId,
                           @RequestParam String name,
                           @RequestParam(name="idcard") String idCard,
                           @RequestParam String sign
                           ) {
        //check sign and time timestamp

        //取user对象，如果为空，那么直接去nppa验证
        User user = userService.findUserByPassportId(userId);
        if (user == null) {
            //同步验证 这里会比较卡哇 fixme 需要压测一下 这个boot的线程模型是个啥样的哇？
            authService.goNppaAuthCheck(userId+"", name, idCard);
            //验证 -验证成功 或者 验证中 存库

            //验证失败 直接返回失败 放弃存库
        } else {
            //如果不为空，看看认证状态， 0认证成功直接返回 1认证中 不会有2失败，因为失败不存
            AuthenticationStatus status = user.getAuthStatus();
            if (AuthenticationStatus.SUCCESS.equals(status)) {
                return "ok:1";
            }
        }

        //验证 -验证成功 或者 验证中 存库
        //验证失败 直接返回失败 放弃存库

        //改一下顺序，先去nppa认证，如果成功或者认证中那么在存库，自己就不用validate

        //还是要把对象取过来
        User user = authService.findOrBuildUser(userId, gameId, name, idCard);
        //检查是不是已经认证状态 或者 认证中的状态
        AuthenticationStatus authStatus  = AuthenticationStatus.codeOf(user.getAuthStatus());
        if (authStatus == AuthenticationStatus.SUCCESS) {
            //这个抽象成对象和状态吧
            return "ok:1";
        }
        //新创建的角色，去认证
        if (authStatus == AuthenticationStatus.INITIALIZE) {
            authService.goNppaAuthCheck(user);
            if (AuthenticationStatus.SUCCESS == AuthenticationStatus.codeOf(user.getAuthStatus())) {
                return "ok:1";
            }
            if (AuthenticationStatus.UNDER_WAY == AuthenticationStatus.codeOf(user.getAuthStatus())) {
                return "ok:2";
            }
        }
        //之前的结果还没返回，再去取一次
        if (authStatus == AuthenticationStatus.UNDER_WAY) {
            authService.goNppaAuthQuery(user);
        }
        //失败了 告诉他失败了 让他换个身份再来试试
        if (authStatus == AuthenticationStatus.FAIL) {
            return "fail:7";
        }
        return "fail:999";
    }


}
