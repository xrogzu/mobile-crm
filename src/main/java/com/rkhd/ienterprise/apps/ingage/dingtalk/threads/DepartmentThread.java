package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.Department;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department.DepartmentHelper;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.base.passport.service.PassportService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdPassportXsyPassportIdService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/1/7.
 */

/**
 * 同步钉钉部门到销售易
 */
public class DepartmentThread  {
    private static final Logger LOG = LoggerFactory.getLogger(DepartmentThread.class);

    public static final Long departType = 2L;
    /**
     * 销售易的basicAccessToken
     */
    private String xsyBasicAccessToken ;
    /**
     * 第三方企业ID
     */
    private String thirdCorpid;
    /**
     *  第三方企业的access_token
     */
    private String thirdCorpidAccessToken;
    /**
     * 销售易租户ID
     */
    private long xsyTenantid;

    RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    private DepartmentService departmentService;

    private PassportService passportService;

    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;

    private CommonService commonService;

    private TenantService tenantService;

//    private RelThirdUserXsyUserService relThirdUserXsyUserService;
//
//    private UserService userService;
//
//    private XsyApiUserService xsyUApiUserService;

    Map<String,RelThirdDepXsyDepartment> noDelXsyDepartmentMap = new HashMap<String, RelThirdDepXsyDepartment>();

    public DepartmentThread(String xsyBasicAccessToken, String thirdCorpid, String thirdCorpidAccessToken,
                            long xsyTenantid, RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService
            , DepartmentService departmentService, PassportService passportService, RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService, CommonService commonService,
                            TenantService tenantService
//            , RelThirdUserXsyUserService relThirdUserXsyUserService, UserService userService, XsyApiUserService xsyUApiUserService
    ){
        super();
        this.xsyBasicAccessToken = xsyBasicAccessToken;
        this.thirdCorpid = thirdCorpid;
        this.thirdCorpidAccessToken = thirdCorpidAccessToken;
        this.xsyTenantid = xsyTenantid;
        this.relThirdDepXsyDepartmentService = relThirdDepXsyDepartmentService;
        this.departmentService = departmentService;
        this.passportService = passportService;
        this.relThirdPassportXsyPassportIdService = relThirdPassportXsyPassportIdService;
        this.commonService = commonService;
        this.tenantService = tenantService;
//        this.relThirdUserXsyUserService = relThirdUserXsyUserService;
//        this.userService = userService;
//        this.xsyUApiUserService = xsyUApiUserService;
    }

