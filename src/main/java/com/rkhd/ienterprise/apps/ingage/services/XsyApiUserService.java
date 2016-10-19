package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2015/12/17.
 */
@Component("mwebXsyApiUserService")
public class XsyApiUserService {
    private static final Logger LOG = LoggerFactory.getLogger(XsyApiUserService.class);
    /**
     * 获取用户明细
     * @param authorization
     * @return
     */
    public EntityReturnData getUserInfo(String authorization,long id){

        String url = String.format("%s/data/v1/objects/user/info", SystemGlobals.getPreference("crmapi.host.url"));
        String apiResultString = null;
        try {

            Map<String, String> params = new HashMap<String, String>();
            params.put("id",""+id);
            apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);


            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        return  null;
    }

    /**
     * 获取用户描述
     * @param authorization
     * @return
     */
    public EntityReturnData getUserDesc(String authorization){

        String url = String.format("%s/data/v1/objects/user/describe", SystemGlobals.getPreference("crmapi.host.url"));
        String apiResultString = null;
        try {

            apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,null);

            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        return  null;
    }
    /**
     * 获取用户列表
     * @param authorization
     * @return
     */
    public EntityReturnData getUserList(String authorization,String searchName,int pageNo,int pagesize){
        String url = String.format("%s/data/v1/objects/user/list", SystemGlobals.getPreference("crmapi.host.url"));
        String apiResultString = null;
        try {
            apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,null);
            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        return  null;
    }
    /**
     * 创建用户
     * @param authorization
     *  @param xsyUser
     * @return
     */
    public EntityReturnData createUser(String authorization, XsyUserDto xsyUser){
        String url = String.format("%s/data/v1/objects/user/create", SystemGlobals.getPreference("crmapi.host.url"));
        JSONObject json = new JSONObject();
        String xsyUser_string = JSON.toJSONString(xsyUser);
        JSONObject xsyUser_json = JSON.parseObject(xsyUser_string);
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("record", xsyUser_json);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return returnData;
    }
    /**
     * 修改用户
     * @param authorization
     *  @param record
     * @return
     */
    public EntityReturnData updateUser(String authorization, JSONObject record){
        String url = String.format("%s/data/v1/objects/user/update", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, Object> params = new HashMap<String, Object>();

//        JSONObject record  = (JSONObject) JSON.toJSON(xsyUser);
        JSONObject data = new JSONObject();
        data.put("public",true);
        data.put("record",record);
        params = JSON.parseObject(data.toJSONString());
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return returnData;
    }
    /**
     * 删除用户
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData deleteUser(String authorization,long id)  {
        String url = String.format("%s/data/v1/objects/user/delete", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            return  APIResultUtil.executeResult(apiResultString);
        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return returnData;
    }
}
