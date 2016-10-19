package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Contact;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 联系人相关
 */
@Component("mwebContactService")
public class ContactService {

    private  static Logger LOG = LoggerFactory.getLogger(ContactService.class);


    /**
     * 联系人描述
     * @param authorization
     * @return
     */
    public EntityReturnData getContactDesc(String authorization){

        String url = String.format("%s/data/v1/objects/contact/describe", SystemGlobals.getPreference("crmapi.host.url"));
        String apiResultString = null;
        try {
            LOG.debug("获取联系人描述url={}",url);
            apiResultString =  MwebHttpClientUtil.basicAuthorizationGet(authorization,url,null);
            LOG.debug("获取联系人描述cmsapi返回结果为={}",apiResultString);
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
     * 查询某用户的所有联系人
     * @param authorization
     * @param contactName   联系人姓名
     * @param accountId   所属公司ID
     * @param pageno   页码
     * @param pagelimit   当前页显示条数
     * @param startDate   起始日期，毫秒计算
     * @param endTime    结束日期，毫秒计算
     * @param orderField   可选字段contactName,createdAt
     * @return
     */
    public  EntityReturnData getContactList(String authorization, String contactName,Long accountId,int pageno,int pagelimit,long startDate,long  endTime,String orderField,boolean isDesc){
        String url = String.format("%s/data/v1/query", SystemGlobals.getPreference("crmapi.host.url"));
        LOG.debug("查看联系人列表的URL：{}",url);
        try {
            if(StringUtils.isBlank(orderField)){
                orderField = "contactName";
            }
            String orderDesc = "asc";
            if(isDesc){
                orderDesc = "desc";
            }
            String searchFields = " id,contactName,accountId,post,phone,mobile";
            String  sql = "select ${searchFields} from Contact ";
            boolean haveWhere = false;
            if(StringUtils.isNotBlank(contactName)){
                sql += " where contactName like '%"+contactName+"%' ";
                haveWhere = true;
            }
            if(accountId!=null && accountId != 0){
                if(!haveWhere){
                    sql += " where ";
                    haveWhere = true;
                }else{
                    sql += " and ";
                }
                sql += "  accountId = "+accountId;
            }
            if(startDate != 0){
                if(!haveWhere){
                    sql += " where ";
                    haveWhere = true;
                }else{
                    sql += " and ";
                }
                sql += "  createdAt >= "+startDate;

            }
            if(endTime != 0){
                if(!haveWhere){
                    sql += " where ";
                    haveWhere = true;
                }else{
                    sql += " and ";
                }
                sql += "   createdAt <= "+ endTime;

            }
            sql += " order by  "+orderField+" "+orderDesc+"  limit  "+(pageno*pagelimit) +","+pagelimit;
            sql = sql.replace("${searchFields}",searchFields);

            Map<String, String> params = new HashMap<String, String>();
            params.put("q",sql);
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



    /**
     * 查询有效条数的联系人
     * @param authorization
     * @param contactName
     * @param start     从0
     * @param limit     数量不要超过30，因为openApi最大数量为30
     * @return
     */
    public  EntityReturnData getContactPage(String authorization, String contactName,int start ,int limit){
        if(limit >= 30){
            limit = 30;
        }
        String url = String.format("%s/data/v1/query", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            String sql = null;
            String searchFields = " id,contactName,depart,accountId,ownerId,post,phone,mobile,email,state,address,zipCode,gender,birthday";
            if(StringUtils.isNotBlank(contactName)){
                sql = "select ${searchFields} from Contact where contactName like '%"+contactName+"%' limit " +limit;
                sql = sql.replace("${searchFields}",searchFields);
            }else {
                sql = "select ${searchFields} from Contact limit " +limit;
                sql = sql.replace("${searchFields}",searchFields);
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("q",sql);
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);
            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            if(ret.isSuccess()){

                JSONObject pager = (JSONObject)ret.getEntity();
                JSONArray contacts = new JSONArray(); //存放查询结果
                contacts.addAll(pager.getJSONArray("records"));

                ret.setEntity(contacts);
            }
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
     * 创建联系人
     * @param authorization
     * @param contact_json
     * @return
     */
    public EntityReturnData createContact(String authorization,JSONObject contact_json)  {
        String url = String.format("%s/data/v1/objects/contact/add", SystemGlobals.getPreference("crmapi.host.url"));
        JSONObject json = new JSONObject();
        if (contact_json.containsKey("accountId") && (contact_json.getLong("accountId") == null ||  contact_json.getLong("accountId")==0)){
            contact_json.remove("accountId");
        }

        json.put("record", contact_json);

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("record", contact_json);
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
     * 修改联系人
     * @param authorization
     * @param contact
     * @return
     */
    public EntityReturnData updateContact(String authorization,JSONObject contact)  {
        String url = String.format("%s/data/v1/objects/contact/update", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String, Object> params = new HashMap<String, Object>();
        // params.put("public","false");
        JSONObject record = contact;
        //if(record.getInteger("accountId") == 0){}
            record.remove("accountId");//公司ID openApi不支持修改
//        if(record.containsKey("dimDepart")){
//            record.remove("dimDepart");
//        }

        params = JSON.parseObject(record.toJSONString());
        try {
            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostJSON(authorization,url,params);
            LOG.info("updateContact result is ：{}",apiResultString);
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
     * 删除联系人
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData deleteContact(String authorization,long id)  {
        String url = String.format("%s/data/v1/objects/contact/delete", SystemGlobals.getPreference("crmapi.host.url"));
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

    /**
     * 联系人明细
     * @param authorization
     * @param id
     * @return
     */
    public EntityReturnData getContactById(String authorization,long id)  {
        String url = String.format("%s/data/v1/objects/contact/info", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
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

    /**
     * 转移联系人
     * @param authorization
     * @param id    联系人id
     * @param targetOwnerId     新的所有者的id
     * @return
     */
    public EntityReturnData doTrans(String authorization,long id,long targetOwnerId)  {
        String url = String.format("%s/data/v1/objects/contact/transfer", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,String> params = new HashMap<String, String>();
        params.put("id",""+id);
        params.put("targetOwnerId",""+targetOwnerId);
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