    /**
     * 做同步工作第一步， 做新增和修改的部分
     */
    public void doAddAndUpdateSync() throws OApiException, ServiceException {
//        EntityReturnData departMentEntityReturnData = departmentService.getDepartmentList(this.xsyBasicAccessToken);
//        //全公司的ID
//        if(departMentEntityReturnData.isSuccess()){
//            JSONObject entity = (JSONObject) departMentEntityReturnData.getEntity();
//            xsyRootDepartmentId = entity.getLong("id");
//        }
        //查询钉钉企业的部门列表
        List<Department> ddDepartments = DepartmentHelper.listDepartments(this.thirdCorpidAccessToken);
        LOG.info("Corpid[{}] get dingding`s department list is ：{}",this.thirdCorpid, JSON.toJSONString(ddDepartments));
//查询销售易部门列表
        LOG.info("Corpid[ "+thirdCorpid+"] doAddAndUpdateSync  with  xsyBasicAccessToken：{}",xsyBasicAccessToken);
        EntityReturnData departmentListEntity =  departmentService.getDepartmentList(xsyBasicAccessToken);
        LOG.info("Corpid[{}] get xiaoshouyi`s department list is ：{}",this.thirdCorpid, JSON.toJSONString(departmentListEntity));
        List<com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department > xsyDepartmentList = new ArrayList<com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department>();
        long rootXsyDepartmentId = 0L;
        if(departmentListEntity.isSuccess())
        {
            JSONObject entity = (JSONObject) departmentListEntity.getEntity();
// 约定0L标识总公司的PID
            rootXsyDepartmentId = entity.getLong("id");
            LOG.info("Corpid[{}]get xiaoshouyi`s \"all company\"`s id is {}",this.thirdCorpid,rootXsyDepartmentId);
//递归销售易部门，把部门放到xsyDepartmentList里
            recursionXsyDepartment(entity,xsyDepartmentList,0L);
        }else{
            LOG.error("Corpid[{}] get xiaoshouyi`s department is failure. xsyBasicAccessToken={},apiReturn="+JSON.toJSONString(departmentListEntity),this.thirdCorpid,xsyBasicAccessToken);
            throw new OApiException(Integer.parseInt(departmentListEntity.getErrorCode()),"pull xiaoshouyi department data fail");
        }
        //方便快速索引销售易的部门
        Map<String,com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department> xsyDepartmentMap =
                new HashMap<String, com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department>();
        for(com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department item:xsyDepartmentList){
            xsyDepartmentMap.put("xsy_"+item.getId(),item);
        }

//查出所有的现有的映射关系
        List<RelThirdDepXsyDepartment> relThirdDepXsyDepartmentLists =
                relThirdDepXsyDepartmentService.getRelThirdDepXsyDepartmentByThirdCorpidAndThirdSource(thirdCorpid,Platform.dingding, Env.SUITE_KEY);
//            双向映射方便建立查询
        Map<String,RelThirdDepXsyDepartment> relMap = new HashMap<String,RelThirdDepXsyDepartment>();
        for(RelThirdDepXsyDepartment temp:relThirdDepXsyDepartmentLists){
            relMap.put("xsy-"+temp.getXsyTenantid()+"-"+temp.getXsyDepartmentId(),temp);
            relMap.put("dd-"+temp.getThirdCorpid()+"-"+temp.getThirdDepartmentId(),temp);
        }
        //将钉钉部门组装树形数据
        JSONArray newDindingDepartments = createDingdingDepartmentTree(ddDepartments);
        LOG.info("Corpid[{}] dingding department tree list is ：{}",this.thirdCorpid,JSON.toJSONString(newDindingDepartments));
        /**
         * 为删除做准使用的map
         */

        //递归遍历钉钉树，做修改或新建操作
        LOG.info("recursion dingding department tree.begin do create or update department ");
        recursionDingDepartments(newDindingDepartments, relMap,  rootXsyDepartmentId, 0L ,xsyDepartmentMap );

    }
    //做删除部门操作
    public void doDelDeparmentOpp() {
        try {
            //从销售易里删除在销售易里但是不在钉钉里的部门
            LOG.info("Corpid[ "+thirdCorpid+"] doDelDeparmentOpp  with  xsyBasicAccessToken：{}",xsyBasicAccessToken);
            EntityReturnData departmentListEntity =  departmentService.getDepartmentList(xsyBasicAccessToken);
            if(departmentListEntity.isSuccess())
            {
                JSONObject xsyRootDepartmentTreeNode = (JSONObject) departmentListEntity.getEntity();
                //递归删除销售易有，但是钉钉没有的部门
                recursionDeleteXsyDepartments(xsyRootDepartmentTreeNode);
            }


        }   catch (ServiceException e) {
            e.printStackTrace();
        }
    }
    //递归销售易部门,约定0位全公司的父ID
    private void recursionXsyDepartment(JSONObject department,List<com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department > xsyDepartmentList,Long pid){
        com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
        xsyDepartment.setId(department.getLong("id"));
        xsyDepartment.setDepartName(department.getString("name"));
        xsyDepartment.setParentDepartId(pid);
        xsyDepartment.setDepartType(departType);//部门类型默认为销售部
        //放到xsyDepartmentList里
        xsyDepartmentList.add(xsyDepartment);
        //递归子部门
        if(department.containsKey("subs")){
            JSONArray subs = department.getJSONArray("subs");
            for(int i=0;i<subs.size();i++){
                JSONObject temDepartment = subs.getJSONObject(i);
                recursionXsyDepartment(temDepartment,xsyDepartmentList,xsyDepartment.getId());
            }
        }
    }

