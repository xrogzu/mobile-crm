package com.rkhd.ienterprise.apps.ingage.wx.helper;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxUtils;
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.base.license.service.UserLicenseService;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.profile.model.PersonalProfile;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.ienterprise.util.PinyinUtil;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 同步微信用户到销售易用户表
 */
@Component
public class SynWxUser2XsyUserService {

    private static final Logger LOG = LoggerFactory.getLogger(SynWxUser2XsyUserService.class);

    @Autowired
    private SynPassportService synPassportService;

    @Autowired
    private DepartService departService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;
    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonalProfileService personalProfileService;
    @Autowired
    private OauthTokenService oauthTokenService;
    @Autowired
    private UserLicenseService userLicenseService;


        /**
         * 参考：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98
         * 参数为：获取部门成员(详情)或获取成员 接口的成员详情
         * @param tenantParam
         * @param wxUser
         * @param managerWxuserID 管理人员
         *
         */
    public   JSONObject doSyncWXUser2Xsy (TenantParam tenantParam , JSONObject wxUser, String thirdCorpid, Map<String,Role> roleMap,
                                          Map<String,Responsibility> responsibilityMap, boolean isSetManager, String managerWxuserID, long xsyAdminUserId , WxInfoType wxInfoType) throws ServiceException, PaasException {
        JSONObject  jsonObject = new JSONObject();
        boolean success = false;
        jsonObject.put("isSettingManager",isSetManager);
        //1=已关注，2=已禁用，4=未关注
        int status = wxUser.getIntValue("status");
        String  weixinid = wxUser.getString("weixinid");
        String  email = wxUser.getString("email");
        String  mobile = wxUser.getString("mobile");
        String  avatar = wxUser.getString("avatar");
        long  passportId = 0;
        long xsy_userId = 0L;
        String  wxUserid =  wxUser.getString("userid");//账号
        //只有状态为1,wxUserid不为空 的才进行逻辑处理
        if( StringUtils.isNotBlank(wxUserid)){

            JSONObject executeResult =  synPassportService.doSynPassport(wxUser);

            if(!executeResult.containsKey("passportId") || false == executeResult.getBoolean("doExecute")){
                LOG.error("passport do error,wxUser="+wxUser.toJSONString());
                jsonObject.put("success",false);
                jsonObject.put("errorMsg","syn passport error");
                LOG.error("syn passport error");
            }else {
//                同步passport与租户关系
                passportId = executeResult.getLongValue("passportId");
                long xsyTenantid = tenantParam.getTenantId();
                boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passportId,xsyTenantid);
                if(!exitPassportIdAndTenantId){
                    commonService.joinPassportInTenant(xsyTenantid, passportId);
                }
//同步用户信息
                JSONArray departmentArray = wxUser.getJSONArray("department");

                String  uname = wxUser.getString("name");
               String  position = wxUser.getString("position");

                JSONObject jsonUtilInfo =  WxUtils.wxUserPassportLoginInfo(mobile,email,weixinid);
                String loginName = jsonUtilInfo.getString("loginName");
                long mainXsyDepartmetId = 0;
                List<Long > relatedIds = new ArrayList<Long >();
                if(isSetManager){
                    Depart xsydepart = departService.getTop(tenantParam);
                    mainXsyDepartmetId = xsydepart.getId();
                }else {
                    if(departmentArray != null && departmentArray.size() > 0){
                        int wxDepartmetnId = 0;
                        boolean haveMainDepartment = false;
                        for(int i = 0;i<departmentArray.size();i++){
                            wxDepartmetnId = departmentArray.getInteger(i);
                            //判断部门映射关系是否存在，如果不存在则默认0
                            RelThirdDepXsyDepartment relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(
                                    thirdCorpid,wxDepartmetnId+"",Platform.WEIXIN, WXAppConstant.APP_ID);
                            if(relThirdDepXsyDepartment == null){
                                continue;
                            }else {
                                if( !haveMainDepartment){
                                    mainXsyDepartmetId = relThirdDepXsyDepartment.getXsyDepartmentId();
                                    haveMainDepartment = true;
                                }else {
                                    relatedIds.add(relThirdDepXsyDepartment.getXsyDepartmentId());
                                }
                            }
                        }


                    }else {
                        Depart xsydepart = departService.getTop(tenantParam);
                        mainXsyDepartmetId = xsydepart.getId();
                    }
                }
                if(mainXsyDepartmetId == 0){
//                    兜底使用
                    Depart xsydepart = departService.getTop(tenantParam);
                    mainXsyDepartmetId = xsydepart.getId();
                }

                com.rkhd.ienterprise.base.user.model.User checkUser = null ;
                RelThirdUserXsyUser relThirdUserXsyUser =   relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdUserId(
                        thirdCorpid,
                        wxUserid,Platform.WEIXIN, WXAppConstant.APP_ID);

                if (relThirdUserXsyUser != null){
                    //有用户修改名字的现象存在，所以在这里避免修改名字重新增加用户
                    xsy_userId =  relThirdUserXsyUser.getXsyUserId();
                    checkUser = userService.get(xsy_userId,tenantParam);
                }
                // 避免用户建立成功，但是映射关系建立失败的现象
                if( checkUser == null) {
                    checkUser =  userService.getByPassportIdAndTenantId(passportId, tenantParam);
                }
               // LOG.info(""+JSON.toJSONString(roleMap));
                Long  roleId =  0L;
                if(roleMap.get(AuthCompositionHelper.ROLE_NORMAL_KEY) == null || responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY) == null){
                    success = false;
                    LOG.error("AuthCompositionHelper.ROLE_NORMAL_KEY==null?"+(AuthCompositionHelper.ROLE_NORMAL_KEY==null?true:false));
                    LOG.error("AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY==null?"+(AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY==null?true:false));
                }else{
                    roleId = roleMap.get(AuthCompositionHelper.ROLE_NORMAL_KEY).getId();
                    Long  responsibilityId = responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY).getId();


                    if(isSetManager ||(managerWxuserID != null &&  managerWxuserID.equals(wxUserid)) || (xsyAdminUserId != 0 && checkUser != null && checkUser.getId() == xsyAdminUserId) ){
                        roleId =  roleMap.get(AuthCompositionHelper.ROLE_SYSTEM_MANAGER_KEY).getId();
                        responsibilityId = responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_SYSTEM_KEY).getId();
                    }
                    if(wxInfoType == WxInfoType.CREATE_AUTH && isSetManager){
                        //删除组内所有人的licenese
                        List<Long> userIds = userService.getAllUserIds(tenantParam);
                        int length = userIds.size();
                        for(int i=0;i<length;i++){
                            userLicenseService.deleteByUserId(userIds.get(i),tenantParam);
                        }
                    }

