package com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import org.apache.commons.lang.StringUtils;


public class UserInfoRequest extends WXRequest {
//https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=USERID
    public UserInfoRequest(String access_token  ,String userid  ) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //以下拼凑url路径
        this.path = "/cgi-bin/user/get";
        this.addParameter("access_token", access_token);
        this.addParameter("userid", userid);

    }

    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
