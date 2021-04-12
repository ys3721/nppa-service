package com.iceicelee.nppaservice.pojo;

/**
 * @author: Yao Shuai
 * @date: 2021/4/12 19:50
 */
public class FeidouLoginCheckResp extends FeidouPlatformResponse {

    private long userId;

    private int errorCode;

    public FeidouLoginCheckResp() {
    }

    public long getUserId() {
        return userId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    protected void parseDetails() {
        if (this.isOk()) {
            userId = Long.parseLong(this.getResponseParams().get(0));
            if (userId <= 0) {
                throw new IllegalArgumentException();
            }
        } else {
            errorCode = Integer.parseInt(this.getResponseParams().get(0));
        }
    }
}
