package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;

import java.io.Serializable;

/**
 * Created by dell on 2016/1/21.
 */
public class DingSessionUser implements Serializable{

    private static final long serialVersionUID = 2232133836467216176L;

    private String thirdcorpid;

    private String ding_access_token;

    private String ding_id;

    private String ding_userId;

    private String  xsy_user_token;

    private TenantParam xsy_TenantParam;

    private XsyUserDto xsy_user;

    private RelThirdToken relThirdToken;

    private String xsy_TenantName;

    private boolean normal;

    private boolean manager;

    private boolean systemManager;

    public String getThirdcorpid() {
        return thirdcorpid;
    }

    public void setThirdcorpid(String thirdcorpid) {
        this.thirdcorpid = thirdcorpid;
    }

    public String getDing_access_token() {
        return ding_access_token;
    }

    public void setDing_access_token(String ding_access_token) {
        this.ding_access_token = ding_access_token;
    }

    public String getDing_id() {
        return ding_id;
    }

    public void setDing_id(String ding_id) {
        this.ding_id = ding_id;
    }

    public String getDing_userId() {
        return ding_userId;
    }

    public void setDing_userId(String ding_userId) {
        this.ding_userId = ding_userId;
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
}
