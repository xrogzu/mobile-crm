package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.utils.MapTools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("mwebScheduleService")
public class ScheduleService {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);

    public EntityReturnData desc(String authorization){
        String url = String.format("%s/data/v1/objects/schedule/describe", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            String sql = null;
            Map<String, String> params = new HashMap<String, String>();
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    public EntityReturnData docreate(String authorization ,JSONObject record){
        String url = String.format("%s/data/v1/objects/schedule/create", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,Object> params = MapTools.parseJSON2Map(record);
        String apiResultString = null;
        try {
            apiResultString = MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    public EntityReturnData doUpdate(String authorization ,JSONObject record){
        String url = String.format("%s/data/v1/objects/schedule/update", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,Object> params = MapTools.parseJSON2Map(record);
        String apiResultString = null;
        try {
            apiResultString = MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    public EntityReturnData get(String authorization ,Long id){
        String url = String.format("%s/data/v1/objects/schedule/info", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     * 拒绝某个日程
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData reject(String authorization ,Long id){
        String url = String.format("%s/data/v1/objects/schedule/reject", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }
    /**
     * 退出某个日程
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData quit(String authorization ,Long id){
        String url = String.format("%s/data/v1/objects/schedule/quit", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }
    /**
     * 接受某个日程
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData accept(String authorization ,Long id){
        String url = String.format("%s/data/v1/objects/schedule/accept", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     *  startDate	Long	是	开始日期；查询日程的开始日期大于此日期的数据
     *  endDate	Long	是	结束日期；查询日程的结束日期小于此日期的数据
     *  userId	Long	否	创建日程用户ID
     *  type	String	否	日程类型；支持多条查询，数据用逗号分隔
     *   belongId	Long	否	关联业务对象实体ID
     *  objectId	Long	否	关联业务对象数据ID
     */

    public EntityReturnData list(String authorization ,Long startTime,Long endDate,Long userId,String type,Long belongId,Long objectId){
        String url = String.format("%s/data/v1/objects/schedule/list", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        if(startTime > 0 ){
            params.put("startDate",""+startTime);
        }
        if(endDate > 0 ){
            params.put("endDate",""+endDate);
        }
        if(userId != null && userId > 0 ){
            params.put("userId",""+userId);
        }
        if(StringUtils.isNotBlank(type)){
            params.put("type",type);
        }
        if(belongId !=null && belongId > 0 ){
            params.put("belongId",""+belongId);
        }
        if(objectId != null && objectId > 0 ){
            params.put("objectId",""+objectId);
        }

        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }
    /**
     * 删除某个日程
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData delete(String authorization ,Long id){
        String url = String.format("%s/data/v1/objects/schedule/delete", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+id);
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e!=null?e.getMessage():"null");
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }


}
