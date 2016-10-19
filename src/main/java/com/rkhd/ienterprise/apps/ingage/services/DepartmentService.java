package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2016/1/7.
 */
@Component("mwebDepartmentService")
public class DepartmentService {
    private  static Logger LOG = LoggerFactory.getLogger(DepartmentService.class);


    /**
     * 部门描述
     * @param authorization
     * @return
     */
    public EntityReturnData getDepartmentDesc(String authorization)  {
        String url = String.format("%s/data/v1/objects/depart/describe", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
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
     * 查询所有公司列表
     * @param authorization
     * @return
     */
    public EntityReturnData getDepartmentList(String authorization)  {
        String url = String.format("%s/data/v1/objects/depart/tree", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
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

    public EntityReturnData createDepartment(String authorization,Department department)  {
        String url = String.format("%s/data/v1/objects/depart/create", SystemGlobals.getPreference("crmapi.host.url"));

        JSONObject json = new JSONObject();
        String department_string = JSON.toJSONString(department);
        JSONObject department_json = JSON.parseObject(department_string);
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("record", department_json);
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
    public EntityReturnData updateDepartment(String authorization,Department department)  {
        String url = String.format("%s/data/v1/objects/depart/update", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, Object> params = new HashMap<String, Object>();

        JSONObject record  = (JSONObject) JSON.toJSON(department);
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
    public EntityReturnData getDepartmentById(String authorization,long departmentId)  {
        String url = String.format("%s/data/v1/objects/depart/info", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,String> params = new HashMap<String, String>();
        params.put("id",""+departmentId);
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
    /**
     * 删除部门
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData deleteDeparment(String authorization,long id)  {
        String url = String.format("%s/data/v1/objects/depart/delete", SystemGlobals.getPreference("crmapi.host.url"));
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


    public EntityReturnData getMyDepartmenttree(String authorization)  {
        String url = String.format("%s/data/v1/objects/depart/dimtree", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        try {
            params.put("belongId","1");
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
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
