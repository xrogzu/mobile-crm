package com.rkhd.ienterprise.apps.ingage.wx.helper;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.enums.SynErrorMsg;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.SynResult;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.auth.service.ResponsibilityService;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.rkhd.ienterprise.thirdparty.model.SynResult;
//import com.rkhd.ienterprise.thirdparty.service.SynResultService;

/**
 * 授权同步执行完后执行该方法
 */
@Component
public class AuthCompositionHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AuthCompositionHelper.class);

    private WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private SynResultService synResultService;
    @Autowired
    private SynPassportService synPassportService;
    @Autowired
    private  SynWxCorpService  synWxCorpService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private  SynWxUser2XsyUserService synWxUser2XsyUserService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private ResponsibilityService responsibilityService;

    @Autowired
    private CommonSynHelper commonSynHelper;

    public static final int ROLE_SYSTEM_MANAGER = 1;//系统管理员
    public static final int ROLE_NORMAL = 2;//普通用户

    public static final String ROLE_SYSTEM_MANAGER_KEY = "ROLE_SYSTEM_MANAGER";//系统管理员
    public static final String ROLE_NORMAL_KEY = "ROLE_NORMAL";//普通用户


    public static final int RESPONSIBILITY_SYSTEM = 1;//系统管理员职能
    public static final int RESPONSIBILITY_NORMAL = 2;//普通用户职能

    public static final String RESPONSIBILITY_SYSTEM_KEY = "RESPONSIBILITY_SYSTEM";//系统管理员职能
    public static final String RESPONSIBILITY_NORMAL_KEY = "RESPONSIBILITY_NORMAL";//普通用户职能


    /**
     * 参数为http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BA%94%E7%94%A8%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * 获取企业号的永久授权码方法返回的参数
     * @param authJson
     * @return
     */
    public JSONObject doSyncAuth(JSONObject authJson, WxInfoType wxInfoType)  {

        String corp_access_token = authJson.getString("access_token");//授权方（企业）access_token
        JSONObject auth_corp_info = authJson.getJSONObject("auth_corp_info");//授权企业信息
        String corpid = auth_corp_info.getString("corpid");//授权方（企业）ID
        String  corp_name = auth_corp_info.getString("corp_name");
        //被授权信息
//        JSONObject auth_info = authJson.getJSONObject("auth_info");
        JSONObject auth_user_info = authJson.getJSONObject("auth_user_info");//授权用户

        String wxuserid = auth_user_info.getString("userid");
        boolean success = false;
        Date now = new Date();
        String  errorMsg = null;

        if(StringUtils.isBlank(wxuserid)){
            //开始同步只获得用户emai,或者只获得mobile的情况
            try {
                JSONObject wxUser  = getWxUserByEmailOrMobile(authJson,corp_access_token);
                String manager_email = auth_user_info.getString("email");
                String manager_mobile = auth_user_info.getString("mobile");
                String weixinid = null;
                if(wxUser != null && wxUser.get("weixinid") != null){
                    weixinid = wxUser.getString("weixinid");
                }
                // 同步管理员的passport
                JSONObject synPassportResultJSONObject = synPassportService.doSynPassport4Manager(weixinid  ,manager_email, manager_mobile);
                long passportId = synPassportResultJSONObject.getLong("passportId");
                JSONObject synCorpResult =  synWxCorpService.synWxCorp(authJson);
                boolean synCorpResultSuceess = synCorpResult.getBoolean("success");

                if(!synCorpResultSuceess){
                    saveSynResult(  corpid,now ,"false",SynErrorMsg.REGISTERSERVICE_ERROR  );
                    errorMsg = SynErrorMsg.REGISTERSERVICE_ERROR.name();
                    success = false;
                }else {
                    TenantParam tenantParam = (TenantParam) synCorpResult.get("tenantParam");
                    //判断passportID与租户之间是否建立关系
                    long xsyTenantid = tenantParam.getTenantId();
                    boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passportId, xsyTenantid);
                    if (!exitPassportIdAndTenantId) {
                        commonService.joinPassportInTenant(xsyTenantid, passportId);
                    }
                    Map<String, Role> roleMap = this.getTenantRoleMap(tenantParam);
                    Map<String, Responsibility> responsibilityMap = this.getTenantResponsibilityMap(tenantParam);

                    //同步系统管理员
                    JSONObject synManagerResultJSON = synWxUser2XsyUserService.doSyncWXUser2Xsy4NoUserId(tenantParam,  corpid, roleMap, responsibilityMap, true,  wxInfoType,manager_email,manager_mobile);

                    //同步系统管理员完毕
                    if (synManagerResultJSON.getBoolean("success")) {
//同步其他非管理员用户
                        doSyncAfterManager(synManagerResultJSON.getLong("xsy_userId"),authJson,wxInfoType,tenantParam);
//                        保存同步结果
                        saveSynResult(  corpid,now ,"true",SynErrorMsg.OK  );
                        errorMsg = SynErrorMsg.OK.name();
                        success = true;
                    }else{
                        LOG.error(corp_name+"["+corpid+"] syn failed for before reason ");
                        saveSynResult(  corpid,now ,"false",SynErrorMsg.SYN_SYSTEMMANAGER_ERROR  );
                        errorMsg = SynErrorMsg.SYN_SYSTEMMANAGER_ERROR.name();
                    }
                }

            } catch (WXException e) {
                e.printStackTrace();
                LOG.error("syn failed,error msg:"+e.getMessage());
                errorMsg = SynErrorMsg.WX_SERVICE_ERROR.name();
            } catch (ServiceException e) {
                e.printStackTrace();
                errorMsg = SynErrorMsg.SERVICE_ERROR.name();
                LOG.error("syn failed,error msg:"+e.getMessage());
            } catch (PaasException e) {
                e.printStackTrace();
                LOG.error("syn failed,error msg:"+e.getMessage());
                errorMsg = SynErrorMsg.SERVICE_ERROR.name();
            }

        }else {
            try {
                saveSynResult(  corpid,now ,"true",SynErrorMsg.DOING  );

                JSONObject wxUser  = wxApi.get_user(corp_access_token,wxuserid);
                LOG.info("manager wxUser :"+JSON.toJSONString(wxUser));
                //{"errmsg":"no privilege to access/modify contact/party/agent ","errcode":60011}
                if(wxUser.containsKey("errcode") && 0 != wxUser.getIntValue("errcode")){
                    saveSynResult(  corpid,now ,"false",SynErrorMsg.MANAGER_STATE_ERROR  );
                    errorMsg = SynErrorMsg.MANAGER_STATE_ERROR.name();
                }else{
                    //同步系统管理员的pssport
                    JSONObject synPassportResultJSONObject = synPassportService.doSynPassport(wxUser);
                    boolean passportDoExecute = synPassportResultJSONObject.getBoolean("doExecute");
                    long passportId = synPassportResultJSONObject.getLong("passportId");

                    if(!passportDoExecute){
                        int status = wxUser.getIntValue("status");
                        String  weixinid = wxUser.getString("weixinid");
                        String  email = wxUser.getString("email");
                        String  mobile = wxUser.getString("mobile");
                        if(status != 1){
                            saveSynResult(  corpid,now ,"false",SynErrorMsg.MANAGER_STATE_ERROR  );
                            errorMsg = SynErrorMsg.MANAGER_STATE_ERROR.name();
                        }else if(StringUtils.isBlank(weixinid)){
                            saveSynResult(  corpid,now ,"false",SynErrorMsg.NO_WEIXINID  );
                            errorMsg = SynErrorMsg.NO_WEIXINID.name();
                        }else if(StringUtils.isBlank(email) && StringUtils.isBlank(mobile)){
                            saveSynResult(  corpid,now ,"false",SynErrorMsg.NO_LINKINFO  );
                            errorMsg = SynErrorMsg.NO_LINKINFO.name();
                        }
                    }else {
                        //同步微信企业
                        JSONObject synCorpResult =  synWxCorpService.synWxCorp(authJson);
                        boolean synCorpResultSuceess = synCorpResult.getBoolean("success");

                        if(!synCorpResultSuceess){
                            saveSynResult(  corpid,now ,"false",SynErrorMsg.REGISTERSERVICE_ERROR  );
                            errorMsg = SynErrorMsg.REGISTERSERVICE_ERROR.name();
                            success = false;
                        }else{
                            TenantParam tenantParam = (TenantParam) synCorpResult.get("tenantParam");
                            //判断passportID与租户之间是否建立关系
                            long xsyTenantid = tenantParam.getTenantId();
                            boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passportId,xsyTenantid);
                            if(!exitPassportIdAndTenantId){
                                commonService.joinPassportInTenant(xsyTenantid, passportId);
                            }
                            Map<String,Role> roleMap = this.getTenantRoleMap(tenantParam);
                            Map<String,Responsibility> responsibilityMap = this.getTenantResponsibilityMap(tenantParam);

                            //同步系统管理员
                            JSONObject synManagerResultJSON = synWxUser2XsyUserService.doSyncWXUser2Xsy(tenantParam,wxUser,corpid,roleMap,responsibilityMap,true,wxUser.getString("weixinid"),0,wxInfoType);
                            //同步系统管理员完毕
                            if(synManagerResultJSON.getBoolean("success")){

                                //同步其他非管理员用户
                                doSyncAfterManager( synManagerResultJSON.getLong("xsy_userId"),authJson,wxInfoType,tenantParam);
//                        保存同步结果
                                saveSynResult(  corpid,now ,"true",SynErrorMsg.OK  );
                                errorMsg = SynErrorMsg.OK.name();
                                success = true;
                            }else{
                                LOG.error(corp_name+"["+corpid+"]sync fail");
                                saveSynResult(  corpid,now ,"false",SynErrorMsg.SYN_SYSTEMMANAGER_ERROR  );
                                errorMsg = SynErrorMsg.SYN_SYSTEMMANAGER_ERROR.name();
                            }
                        }
                    }
                }

            } catch (ServiceException e) {
                e.printStackTrace();
                try {
                    saveSynResult(  corpid,now ,"false",SynErrorMsg.SERVICE_ERROR  );
                    errorMsg = SynErrorMsg.SERVICE_ERROR.name();
                } catch (ServiceException e1) {
                    e1.printStackTrace();
                }
                LOG.error("syn failed,error msg:"+e.getMessage());

            } catch (PaasException e) {
                e.printStackTrace();
                LOG.error("syn failed,error msg:"+e.getMessage());
                errorMsg = SynErrorMsg.SERVICE_ERROR.name();
            } catch (WXException e) {
                e.printStackTrace();
                LOG.error("syn failed,error msg:"+e.getMessage());
                errorMsg = SynErrorMsg.SERVICE_ERROR.name();
            } catch ( Exception e) {
                e.printStackTrace();
                LOG.error("syn failed,error msg:"+e.getMessage());
                errorMsg = SynErrorMsg.SERVICE_ERROR.name();
            }
        }


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",success);
        jsonObject.put("errorMsg",errorMsg);
        return jsonObject;
    }

    public void doSyncAfterManager( Long xsy_ManagerUserId,JSONObject authJson, WxInfoType wxInfoType, TenantParam tenantParam ) throws WXException, ServiceException, PaasException {
        JSONObject auth_info = authJson.getJSONObject("auth_info");
        String corp_access_token = authJson.getString("access_token");//授权方（企业）access_token
        JSONObject auth_corp_info = authJson.getJSONObject("auth_corp_info");//授权企业信息
        String corpid = auth_corp_info.getString("corpid");//授权方（企业）ID


        JSONArray agents = auth_info.getJSONArray("agent");
        JSONObject agent_0 = agents.getJSONObject(0);

        JSONObject privilege = agent_0.getJSONObject("privilege");

        JSONArray extra_users = privilege.getJSONArray("extra_user");
        JSONArray allow_users = privilege.getJSONArray("allow_user");
        JSONArray  extra_tags = privilege.getJSONArray("extra_tag");
        JSONArray allow_tags = privilege.getJSONArray("allow_tag");
        //开始同步其他非管理员数据
        commonSynHelper.doSync(xsy_ManagerUserId,tenantParam,corpid,corp_access_token,extra_users,allow_users,extra_tags,allow_tags,  wxInfoType);
    }


    public void saveSynResult(String  corpid,Date now,String result,SynErrorMsg errorMsg  ) throws ServiceException {
        String operatorMethod = "update";
        SynResult synResult =  synResultService.getByThirdCorpid(corpid, WXAppConstant.APP_ID, Platform.WEIXIN);
        if(synResult == null){
            synResult = new SynResult();
            synResult.setCreatedAt(now.getTime());
            operatorMethod = "add";
        }
        synResult.setUpdatedAt(now.getTime());
        synResult.setSuiteKey(WXAppConstant.APP_ID);
        synResult.setSynErrorMsg(errorMsg==null?null:errorMsg.toString());
        synResult.setSynResult(result);
        synResult.setThirdCorpid(corpid);
        synResult.setThirdSource(Platform.WEIXIN);
        if("add".equals(operatorMethod)){
            synResultService.save(synResult);
        }else {
            synResultService.update(synResult);
        }
    }

    /**
     * 工具方法，查询所有角色对应的map
     * @param tenantParam
     * @return
     * @throws PaasException
     */
    public  Map<String,Role> getTenantRoleMap( TenantParam tenantParam ) throws PaasException {
        Map<String,Role> rolMap = new HashMap<String,Role>();
        List<Role> roleList = roleService.getAllList(tenantParam);
        if(CollectionUtils.isNotEmpty(roleList)){
            for(com.rkhd.platform.auth.model.Role role : roleList){
                if(role.getRoleType() == ROLE_SYSTEM_MANAGER){
                    rolMap.put(ROLE_SYSTEM_MANAGER_KEY,role);
                }else if(role.getRoleType() == ROLE_NORMAL){
                    rolMap.put(ROLE_NORMAL_KEY,role);
                }
            }
        }else{
            LOG.error("nog get rolelist from tenantParam["+tenantParam.getTenantId()+"]");
        }
        return rolMap;
    }

    /**
     * 工具方法，查询所有功能角色对应的map
     * @param tenantParam
     * @return
     * @throws PaasException
     */
    public  Map<String,Responsibility> getTenantResponsibilityMap( TenantParam tenantParam ) throws PaasException {
        Map<String,Responsibility> responsibilityMap = new HashMap<String,Responsibility>();
        List<Responsibility> responsibilityList = responsibilityService.getAllResponsibility(tenantParam);
        if(CollectionUtils.isNotEmpty(responsibilityList)){
            for(Responsibility item : responsibilityList){
                if(item.getResponsibilityType() == RESPONSIBILITY_SYSTEM){
                    responsibilityMap.put(RESPONSIBILITY_SYSTEM_KEY,item);
                }else if(item.getResponsibilityType() == RESPONSIBILITY_NORMAL){
                    responsibilityMap.put(RESPONSIBILITY_NORMAL_KEY,item);
                }
            }
        }else{
            LOG.error("nog get responsibilityList from tenantParam["+tenantParam.getTenantId()+"]");
        }
        return responsibilityMap;
    }

    public static void main(String[] args){
        String view = "{\"name\":\"abc\"}";
        JSONObject json = JSON.parseObject(view);
        System.out.println(json.getString("name"));
    }

    public JSONObject getWxUserByEmailOrMobile(JSONObject authJson, String corp_access_token) throws WXException {
        //下面开始同步通讯录
        JSONObject departmentListJson =  wxApi.getDepartmentList(corp_access_token,null);//所有部门
        JSONArray allAuthPersons = new JSONArray();

        JSONObject auth_info = authJson.getJSONObject("auth_info");
        JSONArray agents = auth_info.getJSONArray("agent");
        JSONObject agent_0 = agents.getJSONObject(0);
        JSONObject privilege = agent_0.getJSONObject("privilege");
        JSONArray extra_users = privilege.getJSONArray("extra_user");
        JSONArray allow_users = privilege.getJSONArray("allow_user");
        JSONArray  extra_tags = privilege.getJSONArray("extra_tag");
        JSONArray allow_tags = privilege.getJSONArray("allow_tag");

        if(departmentListJson.getInteger("errcode").intValue() == 0) {
            JSONArray wxDepartmentList   = departmentListJson.getJSONArray("department");
            if(wxDepartmentList != null ){
                JSONObject wxdepartment = null;
                for(int i=0;i<wxDepartmentList.size();i++){
                    wxdepartment = wxDepartmentList.getJSONObject(i);
                    String partyId = wxdepartment.getString("id");
                    //同步当前部门下的员工
                    JSONObject userListJson = wxApi.getDepartment_user_list(corp_access_token,partyId,0,0+"");

                    if(userListJson.getIntValue("errcode") == 0) {
                        JSONArray userlist = userListJson.getJSONArray("userlist");
                        JSONObject wxUserItem = null;
                        for (int j = 0; j < userlist.size(); j++) {
                            wxUserItem = userlist.getJSONObject(j) ;

                            allAuthPersons.add(wxUserItem);
                        }
                    }


                }
            }
        }
        if(extra_users != null) {
            JSONObject wxUserItem = null;
            for(int i=0;i<extra_users.size();i++){
                String userid = extra_users.getString(i);
                wxUserItem =  wxApi.get_user(corp_access_token,userid);

                allAuthPersons.add(wxUserItem);
            }
        }
        if(allow_users != null) {
            JSONObject wxUserItem = null;
            for(int i=0;i<allow_users.size();i++){
                String userid = allow_users.getString(i);
                wxUserItem =  wxApi.get_user(corp_access_token,userid);

                allAuthPersons.add(wxUserItem);
            }
        }

        if(extra_tags != null){
            String wxTagId= null;
            JSONObject wxUserItem = null;
            for(int i=0;i<extra_tags.size();i++){
                wxTagId = extra_tags.getString(i);
                JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);

                if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                    JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                    if(userlist != null){
                        String wxUserId = null;
                        for(int j=0;j<userlist.size();j++){
                            wxUserId = userlist.getJSONObject(j).getString("userid");
                            wxUserItem =  wxApi.get_user(corp_access_token,wxUserId);

                            allAuthPersons.add(wxUserItem);
                        }
                    }
                }
            }
        }

        if(allow_tags != null){
            String wxTagId= null;
            JSONObject wxUserItem = null;
            for(int i=0;i<allow_tags.size();i++){
                wxTagId = allow_tags.getString(i);
                JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);

                if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                    JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                    if(userlist != null){
                        String wxUserId = null;
                        for(int j=0;j<userlist.size();j++){
                            wxUserId = userlist.getJSONObject(j).getString("userid");
                            wxUserItem =  wxApi.get_user(corp_access_token,wxUserId);

                            allAuthPersons.add(wxUserItem);
                        }
                    }
                }
            }
        }
        if(allAuthPersons.size() == 0 ){
           return null;
        }else{
            JSONObject auth_user_info = authJson.getJSONObject("auth_user_info");//授权用户
            String manager_email = auth_user_info.getString("email");
            String manager_mobile = auth_user_info.getString("mobile");
            for (int i=0;i<allAuthPersons.size();i++){
                if(StringUtils.isNotBlank(manager_mobile)){
                    if(manager_mobile.equals(allAuthPersons.getJSONObject(i).getString("mobile"))  ){//手机号相同则认为是管理员
                        return allAuthPersons.getJSONObject(i);
                    }
                }
                if(StringUtils.isNotBlank(manager_email)){
                    if(manager_email.equals(allAuthPersons.getJSONObject(i).getString("email"))  ){//邮箱相同则认为是管理员
                        return allAuthPersons.getJSONObject(i);
                    }
                }
            }
        }
        return null;
    }

}
