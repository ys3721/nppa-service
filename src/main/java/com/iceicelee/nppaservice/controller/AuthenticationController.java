package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
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
        long now = System.currentTimeMillis();
        //取user对象，如果为空，那么直接去nppa验证
        User user = userService.findUserByPassportId(userId);
        if (user == null) {
            //同步验证 这里会比较卡哇 fixme 需要压测一下 这个boot的线程模型是个啥样的哇？
            NppaCheckResp response = authService.goNppaAuthCheck(userId+"", name, idCard);
            //验证 -验证成功 或者 验证中 存库
            if (response.getErrcode() == 0 && response.getData() != null) {
                int status = response.getStatus();
                String pi = response.getPi();
                //认证失败
                if (status == AuthenticationStatus.FAIL.getCode()) {
                    return "fail:7";
                } else if (status == AuthenticationStatus.SUCCESS.getCode()) {
                    userService.createUserAndSave(userId, gameId, pi, "", name, idCard, timesTamp, status, now);
                    return "ok:1";

                } else if (status == AuthenticationStatus.UNDER_WAY.getCode()) {
                    //成功 存库 并且返回成功
                    userService.createUserAndSave(userId, gameId, pi, "", name, idCard, timesTamp, status, now);
                    return "ok:2";
                } else {
                    // log
                    return "fail:999";
                }
            }
        } else {
            //user 存在只能是已经通过 或者 认证中
            if (AuthenticationStatus.SUCCESS == user.getAuthStatus()) {
                return "ok:1";
            }
            if (AuthenticationStatus.UNDER_WAY == user.getAuthStatus()) {
                //还是这样吧
                return "ok:2";
            }
        }
        return "fail:999";
    }


}
