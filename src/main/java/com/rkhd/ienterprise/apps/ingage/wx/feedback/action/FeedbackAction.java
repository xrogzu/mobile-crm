package com.rkhd.ienterprise.apps.ingage.wx.feedback.action;

import cloud.multi.tenant.TenantParam;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.ErrorObj;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.XsyUserDto;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.XsyUtils;
import com.rkhd.ienterprise.apps.ingage.utils.ErrorCode;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.dtos.SessionUser;
import com.rkhd.ienterprise.base.feedback.model.Feedback;
import com.rkhd.ienterprise.base.feedback.service.FeedbackService;
import com.rkhd.ienterprise.base.jira.service.JiraService;
import com.rkhd.ienterprise.exception.WebException;
import com.rkhd.ienterprise.web.RequestUtil;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dell on 2016/3/2.
 */
@Namespace("/wx/feedback")
public class FeedbackAction extends WxBaseAction {
    private  static Logger LOG = LoggerFactory.getLogger(FeedbackAction.class);
    private static final String PROJECT_KEY = "FEEDBACK";
    private static final String INIT_STATUS = "10111";
    private static final Long ISSUE_TYPE = 10100L;
    private String content;
    private String contactWay;
    private short device = 3;//安卓1: ios 2;pc:3
    private String version = "1.0";

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private JiraService jiraService;

    @Action(value = "add",
            results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")}
    )
    public String addFeedback() throws WebException {
        EntityReturnData returnData = new EntityReturnData();
        try {
            if(XsyUtils.containEmojiCharacter(content)){
                returnData.setSuccess(false);
                ErrorObj errorObj = new ErrorObj();
                errorObj.setKey("EMOJICHAR");
                errorObj.setMsg("包含表情字符");
                errorObj.setStatus(ErrorCode.EMOJICHAR.getErrorCode());
                returnData.setEntity(errorObj);
            }else {
                SessionUser sessionUser = getSessionUser();
                TenantParam tenantParam = sessionUser.getXsy_TenantParam();
                Feedback fb = new Feedback();
                fb.setContent(content);
                fb.setProblemType((short) 0);
                fb.setUserId(sessionUser.getXsy_user().getId());
                fb.setRemoteIp(RequestUtil.getClientIpAddress(getRequest()));
                fb.setBrowser(RequestUtil.getRemoteBrowser(getRequest()));
                fb.setOs(RequestUtil.getRemoteOs(getRequest()));
                fb.setRefer(subRefer());
                fb.setContactWay(contactWay);//contactWay
                fb.setVersion(version);//version
                fb.setDevice(getDevice());//device
                fb.setDealStatus(INIT_STATUS);//dealStatus
                fb.setTenantId(tenantParam.getTenantId());
                fb.setCreatedAt(System.currentTimeMillis());
                //fb.setIssueKey();//issueKey

                fb.setId(feedbackService.save(fb, tenantParam));

                //sendMail(fb);
                String xsy_TenantParamName = sessionUser.getXsy_TenantName();
                XsyUserDto xsy_user = sessionUser.getXsy_user();
                //写入jira
                Map<String, Object> fieldMap = getFeedbackFieldMap(xsy_TenantParamName,  xsy_user);
                fieldMap.put("feedbackId", fb.getId());

                jiraService.asynCreateIssueWithFeedback(PROJECT_KEY, ISSUE_TYPE, "{noformat}" + content + "{noformat}", getSummery(xsy_TenantParamName), fieldMap, tenantParam);
                returnData.setSuccess(true);
            }
        } catch (Exception e) {
            LOG.error("FeedbackAction add error ,msg="+e.getMessage(), e);
            throw new WebException(e);
        }
        getRequest().setAttribute("jsondata",returnData);
        return SUCCESS;
    }
    private String subRefer() {
        final String REG = "http://[\\w.]+[:\\d+]*/";
        Pattern pattern = Pattern.compile(REG);
        String refer = RequestUtil.getRefer(getRequest());
        if (refer != null) {
            Matcher matcher = pattern.matcher(RequestUtil.getRefer(getRequest()));
            return "/" + matcher.replaceFirst("");
        }
        return null;
    }
    //获取问题主题
    public String getSummery(String xsy_TenantParamName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return xsy_TenantParamName+ sdf.format(new Date());
    }

    public Map<String, Object> getFeedbackFieldMap(String xsy_TenantParamName, XsyUserDto xsy_user) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z",getRequest().getLocale());
        Map<String,Object> option = new HashMap<String, Object>();
        option.put("value","等待处理");
        option.put("id","10111");
        //联系人
        fieldMap.put("customfield_10307",xsy_user.getName());
        //客户名称
        fieldMap.put("customfield_10306",xsy_TenantParamName);
        //提交时间
        fieldMap.put("customfield_10309",sdf.format(new Date()));
        //联系方式
        fieldMap.put("customfield_10308",contactWay);
        //处理状态，默认等待处理
        fieldMap.put("customfield_10310",option);
        //提交设备
        fieldMap.put("customfield_10311","web");
        //版本信息
        fieldMap.put("customfield_10312",version);
        return fieldMap;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public short getDevice() {
        return device;
    }

    public void setDevice(short device) {
        this.device = device;
    }
}
