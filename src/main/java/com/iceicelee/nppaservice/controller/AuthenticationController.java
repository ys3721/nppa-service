package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author: Yao Shuai
 * @date: 2021/4/6 20:06
 */
@RestController
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserDao userDao;
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
        //检查是不是已经认证状态 或者 认证中的状态

        //没有认证，去认证

        //认证中 那么去取结果
        //
        User user = userDao.findById(userId);
        if (user == null) {
            user = new User();
            user.setId(userId);
            user.setPassportName("");
            user.setRealName(name);
            user.setIdNumber(idCard);
            user.setAuthStatus((byte) 4);
            user.setCreateTime(new Timestamp(System.currentTimeMillis()));
            userDao.save(user);
        }
        return "ok:1";
    }


}
