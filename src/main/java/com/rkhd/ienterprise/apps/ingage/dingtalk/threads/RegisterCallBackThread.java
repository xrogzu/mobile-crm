package com.rkhd.ienterprise.apps.ingage.dingtalk.threads;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.eventchange.EventChangeHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册回调信息接口
 */
public class RegisterCallBackThread  extends Thread{
    private static final Logger LOG = LoggerFactory.getLogger(RegisterCallBackThread.class);

    private String  requestUrl;
    private String thirdCorpidAccessToken;
    private String thirdCorpid;

    private String contextPath;

    public RegisterCallBackThread(String requestUrl, String thirdCorpidAccessToken, String thirdCorpid, String contextPath){
        super();
        this.requestUrl = requestUrl;
        this.thirdCorpidAccessToken = thirdCorpidAccessToken;
        this.thirdCorpid = thirdCorpid;
        this.contextPath = contextPath;
    }
    public void run(){
        deExecute(0,contextPath);

    }

    /**
     * 针对钉钉不稳定因素出现，这里进行频次限制
     * @param i
     */
    private void deExecute(int i, String contextPath){
        if(i>2){
            return;
        }
        LOG.info("Corpid["+thirdCorpid+"] begin reg callback info");
        String flag = ".com";

        String reqHostString = requestUrl.substring(0,requestUrl.indexOf(flag))+flag;

        List<String> callBackTagList = new ArrayList<String>();
        callBackTagList.add("user_add_org");
        callBackTagList.add("user_modify_org");
        callBackTagList.add("user_leave_org");
        callBackTagList.add("org_admin_add");
        callBackTagList.add("org_admin_remove");
        callBackTagList.add("org_dept_create");
        callBackTagList.add("org_dept_modify");
        callBackTagList.add("org_dept_remove");

        //查询已有接口
        String corpAccessToken = this.thirdCorpidAccessToken;
        String searchResult = null;
        try{
            searchResult =  EventChangeHelper.getEventChange(thirdCorpidAccessToken);
        }catch(Exception e){
            //  e.printStackTrace();
        }
        try {
            if(StringUtils.isNotBlank(searchResult)){
                JSONObject searchResultJSON = JSON.parseObject(searchResult);
                JSONArray call_back_tag = searchResultJSON.getJSONArray("call_back_tag");
                String url = searchResultJSON.getString("url");
                if(call_back_tag.size()>0 || StringUtils.isNotBlank(url)){
                    String delResult = EventChangeHelper.deleteEventChange(thirdCorpidAccessToken);
                    LOG.info("Corpid["+thirdCorpid+"] delete callback Interface,result is :"+delResult);
                }
            }
            String regPath = reqHostString+contextPath+"/dingtalk/eventChangeReceive.action";
            LOG.info("Corpid["+thirdCorpid+"] callback path is :"+regPath);
            String registInfo = EventChangeHelper.registerEventChange(corpAccessToken, callBackTagList, Env.TOKEN, Env.ENCODING_AES_KEY, regPath);
            LOG.info("Corpid["+thirdCorpid+"] reg callback result is :"+registInfo);
            try{
//                查询注册结果，验证使用
                searchResult =  EventChangeHelper.getEventChange(thirdCorpidAccessToken);
                LOG.info("Corpid["+thirdCorpid+"] search reg callbackResult is :"+searchResult);
            }catch(Exception e){
                //  e.printStackTrace();
            }
        } catch (Exception e) {
            LOG.error ("Corpid["+thirdCorpid+"] reg callback failure .error msg is "+e.getMessage());
            deExecute(++i,contextPath);
            e.printStackTrace();
        }
    }

}
