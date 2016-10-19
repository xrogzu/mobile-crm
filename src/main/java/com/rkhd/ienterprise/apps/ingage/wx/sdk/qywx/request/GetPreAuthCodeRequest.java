package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response.GetPreAuthCodeResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXJsonUtil;


public class GetPreAuthCodeRequest extends WXRequest {

    public GetPreAuthCodeRequest(String suite_access_token,String suite_id ) throws WXException {
        super();
        this.method = WXHTTPMethod.POST;
       //以下拼凑url路径
        //https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=SUITE_ACCESS_TOKEN
        this.baseUrl  =  WXBaseUrl.qyapiurl;
        this.path ="/cgi-bin/service/get_pre_auth_code";
        this.addParameter("suite_access_token", suite_access_token);

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("suite_id",suite_id);
        //post 方法参数放到body里
        this.body  = bodyParams.toJSONString();

    }

    public GetPreAuthCodeResponse createResponse(String body) throws WXException {
        return WXJsonUtil.jsonToBean(body, GetPreAuthCodeResponse.class);
    }
}
