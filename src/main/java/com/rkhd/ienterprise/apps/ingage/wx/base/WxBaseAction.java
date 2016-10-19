package com.rkhd.ienterprise.apps.ingage.wx.base;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SetSystemProp;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.auth.model.UserRole;
import com.rkhd.platform.auth.service.UserRoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(WxBaseAction.class);

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Autowired
    private RelThirdTokenService relThirdTokenService;


    public static final String wxSessionUserKey = "wxSessionUser";

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String REPEAT_ERROR_CODE="300004";//与openApi约定的错误编码，以后如果openAPI错误编码换掉了，这边也要跟着换

    public static final String PAGES_ROOT = "/WEB-INF/pages/wx";
    public static final String REQUEST_HEADER_USER_AGENT = "User-Agent";
    public static final String LOG_LOGIN_MODEL = "PC";
    public static final String LOG_LOGIN_SOURCE = "3";

    private int pageNo = 0;//当前页号

    private int pagesize = 0;//每页显示条数

    private String scene;//场景


    @Autowired
    @Qualifier("mwebCommonService")
    private CommonService commonService;

    @Autowired
    private UserRoleService userRoleService;

    protected HttpServletRequest getRequest() {

        return ServletActionContext.getRequest();
    }

    protected HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    protected ServletContext getServletContext() {
        return ServletActionContext.getServletContext();
    }


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPagesize() {
        if(pagesize == 0){
            pagesize = XsyUtils.defalut_page_limit;
        }
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }


    public void setSessionUser(SessionUser sessionUser) {
        getRequest().getSession().setAttribute(wxSessionUserKey,sessionUser);
    }

    public SessionUser getSessionUser() {
        SessionUser sessionUser = (SessionUser) getRequest().getSession().getAttribute(wxSessionUserKey);
//        sessionUser = new SessionUser();
//        sessionUser.setThirdcorpid("wx7409b34a733c7e82");
//        TenantParam tenantParam = new TenantParam(102520L);
//        sessionUser.setXsy_TenantParam(tenantParam);
//        XsyUserDto xsyUserDto = new XsyUserDto();
//        xsyUserDto.setId(93741L);
//        xsyUserDto.setDepartId(171709);
//        xsyUserDto.setName("徐。。");
//        xsyUserDto.setPhone("15001279241");
//        sessionUser.setXsy_user(xsyUserDto);

        return  sessionUser;
    }


    public String getAccessToken(){
//      return "97c2c16db166d95712e022c3b6c09f26985a389d69ad04eed3a28d744f6cfaf7";
        SessionUser sessionUser = this.getSessionUser();
        if(sessionUser == null){
            return null;
        }else {
            return sessionUser.getXsy_user_token();
        }
    }

    public String getUserID(){
        SessionUser sessionUser = this.getSessionUser();
        if(sessionUser == null){
            return null;
        }else {
            return sessionUser.getXsy_user().getId()+"";
        }
    }

    public String getPermanent_code(String corpid) throws ServiceException {
        RelThirdToken relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.WEIXIN,corpid, WXAppConstant.APP_ID);
        if(relThirdToken == null){
            return null;
        }
        //String  permanent_code = SetSystemProp.getKeyValue("permanent_code"+corpid);
        return relThirdToken.getPermanentCode();
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public void setSessionUserRole(SessionUser dingSessionUser, long xsy_uid, long  xsy_tenantid) throws PaasException {
        TenantParam tenantParam = new TenantParam(xsy_tenantid);
        // 设置用户角色
        Map<String,Role> roleMap =  commonService.initRoleInfo(tenantParam);

        Long  normalRoleId = roleMap.get(SyncAddressBookeThread.ROLE_NORMAL_KEY).getId();
        Long  managerRoleId = roleMap.get(SyncAddressBookeThread.ROLE_MANAGER_KEY).getId();
        Long  systemManagerRoleId = roleMap.get(SyncAddressBookeThread.ROLE_SYSTEM_MANAGER_KEY).getId();

        List<UserRole> userRoles =  userRoleService.getListByUserId(xsy_uid,tenantParam);
        UserRole userRole = null;
        boolean isNormal = false;
        boolean isManager = false;
        boolean isSystemManager = false;
        for(UserRole item : userRoles){
            if(userRole == null){
                if(item.getRoleId().longValue() == normalRoleId){
                    isNormal = true;
                }else if(item.getRoleId().longValue() == managerRoleId){
                    isManager = true;
                }else if(item.getRoleId().longValue() == systemManagerRoleId){
                    isSystemManager = true;
                }
            }else {
                if(isNormal){
                    if(item.getRoleId().longValue() == managerRoleId){
                        isNormal = false;
                        isManager = true;
                    }else if(item.getRoleId().longValue() == systemManagerRoleId){
                        isNormal = false;
                        isManager = false;
                        isSystemManager = true;
                    }
                }else if(isManager){
                    if(item.getRoleId().longValue() == systemManagerRoleId){
                        isNormal = false;
                        isManager = false;
                        isSystemManager = true;
                    }
                }
            }
            if(isSystemManager){
                break;
            }
        }
        dingSessionUser.setManager(isManager);
        dingSessionUser.setNormal(isNormal);
        dingSessionUser.setSystemManager(isSystemManager);
    }
}
