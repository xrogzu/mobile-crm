package com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

/**
 * 获取应用提供商凭证
 */
public class GetProviderTokenRequest  extends WXRequest<JSONObject> {

    public GetProviderTokenRequest(String corpid  ,String provider_secret  ) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径
        this.path = "/cgi-bin/service/get_provider_token";

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("corpid", corpid);
        bodyParams.put("provider_secret", provider_secret);
        //post 方法参数放到body里
        this.body = bodyParams.toJSONString();

    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
