package com.rkhd.ienterprise.apps.ingage.wx.account.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.TimeEnum;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
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
import org.apache.commons.lang.StringUtils;
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

@Namespace("/wx/account")
public class AccountAction  extends WxBaseAction {
    private static final Logger LOG = LoggerFactory.getLogger(AccountAction.class);

    @Autowired
    @Qualifier("mwebCustomizeService")
    private CustomizeService customizeService;

    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;

    @Autowired
    @Qualifier("mwebDimensionService")
    private DimensionService dimensionService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;


    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;



    /**
     * 列表页查询参数 名称
     */
    private String searchName = null;
    /**
     * 列表页查询参数 级别
     */
    private int level = 0;

    private Long accountId ;

    /**
     * 公司信息
     */
    private String  account;

    /**
     *团队成员
     */
    private String teamitems;

    private Long ownerid ;

    /**
     * 查询日期类型
     */
    private long searchDateType = 0;



    /**
     * 公司列表
     * @return
     * @throws Exception
     */
    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/account/account_list.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "info", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/account/account_info.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "search", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/account/account_search.jsp")})
    public String search() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/account/account_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/account/account_update.jsp")})
    public String update() throws Exception{
        return  SUCCESS;
    }


    /**
     * 行业id，供给下拉选择
     * @return
     * @throws Exception
     */
    @Action(value = "industryIds", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getAccouontIndustryId()  {
        EntityReturnData ret  =  null ;
        try{
            ret = accountService.getAccountDesc(getAccessToken());
            if (ret.isSuccess()){
                JSONObject entity_JSON = (JSONObject) ret.getEntity();
                JSONArray fields = entity_JSON.getJSONArray("fields");
                JSONObject jsonObject = null;
                JSONArray selectitems = new JSONArray();
                for(int i=0;i<fields.size();i++)
                {
                    jsonObject = fields.getJSONObject(i);
                    if("industryId".equals(jsonObject.getString("propertyname"))){
                        selectitems = jsonObject.getJSONArray("selectitem");
                        break;
                    }
                }
                ret.setEntity(selectitems);
            }else {
                ret.setEntity(new JSONArray());
                LOG.error("token={} ,result is {}",getAccessToken(), JSON.toJSONString(ret));
            }
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    /**
     * 查询可选用户级别
     * @return
     * @throws Exception
     */
    @Action(value = "getLevelInfo", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getLevelInfo() {
        try{
            EntityReturnData ret = accountService.getAccountDesc(getAccessToken());
            if (ret.isSuccess()){
                LOG.info("{}",JSON.toJSONString(ret));
                JSONObject entity_JSON = (JSONObject) ret.getEntity();
                JSONArray fields = entity_JSON.getJSONArray("fields");
                JSONObject levelInfo = null;
                for(int i=0 ;i <fields.size();i++){
                    levelInfo = fields.getJSONObject(i);
                    if("level".equals(levelInfo.getString("propertyname")) ){
                        JSONArray selectitem = levelInfo.getJSONArray("selectitem");
                        ret.setEntity(selectitem);
                        break;
                    }
                    levelInfo = null;
                }
            }else {
                LOG.error("token={} ,result is {}",getAccessToken(),JSON.toJSONString(ret));
                ret.setEntity(new JSONArray());
            }
            getRequest().setAttribute("jsondata",ret);

        }catch (Exception e){
            e.printStackTrace();
            EntityReturnData ret = new EntityReturnData();
            getRequest().setAttribute("jsondata",ret);
        }
        return  SUCCESS;
    }

    /**
     * 1.公司列表页查看该用户的所有客户
     * @return
     * @throws Exception
     */
    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist()  {
        try{
            int pagesize = getPagesize();
            String searchName = XsyUtils.beautifyString(getSearchName());
            TimeEnum[] timeEnum = TimeEnum.values();
            TimeEnum temp = null;
            long searchDateType = this.getSearchDateType();
            long createStartTime = 0;
            long createEndTime = 0;

            if(searchDateType != 0 ){
                for(TimeEnum type :timeEnum){
                    if(type.getValue() == searchDateType){
                        temp = type;
                        break;
                    }
                }
                DateDto dto =  StaticsDateUtil.getTimeInfo(temp);
                createStartTime = dto.getDownDate();
                createEndTime = dto.getUpDate();
            }
            EntityReturnData returnData1 = accountService.getAccountList(getAccessToken(),searchName,getLevel(),getPageNo(),pagesize,createStartTime,createEndTime);
            JSONObject ret  = new JSONObject();
            JSONObject tempJSON = null;
            String temp_name = null;

            if(returnData1.isSuccess() && returnData1.getEntity() != null ){
                JSONObject pageData = (JSONObject) returnData1.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
                pageData.put("totalPage",totalPage);
                pageData.put("pageNo",getPageNo());
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");

                JSONArray accounts = pageData.getJSONArray("records");
                JSONArray newaccounts = new JSONArray();
                //中文转拼音
                for(int i = 0; i < accounts.size(); i++){
                    tempJSON = null;
                    temp_name = null;
                    tempJSON = accounts.getJSONObject(i);
                    temp_name = tempJSON.getString("accountName");
                    temp_name = PinyinUtil.cnToPinyin(temp_name);
                    tempJSON.put("accountName_py",temp_name);
                    newaccounts.add(tempJSON);
                }
                //防止xdd攻击
                newaccounts = CodeFilter.xddTerminator(newaccounts);
                LOG.info("getPageNo()="+getPageNo()+";result length="+newaccounts.size());
                pageData.put("records",newaccounts);

                returnData1.setEntity(pageData);
                getRequest().setAttribute("jsondata",returnData1);
            }else {
                getRequest().setAttribute("jsondata",returnData1);
                LOG.error("token={} ,searchName = "+searchName+",level="+getLevel()+";pageNo="+getPageNo()+"  result is {}",getAccessToken(),JSON.toJSONString(ret));
            }
        }catch (Exception e){
            e.printStackTrace();
            EntityReturnData ret = new EntityReturnData();
            getRequest().setAttribute("jsondata",ret);
        }
        return SUCCESS;
    }
    /**
     * 按照名称查询公司，联系人
     * @return
     * @throws Exception
     */
    @Action(value = "dosearch", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dosearch() {
        int pagesize = getPagesize();
        EntityReturnData retdata = new EntityReturnData();
        try{
            String searchName = XsyUtils.beautifyString(getSearchName());
            EntityReturnData returnData1 = accountService.getAccountList(getAccessToken(),searchName,0,getPageNo(),pagesize,0,0);
            EntityReturnData returnData2 = contactService.getContactList(getAccessToken(),searchName,0L,getPageNo(),getPagesize(), 0,0,null,false);

            JSONObject ret  = new JSONObject();
            JSONObject tempJSON = null;
            String temp_name = null;
            JSONArray newaccounts = new JSONArray();
            JSONArray newcontacts = new JSONArray();
            if(returnData1.isSuccess() && returnData1.getEntity() != null ){
                JSONObject pageData = (JSONObject) returnData1.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
                pageData.put("totalPage",totalPage);
                pageData.put("pageNo",getPageNo());
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");

                JSONArray accounts = pageData.getJSONArray("records");
                //中文转拼音
                for(int i = 0; i < accounts.size(); i++){
                    tempJSON = null;
                    temp_name = null;
                    tempJSON = accounts.getJSONObject(i);
                    temp_name = tempJSON.getString("accountName");

                    temp_name = PinyinUtil.cnToPinyin(temp_name);
                    tempJSON.put("accountName_py",temp_name);
                    newaccounts.add(tempJSON);
                }
                pageData.put("records",newaccounts);
                returnData1.setEntity(pageData);
            }else {
                LOG.error("token={} ,searchName = "+searchName+",level="+getLevel()+";pageNo="+getPageNo()+"  returnData1 is {}",getAccessToken(),JSON.toJSONString(returnData1));
            }
            if(returnData2.isSuccess() && returnData2.getEntity() != null ){
                JSONObject pageData = (JSONObject) returnData2.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
                pageData.put("totalPage",totalPage);
                pageData.put("pageNo",getPageNo());
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");

                JSONArray contacts = pageData.getJSONArray("records");
                //中文转拼音
                for(int i = 0; i < contacts.size(); i++){
                    tempJSON = contacts.getJSONObject(i);
                    temp_name = tempJSON.getString("contactName");
                    temp_name = PinyinUtil.cnToPinyin(temp_name);
                    tempJSON.put("contactName_py",temp_name);
                    newcontacts.add(tempJSON);
                }
                pageData.put("records",newcontacts);
                returnData2.setEntity(pageData);
            }else {
                LOG.error("token={} ,searchName = "+searchName+",level="+getLevel()+";pageNo="+getPageNo()+"  returnData2 is {}",getAccessToken(),JSON.toJSONString(returnData2));
            }
            //防止xss攻击
            returnData1 = CodeFilter.xddTerminator(returnData1);
            returnData2 = CodeFilter.xddTerminator(returnData2);

            ret.put("accounts",returnData1);
            ret.put("contacts",returnData2);

            retdata.setSuccess(true);
            retdata.setEntity(ret);
        }catch ( Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",retdata);
        return SUCCESS;
    }




    /**
     * 创建客户
     * @return
     * @throws Exception
     */
    @Action(value = "docreate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String docreate() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            String accountJSON = getAccount();
            if(XsyUtils.containEmojiCharacter(accountJSON)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject account =  JSON.parseObject(accountJSON);
                returnData =  accountService.createAccount(getAccessToken(),account);
                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
                    String errorCode = returnData.getErrorCode();
                    String errorMsg = returnData.getMsg();
                    if(errorCode != null   ){
                        if(REPEAT_ERROR_CODE.equals(errorCode) && errorMsg.indexOf("accountName")>=0){
                            returnData.setSuccess(false);
                            ErrorObj errorObj = new ErrorObj();
                            errorObj.setKey("accountName");
                            errorObj.setMsg("公司名称重复");
                            errorObj.setStatus(ErrorCode.DUPLICATE.getErrorCode());
                            returnData.setEntity(errorObj);
                        }else {
                            returnData = new EntityReturnData();
                            LOG.error("token="+getAccessToken()+" ,account = "+JSON.toJSONString(account)+" returnData="+JSON.toJSONString(returnData));
                        }
                    }else{
                        returnData = new EntityReturnData();
                        LOG.error("token="+getAccessToken()+" ,account = "+JSON.toJSONString(account)+" returnData="+JSON.toJSONString(returnData));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    /**
     * 修改客户
     * @return
     * @throws Exception
     */
    @Action(value = "doupdate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doupdate(){
        String account_string = getAccount();
        EntityReturnData returnData = new EntityReturnData();
        try{
            if(XsyUtils.containEmojiCharacter(account_string)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject account = JSON.parseObject(account_string);
                long id = account.getLong("id");
                Long newdimDepart = account.getLong("dimDepart");
                EntityReturnData dbAndExtraDta =  accountService.getAccountById(getAccessToken(),id);
                if(dbAndExtraDta.isSuccess()){
                    //如果所属部门没有改变则移除部门信息
                       JSONObject dbAccouont = (JSONObject) dbAndExtraDta.getEntity();
                       Long dbDimDepart = dbAccouont.getLong("dimDepart");
                       if(newdimDepart != null && dbDimDepart!= null  && dbDimDepart.longValue() == newdimDepart.longValue()){
                           account.remove("dimDepart");
                       }
                }


                returnData =  accountService.updateAccount(getAccessToken(),account);
                LOG.error("update account return data is:{}",JSON.toJSONString(returnData));
                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
//                {"error_code":300004,"message":"[accountName] is  already exists"}
                    String errorCode = returnData.getErrorCode();
                    String errorMsg = returnData.getMsg();
                    if( errorCode != null ){
                        if(REPEAT_ERROR_CODE.equals(errorCode) && errorMsg.indexOf("accountName")>=0){
                            returnData.setSuccess(false);
                            ErrorObj errorObj = new ErrorObj();
                            errorObj.setKey("accountName");
                            errorObj.setMsg("公司名称重复");
                            errorObj.setStatus(ErrorCode.DUPLICATE.getErrorCode());
                            returnData.setEntity(errorObj);
                        }else {
                            returnData = new EntityReturnData();
                            LOG.error("token="+getAccessToken()+" ,account = "+JSON.toJSONString(account)+" returnData="+JSON.toJSONString(returnData));
                        }
                    }else{
                        returnData = new EntityReturnData();
                        LOG.error("token="+getAccessToken()+" ,account = "+JSON.toJSONString(account)+" returnData="+JSON.toJSONString(returnData));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    /**
     * 删除客户
     * @return
     * @throws Exception
     */
    @Action(value = "dodel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dodel()  {
        EntityReturnData returnData = null;
        try{
            returnData =  accountService.deleteAccount(getAccessToken(), getAccountId());
            if(!returnData.isSuccess()){
                LOG.error("token="+getAccessToken()+" ,accountId = "+getAccountId()+" returnData="+JSON.toJSONString(returnData));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    /**
     * 客户转移
     * @return
     * @throws Exception
     */
    @Action(value = "changeto", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String transferAccount()  {
        EntityReturnData returnData = new EntityReturnData();
        try{
            returnData = accountService.transferAccount(getAccessToken(),getAccountId(),getOwnerid());
            if(!returnData.isSuccess()){
                LOG.error("token="+getAccessToken()+" ,accountId = "+getAccountId()+";ownerid="+getOwnerid()+" returnData="+JSON.toJSONString(returnData));
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 替代原有的get方法
     * @return
     */
    @Action(value = "getDetail", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDetail()  {
        EntityReturnData ret = null;
        boolean success = true;
        try{
            CustomerEntityType type = CustomerEntityType.customize;
            ret = customizeService.getDefaultCustomizeLayout(getAccessToken(),0L,type);
            FormPageData formPageData = new FormPageData();
            if (ret.isSuccess()){
                //获取字段结构
                JSONObject layout_JSON = (JSONObject) ret.getEntity();
                JSONObject parseLayoutJson = ParseFilesUtils.parseDefaultLayout(layout_JSON,getScene(), ObjectType.ACCOUNT);
                formPageData.setStructure(parseLayoutJson);
                //扩展属性
                JSONObject expandPro = new JSONObject();
                //获取字段值
                if(accountId != null && accountId != 0 ){
                    EntityReturnData dataEntityReturnData =  dimensionService.dataPermission(getAccessToken(),1,getAccountId());

                    if(dataEntityReturnData.isSuccess()) {
                        JSONObject dataPermission = (JSONObject) dataEntityReturnData.getEntity();
                        JSONObject dimJSONObject = dataPermission.getJSONObject("dataPermission");
                        if(dimJSONObject.getBoolean("view")){
                            EntityReturnData dbAndExtraDta =  accountService.getAccountById(getAccessToken(),accountId);
                            if(dbAndExtraDta.isSuccess()){
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
                                    String propertyName =   itemJson.getString("propertyName");

                                    if("ITEM_TYPE_SELECT".equals(fieldType)  ){

                                        JSONArray itemArray = itemJson.getJSONArray("selectitem");
                                        temp_expandPro.put(propertyName+"Items", itemArray);
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
                                    }else if("ITEM_TYPE_DUMMY".equals(fieldType) && ( "industryId".equals(propertyName))) {
                                        JSONArray itemArray = itemJson.getJSONArray("dummyItemBean");
                                        temp_expandPro.put(propertyName+"Items", JSONArray.parse(itemArray.toJSONString()));

                                        for(int j=0;j<itemArray.size();j++){
                                            subItemJson = itemArray.getJSONObject(j);
                                            mapData.put(propertyName+""+subItemJson.getString("value"),subItemJson.getString("label"));
                                        }
                                    }
                                }
//组装数据
                                String  industryIdText = null;
                                String  ownerIdText = null;
                                JSONArray members = null;

                                JSONObject accountJSON = (JSONObject) dbAndExtraDta.getEntity();
                                //特别处理
                                ParseFilesUtils.dataBeatutiful(accountJSON,ObjectType.ACCOUNT);

                                Long createdBy = accountJSON.getLong("createdBy");
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
                                Long updatedBy = accountJSON.getLong("updatedBy");
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
                                formPageData.setData(accountJSON);
                                long industryId =  0L;
                                if(StringUtils.isNotBlank(accountJSON.getString("industryId"))){
                                    industryId = accountJSON.getLong("industryId");
                                }
                                if(industryId > 0){
                                    //遍历设置行业名称
                                    industryIdText = mapData.get("industryId"+industryId);
                                }
//组装下拉选Text数据
                                Set<String> keySet = accountJSON.keySet();
                                Iterator<String>  it =  keySet.iterator();
                                while(it.hasNext()){
                                    String propertyName = it.next();
                                    if(selectlistPropertyMap.containsKey(propertyName)){
                                        Long dbValue = accountJSON.getLong(propertyName);
                                        if(dbValue != null && dbValue.longValue() != 0){
                                            String dbcSelectText = mapData.get(propertyName+dbValue);
                                            expandPro.put(propertyName+"Text",dbcSelectText==null?"":dbcSelectText);
                                        }
                                    }else if(checkboxPropertyMap.containsKey(propertyName)){
                                        JSONArray dbValues = accountJSON.getJSONArray(propertyName);
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
                                long ownerId =   accountJSON.getLong("ownerId");
                                if(ownerId != 0){
                                    EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),ownerId);
                                    if(userInfoEntityReturnData.isSuccess()){
                                        JSONObject ownerUser = (JSONObject) userInfoEntityReturnData.getEntity();
                                        ownerIdText = ownerUser.getString("name");
                                    }else {
                                        LOG.error("token="+getAccessToken()+" ,ownerId = "+ownerId+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                                    }
                                }
                                members = accountJSON.getJSONArray("members");
                                if(members != null && members.size()>0){
                                    int size = members.size();
                                    for(int i=0;i<size;i++){
                                        JSONObject member = members.getJSONObject(i);
                                        LOG.info(member.toJSONString());
                                        long memberId = member.getLong("id");
                                        EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),memberId);
                                        if(userInfoEntityReturnData.isSuccess()){
                                            JSONObject memberUser = (JSONObject) userInfoEntityReturnData.getEntity();
                                            LOG.info(memberUser.toJSONString());
                                            member.put("name",memberUser.getString("name"));
                                        }else {
                                            LOG.error("token="+getAccessToken()+" ,memberId = "+memberId+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                                            member.put("name","");
                                        }
                                        if(member == null){
                                            continue;
                                        }
                                        members.set(i,member);
                                    }
                                }

                                //设置数据权限
                                EntityReturnData   contactDesc = contactService.getContactDesc(getAccessToken());
                                if (contactDesc.isSuccess()) {
                                    JSONObject entity_JSON = (JSONObject) contactDesc.getEntity();
                                    entity_JSON.remove("fields");
                                    dimJSONObject.put("createContact", entity_JSON.getBoolean("createable"));
                                }else {
                                    dimJSONObject.put("createContact", false);
                                }
                                EntityReturnData   opportunityDes = opportunityService.getOpportunityDesc(getAccessToken());
                                if (opportunityDes.isSuccess()){
                                    JSONObject entity_JSON = (JSONObject) opportunityDes.getEntity();
                                    dimJSONObject.put("createOpportunity", entity_JSON.getBoolean("createable"));
                                }else {
                                    dimJSONObject.put("createOpportunity", false);
                                }
                                //所属部门中文描述
                                long dimDepart = accountJSON.getLong("dimDepart");
                                String dimDepartText = null;
                                if(dimDepart != 0){
                                    EntityReturnData departmentEntity = departmentService.getDepartmentById(getAccessToken() ,dimDepart);
                                    if(departmentEntity.isSuccess()){
                                        JSONObject department  = (JSONObject) departmentEntity.getEntity();
                                        dimDepartText = department.getString("departName");
                                    }else{
                                        LOG.error("token="+getAccessToken()+" ,dimDepart = "+dimDepart+" departmentEntity="+JSON.toJSONString(departmentEntity));
                                    }
                                }
                                expandPro.put("industryIdText",industryIdText==null?"":industryIdText);
                                expandPro.put("ownerIdText",ownerIdText==null?"":ownerIdText);
                                // expandPro.put("members",members);
                                expandPro.put("dimDepartText",dimDepartText==null?"":dimDepartText);
                                expandPro.put("dataPermission",dimJSONObject);

                            }else{
                                success = false;
                                ret.setSuccess(false);
                                ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
                            }
                        }else {
                            success = false;
                            ret.setSuccess(false);
                            ret.setErrorCode(ErrorCode.NO_VIEW_AUTHORITY.getErrorCode());
                        }
                    }else{
                        success = false;
                        ret.setSuccess(false);
                        ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
                    }
                }
                if(success ){
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
                ret.setEntity(new JSONArray());
                ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
            }
        }catch (Exception e){
            ret = new EntityReturnData();
            ret.setErrorCode(ErrorCode.OTHER_ERROR.getErrorCode());
            e.printStackTrace();
        }

        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    /**
     * 查看公司状态
     * @return
     * @throws Exception
     */
    private Map<String,String> getAccouontStates() {
        Map<String ,String> map = new HashMap<String ,String>();
        try{
            if( getRequest().getSession().getAttribute("companyStatus") != null){
                map = (Map<String, String>) getRequest().getSession().getAttribute("companyStatus");
            }else {
                EntityReturnData ret = accountService.getAccountDesc(getAccessToken());
                if (ret.isSuccess()){
                    JSONObject entity_JSON = (JSONObject) ret.getEntity();
                    JSONArray fields = entity_JSON.getJSONArray("fields");
                    JSONArray dbcSelect1 = new JSONArray();
                    JSONObject item = null;

                    for(int i=0;i<fields.size();i++){
                        item = fields.getJSONObject(i);
                        if("dbcSelect1".equals(item.getString("propertyname"))){
                            dbcSelect1 = item.getJSONArray("selectitem");
                            break;
                        }
                    }

                    for(int i=0;i<dbcSelect1.size();i++){
                        item = dbcSelect1.getJSONObject(i);
                        map.put("t_"+item.getString("value"),item.getString("label"));
                    }
                    if(!map.isEmpty()){
                        getRequest().getSession().setAttribute("companyStatus",map);
                    }
                }else {
                    ret.setEntity(new JSONArray());
                    LOG.error("token="+getAccessToken()+" ,ret="+JSON.toJSONString(ret));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return map ;
    }

    /**
     * 公司可选行业id，供给下拉选择
     * @return
     * @throws Exception
     */
    @Action(value = "getstates", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getAccouontStates4web()  {
        EntityReturnData ret  =  new EntityReturnData() ;
        try{
            Map<String,String> map = this.getAccouontStates();
            JSONArray data = new JSONArray();
            if(!map.isEmpty()){
                for (Map.Entry<String,String> entry : map.entrySet()) {
                    JSONObject json = new JSONObject();
                    json.put("value",entry.getKey().replace("t_",""));
                    json.put("label",entry.getValue());
                    data.add(json);
                }
            }
            ret.setEntity(data);
            ret.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    @Action(value = "getDesc", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDesc()  {
        EntityReturnData ret = new EntityReturnData();
        try{
             ret = accountService.getAccountDesc(getAccessToken());
            if (ret.isSuccess()){
                JSONObject entity_JSON = (JSONObject) ret.getEntity();
                entity_JSON.remove("fields");
                ret.setEntity(entity_JSON);
            }else {
                ret.setEntity(new JSONObject());
            }
        }catch (Exception e){e.printStackTrace();}

        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }



    /**************************************************************************/
    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }



    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTeamitems() {
        return teamitems;
    }

    public void setTeamitems(String teamitems) {
        this.teamitems = teamitems;
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
