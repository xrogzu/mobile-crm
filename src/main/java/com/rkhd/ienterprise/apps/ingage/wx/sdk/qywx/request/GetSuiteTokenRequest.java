package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.response.GetSuiteTokenResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GetSuiteTokenRequest extends WXRequest<JSON> {

    private  static Logger LOG = LoggerFactory.getLogger(GetSuiteTokenRequest.class);

    public GetSuiteTokenRequest(String suite_id, String suite_secret, String suite_ticket) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
       //以下拼凑url路径

        this.path ="/cgi-bin/service/get_suite_token";
        //以下拼凑传递参数
        JSONObject bodyParams = new JSONObject();
        bodyParams.put("suite_id",suite_id);
        bodyParams.put("suite_secret",suite_secret);
        bodyParams.put("suite_ticket",suite_ticket);
        //post 方法参数放到body里
        this.body  = bodyParams.toJSONString();
    }

    public JSON createResponse(String body) throws WXException {

        return JSON.parseObject(body);
    }
}
