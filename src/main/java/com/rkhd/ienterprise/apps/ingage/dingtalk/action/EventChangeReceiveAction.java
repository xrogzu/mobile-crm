package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.aes.DingTalkEncryptException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.aes.DingTalkEncryptor;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.*;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.thirdparty.service.*;
import com.rkhd.platform.auth.service.ResponsibilityService;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.auth.service.UserRoleService;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by hougx on 2016/1/27.
 */
@Namespace("/dingtalk")
public class EventChangeReceiveAction extends BaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(EventChangeReceiveAction.class);


    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;
    @Autowired
    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;

    @Autowired
    private PassportService passportService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResponsibilityService responsibilityService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RelThirdTokenService relThirdTokenService;
    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;
    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private OauthTokenService oauthTokenService;

    @Autowired
    private TenantService tenantService;
    @Autowired
    private DepartService departService;
    @Autowired
    private PersonalProfileService profileService;


    /**
     * 应用回调函数
     * @return
     * @throws Exception
     */
    @Action(value = "eventChangeReceive", results = {@Result(name = SUCCESS, location = "/WEB-INF/pages/common/infoPage.jsp")})
    public String eventChangeReceive() throws Exception{

        /**url中的签名**/
        String msgSignature = getRequest().getParameter("signature");
        /**url中的时间戳**/
        String timeStamp = getRequest().getParameter("timestamp");
        /**url中的随机字符串**/
        String nonce = getRequest().getParameter("nonce");

        /**post数据包数据中的加密数据**/
        ServletInputStream sis =  getRequest().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(sis));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        JSONObject jsonEncrypt = JSONObject.parseObject(sb.toString());
        String encrypt = jsonEncrypt.getString("encrypt");


        /**对encrypt进行解密**/
        DingTalkEncryptor dingTalkEncryptor = null;
        String plainText = null;
        boolean haveError  = false;
        try {
            //对于DingTalkEncryptor的第三个参数，ISV进行配置的时候传对应套件的SUITE_KEY，普通企业传Corpid
            dingTalkEncryptor = new DingTalkEncryptor(Env.TOKEN, Env.ENCODING_AES_KEY, Env.SUITE_KEY.length()>0 ? Env.SUITE_KEY : Env.CORP_ID);

            plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
        } catch (DingTalkEncryptException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            LOG.error("Env.TOKEN="+Env.TOKEN+";Env.ENCODING_AES_KEY="+Env.ENCODING_AES_KEY+";Env.SUITE_KEY="+Env.SUITE_KEY);
            e.printStackTrace();
            haveError = true;
        }
        String res = "success";
        if(!haveError){
            /**对从encrypt解密出来的明文进行处理**/
            JSONObject plainTextJson = JSONObject.parseObject(plainText);
            String eventType = plainTextJson.getString("EventType");
            LOG.info("eventType={}",eventType);

            LOG.info("plainTextJson={}",plainTextJson.toJSONString());
            if("user_add_org".equalsIgnoreCase(eventType) || "user_modify_org".equalsIgnoreCase(eventType)){//用户添加
                String thirdCorpid = plainTextJson.getString("CorpId");
                JSONArray userIds  = plainTextJson.getJSONArray("UserId");
                new UserAddOrgThread( thirdCorpid,  userIds,  relThirdTokenService,  commonService, relThirdCorpXsyTenantService)
                        .run();

            } else if("user_leave_org".equalsIgnoreCase(eventType)){
                String corpId = plainTextJson.getString("CorpId");
                JSONArray userIds  = plainTextJson.getJSONArray("UserId");
                new UserLeaveOrgThread(corpId,userIds,userService,relThirdTokenService,relThirdUserXsyUserService).run();

            }else if("org_admin_add".equalsIgnoreCase(eventType)){//通讯录用户被设为管理员
                LOG.info("org_admin_add ,{}",plainTextJson.toJSONString());
                JSONArray userIds  = plainTextJson.getJSONArray("UserId");
                String corpId = plainTextJson.getString("CorpId");
//
                new OrgAdminAddThread(corpId,userIds,relThirdTokenService,commonService,relThirdCorpXsyTenantService).run();


            }else if("org_admin_remove".equalsIgnoreCase(eventType)){//通讯录用户被取消设置管理员
                LOG.info("org_admin_remove ,{}",plainTextJson.toJSONString());
                JSONArray userIds  = plainTextJson.getJSONArray("UserId");
                String corpId = plainTextJson.getString("CorpId");
                new OrgAdminAddThread(corpId,userIds,relThirdTokenService,commonService,relThirdCorpXsyTenantService).run();

            }else if("org_dept_create".equalsIgnoreCase(eventType) || "org_dept_modify".equalsIgnoreCase(eventType)){
                String corpId = plainTextJson.getString("CorpId");
                JSONArray departmentIds  = plainTextJson.getJSONArray("DeptId");
                new OrgDeptAddUpdateThread(corpId,departmentIds,relThirdTokenService,
                        relThirdDepXsyDepartmentService,relThirdCorpXsyTenantService,
                        departmentService, commonService, oauthTokenService, relThirdUserXsyUserService, departService, profileService, userRoleService).run();

            } else if("org_dept_remove".equalsIgnoreCase(eventType)){
                String corpId = plainTextJson.getString("CorpId");
                JSONArray departmentIds  = plainTextJson.getJSONArray("DeptId");
                new OrgDeptRemoveThread(corpId,departmentIds,relThirdTokenService,
                        relThirdDepXsyDepartmentService,relThirdCorpXsyTenantService,
                        relThirdUserXsyUserService, departmentService,oauthTokenService).run();

            }else if("check_url".equalsIgnoreCase(eventType)){//测试回调url,在注册时会回调

            }
        }else {
            res = "failure";
        }
        /**对返回信息进行加密**/
        long timeStampLong = Long.parseLong(timeStamp);
        Map<String,String> jsonMap = null;
        try {
            jsonMap = dingTalkEncryptor.getEncryptedMap(res, timeStampLong, nonce);
        } catch (DingTalkEncryptException e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.putAll(jsonMap);
        // getResponse().getWriter().append(json.toString());
        LOG.info("返回结果为：{}", JSON.toJSONString(json));
        getRequest().setAttribute("returnStr",json.toJSONString());
        return SUCCESS;
    }

}
