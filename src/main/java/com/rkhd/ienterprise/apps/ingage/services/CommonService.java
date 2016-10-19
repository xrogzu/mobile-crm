package com.rkhd.ienterprise.apps.ingage.services;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.approval.service.InitApprovalDataService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.DDUtils;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.isales.account.model.Account;
import com.rkhd.ienterprise.apps.isales.analysis.service.InitSearchFavoriteService;
import com.rkhd.ienterprise.apps.isales.campaign.model.Campaign;
import com.rkhd.ienterprise.apps.isales.campaign.service.CampaignService;
import com.rkhd.ienterprise.apps.isales.cases.model.Case;
import com.rkhd.ienterprise.apps.isales.cases.service.CaseService;
import com.rkhd.ienterprise.apps.isales.contact.model.Contact;
import com.rkhd.ienterprise.apps.isales.contract.model.Contract;
import com.rkhd.ienterprise.apps.isales.contract.service.ContractService;
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.apps.isales.department.service.InitDepartDataService;
import com.rkhd.ienterprise.apps.isales.highsea.service.InitHighSeaDataService;
import com.rkhd.ienterprise.apps.isales.lead.model.Lead;
import com.rkhd.ienterprise.apps.isales.lead.service.LeadService;
import com.rkhd.ienterprise.apps.isales.opportunity.model.Opportunity;
import com.rkhd.ienterprise.apps.isales.order.model.Order;
import com.rkhd.ienterprise.apps.isales.order.service.OrderService;
import com.rkhd.ienterprise.apps.isales.payment.model.Payment;
import com.rkhd.ienterprise.apps.isales.payment.service.PaymentService;
import com.rkhd.ienterprise.apps.isales.process.model.Stage;
import com.rkhd.ienterprise.apps.isales.process.service.StageService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.model.SalesParameter;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.InitExpenseTypeDataService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.InitIndustryDataService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.InitSalesParameterDataService;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.SalesParameterService;
import com.rkhd.ienterprise.apps.isales.util.ServiceUtil;
import com.rkhd.ienterprise.apps.twitter.blog.service.InitBlogDataService;
import com.rkhd.ienterprise.apps.twitter.file.service.InitTwitterFileDataService;
import com.rkhd.ienterprise.apps.workreport.service.InitWorkReportDataService;
import com.rkhd.ienterprise.base.authority.service.InitRoleDataService;
import com.rkhd.ienterprise.base.captcha.service.CaptchaService;
import com.rkhd.ienterprise.base.dbcustomize.constant.DBCustomizeConstants;
import com.rkhd.ienterprise.base.dbcustomize.model.EntityBelong;
import com.rkhd.ienterprise.base.dbcustomize.model.Item;
import com.rkhd.ienterprise.base.dbcustomize.model.SelectItem;
import com.rkhd.ienterprise.base.dbcustomize.service.*;
import com.rkhd.ienterprise.base.dbcustomize.util.DBCustomizeUtil;
import com.rkhd.ienterprise.base.dimension.constant.DimensionConstants;
import com.rkhd.ienterprise.base.dimension.service.DimensionUserService;
import com.rkhd.ienterprise.base.dimension.service.InitDimensionDataService;
import com.rkhd.ienterprise.base.license.constant.LicenseConstants;
import com.rkhd.ienterprise.base.license.model.License;
import com.rkhd.ienterprise.base.license.service.LicenseFunctionService;
import com.rkhd.ienterprise.base.license.service.LicenseService;
import com.rkhd.ienterprise.base.license.service.UserLicenseService;
import com.rkhd.ienterprise.base.manager.model.UserManager;
import com.rkhd.ienterprise.base.manager.service.UserManagerService;
import com.rkhd.ienterprise.base.multitenant.model.PassportTenant;
import com.rkhd.ienterprise.base.multitenant.model.Tenant;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.passport.model.Passport;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.base.profile.model.PersonalProfile;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.base.relation.model.GroupMember;
import com.rkhd.ienterprise.base.relation.service.GroupMemberService;
import com.rkhd.ienterprise.base.setting.service.InitCommonParameterDataService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.base.view.model.PageView;
import com.rkhd.ienterprise.base.view.model.UserSetting;
import com.rkhd.ienterprise.base.view.service.UserSettingService;
import com.rkhd.ienterprise.constant.CommonConstancts;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.passport.DefaultPasswordGenerator;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.model.RelThirdPassportXsyPassportId;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdPassportXsyPassportIdService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.ienterprise.util.Pagination;
import com.rkhd.ienterprise.util.PaginationParam;
import com.rkhd.ienterprise.util.PinyinUtil;
import com.rkhd.ienterprise.util.StringUtil;
import com.rkhd.ienterprise.web.ParamUtil;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.auth.service.*;
import com.rkhd.platform.customize.constant.CustomizeConstant;
import com.rkhd.platform.customize.exception.CustomizeException;
import com.rkhd.platform.customize.model.CriteriaModel;
import com.rkhd.platform.customize.model.CustomEntity;
import com.rkhd.platform.customize.model.CustomItem;
import com.rkhd.platform.customize.service.CustomDataService;
import com.rkhd.platform.customize.service.CustomEntityService;
import com.rkhd.platform.customize.service.CustomItemService;
import com.rkhd.platform.customize.util.MongoDBHelper;
import com.rkhd.platform.exception.PaasException;
import com.rkhd.platform.rule.model.EntityDuplicateRule;
import com.rkhd.platform.rule.model.EntityDuplicateRuleCriteria;
import com.rkhd.platform.rule.service.EntityDuplicateRuleCriteriaService;
import com.rkhd.platform.rule.service.EntityDuplicateRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 提取出一些多处用到的代码
 */
@Component("mwebCommonService")
public class CommonService {
    private static final Logger LOG = LoggerFactory.getLogger(CommonService.class);
    @Autowired
    private PassportService passportService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private InitCommonParameterDataService initCommonParameterDataService;

    @Autowired
    private InitBlogDataService initBlogDataService;

    @Autowired
    private InitTwitterFileDataService initTwitterFileDataService;

    @Autowired
    private InitDimensionDataService initDimensionDataService;

    @Autowired
    private InitRoleDataService initRoleDataService;

    @Autowired
    private InitDBCustomizeDataService initDBCustomizeDataService;

    @Autowired
    private InitSalesParameterDataService initSalesParameterDataService;

    @Autowired
    private InitIndustryDataService initIndustryDataService;

    @Autowired
    private InitExpenseTypeDataService initExpenseTypeDataService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private InitDepartDataService initDepartDataService;

    @Autowired
    private InitApprovalDataService initApprovalDataService;

    @Autowired
    private InitSearchFavoriteService initSearchFavoriteService;

    @Autowired
    private InitHighSeaDataService initHighSeaDataService;

    @Autowired
    private InitWorkReportDataService initWorkReportDataService;

    @Autowired
    private DepartService departService;

    @Autowired
    private DimensionUserService dimensionUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserLicenseService userLicenseService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResponsibilityService responsibilityService;

    @Autowired
    private UserResponsibilityService userResponsibilityService;

    @Autowired
    private PersonalProfileService personalProfileService;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private SelectItemService selectItemService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;


    @Autowired
    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;


    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private UserManagerService userManagerService;

    @Autowired
    private EntityDuplicateRuleService entityDuplicateRuleService;

