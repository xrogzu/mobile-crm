package com.rkhd.ienterprise.apps.ingage.global.dto;

/**
 * User: teddy zhang
 * Date: 14-4-9
 * Time: 上午11:51
 * Copyright: Ingageapp.com, Inc.
 */
public class Login {

    private long passportId;
    private long userId;
    private long createdAt;
    private long tenantId;
    private String model;//机器型号 iphone4/5  Huawwei  PC
    private String source; //来源  1,2,3
    private String os;                  //系统信息 android版本 ios版本 浏览器版本
    private String appVersion; //手机应用程序版本





    //扩展字段
//    private String source;
//    private String systemVersion;


    public long getPassportId() {
        return passportId;
    }

    public void setPassportId(long passportId) {
        this.passportId = passportId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String toString() {
        return "Login{" +
                "passportId=" + passportId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", tenantId=" + tenantId +
                ", model='" + model + '\'' +
                ", source='" + source + '\'' +
                ", os='" + os + '\'' +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
