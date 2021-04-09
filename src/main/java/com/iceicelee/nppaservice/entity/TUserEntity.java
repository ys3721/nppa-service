package com.iceicelee.nppaservice.entity;

import java.sql.Timestamp;

/**
 * user类的实体类对象 方便切换dao实现
 *
 * @author: Yao Shuai
 * @date: 2021/4/9 19:48
 */
public class TUserEntity {

    private long id;

    private String pi;

    private int gameId;

    private String passportName;

    private Timestamp createTime;

    private byte authStatus;

    private Timestamp authTime;

    private String realName;

    private String idNumber;


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
