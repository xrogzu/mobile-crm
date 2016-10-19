package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.util.PinyinUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;


@Namespace("/dingtalk/account")
public class AccountAction extends BaseAction{
    private static final Logger LOG = LoggerFactory.getLogger(AccountAction.class);


    @Autowired
    private IsvReceiveService isvReceiveService;
    @Autowired
    private PassportService passportService;

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
    @Qualifier("mwebCommonService")
    private CommonService commonService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    private FeedService feedService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;



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
     * 公司列表
     * @return
     * @throws Exception
     */
    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/account/account_list.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "info", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/account/account_info.jsp")})
    public String info() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "search", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/account/account_search.jsp")})
    public String search() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/account/account_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/account/account_update.jsp")})
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
                LOG.error("token={} ,result is {}",getAccessToken(),JSON.toJSONString(ret));
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
            EntityReturnData returnData1 = accountService.getAccountList(getAccessToken(),searchName,getLevel(),getPageNo(),pagesize,0,0);
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

                pageData.put("records",newaccounts);

                returnData1.setEntity(pageData);
                getRequest().setAttribute("jsondata",returnData1);
            }else {
                LOG.error("token={} ,searchName = "+searchName+",level="+getLevel()+";pageNo="+getPageNo()+"  result is {}",getAccessToken(),JSON.toJSONString(ret));
                getRequest().setAttribute("jsondata",returnData1);
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
    public String dosearch() throws Exception{
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
                    tempJSON = null;
                    temp_name = null;
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
     * 查看某个客户的详情
     * @return
     * @throws Exception
     */
    @Action(value = "get", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String get() {
        EntityReturnData retdata = null;
        try{
             retdata = accountService.getAccountById(getAccessToken(),this.getAccountId());
            if(retdata.isSuccess()){
                JSONObject accountJSON = (JSONObject) retdata.getEntity();
                long industryId =  0L;
                if(StringUtils.isNotBlank(accountJSON.getString("industryId"))){
                    industryId = accountJSON.getLong("industryId");
                }
                long levelId = 0L;
                if(StringUtils.isNotBlank(accountJSON.getString("level"))){
                    levelId = accountJSON.getLong("level");
                }
                EntityReturnData descRet  = accountService.getAccountDesc(getAccessToken());
                if (descRet.isSuccess()){
                    JSONObject desc_JSON = (JSONObject) descRet.getEntity();
                    //开始设置行业名称
                    boolean haveIndustryIdText = false;
                    boolean haveLevelText = false;

                    JSONObject jsonObject = null;
                    JSONArray selectitems = null;
                    JSONArray levelitems = null;
                    JSONArray fields = desc_JSON.getJSONArray("fields");
                    for(int i=0;i<fields.size();i++)
                    {
                        jsonObject = fields.getJSONObject(i);
                        if("industryId".equals(jsonObject.getString("propertyname"))){
                            selectitems = jsonObject.getJSONArray("selectitem");
                        }else if("level".equals(jsonObject.getString("propertyname")) ){
                            levelitems = jsonObject.getJSONArray("selectitem");
                        }
                    }
                    if(industryId > 0){
                        //遍历设置行业名称
                        for(int i=0;i<selectitems.size();i++){
                            jsonObject = selectitems.getJSONObject(i);
                            if( industryId == jsonObject.getLong("value") ){
                                accountJSON.put("industryIdText",jsonObject.getString("label"));
                                haveIndustryIdText = true;
                            }
                        }
                    }
                    if(!haveIndustryIdText){
                        accountJSON.put("industryIdText","");
                    }

                    if(levelId > 0){
                        for(int i=0;i<levelitems.size();i++){
                            jsonObject = levelitems.getJSONObject(i);
                            if( levelId == jsonObject.getLong("value") ){
                                accountJSON.put("levelText",jsonObject.getString("label"));
                                haveLevelText = true;
                            }
                        }
                    }
                    if(!haveLevelText){
                        accountJSON.put("levelText","");
                    }
                }else {
                    LOG.error("token="+getAccessToken()+" ,descRetdescRet="+JSON.toJSONString(descRet));
                }
                boolean haveOwner = false;
                long ownerId =   accountJSON.getLong("ownerId");
                if(ownerId != 0){
                    EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),ownerId);

                    if(userInfoEntityReturnData.isSuccess()){
                        JSONObject ownerUser = (JSONObject) userInfoEntityReturnData.getEntity();
                        accountJSON.put("ownerText",ownerUser.getString("name"));
                        haveOwner = true;
                    }else {
                        LOG.error("token="+getAccessToken()+" ,ownerId = "+ownerId+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                    }
                }
                if(!haveOwner){
                    accountJSON.put("ownerText","");
                }

                JSONArray members = accountJSON.getJSONArray("members");
                if(members != null && members.size()>0){
                    int size = members.size();
                    for(int i=0;i<size;i++){
                        JSONObject member = members.getJSONObject(i);
                        long memberId = member.getLong("id");
                        EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),memberId);
                        if(userInfoEntityReturnData.isSuccess()){
                            JSONObject memberUser = (JSONObject) userInfoEntityReturnData.getEntity();
                            member.put("name",memberUser.getString("name"));
                        }else {
                            LOG.error("token="+getAccessToken()+" ,memberId = "+memberId+" userInfoEntityReturnData="+JSON.toJSONString(userInfoEntityReturnData));
                            member.put("name","");
                        }
                        members.set(i,member);
                    }
                    accountJSON.put("members",members);
                }

                EntityReturnData dataEntityReturnData =  dimensionService.dataPermission(getAccessToken(),1,getAccountId());
//                LOG.info("dataEntityReturnData={}",JSON.toJSONString(dataEntityReturnData));
                if(dataEntityReturnData.isSuccess()){
                    JSONObject dataPermission = (JSONObject) dataEntityReturnData.getEntity();
                    accountJSON.put("dataPermission",dataPermission.getJSONObject("dataPermission"));
                }else {
                    accountJSON.put("dataPermission",new JSONObject());
                    LOG.error("token="+getAccessToken()+" ,accouontId = "+getAccountId()+" dataEntityReturnData="+JSON.toJSONString(dataEntityReturnData));
                }
                //公司状态中文描述
                Map<String,String> statesMap =  getAccouontStates();
                String dbcSelect1Text = statesMap.get("t_"+accountJSON.getString("dbcSelect1"));
                accountJSON.put("dbcSelect1Text",dbcSelect1Text==null?"":dbcSelect1Text);

                //所属部门中文描述
                long dimDepart = accountJSON.getLong("dimDepart");
                if(dimDepart != 0){
                    EntityReturnData departmentEntity = departmentService.getDepartmentById(getAccessToken() ,dimDepart);
                    if(departmentEntity.isSuccess()){
                        JSONObject department  = (JSONObject) departmentEntity.getEntity();
                        String departmetnName = department.getString("departName");
                        accountJSON.put("dimDepartText",departmetnName);
                    }else{
                        accountJSON.put("dimDepartText","");
                        LOG.error("token="+getAccessToken()+" ,dimDepart = "+dimDepart+" departmentEntity="+JSON.toJSONString(departmentEntity));
                    }
                }
                accountJSON = CodeFilter.xddTerminator(accountJSON);
                retdata.setEntity(accountJSON);
            }else {
                LOG.error("token={} ,accountId= "+getAccountId()+";reslult="+JSON.toJSONString(retdata),getAccessToken());
            }

        }catch(Exception e){
             retdata = new EntityReturnData();
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
                }else{
                    LOG.error("token="+getAccessToken()+" ,account = "+JSON.toJSONString(account)+" returnData="+JSON.toJSONString(returnData));
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

                JSONObject account =  JSON.parseObject(account_string);
                //设置客户所有人
                //account.setOwnerId(getUserID());
//                Long dimDepart = commonService.getDepartmentDimension(getAccessToken());
//                account.setDimDepart(dimDepart);
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

    @Action(value = "getDesc", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getAddDesc() throws Exception{
        EntityReturnData ret = accountService.getAccountDesc(getAccessToken());
        if (ret.isSuccess()){
            JSONObject entity_JSON = (JSONObject) ret.getEntity();
            JSONArray fields = entity_JSON.getJSONArray("fields");
            ret.setEntity(fields);
        }else {
            ret.setEntity(new JSONArray());
            LOG.error("token="+getAccessToken()+" ,ret="+JSON.toJSONString(ret));
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    /**
     * 查看公司状态
     * @return
     * @throws Exception
     */
    private Map<String,String> getAccouontStates() throws Exception{
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





}
