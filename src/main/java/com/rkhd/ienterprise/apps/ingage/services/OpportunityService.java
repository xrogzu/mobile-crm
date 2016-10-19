package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.APIResultUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.MwebHttpClientUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.base.dbcustomize.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("mwebOpportunityService")
public class OpportunityService {
    private  static Logger LOG = LoggerFactory.getLogger(OpportunityService.class);

    @Autowired
    @Qualifier("opportunityService")
    private com.rkhd.ienterprise.apps.isales.opportunity.service.OpportunityService platOpportunityService;
    @Autowired
    private ItemService itemService;

    /**
     * 商机描述
     * @param authorization
     * @return
     */
    public EntityReturnData getOpportunityDesc(String authorization)  {
        String url = String.format("%s/data/v1/objects/opportunity/describe", SystemGlobals.getPreference("crmapi.host.url"));
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
     * 查询所有商机列表
     * @param authorization
     * @param  opportunityName 商机名称
     * @param accountId 所属公司id
     * @param pageno 页号
     * @param pagelimit 显示条数
     * @param saleStageId 销售阶段ID
     * @param closeDateDown 结单时间下限
     * @param closeDateUp 结单时间上限
     * @param money_down 金额下限
     * @param money_up 金额上限
     * @param orderField 排序字段  排序字段.默认为：opportunityName 可先参数有：saleStageId，money，updatedAt，opportunityName 支持多字段查询例如：saleStageId desc, money desc
     * @param isDesc 降序排序
     * @return
     */
    public EntityReturnData getOpportunityList(String authorization, String opportunityName,long accountId,int pageno,int pagelimit
            ,long saleStageId,long closeDateDown,long closeDateUp,long money_down,long money_up,String orderField
            ,boolean isDesc,long createStartTime,long createEndTime
    ){
        String url = String.format("%s/data/v1/query", SystemGlobals.getPreference("crmapi.host.url"));
        try {
            String sql = null;
            String searchFields = "id,ownerId,opportunityName,accountId,opportunityType,money,saleStageId,sourceId,dimDepart,closeDate";
            sql = "select ${searchFields} from Opportunity ";
            boolean haveWhere = false;

            if(StringUtils.isNotBlank(opportunityName)){
                haveWhere = true;
                sql += "where opportunityName like '%"+opportunityName+"%' ";
            }
            if(accountId != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " accountId = "+accountId;
            }
            if(saleStageId != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " saleStageId = "+saleStageId;
            }
            if(closeDateDown != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " closeDate >= "+closeDateDown;
            }
            if(closeDateUp != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " closeDate <= "+closeDateUp;
            }
            if(money_down != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " money >= "+money_down;
            }
            if(money_up != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql += " where ";
                }else {
                    sql += " and ";
                }
                sql += " money <= "+money_up;
            }
            if(createStartTime != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql +=" where ";
                }else{
                    sql +=" and ";
                }
                sql += " createdAt  >= "+createStartTime;
            }
            if(createEndTime != 0){
                if(!haveWhere){
                    haveWhere = true;
                    sql +=" where ";
                }else{
                    sql +=" and ";
                }
                sql += " createdAt  <= "+createEndTime;
            }

            if(StringUtils.isBlank(orderField)){
                orderField = "opportunityName";
            }
            String orderType = "asc";
            if(isDesc){
                orderType = "desc";
            }
            if(orderField.indexOf("desc") >= 0 || orderField.indexOf("asc") >=0){
                orderType = "";
            }
            sql += " order  by "+orderField+ " "+orderType+"   limit  "+(pageno*pagelimit) +","+pagelimit;
            sql = sql.replace("${searchFields}",searchFields);

            Map<String, String> params = new HashMap<String, String>();
            params.put("q",sql);

            String apiResultString =  MwebHttpClientUtil.basicAuthorizationPostString(authorization,url,params);

            EntityReturnData ret =  APIResultUtil.executeResult(apiResultString);
            if(!ret.isSuccess()){
                LOG.error("token="+authorization+";url="+url+";params="+JSON.toJSONString(params)+";apiResultString="+apiResultString);
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
     *
     * @param authorization
     * @param opportunityDto
     * @return
     */
    public EntityReturnData createOpportunity(String authorization,JSONObject opportunityDto)  {
        String url = String.format("%s/data/v1/objects/opportunity/create", SystemGlobals.getPreference("crmapi.host.url"));
        LOG.info("创建商机的URL：{}",url);

        JSONObject json = new JSONObject();
        JSONObject opportunityDto_json = opportunityDto;
        opportunityDto_json.remove("id");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("record", opportunityDto_json);
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
     *
     * @param authorization
     * @param opportunityDto
     * @return
     */
    public EntityReturnData updateOpportunity(String authorization,JSONObject opportunityDto)  {
        String url = String.format("%s/data/v1/objects/opportunity/update", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String, Object> params = new HashMap<String, Object>();

        JSONObject record  = opportunityDto;
        record.remove("accountId");//openApi不允许修改客户

        params = JSON.parseObject(record.toJSONString());
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

    public EntityReturnData deleteOpportunity(String authorization,long id)  {
        String url = String.format("%s/data/v1/objects/opportunity/delete", SystemGlobals.getPreference("crmapi.host.url"));
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
     * 根据ID查询
     * @param authorization
     * @param opportunityId
     * @return
     */
    public EntityReturnData getOpportunityById(String authorization,long opportunityId)  {
        String url = String.format("%s/data/v1/objects/opportunity/info", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,String> params = new HashMap<String, String>();
        params.put("id",""+opportunityId);
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
     * 根据ID查询联系人
     * @param authorization
     * @param opportunityId
     * @return
     */
    public EntityReturnData getContacts(String authorization,long opportunityId)  {
        String url = String.format("%s/data/v1/objects/opportunity/contact/contacts", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,String> params = new HashMap<String, String>();
        params.put("opportunityId",""+opportunityId);
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
     * 为销售机会添加联系人
     * @param authorization
     * @param opportunityId
     * @return
     */
    public EntityReturnData addContacts(String authorization, long opportunityId, List<Long> contactIds)  {
        String url = String.format("%s/data/v1/objects/opportunity/contact/add", SystemGlobals.getPreference("crmapi.host.url"));

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("opportunityId",""+opportunityId);
        try {
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<contactIds.size();i++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contactId",contactIds.get(i));
                jsonArray.add(jsonObject);
            }
            params.put("contacts",jsonArray );

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
     * 为销售机会删除联系人
     * @param authorization
     * @param opportunityId
     * @return
     */
    public EntityReturnData deleteContacts(String authorization, long opportunityId, List<Long> contactIds)  {
        String url = String.format("%s/data/v1/objects/opportunity/contact/removecontacts", SystemGlobals.getPreference("crmapi.host.url"));
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("opportunityId",""+opportunityId);
        try {
            String oppContactIds = "";
            for(int i=0;i<contactIds.size();i++){
                if(oppContactIds.length()>0){
                    oppContactIds +=",";
                }
                oppContactIds += contactIds.get(i);
            }
            params.put("oppContactIds",oppContactIds);
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
     * 商机转移
     * @param authorization
     * @param opportunityId 商机id
     * @param targetUserId  目标用户ID
     * @return
     */
    public EntityReturnData transOpportunity(String authorization, long  opportunityId,long targetUserId ){
        String url = String.format("%s/data/v1/objects/opportunity/transfer", SystemGlobals.getPreference("crmapi.host.url"));
        LOG.debug("transfer Opportunity URL：{}",url);
        Map<String,String> params = new HashMap<String, String>();
        try {
            params.put("id",""+opportunityId);
            params.put("targetOwnerId",""+targetUserId);
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
