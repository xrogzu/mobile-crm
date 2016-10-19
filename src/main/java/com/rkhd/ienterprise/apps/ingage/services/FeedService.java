package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
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
 * 动态
 */
@Component
public class FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedService.class);

    /**
     *
     * @param authorization
     * @param belongId  业务实体ID      1客户，2联系人，3商机
     * @param objectId  业务对象ID
     * @param pageNo    查看页码，需为正整数  从1开始
     * @param pageSize     每页条数
     * @return
     */
    public EntityReturnData findFeedList(String authorization,long belongId,long objectId,int pageNo,int pageSize ){

        String url = String.format("%s/data/v1/objects/feed/list", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        try {
            params.put("belongId",belongId+"");
            params.put("objectId",objectId+"");
            params.put("pageNo",pageNo+"");
            params.put("pageSize",pageSize+"");
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
