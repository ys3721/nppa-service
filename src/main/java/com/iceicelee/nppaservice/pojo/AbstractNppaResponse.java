package com.iceicelee.nppaservice.pojo;

/**
 * @author: Yao Shuai
 * @date: 2021/4/9 20:49
 */
public class AbstractNppaResponse {

    /**
     * 状态码
     */
    private int errcode;

    /**
     * 状态描述
     */
    private String errmsg;


    private String totalResponse;


    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getTotalResponse() {
        return totalResponse;
    }

    public void setTotalResponse(String totalResponse) {
        this.totalResponse = totalResponse;
    }

    @Override
    public String toString() {
        return totalResponse;
    }
}
