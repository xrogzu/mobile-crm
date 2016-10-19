package com.rkhd.ienterprise.apps.ingage.wx.sdk.department;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.department.request.DepartmentListRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;


public class DepartmentService {

    public JSONObject getDepartmentList(String access_token  , String departmentId  ) throws WXException {
        DepartmentListRequest request = new DepartmentListRequest(access_token,departmentId);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }
}
