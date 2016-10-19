package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ActivityRecordDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.services.AccountService;
import com.rkhd.ienterprise.apps.ingage.services.ActivityRecordService;
import com.rkhd.ienterprise.apps.ingage.services.ContactService;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.ActivityRecordTypeTransUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
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
 * Created by dell on 2016/1/13.
 */
@Namespace("/dingtalk/activityrecord")
public class ActivityRecordAction extends BaseAction{
    private static final Logger LOG = LoggerFactory.getLogger(ActivityRecordAction.class);

    private int id = 0;


    private long belongId = 0;//来源业务对象1：客户，2：联系人，3：销售机会，6：市场活动，11：销售线索

    private long objectId = 0l;//来源对象的ID，如客户的ID


    @Autowired
    @Qualifier("mwebActivityRecordService")
    private ActivityRecordService activityRecordService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    @Qualifier("mwebAccountService")
    private AccountService accountService;

    @Autowired
    @Qualifier("mwebContactService")
    private ContactService contactService;

    private String activityRecordDtoString ;

    @Action(value = "create", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/activityrecord/activityrecord_create.jsp")})
    public String createPage() throws Exception{

        return  SUCCESS;
    }
    @Action(value = "qiandao", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/activityrecord/activityrecord_qiandao.jsp")})
    public String qiandaoPage() throws Exception{

        return  SUCCESS;
    }


    @Action(value = "dolist", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dolist() throws Exception{
        int pagesize = getPagesize();
        long belongId = getBelongId();
        long objectId = getObjectId();
        EntityReturnData returnData1 = activityRecordService.getPage(getAccessToken(),getPageNo(),pagesize,belongId,objectId,null,null,0,null);
        if(returnData1.isSuccess() && returnData1.getEntity() != null ){
            JSONObject pageData = (JSONObject) returnData1.getEntity();
            long totalSize = pageData.getLong("totalSize");
            long totalPage = ((totalSize%pagesize==0)?(totalSize/pagesize):(totalSize/pagesize+1));
            pageData.put("totalPage",totalPage);
            pageData.put("pageNo",getPageNo());
            pageData.put("pagesize",getPagesize());
            pageData.remove("count");
            JSONArray recordTypes = this.getActivityRecordType();
            Map<String,JSONObject> recordTypeMap = new HashMap<String,JSONObject>();
            JSONObject temp = null;
            for(int i=0;i<recordTypes.size();i++){
                temp = recordTypes.getJSONObject(i);
                recordTypeMap.put("type_"+temp.getLong("id"),temp);
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
                        LOG.error("xsyApiUserService.getUserInfo error, token="+getAccessToken()+",userid="+record.getLong("uid"));
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
//            防止xss攻击
            pageData = CodeFilter.xddTerminator(pageData);
            returnData1.setEntity(pageData);
        }else{
            LOG.error("activityRecordService.getPage error, token="+getAccessToken()+",pageNo="+getPageNo()+",pagesize="+pagesize+",belongId="+belongId+",objectId="+objectId);
        }
        getRequest().setAttribute("jsondata",returnData1);
        return SUCCESS;
    }
    @Action(value = "get", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String get()  {
        int pagesize = getPagesize();
        EntityReturnData returnData1 =  null;
        try{
            returnData1 =  activityRecordService.get(getAccessToken(),getId());
            if(!returnData1.isSuccess()){
                LOG.error("activityRecordService.get error, token="+getAccessToken()+",id="+getId());
            }
        }catch (Exception e){
            e.printStackTrace();
            returnData1 = new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",returnData1);
        return SUCCESS;
    }

    /**
     *创建活动记录
     */
    @Action(value = "docreate", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String create() {
        String activityRecordDtoString = this.getActivityRecordDtoString();
        EntityReturnData returnData =  new EntityReturnData();
        try{
            if(XsyUtils.containEmojiCharacter(activityRecordDtoString)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                ActivityRecordDto activityRecordDto = JSON.parseObject(activityRecordDtoString, ActivityRecordDto.class);
                long activityTypeId = ActivityRecordTypeTransUtil.trans(getAccessToken(),getRequest(),activityRecordDto.getActivityTypeId());
                activityRecordDto.setActivityTypeId(activityTypeId);
                returnData =  activityRecordService.addActivityRecord(getAccessToken(),activityRecordDto);
                if(!returnData.isSuccess()){
                    LOG.error("activityRecordService.addActivityRecord  error,token="+getAccessToken()+";dto="+JSON.toJSONString(activityRecordDto));
                }
            }
        }catch (Exception e){
            LOG.error("ActivityRecordAction doCreate error,msg="+e.getMessage());
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }

    public JSONArray getActivityRecordType(){
        JSONArray params = null;
        ActivityRecordService service = new ActivityRecordService();
        EntityReturnData entityReturnData =  service.getDesc(getAccessToken());
        if(entityReturnData.isSuccess()){
            JSONObject descJSONObject = (JSONObject) entityReturnData.getEntity();
            JSONArray busiparamsArray = descJSONObject.getJSONArray("busiparams");
            JSONObject paramsJSONObject = null;
            for(int i=0;i<busiparamsArray.size();i++){
                paramsJSONObject = busiparamsArray.getJSONObject(i);
//                LOG.info("{}", JSON.toJSONString(paramsJSONObject));
                if(paramsJSONObject.getString("fieldname").equals("type")){
                     params = paramsJSONObject.getJSONArray("params");
                    break;
                }
            }
        }else {
            LOG.error("service.getDesc error ，token={}，entityReturnData is：{}",getAccessToken(),JSONObject.toJSONString(entityReturnData));
        }
        return params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityRecordDtoString() {
        return activityRecordDtoString;
    }

    public void setActivityRecordDtoString(String activityRecordDtoString) {
        this.activityRecordDtoString = activityRecordDtoString;
    }


    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public long getBelongId() {
        return belongId;
    }

    public void setBelongId(long belongId) {
        this.belongId = belongId;
    }
}
