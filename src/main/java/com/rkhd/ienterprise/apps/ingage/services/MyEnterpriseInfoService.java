package com.rkhd.ienterprise.apps.ingage.services;


import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.base.smartsearch.enterprise.EnterpriseInfoService;
import com.rkhd.ienterprise.nsearch.dto.SearchCondition;
import com.rkhd.ienterprise.nsearch.dto.SearchResult;
import com.rkhd.ienterprise.nsearch.dto.account.XAccount;
import com.rkhd.ienterprise.nsearch.service.SearchControlService;
import com.rkhd.ienterprise.nsearch.service.constance.Constances;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业工商信心查询
 */
@Component("mwebMyEnterpriseInfoService")
public class MyEnterpriseInfoService {
    @Autowired
    private EnterpriseInfoService enterpriseInfoService;
    @Autowired
    private SearchControlService searchControlService;

    @Autowired
    @Qualifier("mwebReferenceService")
    private ReferenceService referenceService;

    /**
     *
     * @param tenantParam
     * @param enterpriseName
     * @param pageNo  从0开始
     * @return
     */
    public EntityReturnData search(String token,TenantParam tenantParam, String enterpriseName, int pageNo){
       EntityReturnData returnData = new EntityReturnData();
        returnData.setSuccess(true);
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isBlank(enterpriseName)){
            return returnData;
        }
        try {
            int no=10;

            SearchCondition.Builder builder = new SearchCondition.Builder(tenantParam.getTenantId(), "account.account_name_x:" + enterpriseName, pageNo * no, no).addFilterQuery("account.account_type:1").addOrder("account.id", SolrQuery.ORDER.desc);
            builder.setDefType("dedup");
            SearchCondition condition = builder.build();
            SearchResult sr = searchControlService.query(Constances.Engine.ACCOUNT, condition);
            if (null != sr) {
                List<XAccount> xAccountList = sr.getDatas();
                JSONArray idList = new JSONArray();
                for (XAccount xAccount : xAccountList) {
                    idList.add(xAccount.getId());
                }
                EntityReturnData companyListEntity =  referenceService.referenceSearch(token,1,idList);
                if(companyListEntity.isSuccess() ){
                    JSONObject companyDataObj = (JSONObject) companyListEntity.getEntity();
                    JSONArray accounts = companyDataObj.getJSONArray("records");
                    for(int i=0;i<accounts.size();i++){
                        JSONObject item = accounts.getJSONObject(i);
                        item.put("type", "inner");
                        jsonArray.add(item);
                        no--;
                        if (no == 0) {
                            break;
                        }
                    }
                }
            }

            if(no>0) {
                List<Map<String, String>> itemList = enterpriseInfoService.search(enterpriseName);

                if(itemList!=null) {
                    Map<String,Boolean> nameMap = new HashMap<String, Boolean>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                         JSONObject localIemp = jsonArray.getJSONObject(i);
                        nameMap.put(localIemp.getString("name"),true);

                    }

                    for (Map<String, String> map : itemList) {
                        String cName = map.get("name").toString();
                        if(StringUtils.isBlank(cName) || "null".equals(cName.trim()))
                        {
                            continue;
                        }

                        if (nameMap.containsKey(cName)) {
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", map.get("id"));
                        item.put("name", map.get("name"));
                        item.put("type", "outter");
                        jsonArray.add(item);
                        no--;
                        if (no ==0) {
                            break;
                        }
                    }
                }
            }
            returnData.setEntity(jsonArray==null?new ArrayList():jsonArray);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnData;
    }

    public EntityReturnData  get(String enterpriseName){
        EntityReturnData returnData = new EntityReturnData();
        returnData.setSuccess(true);
        if(StringUtils.isNotBlank(enterpriseName)){
            Map<String, Object>  enterprise = enterpriseInfoService.get(enterpriseName);
            JSONObject detailEnterprise = JSON.parseObject(JSONObject.toJSONString(enterprise));
            returnData.setEntity(detailEnterprise);
        }
        return returnData;
    }
}


