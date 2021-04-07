package com.iceicelee.nppaservice.http;

import java.util.Map;

/**
 * 做个接口吧 方便换第三方的库
 *
 * @author: Yao Shuai
 * @date: 2021/4/7 20:23
 */
public interface IHttpClient {

    String post(String urlStr, Map<String, String> requestProperty, String postData);

    String get(String urlStr, Map<String, String> reqProps, Map<String, String> urlParams);

}
