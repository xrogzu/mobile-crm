package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("mwebAccountService")
public class AccountService {

    private  static Logger LOG = LoggerFactory.getLogger(AccountService.class);
    /**
     * 查看公司列表
     * @param companyName
     * @param level     公司类型
     * @param pageno     当前页页码
     * @param pagelimit     当前页显示条数，默认最大为30。openApi目前只支持最大30
     * @return
     */
    public EntityReturnData getAccountList(String authorization, String companyName,int level,int pageno,int pagelimit,long createStartTime,long createEndTime){
        String url = String.format("%s/data/v1/query", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("search company list url is ：{}",url);
        String sql = null;
        Map<String, String> params = new HashMap<String, String>();
        try {

            String searchFields = "id,accountName";
            sql = "select ${searchFields} from Account ";
            boolean hasWhere = false;
            if(StringUtils.isNotBlank(companyName)){
                hasWhere = true;
                sql += " where  accountname  like '%"+companyName+"%' ";
            }
            if(level != 0){
                if(!hasWhere){
                    sql +=" where ";
                    hasWhere = true;
                }else{
                    sql +=" and ";
                }
                sql += " level  = "+level;
            }
            if(createStartTime != 0){
                if(!hasWhere){
                    sql +=" where ";
                    hasWhere = true;
                }else{
                    sql +=" and ";
                }
                sql += " createdAt  >= "+createStartTime;
            }
            if(createEndTime != 0){
                if(!hasWhere){
                    sql +=" where ";
                    hasWhere = true;
                }else{
                    sql +=" and ";
                }
                sql += " createdAt  <= "+createEndTime;
            }

            sql += " order  by  accountName   limit  "+(pageno*pagelimit) +","+pagelimit;
            sql = sql.replace("${searchFields}",searchFields);

            params.put("q",sql);
//            LOG.info("sql：{}",sql);
//            LOG.info("pageno：{};pagelimit:{}",pageno,pagelimit);
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//            LOG.info("search company list openApi return data is ：{},url is :"+url+";params:"+JSON.toJSONString(params),apiResultString);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("search company list error ,errorMsg is ：{},url is :"+url+";params:"+JSON.toJSONString(params),e.getMessage());
            if(e instanceof  java.net.SocketTimeoutException){
                return  APIResultUtil.networkErrorMsg();
            }
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     * 查询客户描述
     */
    public EntityReturnData getAccountDesc(String authorization){
        String url = String.format("%s/data/v1/objects/account/describe", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("获取客户描述：{}",url);

        try {
            String sql = null;

            Map<String, String> params = new HashMap<String, String>();
            params.put("q",sql);
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//            LOG.info("获取客户描述的cmsapi返回结果为：{}",apiResultString);
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
    /****
     * 创建公司
     */
    public EntityReturnData createAccount(String authorization ,JSONObject record){

        String url = String.format("%s/data/v1/objects/account/create", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("create account URL：{}",url);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("public","false");
        record.remove("id");
       
        if(StringUtils.isBlank(record.getString("industryId")) || record.getInteger("industryId") == 0){
            record.remove("industryId");
        }
        if(StringUtils.isBlank(record.getString("parentAccountId")) || record.getInteger("parentAccountId") == 0){
            record.remove("parentAccountId");
        }
        if(StringUtils.isBlank(record.getString("dbcSelect1")) || "0".equals(record.getString("dbcSelect1"))){
            record.remove("dbcSelect1");
        }
        params.put("record",record);
        try {
//            LOG.info("创建客户参数为："+JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
//            LOG.info("创建客户返回消息为："+apiResultString);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     * 修改公司
     * @param authorization
     * @param record
     * @return
     */
    public EntityReturnData updateAccount(String authorization ,JSONObject record){

        String url = String.format("%s/data/v1/objects/account/update", SystemGlobals.getPreference("crmapi.host.url"));
        LOG.debug("update Account`s URL：{}",url);

        Map<String, Object> params = new HashMap<String, Object>();

        if(StringUtils.isBlank(record.getString("industryId")) || record.getInteger("industryId") == 0){
            record.remove("industryId");
        }
        if(StringUtils.isBlank(record.getString("parentAccountId")) || record.getInteger("parentAccountId") == 0){
            record.remove("parentAccountId");
        }
        if(StringUtils.isBlank(record.getString("dbcSelect1")) || "0".equals(record.getString("dbcSelect1"))){
            record.remove("dbcSelect1");
        }
         params = JSON.parseObject(record.toJSONString());
//        params.put("json",JSON.toJSONString(record));
        try {
//            LOG.info("update Account`s  parameter is ："+JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
//            LOG.info("update Account,openApi result is ："+apiResultString);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     * 删除公司
     * @param authorization
     * @param accountId
     * @return
     */
    public EntityReturnData deleteAccount(String authorization ,long accountId){

        String url = String.format("%s/data/v1/objects/account/delete", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.debug("delete Account`s URL：{}",url);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+accountId);

        try {
//            LOG.info("delete Account`s  parameter is："+JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//            LOG.info("delete Account.  openApi result is："+apiResultString);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }

    /**
     * 公司详情
     * @param authorization
     * @param accountId
     * @return
     */
    public EntityReturnData getAccountById(String authorization ,long accountId){

        String url = String.format("%s/data/v1/objects/account/info", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("getAccountById`s URL：{}",url);

      //  String[] filds = new String []{"accountName"};

        Map<String, String> params = new HashMap<String, String>();
        params.put("id",accountId+"");
        try {
//            LOG.info("getAccountById params  is：{}",JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//            LOG.info("getAccountById openApi result is：{}",apiResultString);
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
    public EntityReturnData getAccountByIds(String authorization ,JSONArray accountIds){
        String url = String.format("%s/data/v1/query", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("getAccountByIds`s URL：{}",url);
        try {
                String sql = null;
                String searchFields = "id,accountName";
                sql = "select ${searchFields} from Account  where id in (";
                for(int i=0;i<accountIds.size();i++){
                    if( i == 0){
                        sql += accountIds.get(0);
                    }else {
                        sql += ","+ accountIds.get(i);
                    }
                }
                sql  += ")";
                sql = sql.replace("${searchFields}",searchFields);
                Map<String, String> params = new HashMap<String, String>();
                params.put("q",sql);
//                LOG.info("getAccountByIds`s parameter ：{}",JSON.toJSONString(params));
                LOG.info("sql：{}",sql);
                String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//                LOG.info("getAccountByIds openApi result is ：{}",apiResultString);
                EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
                return  ret;
        }catch (IOException e) {
                e.printStackTrace();
                if(e instanceof  java.net.SocketTimeoutException){
                    return  APIResultUtil.networkErrorMsg();
             }
       }
        EntityReturnData  returnData = new EntityReturnData();
        return returnData;
    }
    public EntityReturnData transferAccount(String authorization ,long accountId,Long targetOwnerId){

        String url = String.format("%s/data/v1/objects/account/transfer", SystemGlobals.getPreference("crmapi.host.url"));
//        LOG.info("transferAccount`s URL：{}",url);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id",""+accountId);
        params.put("targetOwnerId",""+targetOwnerId);
        try {
//            LOG.info("transferAccount`s parameter is："+JSON.toJSONString(params));
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
//            LOG.info("transferAccount,openApi result is ："+apiResultString);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            return  ret;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EntityReturnData  returnData = new EntityReturnData();
        return  returnData;
    }  

}
