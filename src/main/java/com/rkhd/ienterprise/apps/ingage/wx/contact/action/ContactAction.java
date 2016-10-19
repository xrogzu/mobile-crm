package com.rkhd.ienterprise.apps.ingage.wx.contact.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.TimeEnum;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.DateTypeUtils;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.StaticsDateUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.enums.CustomerEntityType;
import com.rkhd.ienterprise.apps.ingage.enums.ObjectType;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
import com.rkhd.ienterprise.apps.ingage.utils.ParseFilesUtils;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.FormPageData;
import com.rkhd.ienterprise.util.PinyinUtil;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 联系人相关
 */
@Namespace("/wx/contact")
public class ContactAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(ContactAction.class);

    @Autowired
    @Qualifier("mwebCustomizeService")
    private CustomizeService customizeService;

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

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;

    private Long accountId = 0L;

    private Long contactId = 0L;

    private String searchName = null;

    private String  contact;

    private int createDateType = 0;//0:全部 ，1：本日，2：昨日，3：本周，4：本月，5：本季，6本年

    private String orderField;

    private boolean isDesc = false;

    private Long ownerid ;

    /**
     * 查询日期类型
     */
    private long searchDateType = 0;

    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/contact/contact_list.jsp")})
    public String list() throws Exception{ 
        return  SUCCESS;
    }
    @Action(value = "info", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/contact/contact_info.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "search", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/contact/contact_search.jsp")})
    public String search() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/contact/contact_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/contact/contact_update.jsp")})
    public String update() throws Exception{
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
        EntityReturnData returnData2 = null;
        try{
//            LOG.info("请求到达");
            int pagesize = getPagesize();
            String searchName = XsyUtils.beautifyString(getSearchName());
            long endTime = 0 ;
            if(getCreateDateType() == 2){
                endTime = DateTypeUtils.getYestarDayEndTime();
            }
            Long startTime = 0L;

            long searchDateType = this.getSearchDateType();

            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;

            if(searchDateType != 0  ){
                for(TimeEnum type :timeEnum){
                    if(type.getValue() == searchDateType){
                        temp = type;
                        break;
                    }
                }
                DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
                startTime = dto.getDownDate();
                endTime = dto.getUpDate();
            }else {
                startTime = DateTypeUtils.getCreateTime(getCreateDateType());
            }


            returnData2 =  contactService.getContactList(getAccessToken(),searchName,getAccountId(),getPageNo(),pagesize,startTime ,endTime,getOrderField(),isDesc());

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
                JSONObject contact =  JSON.parseObject(contact_string ); //JSONObject.toJavaObject(JSON.parseObject(accountJSON),Account.class);
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
    public String doupdate() {
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
                long id = contact.getLong("id");
                Long newdimDepart = contact.getLong("dimDepart");
                EntityReturnData dbAndExtraDta =  contactService.getContactById(getAccessToken(),id);
                if(dbAndExtraDta.isSuccess()){
                    //如果所属部门没有改变则移除部门信息
                    JSONObject dbData = (JSONObject) dbAndExtraDta.getEntity();
                    Long dbDimDepart = dbData.getLong("dimDepart");
                    if(newdimDepart != null && dbDimDepart!= null  && dbDimDepart.longValue() == newdimDepart.longValue()){
                        contact.remove("dimDepart");
                    }
                }

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

    @Action(value = "getDetail", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDetail()  {
        EntityReturnData ret = null;
        boolean success = true;
        try{

            CustomerEntityType type = CustomerEntityType.contact;

            ret = customizeService.getDefaultCustomizeLayout(getAccessToken(),0L,type);

            FormPageData formPageData = new FormPageData();
            if (ret.isSuccess()){
                //获取字段结构
                JSONObject layout_JSON = (JSONObject) ret.getEntity();
                JSONObject parseLayoutJson = ParseFilesUtils.parseDefaultLayout(layout_JSON,getScene(), ObjectType.CONTACT);
                LOG.info(parseLayoutJson.toJSONString());
                formPageData.setStructure(parseLayoutJson);

                JSONObject expandPro = new JSONObject();
                //获取字段值
                if(getContactId() != null && getContactId() != 0){
                    EntityReturnData dataEntityReturnData =  dimensionService.dataPermission(getAccessToken(),2,getContactId());
                    if(dataEntityReturnData.isSuccess()){
                        JSONObject dataPermission = (JSONObject) dataEntityReturnData.getEntity();
                        JSONObject dimJSONObject = dataPermission.getJSONObject("dataPermission");
                        if(dimJSONObject.getBoolean("view")){
                            EntityReturnData dbEntity =   contactService.getContactById(getAccessToken(),getContactId());
                            if(dbEntity.isSuccess()){
                                JSONObject contactObject = (JSONObject) dbEntity.getEntity();
                                //特别处理
                                ParseFilesUtils.dataBeatutiful(contactObject,ObjectType.CONTACT);
//过滤非法字段
                                formPageData.setData(contactObject);
                                JSONArray  components  = layout_JSON.getJSONArray("components");
                                Map<String,String> mapData = new HashMap<String, String>();//存储select里的值，键的命名方式为：propertNname+value:Label
                                JSONObject itemJson = null;
                                String fieldType = null;
                                JSONObject subItemJson = null;
                                JSONObject temp_expandPro = new JSONObject();
                                JSONObject selectlistPropertyMap = new JSONObject();
                                JSONObject checkboxPropertyMap = new JSONObject();
//准备数据
                                for(int i=0;i<components.size();i++){
                                    itemJson = components.getJSONObject(i);
                                    fieldType = itemJson.getString("type");
                                    String propertyName   = itemJson.getString("propertyName");
                                    if("ITEM_TYPE_SELECT".equals(fieldType)){

                                        JSONArray itemArray = itemJson.getJSONArray("selectitem");
                                        temp_expandPro.put(propertyName+"Items",itemArray);
                                        selectlistPropertyMap.put(propertyName,true);
                                        for(int j=0;j<itemArray.size();j++){
                                            subItemJson = itemArray.getJSONObject(j);
                                            mapData.put(propertyName+""+subItemJson.getString("value"),subItemJson.getString("label"));
                                        }
                                    }else if ("ITEM_TYPE_CHECKBOX".equals(fieldType) ){
                                        JSONArray itemArray = itemJson.getJSONArray("checkitem");
                                        temp_expandPro.put(propertyName+"Items",itemArray);
                                        checkboxPropertyMap.put(propertyName,true);
                                        for(int j=0;j<itemArray.size();j++){
                                            subItemJson = itemArray.getJSONObject(j);
                                            mapData.put(propertyName+""+subItemJson.getString("value"),subItemJson.getString("label"));
                                        }
                                    }
                                }
                                //组装下拉选Text数据
                                Set<String> keySet = contactObject.keySet();
                                Iterator<String> it =  keySet.iterator();
                                while(it.hasNext()){
                                    String propertyName = it.next();
                                    if(selectlistPropertyMap.containsKey(propertyName)){
                                        Long dbValue = contactObject.getLong(propertyName);
                                        if(dbValue != null && dbValue.longValue() != 0){
                                            String dbcSelectText = mapData.get(propertyName+dbValue);
                                            expandPro.put(propertyName+"Text",dbcSelectText==null?"":dbcSelectText);
                                        }
                                    }else if(checkboxPropertyMap.containsKey(propertyName)){
                                        JSONArray dbValues = contactObject.getJSONArray(propertyName);
                                        String dbConboxValueString = "";
                                        if(dbValues != null && dbValues.size() > 0){
                                            for(int i=0;i<dbValues.size();i++){
                                                if(dbConboxValueString.length()>0){
                                                    dbConboxValueString += ",";
                                                }
                                                dbConboxValueString += mapData.get(propertyName+dbValues.get(i));
                                            }
                                        }
                                        expandPro.put(propertyName+"Text",dbConboxValueString==null?"":dbConboxValueString);
                                    }
                                }

                                Long createdBy = contactObject.getLong("createdBy");
                                if(createdBy != null   ){
                                    if(createdBy.longValue() > 0){
                                        EntityReturnData userEntity = xsyApiUserService.getUserInfo(getAccessToken(),createdBy);
                                        if(userEntity.isSuccess()){
                                            JSONObject user = (JSONObject) userEntity.getEntity();
                                            String uName = user.getString("name");
                                            expandPro.put("createdByText",uName);
                                        }else{
                                            expandPro.put("createdByText", "");
                                            LOG.error("xsyApiUserService.getUserInfo token="+getAccessToken()+";createdBy="+createdBy+";apiReturnData is "+JSON.toJSONString(userEntity));
                                        }
                                    }else{
                                        expandPro.put("createdByText", "");
                                    }
                                }
                                Long updatedBy = contactObject.getLong("updatedBy");
                                if(updatedBy != null   ){
                                    if(updatedBy.longValue() > 0){
                                        EntityReturnData userEntity = xsyApiUserService.getUserInfo(getAccessToken(),updatedBy);
                                        if(userEntity.isSuccess()){
                                            JSONObject user = (JSONObject) userEntity.getEntity();
                                            String uName = user.getString("name");
                                            expandPro.put("updatedByText",uName);
                                        }else{
                                            expandPro.put("updatedByText", "");
                                            LOG.error("xsyApiUserService.getUserInfo token="+getAccessToken()+";updatedBy="+updatedBy+";apiReturnData is "+JSON.toJSONString(userEntity));
                                        }
                                    }else{
                                        expandPro.put("updatedByText", "");
                                    }
                                }
                                //查询客户的中文名字
                                Long accountId = contactObject.getLongValue("accountId");
                                JSONArray accountIds = new JSONArray();
                                accountIds.add(accountId);
                                EntityReturnData accountRetdata =  referenceService.referenceSearch(getAccessToken(),1,accountIds);
                                String accountIdText = "";
                                if(accountRetdata.isSuccess()){
                                    Map<String,String> comapanyNameMap  = new HashMap<String,String>();
                                    JSONObject companyDataObj = (JSONObject) accountRetdata.getEntity();
                                    JSONArray compays = companyDataObj.getJSONArray("records");
                                    for(int m = 0;m<compays.size();m++){
                                        JSONObject cJSON = compays.getJSONObject(m);
                                        accountIdText = cJSON.getString("name");
                                    }
                                }else {
                                    LOG.error("referenceService referenceSearch with token="+getAccessToken()+";accountIds="+accountIds+";apiReturnData is "+JSON.toJSONString(accountRetdata));
                                }
                                expandPro.put("accountIdText",accountIdText);
//查询所有人的中文名字
                                Long  ownerId =contactObject.getLongValue("ownerId");
                                String ownerIdText = null;
                                EntityReturnData userEntity = xsyApiUserService.getUserInfo(getAccessToken(),ownerId);
                                if(userEntity.isSuccess()){
                                    JSONObject user = (JSONObject) userEntity.getEntity();
                                    ownerIdText = user.getString("name");
                                }else{
                                    LOG.error("xsyApiUserService.getUserInfo token="+getAccessToken()+";ownerId="+ownerId+";apiReturnData is "+JSON.toJSONString(userEntity));
                                }
                                expandPro.put("ownerIdText",ownerIdText);

                                EntityReturnData   opportunityDes = opportunityService.getOpportunityDesc(getAccessToken());
                                if (opportunityDes.isSuccess()){
                                    JSONObject entity_JSON = (JSONObject) opportunityDes.getEntity();
                                    dimJSONObject.put("createOpportunity", entity_JSON.getBoolean("createable"));
                                }else {
                                    dimJSONObject.put("createOpportunity", false);
                                }
                                //设置数据权限
                                expandPro.put("dataPermission",dataPermission);

                                //查询部门的中文名字
                                String dimDepartText = null;
                                long dimDepart = contactObject.getLong("dimDepart");
                                if(dimDepart != 0){
                                    EntityReturnData departmentEntity = departmentService.getDepartmentById(getAccessToken() ,dimDepart);
                                    if(departmentEntity.isSuccess()){
                                        JSONObject department  = (JSONObject) departmentEntity.getEntity();
                                        dimDepartText = department.getString("departName");
                                    }else{
                                        LOG.error("departmentService.getDepartmentById error; token="+getAccessToken()+";dimDepart="+dimDepart+"; apiReturnData={}",JSON.toJSONString(departmentEntity));
                                        dimDepartText = "";
                                    }
                                }
                                expandPro.put("dimDepartText",dimDepartText);
                            }else{
                                success = false;
                                ret.setSuccess(false);
                                ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
                            }
                        }else{
                            success = false;
                            ret.setSuccess(false);
                            ret.setErrorCode(ErrorCode.NO_VIEW_AUTHORITY.getErrorCode());
                        }
                    }else {
                        success = false;
                        ret.setSuccess(false);
                        ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
                    }
                }
                if(success){
                    //扩展属性
                    EntityReturnData departmenttree = departmentService.getMyDepartmenttree(getAccessToken());
                    if(departmenttree.isSuccess()){
                        expandPro.put("dimDepartJSONObject",departmenttree.getEntity());
                    }else {
                        expandPro.put("dimDepartJSONObject",new JSONArray());
                    }
                    formPageData.setExpandPro(expandPro);
                    formPageData = CodeFilter.xddTerminator(formPageData);

                    ret.setEntity(formPageData);
                    ret.setSuccess(success);
                    ret.setErrorCode(ErrorCode.OK.getErrorCode());
                }



            }else {
                ret.setSuccess(false);
                ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
            }
        }catch (Exception e){
            ret = new EntityReturnData();
            ret.setErrorCode(ErrorCode.OTHER_ERROR.getErrorCode());
            e.printStackTrace();
        }
        LOG.info(JSON.toJSONString(ret));
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    @Action(value = "getDesc", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getContactDesc() {
        EntityReturnData ret = new EntityReturnData();
        try{
            ret = contactService.getContactDesc(getAccessToken());
            if (ret.isSuccess()){
                JSONObject entity_JSON = (JSONObject) ret.getEntity();
                entity_JSON.remove("fields");
                ret.setEntity(entity_JSON);
            }else {
                ret.setEntity(new JSONObject());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }


    public Long getContactId() {
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

    public long getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(long searchDateType) {
        this.searchDateType = searchDateType;
    }
}
