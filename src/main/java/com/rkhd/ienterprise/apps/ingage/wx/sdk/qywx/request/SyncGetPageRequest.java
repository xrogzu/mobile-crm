package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

public class SyncGetPageRequest  extends WXRequest {

    public SyncGetPageRequest(String access_token,int seq,int offset ) throws WXException {
        super();
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径
        //https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=SUITE_ACCESS_TOKEN
        this.baseUrl  =  WXBaseUrl.qyapiurl;
        this.path ="/cgi-bin/sync/getpage";
        this.addParameter("access_token", access_token);

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("seq",seq);
        bodyParams.put("offset",offset);
        //post 方法参数放到body里
        this.body  = bodyParams.toJSONString();

    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
