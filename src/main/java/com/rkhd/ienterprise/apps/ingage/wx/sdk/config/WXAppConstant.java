package com.rkhd.ienterprise.apps.ingage.wx.sdk.config;


public class WXAppConstant {
    public static String TOKEN = "";
    public static String APP_ID = "";
    public static String APP_SECRET = "";
    public static String EncodingAESKey = "";
    public static String DOMAIN_NAME = "";
    public static String CorpID= "";
    public static String providersecret = "";

    public static void init(String appId, String appSecret, String appToken, String encodingAESKey, String domainName) {
        APP_ID = appId;
        APP_SECRET = appSecret;
        TOKEN = appToken;
        EncodingAESKey = encodingAESKey;
        DOMAIN_NAME = domainName;
    }

    public static void init(String appId, String appSecret, String appToken, String encodingAESKey) {
        APP_ID = appId;
        APP_SECRET = appSecret;
        TOKEN = appToken;
        EncodingAESKey = encodingAESKey;
    }
}
