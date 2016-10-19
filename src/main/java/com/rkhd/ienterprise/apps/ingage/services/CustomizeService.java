package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.enums.CustomerEntityType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("mwebCustomizeService")
public class CustomizeService {
    private  static Logger LOG = LoggerFactory.getLogger(CustomizeService.class);

    public EntityReturnData  getDefaultCustomizeLayout(String authorization, long entityId, CustomerEntityType type){
        EntityReturnData returnData = _getCustomizeLayout( authorization,  entityId,  type);
        if(returnData.isSuccess()){
            JSONObject outter = (JSONObject) returnData.getEntity();
            JSONArray layouArrays = outter.getJSONArray("data");
            JSONObject defaultLayout = null;
            if( layouArrays != null && layouArrays.size()>0){
                for(int i=0;i<layouArrays.size();i++){
                    defaultLayout = layouArrays.getJSONObject(i);
                    if(defaultLayout.getBoolean("defaultEntity")){
                        break;
                    }
                }
            }
            returnData.setEntity(defaultLayout);
        }
        return returnData;


    }


    /**
     * 自定义页面布局
     * @param authorization
     * @return
     */
    private EntityReturnData _getCustomizeLayout(String authorization, long entityId, CustomerEntityType type){

        String url = String.format("%s/data/v2/objects/{objectType}/{entityType}/layouts.do", SystemGlobals.getPreference("crmapi.host.url"));
        String apiResultString = null;
        try {
            if(entityId != 0){
                url = url.replace("{entityType}",entityId+"");
            }else{
                url = url.replace("/{entityType}","");
            }
            url = url.replace("{objectType}",type.toString());

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
}
