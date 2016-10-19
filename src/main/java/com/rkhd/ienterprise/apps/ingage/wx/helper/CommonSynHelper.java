package com.rkhd.ienterprise.apps.ingage.wx.helper;


import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.WxDepartment;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.WXApi;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.base.oauth.model.OauthToken;
import com.rkhd.ienterprise.base.oauth.service.OauthTokenService;
import com.rkhd.ienterprise.base.user.service.UserService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdUserXsyUser;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdUserXsyUserService;
import com.rkhd.platform.auth.model.Responsibility;
import com.rkhd.platform.auth.model.Role;
import com.rkhd.platform.auth.service.RoleService;
import com.rkhd.platform.auth.service.UserRoleService;
import com.rkhd.platform.exception.PaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommonSynHelper {
    private  static Logger LOG = LoggerFactory.getLogger(CommonSynHelper.class);
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private  SynWxDepartmentService  synWxDepartmentService;
    @Autowired
    private OauthTokenService oauthTokenService;

    @Autowired
    private DepartService departService;

    @Autowired
    private SynWxUser2XsyUserService synWxUser2XsyUserService;
    @Autowired
    private AuthCompositionHelper authCompositionHelper;

    @Autowired
    private RelThirdUserXsyUserService relThirdUserXsyUserService;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    private WXApi wxApi =  WXApi.getWxApi();

    /**
     *
     * @param tenantParam
     * @param thirdCorpid
     * @param corp_access_token
     * @param extra_users       额外通讯录（成员）
     * @param allow_users       应用可见范围（成员）
     * @param extra_tags        额外通讯录（标签）
     * @param allow_tags        应用可见范围（标签）
     * @throws PaasException
     * @throws ServiceException
     * @throws WXException
     */
    public void doSync( Long xsy_ManagerUserId,TenantParam tenantParam, String thirdCorpid, String corp_access_token,JSONArray extra_users , JSONArray allow_users, JSONArray extra_tags, JSONArray allow_tags, WxInfoType wxInfoType) throws PaasException, ServiceException, WXException {
        //1：查出所有系统管理员， 系统管理员则不修改他的角色
//        Role adminRole =  roleService.getDefaultAdmin(tenantParam);
//        List<UserRole>  userRoles = userRoleService.getListByRoleId(adminRole.getId(),tenantParam);
//        Map<String,Boolean> adminUserMap = new HashMap<String,Boolean>();
//        Long xsy_ManagerUserId = 0L;
//        if(userRoles != null ){
//            for(int i=0 ;i<userRoles.size();i++){
//                adminUserMap.put("u_"+userRoles.get(i).getUserId(),true);
//                if(i == userRoles.size()-1){
//                    //保留最后一个系统管理员
//                    xsy_ManagerUserId = userRoles.get(i).getUserId();
//                }
//            }
//        }
        // 获取管理员的微信ID
        RelThirdUserXsyUser relThirdUserXsyUser = relThirdUserXsyUserService.getRelThirdUserXsyUserByXsyUserIdTenantidAndSource(
                xsy_ManagerUserId,tenantParam.getTenantId(),Platform.WEIXIN,WXAppConstant.APP_ID
        );
        if(xsy_ManagerUserId == 0){
            LOG.error("tenantid["+tenantParam.getTenantId()+"] no find xsy_ManagerUserId");
        }else if(relThirdUserXsyUser == null ){
            LOG.error("tenantid["+tenantParam.getTenantId()+"] no find manager relation");
        }else {
            //获取销售易的xsy_token
            OauthToken oauthToken = oauthTokenService.getBasicTokenByUserIdAndTenantId(xsy_ManagerUserId,tenantParam);
            String xsyBasicAccessToken = oauthToken.getAccessToken();
//             获取管理员的wxUserID
            String managerWxuserID = relThirdUserXsyUser.getThirdUserId();
            Long xsyManagerId = relThirdUserXsyUser.getXsyUserId();
            Depart xsyTopdepart = departService.getTop(tenantParam);
            //同步部门结构
            JSONObject synWxDepartmentResultJson =  synWxDepartmentService.synWxDepartment(tenantParam,thirdCorpid,xsyBasicAccessToken,corp_access_token,xsyTopdepart);
            //同步人员，包括应用可见范围（成员），应用可见范围（标签），额外通讯录（标签）里的人，额外通讯录（成员）和部门成员
            Map<String,Boolean> extraNeedSyncExtraUserMap = generateSyncExtraUserMap(extra_users,corp_access_token,allow_users,extra_tags,allow_tags);
            LOG.info("extraNeedSyncExtraUserMap="+JSON.toJSONString(extraNeedSyncExtraUserMap));
            //同步部门里的人
            JSONArray  wxDepartmentTree = synWxDepartmentResultJson.getJSONArray("wxDepartmentTree");
            Map<String ,String> xsyDepartment2wxdepartment = (Map<String, String>) synWxDepartmentResultJson.get("xsyDepartment2wxdepartment");

            Depart xsyTopDepartment = departService.getTop(tenantParam);
            Map<String ,Long> wxdepartment2xsyDepartment = (Map<String, Long>) synWxDepartmentResultJson.get("wxdepartment2xsyDepartment");
            Map<String,Role> roleMap = authCompositionHelper.getTenantRoleMap(tenantParam);
            Map<String,Responsibility> responsibilityMap = authCompositionHelper.getTenantResponsibilityMap(tenantParam);

            Map<String,String> xsyUser2WxUserMap = new HashMap<String, String>();
            recursionDepartment(tenantParam,thirdCorpid,corp_access_token,managerWxuserID,wxDepartmentTree,xsyTopDepartment,wxdepartment2xsyDepartment,
                    roleMap,responsibilityMap,extraNeedSyncExtraUserMap,  xsyUser2WxUserMap,  wxInfoType);
            //同步特别指定的人
            JSONObject wxUserItem = null;
            for (String userid : extraNeedSyncExtraUserMap.keySet()) {
                wxUserItem =  wxApi.get_user(corp_access_token,userid);
                LOG.info("need syn user is ："+JSON.toJSONString(wxUserItem));
                if(wxUserItem.containsKey("errcode" ) && 0 == wxUserItem.getIntValue("errcode")){
                   JSONObject synUserResult =  synWxUser2XsyUserService.doSyncWXUser2Xsy(tenantParam,wxUserItem,thirdCorpid,roleMap,responsibilityMap,false,managerWxuserID,xsyManagerId,  wxInfoType);
                    if(synUserResult.getBoolean("success")){
                        long xsy_userId = synUserResult.getLong("xsy_userId");
                        String  wxUserid = synUserResult.getString("wxUserid");
                        xsyUser2WxUserMap.put(""+xsy_userId,wxUserid);
                    }
                }
            }
            //删除销售易里有，但是微信里没有的用户
            this.doDeleteXsyUser(thirdCorpid,tenantParam,xsyManagerId,xsyUser2WxUserMap);
//           //删除销售易里有，但是微信里没有的部门
            this.doDelDeparmentOpp(tenantParam,thirdCorpid,xsyBasicAccessToken,xsyDepartment2wxdepartment,xsyTopdepart);
        }
    }
    //递归处理部门下的人员
    private void recursionDepartment(TenantParam tenantParam,String  thirdCorpid,String corp_access_token, String managerWxuserID ,JSONArray  wxDepartmentTree,Depart xsyTopDepartment,Map<String ,Long> wxdepartment2xsyDepartment,Map<String,Role> roleMap ,
                                     Map<String,Responsibility> responsibilityMap, Map<String,Boolean> extraNeedSyncExtraUserMap,Map<String,String> xsyUser2WxUserMap , WxInfoType wxInfoType) throws WXException, ServiceException, PaasException {
        for(int i=0;i<wxDepartmentTree.size();i++){
            WxDepartment wxdepartment = (WxDepartment) wxDepartmentTree.get(i);
            //同步当前部门下的员工
            JSONObject userListJson = wxApi.getDepartment_user_list(corp_access_token,wxdepartment.getId()+"",0,0+"");
            if(userListJson.getIntValue("errcode") == 0){
                JSONArray userlist = userListJson.getJSONArray("userlist");
                JSONObject wxUserItem = null;
                for(int j= 0;j<userlist.size();j++)
                {
                    wxUserItem = userlist.getJSONObject(j);
                    JSONObject synUserResult = synWxUser2XsyUserService.doSyncWXUser2Xsy(tenantParam,wxUserItem,thirdCorpid,roleMap,responsibilityMap,false,managerWxuserID,0,  wxInfoType);

                    if(extraNeedSyncExtraUserMap.containsKey(wxUserItem.getString("userid"))){
//                        移除已经指定的客户
                        extraNeedSyncExtraUserMap.remove(wxUserItem.getString("userid"));
                    }
                    if(synUserResult.getBoolean("success")){
                        long xsy_userId = synUserResult.getLong("xsy_userId");
                        String  wxUserid = synUserResult.getString("wxUserid");
                        xsyUser2WxUserMap.put(""+xsy_userId,wxUserid);
                    }
                }
            }
            //同步子部门的员工
            JSONArray childes = wxdepartment.getChildes();
            if(childes != null){
                recursionDepartment(tenantParam,thirdCorpid,corp_access_token,managerWxuserID,childes,xsyTopDepartment,wxdepartment2xsyDepartment,roleMap,responsibilityMap,extraNeedSyncExtraUserMap,xsyUser2WxUserMap,  wxInfoType);
            }
        }
    }

    /**
     * 生成需要额外同步的用户。除去部门里的。即需要额外同步的用户包括标签里的和特定的
     * 把数据保存到 返回结果里
     *
     * @param extra_users       额外通讯录（成员）
     * @param allow_users       应用可见范围（成员）
     * @param extra_tags        额外通讯录（标签）
     * @param allow_tags        应用可见范围（标签）
     */
    private  Map<String,Boolean>  generateSyncExtraUserMap(JSONArray extra_users ,  String corp_access_token,JSONArray allow_users,  JSONArray extra_tags, JSONArray allow_tags) throws WXException {
        Map<String,Boolean> needSyncExtraUserMap = new HashMap<String, Boolean>();
        if(extra_users != null){
            String wxUserId = null;
            for(int i=0;i<extra_users.size();i++){
                wxUserId = extra_users.getString(i);
                needSyncExtraUserMap.put(wxUserId,true);
            }
        }
        if(allow_users != null){
            String wxUserId = null;
            for(int i=0;i<allow_users.size();i++){
                wxUserId = allow_users.getString(i);
                needSyncExtraUserMap.put(wxUserId,true);
            }
        }
        if(extra_tags != null){
            String wxTagId= null;
            for(int i=0;i<extra_tags.size();i++){
                wxTagId = extra_tags.getString(i);
                JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);
                LOG.info("tagMemberJsonObject="+tagMemberJsonObject.toJSONString());
                if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                    JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                    if(userlist != null){
                        String wxUserId = null;
                        for(int j=0;j<userlist.size();j++){
                            wxUserId = userlist.getJSONObject(j).getString("userid");
                            if(StringUtils.isNotBlank(wxUserId)){
                                needSyncExtraUserMap.put(wxUserId,true);
                            }
                        }
                    }
                }
            }
        }

        if(allow_tags != null){
            String wxTagId= null;
            for(int i=0;i<allow_tags.size();i++){
                wxTagId = allow_tags.getString(i);
                JSONObject tagMemberJsonObject =  wxApi.getTagMember(corp_access_token,wxTagId);
                LOG.info("tagMemberJsonObject="+tagMemberJsonObject.toJSONString());
                if(tagMemberJsonObject != null && tagMemberJsonObject.containsKey("userlist")){
                    JSONArray userlist = tagMemberJsonObject.getJSONArray("userlist");
                    if(userlist != null){
                        String wxUserId = null;
                        for(int j=0;j<userlist.size();j++){
                            wxUserId = userlist.getJSONObject(j).getString("userid");
                            if(StringUtils.isNotBlank(wxUserId)){
                                needSyncExtraUserMap.put(wxUserId,true);
                            }

                        }
                    }
                }
            }
        }
        return needSyncExtraUserMap;
    }

    public void doDeleteXsyUser( String thirdCorpid,TenantParam tenantParam,long xsyMangerId,Map<String,String> xsyUser2WxUserMap) throws ServiceException {
        List<Long> xsyUserIds =  userService.getAllUserIds(tenantParam);
        if(CollectionUtils.isNotEmpty(xsyUserIds)){
            for(long id :xsyUserIds){
                if(id == xsyMangerId  ){
                    continue;//避免管理员被删除
                }
                if(xsyUser2WxUserMap.containsKey(""+id) == false ){
                    //删除激活状态的用户
                    com.rkhd.ienterprise.base.user.model.User user = userService.get(id,tenantParam);
                    RelThirdUserXsyUser rel =  relThirdUserXsyUserService.getRelThirdUserXsyUserByXsyUserIdTenantidAndSource(id,tenantParam.getTenantId(),Platform.WEIXIN, WXAppConstant.APP_ID);
                    if(user != null && user.getStatus().equals(com.rkhd.ienterprise.base.user.model.User.Status.ACTIVE.getValue()) ){
                        LOG.info("Corpid["+thirdCorpid+"] execute delete xiaoshouyi user【{}】", JSON.toJSONString(userService.get(id,tenantParam)));
                        try{
                            //判断用户是否是超级管理员，如果是则不删除
                            if(StringUtils.isBlank(rel.getThirdUserPhone()) && StringUtils.isBlank(rel.getThirdUserEmail())){

                                userService.departureUser(id,tenantParam);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        LOG.info("Corpid["+thirdCorpid+"] execute do delete xiaoshouyi more user end ");
    }

    //做删除部门操作
    public void doDelDeparmentOpp(TenantParam tenantParam,String thirdCorpid,String xsyBasicAccessToken,Map<String ,String> xsyDepartment2wxdepartment,  Depart xsyTopdepart ) {
        try {
            //从销售易里删除在销售易里但是不在钉钉里的部门
            LOG.info("Corpid[ "+thirdCorpid+"] doDelDeparmentOpp  with  xsyBasicAccessToken:{}",xsyBasicAccessToken);
            EntityReturnData departmentListEntity =  departmentService.getDepartmentList(xsyBasicAccessToken);

            if(departmentListEntity.isSuccess())
            {
                JSONObject xsyRootDepartmentTreeNode = (JSONObject) departmentListEntity.getEntity();
                //递归删除销售易有，但是钉钉没有的部门
                recursionDeleteXsyDepartments(tenantParam ,thirdCorpid,xsyBasicAccessToken,xsyRootDepartmentTreeNode,xsyDepartment2wxdepartment,xsyTopdepart);
            }else{
                LOG.error("Corpid[ "+thirdCorpid+"] departmentListEntity  ="+JSON.toJSONString(departmentListEntity)+";  xsyDepartment2wxdepartment： "+JSON.toJSONString(xsyDepartment2wxdepartment)+";xsyBasicAccessToken="+xsyBasicAccessToken);
            }


        }   catch (ServiceException e) {
            e.printStackTrace();
        }
    }
    /**
     * 递归
     * 遍历删除销售易公司的部门对应在钉钉里找不到的部门
     * @param xsyDepartmentTreeNode
     */
    public boolean  recursionDeleteXsyDepartments(TenantParam tenantParam,String thirdCorpid,String xsyBasicAccessToken,JSONObject xsyDepartmentTreeNode,Map<String ,String> xsyDepartment2wxdepartment,  Depart xsyTopdepart) throws ServiceException {
        boolean result = false;
        if(xsyDepartmentTreeNode.containsKey("subs")){
            JSONArray departmentNodes  = xsyDepartmentTreeNode.getJSONArray("subs");
            boolean subHaveAllDel = true;
            for(int i=0;i<departmentNodes.size();i++){
                JSONObject departmentNode = departmentNodes.getJSONObject(i);
                result = recursionDeleteXsyDepartments(tenantParam,thirdCorpid,xsyBasicAccessToken,departmentNode,xsyDepartment2wxdepartment,xsyTopdepart);
                if(!result){
                    subHaveAllDel = false;
                }
            }
            if(subHaveAllDel){
                //删除所有的子节点后
                xsyDepartmentTreeNode.remove("subs");
                result = recursionDeleteXsyDepartments(tenantParam,thirdCorpid,xsyBasicAccessToken,xsyDepartmentTreeNode,xsyDepartment2wxdepartment,xsyTopdepart);
            }else {
                result = false;
            }

        }else{
            //查找映射关系
            long xsyDepartmentId = xsyDepartmentTreeNode.getLong("id");
            String name = xsyDepartmentTreeNode.getString("name");
            if(!("dev".equals(name)) && !xsyDepartment2wxdepartment.containsKey(""+xsyDepartmentId) && xsyDepartmentId != xsyTopdepart.getId()){
                //删除部门
                EntityReturnData delResult = departmentService.deleteDeparment(xsyBasicAccessToken,xsyDepartmentId);
                LOG.info("Corpid["+thirdCorpid+"]delete department [{}] result is：{}", xsyDepartmentId,JSON.toJSONString(delResult));
                if(delResult.isSuccess()){

                    //删除映射关系
//                    RelThirdDepXsyDepartment rel =  relThirdDepXsyDepartmentService.getRelThirdDepXsyDepartmentByXsytenantIdDepartmentIdAndThirdSource(xsyDepartmentId,tenantParam.getTenantId(),Platform.WEIXIN,WXAppConstant.APP_ID);
//                    if(rel !=null){
//                        result =  relThirdDepXsyDepartmentService.delete(rel.getId());
//                    }
                    result = true;
                }else {
                    result = false;
                    EntityReturnData delEntityReturnData =  departmentService.getDepartmentById(xsyBasicAccessToken,xsyDepartmentId);
                    LOG.error("Corpid["+thirdCorpid+"] delete department ["+xsyDepartmentId+"] failure  result is ："+JSON.toJSONString(delResult)+",be delete department is "+ JSON.toJSONString(delEntityReturnData));
                }
            }else {
                result = true;
            }
        }
        return result;
    }

}