                    if( checkUser == null || checkUser.getTenantId().longValue() != tenantParam.getTenantId().longValue()){
                        if(isSetManager){
                            //新建用户
                            xsy_userId = commonService.saveUser(tenantParam,passportId,uname,mainXsyDepartmetId, User.Status.ACTIVE);
                            //设置用户license,只给管理员设值license
                            commonService.initUserLicense(xsy_userId,tenantParam);
                        }else {
                            xsy_userId = commonService.saveUser(tenantParam,passportId,uname,mainXsyDepartmetId, User.Status.ACTIVE);
                        }

                        //设置用户角色
                        commonService.saveUserRole(roleId,xsy_userId,tenantParam);
//                设置用户职能
                        commonService.saveResponsibility(responsibilityId,xsy_userId,tenantParam);
//              设置用户详细属性
                        PersonalProfile profile = personalProfileService.getByUserId(xsy_userId,tenantParam);
                        if(profile != null){
                            personalProfileService.delete(profile.getId(),tenantParam);
                        }
                        commonService.saveProfile( tenantParam,  xsy_userId, position, mainXsyDepartmetId, mobile, email);
                        //设置加入部门
                        commonService.joinDepart(mainXsyDepartmetId,xsy_userId,tenantParam);
                        //设置用户多维度
                        commonService.saveUserDimension(xsy_userId,  tenantParam,  mainXsyDepartmetId,relatedIds);
                        //初始化用户视图
                        commonService.saveSystemInitView(xsy_userId);
                        //激活passprot
                        commonService.activatePassport(passportId,loginName);
                        //
                        commonService.clearCurrentCaptcha(loginName);

                    }else {
                        if(isSetManager){
                            //初始化管理员的licenese
                            commonService.initUserLicense(xsy_userId,tenantParam);
                        }
                        //更新用户
                        xsy_userId = checkUser.getId();
                        checkUser = userService.get(xsy_userId,tenantParam);
                        checkUser.setName(uname);
                        checkUser.setPinyin(PinyinUtil.cnToPinyin(checkUser.getName()));
                        checkUser.setStatus(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue());
                        checkUser.setDepartId(mainXsyDepartmetId);
                        if(passportId ==checkUser.getPassportId() ){
                            //此处必须passportId相同的情况下才可以判断，否则就会出现user表里TENANT_ID,passportId相同的重复数据。
                            checkUser.setPassportId(passportId);
                        }


                        boolean updateUerResult = userService.update(checkUser,tenantParam);
                        if(updateUerResult){
                            PersonalProfile dbpersonalProfile = personalProfileService.getByUserId(xsy_userId,tenantParam);
                            if(dbpersonalProfile == null){
                                commonService.saveProfile(tenantParam,  xsy_userId, position, mainXsyDepartmetId, mobile, email);
                            }else{
                                dbpersonalProfile.setTenantId(tenantParam.getTenantId());
                                dbpersonalProfile.setPositionName(position);
                                dbpersonalProfile.setDepartId(mainXsyDepartmetId);
                                dbpersonalProfile.setPhone(mobile);
                                dbpersonalProfile.setPersonalEmail(email);
                                dbpersonalProfile.setDelFlg(PersonalProfile.NON_DELETED);

                                personalProfileService.update(dbpersonalProfile,tenantParam);
                            }

                            // 变更时如果是管理员则设置用户角色，其余角色不变更
                            if( roleId ==  roleMap.get(AuthCompositionHelper.ROLE_SYSTEM_MANAGER_KEY).getId()){
                                commonService.saveUserRole(roleId,xsy_userId,tenantParam);
                            }

                            // 变更时如果是管理员则设置用户职能，其余不变更用户职能
                            if( responsibilityId == responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_SYSTEM_KEY).getId()){
                                commonService.saveResponsibility(responsibilityId,xsy_userId,tenantParam);
                            }
                            //设置用户多维度
                            commonService.saveUserDimension(xsy_userId,  tenantParam,  mainXsyDepartmetId,relatedIds);

                            //设置加入部门
                            commonService.joinDepart(mainXsyDepartmetId,xsy_userId,tenantParam);
                        }else{
                            LOG.error(" update user error,checkUser="+JSON.toJSONString(checkUser)+";tenantParam="+JSON.toJSONString(tenantParam));
                        }

                    }
                    LOG.info("checkUser="+JSON.toJSONString(checkUser));
                    LOG.info("wxUser="+JSON.toJSONString(wxUser));

