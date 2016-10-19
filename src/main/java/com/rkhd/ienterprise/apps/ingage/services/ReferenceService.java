package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

@Component("mwebReferenceService")
public class ReferenceService {

    private  static Logger LOG = LoggerFactory.getLogger(ReferenceService.class);

    /**
     * 商机描述
     * https://api-dev10.xiaoshouyi.com/data/v1/reference?belongId=1&ids=290302,290301,289707
     * @param authorization
     * @param  belongId 1:客户，2联系人，3商机,70团队成员
     * @param  idArray  id数组
     * @return
     */
    public EntityReturnData referenceSearch(String authorization, long belongId, JSONArray  idArray)  {
        String url = String.format("%s/data/v1/reference", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("referenceSearch  URL：{}",url);
        Map<String,String> params = new HashMap<String, String>();
        try {
            String ids = "";
            for(int i=0;i<idArray.size();i++){
                if( i == 0){
                    ids += idArray.get(0);
                }else {
                    ids += ","+ idArray.get(i);
                }
            }

            params.put("belongId",""+belongId);
            params.put("ids",ids);
//            LOG.info("referenceSearch  params：{}", JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,params);
//            LOG.info("referenceSearch return data ：{}",apiResultString);
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
