package com.iceicelee.nppaservice.pojo;

import com.iceicelee.nppaservice.constants.AuthenticationConstants.AuthenticationStatus;
import com.iceicelee.nppaservice.utils.EncryptUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

/**
 * 代表一用户吧
 *
 * @author: Yao Shuai
 * @date: 2021/4/2 15:58
 */
public class User {

    private long id;

    private int gameId;

    private String pi;

    private String passportName;

    private Timestamp createTime;

    private AuthenticationStatus authStatus;

    private Timestamp authTime;

    private String realName;

    private String idNumber;

    public User() {
    }

    public User(long id) {
        this.id = id;
    }

    public User(long id, int gameId, String pi, String passportName, Timestamp createTime,
                AuthenticationStatus authStatus, Timestamp authTime, String realName, String idNumber) {
        this.id = id;
        this.gameId = gameId;
        this.pi = pi;
        this.passportName = passportName;
        this.createTime = createTime;
        this.authStatus = authStatus;
        this.authTime = authTime;
        this.realName = realName;
        this.idNumber = idNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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

    public AuthenticationStatus getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(AuthenticationStatus authStatus) {
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

    public String getBirthdayIntFromPi() {
        if (StringUtils.isEmpty(pi)) {
            return "0";
        }
        //前六位是生日部分 1hgdci
        String birthdayPiStr = this.getPi().substring(0, 6);
        try {
            long birthLong = EncryptUtils.twentySix2Decimal(birthdayPiStr);
            StringBuilder stringBuilder = new StringBuilder(birthLong+"");
            //弄成这种格式1994-03-02
            stringBuilder.insert(4,"-");
            stringBuilder.insert(7,"-");
            return stringBuilder.toString();
        } catch (Exception e) {
            return "1900-01-01";
        }
    }
}
