package com.rkhd.ienterprise.apps.ingage.wx.sdk.department.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import org.apache.commons.lang.StringUtils;


public class DepartmentListRequest extends WXRequest {

    public DepartmentListRequest(String access_token  ,String departmentId  ) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //以下拼凑url路径
        //https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
        this.path = "/cgi-bin/department/list";
        this.addParameter("access_token", access_token);
        if(StringUtils.isNotEmpty(departmentId)){
            this.addParameter("id", departmentId);
        }

    }
    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
