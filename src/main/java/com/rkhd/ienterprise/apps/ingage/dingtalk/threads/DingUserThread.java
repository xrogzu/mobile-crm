package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.Department;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.DepartmentHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdPassportXsyPassportIdService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步钉钉部门成员到销售易
 *
 * 有三处涉及到添加修改用户的地方。分别是：
 *
 *   DingUserThread：同步普通用户
 *   SyncAddressBookeThread：同步管理员用户
 *   UserAddOrgThread:添加修改用户
 */
public class DingUserThread {
    private static final Logger LOG = LoggerFactory.getLogger(DingUserThread.class);

    private String thirdCorpid;


    private String thirdCorpidAccessToken;

    /**
     * 销售易租户ID
     */
    private long xsyTenantid;

    RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;

    private CommonService commonService;

    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    private UserService userService;

    private TenantParam  tenantParam = null;

    //为删除多余做准备
    Map<String,Boolean> dingExitUserMap = new HashMap<String,Boolean>();



    public DingUserThread(String thirdCorpid,  String thirdCorpidAccessToken,
                          long xsyTenantid,
                          RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService
                         ,  CommonService commonService,
                          RelThirdUserXsyUserService relThirdUserXsyUserService,
                          UserService userService ){
        super();
        this.thirdCorpid = thirdCorpid;
        this.thirdCorpidAccessToken = thirdCorpidAccessToken;
        this.relThirdPassportXsyPassportIdService = relThirdPassportXsyPassportIdService;
        this.xsyTenantid = xsyTenantid;
        this.commonService = commonService;
        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
        this.userService = userService;
        this.tenantParam = new TenantParam(xsyTenantid);

    }

    /**
     * 钉钉用户列表
     */
    List<User >dingUseLists = new ArrayList<User>();

    public void doCreateOrUpdateUser() throws OApiException, ServiceException, PaasException {
        LOG.info("Corpid["+this.thirdCorpid+"] begin sync userinfo");

        /**
         * 1:遍历所有部门【ding_department_list】
         * 2：查询部门下的所有用户【ding_user_list】
         * 3：遍历所得部门下的所有用户,获得其详情[ding_user_detail]
         */
        List<Department> dingDepartments = DepartmentHelper.listDepartments(this.thirdCorpidAccessToken);
        String dingDepartmentId = null;
        //防重使用的map
        Map<String,Boolean> fangChongMap = new HashMap<String,Boolean>();

        //准备角色参数
        Map<String,com.rkhd.platform.auth.model.Role> roleMap =  commonService.initRoleInfo(tenantParam);
        Map<String,Responsibility> responsibilityMap = commonService.initResponsibilityInfo(tenantParam);

        for(int i=0 ;i<dingDepartments.size();i++){
            Department ding_department = dingDepartments.get(i);
            dingDepartmentId = ding_department.getId();
            //查询钉钉该部门下用户
            List<User> ddUsers =   UserHelper.getDepartmentUser(thirdCorpidAccessToken,new Long(dingDepartmentId));
            if(CollectionUtils.isNotEmpty(ddUsers)){
                for(User ding_u : ddUsers){
                    String ding_userid = ding_u.getUserid();//员工在企业内的UserID
                    User dingUserDetail = UserHelper.getUser(thirdCorpidAccessToken,ding_userid);

                    LOG.info("Corpid["+this.thirdCorpid+"] have find dingUserDetail is ：{}",JSON.toJSONString(dingUserDetail));
                    String ding_id = dingUserDetail.getDingId();
                    if(fangChongMap.containsKey(ding_id)){
                        continue;
                    }
//                    if(manger_dingId.equals(dingUserDetail.getDingId())){
//                        dingExitUserMap.put(""+manager_xsyId,true);
//                        continue;
//                    }
                    //执行同步用户
                    Map<String, Object> retunData =   commonService.doSyncDingUser2Xsy(dingUserDetail,this.thirdCorpid,this.tenantParam,roleMap,responsibilityMap,true);
                    //现在在职员工的ID Map，为删除离职员工做准备
                    dingExitUserMap.put(""+retunData.get("xsy_userId"),true);
                    LOG.info("dingExitUserMap={}",JSON.toJSONString(dingExitUserMap));
                    fangChongMap.put(ding_id,true);
                }
            }
        }
        LOG.info("Corpid["+this.thirdCorpid+"] sync do add or update dingding user end ");
    }
    public void doDeleteXsyUser(long xsyMangerId) throws ServiceException {
        List<Long> xsyUserIds =  userService.getAllUserIds(this.tenantParam);
        if(CollectionUtils.isNotEmpty(xsyUserIds)){
            for(long id :xsyUserIds){
                if(id == xsyMangerId  ){
                    continue;//避免管理员被删除
                }
                if(dingExitUserMap.containsKey(""+id) == false ){
                    //删除激活状态的用户
                    com.rkhd.ienterprise.base.user.model.User user = userService.get(id,this.tenantParam);
                    if(user != null && user.getStatus().equals(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue()) ){
                        LOG.info("Corpid["+this.thirdCorpid+"] execute delete xiaoshouyi user【{}】",JSON.toJSONString(userService.get(id,this.tenantParam)));
                         userService.departureUser(id,this.tenantParam);

                    }
                    //删除映射关系
                    RelThirdUserXsyUser rel =  relThirdUserXsyUserService.getRelThirdUserXsyUserByXsyUserIdTenantidAndSource(id,this.xsyTenantid,Platform.dingding,Env.SUITE_KEY);
                    if(rel != null){
                       // relThirdUserXsyUserService.delete(rel.getId());
                    }

                }
            }
        }
        LOG.info("Corpid["+this.thirdCorpid+"] execute do delete xiaoshouyi more user end ");
    }









}
