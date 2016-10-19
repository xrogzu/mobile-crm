package com.rkhd.ienterprise.apps.ingage.wx.expose.action;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.EntityReturnData;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.wx.base.WxBaseAction;
import com.rkhd.ienterprise.apps.ingage.wx.demo.action.DemoAction;
import com.rkhd.ienterprise.apps.ingage.wx.helper.ChangeAuthCompositionHelper;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxInfoType;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.platform.exception.PaasException;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Namespace("/wx/expose")
public class ExposeAction extends WxBaseAction {

    private static final Logger LOG = LoggerFactory.getLogger(DemoAction.class);
    @Autowired
    private ChangeAuthCompositionHelper changeAuthCompositionHelper;

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Action(value = "doSync", results = {@Result(name = SUCCESS, location = PAGES_ROOT+"/jsondata.jsp")})
    public String doSync()  {
        String tenantId = getRequest().getParameter("tenantId");
        EntityReturnData ret = new EntityReturnData();
        try {

            RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByTenantIdAndSource(tenantId, Platform.WEIXIN, WXAppConstant.APP_ID);
            JSONObject retJson = null;
            boolean success = false;
            if(relThirdCorpXsyTenant!= null){
                 retJson = changeAuthCompositionHelper.doSyncAuth(relThirdCorpXsyTenant.getThirdCorpid(), WXAppConstant.APP_ID , WxInfoType.PC_SYNC);
                success =  retJson.getBoolean("success");
            }else {
                 retJson = new JSONObject();
                success = false;
                retJson.put("success",false);
                retJson.put("errorcode","000001");
                retJson.put("errmsg","no get relThirdCorpXsyTenant");
            }
            ret.setSuccess(success);
            ret.setEntity(retJson);
        } catch (WXException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (PaasException e) {
            e.printStackTrace();
        }
        getRequest().setAttribute("jsondata",ret);
        return  SUCCESS;
    }
}
