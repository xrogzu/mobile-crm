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
 * Created by dell on 2015/12/22.
 */
@Component("mwebDimensionService")
public class DimensionService {

    private  static Logger LOG = LoggerFactory.getLogger(DimensionService.class);

    /**
     * 实体业务对象查询
     * @param authorization
     * @return
     */
    public EntityReturnData dimensionBelong(String authorization)  {
        String url = String.format("%s/data/v1/picks/dimension/belongs", SystemGlobals.getPreference("crmapi.host.url"));

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
     * 部门多维度查询
     * @param authorization
     * @param belongId  具体数值参考【实体业务对象查询】的返回数据
     * @return
     */
    public EntityReturnData dimensionDepartments(String authorization, long belongId)  {
        String url = String.format("%s/data/v1/picks/dimension/departments", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,String> params = new HashMap<String, String >();
        params.put("belongId",""+belongId);
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
     * 数据权限查询
     * @param authorization
     * @param belongId  具体数值参考【实体业务对象查询】的返回数据 1:客户，2：联系人，3商机
     * @return
     */
    public EntityReturnData dataPermission(String authorization, long belongId,long id)  {
        String url = String.format("%s/data/v1/picks/dimension/dataPermission", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,String> params = new HashMap<String, String >();
        params.put("belongId",""+belongId);
        params.put("id",""+id);
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


}