    /**
     * 返回钉钉组织树形结构，属性只是多了个subs：[]。
     * @param ddDepartments
     * @return
     */
    public JSONArray createDingdingDepartmentTree(List<Department> ddDepartments){
            if(CollectionUtils.isEmpty(ddDepartments)){
                return new JSONArray();
            }
            JSONArray retData = new JSONArray();
            Map<String,JSONObject> departmentMap = new HashMap<String, JSONObject>();

            JSONArray ddDepartmentArray = new JSONArray();
            for(Department ddDepartment :ddDepartments){
                JSONObject jsonDdDepartment = (JSONObject) JSON.toJSON(ddDepartment);
                String key = "id_"+ddDepartment.getId();
                departmentMap.put(key,jsonDdDepartment);
                ddDepartmentArray.add(jsonDdDepartment);
            }

        for(int i=0;i<ddDepartmentArray.size();i++){
            JSONObject temDepartment = ddDepartmentArray.getJSONObject(i);
            if(!temDepartment.containsKey("parentid")){
                temDepartment.put("parentid","0");
            }
            String pid = temDepartment.getString("parentid");
            String mapKey = "id_"+pid;
            if(departmentMap.containsKey(mapKey) ){
                JSONObject pDdDepartment = departmentMap.get(mapKey);
                if(!pDdDepartment.containsKey("subs")){
                    pDdDepartment.put("subs",new JSONArray());
                }
                pDdDepartment.getJSONArray("subs").add(temDepartment);
            }else{
                retData.add(temDepartment) ;
            }
        }
            return retData;
    }

