package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import cloud.multi.tenant.TenantParam;

import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
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

/**
 * Created by hougx on 2015/12/14.
 */

public class BaseAction // implements Preparable
 {
    private static final Logger LOG = LoggerFactory.getLogger(BaseAction.class);

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String REPEAT_ERROR_CODE="300004";//与openApi约定的错误编码，以后如果openAPI错误编码换掉了，这边也要跟着换

    public static final String PAGES_ROOT = "/WEB-INF/pages";

    private int pageNo = 0;//当前页号

    private int pagesize = 0;//每页显示条数

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

//    public void prepare() throws Exception {
//        进行拦截，使用https
//        urlFilter4Http();
//        进行拦截，如果session过期可以继续登录保持在线
//        doLoginFilter();
//    }
    public String getAccessToken(){
        DingSessionUser dingSessionUser = this.getDingSessionUser();
        String user_token =  null;
        if(dingSessionUser!= null){
            user_token = dingSessionUser.getXsy_user_token();
        }
        LOG.info("user_token={}",user_token);

        return  user_token;//"ea36e02b2e9bc1573837836f18dc733ea48af8f953015f1d97200864c55e94eb";


    }


    public String getUserID(){
        DingSessionUser dingSessionUser =this.getDingSessionUser();
        String xsy_user_id =  null;
        if(dingSessionUser!= null){
            xsy_user_id = dingSessionUser.getXsy_user().getId()+"";
        }
        LOG.info("xsy_user_id={}",xsy_user_id);
        return "46906";//"46906";
    }

    public DingSessionUser getDingSessionUser(){
        DingSessionUser dingSessionUser = (DingSessionUser) getRequest().getSession().getAttribute(Sessionkes.SESSIONUSER.toString());

        //测试阶段使用
//        if(dingSessionUser == null){
//            dingSessionUser =  new DingSessionUser();
//            TenantParam tenantParam = new TenantParam(40512L);
//            XsyUserDto xsyUserDto = new  XsyUserDto();
//            xsyUserDto.setId(71205L);
//            dingSessionUser.setXsy_TenantParam(tenantParam);
//            dingSessionUser.setXsy_user_token("92c0829dcf9058bfdf7e5cf738570c92931054c39d1f179cc9a6756e97783e95");
//            dingSessionUser.setXsy_user(xsyUserDto);
//        }

        return dingSessionUser;
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

//    private void doLoginFilter(){
//        HttpServletRequest request = getRequest();
//        String uri = request.getServletPath();
//        uri = uri.replace(".action","");
//
//        String refer = request.getHeader("referer");
//        LOG.info("refer = "+refer);
//
//        String permissionUri =  ",/dingtalk/eventChangeReceive,/dingtalk/t,/wel/index,/dingtalk/dologin,/dingtalk/login,/dingtalk/user/getuser,/dingtalk/authorized/no,";
//        boolean permission = false;
//        if(permissionUri.indexOf(","+uri+",")>=0){
//            permission = true;
//        }
//        if(!permission){
//            DingSessionUser dingSessionUser = (DingSessionUser) request.getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
//            if(dingSessionUser == null){
//                LOG.error("no  authorized url:"+uri);
//                HttpServletResponse response = getResponse();
//                try {
//                    response.sendRedirect(request.getContextPath()+"/dingtalk/authorized/no.action");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

//    private void urlFilter4Http(){
//        HttpServletRequest request= getRequest();
//
//        String requestUrl = request.getRequestURL().toString();
//
//        requestUrl = requestUrl.replace("http://","https://");
//        request.setAttribute("requestUrl",requestUrl);
//
//    }

    public void setSessionUserRole( DingSessionUser dingSessionUser,long xsy_uid,long  xsy_tenantid) throws PaasException {
        TenantParam tenantParam = new TenantParam(xsy_tenantid);
        // 设置用户角色
        Map<String,com.rkhd.platform.auth.model.Role> roleMap =  commonService.initRoleInfo(tenantParam);

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
