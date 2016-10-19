package com.rkhd.ienterprise.apps.ingage.wx.helper;

import cloud.multi.tenant.TenantParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.exception.WXException;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxRuntime;
import com.rkhd.ienterprise.apps.manager.register.service.RegisterService;
import com.rkhd.ienterprise.base.license.model.License;
import com.rkhd.ienterprise.base.license.service.LicenseService;
import com.rkhd.ienterprise.base.multitenant.model.Tenant;
import com.rkhd.ienterprise.base.multitenant.service.TenantService;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdCorpXsyTenant;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import com.rkhd.ienterprise.thirdparty.service.RelThirdCorpXsyTenantService;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.platform.exception.PaasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class SynWxCorpService {

    private static final Logger LOG = LoggerFactory.getLogger(SynWxCorpService.class);

    @Autowired
    private RelThirdCorpXsyTenantService relThirdCorpXsyTenantService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RelThirdTokenService relThirdTokenService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LicenseService licenseService;

//    3：同步企业及映射关系。
//            3.1 根据微信企业的corpid查看映射关系，查看企业是否存在，如果存在则修改企业的license信息,否则继续执行下面的工作
//    3.2 根据企业名称查询已有租户是否存在。如果不存在则创建租户，初始化租户。
//            3.2 根据租户创建租户映射关系

    /**
     *
     * 参数值为获得永久授权码得到的信息 [获取企业号的永久授权码]
     * 参考网址：http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BA%94%E7%94%A8%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     * 目标:实现公司同步
     * * @param authInfo
     */
    public JSONObject synWxCorp(JSONObject authInfo) throws ServiceException, WXException, PaasException {
        LOG.info("authInfo="+authInfo.toJSONString());
        //授权企业信息
        JSONObject corp_info = authInfo.getJSONObject("auth_corp_info");
        String  corp_name = corp_info.getString("corp_name");
        //第三方企业id
        String  thirdCorpid = corp_info.getString("corpid") ;

        RelThirdCorpXsyTenant relThirdCorpXsyTenant = relThirdCorpXsyTenantService.getByThirdPlatformIdAndSource(thirdCorpid, Platform.WEIXIN, WXAppConstant.APP_ID);
        TenantParam tenantParam = null;
        long xsyTenantid = 0L;
        boolean success = true;
        String errorCode = "";

        if(relThirdCorpXsyTenant == null){
            tenantParam = registerService.getAnInitializedTenant(CommonService.tenantType);
            if(tenantParam != null){
                xsyTenantid = tenantParam.getTenantId();
                // 更新租户信息
                updateTenant(xsyTenantid,corp_name);
                //license
                licenseService.initTrialTenant(tenantParam);   //试用License数据初始化

                commonService.updateLicenseEdition(xsyTenantid);

                customSetLicense(tenantParam);
                //            建立租户级别映射关系
                this.createAndSaveRelThirdCorpXsyTenant(thirdCorpid,xsyTenantid);
            }else{
                LOG.error("getAnInitializedTenant return result is null");
                success = false;
                errorCode = "XsyTenantidError";
            }

        }else {
            xsyTenantid = relThirdCorpXsyTenant.getXsyTenantid();
            tenantParam = new TenantParam(xsyTenantid);
            //二次授权 不修改lincenses的截止日期
           // customSetLicense(tenantParam);
        }
        String  permanent_code = null;
        String  access_token = null;
        if(success){
            //永久授权码
            permanent_code = authInfo.getString("permanent_code");
            access_token =  authInfo.getString("access_token");
            //保存或更新永久授权码
              saveWxToken( thirdCorpid, permanent_code, access_token);
        }


        JSONObject executeResult = new JSONObject();
        executeResult.put("permanent_code",permanent_code);
        executeResult.put("access_token",access_token);
        executeResult.put("tenantParam",tenantParam);
        executeResult.put("success",success);
        executeResult.put("errorCode",errorCode);
        return executeResult;
    }

    private void createAndSaveRelThirdCorpXsyTenant(String thirdCorpid,long xsyTenantid) throws ServiceException {
        RelThirdCorpXsyTenant rel = new RelThirdCorpXsyTenant();
        rel.setThirdSource(Platform.WEIXIN);//来源：微信
        rel.setThirdCorpid(thirdCorpid);
        rel.setThridAuthStatus("authorize");//状态：授权
        rel.setXsyTenantid(xsyTenantid);
        rel.setSuiteKey(WXAppConstant.APP_ID);

        relThirdCorpXsyTenantService.save(rel);
    }

    /**
     * 保存微信永久授权码和accessToken
     * @param thirdCorpid
     * @param permanent_code
     * @throws ServiceException
     * @throws WXException
     */
    public RelThirdToken saveWxToken( String  thirdCorpid,String permanent_code,String access_token) throws ServiceException, WXException {
        RelThirdToken relThirdToken = relThirdTokenService.getRelThirdTokenByThirdSourceAndThirdCorpid(Platform.WEIXIN,thirdCorpid,WXAppConstant.APP_ID);
        boolean newAdd = false;
        if(relThirdToken == null){
            newAdd = true;
            relThirdToken = new RelThirdToken();
        }

        Date now = new Date();
        relThirdToken.setTokenValue(access_token);
        //relThirdToken.setJsapiTicket();
        relThirdToken.setPermanentCode(permanent_code);
        relThirdToken.setCreateAt(now);
        relThirdToken.setExpiresIn(7200L);
        relThirdToken.setThirdCorpid(thirdCorpid);
        relThirdToken.setThirdSource(Platform.WEIXIN);
        relThirdToken.setSuiteKey(WXAppConstant.APP_ID);
        if(newAdd){
            relThirdTokenService.save(relThirdToken);
            LOG.info("new added corp`s token  success ,it is {}", JSON.toJSONString(relThirdToken));
        }else{
            relThirdTokenService.update(relThirdToken);
            LOG.info("update corp`s token  success ,it is {}",JSON.toJSONString(relThirdToken));
        }
        return relThirdToken;
    }
    private void updateTenant(Long tenantId,String companyName) throws ServiceException {
        Tenant tenant = tenantService.get(tenantId);
        tenant.setCompany(companyName);
        //tenant.setType(appType);
        tenant.setInitStatus(200);
        tenant.setCreatedAt(System.currentTimeMillis());
        //getTenantParam().setType(appType);

        tenantService.update(tenant);
    }

    public void customSetLicense(TenantParam tenantParam) throws ServiceException {
        //个性化： 修改钉钉授权用户人数和终止日期
        List<License> licenses =  licenseService.getLicenses(tenantParam);
        Calendar c = Calendar.getInstance();
        c.add( Calendar.YEAR, 1);//365天
        c.clear(Calendar.MILLISECOND);
        long expireTime =  c.getTimeInMillis();
        Integer dingLicenseUserNum = 200000;

        for(License licenseItem: licenses){
            licenseItem.setExpireTime(expireTime);

            //licenseItem.setUserNum(dingLicenseUserNum);
            licenseService.update(licenseItem,tenantParam);
        }
    }

}
