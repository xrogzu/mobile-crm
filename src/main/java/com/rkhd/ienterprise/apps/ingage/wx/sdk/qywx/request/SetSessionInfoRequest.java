package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response.SetSessionInfoResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXJsonUtil;


public class SetSessionInfoRequest extends WXRequest {

    public SetSessionInfoRequest(String suite_access_token, String pre_auth_code,JSONArray appid) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径

        this.path ="/cgi-bin/service/set_session_info";
        //以下拼凑传递参数
        this.addParameter("suite_access_token",suite_access_token);


        JSONObject bodyParams = new JSONObject();
        bodyParams.put("pre_auth_code",pre_auth_code);

        JSONObject session_info = new JSONObject();
        session_info.put("appid",appid.toString());

        bodyParams.put("session_info",session_info);
        //post 方法参数放到body里
        this.body  = bodyParams.toJSONString();
    }
    public WXResponse createResponse(String body) throws WXException {
        return WXJsonUtil.jsonToBean(body, SetSessionInfoResponse.class);
    }
}
