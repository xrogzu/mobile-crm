package com.rkhd.ienterprise.apps.ingage.wx.dtos;


import java.io.Serializable;

public class WxMessageFlagEntity  implements Serializable {
    private static final long serialVersionUID = 6753808565543490272L;
    /**
     * 销售易passportID
     */
    private long xsy_passportId;
    /**
     * 销售易userId
     */
    private long xsy_userId;
    /**
     * 微信userId
     */
    private String wx_userid;
    /**
     * 销售易passportID是否新增
     */
    private boolean xsyPassportIsNew;
    /**
     * 销售易User是否新增
     */
    private boolean xsyUserIsNew;

    /**
     * 登录名
     */
    private String  loginName;
    /**
     * 密码
     */
    private String password;

    public long getXsy_passportId() {
        return xsy_passportId;
    }

    public void setXsy_passportId(long xsy_passportId) {
        this.xsy_passportId = xsy_passportId;
    }

    public long getXsy_userId() {
        return xsy_userId;
    }

    public void setXsy_userId(long xsy_userId) {
        this.xsy_userId = xsy_userId;
    }

    public String getWx_userid() {
        return wx_userid;
    }

    public void setWx_userid(String wx_userid) {
        this.wx_userid = wx_userid;
    }

    public boolean isXsyPassportIsNew() {
        return xsyPassportIsNew;
    }

    public void setXsyPassportIsNew(boolean xsyPassportIsNew) {
        this.xsyPassportIsNew = xsyPassportIsNew;
    }

    public boolean isXsyUserIsNew() {
        return xsyUserIsNew;
    }

    public void setXsyUserIsNew(boolean xsyUserIsNew) {
        this.xsyUserIsNew = xsyUserIsNew;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
