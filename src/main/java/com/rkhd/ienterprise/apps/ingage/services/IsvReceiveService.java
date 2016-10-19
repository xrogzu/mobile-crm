package com.rkhd.ienterprise.apps.ingage.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service.ServiceHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.RegisterCallBackThread;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.service.*;
import com.rkhd.platform.auth.service.ResponsibilityService;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.exception.PaasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2015/12/24.
 */
@Component
public class IsvReceiveService {
    private static final Logger LOG = LoggerFactory.getLogger(IsvReceiveService.class);
    @Autowired
    private PassportService passportService;

    @Autowired
    private TenantService tenantService;


    @Autowired
    private UserService userService;


    @Autowired
    private PersonalProfileService personalProfileService;

    @Autowired
    private DingService dingService;

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Autowired
    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    @Autowired
    private OauthTokenService oauthTokenService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResponsibilityService responsibilityService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    @Autowired
    @Qualifier("mwebCommonService")
    private CommonService commonService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;


    private Map<String ,Boolean> temCache = new HashMap<String, Boolean>();
    /**
     * 企业授权时执行
     * @param  tmp_auth_code 临时授权码
     * @return
     */
    public EntityReturnData onTmp_auth_code(String tmp_auth_code, String requestUrl, String contextPath) throws ServiceException, OApiException, PaasException {
        try{
        if(temCache.containsKey(tmp_auth_code)){
            return null;
        }
        temCache.put(tmp_auth_code,true);

        //获得企业信息
        JSONObject permanentJson = ServiceHelper.getPermanentCode(tmp_auth_code,  dingService.getSuiteAccessToken());
        LOG.info("getPermanentCode data is :"+permanentJson);
        //企业信息
        JSONObject auth_corp_info = permanentJson.getJSONObject("auth_corp_info");
        // 授权方企业id
        String  thirdCorpid = auth_corp_info.getString("corpid");
//        String corpName = auth_corp_info.getString("corp_name");
        String permanent_code = permanentJson.getString("permanent_code");//永久授权码。真实开发中，请务必将corpId和permanent_code做持久存储

            //获取企业授权的授权数据
        JSONObject  authInfo = ServiceHelper.getAuthInfo(dingService.getSuiteAccessToken(), Env.SUITE_KEY,thirdCorpid,permanent_code);
        LOG.info(" be Authorized Enterprise info is ："+ JSON.toJSONString(authInfo));

//            获取企业授权的access_token
        String thirdCorpidAccessToken =  ServiceHelper.getCorpToken(thirdCorpid,permanent_code,dingService.getSuiteAccessToken());
        LOG.info("be Authorized Enterprise`access_token from dingding is :"+thirdCorpidAccessToken);
            /**
             * 同步通讯录
              */
            new  SyncAddressBookeThread( relThirdDepXsyDepartmentService
                    , relThirdCorpXsyTenantService,  departmentService,
                     passportService,  relThirdPassportXsyPassportIdService,
                     commonService,  tenantService,  relThirdUserXsyUserService,
                     userService,  personalProfileService,
                    dingService,   oauthTokenService,
                     responsibilityService,  roleService,
                     auth_corp_info, thirdCorpidAccessToken, permanent_code,  authInfo).start();

            /**
             * 注册回调信息
             */
            new RegisterCallBackThread(requestUrl,thirdCorpidAccessToken,thirdCorpid,contextPath).start();


            temCache.remove(tmp_auth_code);

             EntityReturnData ret = new EntityReturnData();
             ret.setSuccess(true);
             return ret;
        }catch (Exception e){
            throw new ServiceException(e);
        }
    }



}