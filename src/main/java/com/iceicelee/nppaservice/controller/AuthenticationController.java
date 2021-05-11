package com.iceicelee.nppaservice.controller;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.FeidouFaceIdResp;
import com.iceicelee.nppaservice.pojo.FeidouLoginCheckResp;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.service.AuthenticationService;
import com.iceicelee.nppaservice.service.IUserService;
import com.iceicelee.nppaservice.service.UserService;
import com.iceicelee.nppaservice.utils.EncryptUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
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

    private IUserService userService;

    private IHttpClient httpClient;

    private int thisYear;

    @Autowired
    public AuthenticationController(AuthenticationService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
        this.calculateYear();
    }

    private void calculateYear() {
        this.thisYear =Calendar.getInstance().get(Calendar.YEAR);
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
    @RequestMapping(value = "/sdk.login.go")
    public String loginCheck(HttpServletRequest req, int timestamp, int gameid, String serverid, int logintype, String device,
                             String devicetype, String deviceversion, String deviceudid, String devicemac,
                             String deviceidfa, String appversion, String appsflyerid, String sdktitle,
                             String sdkversion, String username, String password, String sign) {
        String calSign = EncryptUtils.encodeByMD5(("jasdlfjWRSSajfjalsdfasdf" + timestamp + username + gameid + password + "1"));
        if(!calSign.equals(sign)) {
            log.error("sdk.login.go签名校验失败，请检查！");
        }
        String ip = req.getRemoteAddr();
        serverid = Strings.isEmpty(serverid) ? "0": serverid;
        //先去飞豆取飞豆号
        String loginCheckUrl = "http://api.feidou.com/local.logincheck.php";
        Map<String, String> localReqParams = new HashMap<>();
        //md5(timestamp+username+ip+gameid+serverid+password+logintype+key)注意顺序。
        String localSign = EncryptUtils.encodeByMD5(timestamp+username+ip+gameid+serverid+password+logintype+"qWIbvFQpdIrtUg4MayqW");
        localReqParams.put("timestamp", timestamp+"");
        localReqParams.put("username", username);
        localReqParams.put("ip", ip);
        localReqParams.put("gameid", gameid+"");
        localReqParams.put("serverid", serverid);
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
                    loginCheckResp.setBothDayInfo("1982-02-02");
                    loginCheckResp.setNeedPreventAddict(0);
                    return loginCheckResp.toResponseString();
                } else {
                    //他成功了
                    loginCheckResp.setAddictInfoCompletion(1);
                    //逻辑错误 这个就按平台的来就行了吧
                    String birthDay = user.getBirthdayIntFromPi().replaceAll("-", "");
                    //是不是防沉迷按照他的生日算一下就得了 不管平台返回来是多少了
                    loginCheckResp.setNeedPreventAddict(this.isNeedPreventAddict(birthDay) ? 1 : 0);
                    loginCheckResp.setBothDayInfo(user.getBirthdayIntFromPi());
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

    private boolean isNeedPreventAddict(String birthDay) {
        if (Strings.isEmpty(birthDay) || birthDay.length() < 8) {
            log.error("生日错误 " + birthDay);
            return false;
        }
        try {
            int borthYear = Integer.parseInt(birthDay)  / 10000;
            return thisYear - borthYear < 18;
        } catch (Exception e) {
            log.error("生日错误e " + birthDay);
            e.printStackTrace();
        }
        return false;
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
    @RequestMapping("/local.faceid.go")
    public String realNameAuth(HttpServletRequest req, int timestamp, int gameid, String serverid, int logintype, String device,
                               String devicetype, String deviceversion, String deviceudid, String devicemac,
                               String deviceidfa, String appversion, String appsflyerid, String sdktitle,
                               String sdkversion, String username, String password, String sign,
                               String userid, String name, String idcard) {
        //check sign and time timestamp
        String calSign = EncryptUtils.encodeByMD5("jasdlfjWRSSajfjalsdfasdf" + timestamp + userid + gameid + name + idcard);
        if(!calSign.equals(sign)) {
            log.error("local.faceid.go签名校验失败，请检查！");
        }
        //先去飞豆实名认证一下，飞豆重复认证直,接返回ok:1,但是不会更新之前的实名认证信息
        String faceIdCheckUrl = "http://api.feidou.com/local.faceid.php";
        String ip = req.getRemoteAddr();

        Map<String, String> localReqParams = new HashMap<>();
        //md5(timestamp+idcard+gameid+userid+key)注意顺序
        String localSign = EncryptUtils.encodeByMD5(timestamp+idcard+gameid+userid+"qWIbvFQpdIrtUg4MayqW");
        localReqParams.put("timestamp", timestamp+"");
        localReqParams.put("userid", userid);
        localReqParams.put("ip", ip);
        localReqParams.put("gameid", gameid+"");
        localReqParams.put("name", name);
        localReqParams.put("idcard", idcard);
        localReqParams.put("sign", localSign);
        String localResult = this.getHttpClient().get(faceIdCheckUrl, null, localReqParams);
        FeidouFaceIdResp faceIdResp = new FeidouFaceIdResp();
        faceIdResp.parse(localResult);
        if (!faceIdResp.isOk()) {
            return faceIdResp.toResponseString();
        } else {
            //去nppa验证一下
            NppaCheckResp response = authService.goNppaAuthCheck(userid, name, idcard);
            //先不考虑nppa挂了
            if (response.isErrorHappen()) {
                //log
                log.error(String.format("验证返回错误！去Nppa验证报错，错误码是%s，错误信息%s",
                        response.getErrcode(), response.getErrmsg()));
                //当成认证中处理
                this.processAuthUnderWay(Long.parseLong(userid), gameid, username, name, idcard);
                return faceIdResp.toResponseString();
            } else {
                if (response.getStatus() == AuthenticationStatus.FAIL.getCode()) {
                    //失败
                    this.processAuthFail(Long.parseLong(userid), gameid, username, name, idcard);
                    faceIdResp.setOkOrFail("fail");
                    faceIdResp.setRespCode(7);
                    return faceIdResp.toResponseString();
                } else if (response.getStatus() == AuthenticationStatus.SUCCESS.getCode()) {
                    //成功
                    this.processAuthOK(Long.parseLong(userid), gameid, response.getPi(), username, name, idcard);
                    return faceIdResp.toResponseString();
                } else {
                    //只有认证中了
                    this.processAuthUnderWay(Long.parseLong(userid), gameid, username, name, idcard);
                    return faceIdResp.toResponseString();
                }
            }
        }
    }

    private void processAuthFail(Long userid, int gameId, String username, String realName, String idCard) {
        User user = new User();
        user.setId(userid);
        user.setGameId(gameId);
        user.setPi("");
        user.setPassportName(username);
        user.setRealName(realName);
        user.setIdNumber(idCard);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setAuthStatus(AuthenticationStatus.FAIL);
        user.setAuthTime(new Timestamp(System.currentTimeMillis()));
        userService.saveOrUpdateUser(user);
    }

    private void processAuthOK(long userid, int gameId, String pi, String username, String realName, String idCard) {
        User user = new User();
        user.setId(userid);
        user.setGameId(gameId);
        user.setPi(pi);
        user.setPassportName(username);
        user.setRealName(realName);
        user.setIdNumber(idCard);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setAuthStatus(AuthenticationStatus.SUCCESS);
        user.setAuthTime(new Timestamp(System.currentTimeMillis()));
        userService.saveOrUpdateUser(user);

    }

    private void processAuthUnderWay(Long userid, int gameId, String username, String realName, String idCard) {
        User user = new User();
        user.setId(userid);
        user.setGameId(gameId);
        user.setPi("");
        user.setPassportName(username);
        user.setRealName(realName);
        user.setIdNumber(idCard);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setAuthStatus(AuthenticationStatus.UNDER_WAY);
        user.setAuthTime(new Timestamp(System.currentTimeMillis()));
        userService.saveOrUpdateUser(user);
    }

    public IHttpClient getHttpClient() {
        return httpClient;
    }

    @Autowired
    public void setHttpClient(IHttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
