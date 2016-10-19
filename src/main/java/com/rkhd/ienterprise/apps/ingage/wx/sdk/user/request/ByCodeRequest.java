package com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import org.apache.commons.lang.StringUtils;

/**
 * 参考：
 * http://qydev.weixin.qq.com/wiki/index.php?title=OAuth%E9%AA%8C%E8%AF%81%E6%8E%A5%E5%8F%A3
 */
public class ByCodeRequest extends WXRequest {

    public ByCodeRequest(String access_token  ,String code  ) throws WXException {
        //https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //以下拼凑url路径
        this.path = "/cgi-bin/user/getuserinfo";
        this.addParameter("access_token", access_token);
        this.addParameter("code", code);
    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
