package com.rkhd.ienterprise.apps.ingage.services;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.isales.analysis.dto.DashboardSearchCondition;
import com.rkhd.ienterprise.apps.isales.analysis.service.DashboardService;
import com.rkhd.ienterprise.apps.isales.assistant.service.AssistantService;
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.apps.isales.opportunity.model.Opportunity;
import com.rkhd.ienterprise.apps.isales.process.model.Stage;
import com.rkhd.ienterprise.apps.isales.process.service.StageService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.SalesParameterService;
import com.rkhd.ienterprise.base.dbcustomize.constant.DBCustomizeConstants;
import com.rkhd.ienterprise.base.dbcustomize.model.Item;
import com.rkhd.ienterprise.base.dbcustomize.service.EntityBelongTypeService;
import com.rkhd.ienterprise.base.dbcustomize.service.ItemService;
import com.rkhd.ienterprise.base.dimension.constant.DimensionConstants;
import com.rkhd.ienterprise.base.dimension.model.DimensionUser;
import com.rkhd.ienterprise.base.dimension.service.DimensionPrivilegeService;
import com.rkhd.ienterprise.base.dimension.service.DimensionUserService;
import com.rkhd.ienterprise.base.manager.service.UserManagerService;
import com.rkhd.ienterprise.base.relation.service.GroupMemberService;
import com.rkhd.ienterprise.base.setting.service.CommonParameterService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.dimension.DimensionUtil;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.util.Pagination;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dell on 2016/2/29.
 */
@Service
public class CharDataService {

    private static final Logger LOG = LoggerFactory.getLogger(CharDataService.class);


    @Autowired
    private ItemService itemService;

    @Autowired
    private DimensionPrivilegeService dimensionPrivilegeService;

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private DimensionUtil dimensionUtil;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    StageService stageService;

    @Autowired
    CommonParameterService commonParameterService;

    @Autowired
    UserManagerService userManagerService;

    @Autowired
    private EntityBelongTypeService entityBelongTypeService;

    @Autowired
    SalesParameterService salesParameterService;

    @Autowired
    private AuthCommonService authCommonService;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartService departService;


    @Autowired
    private DimensionUserService dimensionUserService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    @Qualifier("mwebCommonService")
    private CommonService commonService;



