package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.DepartmentService;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Namespace("/dingtalk/department")
public class DepartmentAction extends BaseAction{
    private  static Logger LOG = LoggerFactory.getLogger(DepartmentAction.class);

    @Autowired
    @Qualifier("mwebDepartmentService")
    private DepartmentService departmentService;

    /**
     * 查询当前登录用户可以看到的部门列表
     * @return
     */
    @Action(value = "mydepartment", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String mydepartment()  {
        EntityReturnData ret = new EntityReturnData();
        try{
             ret = departmentService.getMyDepartmenttree(getAccessToken());
            if(!ret.isSuccess()){
                LOG.error("departmentService.getMyDepartmenttree error; token="+getAccessToken()+";apiReturnData = "+JSON.toJSONString(ret));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
}
