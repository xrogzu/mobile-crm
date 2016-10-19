package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

/**
 *获取企业号的永久授权码
 * 参考：
 * http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BA%94%E7%94%A8%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
 */
public class GetPermanentCodeRequest extends WXRequest {

    public GetPermanentCodeRequest(String suite_access_token  ,String suite_id  , String auth_code) throws WXException {
        super();
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径
        //https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=SUITE_ACCESS_TOKEN
        this.baseUrl = WXBaseUrl.qyapiurl;
        this.path = "/cgi-bin/service/get_permanent_code";
        this.addParameter("suite_access_token", suite_access_token);

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("suite_id", suite_id);
        bodyParams.put("auth_code", auth_code);
        //post 方法参数放到body里
        this.body = bodyParams.toJSONString();

    }

    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}