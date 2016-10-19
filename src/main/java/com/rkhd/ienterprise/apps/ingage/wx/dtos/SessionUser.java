package com.rkhd.ienterprise.apps.ingage.wx.dtos;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;

import java.io.Serializable;


public class SessionUser  implements Serializable {
    private static final long serialVersionUID = -7889782291974733271L;

    private String thirdcorpid;

    private String third_access_token;

    private String third_platform_id;

    private String third_userId;

    private String  xsy_user_token;

    private TenantParam xsy_TenantParam;

    private XsyUserDto xsy_user;

    private RelThirdToken relThirdToken;

    private String xsy_TenantName;

    private boolean normal;

    private boolean manager;

    private boolean systemManager;

    private String jsticket;

    public String getThirdcorpid() {
        return thirdcorpid;
    }

    public void setThirdcorpid(String thirdcorpid) {
        this.thirdcorpid = thirdcorpid;
    }



    public String getXsy_user_token() {
        return xsy_user_token;
    }

    public void setXsy_user_token(String xsy_user_token) {
        this.xsy_user_token = xsy_user_token;
    }

    public XsyUserDto getXsy_user() {
        return xsy_user;
    }

    public void setXsy_user(XsyUserDto xsy_user) {
        this.xsy_user = xsy_user;
    }

    public RelThirdToken getRelThirdToken() {
        return relThirdToken;
    }

    public void setRelThirdToken(RelThirdToken relThirdToken) {
        this.relThirdToken = relThirdToken;
    }

    public TenantParam getXsy_TenantParam() {
        return xsy_TenantParam;
    }

    public void setXsy_TenantParam(TenantParam xsy_TenantParam) {
        this.xsy_TenantParam = xsy_TenantParam;
    }

    public String getXsy_TenantName() {
        return xsy_TenantName;
    }

    public void setXsy_TenantName(String xsy_TenantName) {
        this.xsy_TenantName = xsy_TenantName;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isSystemManager() {
        return systemManager;
    }

    public void setSystemManager(boolean systemManager) {
        this.systemManager = systemManager;
    }

    public String getThird_access_token() {
        return third_access_token;
    }

    public void setThird_access_token(String third_access_token) {
        this.third_access_token = third_access_token;
    }

    public String getThird_platform_id() {
        return third_platform_id;
    }

    public void setThird_platform_id(String third_platform_id) {
        this.third_platform_id = third_platform_id;
    }

    public String getThird_userId() {
        return third_userId;
    }

    public void setThird_userId(String third_userId) {
        this.third_userId = third_userId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJsticket() {
        return jsticket;
    }

    public void setJsticket(String jsticket) {
        this.jsticket = jsticket;
    }
}
