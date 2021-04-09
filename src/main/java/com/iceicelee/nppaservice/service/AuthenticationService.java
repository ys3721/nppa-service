package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.config.NppaConfig;
import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 首先如果user对象不存在的话那么就创建一个
 * 这个类要去nppa的服务器把实名认证作了
 *
 * 认证相应有三种结果，成功，失败，和认证中 三个结果
 *
 * @author: Yao Shuai
 * @date: 2021/4/6 20:08
 */
@Service
public class AuthenticationService {

    // FIXME: 2021/4/7 先放这儿吧
    public static final String AUTH_URL = "https://wlc.nppa.gov.cn/test/authentication/";

    private UserDao userDao;

    private IHttpClient httpClient;

    private NppaConfig nppaConfig;


    private SignService signService;

    @Autowired
    public AuthenticationService(UserDao userDao, IHttpClient httpClient) {
        this.userDao = userDao;
        this.httpClient = httpClient;
    }


    public UserDao getUserDao() {
        return userDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }


    /**
     * 创建一个没有认证的默认的user
     *
     */
    private User buildInitializeUser(long userId, int gameId, String name, String idCard) {
        User welcomeUser = new User();
        welcomeUser.setAuthStatus((byte) AuthenticationStatus.INITIALIZE.getCode());
        welcomeUser.setId(userId);
        welcomeUser.setGameId(gameId);
        welcomeUser.setRealName(name);
        welcomeUser.setIdNumber(idCard);
        welcomeUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
        this.userDao.save(welcomeUser);
        return welcomeUser;
    }

    /**
     * 去平台认证一下这个人的实名是不是真的
     * String urlStr, Map<String, String> requestProperty, String postData
     *
     * @param ai 这个就是userid， 在nppa中定义为
     *           本次实名认证行为在游戏内部对应的唯一标识，该标识将作为实名认证结果查询的唯一依据
     *
     */
    public void goNppaAuthCheck(String ai, String realName, String idNum) {
        String url = AUTH_URL + "/check/"; //fixme 先这么写死了哇
        String postData = this.buildEncryptData(ai, realName, idNum);
        Map<String, String> reqHeadMap = this.buildCommonReqHeadMap();
        String sign =  signService.sign(reqHeadMap, null, postData);
        if (sign == null) {
            //lgo
            return;
        }
        reqHeadMap.put("sign", sign);
        String respStr = httpClient.post(url, reqHeadMap, postData);
        System.out.println(respStr);
        if (StringUtils.isEmpty(respStr)) {
            //报错了吧
        } else {
            JSONObject jsonObject = JSONObject.fromObject(respStr);
        }

    }

    /**
     *
     * @return
     */
    public String buildEncryptData(String ai, String realName, String idNum) {
        JSONObject jo = new JSONObject();
        jo.put("ai", ai);
        jo.put("name", realName);
        jo.put("idNum", idNum);
        String dataStr = jo.toString();

        byte[] byteSecretKey = EncryptUtils.hexStringToByte(this.getNppaConfig().getSecretKey());
        String dataContent = EncryptUtils.aesGcmEncrypt(dataStr, byteSecretKey);
        JSONObject postBody = new JSONObject();
        postBody.put("data", dataContent);
        return postBody.toString();
    }

    private Map<String, String> buildCommonReqHeadMap() {
        Map<String, String> headPropertyMap = new HashMap<>(this.getNppaConfig().getAppIdAndBizIdMap());
        headPropertyMap.put("timestamps", System.currentTimeMillis() + "");
        return headPropertyMap;
    }
    public void goNppaAuthQuery(User user) {

    }

    public NppaConfig getNppaConfig() {
        return nppaConfig;
    }

    @Autowired
    public void setNppaConfig(NppaConfig nppaConfig) {
        this.nppaConfig = nppaConfig;
    }

    @Autowired
    public void setSignService(SignService signService) {
        this.signService = signService;
    }
}
