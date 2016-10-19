package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.GroupService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

@Namespace("/dingtalk/group")
public class GroupAction extends BaseAction{
    private  static Logger LOG = LoggerFactory.getLogger(GroupAction.class);

    /**
     * 新设置的相关成员id，多个用逗号分隔。
     */
    private String setOwnerIds;

    private long  businessId;//实体对象的主键id

    private long belongs;//取值范围 1:客户,2：联系人,3:销售机会



    @Autowired
    @Qualifier("mwebGroupService")
    private GroupService groupService;

    /**
     * 查询已选择的相关成员
     * @return
     * @throws Exception
     */
    @Action(value = "relateowner", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String relateowner()  {

        EntityReturnData ret = null;
        try{
            ret = groupService.queryMember(getAccessToken(),getBusinessId(), getBelongs(),0);
           if(!ret.isSuccess()){
                LOG.error("groupService.queryMember error ,token="+getAccessToken()+"，businessId="+getBusinessId()+";apiReturnData is："+JSONObject.toJSONString(ret));
            }
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
    /**
     * 设置相关团队成员
     * @return
     * @throws Exception
     */
    @Action(value = "setrelateowner", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String setrelateowner()  {
        EntityReturnData ret = null;
        try{

            String oldSelIds = "";
            String setOwenerIdArray  = getSetOwnerIds();
            EntityReturnData   oldSelected = groupService.queryMember(getAccessToken(),getBusinessId(), getBelongs(),0);
            List<Long> delIds = new ArrayList<Long>();
            List<Long> addIds = new ArrayList<Long>();
            if(oldSelected.isSuccess()){
                JSONObject users = (JSONObject) oldSelected.getEntity();
                JSONArray userList = users.getJSONArray("users");
                //获取需要删除的团队成员的id
                for(int i=0;i<userList.size();i++){
                    JSONObject jsonObject = userList.getJSONObject(i);
                    if((","+setOwenerIdArray+",").indexOf(","+jsonObject.getLong("id")+",")<0){
                        delIds.add(jsonObject.getLong("id"));
                    }
                    if(oldSelIds.length()>0){
                            oldSelIds += ",";
                    }
                }

            }else{
                LOG.error("groupService.queryMember error ,token="+getAccessToken()+"，businessId="+getBusinessId()+";belongs="+getBelongs()+"; apiReturnData is："+JSONObject.toJSONString(oldSelected));
            }
            if(StringUtils.isNotBlank(setOwenerIdArray)){
                //获取需要新增的团队成员的id
                String[] setOwenerIdArrayList = setOwenerIdArray.split(",");
                for(int i=0;i<setOwenerIdArrayList.length;i++){
                    String beSelId = setOwenerIdArrayList[i];
                    if((","+oldSelIds+",").indexOf(","+beSelId+",")<0){
                        addIds.add(new Long(beSelId));
                    }
                }
            }
            //先删除操作
            boolean delReultBoolean = false;
            EntityReturnData delResult = null;
            if(delIds.size() >0 ){
                delResult = groupService.quitMember(getAccessToken(),getBelongs(),getBusinessId(), delIds);
                if(delResult.isSuccess()){
                    delReultBoolean = true;
                }
            }else{
                delReultBoolean = true;
            }
            //再添加操作
            EntityReturnData addResult = null;
            boolean addReultBoolean = false;
            if(delReultBoolean && addIds.size() >0 ){
                addResult = groupService.joinRelated(getAccessToken(),getBelongs(),getBusinessId(), addIds);
                if(addResult.isSuccess()){
                    addReultBoolean = true;
                }else{
                    LOG.error("groupService.queryMember error ,token="+getAccessToken()+"，businessId="+getBusinessId()+";belongs="+getBelongs()+"; apiReturnData is："+JSONObject.toJSONString(oldSelected));
                    addReultBoolean = false;

                }
            }else{
                    addReultBoolean = true;
            }
            ret = new EntityReturnData();
            if(addReultBoolean && delReultBoolean){
                ret.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            ret =  new EntityReturnData();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }


    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }

    public long getBelongs() {
        return belongs;
    }

    public void setBelongs(long belongs) {
        this.belongs = belongs;
    }

    public String getSetOwnerIds() {
        return setOwnerIds;
    }

    public void setSetOwnerIds(String setOwnerIds) {
        this.setOwnerIds = setOwnerIds;
    }
}
