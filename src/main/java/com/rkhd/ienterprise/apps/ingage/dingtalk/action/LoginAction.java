package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SessionUtils;
import com.rkhd.ienterprise.base.multitenant.model.Tenant;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.platform.exception.PaasException;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2016/1/20.
 */
@Namespace("/dingtalk")
public class LoginAction extends BaseAction{

    private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private OauthTokenService oauthTokenService;

    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    @Action(value = "login", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/dingtalk/login.jsp")})
    public String login() throws Exception{
        return  SUCCESS;
    }
    @Action(value = "dologin",results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String dologin() {
        LOG.info("reach doLogin page ");
        EntityReturnData ret = new EntityReturnData();

        Long startTime = System.currentTimeMillis();
        try {
            String thirdcorpid = getRequest().getParameter("corpid");//第三方企业id
            if(SessionUtils.needReLogin(getRequest())){
                executeLogin(thirdcorpid,ret);
            }else {
                DingSessionUser dingSessionUser = (DingSessionUser) getRequest().getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
                ret.setSuccess(true);
                ret.setEntity(dingSessionUser);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            ret.setEntity(null);
        } catch (Exception e) {
            e.printStackTrace();
            ret.setEntity(null);
        }
        Long endTime = System.currentTimeMillis();
        LOG.info("login consume ：{}ms",(endTime-startTime));
//        LOG.info(JSON.toJSONString(ret));
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    public void executeLogin(String thirdcorpid,EntityReturnData ret) throws OApiException, ServiceException, PaasException {
        RelThirdToken relThirdToken =  relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,thirdcorpid, Env.SUITE_KEY);
        DingSessionUser   dingSessionUser = new DingSessionUser();
        String  ding_access_token = relThirdToken.getTokenValue();

        String code = getRequest().getParameter("code");//授权code
        //从钉钉中拿取登录用户信息
        JSONObject dingUserInfo = UserHelper.getUserInfo(ding_access_token,code);

        String dinguserid = dingUserInfo.getString("userid");//员工在企业内的UserID
        //从钉钉中拿取登录用户详情
        com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User user = UserHelper.getUser(ding_access_token,dinguserid);

        String dingId = user.getDingId();
        dingSessionUser.setDing_access_token(ding_access_token);
        dingSessionUser.setDing_id(dingId);
        dingSessionUser.setDing_userId(dinguserid);
        dingSessionUser.setThirdcorpid(thirdcorpid);
        dingSessionUser.setRelThirdToken(relThirdToken);

        //设置公司名称
        RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(thirdcorpid,Platform.dingding, Env.SUITE_KEY);
        if(relThirdCorpXsyTenant == null) {
            LOG.error("thirdcorpid[" + thirdcorpid + "]not get relThirdCorpXsyTenant with thirdcorpid=" + thirdcorpid + " ;Env.SUITE_KEY=" + Env.SUITE_KEY + " not a real thirdcorp");
        }else {
            Tenant tenant = tenantService.get(relThirdCorpXsyTenant.getXsyTenantid());
            dingSessionUser.setXsy_TenantName(tenant.getCompany());
            //获得用户映射关系
            RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdUserId(thirdcorpid,dinguserid, Platform.dingding, Env.SUITE_KEY);
            if(relThirdUserXsyUser == null) {
                ret.setSuccess(false);
                ret.setMsg("no get ding user relation from relThirdUserXsyUser");
                LOG.error("thirdcorpid[" + thirdcorpid + "]not get relThirdUserXsyUser with thirdcorpid=" + thirdcorpid + " ;Env.SUITE_KEY=" + Env.SUITE_KEY + " not a real thirdcorp");
            } else {
                long xsy_uid = relThirdUserXsyUser.getXsyUserId();
                long  xsy_tenantid = relThirdUserXsyUser.getXsyTenantid();
                TenantParam tenantParam = new TenantParam(xsy_tenantid);
                dingSessionUser.setXsy_TenantParam(tenantParam);
                LOG.info("get oauthToken with xsy_uid={} and xsy_tenantid={}",xsy_uid,xsy_tenantid);
                OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_uid,tenantParam);
                if(oauthToken == null){
                    LOG.error("not get oauthToken with xsy_uid={} and xsy_tenantid={} oauthToken=null ",xsy_uid,xsy_tenantid);
                }else {
                    EntityReturnData userEntityReturnData = xsyApiUserService.getUserInfo(oauthToken.getAccessToken(),xsy_uid);
                    if(userEntityReturnData.isSuccess()){
                        JSONObject xsyUser = (JSONObject) userEntityReturnData.getEntity();
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
                            LOG.error("thirdcorpid[" + thirdcorpid + "]departmentService.getDepartmentById error,token=" + oauthToken.getAccessToken() + " ;departId=" +departId+";departmentEntity="+JSON.toJSONString(departmentEntity));
                        }

                        dingSessionUser.setXsy_user(xsyUserDto);
                        dingSessionUser.setXsy_user_token(oauthToken.getAccessToken());
                        setSessionUserRole(  dingSessionUser, xsy_uid,  xsy_tenantid);
                        ret.setSuccess(true);
                        ret.setEntity(xsyUser);
                        getRequest().getSession().setAttribute(Sessionkes.SESSIONUSER.toString(),dingSessionUser);
                    }else{
                        LOG.error("not get xsyUser from openApi ,xsyaccessToken = "+oauthToken.getAccessToken()+" ,xsy_uid = "+xsy_uid+",result is {} ",JSON.toJSONString(userEntityReturnData));
                    }
                }
            }
        }
//            Long endTime = System.currentTimeMillis();
//            LOG.info("stand login consume：{}ms",(endTime-startTime));

    }


}
