package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ActivityRecordDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("mwebActivityRecordService")
public class ActivityRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityRecordService.class);

    public EntityReturnData getDesc(String authorization){
        String url = String.format("%s/data/v1/feed/activityRecord/describe", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("查看活动记录描述的URL：{}",url);
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
//            LOG.info("查看活动记录描述的cmsapi返回结果为：{}",apiResultString);
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
    /**
     * 查看活动记录列表
     * @param authorization
     * @param pageno
     * @param pagesize
     * @return
     */
    public EntityReturnData getPage(String authorization, int pageno , int pagesize,long belongId,long objectId,Date startTime,Date endTime ,long ownerId,JSONArray dimDepartArray ){
        String url = String.format("%s/data/v1/feed/activityRecord/list", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("查看活动记录列表的URL：{}",url);
        try {
            Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put("page",(pageno+1)+"");
            queryMap.put("pageSize",pagesize+"");
            Map<String, String> q = new HashMap<String, String>();
            if(belongId != 0){
                q.put("belongId",belongId+"");
                q.put("objectId",objectId+"");
            }
            if (startTime != null){
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                q.put("startTime",format1.format(startTime));
            }
            if (endTime != null){
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                q.put("endTime",format1.format(endTime));
            }
            if(ownerId != 0){
                q.put("ownerId",ownerId+"");
            }
            if(dimDepartArray != null && !dimDepartArray.isEmpty()){
                q.put("dimDepart", dimDepartArray.toJSONString());
            }

            if(!q.isEmpty()){
                queryMap.put("q",JSON.toJSONString(q));
            }

            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,queryMap);
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
    public EntityReturnData get(String authorization, int id){

        String url = String.format("%s/data/v1/feed/activityRecord/info", SystemGlobals.getPreference("crmapi.host.url"));
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id",id);
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
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

    public EntityReturnData addActivityRecord(String authorization, ActivityRecordDto activityRecordDto){

        String url = String.format("%s/data/v1/feed/activityRecord/create", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            JSONObject activityRecordDtoJSON = JSONObject.parseObject(JSON.toJSONString(activityRecordDto));
            activityRecordDtoJSON.put("publishTime",new Date().getTime());
            Map<String, String> params =  new HashMap<String, String>();
            params.put("objectInfo",activityRecordDtoJSON.toString());
           // objectInfo
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
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