    /**
     * 递归调用，遍历某个部门
     *ddDepartment = {id:'xx',name:'yy',parentid:'zz'[,subs:[]]};
     * @param rootXsyDepartmentId 销售易部门【全公司】的id
     * @param newDindingDepartments
     */
    public void recursionDingDepartments(JSONArray newDindingDepartments, Map<String,RelThirdDepXsyDepartment> relMap,
                                       long rootXsyDepartmentId,long xsyDepartmentParentId,
                                       Map<String,com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department>xsyDepartmentMap
                                         ) throws ServiceException {
//        LOG.info("newDindingDepartments={}",JSON.toJSONString(newDindingDepartments));
        LOG.info("Corpid[{}] begin sync department ：{}",this.thirdCorpid,JSON.toJSONString(newDindingDepartments));
        for(int i=0;i<newDindingDepartments.size();i++){
            JSONObject ddDepartment = newDindingDepartments.getJSONObject(i);
            String ddDepartmentName = ddDepartment.getString("name");
             String ddDepartmentId = ddDepartment.getString("id");
            String key1 = "dd-"+thirdCorpid+"-"+ddDepartmentId;

            RelThirdDepXsyDepartment relThirdDepXsyDepartment =  relMap.get(key1);
            //根节点建立关系即可
            long newXsyDepartmentParentId = 0L;
            LOG.info("{}",JSON.toJSONString(ddDepartment));
            if("1".equals(ddDepartmentId)){
                relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(this.thirdCorpid,ddDepartmentId,Platform.dingding,Env.SUITE_KEY);
                if(relThirdDepXsyDepartment == null){
//                    新建钉钉根节点和销售易根节点的映射关系;
                    relThirdDepXsyDepartment =  this.createRelThirdDepXsyDepartment("1",rootXsyDepartmentId);
                    setRelMap(  relMap, relThirdDepXsyDepartment);
                }
                //重设xsyDepartmentParentId
                newXsyDepartmentParentId = relThirdDepXsyDepartment.getXsyDepartmentId();
                //为删除准备
                noDelXsyDepartmentMap.put("xsy_"+relThirdDepXsyDepartment.getXsyDepartmentId(),relThirdDepXsyDepartment);

            }else {
                com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = null;
                //查询是否存在映射关系
                if(   relThirdDepXsyDepartment == null){
                    //新建部门，并建立映射关系，最后把映射关系放到map里
                     xsyDepartment = this.createXsyDepartmentAndRelation(ddDepartment,relMap,xsyDepartmentParentId,xsyDepartmentMap);
                     relThirdDepXsyDepartment =  this.createRelThirdDepXsyDepartment(ddDepartmentId,xsyDepartment.getId());

                    setRelMap(  relMap, relThirdDepXsyDepartment);
                    xsyDepartmentMap.put("xsy_"+xsyDepartment.getId(),xsyDepartment);

                }else  {
                    //获取销售易的部门，如果部门不存在则新建，如果存在则修改部门的pid和部门名称
                    long xsyDepatmentId = relThirdDepXsyDepartment.getXsyDepartmentId();
                     xsyDepartment = xsyDepartmentMap.get("xsy_"+xsyDepatmentId);
                    if(xsyDepartment == null){
                        //销售易部门不存在，则新建部门，新建关系
                        xsyDepartment = this.createXsyDepartmentAndRelation(ddDepartment,relMap,rootXsyDepartmentId,xsyDepartmentMap);
                        relThirdDepXsyDepartment =  this.createRelThirdDepXsyDepartment(ddDepartmentId,xsyDepartment.getId());
                        setRelMap(  relMap, relThirdDepXsyDepartment);
                        xsyDepartmentMap.put("xsy_"+xsyDepartment.getId(),xsyDepartment);
                    }else{
                        //销售易部门存在，则修改销售易部门名称和pid
                        String dingdingPid = ddDepartment.getString("parentid");
                        String dingdingDepartmentName = ddDepartment.getString("name");
                        xsyDepartment.setDepartName(dingdingDepartmentName);
                        xsyDepartment.setParentDepartId(xsyDepartmentParentId);
                        EntityReturnData updateResult =  departmentService.updateDepartment(this.xsyBasicAccessToken,xsyDepartment);
                        LOG.info("Corpid[{}] update department result is ：{}",this.thirdCorpid, JSON.toJSONString(updateResult));

                    }
                }
                //重设xsyDepartmentParentId
                newXsyDepartmentParentId = xsyDepartment.getId();
                //为删除准备
                noDelXsyDepartmentMap.put("xsy_"+relThirdDepXsyDepartment.getXsyDepartmentId(),relThirdDepXsyDepartment);
            }
            if(  ddDepartment.containsKey("subs")){
                JSONArray subDindingDepartments = ddDepartment.getJSONArray("subs");
                //子部门递归调用
                recursionDingDepartments(subDindingDepartments, relMap,rootXsyDepartmentId,newXsyDepartmentParentId,xsyDepartmentMap);
            }

        }
    }

    /**
     * 创建销售易的部门，并创建销售易部门与钉钉部门的关系
     * 返回新部门的ID
     */
    private com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department  createXsyDepartmentAndRelation( JSONObject ddDepartment ,Map<String,RelThirdDepXsyDepartment> relMap,
                                                 long xsyDepartmentParentId,
                                                 Map<String,com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department> xsyDepartmentMap
           ) throws ServiceException {

        com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
        xsyDepartment.setDepartCode("");
        xsyDepartment.setDepartName(ddDepartment.getString("name"));
        xsyDepartment.setDepartType(departType);//约定都为销售
        xsyDepartment.setParentDepartId(xsyDepartmentParentId);

        //创建部门
        EntityReturnData createDepartmentEntityReturnData =  departmentService.createDepartment(this.xsyBasicAccessToken,xsyDepartment);
        long xsyDepartmentId = 0L;
        if(createDepartmentEntityReturnData.isSuccess()){
//                    {"entity":{"id":87105},"success":true}
            JSONObject entity = (JSONObject) createDepartmentEntityReturnData.getEntity();
             xsyDepartmentId = entity.getLong("id");
            xsyDepartment.setId(xsyDepartmentId);
        }
        return xsyDepartment;
    }

