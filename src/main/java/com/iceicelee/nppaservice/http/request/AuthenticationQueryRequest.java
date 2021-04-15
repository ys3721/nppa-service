package com.iceicelee.nppaservice.http.request;

import com.iceicelee.nppaservice.http.HttpConnector;
import com.iceicelee.nppaservice.http.IHttpClient;
import com.iceicelee.nppaservice.pojo.NppaCheckResp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Nppa的authentication取结果的接口
 *
 * @author: Yao Shuai
 * @date: 2021/4/15 17:23
 */
public class AuthenticationQueryRequest {

    public static final String url = "http://api2.wlc.nppa.gov.cn/idcard/authentication/query";

    public HttpMethod httpMethod = HttpMethod.GET;

    public IHttpClient httpConnector;

    private Map<String, String> params;

    private Map<String, String> httpHeadProperties;

    @Autowired
    public AuthenticationQueryRequest(IHttpClient httpConnector) {
        this.httpConnector = httpConnector;
    }

    public void assemble(Map<String, String> reqHeadProps, Map<String, String> params) {
        this.params = params;
        this.httpHeadProperties = reqHeadProps;
    }

    public NppaCheckResp send() {
        String result = httpConnector.get(url, httpHeadProperties, params);
        NppaCheckResp nppaCheckResp = new NppaCheckResp();
        if (StringUtils.isEmpty(result)) {
            //报错了吧
            nppaCheckResp.setErrcode(10086);
            nppaCheckResp.setErrmsg("服务器内部错误");
        } else {
            nppaCheckResp.parserFromJson(result);
        }
        return nppaCheckResp;
    }

}
