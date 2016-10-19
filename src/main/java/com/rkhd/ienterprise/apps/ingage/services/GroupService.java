package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/1/25.
 */
@Component("mwebGroupService")
public class GroupService {
    private  static Logger LOG = LoggerFactory.getLogger(GroupService.class);

    /***
     * 添加负责人团队成员
     * @param authorization
     * @param belongs 取值范围 1:客户,2：联系人,3:销售机会
     * @param businessId  业务id
     * @param userIds   用户ID列表
     * @return
     */
    public EntityReturnData joinOwner(String authorization, long belongs,long businessId , List<Long> userIds)  {
        String url = String.format("%s/data/v1/objects/group/join-owner", SystemGlobals.getPreference("crmapi.host.url"));

        try {
            Map<String, String> params = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("belongId",belongs);
            jsonObject.put("businessId",businessId);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<userIds.size();i++){
                JSONObject json = new JSONObject();
                json.put("id",userIds.get(i));
                jsonArray.add(json);
            }
            jsonObject.put("users",jsonArray);
            params.put("params",jsonObject.toString());
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization, url, params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;

        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /***
     *  添加相关员工
     * @param authorization
     * @param belongs 取值范围 1:客户,2：联系人,3:销售机会
     * @param businessId  业务id
     * @param userIds   用户ID列表
     * @return
     */
    public EntityReturnData joinRelated(String authorization, long belongs,long businessId , List<Long> userIds)  {
        String url = String.format("%s/data/v1/objects/group/join-related", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            Map<String, String> params = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("belongId",belongs);
            jsonObject.put("businessId",businessId);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<userIds.size();i++){
                JSONObject json = new JSONObject();
                json.put("id",userIds.get(i));
                jsonArray.add(json);
            }
            jsonObject.put("users",jsonArray);
            params.put("params",jsonObject.toString());
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization, url, params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;

        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }
    /***
     *  删除团队成员
     * @param authorization
     * @param belongs 取值范围 1:客户,2：联系人,3:销售机会
     * @param businessId  业务id
     * @param userIds   用户ID列表
     * @return
     */
    public EntityReturnData quitMember(String authorization,  long belongs,long businessId , List<Long> userIds)  {
        String url = String.format("%s/data/v1/objects/group/quit-member", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            Map<String, String> params = new HashMap<String, String>();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("belongId",belongs);
            jsonObject.put("businessId",businessId);
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<userIds.size();i++){
                JSONObject json = new JSONObject();
                json.put("id",userIds.get(i));
                jsonArray.add(json);
            }
            jsonObject.put("users",jsonArray);
            params.put("params",jsonObject.toString());
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization, url, params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;

        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /***
     *  查询团队成员
     * @param authorization
     * @param businessId    实体id
     * @param belongs 取值范围 1:客户,2：联系人,3:销售机会
     * @param ownerFlag    1 负责员工 ,0: 相关员工, 不传：全部
     * @return
     */
    public EntityReturnData queryMember(String authorization, long businessId, long belongs , long  ownerFlag )  {
        String url = String.format("%s/data/v1/objects/group/query-member", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            Map<String, String> params = new HashMap<String, String>();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("belongId",belongs);
            jsonObject.put("ownerFlag",ownerFlag);
            jsonObject.put("businessId",businessId);
            params.put("params",jsonObject.toJSONString());

            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization, url, params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;

        } catch (IOException e) {
            e.printStackTrace();
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }
}
