package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.XsyApiUserService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by dell on 2016/1/13.
 */
@Namespace("/dingtalk/xsyuser")
public class XsyUserAction extends BaseAction{
    private static final Logger LOG = LoggerFactory.getLogger(XsyUserAction.class);


    @Autowired
    @Qualifier("mwebXsyApiUserService")
    private XsyApiUserService xsyApiUserService;

    private int pageNo = 0;//当前页号

    private int pagesize = 0;//每页显示条数

    private String searchName = null;

    @Action(value = "pager", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getpager() throws Exception{
        try{
            EntityReturnData ret = xsyApiUserService.getUserList(getAccessToken(),getSearchName(),getPageNo(),getPagesize());
            if(ret.isSuccess() && ret.getEntity() != null ) {
                JSONObject pageData = (JSONObject) ret.getEntity();
                long totalSize = pageData.getLong("totalSize");
                long totalPage = ((totalSize % pagesize == 0) ? (totalSize / pagesize) : (totalSize / pagesize + 1));
                pageData.put("totalPage", totalPage);
                pageData.put("pageNo", getPageNo());
                pageData.put("pagesize", getPagesize());

                pageData.remove("count");
//                JSONArray contacts = pageData.getJSONArray("records");
                // 防止xss攻击
                ret.setEntity(CodeFilter.xddTerminator(pageData));

            }
            getRequest().setAttribute("jsondata",ret);

        }catch (Exception e){e.printStackTrace();}

        return  SUCCESS;
    }
    /************************************************************************************************/
    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPagesize() {
        if(pagesize == 0){
            pagesize = XsyUtils.defalut_page_limit;
        }
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
