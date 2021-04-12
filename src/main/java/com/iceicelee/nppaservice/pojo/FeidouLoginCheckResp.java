package com.iceicelee.nppaservice.pojo;

/**
 * @author: Yao Shuai
 * @date: 2021/4/12 19:50
 *
 * ok:%1:%2:%3:%4:%5:%6	%1表示用户ID（userid）
 * %2表示用户名（username）
 * %3该用户是否为防沉迷对象（%3= 0不是，%3=1是）
 * %4 该用户防沉迷信息是否完整（%4= 0不完整，%4=1完整）
 * %5 该用户的防沉迷信息生日（如防沉迷信息完整的话，返回格式为1987-01-01，否则为0）
 * %6 表示用户的在线累计秒数
 *
 * fail:%1:%2	%2代表验证码地址，仅当%1=8时会出现。
 * %1表示错误信息数字代号，其代号的具体含义如下：
 * 1 ：	签名验证失败
 * 2 ：	时间戳过期
 * 3 ：	有参数为空或格式不正确
 * 4 ：	用户已经被平台锁定
 * 5 ：	非法用户（预留）
 * 6 ：	用户名密码（或验证码）验证未通过
 * 7 ：	cookie验证未通过
 * 8 ：	密码错误5次时，需要用户输入图形验证码，此时%2代表验证码地址
 * 9 ：	尚未绑定手机号用户，禁止登陆web版游戏。（仅判断 device=pc时，此功能默认不开启）
 * 11： 帐号未激活（预留）
 * 999 ：系统异常，登录操作不成功
 */
public class FeidouLoginCheckResp extends FeidouPlatformResponse {

    private long userId;

    private String userName;

    /**
     * 该用户是否为防沉迷对象（0不是，1是)
     */
    private int needPreventAddict;

    /**
     * 该用户防沉迷信息是否完整（%4= 0不完整，%4=1完整）
     */
    private int addictInfoCompletion;

    /**
     * 返回格式为1987-01-01，否则为0
     */
    private String bothDayInfo;

    /**
     * 表示用户的在线累计秒数
     */
    private int onlineSecondToday;

    /**
     * 失败就这一个信息吧先
     */
    private int errorCode;

    public FeidouLoginCheckResp() {
    }

    public long getUserId() {
        return userId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNeedPreventAddict() {
        return needPreventAddict;
    }

    public void setNeedPreventAddict(int needPreventAddict) {
        this.needPreventAddict = needPreventAddict;
    }

    public int getAddictInfoCompletion() {
        return addictInfoCompletion;
    }

    public void setAddictInfoCompletion(int addictInfoCompletion) {
        this.addictInfoCompletion = addictInfoCompletion;
    }

    public String getBothDayInfo() {
        return bothDayInfo;
    }

    public void setBothDayInfo(String bothDayInfo) {
        this.bothDayInfo = bothDayInfo;
    }

    public int getOnlineSecondToday() {
        return onlineSecondToday;
    }

    public void setOnlineSecondToday(int onlineSecondToday) {
        this.onlineSecondToday = onlineSecondToday;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    protected void parseDetails() {
        if (this.isOk()) {
            userId = Long.parseLong(this.getResponseParams().get(0));
            if (userId <= 0) {
                throw new IllegalArgumentException();
            }
            userName = this.getResponseParams().get(1);
            needPreventAddict = Integer.parseInt(this.getResponseParams().get(2));
            addictInfoCompletion = Integer.parseInt(this.getResponseParams().get(3));
            bothDayInfo = this.getResponseParams().get(4);
            onlineSecondToday = Integer.parseInt(this.getResponseParams().get(5));
        } else {
            errorCode = Integer.parseInt(this.getResponseParams().get(0));
        }
    }

    public String toResponseString() {
        StringBuilder sb = new StringBuilder();
        if (this.isOk()) {
            sb.append(this.getOkOrFail()).append(":").append(userId).append(":").append(userName)
                    .append(":").append(needPreventAddict).append(":").append(addictInfoCompletion)
                    .append(":").append(bothDayInfo).append(":").append(onlineSecondToday);
        } else {
            sb.append(this.getOkOrFail()).append(":").append(errorCode);
        }
        return sb.toString();
    }

}
