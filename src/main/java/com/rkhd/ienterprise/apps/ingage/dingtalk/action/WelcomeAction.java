package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdDepXsyDepartmentService;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dell on 2015/12/15.
 */
@Namespace("/wel")
public class WelcomeAction extends BaseAction{
    private static final Logger LOG = LoggerFactory.getLogger(WelcomeAction.class);

//    @Autowired
//    private ContentService contentService;

    private DingService dDingService;


    @Autowired
    private RelThirdDepXsyDepartmentService relThirdDepXsyDepartmentService;

    @Action(value = "index", results = {@Result(name = "success", location = PAGES_ROOT+"/welcome.jsp")})
    public String index() throws Exception{

       return  SUCCESS;
    }


    private Long time;
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

//    @Action(value = "json",
//            results = {@Result(name = "success",  location = PAGES_ROOT + "/jsondata.jsp")})
//    public String json() {
//        ActionContext actionContext = ActionContext.getContext();
//        LOG.debug("json 请求到达  time="+time);
//        Map<String,Object> jsonData = new HashMap<String,Object>();
//        jsonData.put("time",time);
//
//        getRequest().setAttribute("jsondata",jsonData);
//        return SUCCESS;
//    }

}