    public EntityReturnData getSalesFunnelCharData( TenantParam tenantParam,User currentUser,long startTime,long endTime ) throws ServiceException {
        EntityReturnData retData = new EntityReturnData();
        try{
            JSONObject charData = getSalesFunnel( tenantParam,currentUser , startTime,   endTime);

            retData.setSuccess(true);
            retData.setEntity(charData);
        }catch (Exception e){
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return retData;
    }


    /**
     * 销售漏斗
     * 参考仪表盘里的方法做的
     * @param tenantParam
     * @param currentUser
     * @param startTime
     * @param endTime
     * @return
     * @throws ServiceException
     */
    private JSONObject getSalesFunnel(TenantParam tenantParam,
            User currentUser
            ,Long startTime,  Long endTime) throws ServiceException {

        DashboardSearchCondition dashboardCondition = new DashboardSearchCondition();
        Long belongId = DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY;
//        默认业务类型ID
        Long  entityTypeId = entityBelongTypeService.getDefault(belongId,tenantParam).getId();
        List<Stage> stages = stageService.getStageListByBusinessTypeId(entityTypeId, tenantParam);
        JSONArray jsonArray = new JSONArray();

//        设置开始结束时间
        Item closeDateItem = itemService.getItemByEntryPropertyName(DBCustomizeConstants.DUMMY_ITEM_OPPORTUNITY_CLOSE_DATE,tenantParam);
        DateFormat cnDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject startJson = new JSONObject();
        startJson.put("item", closeDateItem.getId());
        startJson.put("type",  7);//参考SearchFavUtil,比较各个数值的意义
        startJson.put("value", cnDf.format(new Date(startTime)));
        jsonArray.add(startJson);

        JSONObject endJson = new JSONObject();
        endJson.put("item", closeDateItem.getId());
        endJson.put("type",  9);
        endJson.put("value", cnDf.format(new Date(endTime)) );
        jsonArray.add(endJson);

//设置ENTITY_TYPE
        Item typeItem = itemService.getItemByEntryPropertyName(DBCustomizeConstants.DUMMY_ITEM_OPPORTUNITY_ENTITY_TYPE,tenantParam);
        JSONObject statusJson = new JSONObject();
        statusJson.put("item", typeItem.getId());
        statusJson.put("type",  10);
        statusJson.put("value", entityTypeId );
        jsonArray.add(statusJson);

        dashboardCondition.setConditions(jsonArray.toString());

        dashboardCondition.setBelongId(belongId);

        if(!authCommonService.canViewAllWithAssist(belongId,currentUser.getId(),tenantParam)){
            LOG.debug("not can see all");
            dashboardCondition.setCurrentUserId(currentUser.getId());
//
            Set<Long> viewUserIds = dimensionUtil.getViewUserIds(currentUser.getId(), authCommonService.getAssistantLeaderIds(currentUser.getId(), tenantParam), tenantParam);
            dashboardCondition.setViewUserIds(viewUserIds);
//
            Set<Long>   leaderIds = new HashSet(this.assistantService.leaderListByAssistantId(currentUser.getId(), tenantParam));
            Map<Short, Set<Long>> dimensions = this.dimensionUtil.getDimensions(currentUser.getId(), tenantParam);
            Map<Short, Short[]> dimPermissions = this.dimensionPrivilegeService.getUserPerMapByBelongId(currentUser.getId(), belongId,tenantParam);

            dimensionUtil.setDimSearchCondition(dashboardCondition,leaderIds,dimensions,dimPermissions,tenantParam);
        }else {
            LOG.debug(" can see all");
        }

        double total = 0;//漏斗总值
        double win = 0;
        double estimateCompleted = 0;//预计完成金额

        List<JSONObject> series = new ArrayList<JSONObject>();
        LOG.info("dashboardCondition={}",JSON.toJSONString(dashboardCondition));
//        从solr中统计商机金额
        Map<Long,Map<String,Number>> map = dashboardService.getOpportunityMoneytBySolr(dashboardCondition, tenantParam, null, null);

        for (Stage stage : stages) {

            if (stage.getStatus().equals(Opportunity.STATUS_LOSE)) {
                continue;
            }
            Map<String,Number> result = map.get(stage.getId());

//            LOG.info("stage={}",JSON.toJSONString(stage)+"\r\n result={}"+JSON.toJSONString(result));
            double amount = 0 ;
            int count =  0 ;
            if( result != null ) {
                amount = result.get("money") != null ? result.get("money").doubleValue() : 0 ;
                count = result.get("count") != null ? result.get("count").intValue() : 0 ;
            }
            total += amount ;
            estimateCompleted += stage.getPercent()*amount/100;

            JSONObject object = new JSONObject();
            object.put("amount", amount);
            object.put("count", count);
            object.put("id", stage.getId());
            object.put("name", stage.getStageName());
            series.add(object);

            if (stage.getStatus().equals(Opportunity.STATUS_WIN)) {
                win =  amount;
            }
        }

        JSONObject data = new JSONObject();
        data.put("total", total);//漏斗总值
        data.put("win", win);//实际完成的

//        数字格式化为2为小数
        BigDecimal b   =   new   BigDecimal(estimateCompleted);

        data.put("estimateCompleted", b.setScale(0,   BigDecimal.ROUND_HALF_UP));//预计完成的
        data.put("series", series);//漏斗数值
        return data;
    }

    /**
     * 赢单业绩排行
     * @param tenantParam
     * @param currentUser
     * @param startTime
     * @param endTime
     * @param maxCount  显示条数
     * @param maxCount  显示条数
     * @return
     * @throws ServiceException
     */
        public JSONObject getSalesRankData(TenantParam tenantParam, User currentUser  , Long startTime, Long endTime,Integer maxCount) throws ServiceException {
//            String titlePrefix = authCommonService.getChartTitlePrefix( currentUser,tenantParam,0);

            DashboardSearchCondition dashboardCondition = new DashboardSearchCondition();
            Long  belongId = DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY;
            dashboardCondition.setBelongId(belongId);
            Set<Long>  viewUserIds  =  dimensionUtil.getDimUserIds(currentUser.getId(),belongId,tenantParam);
            List<Long>  userIds = new ArrayList<Long>(viewUserIds);
            if(!authCommonService.canViewAllWithAssist(belongId,currentUser.getId(),tenantParam)){
                LOG.debug("not can see all");
                /**
                 * 销售专员，只能看到本人数据的情况下，对本人所在部门及相关部门的所有同事进行排名
                 */
                if(viewUserIds.size() == 1){

                    Set<Long> departIds = new HashSet<Long>();
                    departIds.add(currentUser.getDepartId());

                    Map<Short, Set<Long>> permMap = this.getDimMap(currentUser.getId(),tenantParam);
                    //用户的主部门和相关部门id
                    Set<Long> dimPermissions4Department =  permMap.get(DimensionConstants.Type.DEPARTMENT);

                    //查询出部门的所有人员
                    Iterator<Long> it = dimPermissions4Department.iterator();
                    int pageNo = 1;
                    while (it.hasNext()){
                        Long departmetnId = it.next();
                        Depart depart =  departService.get(departmetnId,tenantParam);
                        long groupId = depart.getGroupId();
                        Pagination<Long> membersemberIdPagers  = groupMemberService.getMembersByStatus(groupId,(short)0,pageNo,Integer.MAX_VALUE,tenantParam);
                        if(membersemberIdPagers.getDataCount()>0){
                            List<Long>  departUserIds = membersemberIdPagers.getCurrentPageDatas();
//                            userIds.addAll(departUserIds);
                            viewUserIds.addAll(departUserIds);
                        }
                    }
                }else {
                    dashboardCondition.setCurrentUserId(currentUser.getId());
                    viewUserIds.add(currentUser.getId());
                }
                //避免重复用户出现
                userIds = new ArrayList<Long>(viewUserIds);

                dashboardCondition.setViewUserIds(viewUserIds);
                dashboardCondition.setUserIdList(userIds);
            }else {
                userIds =  commonService.getAllSalesUserIds(tenantParam);
            }
            LOG.info("viewUserIds.lenth={}",viewUserIds.size());
            dashboardCondition.setIsOld((short)1);//固定值，商机owner_id在本人权限范围内的用户
            JSONArray jsonArray = new JSONArray();
            //        设置开始结束时间
            Item closeDateItem = itemService.getItemByEntryPropertyName(DBCustomizeConstants.DUMMY_ITEM_OPPORTUNITY_CLOSE_DATE,tenantParam);
            DateFormat cnDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject startJson = new JSONObject();
            startJson.put("item", closeDateItem.getId());
            startJson.put("type",  7);//参考SearchFavUtil,比较各个数值的意义
            startJson.put("value", cnDf.format(new Date(startTime)));
            jsonArray.add(startJson);

            JSONObject endJson = new JSONObject();
            endJson.put("item", closeDateItem.getId());
            endJson.put("type",  9);
            endJson.put("value", cnDf.format(new Date(endTime)) );
            jsonArray.add(endJson);

            JSONObject statusJson = new JSONObject();
            statusJson.put("item", DBCustomizeConstants.DUMMY_ITEM_OPPORTUNITY_STATUS_ID);
            statusJson.put("type",  10);
            statusJson.put("value", Opportunity.STATUS_WIN );
            jsonArray.add(statusJson);


            dashboardCondition.setConditions(jsonArray.toString());

            Map<Long,Number> map = dashboardService.getUserOppMoneyBySolr(dashboardCondition,tenantParam);
            List<Map<String,Number>> usersMoneyList = new ArrayList<Map<String, Number>>();
            for(Long userId : userIds ) {
                Map<String,Number> userRateMap = new HashMap<String, Number>();
                userRateMap.put("userId",userId);
                Number moneyObj = map.get(userId);
                Double money = moneyObj != null ? moneyObj.doubleValue() :0 ;
                userRateMap.put("money",money);
                usersMoneyList.add(userRateMap);
            }
            Collections.sort(usersMoneyList, new Comparator<Map<String,Number>>() {
                @Override
                public int compare(Map<String,Number> o1, Map<String,Number> o2) {
                    double result = o2.get("money").doubleValue() - o1.get("money").doubleValue();
                    int first = result > 0 ? 1 :-1;
                    return result != 0 ? first: (o2.get("userId").longValue() - o1.get("userId").longValue() > 0 ? 1 :-1);
                }
            });

            Map<Long,User> userMap = userService.userMapByIdList(userIds,tenantParam);
            JSONObject object = null;
            JSONArray array = new JSONArray();
            if(maxCount == null ) {
                maxCount = 300;
            }
            long totalCount = 0;
            for(int i=0; i<usersMoneyList.size()  ; i++  ) {
                Map<String,Number> userMoneyMap = usersMoneyList.get(i);
                long userId = userMoneyMap.get("userId").longValue();
                if( i< maxCount || userId == currentUser.getId() ) {
                    object = new JSONObject();
                    object.put("ord", i);
                    object.put("userId",userId);
                    object.put("userName",userMap.get(userId) != null ? userMap.get(userId).getName() : "" );
                    object.put("money", userMoneyMap.get("money"));
                    array.add(object);
                }
                totalCount +=  userMoneyMap.get("money").longValue();
            }
            JSONObject retdata = new JSONObject();
            retdata.put("series",array);
            retdata.put("totalCount",totalCount);//业绩总值
            return  retdata;
        }


    public Map<Short, Set<Long>> getDimMap(Long userId, TenantParam tenantParam) throws ServiceException {
        Map<Short, Set<Long>> dimMap = new HashMap<Short, Set<Long>>();
        List<DimensionUser> dimensionUserList = dimensionUserService.getListByUserId(userId, tenantParam);
        for (DimensionUser du : dimensionUserList) {
            if (dimMap.get(du.getDimensionType()) == null) {
                Set<Long> lst = new HashSet<Long>();
                lst.add(du.getDimensionId());
                dimMap.put(du.getDimensionType(), lst);
            } else {
                dimMap.get(du.getDimensionType()).add(du.getDimensionId());
            }
        }
        return dimMap;
    }

}
