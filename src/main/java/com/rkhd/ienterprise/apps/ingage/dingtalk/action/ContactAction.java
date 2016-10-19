package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.*;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
import com.rkhd.ienterprise.util.PinyinUtil;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 联系人相关
 */
@Namespace("/dingtalk/contact")
public class ContactAction extends BaseAction{
    private  static Logger LOG = LoggerFactory.getLogger(ContactAction.class);

    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;

    @Autowired
    @Qualifier("mwebReferenceService")
    private ReferenceService referenceService;

    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    @Qualifier("mwebDimensionService")
    private DimensionService dimensionService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    private Long accountId = 0L;

    private long contactId = 0L;

    private String searchName = null;

    private String  contact;

    private int createDateType = 0;//0:全部 ，1：本日，2：昨日，3：本周，4：本月，5：本季，6本年

    private String orderField;

    private boolean isDesc = false;

    private Long ownerid ;

    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/contact/contact_list.jsp")})
    public String list() throws Exception{ 
        return  SUCCESS;
    }
    @Action(value = "info", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/contact/contact_info.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "search", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/contact/contact_search.jsp")})
    public String search() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/contact/contact_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/contact/contact_update.jsp")})
    public String update() throws Exception{
        return  SUCCESS;
    }
    /**
     * 增加客户时需要使用到的字段描述
     * @return
     * @throws Exception
     */
    @Action(value = "getDesc", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getContactDesc() throws Exception{
        EntityReturnData ret = contactService.getContactDesc(getAccessToken());
        if (ret.isSuccess()){
            JSONObject entity_JSON = (JSONObject) ret.getEntity();
            JSONArray fields = entity_JSON.getJSONArray("fields");
            ret.setEntity(fields);
        }else {
            ret.setEntity(new JSONArray());
            LOG.error("contactService.getContactDesc error, token="+getAccessToken());
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    /**
     * 查询用户创建时间可选数值
     * @return
     * @throws Exception
     */
    @Action(value = "getCreateDateTypeInfo", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getCreateDateTypeInfo() throws Exception{

        JSONArray selectitem = new JSONArray();
//0:全部 ，1：本日，2：昨日，3：本周，4：本月，5：本季，6本年
        JSONObject type1 = new JSONObject();
        type1.put("value",0);
        type1.put("label","全部");

        JSONObject type2 = new JSONObject();
        type2.put("value",1);
        type2.put("label","本日");

        JSONObject type3 = new JSONObject();
        type3.put("value",2);
        type3.put("label","昨日");

        JSONObject type4 = new JSONObject();
        type4.put("value",3);
        type4.put("label","本周");

        JSONObject type5 = new JSONObject();
        type5.put("value",4);
        type5.put("label","本月");

        JSONObject type6 = new JSONObject();
        type6.put("value",5);
        type6.put("label","本季");

        JSONObject type7 = new JSONObject();
        type7.put("value",6);
        type7.put("label","本年");

        selectitem.add(type1);
        selectitem.add(type2);
        selectitem.add(type3);
        selectitem.add(type4);
        selectitem.add(type5);
        selectitem.add(type6);
        selectitem.add(type7);

        EntityReturnData ret = new EntityReturnData();
        ret.setSuccess(true);
        ret.setEntity(selectitem);
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    /**
     * 联系人列表页，查看当前用户的所有联系人
     * @return
     * @throws Exception
     */
    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist() {
        long begin = System.currentTimeMillis();
        EntityReturnData returnData2 = null;
        try{
//            LOG.info("请求到达");
            int pagesize = getPagesize();
            String searchName = XsyUtils.beautifyString(getSearchName());
            long endTime = 0 ;
            if(getCreateDateType() == 2){
                endTime = DateTypeUtils.getYestarDayEndTime();
            }

            returnData2 =  contactService.getContactList(getAccessToken(),searchName,getAccountId(),getPageNo(),pagesize, DateTypeUtils.getCreateTime(getCreateDateType()),endTime,getOrderField(),isDesc());

            JSONObject tempJSON = null;
            String temp_name = null;
            JSONArray newcontacts = new JSONArray();
            if(returnData2.isSuccess() && returnData2.getEntity() != null ){

                JSONObject pageData = (JSONObject) returnData2.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
                pageData.put("totalPage",totalPage);
                pageData.put("pageNo",getPageNo());
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");

                JSONArray contacts = pageData.getJSONArray("records");
                JSONArray accountIds = new JSONArray();
                //中文转拼音
                for(int i = 0; i < contacts.size(); i++){
                    tempJSON = null;
                    temp_name = null;
                    tempJSON = contacts.getJSONObject(i);
                    temp_name = tempJSON.getString("contactName");
                    Long accountId = tempJSON.getLongValue("accountId");
                    accountIds.add(accountId);
                    temp_name = PinyinUtil.cnToPinyin(temp_name);
                    tempJSON.put("contactName_py",temp_name);
                    newcontacts.add(tempJSON);
                }
                if(accountIds.size()>0){
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
                        for(int i = 0; i < newcontacts.size(); i++){
                            tempJSON = newcontacts.getJSONObject(i);
                            tempJSON.put("accountName",comapanyNameMap.get("C-"+tempJSON.getLong("accountId")) == null ?"":comapanyNameMap.get("C-"+tempJSON.getLong("accountId")));
                            newcontacts.set(i,tempJSON);
                        }
                    }else{
                        LOG.error("referenceService.referenceSearch error ,token="+getAccessToken()+" ,accountIds= "+accountIds);
                        for(int i = 0; i < newcontacts.size(); i++){
                            tempJSON = newcontacts.getJSONObject(i);
                            tempJSON.put("accountName","");
                            newcontacts.set(i,tempJSON);
                        }
                    }
                }
                newcontacts = CodeFilter.xddTerminator(newcontacts);
                pageData.put("records",newcontacts);
                returnData2.setEntity(pageData);
            }else{
               //contactService.getContactList(getAccessToken(),searchName,getAccountId(),getPageNo(),pagesize, DateTypeUtils.getCreateTime(getCreateDateType()),endTime,getOrderField(),isDesc());
                LOG.error("contactService.getContactList error;token="+getAccessToken()+";searchName="+searchName+";accountId="+getAccountId()+";pageNo="+getPageNo()
                        +";pagesize="+pagesize
                        +";crateDateType="+getCreateDateType()
                        +";createTime="+DateTypeUtils.getCreateTime(getCreateDateType())
                        +";endTime="+endTime
                        +";orderField="+getOrderField()
                        +";desc="+isDesc()
                        +";returnData2 = "+JSON.toJSONString(returnData2));
            }


        }catch (Exception e){
            LOG.error("ContactAction dolist error ,error msg :"+e.getMessage());
            e.printStackTrace();
            returnData2 = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData2);
        long end = System.currentTimeMillis();
        LOG.info("共耗时{}毫秒",(end-begin));
        return SUCCESS;
    }


    /**
     * 联系人详情
     * @return
     * @throws Exception
     */
    @Action(value = "get", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String get() {
        EntityReturnData retdata = null;
        try{
            retdata = contactService.getContactById(getAccessToken(),getContactId());
            if(retdata.isSuccess()){
                JSONObject contactObject = (JSONObject) retdata.getEntity();
                Long accountId = contactObject.getLongValue("accountId");

                JSONArray accountIds = new JSONArray();
                accountIds.add(accountId);

                EntityReturnData accountRetdata =  referenceService.referenceSearch(getAccessToken(),1,accountIds);
                String accountName = "";
                String accouontPhone = "";
                if(accountRetdata.isSuccess()){
                    Map<String,String> comapanyNameMap  = new HashMap<String,String>();
                    JSONObject companyDataObj = (JSONObject) accountRetdata.getEntity();
                    JSONArray compays = companyDataObj.getJSONArray("records");
                    for(int m = 0;m<compays.size();m++){
                        JSONObject cJSON = compays.getJSONObject(m);
                        accountName = cJSON.getString("name");
                    }
                }else {
                    LOG.error("referenceService referenceSearch with token="+getAccessToken()+";accountIds="+accountIds+";apiReturnData is "+JSON.toJSONString(accountRetdata));
                }
                contactObject.put("accountName",accountName);
                contactObject.put("accouontPhone",accouontPhone);

                Long  ownerId =contactObject.getLongValue("ownerId");
                EntityReturnData userEntity = xsyApiUserService.getUserInfo(getAccessToken(),ownerId);
                if(userEntity.isSuccess()){
                      JSONObject user = (JSONObject) userEntity.getEntity();
                       String uName = user.getString("name");
                       contactObject.put("ownerName",uName);
                }else{
                    contactObject.put("ownerName","");
                    LOG.error("xsyApiUserService.getUserInfo token="+getAccessToken()+";ownerId="+ownerId+";apiReturnData is "+JSON.toJSONString(userEntity));
                }

                EntityReturnData dataEntityReturnData =  dimensionService.dataPermission(getAccessToken(),2,getContactId());
                if(dataEntityReturnData.isSuccess()){
                    JSONObject dataPermission = (JSONObject) dataEntityReturnData.getEntity();
                    contactObject.put("dataPermission",dataPermission.getJSONObject("dataPermission"));
                }else {
                    LOG.error("dimensionService.dataPermission with token="+getAccessToken()+";contactId="+getContactId()+"; apiReturnData={}",JSON.toJSONString(dataEntityReturnData));
                    contactObject.put("dataPermission",new JSONObject());
                }

                //所属部门中文描述
                long dimDepart = contactObject.getLong("dimDepart");
                if(dimDepart != 0){
                    EntityReturnData departmentEntity = departmentService.getDepartmentById(getAccessToken() ,dimDepart);
                    if(departmentEntity.isSuccess()){
                        JSONObject department  = (JSONObject) departmentEntity.getEntity();
                        String departmetnName = department.getString("departName");
                        contactObject.put("dimDepartText",departmetnName);
                    }else{
                        LOG.error("departmentService.getDepartmentById error; token="+getAccessToken()+";dimDepart="+dimDepart+"; apiReturnData={}",JSON.toJSONString(departmentEntity));
                        contactObject.put("dimDepartText","");
                    }
                }
                contactObject = CodeFilter.xddTerminator(contactObject);
                retdata.setEntity(contactObject);
            }else {
                LOG.error("token="+getAccessToken()+" ,contactid = "+getContactId()+" returnData="+JSON.toJSONString(retdata));
            }
        }catch (Exception e){
            LOG.error("ContactAction get error ,error msg :"+e.getMessage());
            e.printStackTrace();
            retdata = new EntityReturnData();
        }

        getRequest().setAttribute("jsondata",retdata);
        return SUCCESS;
    }

    /**
     * 创建联系人
     * @return
     * @throws Exception
     */
    @Action(value = "docreate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String docreate() {
        String contact_string = getContact();
        EntityReturnData returnData = new EntityReturnData();
        try{
            if(XsyUtils.containEmojiCharacter(contact_string)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject contact =  JSON.parseObject(contact_string); //JSONObject.toJavaObject(JSON.parseObject(accountJSON),Account.class);

                returnData =  contactService.createContact(getAccessToken(),contact);
                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
                    LOG.error("contactService.createContact error; token="+getAccessToken()+";contact="+JSON.toJSONString(contact)+"; apiReturnData={}",JSON.toJSONString(returnData));
                    String errorCode = returnData.getErrorCode();
                    String errorMsg = returnData.getMsg();
                    if(errorCode != null   ){
                        if(REPEAT_ERROR_CODE.equals(errorCode) && errorMsg.indexOf("mobile")>=0){
                            returnData.setSuccess(false);
                            ErrorObj errorObj = new ErrorObj();
                            errorObj.setKey("mobile");
                            errorObj.setMsg("电话号码重复");
                            errorObj.setStatus(ErrorCode.DUPLICATE.getErrorCode());
                            returnData.setEntity(errorObj);
                        }else {
                            returnData = new EntityReturnData();
                        }
                    }else{

                        returnData = new EntityReturnData();
                    }
                }
            }
        }catch (Exception e){
            LOG.error("ContactAction docreate error ,error msg :"+e.getMessage());
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    /**
     * 修改联系人
     * @return
     * @throws Exception
     */
    @Action(value = "doupdate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doupdate() throws Exception{
        String contact_string = getContact();
        EntityReturnData returnData = new EntityReturnData();
        try{
            if(XsyUtils.containEmojiCharacter(contact_string)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject contact =  JSON.parseObject(contact_string ); //JSONObject.toJavaObject(JSON.parseObject(accountJSON),Account.class);

                returnData =  contactService.updateContact(getAccessToken(),contact);
                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
                    LOG.error("contactService.updateContact error; token="+getAccessToken()+";contact="+JSON.toJSONString(contact)+"; apiReturnData={}",JSON.toJSONString(returnData));
                    String errorCode = returnData.getErrorCode();
                    String errorMsg = returnData.getMsg();
                    if(errorCode != null   ){
                        if(REPEAT_ERROR_CODE.equals(errorCode) && errorMsg.indexOf("mobile")>=0){
                            returnData.setSuccess(false);
                            ErrorObj errorObj = new ErrorObj();
                            errorObj.setKey("mobile");
                            errorObj.setMsg("电话号码重复");
                            errorObj.setStatus(ErrorCode.DUPLICATE.getErrorCode());
                            returnData.setEntity(errorObj);
                        }else {
                            returnData = new EntityReturnData();
                        }
                    }else{
                        returnData = new EntityReturnData();
                    }
                }
            }
        }catch (Exception e){
            LOG.error("ContactAction doupdate error ,error msg :"+e.getMessage());
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    /**
     * 删除联系人
     * @return
     * @throws Exception
     */
    @Action(value = "dodel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dodel() throws Exception{

        EntityReturnData returnData = null;
        try{
            returnData =  contactService.deleteContact(getAccessToken(),getContactId());
            if(!returnData.isSuccess()){
                LOG.error("contactService.deleteContact error; token="+getAccessToken()+";contactId="+getContactId()+"; apiReturnData={}",JSON.toJSONString(returnData));
            }
            //

        }catch (Exception e){
            LOG.error("ContactAction dodel error ,error msg :"+e.getMessage());
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 联系人转移
     * @return
     * @throws Exception
     */
    @Action(value = "trans", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String transferContact()  {
        EntityReturnData returnData = null;
        try{
            returnData = contactService.doTrans(getAccessToken(),getContactId(),getOwnerid());
            if(!returnData.isSuccess()){
                LOG.error("contactService.doTrans error; token="+getAccessToken()+";contactId="+getContactId()+";ownerId="+getOwnerid()+"; apiReturnData={}",JSON.toJSONString(returnData));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }


    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public void setContact(String  contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public int getCreateDateType() {
        return createDateType;
    }

    public void setCreateDateType(int createDateType) {
        this.createDateType = createDateType;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public void setDesc(boolean desc) {
        isDesc = desc;
    }


    public Long getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(Long ownerid) {
        this.ownerid = ownerid;
    }
}
