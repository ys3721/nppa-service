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
    @RequestMapping(value = "/sdk.login.go")
    public String loginCheck(HttpServletRequest req, int timestamp, int gameid, String serverid, int logintype, String device,
                             String devicetype, String deviceversion, String deviceudid, String devicemac,
                             String deviceidfa, String appversion, String appsflyerid, String sdktitle,
                             String sdkversion, String username, String password, String sign) {
        String calSign = EncryptUtils.encodeByMD5(("jasdlfjWRSSajfjalsdfasdf" + timestamp + username + gameid + password + "1"));
        if(!calSign.equals(sign)) {
            log.error("sdk.login.go?????????????????????????????????");
        }
        String ip = req.getRemoteAddr();
        serverid = Strings.isEmpty(serverid) ? "0": serverid;
        //????????????????????????
        String loginCheckUrl = "http://api.feidou.com/local.logincheck.php";
        Map<String, String> localReqParams = new HashMap<>();
        //md5(timestamp+username+ip+gameid+serverid+password+logintype+key)???????????????
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
        //?????????????????????????????????
        FeidouLoginCheckResp loginCheckResp = new FeidouLoginCheckResp();
        loginCheckResp.parse(localResult);
        if (!loginCheckResp.isOk()) {
            return localResult;
        } else {
            User user = userService.findUserByPassportId(loginCheckResp.getUserId());
            if (user != null) {
                if (AuthenticationStatus.FAIL.equals(user.getAuthStatus())) {
                    //??????????????? ??????????????? ??????????????????
                    loginCheckResp.setAddictInfoCompletion(0);
                    loginCheckResp.setBothDayInfo("0");
                    loginCheckResp.setNeedPreventAddict(1);
                    return loginCheckResp.toResponseString();
                } else if (AuthenticationStatus.UNDER_WAY.equals(user.getAuthStatus())) {
                    //?????? ????????????????????? ????????????????????? ?????????????????????
                    loginCheckResp.setAddictInfoCompletion(1);
                    loginCheckResp.setBothDayInfo("2020-02-02");
                    loginCheckResp.setNeedPreventAddict(1);
                    return loginCheckResp.toResponseString();
                } else {
                    //????????????
                    loginCheckResp.setAddictInfoCompletion(1);
                    loginCheckResp.setNeedPreventAddict(0);
                    loginCheckResp.setBothDayInfo(user.getBirthdayIntFromPi());
                    return loginCheckResp.toResponseString();
                }
            } else {
                //???????????????????????????????????? ?????????????????????????????????
                loginCheckResp.setAddictInfoCompletion(0);
                loginCheckResp.setBothDayInfo("0");
                loginCheckResp.setNeedPreventAddict(1);
                return loginCheckResp.toResponseString();
            }
        }
    }

    /**
     * ????????????????????????
     * timestamp	Int	?????????????????????????????????????????????1270605064???
     * userid	Int(20)	??????ID???userid??????
     * gameid	Int(4)	?????????????????????
     * name	String	?????????????????????
     * idcard	String	?????????????????????
     * sign	Varchar(32)	md5(timestamp+idcard+gameid+userid+key)???????????????
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
            log.error("local.faceid.go?????????????????????????????????");
        }
        //??????????????????????????????????????????????????????,?????????ok:1,?????????????????????????????????????????????
        String faceIdCheckUrl = "http://api.feidou.com/local.faceid.php";
        String ip = req.getRemoteAddr();

        Map<String, String> localReqParams = new HashMap<>();
        //md5(timestamp+idcard+gameid+userid+key)????????????
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
            //???nppa????????????
            NppaCheckResp response = authService.goNppaAuthCheck(userid, name, idcard);
            //????????????nppa??????
            if (response.isErrorHappen()) {
                //log
                log.error(String.format("????????????????????????Nppa???????????????????????????%s???????????????%s",
                        response.getErrcode(), response.getErrmsg()));
                //?????????????????????
                this.processAuthUnderWay(Long.parseLong(userid), gameid, username, name, idcard);
                return faceIdResp.toResponseString();
            } else {
                if (response.getStatus() == AuthenticationStatus.FAIL.getCode()) {
                    //??????
                    this.processAuthFail(Long.parseLong(userid), gameid, username, name, idcard);
                    faceIdResp.setOkOrFail("fail");
                    faceIdResp.setRespCode(7);
                    return faceIdResp.toResponseString();
                } else if (response.getStatus() == AuthenticationStatus.SUCCESS.getCode()) {
                    //??????
                    this.processAuthOK(Long.parseLong(userid), gameid, response.getPi(), username, name, idcard);
                    return faceIdResp.toResponseString();
                } else {
                    //??????????????????
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
