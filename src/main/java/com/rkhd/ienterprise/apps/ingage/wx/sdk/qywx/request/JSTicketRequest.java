package com.rkhd.ienterprise.apps.ingage.wx.sdk.qywx.request;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;

public class JSTicketRequest extends WXRequest {
    /**
     * 参考http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BE%AE%E4%BF%A1JS-SDK%E6%8E%A5%E5%8F%A3#.E6.AD.A5.E9.AA.A4.E4.BA.8C.EF.BC.9A.E9.80.9A.E8.BF.87config.E6.8E.A5.E5.8F.A3.E6.B3.A8.E5.85.A5.E6.9D.83.E9.99.90.E9.AA.8C.E8.AF.81.E9.85.8D.E7.BD.AE
     * @param access_token
     * @throws WXException
     */
    //https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=ACCESS_TOKEN
    public JSTicketRequest(String access_token) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //以下拼凑url路径

        this.path ="/cgi-bin/get_jsapi_ticket";
        this.addParameter("access_token", access_token);

        //post 方法参数放到body里
    }

    public JSON createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
