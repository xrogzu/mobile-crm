package com.rkhd.ienterprise.apps.ingage.wx.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created by dell on 2016/4/28.
 */
public class WxUtils {

    public  static JSONObject wxUserPassportLoginInfo(String mobile,String email,String weixinAccountid){
        String loginName = null;
        String contact = null;
          if(StringUtils.isNotEmpty(mobile)){
            loginName = mobile;
            contact = email;
        } else if(StringUtils.isNotEmpty(email)){
            loginName = email;
            contact = mobile;
        }
        if(StringUtils.isBlank(email ) && StringUtils.isBlank(mobile)){
            loginName = weixinAccountid+"@qq.com";
            contact = "";
        }
        JSONObject retJson = new JSONObject();
        retJson.put("loginName",loginName);
        retJson.put("contact",contact);
        return retJson;
    }
}
