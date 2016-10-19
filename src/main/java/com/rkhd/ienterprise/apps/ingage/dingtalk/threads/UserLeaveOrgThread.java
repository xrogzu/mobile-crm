package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSONArray;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.base.user.model.User;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 钉钉员工离职执行该线程
 */
public class UserLeaveOrgThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(UserLeaveOrgThread.class);

    private UserService userService;

    private String corpId;

    private JSONArray userIds;

    private RelThirdTokenService relThirdTokenService;

    private RelThirdUserXsyUserService relThirdUserXsyUserService;


    public UserLeaveOrgThread(String corpId, JSONArray userIds,UserService userService,RelThirdTokenService relThirdTokenService,
                              RelThirdUserXsyUserService relThirdUserXsyUserService){
        super();
        this.corpId = corpId;
        this.userIds = userIds;
        this.userService = userService;
        this.relThirdTokenService = relThirdTokenService;
        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
    }
    public void run() {
        RelThirdToken relThirdToken = null;
        try {
            relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,corpId, Env.SUITE_KEY);
            if(relThirdToken == null){
                LOG.error("Corpid【"+corpId+"】cannot find relThirdToken");
            }else{
//                String dingAccessToken =  relThirdToken.getTokenValue();
                for(int i=0 ;i<userIds.size();i++){
                    //钉钉用户详情
                    String ding_userid =  userIds.getString(i);
//                    com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User dingUserDetail =  UserHelper.getUser(dingAccessToken,ding_userid );
//                    String ding_id = dingUserDetail.getDingId();
                    //查询映射关系
                    RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByThridCorpidThirdUserId(corpId,ding_userid,Platform.dingding,Env.SUITE_KEY);
                    if(relThirdUserXsyUser != null){
                        TenantParam tenantParam =  new TenantParam(relThirdUserXsyUser.getXsyTenantid());
                        User u = userService.get(relThirdUserXsyUser.getXsyUserId(),tenantParam);
                        if (u.getStatus().equals(User.Status.ACTIVE.getValue())) {
                            //设置用户离职
                            userService.departureUser(u.getId(), tenantParam);
                        }
                    }
                }
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }

}