    public RelThirdDepXsyDepartment createRelThirdDepXsyDepartment(String ddDepartmentId,long xsyDepartmentId ) throws ServiceException {
//保存映射关系
        RelThirdDepXsyDepartment relThirdDepXsyDepartment = new RelThirdDepXsyDepartment();
        relThirdDepXsyDepartment.setThirdCorpid(this.thirdCorpid);
        relThirdDepXsyDepartment.setThirdDepartmentId(ddDepartmentId);
        relThirdDepXsyDepartment.setThirdSource(Platform.dingding);
        relThirdDepXsyDepartment.setXsyDepartmentId(xsyDepartmentId);
        relThirdDepXsyDepartment.setXsyTenantid(this.xsyTenantid);
        relThirdDepXsyDepartment.setSuiteKey(Env.SUITE_KEY);
        relThirdDepXsyDepartmentService.save(relThirdDepXsyDepartment);
        return relThirdDepXsyDepartment;
    }

    public void setRelMap(Map<String,RelThirdDepXsyDepartment> relMap,RelThirdDepXsyDepartment relThirdDepXsyDepartment){
        relMap.put("xsy-"+this.xsyTenantid+"-"+relThirdDepXsyDepartment.getXsyDepartmentId(),relThirdDepXsyDepartment);
        relMap.put("dd-"+relThirdDepXsyDepartment.getThirdCorpid()+"-"+relThirdDepXsyDepartment.getThirdDepartmentId(),relThirdDepXsyDepartment);
    }

    /**
     * 递归
     * 遍历删除销售易公司的部门对应在钉钉里找不到的部门
     * @param xsyDepartmentTreeNode
     */
    public void  recursionDeleteXsyDepartments(JSONObject xsyDepartmentTreeNode) throws ServiceException {
             if(xsyDepartmentTreeNode.containsKey("subs")){
                    JSONArray departmentNodes  = xsyDepartmentTreeNode.getJSONArray("subs");
                    for(int i=0;i<departmentNodes.size();i++){
                        JSONObject departmentNode = departmentNodes.getJSONObject(i);
                        recursionDeleteXsyDepartments(departmentNode);
                    }
                    //删除所有的子节点后
                    xsyDepartmentTreeNode.remove("subs");
                    recursionDeleteXsyDepartments(xsyDepartmentTreeNode);
            }else{
                 //查找钉钉映射关系
                 //noDelXsyDepartmentMap.put("xsy_"+relThirdDepXsyDepartment.getXsyDepartmentId(),relThirdDepXsyDepartment);
                 long xsyDepartmentId = xsyDepartmentTreeNode.getLong("id");
                 String name = xsyDepartmentTreeNode.getString("name");
                 if(!("dev".equals(name)) && !noDelXsyDepartmentMap.containsKey("xsy_"+xsyDepartmentId)){
                        //删除部门
                     EntityReturnData delResult = departmentService.deleteDeparment(this.xsyBasicAccessToken,xsyDepartmentId);
                     LOG.info("Corpid["+this.thirdCorpid+"]delete department [{}] result is：{}", xsyDepartmentId,JSON.toJSONString(delResult));
                     if(delResult.isSuccess()){
                         //删除映射关系
                         RelThirdDepXsyDepartment rel =  relThirdDepXsyDepartmentService.getRelThirdDepXsyDepartmentByXsytenantIdDepartmentIdAndThirdSource(xsyDepartmentId,this.xsyTenantid,Platform.dingding,Env.SUITE_KEY);
                         if(rel !=null){
                             relThirdDepXsyDepartmentService.delete(rel.getId());
                         }
                     }else {
                         EntityReturnData delEntityReturnData =  departmentService.getDepartmentById(this.xsyBasicAccessToken,xsyDepartmentId);
                         LOG.error("Corpid["+this.thirdCorpid+"] delete department [{}] failure  result is ：{},department is "+ JSON.toJSONString(delEntityReturnData),xsyDepartmentId,JSON.toJSONString(delResult));
                     }
                 }
             }
     }
}