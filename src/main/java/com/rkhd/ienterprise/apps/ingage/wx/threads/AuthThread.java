package com.rkhd.ienterprise.apps.ingage.wx.threads;

import com.rkhd.ienterprise.apps.ingage.wx.helper.WxAuthHelper;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;

/**
 * Created by dell on 2016/7/1.
 */
public class AuthThread  extends Thread {

    private WxAuthHelper wxauthHelper;

    private String authCode;

    private WxInfoType wxInfoType;

    public AuthThread (WxAuthHelper wxauthHelper,String authCode,WxInfoType wxInfoType){
        this.wxauthHelper = wxauthHelper;
        this.authCode = authCode;
        this.wxInfoType = wxInfoType;
    }
    public void run(){
        wxauthHelper.doauth(authCode,wxInfoType);
    }
}