    @Autowired
    private EntityDuplicateRuleCriteriaService entityDuplicateRuleCriteriaService;

    @Autowired
    private StageService stageService;

    @Autowired
    private EntityBelongTypeService entityBelongTypeService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    @Autowired
    @Qualifier("mwebDimensionService")
    private DimensionService dimensionService;

    @Autowired
    private SalesParameterService salesParameterService;


    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private EntityBelongService entityBelongService;

    @Autowired
    private LicenseFunctionService licenseFunctionService;
    @Autowired
    private CustomEntityService customEntityService;



    @Autowired
    private InitPackDataService initPackDataService;


    public static final Map<Integer, Integer> TYPE_STATUS_MAP = new HashMap<Integer, Integer>();
    public static final Integer STANDARD_TENANT_INIT_STATUS = 500;
    public static final Integer FMCG_TENANT_INIT_STATUS = 100;
    public static final Integer WECHAT_TENANT_INIT_STATUS = 300;
    static {
        TYPE_STATUS_MAP.put(CommonConstancts.TenantType.STANDARD, STANDARD_TENANT_INIT_STATUS);
        TYPE_STATUS_MAP.put(CommonConstancts.TenantType.FMCG, FMCG_TENANT_INIT_STATUS);
        TYPE_STATUS_MAP.put(CommonConstancts.TenantType.WECHAT, WECHAT_TENANT_INIT_STATUS);
    }

    public static final Integer tenantType = CommonConstancts.TenantType.WECHAT;

//
//    private static final int DATA_HANDLER_MAX = 100;


    //创建销售易用户的passport, 以下情况中用到了：授权用户、拉取部门成员
    public  Passport _doCreateNewPassport(String loginName) throws ServiceException {

        Passport passport = getPassportByLoginName(loginName);
        LOG.info("begin _doCreateNewPassport ,passport = {}",passport==null?null:JSON.toJSONString(passport));
        long  passportId = 0;
        long startTime = 0 ;
        long endTime = 0;
        if (passport != null) {
            LOG.info("passport have been exist. passportid={}",passport.getId());
            passportId = passport.getId();

        }else {
            LOG.info("begin _doCreateNewPassport");
            passport = new Passport();
            if (DDUtils.isEmail(loginName)) {
                passport.setEmail(loginName);
            } else {
                passport.setPhone(loginName);
            }
            //初始密码
            String password =   new DefaultPasswordGenerator().generate(6); //"abc123456";//
            LOG.info("new add user set password is {}",password);
            passport.setPassword(password);
            passport.setValid((short)1);
            passport.setPwdFlg(Passport.PasswordFlag.SYSTEM_GENERATED);
            //入库保存passport
            LOG.info("new passport is {}", JSON.toJSONString(passport));
            startTime = System.currentTimeMillis();
            passportId = passportService.save(passport);
            passport.setId(passportId);
            endTime = System.currentTimeMillis();
            LOG.info("passport have been created ,time consuming {} ms,passportId={}",(endTime-startTime),passportId);
        }
        return passport;
    }

    private Passport getPassportByLoginName(String loginName) throws ServiceException {
        try {

            if (DDUtils.isEmail(loginName)) {
                Passport ps =  passportService.getByEmail(loginName);
                LOG.info("from db loginName={},passprot={}",loginName,ps==null?null:JSON.toJSONString(ps));
                return ps;
            } else {
                return passportService.getByPhone(loginName);
            }
        } catch (ServiceException e) {
            LOG.error("Get passport error loginName is " + loginName, e);
            throw new ServiceException(e);
        }
    }

    /**
     * 创建第三方用户与销售易Passport的映射关系
     * @param thirdPlatformId
     * @param passportId
     * @throws ServiceException
     */
    public void createAndSaveRelThirdPassportXsyPassportId(String thirdPlatformId,long passportId) throws ServiceException {
        RelThirdPassportXsyPassportId rel = new RelThirdPassportXsyPassportId();
        rel.setThirdSource(Platform.dingding);//来源：钉钉
        rel.setPassportId(passportId);
        rel.setThirdPlatformId(thirdPlatformId);
        rel.setSuiteKey(Env.SUITE_KEY);


        relThirdPassportXsyPassportIdService.save(rel);
        LOG.info("create RelThirdPassportXsyPassportId,{}",JSON.toJSONString(rel));
    }



