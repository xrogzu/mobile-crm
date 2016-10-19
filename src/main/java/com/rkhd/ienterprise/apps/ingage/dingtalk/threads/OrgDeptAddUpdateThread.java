package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.Department;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.DepartmentHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.User;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user.UserHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.profile.service.PersonalProfileService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.service.UserRoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件监听
 * 部门添加或修改执行
 */
public class OrgDeptAddUpdateThread extends Thread{
    private static final Logger LOG = LoggerFactory.getLogger(OrgDeptAddUpdateThread.class);

    private String xsyBasicAccessToken ;

    private RelThirdTokenService relThirdTokenService;

    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    private DepartmentService departmentService;//openapi DepartmentService

    private CommonService commonService;

    private OauthTokenService oauthTokenService;

    private RelThirdUserXsyUserService relThirdUserXsyUserService;

    private String thirdCorpid;

    private JSONArray departmentIds;

    private Long  xsyTenantid;

    private TenantParam  tenantParam = null;

    private DepartService departService;

    private PersonalProfileService profileService;
    private UserRoleService userRoleService;


    public OrgDeptAddUpdateThread(String corpId, JSONArray departmentIds,
                                  RelThirdTokenService relThirdTokenService,
                                  RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService, RelThirdCorpXsyTenantService relThirdCorpXsyTenantService, DepartmentService departmentService, CommonService commonService, OauthTokenService oauthTokenService, RelThirdUserXsyUserService relThirdUserXsyUserService, DepartService departService, PersonalProfileService profileService, UserRoleService userRoleService){
        super();
        this.thirdCorpid = corpId;
        this.departmentIds = departmentIds;
        this.relThirdTokenService = relThirdTokenService;
        this.relThirdDepXsyDepartmentService = relThirdDepXsyDepartmentService;
        this.relThirdCorpXsyTenantService = relThirdCorpXsyTenantService;
        this.departmentService = departmentService;
        this.commonService = commonService;
        this.oauthTokenService = oauthTokenService;
        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
        this.departService = departService;
        this.profileService = profileService;
        this.userRoleService = userRoleService;
    }
     public void run(){

        RelThirdToken relThirdToken = null;
        try {
            /**
             * 获取租户的accessToken
             */
            relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.dingding,thirdCorpid, Env.SUITE_KEY);
            LOG.info("Corpid【{}】 create or update department {}",thirdCorpid,JSON.toJSON(relThirdToken));
            if(relThirdToken == null){
                LOG.error("Corpid【{}】 can not find relation by thirdCorpid={}",thirdCorpid,thirdCorpid);
            }else{
                //获取租户信息
                RelThirdCorpXsyTenant relThirdCorpXsyTenantrelThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(this.thirdCorpid,Platform.dingding,Env.SUITE_KEY);
               this.xsyTenantid = relThirdCorpXsyTenantrelThirdCorpXsyTenant.getXsyTenantid();

                tenantParam = new TenantParam(xsyTenantid);

                String thirdCorpidAccessToken =  relThirdToken.getTokenValue();

                TenantParam tenantParam = new TenantParam(xsyTenantid);
                //查找管理员
                RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getFirstRelThirdUserXsyUserByCorpidThird(thirdCorpid,Platform.dingding,Env.SUITE_KEY);
                if(relThirdUserXsyUser  == null){
                    LOG.error("find relThirdUserXsyUser error ,and relThirdUserXsyUser = null");
                }else{
                    long operatorId = 0;
                    long managerId   = 0;
                    long beCreateBy = 0;
                    operatorId = managerId =  beCreateBy  = relThirdUserXsyUser.getXsyUserId();

                    //获取xsyBasicAccessToken
                    OauthToken xsyOauthToken =  oauthTokenService.getBasicTokenByUserIdAndTenantId(operatorId,tenantParam);
                    this.xsyBasicAccessToken = xsyOauthToken.getAccessToken();
                    Map<String,com.rkhd.platform.auth.model.Role> roleMap =  commonService.initRoleInfo(tenantParam);
                    Map<String,Responsibility> responsibilityMap = commonService.initResponsibilityInfo(tenantParam);


                    for(int i=0;i<departmentIds.size();i++){
                        RelThirdDepXsyDepartment relThirdDepXsyDepartment = null;
                        Department dingDepartment = null;
                        //查询钉钉部门详情
                        String dingDepartmentId = departmentIds.getString(i);

                        if("-1".equals(dingDepartmentId)){
//                        进行全公司进行操作，只修改管理员信息，因为钉钉不允许修改公司名称
//                        doUpdateDeparmentManager( thirdCorpidAccessToken, dingDepartmentId, roleMap,
//                                responsibilityMap , relThirdDepXsyDepartment);
                            dingDepartmentId = "1";
                        }
                        dingDepartment =  DepartmentHelper.get(thirdCorpidAccessToken,dingDepartmentId);
                        LOG.info("Corpid【{}】dingDepartment={}",thirdCorpid,JSON.toJSONString(dingDepartment));

                        //查询部门映射关系
                        relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,dingDepartmentId,Platform.dingding,Env.SUITE_KEY);
                        LOG.info("relThirdDepXsyDepartment={}"+JSON.toJSONString(relThirdDepXsyDepartment));
                        if(relThirdDepXsyDepartment == null){
                            //做添加部门操作
                            doAddDepartment( dingDepartment);
                        }else {
                            if("1".equals(dingDepartmentId)){
                                //更新管理员信息
                                doUpdateDeparmentManager( thirdCorpidAccessToken, dingDepartmentId, roleMap,
                                        responsibilityMap , relThirdDepXsyDepartment);
                            }else{
                                //修改管理员信息
                                doUpdateDepartment( dingDepartment,relThirdDepXsyDepartment.getXsyDepartmentId(),
                                        thirdCorpidAccessToken,dingDepartment.getId(), roleMap,
                                        responsibilityMap,  relThirdDepXsyDepartment);
                            }

                        }
                    }
                }


            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }   catch (OApiException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void doAddDepartment(Department dingDepartment) throws ServiceException {
        RelThirdDepXsyDepartment parentRelThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,dingDepartment.getParentid(),Platform.dingding,Env.SUITE_KEY);
        if(parentRelThirdDepXsyDepartment == null){
            LOG.error("Corpid【"+thirdCorpid+"】 cannot find the dingding department[{}]`s parent deparment[{}]`s relation",dingDepartment.getName(),dingDepartment.getParentid());
//            LOG.error("错误，钉钉部门【{}】的父级部门【{}】没有建立映射关系，所以无法创建该部门的映射关系",dingDepartment.getName(),dingDepartment.getParentid());
        }else{
            com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
            xsyDepartment.setDepartName(dingDepartment.getName());
            xsyDepartment.setParentDepartId(parentRelThirdDepXsyDepartment.getXsyDepartmentId());
            xsyDepartment.setDepartType(DepartmentThread.departType);
            //创建销售易部门
            LOG.info("Corpid【"+thirdCorpid+"】 begin create department ，xsyDepartment={}",xsyDepartment);
            EntityReturnData createDepartmentEntityReturnData =  departmentService.createDepartment(this.xsyBasicAccessToken,xsyDepartment);
            LOG.info("Corpid【"+thirdCorpid+"】 create department`result is {}",JSON.toJSONString(createDepartmentEntityReturnData));
            if(createDepartmentEntityReturnData.isSuccess()){
                JSONObject entity = (JSONObject) createDepartmentEntityReturnData.getEntity();
                Long  xsyDepartmentId = entity.getLong("id");
                xsyDepartment.setId(xsyDepartmentId);
                //创建部门映射关系
                this.createRelThirdDepXsyDepartment(dingDepartment.getId(),xsyDepartmentId);
            }else{
                LOG.error("Corpid["+thirdCorpid+"] create xiaohouyi deparment failue ，dingDepartment is {} ", JSON.toJSONString(dingDepartment));
            }
        }
    }

