package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

/**
 * 获取企业号access_token
 */
public class GetCorpTokenRequest  extends WXRequest {
    //https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN

//    参数 	说明
//    suite_id 	应用套件id
//    auth_corpid 	授权方corpid
//    permanent_code 	永久授权码，通过get_permanent_code获取

    public GetCorpTokenRequest(String suite_access_token,String suite_id,String auth_corpid,String permanent_code){
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径

        this.path = "/cgi-bin/service/get_corp_token";
        this.addParameter("suite_access_token", suite_access_token);

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("suite_id", suite_id);
        bodyParams.put("auth_corpid", auth_corpid);
        bodyParams.put("permanent_code", permanent_code);
        //post 方法参数放到body里
        this.body = bodyParams.toJSONString();

    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
