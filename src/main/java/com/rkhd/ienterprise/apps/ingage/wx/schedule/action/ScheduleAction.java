package com.rkhd.ienterprise.apps.ingage.wx.schedule.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.enums.ObjectType;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.utils.ParseFilesUtils;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.FormPageData;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.apps.ingage.wx.pc_admin.action.WxPcAdminAction;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.*;

@Namespace("/wx/schedule")
public class ScheduleAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(ScheduleAction.class);

    public static int frequency_None =  0 ;//不重复
    public static int frequency_byDay =  1 ;//每日重复
    public static int frequency_byWeek = 2 ;//每周重复
    public static int frequency_byMonth =  3 ;//按月重复
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    @Qualifier("mwebDimensionService")
    private DimensionService dimensionService;


    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;


    @Autowired
    @Qualifier("mwebReferenceService")
    private ReferenceService referenceService;


    private String  scheduleStr;

    private Long id ;

    private String scene;//场景

    private String startTime;
    private String endTime;
    private Long userId;
    private String type;
    private Long belongId;
    private Long objectId;


    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/shcedule/shcedule_list.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "info", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/shcedule/shcedule_info.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/shcedule/shcedule_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/shcedule/shcedule_update.jsp")})
    public String update() throws Exception{
        return  SUCCESS;
    }

    @Action(value = "getDetail", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDetail() {
        EntityReturnData ret = new EntityReturnData();
        FormPageData formPageData = new FormPageData();
        boolean success = true;
        //扩展属性
        JSONObject expandPro = new JSONObject();
        try{
            ret =  scheduleService.desc(getAccessToken());
            if (ret.isSuccess()){
                JSONObject layout = (JSONObject)ret.getEntity();
                JSONArray components = layout.getJSONArray("fields");
                layout.remove("fields"); 

                JSONObject itemJson = null;
                JSONObject subItemJson = null;
                //由于目前只做可客户、商机、联系人，所以现在只保留这3个.2016-08-19
                for(int i=0;i<components.size();i++){
                    itemJson = components.getJSONObject(i);
                    String propertyName =   itemJson.getString("propertyname");
                    if("belongId".equals(propertyName)  ){
                        JSONArray itemArray = itemJson.getJSONArray("selectItem");
                        JSONArray itemArray_new = new JSONArray();
                        for(int j=0;j<itemArray.size();j++){
                            subItemJson = itemArray.getJSONObject(j);
                            if(subItemJson.getIntValue("value") == 1
                                    || subItemJson.getIntValue("value") == 2
                                    || subItemJson.getIntValue("value") == 3){
                                itemArray_new.add(subItemJson);
                            }
                        }
                        itemJson.put("selectItem",itemArray_new);
                    }
                }
                layout.put("components",components);

                JSONObject parseLayoutJson = ParseFilesUtils.parseDefaultLayout(layout,getScene(), ObjectType.SCHEDULE);
                formPageData.setStructure(parseLayoutJson);

                if(id != null && id != 0 ){
                    EntityReturnData dbAndExtraDta = scheduleService.get(getAccessToken(),getId());
                    if(dbAndExtraDta.isSuccess()){

                        String fieldType = null;
                        Map<String,String> mapData = new HashMap<String, String>();//存储select里的值，键的命名方式为：propertNname+value:Label

                        JSONObject selectlistPropertyMap = new JSONObject();
                        JSONObject checkboxPropertyMap = new JSONObject();


                        for(int i=0;i<components.size();i++){
                            itemJson = components.getJSONObject(i);
                            fieldType = itemJson.getString("type");
                            String propertyName =   itemJson.getString("propertyname");

                            if("select".equals(fieldType)  ){
                                JSONArray itemArray = itemJson.getJSONArray("selectItem");

                                selectlistPropertyMap.put(propertyName,true);
                                for(int j=0;j<itemArray.size();j++){
                                    subItemJson = itemArray.getJSONObject(j);
                                    mapData.put(propertyName+""+subItemJson.getString("value"),subItemJson.getString("label"));
                                }
                            }
                        }
                        JSONObject scheduleJSON = (JSONObject) dbAndExtraDta.getEntity();
                        //特别处理
                        ParseFilesUtils.dataBeatutiful(scheduleJSON, ObjectType.SCHEDULE);

                        formPageData.setData(scheduleJSON);

                        //组装下拉选Text数据
                        Set<String> keySet = scheduleJSON.keySet();
                        Iterator<String> it =  keySet.iterator();
                        while(it.hasNext()){
                            String propertyName = it.next();
                            if(selectlistPropertyMap.containsKey(propertyName)){
                                Long dbValue = scheduleJSON.getLong(propertyName);
                                String dbcSelectText = mapData.get(propertyName+dbValue);
                                expandPro.put(propertyName+"Text",dbcSelectText==null?"":dbcSelectText);
                            }else if(checkboxPropertyMap.containsKey(propertyName)){
                                JSONArray dbValues = scheduleJSON.getJSONArray(propertyName);
                                String dbConboxValueString = "";
                                if(dbValues != null && dbValues.size() > 0){
                                    for(int i=0;i<dbValues.size();i++){
                                        if(dbConboxValueString.length()>0){
                                            dbConboxValueString += ",";
                                        }
                                        dbConboxValueString += mapData.get(propertyName+dbValues.get(i));
                                    }
                                }
                                expandPro.put(propertyName+"Text", dbConboxValueString);
                            }
                        }
                        if(scheduleJSON.getLong("objectId") != null && scheduleJSON.getLong("belongId") != null){
                            if(1 == scheduleJSON.getLong("belongId").longValue()){//客户
                                EntityReturnData dbBelongReturnEntity =  accountService.getAccountById(getAccessToken(),scheduleJSON.getIntValue("objectId"));
                                if(dbBelongReturnEntity.isSuccess()){
                                    JSONObject  dbBelongEntity = (JSONObject) dbBelongReturnEntity.getEntity();
                                    expandPro.put("objectIdText",dbBelongEntity.getString("accountName"));
                                }else {
                                    expandPro.put("objectIdText","");
                                }

                            }else  if(2 == scheduleJSON.getIntValue("belongId")){//联系人
                                EntityReturnData dbBelongReturnEntity = contactService.getContactById(getAccessToken(),scheduleJSON.getIntValue("objectId"));
                                if(dbBelongReturnEntity.isSuccess()){
                                    JSONObject  dbBelongEntity = (JSONObject) dbBelongReturnEntity.getEntity();
                                    expandPro.put("objectIdText",dbBelongEntity.getString("contactName"));
                                }else {
                                    expandPro.put("objectIdText","");
                                }

                            }else  if(3 == scheduleJSON.getIntValue("belongId")){//商机
                                EntityReturnData dbBelongReturnEntity = opportunityService.getOpportunityById(getAccessToken(),scheduleJSON.getIntValue("objectId"));
                                if(dbBelongReturnEntity.isSuccess()){
                                    JSONObject  dbBelongEntity = (JSONObject) dbBelongReturnEntity.getEntity();
                                    expandPro.put("objectIdText",dbBelongEntity.getString("opportunityName"));
                                }else {
                                    expandPro.put("objectIdText","");
                                }

                            }else  if(6 == scheduleJSON.getIntValue("belongId")){// "label": "市场活动"

                            }
                        }

                        Long createdBy =   scheduleJSON.getLong("createdBy");
                        String  createdByText = null;
                        if(createdBy != null && createdBy != 0){
                            EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),createdBy);
                            if(userInfoEntityReturnData.isSuccess()){
                                JSONObject user = (JSONObject) userInfoEntityReturnData.getEntity();
                                createdByText = user.getString("name");
                            }else {
                                LOG.error("token="+getAccessToken()+" ,createdBy = "+createdBy+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                            }
                        }
                        expandPro.put("createdByText",createdByText==null?"":createdByText);
                        Long updatedBy =   scheduleJSON.getLong("updatedBy");
                        String  updatedByText = null;
                        if(updatedBy != null && updatedBy != 0){
                            EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),updatedBy);
                            if(userInfoEntityReturnData.isSuccess()){
                                JSONObject user = (JSONObject) userInfoEntityReturnData.getEntity();
                                updatedByText = user.getString("name");
                            }else {
                                LOG.error("token="+getAccessToken()+" ,createdBy = "+createdBy+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                            }
                        }
                        expandPro.put("updatedByText",updatedByText==null?"":updatedByText);

                        String currentUserIdString = this.getUserID();
                        long currentUserId = new Long(currentUserIdString);

                        long scheduleCreateUserid = scheduleJSON.getLong("createdBy");
                        JSONObject dimJSONObject = new JSONObject();

                        if(scheduleCreateUserid == currentUserId){
                            //只有创建人有删除权限，其他人没有删除权限
                            dimJSONObject.put("delete",true);
                        }else{
                            dimJSONObject.put("delete",false);
                        }
                        expandPro.put("dataPermission",dimJSONObject);


                        boolean canDoQuitOperate = false;
                        Set memberSet = new HashSet();
                        //搜集参与人员ID
                        JSONArray rejectMember = scheduleJSON.getJSONArray("rejectMember");
                        JSONArray acceptMember = scheduleJSON.getJSONArray("acceptMember");
                        JSONArray waitingMember = scheduleJSON.getJSONArray("waitingMember");
                        if(rejectMember != null){
                            for(int m=0,n=rejectMember.size();m<n;m++){
                                Long id = rejectMember.getLong(m);
                                if(id!=null ){
                                    memberSet.add(id);
                                }
                            }
                        }
                        if(acceptMember != null){
                            for(int m=0,n=acceptMember.size();m<n;m++){
                                Long id = acceptMember.getLong(m);
                                if(id!=null ){
                                    memberSet.add(id);
                                    if(id.longValue() == currentUserId ){
                                        canDoQuitOperate = true;
                                        if(currentUserId == createdBy){
                                            canDoQuitOperate = false;
                                        }
                                    }
                                }
                            }
                        }
                        if(waitingMember != null){
                            for(int m=0,n=waitingMember.size();m<n;m++){
                                Long id = waitingMember.getLong(m);
                                if(id!=null ){
                                    memberSet.add(id);
                                }
                            }
                        }
                        memberSet.add(scheduleJSON.getLong("createdBy"));
                        memberSet.add(scheduleJSON.getLong("updatedBy"));
                        expandPro.put("canDoQuitOperate",canDoQuitOperate);
                        if(!memberSet.isEmpty()){
                            JSONArray memberids = new JSONArray();
                            Iterator<Object>  iterator =  memberSet.iterator();
                            while(iterator.hasNext()){
                                memberids.add(iterator.next());
                            }
                            EntityReturnData userListEntity =  referenceService.referenceSearch(getAccessToken(),70,memberids);
                            if(userListEntity.isSuccess()){
                                JSONObject itemntity  = (JSONObject) userListEntity.getEntity();
                                JSONArray itemRecords = itemntity==null?null : itemntity.getJSONArray("records");
                                JSONObject memberjson = new JSONObject();
                                if(itemRecords != null && itemRecords.size()>0){
                                    SessionUser sessionUser = getSessionUser();
                                    for(int j=0;j<itemRecords.size();j++){
                                        JSONObject itemRecord = itemRecords.getJSONObject(j);
                                        long uId = itemRecord.getLong("id");
                                        RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByXsyUserIdTenantidAndSource(uId,sessionUser.getXsy_TenantParam().getTenantId(), Platform.WEIXIN, WXAppConstant.APP_ID);
                                        if( relThirdUserXsyUser != null){
                                            itemRecord.put("icon",relThirdUserXsyUser.getAvatar());
                                        }else{
                                            itemRecord.put("icon","");
                                        }
                                        memberjson.put(""+uId,itemRecord);
                                    }
                                }
                                expandPro.put("members",memberjson);
                            }
                        }

                    }else {
                        success = false;
                        ret = dbAndExtraDta;
                    }
                }
            }else {
                success = false;
            }
            if(success ){
                formPageData.setExpandPro(expandPro);
                formPageData = CodeFilter.xddTerminator(formPageData);
                ret.setEntity(formPageData);
            }else {
                ret.setEntity(new JSONArray());
            }

        }catch (Exception e){
            e.printStackTrace();
            ret = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return SUCCESS;
    }

    @Action(value = "docreate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String docreate() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            String scheduleStr = getScheduleStr();
            JSONObject schedule =  JSON.parseObject(scheduleStr);
            returnData =  scheduleService.docreate(getAccessToken(),schedule);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    @Action(value = "doupdate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doupdate() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            String scheduleStr = getScheduleStr();
            JSONObject schedule =  JSON.parseObject(scheduleStr);
            returnData =  scheduleService.doUpdate(getAccessToken(),schedule);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    @Action(value = "doaccept", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doaccept() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            long id = getId();
            returnData =  scheduleService.accept(getAccessToken(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    @Action(value = "doquit", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doquit() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            long id = getId();
            returnData =  scheduleService.quit(getAccessToken(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    @Action(value = "doreject", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doreject() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            long id = getId();
            returnData =  scheduleService.reject(getAccessToken(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    @Action(value = "dodelete", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dodelete() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            long id = getId();
            returnData =  scheduleService.delete(getAccessToken(),id);
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist() {

        EntityReturnData returnData = new EntityReturnData();
        try{
                SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd");
                String startTime = getStartTime();
                String endTime = getEndTime();
                Long userId = getUserId();
                String type = getType();
                Long belongId = getBelongId();
                Long objectId = getObjectId();

               Long startTimeLong = 0L;
               Long endTimeLong = 0L;
                Calendar startcal =   Calendar.getInstance();
                 Calendar endCal = Calendar.getInstance();
                if(StringUtils.isNotBlank(startTime)){
                    Date startDate = startdf.parse(startTime);
                    startcal.setTime(startDate);
                    startcal.set(Calendar.HOUR, 0);
                    startcal.set(Calendar.MINUTE, 0);
                    startcal.set(Calendar.SECOND, 0);
                    startcal.set(Calendar.MILLISECOND, 0);
                    startTimeLong = startcal.getTimeInMillis();
                }
                if(StringUtils.isNotBlank(endTime)){
                    Date endDate = startdf.parse(endTime);
                    endCal.setTime(endDate);
                    endCal.set(Calendar.HOUR, 23);
                    endCal.set(Calendar.MINUTE, 59);
                    endCal.set(Calendar.SECOND, 59);
                    endCal.set(Calendar.MILLISECOND, 999);
                    endTimeLong = endCal.getTimeInMillis();
                }

                if(startTimeLong == 0 || endTimeLong == 0 ){
                    LOG.error("parameter error ,startTime and endTime canno`t be null ,but startTime = "+startTime +" ;endTime = "+endTime);
                }else {
                    returnData =  scheduleService.list(getAccessToken(),startTimeLong,endTimeLong,userId,type,belongId,objectId);
                    if(returnData.isSuccess()){

                            Map<String,JSONObject> dayMap = new HashMap<String, JSONObject>();
                            Map<String,JSONArray> monthDayMap = new HashMap<String, JSONArray>();
                            Map<String,JSONArray> weekDayMap = new HashMap<String, JSONArray>();
                             Calendar nowCal = Calendar.getInstance();
                            while(true){
                                Date date = startcal.getTime();
                                nowCal.setTime(date);
                                String dateTimeString = startdf.format(date);

                                int dayOfMonth = nowCal.get(Calendar.DAY_OF_MONTH);
                                String dayOfMonthKey = dayOfMonth+"";
                                if(monthDayMap.get(dayOfMonthKey) == null){
                                    //存放查询日期中都有那几天{key:"yyyy-MM-dd",time:'121231231232'}
                                    monthDayMap.put(dayOfMonthKey,new JSONArray());
                                }

                                JSONArray monthDayMapData = monthDayMap.get(dayOfMonthKey);
                                JSONObject monthDayMapDataItem = new JSONObject();
                                monthDayMapDataItem.put("time",startcal.getTimeInMillis());
                                monthDayMapDataItem.put("key",dateTimeString);

                                monthDayMapData.add(monthDayMapDataItem);
                                monthDayMap.put(dayOfMonthKey,monthDayMapData);

                                int dayOfWeek = nowCal.get(Calendar.DAY_OF_WEEK);
                                String dayOfWeekKey = dayOfWeek+"";
                                if(weekDayMap.get(dayOfWeekKey) == null){
                                    //存放查询日期中都有那几天{key:"yyyy-MM-dd",time:'121231231232'}
                                    weekDayMap.put(dayOfWeekKey,new JSONArray());
                                }

                                JSONArray dayOfWeekMapData = weekDayMap.get(dayOfWeekKey);
                                JSONObject dayOfWeekMapDataItem = new JSONObject();
                                dayOfWeekMapDataItem.put("time",startcal.getTimeInMillis());
                                dayOfWeekMapDataItem.put("key",dateTimeString);
                                dayOfWeekMapData.add(dayOfWeekMapDataItem);
                                weekDayMap.put(dayOfWeekKey,dayOfWeekMapData);


                                JSONObject dateData = new JSONObject();
                                dateData.put("count",0);
                                dateData.put("ids","");//记录已经包含的日程的id
                                dateData.put("list",new JSONArray());
                                dateData.put("dayOfMonth",nowCal.get(Calendar.DAY_OF_MONTH));//记录处于某月份的第x天
                                dateData.put("dayOfWeek",nowCal.get(Calendar.DAY_OF_WEEK));//记录处于一个星期的第x天
                                dayMap.put(dateTimeString,dateData);

                                startcal.add(Calendar.HOUR,24);
                                if(startcal.getTimeInMillis() > endTimeLong){
                                    break;
                                }
                            }
                           JSONObject dbEntity  = (JSONObject) returnData.getEntity();
                           JSONArray  dbRecordsData = dbEntity.getJSONArray("records");
                           //使records按照开始时间排序
                            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                            for (int i = 0; i < dbRecordsData.size(); i++) {
                                jsonValues.add(dbRecordsData.getJSONObject(i));
                            }
                            Collections.sort( jsonValues, new Comparator<JSONObject>() {
                                private static final String KEY_NAME = "startDate";
                                public int compare(JSONObject a, JSONObject b) {
                                       long valA =  a.getLong(KEY_NAME);
                                       long valB =   b.getLong(KEY_NAME);
                                    return valA - valB >= 0 ? 1:-1;
                                }
                            });
                            JSONArray  dbRecords  = new JSONArray();
                            for (int i = 0; i < jsonValues.size(); i++) {
                                dbRecords.add(jsonValues.get(i));
                            }
                            JSONObject expandPro = new JSONObject();

                           if(dbRecords != null && dbRecords.size() >0){
                               int lsize = dbRecords.size();
                               Set memberSet = new HashSet();

                               int date_interval = 1000 * 60 * 60 * 24;
                               Calendar orderScheduleCal = Calendar.getInstance();
                               //遍历日程集合
                               for(int i = 0;i<lsize;i++){
                                   JSONObject scheduleJSON = dbRecords.getJSONObject(i);
                                   //LOG.info("scheduleJSON.getLong(\"id\")="+scheduleJSON.getLong("id"));

                                   long scheduleStartDateLong = scheduleJSON.getLong("startDate");

                                   orderScheduleCal.setTimeInMillis(scheduleStartDateLong);
                                   orderScheduleCal.set(Calendar.HOUR_OF_DAY, 0);
                                   orderScheduleCal.set(Calendar.MINUTE, 0);
                                   orderScheduleCal.set(Calendar.SECOND, 0);
                                   orderScheduleCal.set(Calendar.MILLISECOND, 0);


                                   long scheduleEndDateLong = scheduleJSON.getLong("endDate");
                                   long startOrder =  scheduleStartDateLong - orderScheduleCal.getTimeInMillis();
                                   scheduleJSON.put("startOrder",startOrder);//用来对一天内的日程进行排序使用

                                   long showBeginTime = scheduleStartDateLong;
                                   boolean doAdd = true;
                                   if(scheduleEndDateLong < startTimeLong){
                                       doAdd = false;
                                   }
                                   if(scheduleStartDateLong < startTimeLong){ //如果日程开始时间小于查询时间,且已經開始的日程規定其放到查詢開始日期里
                                      showBeginTime =  startTimeLong ;
                                   }
                                   Date scheduleStartDate =  new Date(scheduleStartDateLong);
                                   Date showBeginDate = new Date(showBeginTime);
                                   String showBeginDateString = startdf.format(showBeginDate);//查询开始时间的字符串形式
                                   String scheduleStartDateString = startdf.format(scheduleStartDate);//日程开始时间的字符串形式

                                   JSONObject dateInfo = dayMap.get(showBeginDateString);

                                   if(dateInfo != null && doAdd ){
                                       JSONArray list = dateInfo.getJSONArray("list");
                                       String ids = dateInfo.getString("ids");
                                       if(StringUtils.isEmpty(ids)){
                                           ids = ","+scheduleJSON.getLong("id")+",";
                                       }else {
                                           if(ids.contains(","+scheduleJSON.getLong("id")+",")){
                                               continue;
                                           }else {
                                               ids +=  scheduleJSON.getLong("id")+",";
                                           }
                                       }

                                       list.add(scheduleJSON);
                                       //搜集参与人员ID
                                       JSONArray rejectMember = scheduleJSON.getJSONArray("rejectMember");
                                       JSONArray acceptMember = scheduleJSON.getJSONArray("acceptMember");
                                       JSONArray waitingMember = scheduleJSON.getJSONArray("waitingMember");
                                       if(rejectMember != null){
                                           for(int m=0,n=rejectMember.size();m<n;m++){
                                               memberSet.add(rejectMember.get(m));
                                           }
                                       }
                                       if(acceptMember != null){
                                           for(int m=0,n=acceptMember.size();m<n;m++){
                                               memberSet.add(acceptMember.get(m));
                                           }
                                       }
                                       if(waitingMember != null){
                                           for(int m=0,n=waitingMember.size();m<n;m++){
                                               memberSet.add(waitingMember.get(m));
                                           }
                                       }
                                       memberSet.add(scheduleJSON.getLong("createdBy"));
                                       memberSet.add(scheduleJSON.getLong("updatedBy"));


                                       dateInfo.put("count",list.size());
                                       dateInfo.put("list",list);
                                       dateInfo.put("ids",ids);
                                       //map.put(itemStartDateString,dateInfo);

                                       String itemEndDateString = startdf.format(new Date(scheduleEndDateLong));
                                       if(!scheduleStartDateString.equals(itemEndDateString)){
                                           Calendar itemStartCal = Calendar.getInstance();
                                           itemStartCal.setTime(scheduleStartDate);
                                           itemStartCal.set(Calendar.HOUR_OF_DAY, 0);
                                           itemStartCal.set(Calendar.MINUTE, 0);
                                           itemStartCal.set(Calendar.SECOND, 0);
                                           itemStartCal.set(Calendar.MILLISECOND, 0);

                                           itemStartCal.add(Calendar.HOUR,24);
                                           /**
                                            * 考虑跨度好几天的日程记录
                                            */
                                           while(true){
                                               if(itemStartCal.getTimeInMillis() > scheduleEndDateLong){
                                                   break;
                                               }else {
                                                   Date date = itemStartCal.getTime();
                                                   String itemDateTimeString = startdf.format(date);
                                                   JSONObject itemDateInfo = dayMap.get(itemDateTimeString);

                                                   if(itemDateInfo == null ){
                                                       break;//超出查询范围了。
                                                   }
                                                   JSONObject newItem = JSON.parseObject(scheduleJSON.toJSONString());
                                                   String newids = itemDateInfo.getString("ids");
                                                   if(StringUtils.isEmpty(newids)){
                                                       newids = ","+newItem.getLong("id")+",";
                                                   }else {
                                                       if(newids.contains(","+newItem.getLong("id")+",")){
                                                           //已经包含了该记录则继续循环
                                                           itemStartCal.add(Calendar.HOUR,24);
                                                           continue;
                                                       }else {
                                                           newids +=  newItem.getLong("id")+",";
                                                       }
                                                   }
                                                   JSONArray itemList = itemDateInfo.getJSONArray("list");
                                                   itemList.add(newItem);

                                                   itemDateInfo.put("count",itemList.size());
                                                   itemDateInfo.put("list",itemList);
                                                   itemDateInfo.put("ids",newids);

                                                   itemStartCal.add(Calendar.HOUR,24);
                                               }
                                           }
                                       }
                                   }
                                   //对重复类型的日程进行操作
                                   if(scheduleJSON.getBoolean("isRecur")){
                                       doRecurRecord(scheduleJSON, startTimeLong,endTimeLong, startdf,  dayMap, monthDayMap,weekDayMap);
                                   }
                               }


                               if(!memberSet.isEmpty()){
                                    JSONArray memberids = new JSONArray();
                                   Iterator<Object>  iterator =  memberSet.iterator();
                                     while(iterator.hasNext()){
                                         memberids.add(iterator.next());
                                     }
                                   EntityReturnData userListEntity =  referenceService.referenceSearch(getAccessToken(),70,memberids);
                                   if(userListEntity.isSuccess()){
                                       JSONObject itemntity  = (JSONObject) userListEntity.getEntity();
                                       JSONArray itemRecords = itemntity==null?null : itemntity.getJSONArray("records");
                                       JSONObject memberjson = new JSONObject();
                                       if(itemRecords != null && itemRecords.size()>0){

                                           SessionUser sessionUser = getSessionUser();
                                           for(int j=0;j<itemRecords.size();j++){
                                               JSONObject itemRecord = itemRecords.getJSONObject(j);
                                               long uId = itemRecord.getLong("id");
                                               RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByXsyUserIdTenantidAndSource(uId,sessionUser.getXsy_TenantParam().getTenantId(), Platform.WEIXIN, WXAppConstant.APP_ID);
                                               if( relThirdUserXsyUser != null){
                                                   itemRecord.put("icon",relThirdUserXsyUser.getAvatar());
                                               }else{
                                                   itemRecord.put("icon","");
                                               }
                                               memberjson.put(""+uId,itemRecord);
                                           }
                                       }
                                       expandPro.put("members",memberjson);
                                   }
                               }
                           }
                        //美化日程里的日期
                        Set<String> keySet = dayMap.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()){
                            String key = iterator.next();
                            JSONObject json = dayMap.get(key);
                            JSONArray jsonArray = json.getJSONArray("list");
                            List<JSONObject> sortList = new ArrayList<JSONObject>();
                            for(int i=0;i<jsonArray.size();i++){
                                //组装到list里为排序做准备
                                sortList.add(jsonArray.getJSONObject(i));

                            }
                            //按照每天开始日期和最初开始日期排序
                            Collections.sort( sortList, new Comparator<JSONObject>() {
                                private static final String KEY_NAME1 = "startOrder";

                                private static final String KEY_NAME2 = "startDate";
                                public int compare(JSONObject a, JSONObject b) {
                                    long valA1 =  a.getLong(KEY_NAME1);
                                    long valB1 =   b.getLong(KEY_NAME1);

                                    long valA2 =  a.getLong(KEY_NAME2);
                                    long valB2 =   b.getLong(KEY_NAME2);
                                    return valA1 - valB1 > 0 ? 1:(valA1 - valB1 == 0 )?(valA2-valB2>=0?1:-1):-1;
                                }
                            });
                            for(int i=0;i<sortList.size();i++){
                                //格式化日期
                                dateBeautiful(sortList.get(i));
                            }
                            json.put("list",sortList);
                            json.remove("ids");
                        }
                        JSONObject record = new JSONObject();
                        record.put("records",dayMap);
                        record.put("expandPro",expandPro);
                        returnData.setEntity(record);
                    }
                }

        }catch (Exception e){
            returnData.setSuccess(false);
            returnData.setEntity(null);
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     *
     * 对重复类型的日程做特别处理
     * @param scheduleItem
     * @param searchBeginTime
     * @param startdf
     * @param dayMap
     */
    public void doRecurRecord(JSONObject scheduleItem,long searchBeginTime,long searchEndTime,SimpleDateFormat startdf, Map<String,JSONObject> dayMap,Map<String,JSONArray> monthDayMap,Map<String,JSONArray> weekDayMap){

        long scheduleStartDate = scheduleItem.getLong("startDate");//日程开始时间
        long scheduleEndDate = scheduleItem.getLong("endDate");//日程结束时间
        long frequency = scheduleItem.getLong("frequency") == null ? -1 :scheduleItem.getLong("frequency").longValue();//重复频率
        int recurStopCondition = scheduleItem.getInteger("recurStopCondition");//1：永不结束；2：结束日期
        long   recurStopValue = scheduleItem.getLongValue("recurStopValue");//结束时间
        int date_interval = 1000 * 60 * 60 * 24;
        if(frequency_None == frequency){
            //不重复，则不处理
        }else   {
            boolean doWhile = true;
            //从开始日期，到结束日期每天都要添加该记录
            Calendar sceduleSearchStartCal = Calendar.getInstance();
            long begin_timeLong = scheduleStartDate;//默认以日程开始时间为基准
            sceduleSearchStartCal.setTime(new Date(scheduleStartDate));//重复基准时间


            if(frequency_byDay == frequency)
            {
                if( searchBeginTime  >= begin_timeLong){
                    begin_timeLong = searchBeginTime;//如果查询时间比日程开始时间晚，则以查询开始时间为基准。

                 }
                sceduleSearchStartCal.setTime(new Date(begin_timeLong));//重复基准时间

            }else if(frequency_byWeek == frequency)
            {
                while(true){
                    if(sceduleSearchStartCal.getTimeInMillis() >= searchBeginTime){
                        break;
                    }else{
                        sceduleSearchStartCal.add(Calendar.DATE,7); //继续下一周开始
                    }
                }

            }else if(frequency_byMonth == frequency)
            {
                int dayOfMonth = sceduleSearchStartCal.get(Calendar.DAY_OF_MONTH);
                Calendar tempCal = Calendar.getInstance();//复制一个sceduleSearchStartCal

                while(true){
                    if(sceduleSearchStartCal.getTimeInMillis() >= searchBeginTime){
                        break;
                    }else{
                        int addMonth = 1;
                        while(true){
                            tempCal.setTimeInMillis(sceduleSearchStartCal.getTimeInMillis());
                            tempCal.add(Calendar.MONTH,addMonth);
                            if(tempCal.getTimeInMillis() > searchEndTime){//超过查询终止时间，则不需要再循环了
                                doWhile = false;
                                break;
                            }
                            int temp_dayOfMonth = tempCal.get(Calendar.DAY_OF_MONTH);
                            if(temp_dayOfMonth == dayOfMonth && tempCal.getTimeInMillis()>=searchBeginTime){//要大于查询开始时间
                                sceduleSearchStartCal = tempCal;
                                break;
                            } else {
                                addMonth ++;
                            }
                        }
                    }
                }

            }
            if(doWhile){
                sceduleSearchStartCal.set(Calendar.HOUR_OF_DAY,0);
                sceduleSearchStartCal.set(Calendar.MINUTE,0);
                sceduleSearchStartCal.set(Calendar.SECOND,0);
                sceduleSearchStartCal.set(Calendar.MILLISECOND,0);

                while(true){

                    Date date = sceduleSearchStartCal.getTime();
                    String scheduleStartTimeString = startdf.format(date);
                    JSONObject scheduleDayInfo = dayMap.get(scheduleStartTimeString);

                    if(scheduleDayInfo == null ){
                        break;//超出查询范围了。
                    }
                    if(recurStopCondition  == 2 && recurStopValue < sceduleSearchStartCal.getTimeInMillis()){//到达截止日期
                        break;
                    }
                    boolean canDoAdd = false;
                    if(frequency_byMonth == frequency ){
                        int showdayIsdayOfMonth = scheduleDayInfo.getIntValue("dayOfMonth");//查询显示时间是一个月的第x天
                        int times = 0;
                        Calendar itemCalendar = Calendar.getInstance();
                        for(long m=scheduleStartDate;m<=scheduleEndDate ;){
                            itemCalendar.setTimeInMillis(m);
                            if(itemCalendar.get(Calendar.DAY_OF_MONTH) == showdayIsdayOfMonth){
                                canDoAdd = true;
                                break;
                            }
                            m += date_interval;
                            times ++;
                            if(times >=31){//每个月最多31天，不必太多
                                break;
                            }
                        }

                    }else if(frequency_byWeek == frequency ){
                        int showdayIsdayOfWeek = scheduleDayInfo.getIntValue("dayOfWeek");//查询显示时间是一个月的第x天
                        int times = 0;
                        Calendar itemCalendar = Calendar.getInstance();
                        for(long m=scheduleStartDate;m<=scheduleEndDate ;){
                            itemCalendar.setTimeInMillis(m);
                            if(itemCalendar.get(Calendar.DAY_OF_WEEK) == showdayIsdayOfWeek){
                                canDoAdd = true;
                                break;
                            }
                            m += date_interval;
                            times ++;
                            if(times >=7){//每个月最多31天，不必太多
                                break;
                            }
                        }

                    }else {
                        //不要提出去，否则会报错。
                        canDoAdd = true;
                    }
//                    LOG.info("id="+scheduleItem.getLong("id")+";canDoAdd="+canDoAdd+";scheduleStartTimeString="+scheduleStartTimeString);
                    if(canDoAdd){
                        //复制当前日程，并添加到新的日期里
                        JSONObject newItem = JSON.parseObject(scheduleItem.toJSONString());
                        String scheduleDayInfoIds = scheduleDayInfo.getString("ids");
                        boolean addOperate = false;
                        if(StringUtils.isEmpty(scheduleDayInfoIds)){
                            scheduleDayInfoIds = ","+newItem.getLong("id")+",";
                            addOperate = true;
                        }else {
                            if(!scheduleDayInfoIds.contains(","+newItem.getLong("id")+",")) {
                                scheduleDayInfoIds +=  newItem.getLong("id")+",";
                                addOperate = true;
                            }
                        }
                        if(addOperate){
                            JSONArray itemList = scheduleDayInfo.getJSONArray("list");
                            itemList.add(newItem);

                            scheduleDayInfo.put("count",itemList.size());
                            scheduleDayInfo.put("list",itemList);
                            scheduleDayInfo.put("ids",scheduleDayInfoIds);
                        }

    //                对跨天数据进行处理
                        int itemStartDate_int = new Long( scheduleStartDate / date_interval).intValue();
                        int  itemEndDate_int  =  new Long( scheduleEndDate / date_interval).intValue();
                        if(itemEndDate_int ==  itemStartDate_int){
                            //不跨天，不作特别处理
                        }else if(itemEndDate_int >  itemStartDate_int){
                            //跨天，从今天开始的每一天都要添加该数据，直到跨天结束
                            int betweenDays =  itemEndDate_int - itemStartDate_int; //跨度为betweenDays天
                            Calendar subItemStartCal = Calendar.getInstance();
                            subItemStartCal.setTime(sceduleSearchStartCal.getTime());//日程开始日期
                            for(int m = 0;m < betweenDays;m++){
                                subItemStartCal.add(Calendar.DATE,1);
                                Date subDate = subItemStartCal.getTime();
                                String subItemDateTimeString = startdf.format(subDate);

                                JSONObject subItemDateInfo = dayMap.get(subItemDateTimeString);
                                if(subItemDateInfo == null ){
                                    break;//超出查询范围了。
                                }
                                //复制当前日程，并添加到新的日期里
                                JSONObject subNewItem = JSON.parseObject(newItem.toJSONString());
                                String subNewids = subItemDateInfo.getString("ids");
                                if(StringUtils.isEmpty(subNewids)){
                                    subNewids = ","+subNewItem.getLong("id")+",";
                                }else {
                                    if(subNewids.contains(","+subNewItem.getLong("id")+",")){
                                        //已经包含了该记录则继续循环

                                        continue;
                                    }else {
                                        subNewids +=  subNewItem.getLong("id")+",";
                                    }
                                }
                                JSONArray subItemList = subItemDateInfo.getJSONArray("list");
                                subItemList.add(subNewItem);

                                subItemDateInfo.put("count",subItemList.size());
                                subItemDateInfo.put("list",subItemList);
                                subItemDateInfo.put("ids",subNewids);
                            }
                        }

                    }

                    if(frequency_byDay == frequency)
                    {
                        sceduleSearchStartCal.add(Calendar.DATE,1); //继续下一天开始
                    }else if(frequency_byWeek == frequency)
                    {
                        sceduleSearchStartCal.add(Calendar.DATE,7); //继续下一周开始
                    }else if(frequency_byMonth == frequency)
                    {
                        sceduleSearchStartCal.add(Calendar.MONTH,1); //继续下一月开始
                    }

             }
            }
        }
    }


    public String getScheduleStr() {
        return scheduleStr;
    }

    public void setScheduleStr(String scheduleStr) {
        this.scheduleStr = scheduleStr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScene() {
        return scene;
    }
    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBelongId() {
        return belongId;
    }

    public void setBelongId(Long belongId) {
        this.belongId = belongId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    private void dateBeautiful( JSONObject item){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
        Long startDate = item.getLong("startDate");
        if(startDate != null && startDate.longValue() > 0){
            Date date = new Date(startDate);
             String dateString = sdf.format(date);
            item.put("startDate",dateString);
        }
        Long endDate = item.getLong("endDate");
        if(endDate != null && endDate.longValue() > 0){
            Date date = new Date(endDate);
            String dateString = sdf.format(date);
            item.put("endDate",dateString);
        }

        Long updatedAt = item.getLong("updatedAt");
        if(updatedAt != null && updatedAt.longValue() > 0){
            Date date = new Date(updatedAt);
            String dateString = sdf.format(date);
            item.put("updatedAt",dateString);
        }

        Long createdAt = item.getLong("createdAt");
        if(createdAt != null && createdAt.longValue() > 0){
            Date date = new Date(createdAt);
            String dateString = sdf.format(date);
            item.put("createdAt",dateString);
        }

    }
}
