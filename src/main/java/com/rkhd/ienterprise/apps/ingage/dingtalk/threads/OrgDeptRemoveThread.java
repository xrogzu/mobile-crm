package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class OrgDeptRemoveThread extends Thread{
    private static final Logger LOG = LoggerFactory.getLogger(OrgDeptRemoveThread.class);

    private String thirdCorpid;

    private JSONArray departmentIds;

    private RelThirdTokenService relThirdTokenService;

    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    private RelThirdUserXsyUserService relThirdUserXsyUserService;



    private OauthTokenService oauthTokenService;

    private DepartmentService departmentService;

    private long xsyTenantid;

    private String xsyBasicAccessToken;

    public OrgDeptRemoveThread(String corpId, JSONArray departmentIds,
                               RelThirdTokenService relThirdTokenService,
                               RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService, RelThirdCorpXsyTenantService relThirdCorpXsyTenantService,
                               RelThirdUserXsyUserService relThirdUserXsyUserService, DepartmentService departmentService,
                               OauthTokenService oauthTokenService){
        super();
        this.thirdCorpid = corpId;
        this.departmentIds = departmentIds;
        this.relThirdTokenService = relThirdTokenService;
        this.relThirdDepXsyDepartmentService = relThirdDepXsyDepartmentService;
        this.relThirdCorpXsyTenantService = relThirdCorpXsyTenantService;
        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
        this.departmentService = departmentService;

        this.oauthTokenService = oauthTokenService;
    }

    public void run(){
        RelThirdToken relThirdToken = null;
        try {
            LOG.info("Corpid["+thirdCorpid+"] dingding have deleted  department {}",thirdCorpid,JSON.toJSON(departmentIds));
            /**
             * 获取租户的accessToken
             */
            relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,thirdCorpid, Env.SUITE_KEY);
            if(relThirdToken == null){
                LOG.error("Corpid["+thirdCorpid+"] can not find relThirdToken ",thirdCorpid);
            }else{
                LOG.info ("Corpid["+thirdCorpid+"] find relThirdToken：{}",thirdCorpid,JSON.toJSONString(relThirdToken));
                //获取租户信息
                RelThirdCorpXsyTenant relThirdCorpXsyTenantrelThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(this.thirdCorpid,Platform.dingding,Env.SUITE_KEY);
                this.xsyTenantid = relThirdCorpXsyTenantrelThirdCorpXsyTenant.getXsyTenantid();
                String dingAccessToken =  relThirdToken.getTokenValue();
                TenantParam  tenantParam = new TenantParam(xsyTenantid);

                //查找管理员
                RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getFirstRelThirdUserXsyUserByCorpidThird(thirdCorpid,Platform.dingding,Env.SUITE_KEY);

                long operatorId = 0;
                long managerId   = 0;
                long beCreateBy = 0;
                operatorId = managerId =  beCreateBy  = relThirdUserXsyUser.getXsyUserId();
                //获取xsyBasicAccessToken
                OauthToken xsyOauthToken =  oauthTokenService.getBasicTokenByUserIdAndTenantId(operatorId,tenantParam);
                this.xsyBasicAccessToken = xsyOauthToken.getAccessToken();

                for(int i=0;i<departmentIds.size();i++){
                    //查询钉钉部门详情
                   // Department dingDepartment =  DepartmentHelper.get(dingAccessToken,departmentIds.getString(i));
                    //查询部门映射关系
                    RelThirdDepXsyDepartment relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,departmentIds.getString(i),Platform.dingding,Env.SUITE_KEY);
                    if(relThirdDepXsyDepartment == null){
                        //没有相关部门
                        LOG.error("Corpid["+this.thirdCorpid+"] do not find relation  dingDepartmentId={}",departmentIds.getString(i));
                    }else {
                        //做修改部门操作
                        LOG.info("Corpid["+this.thirdCorpid+"] find relaton between ding deparment and xiaoshouyi department .it is 【{}】 ",thirdCorpid,JSON.toJSONString(relThirdDepXsyDepartment));
                        EntityReturnData delDepartmentEntityReturnData =  departmentService.deleteDeparment(xsyBasicAccessToken,relThirdDepXsyDepartment.getXsyDepartmentId());
                        LOG.info("Corpid["+thirdCorpid+"] delete ding deparment.it`s id is ["+departmentIds.getString(i)+"] relate xiaoshoyi department`s id is ："+relThirdDepXsyDepartment.getXsyDepartmentId()+".execute result is：{}", JSON.toJSONString(delDepartmentEntityReturnData));
                    }
                }

            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }   catch (Exception e){
            e.printStackTrace();
        }
    }
}
