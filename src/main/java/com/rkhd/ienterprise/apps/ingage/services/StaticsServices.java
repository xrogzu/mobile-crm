package com.rkhd.ienterprise.apps.ingage.services;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.isales.analysis.dto.SalesAnalysisSearchCondition;
import com.rkhd.ienterprise.apps.isales.analysis.service.SalesAnalysisService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.model.SalesParameter;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.SalesParameterService;
import com.rkhd.ienterprise.base.dbcustomize.constant.DBCustomizeConstants;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.dimension.DimensionUtil;
import com.rkhd.ienterprise.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by dell on 2016/2/18.
 */
@Component
public class StaticsServices {

    private static final Logger LOG = LoggerFactory.getLogger(StaticsServices.class);

    @Autowired
    @Qualifier("mwebActivityRecordService")
    private ActivityRecordService mwebActivityRecordService;

    @Autowired
    protected SalesParameterService salesParameterService;


    @Autowired
    private DimensionUtil dimensionUtil;


    @Autowired
    private SalesAnalysisService salesAnalysisService;



    @Autowired
    private AuthCommonService authCommonService;



    /**
     * 统计某时间段内用户可看到的所有各种活动记录的情况
     * @param currentUser
     * @param tenantParam
     * @param startTime
     * @param endTime
     * @return
     */
    public JSONArray getActiveRecordCountMap(  User currentUser,TenantParam tenantParam ,long startTime,long endTime) throws ServiceException {
        SalesAnalysisSearchCondition searchCondition = new SalesAnalysisSearchCondition();
        searchCondition.setStartTime(startTime);
        searchCondition.setEndTime(endTime);
        searchCondition.setEntityType(null);

        Set<Long> authIdSet = new HashSet<Long>();
        long belongID = DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY;//DBCustomizeConstants.ENTITY_BELONG_ACTIVITY_RECORD;
        if (! authCommonService.canViewAllWithAssist(belongID,currentUser.getId(),tenantParam)) {
              authIdSet.addAll(dimensionUtil.getDimUserIds(currentUser.getId(),belongID, tenantParam));
        }

        searchCondition.setUserIdList(new ArrayList<Long>(authIdSet));
        //取得新建商机数
        searchCondition.setBelongId(belongID);

//        searchCondition.setStartCloseDate(startTime);
//        searchCondition.setEndCloseDate(endTime);
        long newCreateOppNum = salesAnalysisService.getOpportunityRecordCount(searchCondition, tenantParam);

        //新建客户数
        belongID = DBCustomizeConstants.ENTITY_BELONG_ACCOUNT;
        authIdSet.clear();
        if (! authCommonService.canViewAllWithAssist(belongID,currentUser.getId(),tenantParam)) {
            authIdSet.addAll(dimensionUtil.getDimUserIds(currentUser.getId(),belongID, tenantParam));
        }
        searchCondition.setUserIdList(new ArrayList<Long>(authIdSet));
        searchCondition.setBelongId( belongID);
        long newCreateAccountNum = salesAnalysisService.getAccountRecordCount(searchCondition, tenantParam);

        Map<Long, Map<String, Number>> userMap  = salesAnalysisService.getActivityTypeClassifyCount(searchCondition, tenantParam);

        //        查看所有的活动记录
        List<SalesParameter> salesParameterList = salesParameterService.getSalesParameterByType(SalesParameter.SaleType.PARAMETER_TYPE_ACTIVITY_RECORD.getValue(), tenantParam);
        //加上快速记录类型
        SalesParameter sp = new SalesParameter();
        sp.setId(-11L);
        sp.setParameterName("记录");
        salesParameterList.add(sp);

        SalesParameter salesParameterItem = null;
        for(int i=salesParameterList.size()-1;i>=0;i--){
            salesParameterItem = salesParameterList.get(i);
//            salesParameterItem.getDummyType()
            if (salesParameterItem.getDisabledFlg() != null && salesParameterItem.getDisabledFlg() == 1) {
                salesParameterList.remove(i);
            }
        }

        JSONArray returnJSONArray = new JSONArray();

        JSONObject dataItem = new  JSONObject();
        dataItem.put("id", -1);
        dataItem.put("text", "新建商机");
        dataItem.put("count", newCreateOppNum);
        returnJSONArray.add(dataItem);

        JSONObject accouontItem = new  JSONObject();
        accouontItem.put("id", -2);
        accouontItem.put("text", "新建客户");
        accouontItem.put("count", newCreateAccountNum);
        returnJSONArray.add(accouontItem);

        for (SalesParameter salesParameter : salesParameterList) {
             JSONObject tempItem = new  JSONObject();
            tempItem.put("id", salesParameter.getId());
            tempItem.put("text", salesParameter.getParameterName());

            Map<String, Number> map = userMap.get(salesParameter.getId());
            if (map == null || map.isEmpty()) {
                tempItem.put("count", 0);
                tempItem.put("count", 0);
            } else {
                int count = map.get("count").intValue();
                tempItem.put("count", count);
            }
            returnJSONArray.add(tempItem);
        }
        return returnJSONArray;
    }


}
