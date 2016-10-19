package com.rkhd.ienterprise.apps.ingage.wx.index.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.apps.ingage.enums.IndexError;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.apps.ingage.wx.interceptors.UrlInterceptor;
import com.rkhd.ienterprise.apps.ingage.wx.isv.action.WxAuthAction;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.model.SynResult;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.ienterprise.thirdparty.service.SynResultService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Namespace("/wx")
public class IndexAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(IndexAction.class);

    private WXApi wxApi =  WXApi.getWxApi();

    public static final  String  indexPath = PAGES_ROOT+"/statics/statics_index.jsp";

    @Autowired
    private UserService userService;

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;


    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;


    @Autowired
    private SynResultService synResultService;

    @Autowired
    private OauthTokenService oauthTokenService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;


    /**
     * http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BA%94%E7%94%A8%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * @return
     */
    @Action(value = "dologin", results = {
            @Result(name = SUCCESS, location = PAGES_ROOT+"/index/index.jsp"),
            @Result(name = "fail", location = PAGES_ROOT+"/login_failure.jsp")
    })
    public String dologin()  {

        SessionUser sessionUser = getSessionUser();
        IndexError  error = null;
        String corpid = getRequest().getParameter("corpid");
//        String auth_code = getRequest().getParameter("auth_code");
//        String expires_in = getRequest().getParameter("expires_in");
        String code = getRequest().getParameter("code");
        String redirectUrl = null;
        JSONObject wxUser = null;
        if(StringUtils.isBlank(code)){
            error = IndexError.NO_CODE;
        }else  {
            //获取企业的永久授权码，终极目标是获取access_token
            String  permanent_code = null;
            try {
                SynResult synResult =  synResultService.getByThirdCorpid(corpid,WXAppConstant.APP_ID, Platform.WEIXIN);
                if( synResult == null  ){
                    error = IndexError.NO_SYN_INFO;
                } else if( "false" == synResult.getSynResult()){

                    error = IndexError.SYN_FAILED;

                }else  if(("true".equals(synResult.getSynResult() ) && "DOING".equals(synResult.getSynErrorMsg()))){

                    error = IndexError.SYN_ING;

                }  else {
                    permanent_code = getPermanent_code(corpid);
                    if(StringUtils.isBlank(permanent_code)){

                        error = IndexError.NO_PERMANENT_CODE;
                    }else{
                        String suitaccess_token = WxRuntime.getSuite_access_token();
                        JSONObject corpToken = wxApi.get_corp_token(suitaccess_token, WXAppConstant.APP_ID,corpid,permanent_code);
                        String access_token = corpToken.getString("access_token");
                        JSONObject userInfo =  wxApi.getUserByCode(access_token,code);

                        if(userInfo.containsKey("UserId")){
//                                LOG.info("userInfo="+userInfo.toJSONString());
                            String userId = userInfo.getString("UserId");
                            wxUser =  wxApi.get_user(access_token,userId);
                            //{"errmsg":"no privilege to access/modify contact/party/agent ","errcode":60011}
                            if(wxUser.containsKey("errcode") && 0 != wxUser.getIntValue("errcode")){

                                error = IndexError.AUTH_ERROR;
                                LOG.error("wxUser={}",wxUser.toJSONString());
                            }else {
                                RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(corpid,Platform.WEIXIN, WXAppConstant.APP_ID);
                                if(relThirdCorpXsyTenant == null){

                                    error = IndexError.NO_RELTHIRDCORPXSYTENANT;
                                }else{
                                    RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdUserId(corpid,userId,Platform.WEIXIN, WXAppConstant.APP_ID);
                                    if(relThirdUserXsyUser == null){

                                        error = IndexError.NO_RELTHIRDUSERXSYUSER;
                                    }else {
                                        Long xsy_uid =  relThirdUserXsyUser.getXsyUserId();
                                        TenantParam tenantParam = new TenantParam(relThirdCorpXsyTenant.getXsyTenantid());
                                        OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_uid,tenantParam);
                                        if(oauthToken == null){

                                            error = IndexError.NO_OAUTHTOKEN;
                                            LOG.error("not get oauthToken with xsy_uid={} and xsy_tenantid={} oauthToken=null ",xsy_uid,relThirdCorpXsyTenant.getXsyTenantid());
                                        }else{
                                            String xsy_assection = oauthToken.getAccessToken();
                                            EntityReturnData userEntityReturnData = xsyApiUserService.getUserInfo(oauthToken.getAccessToken(),xsy_uid);
                                            if(userEntityReturnData == null){

                                                error = IndexError.NO_GET_XSY_USER;
                                                LOG.error("not get xsyUser with accessToken={} and xsy_uid={},userEntityReturnData is  null",oauthToken.getAccessToken(),xsy_uid);

                                            }else if(!userEntityReturnData.isSuccess()){
                                                String errorCode = userEntityReturnData.getErrorCode();
                                                if("110010".equals(errorCode)){
                                                    LOG.error("无效的access token;token="+oauthToken.getAccessToken());

                                                }else  if("110011".equals(errorCode)){
                                                    LOG.error("用户没有访问权限;token="+oauthToken.getAccessToken());
                                                    error = IndexError.NO_LICENESE;
                                                }else   if("110012".equals(errorCode)){
                                                    LOG.error("license已经过期;token="+oauthToken.getAccessToken());
                                                    error = IndexError.LICENESE_EXPIRE;
                                                }else   if("110013".equals(errorCode)){
                                                    LOG.error("没有license授权;token="+oauthToken.getAccessToken());
                                                    error = IndexError.LICENESE_NO_GET_AUTHORIZATION;
                                                }
                                                if(error == null){
                                                    error = IndexError.CONTACT_US;
                                                }
                                                LOG.error("not get xsyUser with accessToken={} and xsy_uid={},userEntityReturnData is  "+JSON.toJSONString(userEntityReturnData),oauthToken.getAccessToken(),xsy_uid);
                                            }else{
                                                JSONObject xsyUser = (JSONObject) userEntityReturnData.getEntity();
                                                //更新用户状态为已激活状态
                                                if( xsyUser.getIntValue("statusInt") == User.Status.INACTIVE.getValue()){
                                                    xsyUser.put("status","1");
                                                    xsyApiUserService.updateUser(oauthToken.getAccessToken(),xsyUser);
                                                }


                                                String jsonString = JSON.toJSONString(xsyUser);

                                                // JSON串转用户组对象
                                                XsyUserDto xsyUserDto =JSON.parseObject(jsonString, XsyUserDto.class);
                                                long departId = xsyUserDto.getDepartId();
                                                EntityReturnData departmentEntity = departmentService.getDepartmentById(oauthToken.getAccessToken(),departId);
                                                if(departmentEntity.isSuccess()){
                                                    JSONObject department  = (JSONObject) departmentEntity.getEntity();
                                                    String departmetnName = department.getString("departName");
                                                    xsyUserDto.setDepartmentName(departmetnName);
                                                }else{
                                                    LOG.error("corpid[" + corpid + "]departmentService.getDepartmentById error,token=" + oauthToken.getAccessToken() + " ;departId=" +departId+";departmentEntity="+JSON.toJSONString(departmentEntity));
                                                }

                                                sessionUser = new SessionUser();
                                                sessionUser.setXsy_TenantParam(tenantParam);
                                                sessionUser.setThirdcorpid(corpid);
                                                sessionUser.setXsy_user_token(xsy_assection);
                                                sessionUser.setXsy_user(xsyUserDto);
                                                setSessionUserRole(  sessionUser, xsy_uid,  tenantParam.getTenantId());

                                                String jsTicket =   wxApi.getJSTicket(access_token) ;
                                                sessionUser.setJsticket(jsTicket);
                                                getRequest().getSession().setAttribute(WxBaseAction.wxSessionUserKey,sessionUser);

                                            }
                                        }
                                    }
                                }
                            }
                            LOG.info("wxUser={}",wxUser.toJSONString());
                        }else {
                            LOG.error("userInfo="+ JSON.toJSONString(userInfo));
                        }

                    }
                }
                if(error != null && corpid != null ){
                    LOG.error("corpid="+corpid+";error="+JSON.toJSONString(error));
                }
            } catch (WXException e) {

                error = IndexError.WEI_XIN_API_ERROR;
                LOG.error("wx api error ");
                e.printStackTrace();
            } catch (ServiceException e) {

                error = IndexError.XSY_SERVICE_ERROR;
                LOG.error("xsy service error ,msg:"+e.getMessage());
                e.printStackTrace();
            } catch (PaasException e) {
                error = IndexError.CONTACT_US;
                LOG.error("parse error,msg = "+e.getMessage());
                e.printStackTrace();
            }
        }


        if(sessionUser != null)
        {
            return SUCCESS;
        }else {
            getRequest().setAttribute("LoginError",error);
            return "fail";
        }

    }
    @Action(value = "index", results = {
            @Result(name = SUCCESS, location = PAGES_ROOT+"/login.jsp"),
            @Result(name = "have_user",  location = indexPath)
    })
    public String Index()  {
        SessionUser sessionUser = getSessionUser();
        boolean result = false;
        IndexError  error = null;
        String corpid = getRequest().getParameter("corpid");
        String sessionCorpid = (String) getRequest().getSession().getAttribute("corpid");
        boolean needReLogin = true;
        //不同微信企业，且sessionUser不为空时可以不进行登陆
        //反过来就是同一个微信企业且sessionUser不为空时可以不进行登陆
        if(StringUtils.isNotBlank(corpid) &&  StringUtils.isNotBlank(sessionCorpid)){
            if(sessionUser!= null &&  corpid.equals(sessionCorpid)){
                needReLogin = false;
            }
        }
        getRequest().getSession().setAttribute("corpid",corpid);

        if(needReLogin ){

            String redirectUrl = null;
            String host = UrlInterceptor.getHostUrl();
            String redirect_uri = host+getRequest().getContextPath()+"/wx/dologin.action?corpid="+corpid;
            try {
                redirect_uri = URLEncoder.encode(redirect_uri, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + corpid + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
            LOG.info("redirectUrl="+redirectUrl);
            getRequest().setAttribute("corpid",corpid);
            getRequest().setAttribute("result",result);
            getRequest().setAttribute("error",error);
            getRequest().setAttribute("redirectUrl",redirectUrl);
            return SUCCESS;

        }else{
            return "have_user";

        }

    }
}
