package com.rkhd.ienterprise.apps.ingage.wx.sdk.message.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

/**
 * 发送消息接口
 */
public class MessageSendRequest extends WXRequest {

    public MessageSendRequest(String access_token, String subAbstractWxMessageJSONString) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径
        this.path = "/cgi-bin/message/send";
        this.addParameter("access_token", access_token);



        //post 方法参数放到body里
        this.body = subAbstractWxMessageJSONString;
    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
