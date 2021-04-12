package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.FeidouLoginCheckResp;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.service.AuthenticationService;
import com.iceicelee.nppaservice.service.UserService;
import com.iceicelee.nppaservice.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: Yao Shuai
 * @date: 2021/4/6 20:06
 */
@RestController
@RequestMapping("/sdk")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private AuthenticationService authService;

    private UserService userService;

    private IHttpClient httpClient;

    @Autowired
    public AuthenticationController(AuthenticationService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * 	   "timestamp", fdloginrequest.gettimestamp());
     *  "gameid", fdloginrequest.getgameid());
     *  "serverid", fdloginrequest.getserverid());
     *  "logintype", fdloginrequest.getlogintype());
     *  "device", fdloginrequest.getdevice());
     *  "devicetype", fdloginrequest.getdevicetype());
     *  "serverid", fdloginrequest.getdeviceversion());
     *  "deviceudid", fdloginrequest.getdeviceudid());
     *  "devicemac", getMac(activity));
     *  "deviceidfa","-1");
     *  "appversion", fdloginrequest.getappversion());
     *  "appsflyerid",fdloginrequest.getappsflyerid());
     *  "sdktitle",getAppVersion(activity) );
     *  "sdkversion", getAppVersion(activity));
     *  "username", fdloginrequest.getusername());
     *  "password",fdloginrequest.getpassword());
     *  "sign", fdloginrequest.g
     * @return
     */
    @RequestMapping(value = "local.logincheck.go")
    public String loginCheck(HttpServletRequest req, int timestamp, int gameid, int serverid, int logintype, String device,
                             String devicetype, String deviceversion, String deviceudid, String devicemac,
                             String deviceidfa, String appversion, String appsflyerid, String sdktitle,
                             String sdkversion, String username, String password, String sign) {
        String ip = req.getRemoteAddr();
        //先去飞豆取飞豆号
        String loginCheckUrl = "http://api.feidou.com/local.logincheck.php";
        Map<String, String> usreParams = new LinkedHashMap<>();
        //md5(timestamp+username+ip+gameid+serverid+password+logintype+key)注意顺序。
        String localSign = EncryptUtils.encodeByMD5(timestamp+username+ip+"5614"+serverid+password+logintype+"qWIbvFQpdIrtUg4MayqW");
        usreParams.put("timestamp", timestamp+"");
        usreParams.put("username", username);
        usreParams.put("ip", ip);
        usreParams.put("gameid", gameid+"");
        usreParams.put("serverid", serverid+"");
        usreParams.put("password", password);
        usreParams.put("logintype", logintype+"");
        usreParams.put("device", device);
        usreParams.put("devicetype", devicetype);
        usreParams.put("deviceversion", deviceversion);
        usreParams.put("deviceudid", deviceudid);
        usreParams.put("devicemac", devicemac);
        usreParams.put("deviceidfa", deviceidfa);
        usreParams.put("appversion", appversion);
        usreParams.put("appsflyerid", appsflyerid);
        usreParams.put("sign",localSign);
        String localResult = this.getHttpClient().get(loginCheckUrl, null, usreParams);
        //如果失败直接原样返还吧
        FeidouLoginCheckResp loginCheckResp = new FeidouLoginCheckResp();
        loginCheckResp.parse(localResult);
        if (!loginCheckResp.isOk()) {
            return localResult;
        } else {
            userService.findUserByPassportId(loginCheckResp.getUserId());
        }

        //真有这个人 取他的userid判断一下是不是已经 上报过nppa了 状态是啥


        return "1";
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
    @GetMapping("/local.faceid.go")
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
        }
        return "fail:999";
    }

    public IHttpClient getHttpClient() {
        return httpClient;
    }

    @Autowired
    public void setHttpClient(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
