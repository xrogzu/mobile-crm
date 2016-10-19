package com.rkhd.ienterprise.apps.ingage.wx.sdk.isv.logininfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;


public class GetLoginInfoService {

    public JSONObject getLoginInfo(String access_token  ,String auth_code) throws  WXException {
        GetLoginInfoRequest request = new GetLoginInfoRequest( access_token  , auth_code);
        JSONObject json = (JSONObject)WXHttpDispatch.execute(request);

        return json;
    }
}
