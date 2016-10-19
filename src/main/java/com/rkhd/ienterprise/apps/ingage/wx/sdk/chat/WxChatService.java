package com.rkhd.ienterprise.apps.ingage.wx.sdk.chat;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.chat.request.ChatSendRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WxChatService {

    private  static Logger LOG = LoggerFactory.getLogger(WxChatService.class);

    public JSONObject chatSend(String access_token, String chatJSONString) throws WXException {
        ChatSendRequest request = new ChatSendRequest( access_token, chatJSONString);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }
}
