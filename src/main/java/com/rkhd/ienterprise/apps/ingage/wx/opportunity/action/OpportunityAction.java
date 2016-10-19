package com.rkhd.ienterprise.apps.ingage.wx.opportunity.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DateDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.OpportunityCloseDateTypes;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.TimeEnum;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.OpportunityDateTypeUtils;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.StaticsDateUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.enums.CustomerEntityType;
import com.rkhd.ienterprise.apps.ingage.enums.ObjectType;
import com.rkhd.ienterprise.apps.ingage.services.*;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
import com.rkhd.ienterprise.apps.ingage.utils.ParseFilesUtils;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.FormPageData;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by dell on 2016/1/22.
 */
@Namespace("/wx/opportunity")
public class OpportunityAction extends WxBaseAction {
    private static final Logger LOG = LoggerFactory.getLogger(OpportunityAction.class);

    @Autowired
    @Qualifier("mwebCustomizeService")
    private CustomizeService customizeService;

    @Autowired
    @Qualifier("mwebOpportunityService")
    private OpportunityService opportunityService;

    @Autowired
    @Qualifier("mwebCommonService")
    private CommonService commonService;

    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;

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
    @Qualifier("mwebReferenceService")
    private ReferenceService referenceService;

    /**
     * 阶段ID
     */
    private long saleStageId = 0;

    /**
     * 结单类型
     */
    private long closeType = 0;

    /**
     * 查询金额下限
     */
    private long  money_down = 0;
    /**
     * 查询金额上限
     */
    private long  money_up = 0;

    /**
     * 排序字段
     */
    private String orderField = null;

    /**
     * 降序
     */
    private boolean desc = true;

    /**
     * 商机信息
     */
    private String  opportunity;

    private Long opportunityId = 0L;

    private String opportunityName;

    private long  accountId = 0L;

    /**
     * 被设置商机关联的联系人
     */
    private String contactIds ;

    private long userId = 0L;

    private long searchDateType = 0;

