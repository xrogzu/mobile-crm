package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;


public class ChatSendRequest extends WXRequest {

//    {
//        "receiver":
//        {
//            "type": "single",
//                "id": "lisi"
//        },
//        "sender": "zhangsan",
//            "msgtype": "text",
//            "text":
//        {
//            "content": "Holiday Request For Pony(http://xxxxx)"
//        }
//    }
    public ChatSendRequest (String access_token, String chatJSONString) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径

        this.path = "/cgi-bin/chat/send";
        this.addParameter("access_token", access_token);
        //post 方法参数放到body里
        this.body = chatJSONString;
    }

    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
