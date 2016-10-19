package com.rkhd.ienterprise.apps.ingage.wx.pc_admin.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.enums.PcAdminErrorEnum;
import com.rkhd.ienterprise.apps.ingage.global.dto.Login;
import com.rkhd.ienterprise.apps.ingage.global.util.UserAgent;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.model.SalesParameter;
import com.rkhd.ienterprise.apps.isales.setting.salesbusiness.service.SalesParameterService;
import com.rkhd.ienterprise.base.security.loginlog.model.LoginLog;
import com.rkhd.ienterprise.base.security.loginlog.service.LoginLogService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.exception.WebException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.model.SynResult;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import com.rkhd.ienterprise.util.LogSendAgent;
import com.rkhd.ienterprise.util.StringUtil;
import com.rkhd.ienterprise.web.RequestUtil;
import com.rkhd.ienterprise.web.WebConstants;
import com.rkhd.ienterprise.web.WebCookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.LoginException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

@Namespace("/wx/pcadmin")
public class WxPcAdminAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(WxPcAdminAction.class);

    private WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private SynResultService synResultService;

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    @Autowired
    private SalesParameterService salesParameterService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 参考文档：
     * 1：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E5%90%8E%E5%8F%B0%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95
     * 2：http://qydev.weixin.qq.com/wiki/index.php?title=%E6%88%90%E5%91%98%E7%99%BB%E5%BD%95%E6%8E%88%E6%9D%83
     * 实现步骤
     * 1:通过《管理后台单点登录》说明里获得auth_code
     * 2：通过《管理后台单点登录》说明里“获取应用提供商凭证”
     * 3：通过《成员登录授权》说明里"获取企业号登录用户信息"获得登录用户信息
     * @return
     * @throws Exception
     */
    @Action(value = "admin", results = {
            @Result(name = SUCCESS,  location = PAGES_ROOT+"/pcadmin/pc_admin_ok.jsp"),
            @Result(name = "fail", location = PAGES_ROOT+"/pcadmin/pc_admin_failed.jsp")
    })
    public String pcadmin()  {
        String method = getRequest().getMethod();
        LOG.info("method="+method);
        Enumeration<String> enumeration = getRequest().getParameterNames();
        while(enumeration.hasMoreElements()){
            String em = enumeration.nextElement();
            LOG.info("{}={}",em,getRequest().getParameter(em));
        }
        PcAdminErrorEnum error = null;
        String auth_code = getRequest().getParameter("auth_code");
        if(StringUtils.isBlank(auth_code)){
            error = PcAdminErrorEnum.NO_AUTH_CODE;
            LOG.error("not get auth_code");
        }else{
            try {
                LOG.info("WXAppConstant.CorpID="+WXAppConstant.CorpID);
                LOG.info("WXAppConstant.providersecret="+WXAppConstant.providersecret);

                String provider_access_token  = wxApi.getProviderTokenRequest(WXAppConstant.CorpID,WXAppConstant.providersecret);
                if(StringUtils.isBlank(provider_access_token)){
                    error = PcAdminErrorEnum.NO_PROVIDER_ACCESS_TOKEN;
                    LOG.error("not get provider_access_token");
                }else {
                    JSONObject loginUserInfo = wxApi.getLoginInfo(provider_access_token,auth_code);

                    LOG.info("loginUserInfo="+JSON.toJSONString(loginUserInfo));
                    if(loginUserInfo == null){
                        error = PcAdminErrorEnum.NO_GET_LOGIN_INFO;
                    }else{
                        int usertype = loginUserInfo.getIntValue("");
                        JSONObject corp_info = loginUserInfo.getJSONObject("corp_info");
                        JSONObject user_info = loginUserInfo.getJSONObject("user_info");

                        String corpid = corp_info.getString("corpid");
                        String userid = user_info.getString("userid");
                        String email =  user_info.getString("email");
                        String mobile =  user_info.getString("mobile");

                        SynResult synResult =  synResultService.getByThirdCorpid(corpid,WXAppConstant.APP_ID, Platform.WEIXIN);
                        if( synResult == null  ){
                            error = PcAdminErrorEnum.NO_SYN_INFO;
                        } else if( "false" == synResult.getSynResult()){
                            error = PcAdminErrorEnum.SYN_FAILED;
                        }else  if(("true".equals(synResult.getSynResult() ) && "DOING".equals(synResult.getSynErrorMsg()))){
                            error = PcAdminErrorEnum.SYN_ING;
                        }  else {

                            RelThirdUserXsyUser relThirdUserXsyUser =  null ;
                            if(StringUtils.isNotBlank(userid)){
                                relThirdUserXsyUser =  relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdUserId(corpid,userid,Platform.WEIXIN,WXAppConstant.APP_ID);
                            }else   if(StringUtils.isNotBlank(email)){
                                relThirdUserXsyUser = relThirdUserXsyUserService.getByThirdUserEmailAndCorpid(email,corpid,Platform.WEIXIN,WXAppConstant.APP_ID);
                            }if(StringUtils.isNotBlank(mobile)){
                                relThirdUserXsyUser = relThirdUserXsyUserService.getByThirdUserPhoneAndCorpid(mobile,corpid,Platform.WEIXIN,WXAppConstant.APP_ID);
                            }

                            if(relThirdUserXsyUser == null){
                                error = PcAdminErrorEnum.NO_USER_RELATION;
                            }else{
                                long  passportId = relThirdUserXsyUser.getXsyPassportId();
                                long  tenantParamId = relThirdUserXsyUser.getXsyTenantid();
                                TenantParam tenantParam  = new TenantParam(tenantParamId);
                                doEntry(passportId,tenantParam);
                            }
                        }
                    }
                }
            } catch (WXException e) {
                e.printStackTrace();
                error = PcAdminErrorEnum.NO_PROVIDER_ACCESS_TOKEN;
                LOG.error("not get provider_access_token");
            } catch (ServiceException e) {
                e.printStackTrace();
                error = PcAdminErrorEnum.CONTACT_US;
            } catch (Exception e) {
                error = PcAdminErrorEnum.CONTACT_US;
                e.printStackTrace();
            }
        }
        if(error == null ){
            return SUCCESS;
        }else{
            getRequest().setAttribute("error",error);
            return "fail";
        }
    }
    public void doEntry(long passportId,TenantParam tenantParam) throws Exception {

            User checkUser = userService.getByPassportIdAndTenantId(passportId, tenantParam);
            prepareLogin(passportId,tenantParam,checkUser);
        //标识用户为微信用户
            rememberWexinUser(checkUser);
            //Web登陆日志实现
            loginLog(checkUser.getId(), tenantParam.getTenantId());
    }

    /**
     * 登陆前cookie设置和日志记录
     *
     * @param
     * @return
     * @throws WebException
     */
    public void prepareLogin(long passportId,TenantParam tenantParam,User checkUser) throws Exception {


//            WebCookieUtil.loginCookie(passport.getId(), tenantId, -1, getServletContext(), getRequest(), getResponse());
            //取后台session过期设置
            Long sessionTime = this.sessionTimeControl(tenantParam);
            WebCookieUtil.sessionLoginCookie(passportId, tenantParam.getTenantId(), sessionTime,
                    getServletContext(), getRequest(), getResponse());

            if (checkUser.getStatus() == User.Status.INACTIVE.getValue()) {
                checkUser.setStatus(User.Status.ACTIVE.getValue());
                userService.update(checkUser, tenantParam);
                //激活新增用户
                //groupMemberService.upadateStatus(checkUser.getId(), (short) 0, new TenantParam(tenantId));
            }
            userService.updateLastestLoginAtByPassportIdAndTenantId(passportId,tenantParam);
            LOG.info("checkUser="+ JSON.toJSONString(checkUser));

            LogSendAgent logAppendAgent = new LogSendAgent("login");
            Login login = getLogin(checkUser,tenantParam.getTenantId());
            logAppendAgent.asynSend(login);



    }

    private Long sessionTimeControl(TenantParam tenantParam) {
        Long sessionTime = null;
        try {
            List<SalesParameter> salesParameterList = salesParameterService.getSalesParameterByType(SalesParameter.SaleType.PARAMETER_TYPE_BROWSER_SESSION_CHECKED.getValue(), tenantParam);

            if (null != salesParameterList && salesParameterList.size() > 0) {
                SalesParameter salesParameter = salesParameterList.get(0);
                if (null != salesParameter && StringUtil.isNotEmpty(salesParameter.getParameterName())) {
                    sessionTime = Long.valueOf(salesParameter.getParameterName());
                }
            }
        } catch (Exception e) {
            LOG.info("sessionTimeControl Error Message" + e.getMessage(), e);
        } finally {
            return null != sessionTime ? sessionTime : 30 * 24 * 60 * 60L;//默认30天，单位s
        }
    }

    private Login getLogin( User checkUser,long tenantId) throws LoginException {
        Login login = new Login();
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(getRequest().getHeader(REQUEST_HEADER_USER_AGENT));
            if (userAgent != null) {
                if (userAgent.getBrowser() != null) {
                    login.setOs(userAgent.getBrowser().getName());
                }
                if (userAgent.getBrowserVersion() != null) {
                    login.setAppVersion(userAgent.getBrowserVersion().getVersion() != null ? userAgent.getBrowserVersion().getVersion() : "");
                }
            }
        } catch (Exception e) {
            //ignore
        }
        login.setUserId(checkUser.getId());
        login.setCreatedAt(System.currentTimeMillis());
        login.setModel(LOG_LOGIN_MODEL);
        login.setSource(LOG_LOGIN_SOURCE);
        login.setTenantId(tenantId);
        return login;
    }

    /**
     * 记录登陆日志
     */
    private void loginLog(Long userId, Long tenantId) throws ServiceException {
        LoginLog loginLog = new LoginLog();

        loginLog.setUserId(userId);
        loginLog.setIp(RequestUtil.getClientIpAddress(getRequest()));
        // 经度
        // 纬度
        loginLog.setDevice(3);
        loginLog.setSystemVersion(RequestUtil.getRemoteOs(getRequest()));
        // 软件版本
        Map<String, String> browserMap = RequestUtil.getBrowserVersion(getRequest());
        String browser = RequestUtil.getRemoteBrowser(getRequest());
        if (StringUtil.isNotEmpty(browser) && null != browserMap && StringUtil.isNotEmpty(browserMap.get("type")) && browser.equals(browserMap.get("type"))) {
            browser = browser + " " + browserMap.get("version");
        }
        loginLog.setTerminalVersion(browser);
        loginLog.setCreatedAt(System.currentTimeMillis());
        loginLog.setCreatedBy(userId);
        loginLog.setTenantId(tenantId);

        loginLogService.save(loginLog, new TenantParam(tenantId));
    }

    private void rememberWexinUser( User checkUser) {
        Cookie c = new Cookie("wexinuserId", ""+checkUser.getId());
        String domain = getServletContext().getInitParameter(WebConstants.SERVLET_CONTEXT_INIT_PARAM_COOKIE_DOMAIN);
        if (StringUtil.isEmpty(domain)) {
            domain = WebCookieUtil.mainDomain(getRequest());
        }
        c.setDomain(domain);
        c.setMaxAge(7 * 24 * 3600); //7 day
        c.setPath("/");
        getResponse().addCookie(c);
    }
}