//创建映射关系
                    String operator = "add";
                    if(relThirdUserXsyUser == null){
                        relThirdUserXsyUser = new RelThirdUserXsyUser();
                        operator = "add";
                        //设置passportID只能在增加的时候赋值，修改时禁止修改passport
                        relThirdUserXsyUser.setXsyPassportId(passportId);
                    }else {
                        operator = "update";
                    }


                    relThirdUserXsyUser.setThirdPlatformUserId(StringUtils.isEmpty(weixinid)?"empty":weixinid);
                    relThirdUserXsyUser.setXsyUserId(xsy_userId);
                    relThirdUserXsyUser.setThirdUserId(wxUserid);
                    relThirdUserXsyUser.setThirdCorpid( thirdCorpid);
                    relThirdUserXsyUser.setThirdSource(Platform.WEIXIN);
                    relThirdUserXsyUser.setXsyTenantid(xsyTenantid);
                    relThirdUserXsyUser.setAvatar(avatar);
                    relThirdUserXsyUser.setSuiteKey(WXAppConstant.APP_ID);
                    if("add".equals(operator)){
                        relThirdUserXsyUserService.save(relThirdUserXsyUser);
                        LOG.info("Corpid["+ thirdCorpid+"] create relThirdUserXsyUser={}", JSON.toJSONString(relThirdUserXsyUser));
                    }else {
                        relThirdUserXsyUserService.update(relThirdUserXsyUser);
                        LOG.info("Corpid["+ thirdCorpid+"] update relThirdUserXsyUser={}", JSON.toJSONString(relThirdUserXsyUser));
                    }
                    if(isSetManager){
                        //获取销售易的xsy_token
                        OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_userId,tenantParam);
                        String xsyBasicAccessToken  = oauthToken.getAccessToken();
                        jsonObject.put("xsy_access_token",xsyBasicAccessToken);
                        jsonObject.put("xsy_manager_id",xsy_userId);
                        // jsonObject.put("wxUser",wxUser);
                    }
                    success = true;
                }


            }
        }
        jsonObject.put("success",success);
        jsonObject.put("xsy_userId",xsy_userId);
        jsonObject.put("wxUserid",wxUserid);
        return jsonObject;
    }


    /**
     * 针对没有userId的用户进行同步
     * @param tenantParam
     * @param thirdCorpid
     * @param roleMap
     * @param responsibilityMap
     * @param isSetManager
     * @param wxInfoType
     * @return
     * @throws ServiceException
     * @throws PaasException
     */
    public   JSONObject doSyncWXUser2Xsy4NoUserId (TenantParam tenantParam ,  String thirdCorpid, Map<String,Role> roleMap,
                                          Map<String,Responsibility> responsibilityMap, boolean isSetManager, WxInfoType wxInfoType, String manager_email, String manager_mobile) throws ServiceException, PaasException {

        JSONObject  jsonObject = new JSONObject();
        boolean success = false;
        jsonObject.put("isSettingManager",isSetManager);
        //1=已关注，2=已禁用，4=未关注
        long  passportId = 0;
        long xsy_userId = 0L;
        JSONObject   executeResult =   synPassportService.doSynPassport4Manager(null  ,manager_email, manager_mobile);

            if(!executeResult.containsKey("passportId") ){
                LOG.error("syn passport do error,manager_email="+manager_email+";manager_mobile="+manager_mobile);
                jsonObject.put("success",false);
                jsonObject.put("errorMsg","syn passport error");
            }else {
//                同步passport与租户关系
                passportId = executeResult.getLongValue("passportId");
                long xsyTenantid = tenantParam.getTenantId();
                boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passportId,xsyTenantid);
                if(!exitPassportIdAndTenantId){
                    commonService.joinPassportInTenant(xsyTenantid, passportId);
                }
//同步用户信息
                JSONArray departmentArray = null;
                String  uname = StringUtils.isNotBlank(manager_email)?manager_email:manager_mobile;
                JSONObject jsonUtilInfo =  WxUtils.wxUserPassportLoginInfo(manager_mobile,manager_email,null);
                String loginName = jsonUtilInfo.getString("loginName");
                long mainXsyDepartmetId = 0;
                List<Long > relatedIds = new ArrayList<Long >();

                Depart xsydepart = departService.getTop(tenantParam);
                mainXsyDepartmetId = xsydepart.getId();


                User checkUser = null ;
                RelThirdUserXsyUser relThirdUserXsyUser = null;
                if(StringUtils.isNotBlank(manager_email)){
                    relThirdUserXsyUser = relThirdUserXsyUserService.getByThirdUserEmailAndCorpid(manager_email,thirdCorpid,Platform.WEIXIN, WXAppConstant.APP_ID);
                }else {
                    relThirdUserXsyUser = relThirdUserXsyUserService.getByThirdUserPhoneAndCorpid(manager_mobile,thirdCorpid,Platform.WEIXIN, WXAppConstant.APP_ID);
                }

                if (relThirdUserXsyUser != null){
                    //有用户修改名字的现象存在，所以在这里避免修改名字重新增加用户
                    xsy_userId =  relThirdUserXsyUser.getXsyUserId();
                    checkUser = userService.get(xsy_userId,tenantParam);
                }
                // 避免用户建立成功，但是映射关系建立失败的现象
                if( checkUser == null) {
                    checkUser =  userService.getByPassportIdAndTenantId(passportId, tenantParam);
                }
                // LOG.info(""+JSON.toJSONString(roleMap));
                Long  roleId =  0L;
                if(roleMap.get(AuthCompositionHelper.ROLE_NORMAL_KEY) == null || responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY) == null){
                    success = false;
                    LOG.error("AuthCompositionHelper.ROLE_NORMAL_KEY==null?"+(AuthCompositionHelper.ROLE_NORMAL_KEY==null?true:false));
                    LOG.error("AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY==null?"+(AuthCompositionHelper.RESPONSIBILITY_NORMAL_KEY==null?true:false));
                }else{
                    roleId =  roleMap.get(AuthCompositionHelper.ROLE_SYSTEM_MANAGER_KEY).getId();
                    Long    responsibilityId = responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_SYSTEM_KEY).getId();

                    if(wxInfoType == WxInfoType.CREATE_AUTH ){
                        //删除组内所有人的licenese
                        List<Long> userIds = userService.getAllUserIds(tenantParam);
                        int length = userIds.size();
                        for(int i=0;i<length;i++){
                            userLicenseService.deleteByUserId(userIds.get(i),tenantParam);
                        }
                    }

                    if( checkUser == null || checkUser.getTenantId().longValue() != tenantParam.getTenantId().longValue()){
                        xsy_userId = commonService.saveUser(tenantParam,passportId,uname,mainXsyDepartmetId, User.Status.ACTIVE);
                        //设置用户license,只给管理员设值license
                        commonService.initUserLicense(xsy_userId,tenantParam);

                        //设置用户角色
                        commonService.saveUserRole(roleId,xsy_userId,tenantParam);
//                设置用户职能
                        commonService.saveResponsibility(responsibilityId,xsy_userId,tenantParam);
//              设置用户详细属性
                        PersonalProfile profile = personalProfileService.getByUserId(xsy_userId,tenantParam);
                        if(profile != null){
                            personalProfileService.delete(profile.getId(),tenantParam);
                        }
                        commonService.saveProfile( tenantParam,  xsy_userId, null, mainXsyDepartmetId, manager_mobile, manager_email);
                        //设置加入部门
                        commonService.joinDepart(mainXsyDepartmetId,xsy_userId,tenantParam);
                        //设置用户多维度
                        commonService.saveUserDimension(xsy_userId,  tenantParam,  mainXsyDepartmetId,relatedIds);
                        //初始化用户视图
                        commonService.saveSystemInitView(xsy_userId);
                        //激活passprot
                        commonService.activatePassport(passportId,loginName);
                        //
                        commonService.clearCurrentCaptcha(loginName);

                    }else {
                        if(isSetManager){
                            //初始化管理员的licenese
                            commonService.initUserLicense(xsy_userId,tenantParam);
                        }
                        //更新用户
                        xsy_userId = checkUser.getId();
                        checkUser = userService.get(xsy_userId,tenantParam);
                        checkUser.setName(uname);
                        checkUser.setPinyin(PinyinUtil.cnToPinyin(checkUser.getName()));
                        checkUser.setStatus(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue());
                        checkUser.setDepartId(mainXsyDepartmetId);
                        if(passportId ==checkUser.getPassportId() ){
                            //此处必须passportId相同的情况下才可以判断，否则就会出现user表里TENANT_ID,passportId相同的重复数据。
                            checkUser.setPassportId(passportId);
                        }
                        boolean updateUerResult = userService.update(checkUser,tenantParam);
                        if(updateUerResult){
                            PersonalProfile dbpersonalProfile = personalProfileService.getByUserId(xsy_userId,tenantParam);
                            if(dbpersonalProfile == null){
                                commonService.saveProfile(tenantParam,  xsy_userId,  null, mainXsyDepartmetId, manager_mobile, manager_email);
                            }else{
                                dbpersonalProfile.setTenantId(tenantParam.getTenantId());
                                dbpersonalProfile.setPositionName(null);
                                dbpersonalProfile.setDepartId(mainXsyDepartmetId);
                                dbpersonalProfile.setPhone(manager_mobile);
                                dbpersonalProfile.setPersonalEmail(manager_email);
                                dbpersonalProfile.setDelFlg(PersonalProfile.NON_DELETED);
                                personalProfileService.update(dbpersonalProfile,tenantParam);
                            }

                            // 变更时如果是管理员则设置用户角色，其余角色不变更
                            if( roleId ==  roleMap.get(AuthCompositionHelper.ROLE_SYSTEM_MANAGER_KEY).getId()){
                                commonService.saveUserRole(roleId,xsy_userId,tenantParam);
                            }

                            // 变更时如果是管理员则设置用户职能，其余不变更用户职能
                            if( responsibilityId == responsibilityMap.get(AuthCompositionHelper.RESPONSIBILITY_SYSTEM_KEY).getId()){
                                commonService.saveResponsibility(responsibilityId,xsy_userId,tenantParam);
                            }
                            //设置用户多维度
                            commonService.saveUserDimension(xsy_userId,  tenantParam,  mainXsyDepartmetId,relatedIds);

                            //设置加入部门
                            commonService.joinDepart(mainXsyDepartmetId,xsy_userId,tenantParam);
                        }else{
                            LOG.error(" update user error,checkUser="+JSON.toJSONString(checkUser)+";tenantParam="+JSON.toJSONString(tenantParam));
                        }

                    }
                    LOG.info("checkUser="+JSON.toJSONString(checkUser));

