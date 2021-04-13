package com.iceicelee.nppaservice.pojo;

/**
 * 这个是补全实名认证的接口
 * 你瞅瞅他起的这个名字谁知道是啥哇
 * ok:%1
 * fail:%1
 *
 * @author: Yao Shuai
 * @date: 2021/4/13 15:16
 */
public class FeidouFaceIdResp extends FeidouPlatformResponse{

    private int respCode;

    @Override
    protected void parseDetails() {
        this.respCode = Integer.parseInt(this.getResponseParams().get(0));
    }

    public String toResponseString() {
        StringBuilder sb = new StringBuilder(this.isOk() ? "ok:" : "fail:");
        sb.append(respCode);
        return sb.toString();
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }
}
