package com.iceicelee.nppaservice.pojo;

import java.sql.Timestamp;

/**
 * 代表一用户吧
 *
 * @author: Yao Shuai
 * @date: 2021/4/2 15:58
 */
public class User {

    private int id;

    private String pi;

    private String passportName;

    private Timestamp createTime;

    private byte authStatus;

    private Timestamp authTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String pi) {
        this.pi = pi;
    }

    public String getPassportName() {
        return passportName;
    }

    public void setPassportName(String passportName) {
        this.passportName = passportName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public byte getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(byte authStatus) {
        this.authStatus = authStatus;
    }

    public Timestamp getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Timestamp authTime) {
        this.authTime = authTime;
    }
}
