package com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;


public class TagMemberRequest  extends WXRequest<JSONObject> {

    public TagMemberRequest(String access_token  ,String tagid  ) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=ACCESS_TOKEN&tagid=TAGID
        //以下拼凑url路径
        this.path = "/cgi-bin/tag/get";

        this.parameters.put("tagid", tagid);
        this.parameters.put("access_token", access_token);

    }

    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
