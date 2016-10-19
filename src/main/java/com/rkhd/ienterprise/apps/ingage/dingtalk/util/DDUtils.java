package com.rkhd.ienterprise.apps.ingage.dingtalk.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 钉钉工具类
 */
public class DDUtils {

    public static  String getLoginNameByDingDingId(String dingId){
        return String.format("%s@dingtalk.com",dingId);
    }
    public static boolean isEmail(String arg){
        if(StringUtils.isEmpty(arg)){
            return false;
        }
        return arg.contains("@");
    }
}
