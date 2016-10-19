package com.rkhd.ienterprise.apps.ingage.wx.statics.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.TimeEnum;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.StaticsDateUtil;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.base.user.model.User;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计使用
 */
@Namespace("/wx/statics")
public class StaticsAction extends WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(StaticsAction.class);

    @Autowired
    private StaticsServices staticsServices;

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;

    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebActivityRecordService")
    private ActivityRecordService activityRecordService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;


    @Autowired
    @Qualifier("mwebReferenceService")
    private ReferenceService referenceService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;


    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;



    /**
     * 查询日期类型
     */
    private long searchDateType = TimeEnum.THIS_WEEK.getValue();

    @Action(value = "index", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/statics/statics_index.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "condition", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/statics/statics_condition.jsp")})
    public String condition() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "account", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/statics/statics_account.jsp")})
    public String account() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "contact", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/statics/statics_contact.jsp")})
    public String contact() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "opportunity", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/statics/statics_opportunity.jsp")})
    public String opportunity() throws Exception{
        return  SUCCESS;
    }




    @Action(value = "typecondition", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String typecondition(){
        EntityReturnData ret = new EntityReturnData();
        ret.setSuccess(true);
        TimeEnum[] types =  TimeEnum.values();
        JSONArray retJSONArray = new JSONArray();
        for(TimeEnum type :types){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",type.getValue());
            jsonObject.put("desc",type.getDesc());
            retJSONArray.add(jsonObject);
        }
        ret.setEntity(retJSONArray);
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    /**
     * 统计不同活动记录完成情况
     * @return
     */
    @Action(value = "activerecords", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doStatisActiveRecord()  {
        JSONArray ret_result_array = new JSONArray();
        EntityReturnData ret = new EntityReturnData();
        try {
            SessionUser   sessionUser = getSessionUser();
            TenantParam tenantParam =  sessionUser.getXsy_TenantParam();
            XsyUserDto xsyUserDto = sessionUser.getXsy_user();
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for(TimeEnum type :timeEnum){
                if(type.getValue() == searchDateType){
                    temp = type;
                    break;
                }
            }
            DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
            User currentUser = new User();
            currentUser.setId(xsyUserDto.getId());
//            查询本期各种活动类型的统计信息
            JSONArray thisData1 = staticsServices.getActiveRecordCountMap(currentUser,tenantParam,dto.getDownDate(),dto.getUpDate());
            DateDto dto_compare =  StaticsDateUtil.getBeCompareTimeInfo(temp);
//            查询上期各种活动类型的统计信息
            JSONArray thisData2 = staticsServices.getActiveRecordCountMap(currentUser,tenantParam,dto_compare.getDownDate(),dto_compare.getUpDate());

            //将两次的数据进行比较然后输出
            Map<String,Long> mapData = new HashMap<String, Long>();
            JSONObject temp_json = null;
            String compareId = null;
//            [{"id":"-12","text":"短信","count":0},{"id":"-11","text":"记录","count":0},{"id":"108185","text":"拜访签到","count":0},{"id":"-10","text":"任务","count":0},{"id":"108184","text":"电话","count":0}]
            for(int i=0;i<thisData2.size();i++){
                temp_json  = thisData2.getJSONObject(i);
                compareId = temp_json.getString("id");
                mapData.put("t_"+compareId,temp_json.getLongValue("count"));
            }
            DecimalFormat df = new DecimalFormat("0");//格式化小数
            for(int i=0;i<thisData1.size();i++){
                temp_json  = thisData1.getJSONObject(i);
                compareId = temp_json.getString("id");
                long current_count = temp_json.getLongValue("count");
                long last_count = mapData.get("t_"+compareId);
                if(last_count==0){
                    temp_json.put("compare",  "N/A");
                }else{
                    // （当前-之前）/之前
                    long result = (current_count-last_count)*100/last_count;
                    temp_json.put("compare", df.format(result) );
                }
//                LOG.info("current_count="+current_count+";last_count="+last_count+";compare="+temp_json.getString("compare"));
                ret_result_array.add(temp_json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.setSuccess(true);
        ret.setEntity(ret_result_array);
        getRequest().setAttribute("jsondata",ret);
        return SUCCESS;
    }

    /**
     * 统计top商机情况
     * @return
     */
    @Action(value = "topopportunity", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doStatisTopOpportunity() {
        EntityReturnData returnData = new EntityReturnData();
        try {
            String access_token = getAccessToken();
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for (TimeEnum type : timeEnum) {
                if (type.getValue() == searchDateType) {
                    temp = type;
                    break;
                }
            }
            DateDto dto = StaticsDateUtil.getTimeInfo(temp);
            returnData =  opportunityService.getOpportunityList(access_token, null, 0, 0, getPagesize(), 0, dto.getDownDate(), dto.getUpDate(), 0, 0,
                    "money", true,0,0);
            JSONObject tempJSON = null;
            if (returnData.isSuccess() && returnData.getEntity() != null) {
                JSONObject pageData = (JSONObject) returnData.getEntity();
//                long totalSize = pageData.getLong("totalSize");
//                long totalPage = ((totalSize % getPagesize() == 0) ? (totalSize / getPagesize()) : (totalSize / getPagesize() + 1));
//                pageData.put("totalPage", totalPage);
                pageData.put("pageNo", getPageNo());
                pageData.put("pagesize", getPagesize());
                pageData.remove("count");
                pageData.remove("totalSize");

                //组装商机阶段的map
                Map<String, JSONObject> saleStageObjectMap = new HashMap<String, JSONObject>();
                JSONArray selectitem = this.getSaleStage_selectitem();
                for (int i = 0; i < selectitem.size(); i++) {
                    JSONObject saleStage = selectitem.getJSONObject(i);
                    long saleStageId = saleStage.getLongValue("value");
                    saleStageObjectMap.put("S-" + saleStageId, saleStage);

                }
                JSONArray opportunitys = pageData.getJSONArray("records");
                JSONArray accountIds = new JSONArray();
                for (int i = 0; i < opportunitys.size(); i++) {
                    tempJSON = opportunitys.getJSONObject(i);
                    //设置商机阶段名称
                    long saleStageId = tempJSON.getLong("saleStageId");
                    if (saleStageObjectMap.get("S-" + saleStageId) != null) {
                        JSONObject saleStage = saleStageObjectMap.get("S-" + saleStageId);
                        tempJSON.put("saleStageText", saleStage.getString("label"));
                        tempJSON.put("saleStagePercentage", saleStage.getDoubleValue("percent"));//saleStage.getString("")
                        tempJSON.put("saleStageOrder", saleStage.getDoubleValue("order"));
                    } else {
                        tempJSON.put("saleStageText", "");
                        tempJSON.put("saleStagePercentage", "0");//saleStage.getString("")
                        tempJSON.put("saleStageOrder", "0");
                    }
                    //为设置公司名称做准备
                    Long accountId = tempJSON.getLongValue("accountId");
                    accountIds.add(accountId);
                }
                if (accountIds.size() > 0) {

                    //设置企业名称
                    EntityReturnData companyListEntity =  referenceService.referenceSearch(getAccessToken(),1,accountIds);
                    if(companyListEntity.isSuccess()){
                        Map<String,String> comapanyNameMap  = new HashMap<String,String>();
                        JSONObject companyDataObj = (JSONObject) companyListEntity.getEntity();

                        JSONArray compays = companyDataObj.getJSONArray("records");
                        for(int m = 0;m<compays.size();m++){
                            JSONObject cJSON = compays.getJSONObject(m);
                            comapanyNameMap.put("C-"+cJSON.getLong("id"),cJSON.getString("name"));
                        }
                        for(int i = 0; i < opportunitys.size(); i++){
                            tempJSON = opportunitys.getJSONObject(i);
                            tempJSON.put("accountName",comapanyNameMap.get("C-"+tempJSON.getLong("accountId")) == null ? "" : comapanyNameMap.get("C-"+tempJSON.getLong("accountId")));
                        }
                    }else{
                        LOG.error("companyListEntity is {}",JSON.toJSONString(companyListEntity));
                    }

                }
                returnData.setEntity(pageData);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata", returnData);
        return SUCCESS;
    }

    /**
     * 统计活动记录列表
     * @return
     */
    @Action(value = "activerecordlist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getActiverecordlist() {
        EntityReturnData returnData = new EntityReturnData();
        try {
            String access_token = getAccessToken();
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for (TimeEnum type : timeEnum) {
                if (type.getValue() == searchDateType) {
                    temp = type;
                    break;
                }
            }
            DateDto dto = StaticsDateUtil.getTimeInfo(temp);
            SessionUser sessionUser = getSessionUser();
            long ownerId = 0 ;
            JSONArray dimDepartArray = null;
             if(sessionUser.isNormal()){
                 ownerId =  sessionUser.getXsy_user().getId();
            }else if(sessionUser.isManager()){
                 EntityReturnData myDepartMentData =   departmentService.getMyDepartmenttree(getAccessToken());
                 if(myDepartMentData.isSuccess()){
                     dimDepartArray = new JSONArray();
                    // {"entity":{"departs":[{"id":110101,"groupId":258800,"text":"全公司","parent":"#","pinyin":"quangongsi"},{"id":110102,"groupId":258801,"text":"管理部门","parent":110101,"pinyin":"guanlibumen"}]},"success":true}
                    JSONObject departsJSONObject = (JSONObject) myDepartMentData.getEntity();
                     JSONArray departs = departsJSONObject.getJSONArray("departs");
                    for(int i=0;i<departs.size();i++){
                        dimDepartArray.add(departs.getJSONObject(i).getLong("id"));
                    }
                 }else{
                    LOG.error("departmentService.getMyDepartmenttree error,token="+getAccessToken());
                 }
             }

            returnData = activityRecordService.getPage(access_token,getPageNo(),getPagesize(),0,0,new Date(dto.getDownDate()),new Date(dto.getUpDate()),ownerId,dimDepartArray);
            if(returnData.isSuccess() && returnData.getEntity() != null ){
                JSONObject pageData = (JSONObject) returnData.getEntity();
                pageData.put("pageNo",0);
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");
                JSONArray recordTypes = this.getActivityRecordType();
                Map<String,JSONObject> recordTypeMap = new HashMap<String,JSONObject>();
                JSONObject temp2 = null;
                for(int i=0;i<recordTypes.size();i++){
                    temp2 = recordTypes.getJSONObject(i);
                    recordTypeMap.put("type_"+temp2.getLong("id"),temp2);
                }
                JSONArray records = pageData.getJSONArray("records");
                JSONObject record = null;
                JSONObject urecord = null;

                Map<String,JSONObject> usrMap = new HashMap<String, JSONObject>();
                JSONObject userRecord = null;
                JSONObject recordTypeJSON = null;

                for(int i=0;i<records.size();i++){
                    record = records.getJSONObject(i);
                    userRecord = null;
                    recordTypeJSON = null;
                    if(usrMap.get("u_"+record.getLong("uid")) == null){
                        EntityReturnData userEntityReturnData = xsyApiUserService.getUserInfo(getAccessToken(),record.getLong("uid"));
                        userRecord = new JSONObject();
                        if(userEntityReturnData.isSuccess()){
                            urecord = (JSONObject) userEntityReturnData.getEntity();
                            userRecord.put("userName",urecord.getString("name"));
                            userRecord.put("userPhone",urecord.getString("phone"));
                            userRecord.put("userId",urecord.getLong("id"));
                        }else{
                            userRecord.put("userName",urecord.getString("name"));
                            userRecord.put("userPhone",urecord.getString("phone"));
                            userRecord.put("userId",urecord.getLong("id"));
                        }
                        usrMap.put("u_"+record.getLong("uid"),userRecord);
                    }else{
                        userRecord = new JSONObject();
                        JSONObject tempJSONObject2 = usrMap.get("u_"+record.getLong("uid")) ;
                        userRecord.put("userName",tempJSONObject2.get("userName"));
                        userRecord.put("userPhone",tempJSONObject2.get("userPhone"));
                        userRecord.put("userId",tempJSONObject2.get("userId"));
                    }
                    record.put("user",userRecord);
                    //设置活动记录类型
                    if(recordTypeMap.get("type_"+record.getLong("type")) == null){
                        recordTypeJSON = new JSONObject();
                        recordTypeJSON.put("id","");
                        recordTypeJSON.put("name","");
                        recordTypeJSON.put("type","");

                    }else{
                        recordTypeJSON = new JSONObject();
                        JSONObject tempJSONObject = recordTypeMap.get("type_"+record.getLong("type"));
                        recordTypeJSON.put("id",tempJSONObject.get("id"));
                        recordTypeJSON.put("name",tempJSONObject.get("name"));
                        recordTypeJSON.put("type",tempJSONObject.get("type"));
                    }
                    record.put("recordType",recordTypeJSON);
                    records.set(i,record);
                }

                returnData.setEntity(pageData);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        getRequest().setAttribute("jsondata", returnData);
        return SUCCESS;
    }

    private JSONArray getSaleStage_selectitem(){
        JSONArray selectitem = (JSONArray) getRequest().getSession().getAttribute("saleStage_selectitem");
        if(selectitem  == null){
            try{
                EntityReturnData ret  = opportunityService.getOpportunityDesc(getAccessToken());
                if (ret.isSuccess()){
                    JSONObject entity_JSON = (JSONObject) ret.getEntity();
                    JSONArray fields = entity_JSON.getJSONArray("fields");
                    JSONObject saleStageIdObject = null;
                    String propertyname = null;
                    for(int i=0;i<fields.size();i++){
                        saleStageIdObject = fields.getJSONObject(i);
                        propertyname = saleStageIdObject.getString("propertyname");
                        if("saleStageId".equals(propertyname) ){
                            break;
                        }
                    }
                    if(saleStageIdObject != null){
                        selectitem = saleStageIdObject.getJSONArray("selectitem");
                        getRequest().getSession().setAttribute("saleStage_selectitem",selectitem);
                    }else {
                        selectitem = new JSONArray();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return selectitem;
    }
    public JSONArray getActivityRecordType(){
        JSONArray params = null;
        ActivityRecordService service = new ActivityRecordService();
        EntityReturnData entityReturnData =  service.getDesc(getAccessToken());
        if(entityReturnData.isSuccess()){
            JSONObject descJSONObject = (JSONObject) entityReturnData.getEntity();
            JSONArray busiparamsArray = descJSONObject.getJSONArray("busiparams");
            JSONObject paramsJSONObject = null;
            boolean isBreak = false;
            for(int i=0;i<busiparamsArray.size();i++){
                paramsJSONObject = busiparamsArray.getJSONObject(i);
                LOG.info("{}", JSON.toJSONString(paramsJSONObject));
                if(paramsJSONObject.getString("fieldname").equals("type")){
                    params = paramsJSONObject.getJSONArray("params");
                    break;
                }
            }
        }else {
            LOG.error("search activity record fail ，authorization={}，result is：{}",getAccessToken(),JSONObject.toJSONString(entityReturnData));
        }
        return params;
    }

    public long getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(long searchDateType) {
        this.searchDateType = searchDateType;
    }

    /**
     * 按照本日，本周，本月，本季，本年进行统计新增客户数、联系人数，新增商机数
     * 还要统计客户总数、联系人总数，商机总数
     * @return
     */
    @Action(value = "staticinfo", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getStatisInfo(){

        EntityReturnData ret = new EntityReturnData();
        try{
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            for(TimeEnum type :timeEnum){
                if(type.getValue() == searchDateType){
                    temp = type;
                    break;
                }
            }

            DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
            EntityReturnData staticNewAccount = accountService.getAccountList(getAccessToken(),null,0,0,1,dto.getDownDate(),dto.getUpDate());
            EntityReturnData staticAllAccount = accountService.getAccountList(getAccessToken(),null,0,0,1,0,0);


            EntityReturnData staticNewContact = contactService.getContactList(getAccessToken(),"",new Long(0),0,1,dto.getDownDate(),dto.getUpDate(),"createdAt",false);
            EntityReturnData staticAllContact = contactService.getContactList(getAccessToken(),"",new Long(0),0,1,0,0,"createdAt",false);


            EntityReturnData staticNewOpportunity = opportunityService.getOpportunityList(getAccessToken(),"",0,0,1,0,0,0,0,0,null,false,dto.getDownDate(),dto.getUpDate());
            EntityReturnData staticAllOpportunity= opportunityService.getOpportunityList(getAccessToken(),"",0,0,1,0,0,0,0,0,null,false,0,0);


            JSONObject json = new JSONObject();
            json.put("searchDateType",searchDateType);
            if(staticNewAccount.isSuccess()){
                JSONObject entity = (JSONObject) staticNewAccount.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("newAddAccouontNum",totalSize);
            }else{
                json.put("newAddAccouontNum",0);
            }
            if(staticAllAccount.isSuccess()){
                JSONObject entity = (JSONObject) staticAllAccount.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("allAccouontNum",totalSize);
            }else{
                json.put("allAccouontNum",0);
            }
            if(staticNewContact.isSuccess()){
                JSONObject entity = (JSONObject) staticNewContact.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("newAddContactNum",totalSize);
            }else{
                json.put("newAddContactNum",0);
            }
            if(staticAllContact.isSuccess()){
                JSONObject entity = (JSONObject) staticAllContact.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("AllContactNum",totalSize);
            }else{
                json.put("AllContactNum",0);
            }
            if(staticNewOpportunity.isSuccess()){
                JSONObject entity = (JSONObject) staticNewOpportunity.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("newAddOpportunityNum",totalSize);
            }else{
                json.put("newAddOpportunityNum",0);
            }
            if(staticAllOpportunity.isSuccess()){
                JSONObject entity = (JSONObject) staticAllOpportunity.getEntity();
                long totalSize = entity.getLong("totalSize");
                json.put("AllOpportunityNum",totalSize);
            }else{
                json.put("AllOpportunityNum",0);
            }
            ret.setEntity(json);
            ret.setSuccess(true);

        }catch (Exception e){

        }
        getRequest().setAttribute("jsondata", ret);
        return SUCCESS;
    }
}
