package com.iceicelee.nppaservice.config;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * APPID
 * bizId
 * SecretKey
 *
 * @author: Yao Shuai
 * @date: 2021/4/8 15:26
 */
@Component
public class NppaConfig {

    private String appId;

    private String bizId;

    private Map<String, String> appIdAndBizIdMap;

    private String secretKey;

    public static final String APPID_KEY = "appId";

    public static final String BIZID_KEY = "bizId";

    public static final String SECRETKEY_KEY = "secretKey";

    public NppaConfig() {
        readConfig();
    }

    private void readConfig() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("nppa.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            this.appId = properties.getProperty(APPID_KEY);
            this.bizId = properties.getProperty(BIZID_KEY);
            this.secretKey = properties.getProperty(SECRETKEY_KEY);
            appIdAndBizIdMap = new HashMap<>();
            appIdAndBizIdMap.put(APPID_KEY, appId);
            appIdAndBizIdMap.put(BIZID_KEY, this.bizId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Map<String, String> getAppIdAndBizIdMap() {
        return appIdAndBizIdMap;
    }

    public void setAppIdAndBizIdMap(Map<String, String> appIdAndBizIdMap) {
        this.appIdAndBizIdMap = appIdAndBizIdMap;
    }
}