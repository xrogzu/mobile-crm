package com.rkhd.ienterprise.apps.ingage.wx.sdk.user;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXHttpDispatch;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.ByCodeRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.DepartmentUserInfoListRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.DepartmentUserListRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request.UserInfoRequest;


public class UserService {

    public JSONObject get_user(String access_token  ,String userid) throws WXException {
        UserInfoRequest request = new UserInfoRequest(access_token  , userid);
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject getDepartment_user_simplelist(String access_token  ,String department_id ,int fetch_child,String status) throws WXException {
        DepartmentUserListRequest request = new DepartmentUserListRequest( access_token  , department_id , fetch_child, status );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject getDepartment_user_list(String access_token  ,String department_id ,int fetch_child,String status) throws WXException {
        DepartmentUserInfoListRequest request = new DepartmentUserInfoListRequest( access_token  , department_id , fetch_child, status );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }

    public JSONObject getUserByCode(String access_token  ,String code) throws WXException {
        ByCodeRequest request = new ByCodeRequest( access_token  , code );
        JSONObject response = (JSONObject) WXHttpDispatch.execute(request);
        return response;
    }
}
