package com.rkhd.ienterprise.apps.ingage.wx.sdk.security.request;


import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.security.response.WXServerIpListResponse;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.utils.WXJsonUtil;

/**
 * Created by lemon_bar on 15/7/23.
 */
public class WXServerIplistRequest extends WXRequest<WXServerIpListResponse> {
    public WXServerIplistRequest(String token) {
        super();
        this.method = WXHTTPMethod.GET;
        this.path = "/cgi-bin/getcallbackip";
        this.addParameter("access_token", token);
    }

    @Override
    public WXServerIpListResponse createResponse(String body) throws WXException {
        return WXJsonUtil.jsonToBean(body, WXServerIpListResponse.class);
    }
}
