package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSONArray;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.exception.PaasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 事件监听
 * 当添加用户时触发
 */
public class UserAddOrgThread extends Thread{
    private static final Logger LOG = LoggerFactory.getLogger(UserAddOrgThread.class);

    private String thirdCorpid;

    private JSONArray userIds;

    private RelThirdTokenService relThirdTokenService;

    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    private CommonService commonService;


    public UserAddOrgThread(String thirdCorpid, JSONArray userIds, RelThirdTokenService relThirdTokenService, CommonService commonService,RelThirdCorpXsyTenantService relThirdCorpXsyTenantService){
        super();
        this.thirdCorpid = thirdCorpid;
        this.userIds = userIds;
        this.relThirdTokenService = relThirdTokenService;
        this.commonService = commonService;
        this.relThirdCorpXsyTenantService = relThirdCorpXsyTenantService;
    }
    public void run(){
        try {
            //查看企业映射关系，如果企业存在才能继续向下进行
            RelThirdCorpXsyTenant relThirdCorpXsyTenant =  relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(thirdCorpid,Platform.dingding, Env.SUITE_KEY);

            if(relThirdCorpXsyTenant == null){
                LOG.error("corpId【{}】 can not find relation between dingding corp and xiaoshou yi",thirdCorpid);//无法查到钉钉企业与销售易的映射关系
            }else{
                RelThirdToken relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,thirdCorpid, Env.SUITE_KEY);
                String dingAccessToken =  relThirdToken.getTokenValue();

               long xsyTenantid =  relThirdCorpXsyTenant.getXsyTenantid();
                TenantParam  tenantParam = new TenantParam(xsyTenantid);
                //准备角色参数
                Map<String,com.rkhd.platform.auth.model.Role> roleMap =  commonService.initRoleInfo(tenantParam);
                Map<String,Responsibility> responsibilityMap = commonService.initResponsibilityInfo(tenantParam);

                for(int i=0 ;i<userIds.size();i++){
                    //钉钉用户详情
                    String ding_userid =  userIds.getString(i);
                    com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUserDetail =  UserHelper.getUser(dingAccessToken,ding_userid );
                    //执行同步用户信息
                    commonService.doSyncDingUser2Xsy(dingUserDetail,this.thirdCorpid,tenantParam,roleMap,responsibilityMap,true);
                }

            }
        } catch (ServiceException e) {
            LOG.error("Corpid["+this.thirdCorpid+"] {}",e.getMessage());
            e.printStackTrace();
        } catch (OApiException e) {
            LOG.error("Corpid["+this.thirdCorpid+"] {}",e.getMessage());
            e.printStackTrace();
        } catch (PaasException e) {
            LOG.error("Corpid["+this.thirdCorpid+"] {}",e.getMessage());
            e.printStackTrace();
        }catch (Exception e) {
            LOG.error("Corpid["+this.thirdCorpid+"] {}",e.getMessage());
            e.printStackTrace();
        }
    }


}
