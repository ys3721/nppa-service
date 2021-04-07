package com.iceicelee.nppaservice.http;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 接口比较怪，包装一下httpclient
 *
 * @author: Yao Shuai
 * @date: 2021/4/7 20:33
 */
@Component
public class ThirdPartHttpClientAdapter implements IHttpClient {

    @Override
    public String post(String urlStr, Map<String, String> requestProperty, String postData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(String urlStr, Map<String, String> reqProps, Map<String, String> urlParams) {
        throw new UnsupportedOperationException();
    }
}