    public void doUpdateDepartment(Department dingDepartment,long xsyDepartmentId,String thirdCorpidAccessToken,String dingDepartmentId, Map<String,com.rkhd.platform.auth.model.Role> roleMap,
                                   Map<String,Responsibility> responsibilityMap,RelThirdDepXsyDepartment relThirdDepXsyDepartment) throws ServiceException, PaasException, OApiException {

        RelThirdDepXsyDepartment parentRelThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,dingDepartment.getParentid(),Platform.dingding,Env.SUITE_KEY);
        if(parentRelThirdDepXsyDepartment == null ){
            LOG.error("Corpid【"+thirdCorpid+"】 cannot find the dingding department[{}]`s parent deparment[{}]`s relation",dingDepartment.getName(),dingDepartment.getParentid());
//            LOG.error("错误，钉钉部门【{}】的父级部门【{}】没有建立映射关系，所以无法创建该部门的映射关系",dingDepartment.getName(),dingDepartment.getParentid());
        }else{

                com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
                xsyDepartment.setId(xsyDepartmentId);
                xsyDepartment.setDepartName(dingDepartment.getName());
                xsyDepartment.setParentDepartId(parentRelThirdDepXsyDepartment.getXsyDepartmentId());
                xsyDepartment.setDepartType(DepartmentThread.departType);
                //修改销售部门信息销售易部门
                EntityReturnData updateDepartmentEntityReturnData =  departmentService.updateDepartment(this.xsyBasicAccessToken,xsyDepartment);
                LOG.info("Corpid【"+thirdCorpid+"】 update department【"+dingDepartment.getName()+"】，sync result is：{}",  JSON.toJSONString(updateDepartmentEntityReturnData));
                if(updateDepartmentEntityReturnData.isSuccess()){

                    doUpdateDeparmentManager( thirdCorpidAccessToken, dingDepartmentId, roleMap,
                            responsibilityMap , relThirdDepXsyDepartment);
                }


        }
    }

    public RelThirdDepXsyDepartment createRelThirdDepXsyDepartment(String dingDepartmentId,long xsyDepartmentId ) throws ServiceException {
//保存映射关系
        RelThirdDepXsyDepartment relThirdDepXsyDepartment = new RelThirdDepXsyDepartment();
        relThirdDepXsyDepartment.setThirdCorpid(this.thirdCorpid);
        relThirdDepXsyDepartment.setThirdDepartmentId(dingDepartmentId);
        relThirdDepXsyDepartment.setThirdSource(Platform.dingding);
        relThirdDepXsyDepartment.setXsyDepartmentId(xsyDepartmentId);
        relThirdDepXsyDepartment.setXsyTenantid(this.xsyTenantid);
        relThirdDepXsyDepartment.setSuiteKey(Env.SUITE_KEY);
        relThirdDepXsyDepartmentService.save(relThirdDepXsyDepartment);
        return relThirdDepXsyDepartment;
    }


    public void doUpdateDeparmentManager(String thirdCorpidAccessToken,String dingDepartmentId, Map<String,com.rkhd.platform.auth.model.Role> roleMap,
                                        Map<String,Responsibility> responsibilityMap ,RelThirdDepXsyDepartment relThirdDepXsyDepartment) throws OApiException, ServiceException, PaasException {
        //防重使用的map
        Map<String,User> fangChongMap = new HashMap<String,User>();
        //获取钉钉部门用户
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
//修改角色
                commonService.setDingUserRole(dingUserDetail,this.thirdCorpid,this.tenantParam,roleMap,responsibilityMap,true);
                fangChongMap.put(ding_id,dingUserDetail);
            }
        }




    }
}