    public void joinPassportInTenant(Long tenantId, Long passportId) {
        try {
            PassportTenant passportTenant = new PassportTenant();
            passportTenant.setPassportId(passportId);
            passportTenant.setTenantId(tenantId);
            tenantService.savePassportTenant(passportTenant);
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    public Long saveUser(TenantParam tenantParam, Long passportId,String userName,long departId ,User.Status status) throws ServiceException {

        try {
            User user = new User();
            user.setPassportId(passportId);
            user.setName(userName);
            user.setStatus(status.getValue());
            user.setFreshGuideStatus((short) 0);
            user.setCreatedBy(0L);
            user.setPinyin(PinyinUtil.cnToPinyin(user.getName()));
            user.setTenantId(tenantParam.getTenantId());
            if(departId == 0){
                Depart  depart = departService.getTop(tenantParam);
                departId = depart.getId();
            }
            user.setDepartId(departId);
            return userService.save(user, tenantParam);
        } catch (ServiceException e) {
            LOG.error("Create SystemUser Error", e);
            throw new ServiceException(e);
        }
    }

    public void initUserLicense(Long userId,TenantParam tenantParam) throws ServiceException {
        userLicenseService.initTrial(userId, tenantParam);
    }

    /**
     * 设置用户角色
     * @param roleId
     * @param userId
     * @param tenantParam
     * @throws PaasException
     */
    public void saveUserRole(Long roleId, Long userId,TenantParam tenantParam) throws  PaasException {
        userRoleService.deleteByUserId(userId,tenantParam);
        userRoleService.save(userId, roleId, tenantParam);
    }

    public void saveResponsibility(Long  responsibilityId,Long userId,TenantParam tenantParam) throws PaasException {
        userResponsibilityService.deleteByUserId(userId,tenantParam);
        userResponsibilityService.save(userId,responsibilityId,tenantParam);
    }
    /**
     *
     * @param tenantId
     * @param userId
     * @param title             职位
     * @param loginName
     * @param contact           联系方式，登录名为email，则contact为电话号码，否则personalEmail为contact
     * @throws ServiceException
     */
    public void saveProfile(Long tenantId, Long userId,String title,String loginName,String contact,TenantParam tenantParam,boolean joinDepartment) throws ServiceException {
        try {
            PersonalProfile profile = new PersonalProfile();
            profile.setUserId(userId);
            Depart depart = departService.getTop(tenantParam);
            if (depart != null) {
                profile.setDepartId(depart.getId());
                profile.setPositionName(title);
                if (DDUtils.isEmail(loginName)) {
                    profile.setPersonalEmail(loginName);
                    profile.setPhone(contact);
                } else {
                    profile.setPersonalEmail(contact);
                    profile.setPhone(loginName);
                }
                profile.setDelFlg(PersonalProfile.NON_DELETED);
                personalProfileService.save(profile, tenantParam);
                if(joinDepartment){
                    joinDepart(depart.getId(), userId,tenantParam);//用户加入组
                }

            } else {
                // setStatusCode(SCode.ERROR.SYSTEM);
                LOG.error("Get top depart failed, tenantId is: " + tenantId);
            }
        } catch (ServiceException e) {
            LOG.error("Save Profile failed, userId is: " + userId, e);
            throw new ServiceException(e);
        }
    }
    public void updateProfile2Db(PersonalProfile profile,TenantParam tenantParam) throws ServiceException {
         LOG.info("profile={},tenantParam={}",JSON.toJSONString(profile) ,JSON.toJSONString(tenantParam));
        PersonalProfile dbprofile = personalProfileService.getByUserId(profile.getUserId(),tenantParam);
//        profile.setId(dbprofile.getId());
        dbprofile.setPhone(profile.getPhone());
        dbprofile.setDepartId(profile.getDepartId());
        dbprofile.setGender(profile.getGender());
        dbprofile.setDelFlg(profile.getDelFlg());
        dbprofile.setUserId(profile.getUserId());
        dbprofile.setPositionName(profile.getPositionName());
        dbprofile.setPersonalEmail(profile.getPersonalEmail());
        dbprofile.setPhone(profile.getPhone());

        LOG.info("开始修改并保存入库的用户详细信息：{}",JSON.toJSONString(dbprofile));

        personalProfileService.update(dbprofile,tenantParam);

    }
    public void joinDepart(Long departId, Long userId,TenantParam tenantParam) throws ServiceException {
        try {
            departService.updateDepartGroupMember(userId, departId, tenantParam);
        } catch (Exception e) {
            LOG.error("Join Depart Error, departId is " + departId +";userId="+userId);
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }


    public void saveSystemInitView(Long userId) throws ServiceException {
        if (userSettingService.getByUserId(userId) == null) {//如果没有保存过视图关联
            UserSetting userSetting = new UserSetting();//保存初始化视图
            userSetting.setUserId(userId);
            userSetting.setPageViewId(PageView.SALES_VIEW);
            userSettingService.save(userSetting);
        }
    }

    public boolean activatePassport(Long passportId,String loginName) throws ServiceException {
        try {
            return passportService.validate(passportId);
        } catch (ServiceException e) {
            LOG.error("Activate passport error, passport is " + loginName, e);
            throw new ServiceException(e);
        }
    }
    public void clearCurrentCaptcha(String loginName) throws ServiceException {
        captchaService.forceClearCode(loginName);
    }

    /**
     * 将钉钉用户转成销售易用户的工具类
     * @param dingUser
     */
    public User dingUser2xsyUser(User beUpDateXsyUse,com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUser,TenantParam tenantParam,Long xsyDeparmentId,String relDimIds, long passportId) throws ServiceException {

        String name = dingUser.getName();
        User user = beUpDateXsyUse;
        if(user == null)
        {
            user = new User();
        }
        user.setDepartId(xsyDeparmentId);
        user.setName(name);
        user.setStatus(User.Status.ACTIVE.getValue());
        user.setFreshGuideStatus((short) 0);
        user.setCreatedBy(0L);
        user.setColleagueRelationDepart(relDimIds);

        user.setTenantId(tenantParam.getTenantId());//因为是内部的情况
        if(xsyDeparmentId == 0){
            Depart  depart = departService.getTop(tenantParam);
            xsyDeparmentId = depart.getId();
        }
        user.setDepartId(xsyDeparmentId);
        user.setPassportId(passportId);

        return user;

    }
    public PersonalProfile dingUser2xsyPersonProfile(com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUser,TenantParam tenantParam,long xsyUserId,long xsyDeparmentId) throws ServiceException {
        String userid = dingUser.getUserid();
//        String isLeaderInDepts = dingUser.getIsLeaderInDepts();  //在对应的部门中是否为主管
//        List<Long> departments = dingUser.getDepartment();
        String position = dingUser.getPosition();

        String mobile = dingUser.getMobile();  //手机号
        String email = dingUser.getEmail();  //电子邮箱
//        boolean  active = dingUser.isActive();  //是否已经激活, true表示已激活, false表示未激活
//        String dingId = dingUser.getDingId();

        PersonalProfile personalProfile = new PersonalProfile();

        personalProfile.setDepartId(xsyDeparmentId);
        personalProfile.setDelFlg(PersonalProfile.NON_DELETED);
        personalProfile.setUserId(xsyUserId);
        personalProfile.setPositionName(position);
        personalProfile.setPersonalEmail(email);
        personalProfile.setPhone(mobile);

//        personalProfileService.save(personalProfile, tenantParam);
//        joinDepart(xsyDeparmentId, xsyUserId,tenantParam);//用户加入组

        return personalProfile;
    }
    public Long saveUserProfile(Long passportId,String uname,long beCreateBy,long departmetnId,String positionName,String phone,String email,TenantParam tenantParam) throws ServiceException {
        User   innerUser = new User();
        innerUser.setName(uname);
        innerUser.setPassportId(passportId);
        innerUser.setPinyin(PinyinUtil.cnToPinyin(innerUser.getName()));
        innerUser.setCreatedBy(beCreateBy);
        innerUser.setDepartId(departmetnId);


        innerUser.setStatus(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue());

        PersonalProfile personalProfile = new PersonalProfile();
        personalProfile.setPhone(phone);
        personalProfile.setPersonalEmail(email);
        personalProfile.setDepartId(departmetnId);
        personalProfile.setPositionName(positionName);
        return userService.saveUserProfile(innerUser, personalProfile, tenantParam);
    }
    private void saveUserManager(Long userId,Long userManagerId,TenantParam tenantParam) throws ServiceException {
        //增加UserManager
        if (userManagerId != null) {
            UserManager userManager = new UserManager();
            userManager.setUserId(userId);
            userManager.setManagerId(userManagerId);
            userManagerService.save(userManager, tenantParam);
        }
    }



    public void saveUserDimension(Long userId,TenantParam tenantParam,long mainDepartmentId,List<Long> relIds) throws ServiceException {

        if(mainDepartmentId == 0){
            Depart depart = departService.getTop(tenantParam);
            mainDepartmentId = depart.getId();
        }
        if(relIds == null){
            relIds = new ArrayList<Long>();
        }
        dimensionUserService.setMain(userId, DimensionConstants.Type.DEPARTMENT, mainDepartmentId, userId, tenantParam);
        dimensionUserService.setRelatedDims(userId, DimensionConstants.Type.DEPARTMENT, relIds, userId, tenantParam);
    }

    private Role getNormalRole(TenantParam tenantParam) throws PaasException {
        return roleService.getDefaultNormal(tenantParam);
    }

    private void saveNormalResponsibility(Long userId,TenantParam tenantParam) throws PaasException {
        Responsibility responsibility = responsibilityService.getDefaultByType(Responsibility.DEFAULT_COMMON_USER, tenantParam);
        userResponsibilityService.save(userId, responsibility.getId(), tenantParam);
    }

    /**
     * 创建租户时使用
     * @param tenantParam
     * @throws ServiceException
     */
    public  void initTenantData(  TenantParam tenantParam) throws ServiceException {
        try {
            if (tenantParam != null) {
                tenantParam.setType(tenantType);

                LOG.info("init tenantParam working 。。。");
                //setTenantParam(new TenantParam(tenantId));
                initCommonParameterDataService.initData(tenantParam);  //CommonParameter初始化  b_common_parameter
                initBlogDataService.initData(tenantParam);  //Blog类别数据初始化(a_blog_category表)
                initTwitterFileDataService.initData(tenantParam);  //销售资源库中的文件目录初始化(a_twitter_file_directory表)
                initDimensionDataService.initData(tenantParam); //多维度权限初始化(b_dimension_tenant表，b_dimension_tenant_belong表)
                initRoleDataService.initData(tenantParam);   //角色初始化(b_role表，b_role_function表)
                initDBCustomizeDataService.initData(tenantParam);  //客户级别等 ，DB定制数据初始化(b_item表，b_select_item表，b_individual_view表，b_multi_form_view表，b_multi_view_item表),防重规则

                docustomerDuplicateRule(tenantParam);//钉钉个性化定制增加防重机制

                initSalesParameterDataService.initData(tenantParam);   //SalesParameter数据初始化(a_sales_parameter表) 活动记录类型

                /**
                 *  商机销售阶段数据初始化(b_entity_belong_type表，b_stage表)
                 *  开始
                 */
//                initSaleStageDataService.initData(tenantParam);   //商机销售阶段数据初始化(b_entity_belong_type表，b_stage表)
                stageService.clearTenantInitData(tenantParam);
                Long entityTypeId = entityBelongTypeService.getDefault(DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY,tenantParam).getId();
                saveStage("初步洽谈",entityTypeId, 20, Opportunity.STATUS_PROCEED, 1,tenantParam);
                saveStage("需求确定",entityTypeId, 50, Opportunity.STATUS_PROCEED, 2,tenantParam);
                saveStage("方案报价",entityTypeId, 80, Opportunity.STATUS_PROCEED, 3,tenantParam);
                saveStage("签约",entityTypeId, 100, Opportunity.STATUS_WIN, 4,tenantParam);
                saveStage("输单",entityTypeId, 0, Opportunity.STATUS_LOSE, 5,tenantParam);
                /**
                 *  商机销售阶段数据初始化(b_entity_belong_type表，b_stage表)
                 *  结束
                 */
                initIndustryDataService.initData(tenantParam);   //行业数据初始化(a_industry表)
                initExpenseTypeDataService.initData(tenantParam);   //费用类型初始化(a_expense_type表)
                //为与PC端一致，lincense放到initialize()方法中
                licenseService.initTrialTenant(tenantParam);   //试用License数据初始化
                //个性化license： 修改钉钉授权用户人数和终止日期
                 this.customSetLicense(tenantParam,true);

                initDBCustomizeDataService.upgradeDataFreeToPro(tenantParam); // 升级DB定制数据到专业版
                initDepartDataService.initDepartData(tenantParam); // 初始化部门
                initApprovalDataService.initData(tenantParam);  // 审批初始化数据
                initPackDataService.initData(tenantParam); //初始化平台包裝數據
                initSearchFavoriteService.initData(tenantParam);
                initHighSeaDataService.initData(tenantParam);
                initWorkReportDataService.initData(tenantParam);
                initDBCustomizeDataService.initEntityTypeToLayout(tenantParam);//初始化平台数据

                //新增修改用户级别
                Item accountLevelItem =  itemService.getItemByEntryPropertyName(DBCustomizeConstants.DUMMY_ITEM_ACCOUNT_LEVEL,tenantParam);
                accountLevelItem.setItemName("公司级别");
                itemService.update(accountLevelItem,tenantParam);
                List<SelectItem> accountLevelSelectItems =  selectItemService.getByItemId(accountLevelItem.getId(),tenantParam);
                SelectItem selectItem = null;
                long itemId = 0;
                for(int i=0;i<accountLevelSelectItems.size();i++){
                    selectItem = accountLevelSelectItems.get(i);
                    if(itemId == 0){
                        itemId = selectItem.getItemId();
                    }
                    if(selectItem.getSelectItemId() == 1){
                        selectItem.setSelectItemName("核心");
                    }else  if(selectItem.getSelectItemId() == 2){
                        selectItem.setSelectItemName("重点");
                    }else  if(selectItem.getSelectItemId() == 3){
                        selectItem.setSelectItemName("普通");
                    }
                    selectItemService.update(selectItem,tenantParam);
                }

//个性化定制，初始化客户状态下来单选，可选值为：潜在客户，成交客户，重要客户
                addItemAccouontStatus(tenantParam);

                SalesParameter salesParameter = new SalesParameter();
                salesParameter.setParameterType(SalesParameter.SaleType.PARAMETER_TYPE_ACTIVITY_RECORD.getValue());
                salesParameter.setParameterName("邮件");
                salesParameter.setParameterOrder(4);
                salesParameter.setDescription("邮件");
                salesParameter.setDummyType(SalesParameter.DummyType.ACTIVITY_RECORD_EMAIL.getValue());
                salesParameter.setUpdatedAt(System.currentTimeMillis());
                salesParameter.setUpdatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
                salesParameterService.save(salesParameter, tenantParam);
//最后一步，设置状态，标识租户初始化结束
//                tenantService.updateInitStatus(tenantParam.getTenantId(), 200);  // 设置租户初始化完毕

                tenantService.updateInitStatus(tenantParam.getTenantId(), 200);  // 设置租户初始化完毕
                LOG.info("初始化租户信息结束");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    public Tenant saveTenant(String companyName ) throws ServiceException {
        try {
            Tenant tenant = new Tenant();
            tenant.setCompany(companyName);
            tenant.setType(tenantType);
            tenant.setEdition(Tenant.EDITION_PROFESSIONAL);
            Long id =  tenantService.saveTenant(tenant);
            tenant.setId(id);
            return tenant;

        } catch (ServiceException e) {
            //
            LOG.error("Create Tenant Error", e);
            throw new ServiceException(e);
        }
    }

    public void addOrUpdateRelThirdToken(String corpId,String access_token,String jsticket,String permanent_code,String thirdsource) throws ServiceException {
        RelThirdToken relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,corpId,Env.SUITE_KEY);
        boolean newAdd = false;
        if(relThirdToken == null){
            newAdd = true;
            relThirdToken = new RelThirdToken();
        }
        Date now = new Date();
        relThirdToken.setTokenValue(access_token);
        relThirdToken.setJsapiTicket(jsticket);
        relThirdToken.setPermanentCode(permanent_code);
        relThirdToken.setCreateAt(now);
        relThirdToken.setExpiresIn(7200L);
        relThirdToken.setThirdCorpid(corpId);
        relThirdToken.setThirdSource(thirdsource);
        relThirdToken.setSuiteKey(Env.SUITE_KEY);
        if(newAdd){
            relThirdTokenService.save(relThirdToken);
            LOG.info("new added corp`s token  success ,it is {}",JSON.toJSONString(relThirdToken));
        }else{
            relThirdTokenService.update(relThirdToken);
            LOG.info("update corp`s token  success ,it is {}",JSON.toJSONString(relThirdToken));
        }
    }
    public  Long getDepartmentDimension(String token){
        /**
         *1:客户
         * 2：联系人
         *3:销售机会
         */
        EntityReturnData data  =  dimensionService.dimensionDepartments(token,1);
        JSONObject entity = (JSONObject) data.getEntity();
        JSONArray records = entity.getJSONArray("records");
        JSONObject dim = records.getJSONObject(0);
        return dim.getLong("id");
    }

    public long commonDimsion(String token ,int type){
        /**
         *1:客户
         * 2：联系人
         *3:销售机会
         */
        EntityReturnData data  =  dimensionService.dimensionDepartments(token,type);
        JSONObject entity = (JSONObject) data.getEntity();
        JSONArray records = entity.getJSONArray("records");
        JSONObject dim = records.getJSONObject(0);
        return dim.getLong("id");
    }

    private Long saveStage(String stageName, Long businessTypeId, int percent, Short status, int order,TenantParam tenantParam) throws ServiceException {
        Stage stage = new Stage();
        stage.setStageName(stageName);
        stage.setEntityTypeId(businessTypeId);
        stage.setStatus(status);
        stage.setPercent(percent);
        stage.setOrder(order);
        stage.setCreatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
        stage.setUpdatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
        return stageService.save(stage, tenantParam);
    }

    /**
     * 调用前必须先调用initRoleInfo方法，如果是多次调用，则在循环外调用一次即可。
     * 执行同步用户操作
      * @param dingUserDetail
     * @return
     * @throws ServiceException
     * @throws PaasException
     */
    public Map<String ,Object> doSyncDingUser2Xsy(com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUserDetail
    , String thirdCorpid,TenantParam  tenantParam ,Map<String,com.rkhd.platform.auth.model.Role> roleMap,
                                                  Map<String,Responsibility> responsibilityMap,boolean departmentHaveBeenSynced
    ) throws ServiceException, PaasException {

        String ding_userid = dingUserDetail.getUserid();//员工在企业内的UserID
        String ding_id = dingUserDetail.getDingId();
        LOG.info("Corpid["+thirdCorpid+"] execute sync dingding user,ding_id={}，dingUserDetail={}",ding_id,JSON.toJSONString(dingUserDetail));

        List<Long> ding_departmetns = dingUserDetail.getDepartment();
        String mainDingDepartmentId = ding_departmetns.get(0)+"";
        //查询passport映射关系
        LOG.info("Corpid["+thirdCorpid+"] get relation by ding_id={} ",ding_id);
//        确定passprot映射关系是否存在，不存在则新建passport映射关系
        RelThirdPassportXsyPassportId relThirdPassportXsyPassportId = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(ding_id,Platform.dingding,Env.SUITE_KEY);
        LOG.info("Corpid["+thirdCorpid+"] get relation by ding_id={} ,relThirdPassportXsyPassportId={} ",ding_id,relThirdPassportXsyPassportId);
        Passport passport = null;
        String loginName = DDUtils.getLoginNameByDingDingId(ding_id) ;
        if(relThirdPassportXsyPassportId != null){
            passport = passportService.get(relThirdPassportXsyPassportId.getPassportId());
        }else {
            //创建销售易passport
            passport =  this._doCreateNewPassport(loginName);
            //创建第三方个人ID与销售易用户的id的对应关系
            LOG.info("Corpid["+ thirdCorpid+"] begin create RelThirdPassportXsyPassportId" );
             createAndSaveRelThirdPassportXsyPassportId(ding_id,passport.getId());
        }
        long xsyTenantid =  tenantParam.getTenantId();
//         tenantParam = new TenantParam(xsyTenantid);
//        判断passport是否在租户里，如果不在则添加
        boolean exitPassportIdAndTenantId = tenantService.existByPassportIdAndTenantId(passport.getId(),xsyTenantid);
        if(!exitPassportIdAndTenantId){
            this.joinPassportInTenant(xsyTenantid, passport.getId());
        }
        //判断部门映射关系是否存在，如果不存在则默认0
        RelThirdDepXsyDepartment relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(
                thirdCorpid,mainDingDepartmentId,Platform.dingding,Env.SUITE_KEY);
        LOG.info("Corpid["+thirdCorpid+"] get RelThirdDepXsyDepartment result is {}",relThirdDepXsyDepartment==null?"  not find":JSON.toJSONString(relThirdDepXsyDepartment) );
        long mianXsyDepartmetId = 0;
        if(relThirdDepXsyDepartment != null){
            mianXsyDepartmetId = relThirdDepXsyDepartment.getXsyDepartmentId();
        }

        String uname = dingUserDetail.getName();
        String positionName = dingUserDetail.getPosition();
        String phone = dingUserDetail.getMobile();
        String email = dingUserDetail.getEmail();

//        JSONArray dimensions = new JSONArray();
        String related = "";
        Long mainDepartmentId = 0L;
        List<Long > relatedIds = new ArrayList<Long >();
        if( departmentHaveBeenSynced && CollectionUtils.isNotEmpty(ding_departmetns)){
            LOG.info("Corpid["+ thirdCorpid+"] begin get user deparmentId and relate departmentId ,and ding_departmetns is "+ding_departmetns);
            for(int i = 0;i<ding_departmetns.size();i++){
                relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource( thirdCorpid,ding_departmetns.get(i)+"",Platform.dingding,Env.SUITE_KEY);
                if(i == 0){
                    mainDepartmentId = relThirdDepXsyDepartment.getXsyDepartmentId() ;
                }else {
                    if(related.contains(",")){
                        related += ",";
                    }
                    relatedIds.add(relThirdDepXsyDepartment.getXsyDepartmentId());
                    related += relThirdDepXsyDepartment.getXsyDepartmentId();
                }
            }
            LOG.info("Corpid["+ thirdCorpid+"] get main = {},related={}",mainDepartmentId,related );

        }
        if (!departmentHaveBeenSynced){
            Depart depart = departService.getTop(tenantParam);
            mainDepartmentId = depart.getId();
        }
        RelThirdUserXsyUser managerRelThirdUserXsyUser = null ;
        long operatorId = 0;
        long managerId   = 0;
        long beCreateBy = 0;

        Long  roleId = roleMap.get(SyncAddressBookeThread.ROLE_NORMAL_KEY).getId();
        Long  responsibilityId = responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_NORMAL_KEY).getId();

        LOG.info("Corpid["+ thirdCorpid+"] begin initRoleInfo");
        String isLeaderInDepts = dingUserDetail.getIsLeaderInDepts();
        String isAdmin = dingUserDetail.getIsAdmin();
        LOG.info("Corpid["+ thirdCorpid+"] dingUserDetail = {}",JSON.toJSONString(dingUserDetail));
        if("true".equals(isAdmin)){
//            系统管理员角色
            roleId = roleMap.get(SyncAddressBookeThread.ROLE_SYSTEM_MANAGER_KEY).getId();
            responsibilityId =  responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_SYSTEM_KEY).getId();
            LOG.info("ding is adminager :{}",JSON.toJSONString(dingUserDetail));
        }else if(isLeaderInDepts.contains("true")){
            //经理角色
            roleId = roleMap.get(SyncAddressBookeThread.ROLE_MANAGER_KEY).getId();
            responsibilityId =  responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_MANAGER_KEY).getId();

            LOG.info("ding is leader :{}",JSON.toJSONString(dingUserDetail));
        }
        LOG.info("Corpid["+ thirdCorpid+"] responsibilityId = {} ,responsibilityId={}",responsibilityId,responsibilityId);
        LOG.info("Corpid["+thirdCorpid+"] begin get managerRelThirdUserXsyUser");
//        查找管理员ID
        managerRelThirdUserXsyUser = relThirdUserXsyUserService.getFirstRelThirdUserXsyUserByCorpidThird(thirdCorpid,Platform.dingding,Env.SUITE_KEY);
        if(managerRelThirdUserXsyUser != null){
            operatorId = managerId =  beCreateBy  = managerRelThirdUserXsyUser.getXsyUserId();
        }
        LOG.info( "Corpid["+thirdCorpid+"] begin create user");
        //创建用户，long managerId,long operatorId, beCreateBy
        com.rkhd.ienterprise.base.user.model.User checkUser = null ;
        RelThirdUserXsyUser relThirdUserXsyUser =   relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdPlatformUserIdThirdUserId(
                thirdCorpid,ding_id,
                dingUserDetail.getUserid(),Platform.dingding,Env.SUITE_KEY);
        long xsy_userId = 0L;
        if (relThirdUserXsyUser != null){
            //有用户修改名字的现象存在，所以在这里避免修改名字重新增加用户
              xsy_userId =  relThirdUserXsyUser.getXsyUserId();
            checkUser = userService.get(xsy_userId,tenantParam);
        }
//        避免用户建立成功，但是映射关系建立失败的现象
        if( checkUser == null) {
            checkUser =  userService.getByPassportIdAndTenantId(passport.getId(), tenantParam);
        }

        if( checkUser == null){

            xsy_userId = saveUserProfile(passport.getId(),uname,beCreateBy,mianXsyDepartmetId,positionName,phone,email,tenantParam);

            //设置直属上级信息
            // saveUserManager(userId,managerId,tenantParam);
            joinDepart(mianXsyDepartmetId,xsy_userId,tenantParam);
//            setUserDimension(userId,dimensions,tenantParam,operatorId);
            saveUserDimension(xsy_userId,tenantParam,mainDepartmentId,relatedIds);
            //设置用户角色
            this.saveUserRole(roleId,xsy_userId,tenantParam);
            //设置用户职能
            this.saveResponsibility(responsibilityId,xsy_userId,tenantParam);
            //初始化用户视图
            this.saveSystemInitView(xsy_userId);

            //激活passprot
            this.activatePassport(passport.getId(),loginName);

            this.clearCurrentCaptcha(loginName);

            LOG.info("Corpid["+thirdCorpid+"] new added user ,xsy_userId = {}",xsy_userId);
        }else{
            //更新用户
            xsy_userId = checkUser.getId();
//            com.rkhd.ienterprise.base.user.model.User beUpdateXsyUser = userService.get(xsy_userId,tenantParam);
            LOG.info("Corpid["+ thirdCorpid+"] begin get xiaoshouyi user by xsyUserId="+xsy_userId+",result = {}",JSON.toJSONString(checkUser));
            com.rkhd.ienterprise.base.user.model.User xsyUser = this.dingUser2xsyUser(checkUser,dingUserDetail,tenantParam,mianXsyDepartmetId,related,passport.getId());
            xsyUser.setId(xsy_userId);
            xsyUser.setName(dingUserDetail.getName());
            xsyUser.setStatus(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue());

            LOG.info("Corpid["+ thirdCorpid+"] begin update xiaoshouyi user {}",JSON.toJSONString(xsyUser));
            xsyUser.setDepartId(mainDepartmentId);
            userService.update(xsyUser,tenantParam);

            PersonalProfile personalProfile =  this.dingUser2xsyPersonProfile(dingUserDetail,tenantParam,xsy_userId,mainDepartmentId);
            this.updateProfile2Db(personalProfile,tenantParam);
            LOG.info("update user info ;");
            //设置用户角色
            this.saveUserRole(roleId,xsy_userId,tenantParam);
            //设置用户职能
            this.saveResponsibility(responsibilityId,xsy_userId,tenantParam);

            //修改用户部门信息
            this.joinDepart(mainDepartmentId,xsy_userId,tenantParam);
//           设置用户多维度信息
           this.saveUserDimension(xsy_userId,tenantParam,mainDepartmentId,relatedIds);

            LOG.info("Corpid["+ thirdCorpid+"] update user["+dingUserDetail.getName()+"] to department ["+mainDepartmentId+"]  ,xsyUser = {} ,",JSON.toJSONString(xsyUser));
        }
        //初始化licene
        this.initUserLicense(xsy_userId,tenantParam);
        LOG.info("Corpid["+ thirdCorpid+"] init new  user[{}]  License",xsy_userId);

        //不存在相同的映射关系则重新建映射关系
        if(relThirdUserXsyUser == null
                || !(
                relThirdUserXsyUser.getXsyUserId().equals(xsy_userId)
                        &&  relThirdUserXsyUser.getXsyPassportId().equals(passport.getId())
                        && relThirdUserXsyUser.getXsyTenantid().equals(xsyTenantid)
        )
        ){
            relThirdUserXsyUser = new RelThirdUserXsyUser();
            relThirdUserXsyUser.setXsyPassportId(passport.getId());
            relThirdUserXsyUser.setThirdPlatformUserId(ding_id);
            relThirdUserXsyUser.setXsyUserId(xsy_userId);
            relThirdUserXsyUser.setThirdUserId(ding_userid);
            relThirdUserXsyUser.setThirdCorpid( thirdCorpid);
            relThirdUserXsyUser.setThirdSource(Platform.dingding);
            relThirdUserXsyUser.setXsyTenantid(xsyTenantid);
            relThirdUserXsyUser.setSuiteKey(Env.SUITE_KEY);
            relThirdUserXsyUserService.save(relThirdUserXsyUser);
            LOG.info("Corpid["+ thirdCorpid+"] create relThirdUserXsyUser={}",JSON.toJSONString(relThirdUserXsyUser));
        }
        Map<String, Object> retunData =   new HashMap<String, Object>();
        retunData.put("xsy_userId",xsy_userId);

        return retunData;
    }

    public  Map<String,com.rkhd.platform.auth.model.Role> initRoleInfo(TenantParam tenantParam) throws PaasException {
//        所有角色
        LOG.info(" begin get all roleList");
        List<com.rkhd.platform.auth.model.Role> roleList = roleService.getAllList(tenantParam);
        LOG.info("  get all roleList result is {}",JSON.toJSONString(roleList));
         Map<String,com.rkhd.platform.auth.model.Role> rolMap = new HashMap<String, Role>();
        if(CollectionUtils.isNotEmpty(roleList)){
            for(com.rkhd.platform.auth.model.Role role : roleList){
                if(role.getRoleType() == SyncAddressBookeThread.ROLE_SYSTEM_MANAGER){
                    rolMap.put( SyncAddressBookeThread.ROLE_SYSTEM_MANAGER_KEY,role);
                }else if(role.getRoleType() ==  SyncAddressBookeThread.ROLE_NORMAL){
                    rolMap.put( SyncAddressBookeThread.ROLE_NORMAL_KEY,role);
                }else if(role.getRoleType() ==  SyncAddressBookeThread.ROLE_MANAGER){
                    rolMap.put( SyncAddressBookeThread.ROLE_MANAGER_KEY,role);
                }
            }
        }
        return rolMap;
    }

    public  Map<String,Responsibility> initResponsibilityInfo(TenantParam tenantParam) throws PaasException {
//            所有职能
        LOG.info(" begin get all responsibilityList  " );
        List<Responsibility> responsibilityList = responsibilityService.getAllResponsibility(tenantParam);
        Map<String,Responsibility> responsibilityMap = new HashMap<String, Responsibility>();
        if(CollectionUtils.isNotEmpty(responsibilityList)){
            for(Responsibility item : responsibilityList){
                if(item.getResponsibilityType() ==  SyncAddressBookeThread.RESPONSIBILITY_SYSTEM){
                    responsibilityMap.put( SyncAddressBookeThread.RESPONSIBILITY_SYSTEM_KEY,item);
                }else if(item.getResponsibilityType() ==  SyncAddressBookeThread.RESPONSIBILITY_NORMAL){
                    responsibilityMap.put( SyncAddressBookeThread.RESPONSIBILITY_NORMAL_KEY,item);
                }else if(item.getResponsibilityType()  ==  SyncAddressBookeThread.RESPONSIBILITY_MANAGER){
                    responsibilityMap.put( SyncAddressBookeThread.RESPONSIBILITY_MANAGER_KEY,item);
                }
            }
        }
        return responsibilityMap;
    }

    public SelectItem createSelectItem(Integer selectItemId,String selectItemName,Short useFlg,Integer selectItemOrder,Short defaultFlg,Integer specialFlg){
        SelectItem selectItem   = new SelectItem();
        selectItem.setSelectItemId(selectItemId);
        selectItem.setSelectItemName(selectItemName);
        selectItem.setUseFlg(useFlg);
        selectItem.setSelectItemOrder(selectItemOrder);
        selectItem.setDefaultFlg(defaultFlg);
        selectItem.setSpecialFlg(specialFlg);

        selectItem.setCreatedAt(Long.valueOf(System.currentTimeMillis()));
        selectItem.setCreatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
        selectItem.setUpdatedAt(Long.valueOf(System.currentTimeMillis()));
        selectItem.setUpdatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
        return selectItem;
    }

    public void addItemAccouontStatus(TenantParam  tenantParam) throws ServiceException {
        Long belongID = DBCustomizeConstants.ENTITY_BELONG_ACCOUNT;
        short itemTypeEntry = DBCustomizeConstants.ITEM_TYPE_SELECT;

        Item item = new Item();
        item.setItemName("公司状态");
        item.setBelongId(belongID);
        item.setItemGroupId(1L);
        item.setItemTypeEntry(itemTypeEntry);
        item.setItemTypeSearch((short)3);
        item.setSystemItemFlg((short)1);//标识字段不可被删除
        item.setExtraItemFlg((short)0);
        item.setUseFlg((short)1);
        item.setItemOrder(7);
        item.setMustEnterFlg((short)0);//非必填
        item.setRadioFlg((short)0);
        item.setSortFlg((short)1);

        List<Item> itemList = itemService.getItemsByAllBelong(item.getBelongId(), tenantParam);
        String entryPropertyName =   DBCustomizeUtil.getDBCustomizePropertyName(belongID, itemList, itemTypeEntry);
        item.setEntryPropertyName(entryPropertyName);

        item.setCreatedAt(Long.valueOf(System.currentTimeMillis()));
        item.setCreatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);
        item.setUpdatedAt(Long.valueOf(System.currentTimeMillis()));
        item.setUpdatedBy(CommonConstancts.DEFAULT_TEMPLATE_USER_ID);

        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        selectItems.add(createSelectItem(1,"潜在",(short)1,1,(short)0,0));
        selectItems.add(createSelectItem(2,"成交",(short)1,2,(short)0,0));
        selectItems.add(createSelectItem(3,"流失",(short)1,3,(short)0,0));
        item.setSelectItemList(selectItems);
        itemService.save(item, tenantParam);
    }

    /**
     * 更改用户角色
     * @param dingUserDetail
     * @param thirdCorpid
     * @param tenantParam
     * @param roleMap
     * @param responsibilityMap
     * @param departmentHaveBeenSynced
     */
    public void setDingUserRole(com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUserDetail
            , String thirdCorpid,TenantParam  tenantParam ,Map<String,com.rkhd.platform.auth.model.Role> roleMap,
                                Map<String,Responsibility> responsibilityMap,boolean departmentHaveBeenSynced) throws ServiceException, PaasException {


        com.rkhd.ienterprise.base.user.model.User checkUser = null ;
        RelThirdUserXsyUser relThirdUserXsyUser =   relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdPlatformUserIdThirdUserId(
                thirdCorpid,dingUserDetail.getDingId(),
                dingUserDetail.getUserid(),Platform.dingding,Env.SUITE_KEY);
        if (relThirdUserXsyUser != null){
            //有用户修改名字的现象存在，所以在这里避免修改名字重新增加用户
            long  xsy_userId =  relThirdUserXsyUser.getXsyUserId();
            checkUser = userService.get(xsy_userId,tenantParam);
        }

        if( checkUser != null) {
            Long  roleId = roleMap.get(SyncAddressBookeThread.ROLE_NORMAL_KEY).getId();
            Long  responsibilityId = responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_NORMAL_KEY).getId();

            LOG.info("Corpid["+ thirdCorpid+"] begin initRoleInfo");
            String isLeaderInDepts = dingUserDetail.getIsLeaderInDepts();
            String isAdmin = dingUserDetail.getIsAdmin();
            if("true".equals(isAdmin)){
//            系统管理员角色
                roleId = roleMap.get(SyncAddressBookeThread.ROLE_SYSTEM_MANAGER_KEY).getId();
                responsibilityId =  responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_SYSTEM_KEY).getId();
            }else if(isLeaderInDepts.contains("true")){
                //经理角色
                roleId = roleMap.get(SyncAddressBookeThread.ROLE_MANAGER_KEY).getId();
                responsibilityId =  responsibilityMap.get(SyncAddressBookeThread.RESPONSIBILITY_MANAGER_KEY).getId();
            }

            //设置用户角色
            this.saveUserRole(roleId,checkUser.getId(),tenantParam);
            //设置用户职能
            this.saveResponsibility(responsibilityId,checkUser.getId(),tenantParam);
        }else{
            LOG.error(" checkUser is null ");
        }
    }

    public void customSetLicense(TenantParam tenantParam,boolean force) throws ServiceException {
        //个性化： 修改钉钉授权用户人数和终止日期
        List<License> licenses =  licenseService.getLicenses(tenantParam);
        Calendar c = Calendar.getInstance();
        c.add( Calendar.YEAR, 3);//3年有效期
        c.clear(Calendar.MILLISECOND);
        long expireTime =  c.getTimeInMillis();
        Integer dingLicenseUserNum = 200000;

        Calendar cNow = Calendar.getInstance();
        for(License licenseItem: licenses){
            if(force  ||  licenseItem.getExpireTime()<cNow.getTimeInMillis()){
                licenseItem.setExpireTime(expireTime);

                licenseItem.setUserNum(dingLicenseUserNum);
                licenseService.update(licenseItem,tenantParam);
            }

        }
    }
    //个性化定制查重机制，联系人电话号码查重，商机名称查重
    public void docustomerDuplicateRule(TenantParam tenantParam) throws PaasException, ServiceException {
//联系人电话号码查重
        EntityDuplicateRule contactEntityDuplicateRule =  entityDuplicateRuleService.getByBelongByStatus(DBCustomizeConstants.ENTITY_BELONG_CONTACT,1,tenantParam);
        if(contactEntityDuplicateRule != null){
            this.initDuplicateRule(contactEntityDuplicateRule,tenantParam,DBCustomizeConstants.DUMMY_ITEM_CONTACT_MOBILE);
        }

    }

    /**
     *
     * @param entityDuplicateRule
     * @param tenantParam
     * @param itermField  例如：DBCustomizeConstants.DUMMY_ITEM_CONTACT_MOBILE
     * @throws PaasException
     * @throws ServiceException
     */
    private void initDuplicateRule(EntityDuplicateRule entityDuplicateRule,TenantParam tenantParam,String itermField) throws PaasException,ServiceException{
        Calendar cal = Calendar.getInstance();

        Item item = itemService.getItemByEntryPropertyNameAndBelongId(entityDuplicateRule.getBelongId(),
                itermField,tenantParam);

        EntityDuplicateRuleCriteria entityDuplicateRuleCriteria = new EntityDuplicateRuleCriteria();
        entityDuplicateRuleCriteria.setBelongId(entityDuplicateRule.getBelongId());
        entityDuplicateRuleCriteria.setRuleId(entityDuplicateRule.getId());
        entityDuplicateRuleCriteria.setRowNo(1);
        entityDuplicateRuleCriteria.setFieldId(item.getId());
        entityDuplicateRuleCriteria.setField(item.getEntryPropertyNameOnly());
        entityDuplicateRuleCriteria.setFieldType("1");
        entityDuplicateRuleCriteria.setOperator(1);
        entityDuplicateRuleCriteria.setSpecialFlg(1);
        entityDuplicateRuleCriteria.setCreatedAt(cal.getTimeInMillis());
        entityDuplicateRuleCriteriaService.save(entityDuplicateRuleCriteria, tenantParam);
    }

    public List<Long> getAllSalesUserIds(TenantParam tenantParam) throws ServiceException {
        Set<Long> uids = new HashSet<Long>();
        for (Depart depart : getAllSalesDepartments(tenantParam)) {
            for (GroupMember member : groupMemberService.getMembersByGroupAndStatus(depart.getGroupId(), (short) 0, tenantParam)) {
                uids.add(member.getUserId());
            }
        }
        return new ArrayList<Long>(uids);
    }
    public List<Depart> getAllSalesDepartments(TenantParam tenantParam) throws ServiceException {
        List<Depart> departs = new ArrayList<Depart>();
        for (Depart depart : departService.getDepartAll(tenantParam)) {
            if (depart.getDepartType().equals(Depart.DEPART_TYPE_SELL)) {
                departs.add(depart);
            }
        }
        return departs;
    }



    public void saveProfile(TenantParam tenantParam, Long userId,String position,long departmetnId,String mobile,String email) throws ServiceException {
        try {
            PersonalProfile profile = new PersonalProfile();
            profile.setUserId(userId);
            if (departmetnId == 0){
                Depart depart = departService.getTop(tenantParam);
                departmetnId = depart.getId();
            }
            profile.setDepartId(departmetnId);
            profile.setPositionName(position);
            profile.setPersonalEmail(email);
            profile.setPhone(mobile);
            profile.setTenantId(tenantParam.getTenantId());

            profile.setDelFlg(PersonalProfile.NON_DELETED);
            personalProfileService.save(profile, tenantParam);
//            joinDepart(departmetnId, userId);//用户加入组
        } catch (ServiceException e) {
            LOG.error("Save Profile failed, userId is: " + userId, e);
            throw new ServiceException(e);
        }
    }


    /**
     * 获取所有业务实体的名称
     *
     * @param user
     * @param tenantParam
     * @return
     */
    public JSONObject getKeywords(User user, TenantParam tenantParam) {
        JSONObject keywords = new JSONObject();
        try {
            Map<Long, EntityBelong> entityBelongs = getEntityBelongs(user, tenantParam);
            for (EntityBelong belong : entityBelongs.values()) {
                if (StringUtil.isNotEmpty(belong.getName())) {
                    keywords.put(belong.getName().toLowerCase(), belong.getLabel());
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return keywords;
    }

    private Map<Long, EntityBelong> getEntityBelongs(User user, TenantParam tenantParam) throws ServiceException {
        Map<Long, EntityBelong> entityBelongs = new HashMap<Long, EntityBelong>();
        for (EntityBelong belong : entityBelongService.getDefaultBelongs(user.getLanguage(), tenantParam)) {
            entityBelongs.put(belong.getId(), belong);
        }
        return entityBelongs;
    }

    public List< JSONObject> initBusinessList( JSONObject keywords, TenantParam tenantParam) throws CustomizeException {
        List<JSONObject> businessList = new ArrayList<JSONObject>();
        if (hasBusinessLicense(LicenseConstants.FUNC_ACCOUNT, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_ACCOUNT);
            o.put("belongName", keywords.get("account"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_OPPORTUNITY, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_OPPORTUNITY);
            o.put("belongName", keywords.get("opportunity"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_CONTACT, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_CONTACT);
            o.put("belongName", keywords.get("contact"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_CONTRACT, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_CONTRACT);
            o.put("belongName", keywords.get("contract"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_LEAD, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_LEAD);
            o.put("belongName", keywords.get("lead"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_ORDER, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_ORDER);
            o.put("belongName", keywords.get("order"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_CAMPAIGN, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_CAMPAIGN);
            o.put("belongName", keywords.get("campaign"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_PARTNER, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_PARTNER);
            o.put("belongName", keywords.get("partner"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_COMPETITOR, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_COMPETITOR);
            o.put("belongName", keywords.get("competitor"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_CASES, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_CASE);
            o.put("belongName", keywords.get("case"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_PAYMENT, tenantParam)) {
            JSONObject o = new JSONObject();
            o.put("belongId", DBCustomizeConstants.ENTITY_BELONG_PAYMENT);
            o.put("belongName", keywords.get("payment"));
            businessList.add(o);
        }
        if (hasBusinessLicense(LicenseConstants.FUNC_EXPENSE, tenantParam)) {
            //费用和报销单是一个权限
            JSONObject o1 = new JSONObject();
            o1.put("belongId", DBCustomizeConstants.ENTITY_BELONG_EXPENSE);
            o1.put("belongName", keywords.get("expense"));
            businessList.add(o1);
            JSONObject o2 = new JSONObject();
            o2.put("belongId", DBCustomizeConstants.ENTITY_BELONG_EXPENSE_ACCOUNT);
            o2.put("belongName", keywords.get("expenseaccount"));
            businessList.add(o2);
        }
        List<CustomEntity> customizeEntities = customEntityService.getAllEntityByCustomFlg(tenantParam.getTenantId(), CustomizeConstant.FLG_TRUE);
        for (CustomEntity customEntity : customizeEntities) {
            JSONObject o = new JSONObject();
            o.put("belongId", customEntity.getId());
            o.put("belongName", customEntity.getLabel());
            businessList.add(o);
        }
        return businessList;
    }

    private boolean hasBusinessLicense(Long funId, TenantParam tenantParam) {
        try {
            return licenseFunctionService.hasFunction(funId, tenantParam);
        } catch (ServiceException e) {
            return false;
        }
    }

    public void updateLicenseEdition(Long tenantId) throws ServiceException {
        //设置版本为专业版
        TenantParam tenantParam = new TenantParam(tenantId);
        License base = licenseService.getBaseLicense(tenantParam);
        base.setEdition(Tenant.EDITION_PROFESSIONAL);
        licenseService.upgrade(base, tenantParam);
    }






}
