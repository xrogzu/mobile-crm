package com.rkhd.ienterprise.apps.ingage.wx.utils;


import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;

/**
 * Created by dell on 2016/4/18.
 */
public class WxRuntime {
    private  static  String suiteTicket = null;
    private  static String suite_access_token = null;//获取应用套件令牌
    private  static String pre_auth_code = null;//获取预授权码

    public static final String suiteTicketKey = "wx.suiteTicket";
    public static final String suite_access_tokenKey = "wx.suite_access_token";
    public static final String pre_auth_codeKey = "wx.pre_auth_code";

    public static String getSuiteTicket (){
        return suiteTicket != null?suiteTicket:SetSystemProp.getKeyValue(suiteTicketKey)!=null?SetSystemProp.getKeyValue(suiteTicketKey):null;
    }
    public static String getSuite_access_token (){
        return suite_access_token != null?suite_access_token:SetSystemProp.getKeyValue(suite_access_tokenKey)!=null?SetSystemProp.getKeyValue(suite_access_tokenKey):null;
    }
    public static String getPre_auth_code (){
        return pre_auth_code != null?pre_auth_code:SetSystemProp.getKeyValue(pre_auth_codeKey)!=null?SetSystemProp.getKeyValue(pre_auth_codeKey):null;
    }

    public static void setSuiteTicket(String suiteTicket) {
        WxRuntime.suiteTicket = suiteTicket;
    }

    public static void setSuite_access_token(String suite_access_token) {
        WxRuntime.suite_access_token = suite_access_token;
    }

    public static void setPre_auth_code(String pre_auth_code) {
        WxRuntime.pre_auth_code = pre_auth_code;
    }
}
