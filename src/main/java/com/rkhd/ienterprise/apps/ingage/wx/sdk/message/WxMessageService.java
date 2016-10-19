package com.rkhd.ienterprise.apps.ingage.wx.sdk.message;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.entitys.AbstractWxMessage;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.message.request.MessageSendRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.QyWxhApiService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request.GetSuiteTokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dell on 2016/7/1.
 */
public class WxMessageService {
    private  static Logger LOG = LoggerFactory.getLogger(WxMessageService.class);

    public JSONObject sendMsg(String access_token, String subAbstractWxMessageJSONString) throws WXException {
        MessageSendRequest request = new MessageSendRequest( access_token, subAbstractWxMessageJSONString);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }
}
