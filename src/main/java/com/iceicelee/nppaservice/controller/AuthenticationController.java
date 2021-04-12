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
        Map<String, String> localReqParams = new LinkedHashMap<>();
        //md5(timestamp+username+ip+gameid+serverid+password+logintype+key)注意顺序。
        String localSign = EncryptUtils.encodeByMD5(timestamp+username+ip+"5614"+serverid+password+logintype+"qWIbvFQpdIrtUg4MayqW");
        localReqParams.put("timestamp", timestamp+"");
        localReqParams.put("username", username);
        localReqParams.put("ip", ip);
        localReqParams.put("gameid", gameid+"");
        localReqParams.put("serverid", serverid+"");
        localReqParams.put("password", password);
        localReqParams.put("logintype", logintype+"");
        localReqParams.put("device", device);
        localReqParams.put("devicetype", devicetype);
        localReqParams.put("deviceversion", deviceversion);
        localReqParams.put("deviceudid", deviceudid);
        localReqParams.put("devicemac", devicemac);
        localReqParams.put("deviceidfa", deviceidfa);
        localReqParams.put("appversion", appversion);
        localReqParams.put("appsflyerid", appsflyerid);
        localReqParams.put("sign", localSign);
        String localResult = this.getHttpClient().get(loginCheckUrl, null, localReqParams);
        //如果失败直接原样返还吧
        FeidouLoginCheckResp loginCheckResp = new FeidouLoginCheckResp();
        loginCheckResp.parse(localResult);
        if (!loginCheckResp.isOk()) {
            return localResult;
        } else {
            User user = userService.findUserByPassportId(loginCheckResp.getUserId());
            if (user != null) {
                if (AuthenticationStatus.FAIL.equals(user.getAuthStatus())) {
                    //设置不完整 生日信息空 是防沉迷对象
                    loginCheckResp.setAddictInfoCompletion(0);
                    loginCheckResp.setBothDayInfo("0");
                    loginCheckResp.setNeedPreventAddict(1);
                    return loginCheckResp.toResponseString();
                } else if (AuthenticationStatus.UNDER_WAY.equals(user.getAuthStatus())) {
                    //设置 是防沉迷对象， 认证信息完整， 生日随便给一个
                    loginCheckResp.setAddictInfoCompletion(1);
                    loginCheckResp.setBothDayInfo("2020-02-02");
                    loginCheckResp.setNeedPreventAddict(1);
                    return loginCheckResp.toResponseString();
                } else {
                    //他成功了
                    loginCheckResp.setAddictInfoCompletion(1);
                    //取pi看看生日啥 xxxxxxxxxxxx明天从这开始吧
                    loginCheckResp.setNeedPreventAddict(0);
                    loginCheckResp.setBothDayInfo("1920-02-02");
                    return loginCheckResp.toResponseString();
                }
            } else {
                //没有这个货，通通要去验证 告诉客户端实名信息不全
                loginCheckResp.setAddictInfoCompletion(0);
                loginCheckResp.setBothDayInfo("0");
                loginCheckResp.setNeedPreventAddict(1);
                return loginCheckResp.toResponseString();
            }
        }
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
    public String realNameAuth(HttpServletRequest req, int timestamp, int gameid, int serverid, int logintype, String device,
                               String devicetype, String deviceversion, String deviceudid, String devicemac,
                               String deviceidfa, String appversion, String appsflyerid, String sdktitle,
                               String sdkversion, String username, String password, String sign,
                               String userid, String name, String idcard) {
        //check sign and time timestamp
        //先去飞豆实名认证一下，飞豆重复认证直,接返回ok:1,但是不会更新之前的实名认证信息
        String faceIdCheckUrl = "http://api.feidou.com/local.faceid.php";

        String localResult = this.getHttpClient().get(faceIdCheckUrl, null, usreParams);


        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        Map<String, String> localReqParams = new LinkedHashMap<>();
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
