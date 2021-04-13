package com.iceicelee.nppaservice.service;

import com.iceicelee.nppaservice.config.NppaConfig;
import com.iceicelee.nppaservice.dao.UserDao;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import com.iceicelee.nppaservice.pojo.User;
import com.iceicelee.nppaservice.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public static final String AUTH_URL = "https://api.wlc.nppa.gov.cn/idcard/authentication/check";

    private UserDao userDao;

    private final IHttpClient httpClient;

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
     * 去平台认证一下这个人的实名是不是真的
     * String urlStr, Map<String, String> requestProperty, String postData
     *
     * @param ai 这个就是userid， 在nppa中定义为
     *           本次实名认证行为在游戏内部对应的唯一标识，该标识将作为实名认证结果查询的唯一依据
     *
     */
    public NppaCheckResp goNppaAuthCheck(String ai, String realName, String idNum) {
        String url = AUTH_URL; //fixme 先这么写死了哇
        String postData = this.buildEncryptData(ai, realName, idNum);
        Map<String, String> reqHeadMap = this.buildCommonReqHeadMap();
        String sign =  signService.sign(reqHeadMap, null, postData);
        reqHeadMap.put("sign", sign);
        String respStr = httpClient.post(url, reqHeadMap, postData);
        System.out.println(respStr);
        NppaCheckResp nppaCheckResp = new NppaCheckResp();
        if (StringUtils.isEmpty(respStr)) {
            //报错了吧
            nppaCheckResp.setErrcode(10086);
            nppaCheckResp.setErrmsg("服务器内部错误");
        } else {
            nppaCheckResp.parserFromJson(respStr);
        }
        return nppaCheckResp;

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