    /**
     * 商机列表
     * @return
     * @throws Exception
     */
    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/opportunity/opportunity_list.jsp")})
    public String list() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "add", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/opportunity/opportunity_add.jsp")})
    public String add() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "update", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/opportunity/opportunity_update.jsp")})
    public String update() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "detail", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/opportunity/opportunity_detail.jsp")})
    public String detail_page() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "condition", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/opportunity/opportunity_condition.jsp")})
    public String condition() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist()  {
        EntityReturnData returnData = null;
        try{
            int pagesize = getPagesize();
            long closeType = getCloseType();
            long closetBeginTime = 0;
            long closetEndTime = 0;
            if(closeType != 0 ){
                OpportunityCloseDateTypes[] opportunityCloseDateTypes = OpportunityCloseDateTypes.values();
                OpportunityCloseDateTypes temp = null;
                for(OpportunityCloseDateTypes type :opportunityCloseDateTypes){
                    if(type.getValue() == closeType){
                        temp = type;
                        break;
                    }
                }
                DateDto dto =  OpportunityDateTypeUtils.getTimeInfo(temp);
                if(dto != null){
                    closetBeginTime = dto.getDownDate();
                     closetEndTime = dto.getUpDate();
                }
            }

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
            String searchName = XsyUtils.beautifyString(getOpportunityName());
            returnData = opportunityService.getOpportunityList(getAccessToken(),searchName,getAccountId(),getPageNo(),getPagesize(),
                    getSaleStageId(),closetBeginTime,closetEndTime,getMoney_down(),getMoney_up(),getOrderField(),isDesc(),createStartTime,createEndTime);
                    //accountService.getAccountList(getAccessToken(),getSearchName(),getLevel(),getPageNo(),pagesize);
            LOG.info("OpportunityAction dolist returnData={}",JSON.toJSONString(returnData));
            JSONObject tempJSON = null;

            if(returnData.isSuccess() && returnData.getEntity() != null ){
                JSONObject pageData = (JSONObject) returnData.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
                pageData.put("totalPage",totalPage);
                pageData.put("pageNo",getPageNo());
                pageData.put("pagesize",getPagesize());
                pageData.remove("count");

                //组装商机阶段的map
                Map<String,JSONObject> saleStageObjectMap = new HashMap<String, JSONObject>();
                JSONArray selectitem = this.getSaleStage_selectitem();
                for(int i=0;i<selectitem.size();i++){
                    JSONObject saleStage = selectitem.getJSONObject(i);
                    long saleStageId = saleStage.getLongValue("value");
                    saleStageObjectMap.put("S-"+saleStageId,saleStage);

                }
                JSONArray opportunitys = pageData.getJSONArray("records");
                JSONArray accountIds = new JSONArray();
                for(int i = 0; i < opportunitys.size(); i++){
                    tempJSON = opportunitys.getJSONObject(i);
                    //设置商机阶段名称
                    long saleStageId = tempJSON.getLong("saleStageId");
                    if(saleStageObjectMap.get("S-"+saleStageId) != null){
                        JSONObject saleStage = saleStageObjectMap.get("S-"+saleStageId);
                        tempJSON.put("saleStageText",saleStage.getString("label"));
                        tempJSON.put("saleStagePercentage",saleStage.getDoubleValue("percent"));//saleStage.getString("")
                        tempJSON.put("saleStageOrder",saleStage.getDoubleValue("order"));
                    }else{
                        tempJSON.put("saleStageText","");
                        tempJSON.put("saleStagePercentage","0");//saleStage.getString("")
                        tempJSON.put("saleStageOrder","0");
                    }

                    //为设置公司名称做准备
                    Long accountId = tempJSON.getLongValue("accountId");
                    accountIds.add(accountId);
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
                        for(int i = 0; i < opportunitys.size(); i++){
                            tempJSON = opportunitys.getJSONObject(i);
                            tempJSON.put("accountName",comapanyNameMap.get("C-"+tempJSON.getLong("accountId")) == null ? "" : comapanyNameMap.get("C-"+tempJSON.getLong("accountId")));
                        }
                    }else{
                        LOG.error("companyListEntity is {}",JSON.toJSONString(companyListEntity));
                    }
                }
                returnData.setEntity(pageData);
            }else {
                    LOG.error("opportunityService.getOpportunityList error,token=" + getAccessToken() + " ;searchName=" +searchName
                                +";accountId="+getAccountId()+";pageNo="+getPageNo()
                                +";pageSize="+getPagesize()+";saleStageId="+getSaleStageId()
                                    +";closetBeginTime="+closetBeginTime+";closetEndTime="+closetEndTime
                                    +";getMoney_down="+getMoney_down()+";getMoney_up="+getMoney_up()
                                    +";getOrderField="+getOrderField()+";isDesc="+isDesc()
                                    +";returnData="+JSON.toJSONString(returnData)
                    );
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }


    /**
     * 创建商机
     * @return
     * @throws Exception
     */
    @Action(value = "docreate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String docreate() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            String opportunityJSON = getOpportunity();
            if(XsyUtils.containEmojiCharacter(opportunityJSON)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject opportunityDto =  JSON.parseObject(opportunityJSON);
                //获取销售阶段
                returnData =  opportunityService.createOpportunity(getAccessToken(),opportunityDto);

                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
                    LOG.error("opportunityService.createOpportunity error,token=" + getAccessToken() +";opportunityDto="+JSON.toJSONString(opportunityDto)
                     +";openAprResult="+JSON.toJSONString(returnData));
                    returnData = new EntityReturnData();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 修改商机
     * @return
     * @throws Exception
     */
    @Action(value = "doupdate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doupdate() {
        EntityReturnData returnData = new EntityReturnData();
        try{
            String opportunityJSON = getOpportunity();
            if(XsyUtils.containEmojiCharacter(opportunityJSON)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                JSONObject opportunityDto =  JSON.parseObject(opportunityJSON);
                long id = opportunityDto.getLong("id");
                Long newdimDepart = opportunityDto.getLong("dimDepart");
                EntityReturnData dbAndExtraDta =  opportunityService.getOpportunityById(getAccessToken(),id);
                if(dbAndExtraDta.isSuccess()){
                    //如果所属部门没有改变则移除部门信息
                    JSONObject dbData = (JSONObject) dbAndExtraDta.getEntity();
                    Long dbDimDepart = dbData.getLong("dimDepart");
                    if(newdimDepart != null && dbDimDepart!= null  && dbDimDepart.longValue() == newdimDepart.longValue()){
                        opportunityDto.remove("dimDepart");
                    }
                }
                returnData =  opportunityService.updateOpportunity(getAccessToken(),opportunityDto);
                boolean isSuccess = returnData.isSuccess();
                if(!isSuccess){
                    LOG.error("opportunityService.updateOpportunity error,token=" + getAccessToken() +";opportunityDto="+JSON.toJSONString(opportunityDto)
                            +";openAprResult="+JSON.toJSONString(returnData));
                    returnData = new EntityReturnData();
                }
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 删除商机
     * @return
     * @throws Exception
     */
    @Action(value = "dodel", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dodel()  {
        EntityReturnData returnData = null;
        try{
            returnData =  opportunityService.deleteOpportunity(getAccessToken(),getOpportunityId());
            if(!returnData.isSuccess()){
                LOG.error("opportunityService.deleteOpportunity error,token=" + getAccessToken() +";opportunityId="+getOpportunityId()
                        +";openAprResult="+JSON.toJSONString(returnData));
                returnData = new EntityReturnData();
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 查询商机关联的联系人
     * @return
     * @throws Exception
     */
    @Action(value = "contacts", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String contacts()  {
        EntityReturnData returnData = null;
        try{
            returnData =  opportunityService.getContacts(getAccessToken(),getOpportunityId());
            if(returnData.isSuccess()){
               JSONObject entity = (JSONObject) returnData.getEntity();
                JSONArray contacts = entity.getJSONArray("contacts");
                JSONObject rel = null;
                Long contactId = null;
                for(int i=0;i<contacts.size();i++){
                     rel = contacts.getJSONObject(i);
                    contactId = rel.getLong("contactId");
                    EntityReturnData contactEntity = contactService.getContactById(getAccessToken(),contactId);
                    if(contactEntity.isSuccess()){
                        JSONObject contactJSON = (JSONObject) contactEntity.getEntity();
                        rel.put("contactName",contactJSON.getString("contactName"));
                        rel.put("phone",contactJSON.getString("phone"));
                        rel.put("mobile",contactJSON.getString("mobile"));
                        rel.put("post",contactJSON.getString("post"));
                    }else{
                        LOG.error("contactService.getContactById error,token=" + getAccessToken() +";contactId="+contactId
                                +";openAprResult="+JSON.toJSONString(contactEntity));
                        rel.put("contactName","");
                        rel.put("phone","");
                        rel.put("post","");
                        rel.put("mobile","");
                    }
                    contacts.set(i,rel);
                }

                // 防止xss攻击
                returnData.setEntity(CodeFilter.xddTerminator(entity));

            }
        }catch (Exception e){
            e.printStackTrace();
            returnData = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 设置商机关联联系人
     * @return
     */
    @Action(value = "setcontacts", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String setcontacts()  {
        EntityReturnData returnData = null;
        try{
            String currentSelectContactIds = getContactIds();
            EntityReturnData haveSelectedData  =  opportunityService.getContacts(getAccessToken(),getOpportunityId());
            if(haveSelectedData.isSuccess()){
                LOG.info("haveSelectedData={}",JSON.toJSONString(haveSelectedData));
                String haveSelectIds = "";
                String relationTableId = "";
                //haveSelectedData={"entity":{"totalSize":"1","contacts":[{"oppContactId":"94949","opportunityId":"179254","contactId":"146985","contactRoleId":"","mainFlg":"0"}]},"success":true}
                JSONObject pageData = (JSONObject)haveSelectedData.getEntity();
                long  totalSize = pageData.getLong("totalSize");
                if(totalSize > 0){
                    JSONArray contacts = pageData.getJSONArray("contacts");
                    JSONObject item = null;
                    for(int i = 0 ;i<contacts.size();i++){
                        item  = contacts.getJSONObject(i);
                        if(i>0){
                            haveSelectIds += ",";
                            relationTableId += ",";
                        }
                        haveSelectIds += item.getLong("contactId");
                        relationTableId +=  item.getLong("oppContactId");
                    }
                }

                List<Long> willBeAddContactIds = new ArrayList<Long>();
                List<Long> willBeDelContactIds = new ArrayList<Long>();
                String[] currentSelectContactIdArray = currentSelectContactIds.split(",");
                if(StringUtils.isNotBlank(haveSelectIds) ){
                    String[] haveBeSelectedIdArray = haveSelectIds.split(",");
                    String[] relationTableIdArray = relationTableId.split(",");

                    for(int i=0;i<haveBeSelectedIdArray.length;i++){
                        if((","+currentSelectContactIds+",").indexOf(","+haveBeSelectedIdArray[i]+",")<0){
                            willBeDelContactIds.add(new Long(relationTableIdArray[i]));
                        }
                    }
                    if(currentSelectContactIdArray != null ){
                        for(int i=0;i<currentSelectContactIdArray.length;i++){
                            if(StringUtils.isBlank(currentSelectContactIdArray[i])){
                                continue;
                            }
                            if((","+haveSelectIds+",").indexOf(","+currentSelectContactIdArray[i]+",")<0){
                                willBeAddContactIds.add(new Long(currentSelectContactIdArray[i]));
                            }
                        }
                    }

                }else{
                    for(int i=0;i<currentSelectContactIdArray.length;i++){
                        willBeAddContactIds.add(new Long(currentSelectContactIdArray[i]));
                    }
                }
                boolean exeuteFirst = false;
                boolean exeuteSecond = false;
                if(willBeDelContactIds.size()>0){
                    returnData = opportunityService.deleteContacts(getAccessToken(),getOpportunityId(),willBeDelContactIds);
                    if(returnData.isSuccess()){
                        exeuteFirst = true;
                    }else {
                        exeuteFirst = false;
                        LOG.error("opportunityService.deleteContacts error,token=" + getAccessToken() +";opportunityId="+getOpportunityId()
                                +"willBeDelContactIds="+JSON.toJSONString(willBeDelContactIds)+" ;openAprResult="+JSON.toJSONString(returnData));

                    }
                }else {
                    exeuteFirst = true;
                }
                if(exeuteFirst){
                    //开始执行新增操作
                    if(willBeAddContactIds.size()>0){
                        returnData = opportunityService.addContacts(getAccessToken(),getOpportunityId(),willBeAddContactIds);
                        if(returnData.isSuccess()){
                            exeuteSecond = true;
                        }else{
                            exeuteSecond = false;
                            LOG.error("opportunityService.addContacts error,token="+getAccessToken()+", getOpportunityId="+getOpportunityId()+",willBeAddContactIds="+ JSONObject.toJSONString(willBeAddContactIds)+";openAprResult="+JSON.toJSONString(returnData));
                        }
                    }else{
                        returnData  = new EntityReturnData();
                        returnData.setSuccess(true);
                        
                    }
                }

            }else{
                returnData = haveSelectedData;
            }

        }catch (Exception e){
            e.printStackTrace();
            LOG.info("set  opportunity contact error ,errormsg ={}"+e.getMessage());
            returnData = new EntityReturnData();
        }

        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    /**
     * 查询结单时段类型
     * @return
     */
    @Action(value = "getCloseDateType", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getCloseDateType(){
        EntityReturnData ret = new EntityReturnData();
        ret.setSuccess(true);
        OpportunityCloseDateTypes[] types =  OpportunityCloseDateTypes.values();
        JSONArray retJSONArray = new JSONArray();
        for(OpportunityCloseDateTypes type :types){
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
 * 商机转移
 * */
    @Action(value = "trans", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String trans(){
        EntityReturnData ret = null;
        try{
          ret =  opportunityService.transOpportunity(getAccessToken(),getOpportunityId(),getUserId());
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("method=trans,error.msg="+e.getMessage()+",opportunityId="+getOpportunityId()+",targetUserId="+getUserID());
            ret = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    @Action(value = "getDetail", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDetail()  {
        EntityReturnData ret = null;
        try{
            CustomerEntityType type = CustomerEntityType.opportunity;
            ret = customizeService.getDefaultCustomizeLayout(getAccessToken(),0L,type);

            FormPageData formPageData = new FormPageData();
            if (ret.isSuccess()){
                //获取字段结构
                JSONObject layout_JSON = (JSONObject) ret.getEntity();
               JSONObject parseLayoutJson = ParseFilesUtils.parseDefaultLayout(layout_JSON,getScene(), ObjectType.OPPORTUNITY);
                formPageData.setStructure(parseLayoutJson);

//                formPageData.setStructure(ParseFilesUtils.parseFields(fields,getScene(), ObjectType.OPPORTUNITY));
                //扩展属性
                JSONObject expandPro = new JSONObject();
                Long opportunityId = getOpportunityId();

                boolean success = true;
                //获取字段值
                if(opportunityId != null && opportunityId != 0 ){
                    EntityReturnData dataEntityReturnData =  dimensionService.dataPermission(getAccessToken(),3,opportunityId);
                    if(dataEntityReturnData.isSuccess()){
                        JSONObject dataPermission = (JSONObject) dataEntityReturnData.getEntity();
                        JSONObject dimJSONObject = dataPermission.getJSONObject("dataPermission");
                        if(dimJSONObject.getBoolean("view")){
                            EntityReturnData dbAndExtraDta =  opportunityService.getOpportunityById(getAccessToken(),opportunityId);
                            if(dbAndExtraDta.isSuccess()){

                                JSONArray  components  = layout_JSON.getJSONArray("components");
                                Map<String,String> mapData = new HashMap<String, String>();//存储select里的值，键的命名方式为：propertNname+value:Label
                                JSONObject itemJson = null;
                                String fieldType = null;
                                JSONObject subItemJson = null;
                                JSONObject selectlistPropertyMap = new JSONObject();
                                JSONObject checkboxPropertyMap = new JSONObject();

                                JSONObject temp_expandPro = new JSONObject();
                                for(int i=0;i<components.size();i++){
                                    itemJson = components.getJSONObject(i);
                                    fieldType = itemJson.getString("type");
                                    String propertyName =  itemJson.getString("propertyName");
                                    LOG.info(JSON.toJSONString(itemJson) );
                                    if("ITEM_TYPE_SELECT".equals(fieldType)){

                                        JSONArray itemArray = itemJson.getJSONArray("selectitem");
                                        temp_expandPro.put(propertyName+"Items", JSONArray.parse(itemArray.toJSONString()));
                                        selectlistPropertyMap.put(propertyName,true);
                                        for(int j=0;j<itemArray.size();j++){
                                            subItemJson = itemArray.getJSONObject(j);
                                            mapData.put(propertyName+""+subItemJson.getString("value"),subItemJson.getString("label"));
                                        }
                                    }else  if("ITEM_TYPE_DUMMY".equals(fieldType) && ( "saleStageId".equals(propertyName  ) || "sourceId".equals(propertyName)    )){

                                        JSONArray itemArray = itemJson.getJSONArray("dummyItemBean");
                                        temp_expandPro.put(propertyName+"Items", JSONArray.parse(itemArray.toJSONString()));

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
                                JSONObject dbDataJson = (JSONObject) dbAndExtraDta.getEntity();
                                ParseFilesUtils.dataBeatutiful(dbDataJson,ObjectType.OPPORTUNITY);
                                formPageData.setData(dbDataJson);

                                //组装下拉选Text数据
                                Set<String> keySet = dbDataJson.keySet();
                                Iterator<String> it =  keySet.iterator();
                                while(it.hasNext()){
                                    String propertyName = it.next();
                                    if(selectlistPropertyMap.containsKey(propertyName)){
                                        Long dbValue = dbDataJson.getLong(propertyName);
                                        if(dbValue != null && dbValue.longValue() != 0){
                                            String dbcSelectText = mapData.get(propertyName+dbValue);
                                            expandPro.put(propertyName+"Text",dbcSelectText==null?"":dbcSelectText);
                                        }
                                    }else if(checkboxPropertyMap.containsKey(propertyName)){
                                        JSONArray dbValues = dbDataJson.getJSONArray(propertyName);
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


                                Long ownerId  = dbDataJson.getLong("ownerId");
                                String  ownerIdText = null;
                                if(ownerId != null && ownerId != 0){
                                    EntityReturnData userInfoEntityReturnData =   xsyApiUserService.getUserInfo(getAccessToken(),ownerId);
                                    if(userInfoEntityReturnData.isSuccess()){
                                        JSONObject ownerUser = (JSONObject) userInfoEntityReturnData.getEntity();
                                        ownerIdText = ownerUser.getString("name");

                                    }else {
                                        LOG.error(" xsyApiUserService.getUserInfo error,token=" + getAccessToken() +";ownerId="+ownerId
                                                +";openAprResult="+JSON.toJSONString(userInfoEntityReturnData));
                                    }
                                }
                                expandPro.put("ownerIdText",ownerIdText);

                                Long accountId = dbDataJson.getLong("accountId");
                                String accountIdText = null;
                                if(accountId != null && accountId !=0){
                                    JSONArray accountIds = new JSONArray();
                                    accountIds.add(accountId);

                                    EntityReturnData companyListEntity =  referenceService.referenceSearch(getAccessToken(),1,accountIds);
                                    JSONObject companyDataObj = (JSONObject) companyListEntity.getEntity();
                                    JSONArray compays = companyDataObj.getJSONArray("records");
                                    for(int m = 0;m<compays.size();m++){
                                        JSONObject cJSON = compays.getJSONObject(m);
                                        accountIdText = cJSON.getString("name");
                                    }
                                }
                                expandPro.put("accountIdText",accountIdText==null?"":accountIdText);

                                Long saleStageId = dbDataJson.getLong("saleStageId");

                                String saleStageIdText = null;
                                Long  saleStageIdPercentage = null;
                                Integer saleStageIdOrder = null;
                                if(saleStageId != null && saleStageId != 0){
                                    JSONArray selectitem =   temp_expandPro.getJSONArray("saleStageIdItems"); //saleStageIdObject.getJSONArray("selectitem");
                                    for(int i=0;i<selectitem.size();i++){
                                        JSONObject item = selectitem.getJSONObject(i);
                                        long saleStage = item.getLongValue("value");
                                        if(saleStageId == saleStage){
                                            saleStageIdText = item.getString("label");
                                            saleStageIdPercentage = item.getLongValue("percent");
                                            saleStageIdOrder = item.getIntValue("order");
                                            break;
                                        }
                                    }
                                }

                                Long createdBy = dbDataJson.getLong("createdBy");
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
                                Long updatedBy = dbDataJson.getLong("updatedBy");
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


                                expandPro.put("saleStageIdText",saleStageIdText==null?"":saleStageIdText);
                                expandPro.put("saleStageIdPercentage",saleStageIdPercentage==null?"0":saleStageIdPercentage);
                                expandPro.put("saleStageIdOrder",saleStageIdOrder==null?"0":saleStageIdOrder);

                                expandPro.put("dataPermission",dimJSONObject);

                                //所属部门中文描述
                                long dimDepart = dbDataJson.getLong("dimDepart");
                                if(dimDepart != 0){
                                    EntityReturnData departmentEntity = departmentService.getDepartmentById(getAccessToken() ,dimDepart);
                                    if(departmentEntity.isSuccess()){
                                        JSONObject department  = (JSONObject) departmentEntity.getEntity();
                                        String departmetnName = department.getString("departName");
                                        expandPro.put("dimDepartText",departmetnName);
                                    } else{
                                        LOG.error(" dimensionService.getDepartmentById error,token=" + getAccessToken() +";dimDepart="+dimDepart
                                                +";openAprResult="+JSON.toJSONString(departmentEntity));
                                        expandPro.put("dimDepartText","");
                                    }
                                }

                                Long sourceId =  dbDataJson.getLong("sourceId");
                                String sourceIdText = null;
                                if(sourceId != null && sourceId != 0){
                                    sourceIdText = mapData.get("sourceId"+sourceId);
                                }
                                expandPro.put("sourceIdText",sourceIdText==null?"":sourceIdText);

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

                    }else {
                        success = false;
                        ret.setSuccess(false);
                        ret.setErrorCode(ErrorCode.XSY_API_ERROR.getErrorCode());
                    }


                }
                if(success){
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
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    /**
     * 查询销售商机级别
     * @return
     */
    @Action(value = "getSaleStage", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getSaleStage (){
        EntityReturnData ret = new EntityReturnData();
        JSONArray selectitem = (JSONArray) this.getSaleStage_selectitem();
        if(selectitem == null ){
            selectitem = new JSONArray();
        }
        ret.setSuccess(true);
        ret.setEntity(selectitem);
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    private JSONArray getSaleStage_selectitem(){
        JSONArray selectitem = (JSONArray) getRequest().getSession().getAttribute("saleStage_selectitem");
//        LOG.info("selectitem={}",JSON.toJSONString(selectitem));
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
                }else{
                    LOG.error("opportunityService.getOpportunityDesc error;token="+getAccessToken());
                }
            }catch (Exception e){
                e.printStackTrace();

            }
        }
        return selectitem;
    }

    @Action(value = "getDesc", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getDesc(){
        EntityReturnData ret = new EntityReturnData();
        try{
            ret = opportunityService.getOpportunityDesc(getAccessToken());
            if (ret.isSuccess()){
                JSONObject entity_JSON = (JSONObject) ret.getEntity();
                entity_JSON.remove("fields");
                entity_JSON.remove("productFields");
                ret.setEntity(entity_JSON);
            }else {
                ret.setEntity(new JSONObject());
            }
        }catch (Exception e){
            e.printStackTrace();
            ret = new EntityReturnData();
        }

        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    public String getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(String opportunity) {
        this.opportunity = opportunity;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getOpportunityName() {
        return opportunityName;
    }

    public void setOpportunityName(String opportunityName) {
        this.opportunityName = opportunityName;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getContactIds() {
        return contactIds;
    }

    public void setContactIds(String contactIds) {
        this.contactIds = contactIds;
    }

    public long getSaleStageId() {
        return saleStageId;
    }

    public void setSaleStageId(long saleStageId) {
        this.saleStageId = saleStageId;
    }

    public long getCloseType() {
        return closeType;
    }

    public void setCloseType(long closeType) {
        this.closeType = closeType;
    }

    public long getMoney_down() {
        return money_down;
    }

    public void setMoney_down(long money_down) {
        this.money_down = money_down;
    }

    public long getMoney_up() {
        return money_up;
    }

    public void setMoney_up(long money_up) {
        this.money_up = money_up;
    }


    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(long searchDateType) {
        this.searchDateType = searchDateType;
    }
}
