package com.rkhd.ienterprise.apps.ingage.wx.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.enums.SynErrorMsg;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.WxDepartment;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WxAuthHelper {
    private static final Logger LOG = LoggerFactory.getLogger(WxAuthHelper.class);
    private WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private AuthCompositionHelper authCompositionHelper;



    /**
     *  只同步企业和管理员，通讯录信息异步执行
     * @param auth_code
     * @param wxInfoType
     * @return
     */
    public JSONObject doauth_new(String auth_code, WxInfoType wxInfoType){
        JSONObject retJson = new JSONObject();
        retJson.put("success",true);
        retJson.put("error",null);

        String suitaccess_token = WxRuntime.getSuite_access_token();
        try {
            JSONObject authJson  = wxApi.get_permanent_code(suitaccess_token, WXAppConstant.APP_ID,auth_code);
            LOG.info("authJson="+ JSON.toJSONString(authJson));
            String corp_access_token = authJson.getString("access_token");//授权方（企业）access_token
            /**
             "auth_user_info":
             {
             "email":"xxxx@aaa.com",
             "mobile":"1234567890",
             "userid":"aa"
             }
             */

            //下面开始同步通讯录
            JSONObject departmentListJson =  wxApi.getDepartmentList(corp_access_token,null);//所有部门
            JSONArray allAuthPersons = new JSONArray();

            JSONObject auth_info = authJson.getJSONObject("auth_info");
            JSONArray agents = auth_info.getJSONArray("agent");
            JSONObject agent_0 = agents.getJSONObject(0);
            JSONObject privilege = agent_0.getJSONObject("privilege");
            JSONArray extra_users = privilege.getJSONArray("extra_user");
            JSONArray allow_users = privilege.getJSONArray("allow_user");
            JSONArray  extra_tags = privilege.getJSONArray("extra_tag");
            JSONArray allow_tags = privilege.getJSONArray("allow_tag");

            if(departmentListJson.getInteger("errcode").intValue() == 0) {
                JSONArray wxDepartmentList   = departmentListJson.getJSONArray("department");
                if(wxDepartmentList != null ){
                    JSONObject wxdepartment = null;
                    for(int i=0;i<wxDepartmentList.size();i++){
                        wxdepartment = wxDepartmentList.getJSONObject(i);
                        String partyId = wxdepartment.getString("id");

                        //LOG.info("wxdepartment="+JSON.toJSONString(wxdepartment));
                        //同步当前部门下的员工
                        JSONObject userListJson = wxApi.getDepartment_user_list(corp_access_token,partyId,0,0+"");
                        //LOG.info("userListJson="+JSON.toJSONString(userListJson));

                        if(userListJson.getIntValue("errcode") == 0) {
                            JSONArray userlist = userListJson.getJSONArray("userlist");
                            JSONObject wxUserItem = null;
                            for (int j = 0; j < userlist.size(); j++) {
                                wxUserItem = userlist.getJSONObject(j) ;
                                wxUserItem.put("from","party_"+partyId);
                                allAuthPersons.add(wxUserItem);
                            }
                        }


                    }
                }
            }
            if(extra_users != null) {
                    JSONObject wxUserItem = null;
                    for(int i=0;i<extra_users.size();i++){
                        String userid = extra_users.getString(i);
                        wxUserItem =  wxApi.get_user(corp_access_token,userid);
                        wxUserItem.put("from","extra_users");
                        allAuthPersons.add(wxUserItem);
                    }
            }
            if(allow_users != null) {
                JSONObject wxUserItem = null;
                for(int i=0;i<allow_users.size();i++){
                    String userid = allow_users.getString(i);
                    wxUserItem =  wxApi.get_user(corp_access_token,userid);
                    wxUserItem.put("from","allow_users");
                    allAuthPersons.add(wxUserItem);
                }
            }

            if(extra_tags != null){
                String wxTagId= null;
                JSONObject wxUserItem = null;
                for(int i=0;i<extra_tags.size();i++){
                    wxTagId = extra_tags.getString(i);
                    JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);

                    if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                        JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                        if(userlist != null){
                            String wxUserId = null;
                            for(int j=0;j<userlist.size();j++){
                                    wxUserId = userlist.getJSONObject(j).getString("userid");
                                    wxUserItem =  wxApi.get_user(corp_access_token,wxUserId);
                                wxUserItem.put("from","extra_tags");
                                    allAuthPersons.add(wxUserItem);
                            }
                        }
                    }
                }
            }

            if(allow_tags != null){
                String wxTagId= null;
                JSONObject wxUserItem = null;
                for(int i=0;i<allow_tags.size();i++){
                    wxTagId = allow_tags.getString(i);
                    JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);

                    if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                        JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                        if(userlist != null){
                            String wxUserId = null;
                            for(int j=0;j<userlist.size();j++){
                                wxUserId = userlist.getJSONObject(j).getString("userid");
                                wxUserItem =  wxApi.get_user(corp_access_token,wxUserId);
                                wxUserItem.put("from","allow_tags");
                                allAuthPersons.add(wxUserItem);
                            }
                        }
                    }
                }
            }
            if(allAuthPersons.size() == 0 ){
                LOG.info("没有授权任何人");
            }else{
                LOG.info("所有授权人的信息为：");
                for (int i=0;i<allAuthPersons.size();i++){
                    LOG.info(JSON.toJSONString(allAuthPersons.getJSONObject(i)));
                }
            }

        } catch (WXException e) {
            e.printStackTrace();
        }

        return retJson;
    }
    /**
     * 进行授权操作
     * @param auth_code
     * @return
     */
    public JSONObject doauth(String auth_code, WxInfoType wxInfoType){
        JSONObject retJson = new JSONObject();
        JSONObject authJson = null;
        boolean success = false;
        SynErrorMsg error = null;
        //执行实际授权操作
        try {
            String suitaccess_token = WxRuntime.getSuite_access_token();
            authJson = wxApi.get_permanent_code(suitaccess_token, WXAppConstant.APP_ID,auth_code);
            if(authJson.containsKey("errcode") && authJson.getLongValue("errcode")!=0){
                if(authJson.getLong("errcode").longValue() == 42008){
                    error = SynErrorMsg.AUTH_CODE_ERROR;
                    retJson.put("msg","auth_code过期了,请重新操作");
                    LOG.error("auth_code 过期了，请重试,authJson"+authJson.toJSONString());
                }else{
                    error = SynErrorMsg.SERVICE_ERROR;
                    LOG.error("authJson ="+authJson.toJSONString());
                }
                success = false;
            }else{
                JSONObject authJsonResult = authCompositionHelper.doSyncAuth(authJson,wxInfoType);
                success = authJsonResult.getBoolean("success");
                if(!success){
                    String errorMsg = authJsonResult.getString("errorMsg");
                    error =  SynErrorMsg.valueOf(errorMsg);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            error = SynErrorMsg.SERVICE_ERROR;
        }
        retJson.put("authJson",authJson);
        retJson.put("success",success);
        retJson.put("error",error);

        return retJson;

    }
}
