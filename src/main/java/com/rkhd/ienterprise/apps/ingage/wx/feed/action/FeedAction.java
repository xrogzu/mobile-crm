package com.rkhd.ienterprise.apps.ingage.wx.feed.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.services.ActivityRecordService;
import com.rkhd.ienterprise.apps.ingage.services.FeedService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.CodeFilter;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

@Namespace("/wx/feed")
public class FeedAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(FeedAction.class);

    long belongId = 1;
    long objectId = 0;

    @Autowired
    private FeedService feedService;

    @Action(value = "list", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String getFeedList()  {
        EntityReturnData ret = null;
        try{
             ret =  feedService.findFeedList(getAccessToken(),getBelongId(),getObjectId(),getPageNo(),getPagesize());
            if(ret.isSuccess()){
                // 防止xss攻击
                ret.setEntity(CodeFilter.xddTerminator(ret.getEntity()));
            }else{
                LOG.error("authorization={}，user_token="+getAccessToken()+";belongid="+getBelongId()+";objectid="+getObjectId()+";    ret={}",getAccessToken(),JSON.toJSONString(ret));
            }
        }catch (Exception e){
            ret = new EntityReturnData();
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }

    public JSONArray getActivityRecordType(){

        HttpServletRequest request =  getRequest();

        JSONArray busiparamsArray  = (JSONArray) request.getSession().getAttribute("rctivityRecordTypes");
        if(busiparamsArray == null){
            EntityReturnData entityReturnData = new ActivityRecordService().getDesc(getAccessToken());
            if(entityReturnData.isSuccess()){
                JSONObject descJSONObject = (JSONObject) entityReturnData.getEntity();
                 busiparamsArray = descJSONObject.getJSONArray("busiparams");
                JSONObject paramsJSONObject = null;
                for(int i=0;i<busiparamsArray.size();i++){
                    paramsJSONObject = busiparamsArray.getJSONObject(i);
//                    LOG.info("{}", JSON.toJSONString(paramsJSONObject));
                    if(paramsJSONObject.getString("fieldname").equals("type")){
                        busiparamsArray = paramsJSONObject.getJSONArray("params");
                        break;
                    }
                }
                request.getSession().setAttribute("rctivityRecordTypes",busiparamsArray);
            }else {
                LOG.error("search activity record fail ，authorization={}，apiReturnData is：{}",getAccessToken(),JSONObject.toJSONString(entityReturnData));
            }
        }

        return busiparamsArray;
    }

    public long getBelongId() {
        return belongId;
    }

    public void setBelongId(long belongId) {
        this.belongId = belongId;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }
}
