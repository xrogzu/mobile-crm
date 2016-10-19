package com.rkhd.ienterprise.apps.ingage.wx.sdk.user.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.base.WXRequest;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXBaseUrl;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.enums.WXHTTPMethod;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by dell on 2016/4/19.
 */
public class DepartmentUserInfoListRequest extends WXRequest {
    //https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD&status=STATUS
    //https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD&status=STATUS
    /**
     *
     * @param access_token
     * @param department_id     必填  获取的部门id
     * @param fetch_child       可选   1/0：是否递归获取子部门下面的成员
     * @param status             可选   0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加，未填写则默认为4
     * @throws WXException
     */
    public DepartmentUserInfoListRequest(String access_token  ,String department_id ,int fetch_child,String status ) throws WXException {
        super(WXBaseUrl.qyapiurl);
        this.method = WXHTTPMethod.GET;
        //以下拼凑url路径
        this.path = "/cgi-bin/user/list";
        this.addParameter("access_token", access_token);
        this.addParameter("department_id", department_id);
        this.addParameter("fetch_child", fetch_child+"");
        if(StringUtils.isNotEmpty(status)){
            this.addParameter("status", status );
        }
    }

    public JSONObject createResponse(String body) throws WXException {
        return JSON.parseObject(body);
    }
}
