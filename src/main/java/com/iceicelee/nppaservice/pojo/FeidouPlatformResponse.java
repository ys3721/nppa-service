package com.iceicelee.nppaservice.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Yao Shuai
 * @date: 2021/4/12 19:40
 */
public abstract class FeidouPlatformResponse {

    private String okOrFail;

    private List<String> responseParams;

    public FeidouPlatformResponse(String okOrFail, List<String> responseParams) {
        this.okOrFail = okOrFail;
        this.responseParams = responseParams;
    }

    public FeidouPlatformResponse() {
    }

    public String getOkOrFail() {
        return okOrFail;
    }

    public List<String> getResponseParams() {
        return responseParams;
    }

    public boolean isOk() {
        return okOrFail.equals("ok");
    }

    public void parse(String respStr) {
        String result = respStr.split(":")[0];
        if (!result.equals("ok") && !result.equals("fail")) {
            throw new IllegalArgumentException();
        }
        String[] params = respStr.split(":");
        List<String> paramsList = new ArrayList<>(Arrays.asList(params));
        paramsList.remove(0);
        this.okOrFail = result;
        this.responseParams = paramsList;
        parseDetails();
    }

    protected abstract void parseDetails();

    public void setOkOrFail(String okOrFail) {
        this.okOrFail = okOrFail;
    }
}