//创建映射关系
                    String operator = "add";
                    if(relThirdUserXsyUser == null){
                        relThirdUserXsyUser = new RelThirdUserXsyUser();
                        operator = "add";
                        //设置passportID只能在增加的时候赋值，修改时禁止修改passport
                        relThirdUserXsyUser.setXsyPassportId(passportId);
                    }else {
                        operator = "update";
                    }


                    relThirdUserXsyUser.setThirdPlatformUserId("empty");
                    relThirdUserXsyUser.setXsyUserId(xsy_userId);
                    relThirdUserXsyUser.setThirdUserEmail(manager_email);
                    relThirdUserXsyUser.setThirdUserPhone(manager_mobile);
//                    relThirdUserXsyUser.setThirdUserId(null);
                    relThirdUserXsyUser.setThirdCorpid( thirdCorpid);
                    relThirdUserXsyUser.setThirdSource(Platform.WEIXIN);
                    relThirdUserXsyUser.setXsyTenantid(xsyTenantid);
//                    relThirdUserXsyUser.setAvatar(null);
                    relThirdUserXsyUser.setSuiteKey(WXAppConstant.APP_ID);
                    if("add".equals(operator)){
                        relThirdUserXsyUserService.save(relThirdUserXsyUser);
                        LOG.info("Corpid["+ thirdCorpid+"] create relThirdUserXsyUser={}", JSON.toJSONString(relThirdUserXsyUser));
                    }else {
                        relThirdUserXsyUserService.update(relThirdUserXsyUser);
                        LOG.info("Corpid["+ thirdCorpid+"] update relThirdUserXsyUser={}", JSON.toJSONString(relThirdUserXsyUser));
                    }
                    if(isSetManager){
                        //获取销售易的xsy_token
                        OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_userId,tenantParam);
                        String xsyBasicAccessToken  = oauthToken.getAccessToken();
                        jsonObject.put("xsy_access_token",xsyBasicAccessToken);
                        jsonObject.put("xsy_manager_id",xsy_userId);
                        // jsonObject.put("wxUser",wxUser);
                    }
                    success = true;
                }
            }

        jsonObject.put("success",success);
        jsonObject.put("xsy_userId",xsy_userId);
        return jsonObject;
    }


}
