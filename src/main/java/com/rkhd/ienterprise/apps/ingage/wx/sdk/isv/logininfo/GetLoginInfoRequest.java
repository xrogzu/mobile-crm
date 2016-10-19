package com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.logininfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

/**
 * 参考：
 * http://qydev.weixin.qq.com/wiki/index.php?title=%E6%88%90%E5%91%98%E7%99%BB%E5%BD%95%E6%8E%88%E6%9D%83
 */
public class GetLoginInfoRequest extends WXRequest<JSONObject> {

    public GetLoginInfoRequest(String access_token  ,String auth_code  ) throws  WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.POST;
        //以下拼凑url路径
        this.path = "/cgi-bin/service/get_login_info";
        this.addParameter("access_token", access_token);

        JSONObject bodyParams = new JSONObject();
        bodyParams.put("auth_code", auth_code);
        //post 方法参数放到body里
        this.body = bodyParams.toJSONString();

    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
