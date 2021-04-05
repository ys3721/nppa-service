package com.iceicelee.nppaservice.pojo;

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
}
