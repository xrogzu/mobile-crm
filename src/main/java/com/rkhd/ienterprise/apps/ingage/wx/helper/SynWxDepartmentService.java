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
import com.rkhd.ienterprise.apps.isales.department.model.Depart;
import com.rkhd.ienterprise.apps.isales.department.service.DepartService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdDepXsyDepartment;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class SynWxDepartmentService {
    private static final Logger LOG = LoggerFactory.getLogger(SynWxDepartmentService.class);

    WXApi wxApi =  WXApi.getWxApi();

    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;
 
    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    /**
     *
     * @param tenantParam
     * @param thirdCorpid
     * @param xsySystemUserAccessToken
     * @param corp_access_token
     * @return
     * @throws WXException
     */
    public JSONObject synWxDepartment(TenantParam tenantParam, String thirdCorpid, String xsySystemUserAccessToken, String corp_access_token,Depart xsyTopdepart) throws WXException, ServiceException {
        //部门列表的返回值参考：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E9%83%A8%E9%97%A8
        JSONObject departmentListJson =  wxApi.getDepartmentList(corp_access_token,null);
//        LOG.info("授权的部门列表为："+departmentListJson.toJSONString());
        JSONObject returnJson = new JSONObject();
        Map<String,WxDepartment> wxDepartmentMap = new HashMap<String, WxDepartment>();
        JSONArray wxDepartmentList = null;
        JSONArray  wxDepartmentTree = null;
        Map<String ,Long> wxdepartment2xsyDepartment = new HashMap<String, Long>();
        Map<String ,String> xsyDepartment2wxdepartment = new HashMap<String, String>();
        if(departmentListJson.getInteger("errcode").intValue() == 0){
            wxDepartmentList = departmentListJson.getJSONArray("department");
//            LOG.info("微信授权的部门列表为={}",wxDepartmentList.toJSONString());
            //获取部门的map
            wxDepartmentMap = departmentJsonArray2Map( wxDepartmentList );
            //组织部门树
            wxDepartmentTree = generateDepartmentTree(   wxDepartmentMap, wxDepartmentList );
            //开始递归同步微信通讯录里的部门
            recursionDepartment(tenantParam,thirdCorpid,xsySystemUserAccessToken,wxDepartmentMap,wxDepartmentTree,wxdepartment2xsyDepartment,xsyDepartment2wxdepartment,xsyTopdepart);
        }
        returnJson.put("wxDepartmentMap",wxDepartmentMap);
        returnJson.put("wxDepartmentList",wxDepartmentList== null?new JSONArray():wxDepartmentList);
        returnJson.put("wxDepartmentTree",wxDepartmentTree== null?new JSONArray():wxDepartmentTree);
        returnJson.put("wxdepartment2xsyDepartment",wxdepartment2xsyDepartment);
        returnJson.put("xsyDepartment2wxdepartment",xsyDepartment2wxdepartment);
        return returnJson;
    }

    /**
     * 部门映射
     * @param departmentList
     * @return
     */
    private static  Map<String,WxDepartment> departmentJsonArray2Map(JSONArray departmentList ){
        Map<String,WxDepartment> map = new HashMap<String, WxDepartment>();
        JSONObject depatmentItem = null;
        for(int i=0;i < departmentList.size() ; i++){
            depatmentItem = departmentList.getJSONObject(i);
            WxDepartment wxDepartment =   JSON.parseObject(depatmentItem.toJSONString(),WxDepartment.class);
            map.put("d-"+wxDepartment.getId(),wxDepartment);
        }
        return map;
    }

    /**
     * 组织部门树形
     *返回结果数据格式为：
     *  [
     *      {WxDepartment对象},....
     *  ]
     * @param wxDepartmentMap
     * @param departmentList
     * @return
     */
    private static JSONArray generateDepartmentTree( Map<String,WxDepartment> wxDepartmentMap,JSONArray departmentList ){
        JSONArray  returnJSONArray = new JSONArray();
//        LOG.info("departmentList="+JSON.toJSONString(departmentList));
//        LOG.info("wxDepartmentMap="+JSON.toJSONString(wxDepartmentMap));
        for(int i=0;i<departmentList.size();i++){
            JSONObject depatmentItem = departmentList.getJSONObject(i);
            int  departmetnParentid = depatmentItem.getIntValue("parentid");
            int  departmetnId = depatmentItem.getIntValue("id");
//            String departmetnName =  depatmentItem.getString("name");
//            if(departmetnParentid == 0){
//                continue;
//            }
            WxDepartment wxDepartment = wxDepartmentMap.get("d-"+departmetnId);
            WxDepartment wxParentDepartment = wxDepartmentMap.get("d-"+departmetnParentid);
//            LOG.info("wxDepartment="+JSON.toJSONString(wxDepartment)+";wxParentDepartment="+JSON.toJSONString(wxParentDepartment));
            if(wxParentDepartment == null || wxParentDepartment.getParentid() == 0  ){
                returnJSONArray.add(wxDepartment);
            }else {
                JSONArray childsArray = wxParentDepartment.getChildes();
                if(childsArray == null){
                    childsArray = new JSONArray();
                    wxParentDepartment.setChildes(childsArray);
                }
                wxParentDepartment.getChildes().add(wxDepartment);
            }
        }
        return returnJSONArray;
    }

    /**
     *
     * @param wxDepartmentMap
     * @param wxDepartmentTree      微信企业的部门树
     */
    private void recursionDepartment(TenantParam tenantParam,String thirdCorpid,String xsySystemUserAccessToken,Map<String,WxDepartment> wxDepartmentMap ,
                                     JSONArray wxDepartmentTree, Map<String ,Long>  wxdepartment2xsyDepartment,  Map<String ,String>  xsyDepartment2wxdepartment,
                                     Depart xsyTopdepart) throws ServiceException {
        for(int i=0;i<wxDepartmentTree.size();i++){
            WxDepartment wxdepartment = (WxDepartment) wxDepartmentTree.get(i);

            //同步当前部门
            execSyncDepartment( tenantParam, thirdCorpid,xsySystemUserAccessToken,   wxDepartmentMap,  wxdepartment,wxdepartment2xsyDepartment,xsyDepartment2wxdepartment,xsyTopdepart );
            //同步子部门
            JSONArray childes = wxdepartment.getChildes();
            if(childes != null){
                recursionDepartment(tenantParam,thirdCorpid,xsySystemUserAccessToken,wxDepartmentMap,childes,wxdepartment2xsyDepartment,xsyDepartment2wxdepartment,xsyTopdepart);
            }
        }
    }

    /**
     * 实际执行同步微信部门
     * 部门不存在，上级部门被授权了且存在      执行新建操作
     * 部门不存在，上级部门未被授权但是存在  创建部门，且上级部门设置为全公司
     * 部门不存在，上级部门未被授权且也不存在        创建部门，且上级部门设置为全公司。出现这种情况的原因是上级部门未授权
     *
     * 部门存在，上级部门被授权了且存在    存在  执行修改操作
     * 部门存在，上级部门未被授权但是存在  修改部门，且上级部门设置为全公司
     *
     * @param wxDepartmentMap
     * @param wxdepartment
     */
    private void execSyncDepartment(TenantParam tenantParam,String thirdCorpid,String xsySystemUserAccessToken, Map<String,WxDepartment> wxDepartmentMap, WxDepartment wxdepartment,
                                    Map<String ,Long>  wxdepartment2xsyDepartment,  Map<String ,String>  xsyDepartment2wxdepartment,Depart xsyTopdepart ) throws ServiceException {

        if(wxdepartment.getParentid() == 0){ //全公司则不需要构建

            long  xsyDepartmentId = xsyTopdepart.getId();
            wxdepartment2xsyDepartment.put(""+wxdepartment.getId(),xsyDepartmentId);
            xsyDepartment2wxdepartment.put(""+xsyDepartmentId,""+wxdepartment.getId());

            return;
        }
        LOG.info("开始同步部门："+JSON.toJSONString(wxdepartment));
//        LOG.info("wxDepartmentMap="+JSON.toJSONString(wxDepartmentMap));
        String thirdDepartmentId = wxdepartment.getId()+"";
        RelThirdDepXsyDepartment relThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,thirdDepartmentId, Platform.WEIXIN, WXAppConstant.APP_ID);
        WxDepartment wxParentDepartment =  wxDepartmentMap.get("d-"+wxdepartment.getParentid());
//        LOG.info("relThirdDepXsyDepartment="+JSON.toJSONString(relThirdDepXsyDepartment));
        long xsyParentDepartmentId = 0;
        String executeMethod = "create";
        long xsyDepartmentId = 0L;
        if(relThirdDepXsyDepartment == null){
            executeMethod = "create";
            if(wxParentDepartment != null){//上级被授权了
                RelThirdDepXsyDepartment parentRelThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,wxParentDepartment.getId()+"", Platform.WEIXIN, WXAppConstant.APP_ID);

                if( parentRelThirdDepXsyDepartment != null){//上级部门存在
                    xsyParentDepartmentId = parentRelThirdDepXsyDepartment.getXsyDepartmentId();
                }else {
                    LOG.info("parentRelThirdDepXsyDepartment="+JSON.toJSONString(parentRelThirdDepXsyDepartment));
                    xsyParentDepartmentId = xsyTopdepart.getId();
                }
            }else {//上级没有被授权了
                xsyParentDepartmentId = xsyTopdepart.getId();
            }
        }else {
            executeMethod = "update";
            xsyDepartmentId = relThirdDepXsyDepartment.getXsyDepartmentId();
            if(wxParentDepartment != null) {//上级被授权了
                RelThirdDepXsyDepartment parentRelThirdDepXsyDepartment = relThirdDepXsyDepartmentService.getRelThirdDepXsyDepByThirdCorpidDepIdAndSource(thirdCorpid,wxParentDepartment.getId()+"", Platform.WEIXIN, WXAppConstant.APP_ID);
               LOG.info("parentRelThirdDepXsyDepartment="+JSON.toJSONString(parentRelThirdDepXsyDepartment));
                if( parentRelThirdDepXsyDepartment != null){//上级部门存在
                    xsyParentDepartmentId = parentRelThirdDepXsyDepartment.getXsyDepartmentId();
                }else{
                    xsyParentDepartmentId = xsyTopdepart.getId();
                }
            }else {
                //上级没有被授权了
                xsyParentDepartmentId = xsyTopdepart.getId();
            }
        }
        if("update".equals(executeMethod)){
            EntityReturnData updateDepartmentEntityReturnData = updateDepartment( xsySystemUserAccessToken, wxdepartment, xsyParentDepartmentId, xsyDepartmentId );
            if(updateDepartmentEntityReturnData.isSuccess()){
                LOG.info("update department success,{}",JSON.toJSONString(updateDepartmentEntityReturnData));
            }else {
                if("20000014".equals(updateDepartmentEntityReturnData.getErrorCode())){
                    relThirdDepXsyDepartmentService.delete(relThirdDepXsyDepartment.getId());
                    xsyDepartmentId = doCreateDepartment( tenantParam, thirdCorpid, xsySystemUserAccessToken,  wxdepartment,  xsyParentDepartmentId);

                }else{
                    LOG.error("update department fail,updateDepartmentEntityReturnData＝"+JSON.toJSONString(updateDepartmentEntityReturnData)+";xsySystemUserAccessToken="+xsySystemUserAccessToken+";wxdepartment="+JSON.toJSONString(wxdepartment)
                            +";xsyParentDepartmentId="+xsyParentDepartmentId+";xsyDepartmentId="+xsyDepartmentId);
                }


            }
        }else if("create".equals(executeMethod)){
            xsyDepartmentId = doCreateDepartment( tenantParam, thirdCorpid, xsySystemUserAccessToken,  wxdepartment,  xsyParentDepartmentId);
        }
        if(xsyDepartmentId != 0){
            wxdepartment2xsyDepartment.put(""+wxdepartment.getId(),xsyDepartmentId);
            xsyDepartment2wxdepartment.put(""+xsyDepartmentId,""+wxdepartment.getId());
        }

    }

    public Long doCreateDepartment(TenantParam tenantParam,String thirdCorpid,String xsySystemUserAccessToken, WxDepartment wxdepartment, long xsyParentDepartmentId) throws ServiceException {
        EntityReturnData createDepartmentEntityReturnData = createDepartment( xsySystemUserAccessToken, wxdepartment, xsyParentDepartmentId );
        Long xsyDepartmentId = 0L;
        if(createDepartmentEntityReturnData.isSuccess()){
            LOG.info("create department success,{}",JSON.toJSONString(createDepartmentEntityReturnData));
            JSONObject entity = (JSONObject) createDepartmentEntityReturnData.getEntity();

            //创建映射关系
            createRelThirdDepXsyDepartment( thirdCorpid, wxdepartment.getId()+"",tenantParam.getTenantId(), entity.getLong("id") );
            xsyDepartmentId =  entity.getLong("id");

        }else {
            LOG.error("create department fail,{}",JSON.toJSONString(createDepartmentEntityReturnData)+";xsySystemUserAccessToken="+xsySystemUserAccessToken+";wxdepartment="+JSONObject.toJSONString(wxdepartment)+";xsyParentDepartmentId="+xsyParentDepartmentId);
        }
        return xsyDepartmentId;

    }

    private EntityReturnData createDepartment(String xsySystemUserAccessToken,WxDepartment wxdepartment,Long xsyParentDepartmentId ){
        //开始创建部门
        com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
        xsyDepartment.setDepartCode("");
        xsyDepartment.setDepartName(wxdepartment.getName());
        xsyDepartment.setDepartType(2L);//约定都为销售
        xsyDepartment.setParentDepartId(xsyParentDepartmentId);

        //创建部门
        EntityReturnData createDepartmentEntityReturnData =  departmentService.createDepartment(xsySystemUserAccessToken,xsyDepartment);
        return createDepartmentEntityReturnData;
    }

    private EntityReturnData updateDepartment(String xsySystemUserAccessToken,WxDepartment wxdepartment,Long xsyParentDepartmentId,Long xsyDepartmentId ){
        //开始修改部门
        com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department xsyDepartment = new com.rkhd.ienterprise.apps.ingage.dingtalk.dto.Department();
        xsyDepartment.setDepartCode("");
        xsyDepartment.setDepartName(wxdepartment.getName());
        xsyDepartment.setDepartType(2L);//约定都为销售
        xsyDepartment.setParentDepartId(xsyParentDepartmentId);
        xsyDepartment.setId(xsyDepartmentId);

        //修改部门
        EntityReturnData updateDepartmentEntityReturnData =  departmentService.updateDepartment(xsySystemUserAccessToken,xsyDepartment);
        return updateDepartmentEntityReturnData;
    }

    public RelThirdDepXsyDepartment createRelThirdDepXsyDepartment(String thirdCorpid,String wxDepartmentId,long  xsyTenantid,long xsyDepartmentId ) throws ServiceException {
//保存映射关系
        RelThirdDepXsyDepartment relThirdDepXsyDepartment = new RelThirdDepXsyDepartment();
        relThirdDepXsyDepartment.setThirdCorpid(thirdCorpid);
        relThirdDepXsyDepartment.setThirdDepartmentId(wxDepartmentId);
        relThirdDepXsyDepartment.setThirdSource(Platform.WEIXIN);
        relThirdDepXsyDepartment.setXsyDepartmentId(xsyDepartmentId);
        relThirdDepXsyDepartment.setXsyTenantid(xsyTenantid);
        relThirdDepXsyDepartment.setSuiteKey(WXAppConstant.APP_ID);
        relThirdDepXsyDepartmentService.save(relThirdDepXsyDepartment);
        return relThirdDepXsyDepartment;
    }
}
