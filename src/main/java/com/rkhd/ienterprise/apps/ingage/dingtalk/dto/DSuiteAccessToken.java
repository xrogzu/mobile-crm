package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

/**
 * 钉钉对象
 */
public class DSuiteAccessToken implements Serializable{
    //应用套件access_token
    private String suite_access_token;
    //有效期
    private long  expires_in;

    public String getSuite_access_token() {
        return suite_access_token;
    }

    public void setSuite_access_token(String suite_access_token) {
        this.suite_access_token = suite_access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }
}
