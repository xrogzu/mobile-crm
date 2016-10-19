package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.auth.AuthHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service.ServiceHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.DDUtils;
import com.rkhd.ienterprise.base.multitenant.model.Tenant;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.passport.model.Passport;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.base.profile.model.PersonalProfile;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdPassportXsyPassportId;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.*;
import com.rkhd.ienterprise.util.PinyinUtil;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.service.ResponsibilityService;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/1/16.
 */
public class SyncAddressBookeThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(SyncAddressBookeThread.class);

    public static final int ROLE_SYSTEM_MANAGER = 1;//系统管理员
    public static final int ROLE_NORMAL = 2;//普通用户
    public static final int ROLE_MANAGER = 3;//经理

    public static final String ROLE_SYSTEM_MANAGER_KEY = "ROLE_SYSTEM_MANAGER";//系统管理员
    public static final String ROLE_NORMAL_KEY = "ROLE_NORMAL";//普通用户
    public static final String ROLE_MANAGER_KEY = "ROLE_MANAGER";//经理


    public static final int RESPONSIBILITY_SYSTEM = 1;//系统管理员职能
    public static final int RESPONSIBILITY_NORMAL = 2;//普通用户职能
    public static final int RESPONSIBILITY_MANAGER  = 3;//经理职能

    public static final String RESPONSIBILITY_SYSTEM_KEY = "RESPONSIBILITY_SYSTEM";//系统管理员职能
    public static final String RESPONSIBILITY_NORMAL_KEY = "RESPONSIBILITY_NORMAL";//普通用户职能
    public static final String RESPONSIBILITY_MANAGER_KEY  = "RESPONSIBILITY_MANAGER";//经理职能

    RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    private DepartmentService departmentService;

    private PassportService passportService;

    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;

    private CommonService commonService;

    private TenantService tenantService;

    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    private UserService userService;


    private PersonalProfileService personalProfileService;

    private DingService dingService;

    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    private OauthTokenService oauthTokenService;

    private ResponsibilityService responsibilityService;

    private RoleService roleService;

    //第三方授权企业信息
    private JSONObject auth_corp_info;

    //钉钉平台第三方企业的accessToken
    private String thirdCorpidAccessToken;

    /**
     * 永久授权码
     */
    private String permanent_code;

    private   JSONObject  authInfo;

    private String thirdCorpid;

    public SyncAddressBookeThread(RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService
            , RelThirdCorpXsyTenantService relThirdCorpXsyTenantService, DepartmentService departmentService,
                                  PassportService passportService, RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService,
                                  CommonService commonService, TenantService tenantService, RelThirdUserXsyUserService relThirdUserXsyUserService,
                                  UserService userService, PersonalProfileService personalProfileService,
                                  DingService dDingService,  OauthTokenService oauthTokenService,
                                  ResponsibilityService responsibilityService, RoleService roleService,
                                  JSONObject auth_corp_info,String thirdCorpidAccessToken,String permanent_code,JSONObject  authInfo){
        super();

        this.relThirdDepXsyDepartmentService = relThirdDepXsyDepartmentService;
        this.departmentService = departmentService;
        this.passportService = passportService;
        this.relThirdPassportXsyPassportIdService = relThirdPassportXsyPassportIdService;
        this.commonService = commonService;
        this.tenantService = tenantService;
        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
        this.userService = userService;
        this.personalProfileService = personalProfileService;
        this.dingService = dDingService;
        this.oauthTokenService = oauthTokenService;
        this.responsibilityService = responsibilityService;
        this.roleService = roleService;
        this.relThirdCorpXsyTenantService = relThirdCorpXsyTenantService;
        this.auth_corp_info = auth_corp_info;
        this.thirdCorpidAccessToken = thirdCorpidAccessToken;
        this.permanent_code = permanent_code;
        this.authInfo = authInfo;
    }
    //同步钉钉通讯录
    public void run() {
        long  synBeginTime = System.currentTimeMillis();
        boolean haveError = false;

        /**
         * 第三方企业ID
         */
        String thirdCorpid  = "";
        long xsyTenantid = 0L;
        Long  xsy_userId = 0L;
        String ding_userId = null;

        String manager_Dingid = null;
        /**
         * 销售易的basicAccessToken undo
         */
        String xsyBasicAccessToken = "";
        Map<String,com.rkhd.platform.auth.model.Role> rolMap = new HashMap<String,com.rkhd.platform.auth.model.Role>();
        Map<String,Responsibility> responsibilityMap = new HashMap<String,Responsibility>();
        long xsyMangerId = 0;
        try {
            thirdCorpid = auth_corp_info.getString("corpid");// 授权方企业id
            this.thirdCorpid = thirdCorpid;
            String corpName = auth_corp_info.getString("corp_name");

//        LOG.info("授权方企业id:"+corpId+";授权方企业名称:"+corpName+";永久授权码:"+permanent_code);
//        //获取企业授权的授权数据
            LOG.info("Corpid["+thirdCorpid+"] Corp data is ："+ JSON.toJSONString(authInfo));
            JSONObject authInfoJson = authInfo.getJSONObject("auth_user_info");
            ding_userId = authInfoJson.getString("userId");

//        //获取当前管理员信息
            com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUserDetail = null;
            String title  =  "";
            String contact =  "";

            if(StringUtils.isBlank(ding_userId)){
                LOG.error("Corpid["+thirdCorpid+"] no get ding admin user`id by ding_userId:"+ding_userId);

            }else{
                dingUserDetail =   UserHelper.getUser(thirdCorpidAccessToken, ding_userId);
                LOG.info("Corpid["+thirdCorpid+"] ding manager info is：{}",JSON.toJSONString(dingUserDetail));
                title = dingUserDetail.getPosition();
                contact = dingUserDetail.getMobile();
        //        /**
        //         * 激活授权套件
        //         */
                    ServiceHelper.getActivateSuite(dingService.getSuiteAccessToken(), Env.SUITE_KEY, thirdCorpid, permanent_code);

        //        查询钉钉用户所属企业与钉钉的对应关系
                    RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(thirdCorpid, Platform.dingding,Env.SUITE_KEY);
        //        映射关系不存在
                    TenantParam tenantParam = null;
                    if(relThirdCorpXsyTenant == null){
                        //  新建租户，并初始化信息
                        Tenant tenant = commonService.saveTenant( corpName );
                        xsyTenantid  = tenant.getId();
                        LOG.info("Corpid["+thirdCorpid+"]　tenant　have been created,tenantId={},companyName={}",xsyTenantid,corpName );
        //           不要更改以下代码顺序
                        tenantParam = new TenantParam(xsyTenantid);
                        tenantParam.setType(tenant.getType());
        //                初始化租户信息
                        commonService.initTenantData(tenantParam);
        //            建立租户级别映射关系
                        this.createAndSaveRelThirdCorpXsyTenant(thirdCorpid,xsyTenantid);

                    }else{
                        xsyTenantid = relThirdCorpXsyTenant.getXsyTenantid();
                        tenantParam = new TenantParam(xsyTenantid);
                        //修改lincenses的截止日期
                        commonService.customSetLicense(tenantParam,false);
                    }
                    //保存钉钉企业授权信息
                    String jsticket = AuthHelper.getJsapiTicket(thirdCorpidAccessToken);
                    /**
                     * 保存钉钉用户的token等信息
                     */
                    commonService.addOrUpdateRelThirdToken(thirdCorpid,thirdCorpidAccessToken,jsticket,permanent_code,Platform.dingding);
                    LOG.info("Corpid["+thirdCorpid+"] save jsticket result success");

                    String loginName = DDUtils.getLoginNameByDingDingId(dingUserDetail.getDingId())  ;
                    String uname =     dingUserDetail.getName();
                    LOG.info("Corpid["+thirdCorpid+"] ding manager info is：{}",JSON.toJSONString(dingUserDetail));
                    manager_Dingid = dingUserDetail.getDingId();

                    long passportId = 0L;
                    RelThirdPassportXsyPassportId relThirdPassportXsyPassportId = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(manager_Dingid,Platform.dingding,Env.SUITE_KEY);
                    if(relThirdPassportXsyPassportId == null){
                        Passport  passport =  commonService._doCreateNewPassport(loginName);
                        passportId = passport.getId();
        //            创建第三方个人ID与销售易用户的id的对应关系
                        commonService.createAndSaveRelThirdPassportXsyPassportId(dingUserDetail.getDingId(),passportId);
                    }else{
                        passportId = relThirdPassportXsyPassportId.getPassportId();
                    }


        //            所有角色
                    List<com.rkhd.platform.auth.model.Role> roleList = roleService.getAllList(tenantParam);
        //            所有职能
                    List<Responsibility> responsibilityList = responsibilityService.getAllResponsibility(tenantParam);

                    if(CollectionUtils.isNotEmpty(roleList)){
                        for(com.rkhd.platform.auth.model.Role role : roleList){
                            if(role.getRoleType() == ROLE_SYSTEM_MANAGER){
                                rolMap.put(ROLE_SYSTEM_MANAGER_KEY,role);
                            }else if(role.getRoleType() == ROLE_NORMAL){
                                rolMap.put(ROLE_NORMAL_KEY,role);
                            }else if(role.getRoleType() == ROLE_MANAGER){
                                rolMap.put(ROLE_MANAGER_KEY,role);
                            }
                        }
                    }
                    if(CollectionUtils.isNotEmpty(responsibilityList)){
                        for(Responsibility item : responsibilityList){
                            if(item.getResponsibilityType() == RESPONSIBILITY_SYSTEM){
                                responsibilityMap.put(RESPONSIBILITY_SYSTEM_KEY,item);
                            }else if(item.getResponsibilityType() == RESPONSIBILITY_NORMAL){
                                responsibilityMap.put(RESPONSIBILITY_NORMAL_KEY,item);
                            }else if(item.getResponsibilityType()  == RESPONSIBILITY_MANAGER){
                                responsibilityMap.put(RESPONSIBILITY_MANAGER_KEY,item);
                            }
                        }
                    }
        //            LOG.info("roleList={}",JSON.toJSONString(roleList));
        //            LOG.info("responsibilityList={}",JSON.toJSONString(responsibilityList));

                    boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passportId,xsyTenantid);
                    if(!exitPassportIdAndTenantId){
                        commonService.joinPassportInTenant(xsyTenantid, passportId);
                    }
                    User checkUser = null;
                    //             判断用户已经建立了关系
                    RelThirdUserXsyUser relThirdUserXsyUser =   relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdPlatformUserIdThirdUserId(
                            thirdCorpid,manager_Dingid,
                            dingUserDetail.getUserid(),Platform.dingding,Env.SUITE_KEY);

                    if (relThirdUserXsyUser != null){
                        //有用户修改名字的现象存在，所以在这里避免修改名字重新增加用户
                        xsy_userId =  relThirdUserXsyUser.getXsyUserId();
                        checkUser = userService.get(xsy_userId,tenantParam);
                    }

                    // 避免用户建立成功，但是映射关系建立失败的现象
                    if( checkUser == null) {
                        checkUser =  userService.getByPassportIdAndTenantId(passportId, tenantParam);
                    }
                User.Status userStatus = User.Status.ACTIVE;

                    if( checkUser == null){
                        xsy_userId = commonService.saveUser(tenantParam, passportId,uname,0,userStatus);
                        commonService.initUserLicense(xsy_userId,tenantParam);
                    }else{
                        xsy_userId = checkUser.getId();
                    }
                    xsyMangerId = xsy_userId;

                    /**
                     * 设置用户系统管理员角色
                     */
                    commonService.saveUserRole(rolMap.get(ROLE_SYSTEM_MANAGER_KEY).getId(),xsy_userId,tenantParam);
                    /**
                     * 设置系统管理员职能
                     */
                    commonService.saveResponsibility(responsibilityMap.get(RESPONSIBILITY_SYSTEM_KEY).getId(),xsy_userId,tenantParam);
                    if( checkUser == null){
                        commonService.saveProfile(xsyTenantid, xsy_userId,title,loginName,contact,tenantParam,false);

                        commonService.saveUserDimension(xsy_userId,tenantParam,0,null);
                        commonService.saveSystemInitView(xsy_userId);
                        //激活passport
                        commonService.activatePassport(passportId,loginName);
                        commonService.clearCurrentCaptcha(loginName);

                    }else{
                        // 此处应该使用修改用户详细信息
                        checkUser = userService.get(xsy_userId,tenantParam);
                        checkUser.setName(uname);
                        checkUser.setPinyin(PinyinUtil.cnToPinyin(checkUser.getName()));
                        checkUser.setStatus(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue());
                        userService.update(checkUser,tenantParam);

                        PersonalProfile personalProfile = personalProfileService.getByUserId(xsy_userId,tenantParam);
                        personalProfile.setPositionName(title);
                        personalProfile.setPhone(dingUserDetail.getMobile());
                        personalProfileService.update(personalProfile,tenantParam);
                        //commonService.saveProfile(xsyTenantid, xsy_userId,title,loginName,contact,tenantParam);
                    }
                    if(relThirdUserXsyUser == null){
                        //创建映射关系
                        relThirdUserXsyUser = new RelThirdUserXsyUser();
                        relThirdUserXsyUser.setXsyPassportId(passportId);
                        relThirdUserXsyUser.setThirdPlatformUserId(manager_Dingid);
                        relThirdUserXsyUser.setXsyUserId(xsy_userId);
                        relThirdUserXsyUser.setThirdUserId(dingUserDetail.getUserid());
                        relThirdUserXsyUser.setThirdCorpid(thirdCorpid);
                        relThirdUserXsyUser.setThirdSource(Platform.dingding);
                        relThirdUserXsyUser.setXsyTenantid(xsyTenantid);
                        relThirdUserXsyUser.setSuiteKey(Env.SUITE_KEY);
                        relThirdUserXsyUserService.save(relThirdUserXsyUser);

                    }
                    LOG.info("Corpid["+thirdCorpid+"] grant authorization end xsyTenantid= "+xsyTenantid +" ;xsy_userId = "+xsy_userId );
                    //获取销售易的xsy_token
                    OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_userId,new TenantParam(xsyTenantid));
                    xsyBasicAccessToken = oauthToken.getAccessToken();
                    LOG.info("Corpid["+thirdCorpid+"] by xsy_userId={},xsyTenantid={} to get xiaoshouyi basicAccessToken,reult basicAccessToken ="+xsyBasicAccessToken,xsy_userId,xsyTenantid );
            }
        } catch (OApiException e) {
            e.printStackTrace();
            haveError = true;
        } catch (ServiceException e) {
            e.printStackTrace();
            haveError = true;
        } catch (PaasException e) {
            e.printStackTrace();
            haveError = true;
        }
        if(!haveError){
//        1：新增修改部门
//        2：新增修改员工
//        3：删除多余的员工
//        4：删除多余的部门

            DepartmentThread departmentThread = new DepartmentThread(xsyBasicAccessToken,thirdCorpid,thirdCorpidAccessToken,xsyTenantid,relThirdDepXsyDepartmentService,departmentService
                    ,passportService,relThirdPassportXsyPassportIdService,commonService,tenantService);
            DingUserThread dDUserThread = new DingUserThread( thirdCorpid,   thirdCorpidAccessToken, xsyTenantid,
                    relThirdPassportXsyPassportIdService,   commonService,relThirdUserXsyUserService,userService);
            try{
                LOG.info("Corpid["+thirdCorpid+"] begin  sync   " );
                departmentThread.doAddAndUpdateSync();
                dDUserThread.doCreateOrUpdateUser();
                dDUserThread.doDeleteXsyUser(xsyMangerId);
                departmentThread.doDelDeparmentOpp();
                LOG.info("Corpid["+thirdCorpid+"] sync   end");
            }catch (Exception e){
                e.printStackTrace();
            }
            long  endBeginTime = System.currentTimeMillis();
            LOG.info("syn time consuming {} ms",(endBeginTime - synBeginTime ));
        }


    }

    /**
     * 创建第三方企业与销售易租户的映射关系
     * @param thirdCorpid
     * @param xsyTenantid
     * @throws ServiceException
     */
    private void createAndSaveRelThirdCorpXsyTenant(String thirdCorpid,long xsyTenantid) throws ServiceException {
        RelThirdCorpXsyTenant rel = new RelThirdCorpXsyTenant();
        rel.setThirdSource(Platform.dingding);//来源：钉钉
        rel.setThirdCorpid(thirdCorpid);
        rel.setThridAuthStatus("authorize");//状态：授权
        rel.setXsyTenantid(xsyTenantid);
        rel.setSuiteKey(Env.SUITE_KEY);

        relThirdCorpXsyTenantService.save(rel);
    }

}
