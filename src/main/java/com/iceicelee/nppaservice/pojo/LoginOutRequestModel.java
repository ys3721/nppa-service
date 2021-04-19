package com.iceicelee.nppaservice.pojo;

import net.sf.json.JSONObject;

import java.sql.Timestamp;

/**
 * @author: Yao Shuai
 * @date: 2021/4/19 17:51
 */
public class LoginOutRequestModel {

    public static final int LOGOUT = 0;

    public static final int LOGIN = 1;

    public static final int AUTHED = 0;

    public static final int GUEST = 2;


    private String roleId;

    /**
     * 0：下线 1上线
     */
    private int logInOut;

    /**
     * 行为发生时间 秒
     */
    private long timeSeconds;

    private int authType;

    private String idfa;

    private String pi;


    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public int getLogInOut() {
        return logInOut;
    }

    public void setLogInOut(int logInOut) {
        this.logInOut = logInOut;
    }

    public long getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(long timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }

    public JSONObject toJsonProtocolStr(int i) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("no", i);
        jsonObject.put("si", this.getRoleId());
        jsonObject.put("bt", this.getLogInOut());
        jsonObject.put("ot", this.getTimeSeconds());
        jsonObject.put("ct", this.getAuthType());
        jsonObject.put("di", this.getIdfa());
        jsonObject.put("pi", this.getPi());
        return jsonObject;
    }
}
