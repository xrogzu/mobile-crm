package com.rkhd.ienterprise.apps.ingage.wx.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Platform;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.wx.sdk.config.WXAppConstant;
import com.rkhd.ienterprise.apps.ingage.wx.utils.WxUtils;
import com.rkhd.ienterprise.base.passport.model.Passport;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.model.RelThirdPassportXsyPassportId;
import com.rkhd.ienterprise.thirdparty.service.RelThirdPassportXsyPassportIdService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynPassportService {
    private static final Logger LOG = LoggerFactory.getLogger(SynPassportService.class);

    @Autowired
    private RelThirdPassportXsyPassportIdService relThirdPassportXsyPassportIdService;


    @Autowired
    private CommonService commonService;



//    2：同步管理员或普通职员的passport及映射关系。
//            2.1  用户如果没有weixinid，则工作停止。如果是系统管理员是则所有工作停止，普通员工则跳过继续向下执行；
//            2.2 使用激活用户的moile或 email建立passport。
//            2.2.1 如果都不存在moile或 email 则工作停止。
//            2.2.2  如果存在moile或 email 其中一个或者都存在，则查看根据已有mobile或email能否查到passport.
//    2.2.2.1 如果未查到passport则根据已有的moile或email创建passport
//    2.2.3 使用已有的passport来判断passport 与微信weixinid 的映射关系是否存在，如果存在则[同步创建passport及映射关系]的工作停止,如果不存在则创建映射关系

    /**
     * 执行同步passport的策略
     * @param wxUser
     * @return
     */
    public JSONObject doSynPassport(JSONObject wxUser) throws ServiceException {
        int status = wxUser.getIntValue("status");
        String  weixinid = wxUser.getString("weixinid");
        String  email = wxUser.getString("email");
        String  mobile = wxUser.getString("mobile");
        String  userId = wxUser.getString("userid");
        boolean doExecute = false;
        long passportId = 0L;
        String errorMsg = "";
        //只有状态为1,weixinid不为空 且 （email 或者mobile不为空的 ）的才进行逻辑处理
            JSONObject jsonUtilInfo =  WxUtils.wxUserPassportLoginInfo(mobile,email,weixinid);
            String loginName = jsonUtilInfo.getString("loginName");
            Passport passport = null;
            RelThirdPassportXsyPassportId relThirdPassportXsyPassportId = getRelThirdPassportXsyPassportIdByInfo(weixinid,mobile,email);

            if(relThirdPassportXsyPassportId == null){
                passport =  commonService._doCreateNewPassport(loginName);
                passportId = passport.getId();
                //  创建第三方个人ID与销售易用户的id的对应关系
                createAndSaveRelThirdPassportXsyPassportIdForWx(weixinid, mobile, email, passportId);
            }else{
                passportId = relThirdPassportXsyPassportId.getPassportId();
                //  创建第三方个人ID与销售易用户的id的对应关系
                createAndSaveRelThirdPassportXsyPassportIdForWx(weixinid, mobile, email, passportId);
            }
            doExecute = true;

        JSONObject executeResult = new JSONObject();
        executeResult.put("doExecute",doExecute); //只有状态为1,weixinid不为空 且 （email 或者mobile不为空的 ）的才进行逻辑处理
        executeResult.put("passportId",passportId);
        executeResult.put("errorMsg",errorMsg);
        return executeResult;
    }

    public JSONObject doSynPassport4Manager(String weixinid  , String manager_email,String manager_mobile) throws ServiceException {
        long passportId = 0L;
        JSONObject jsonUtilInfo =  WxUtils.wxUserPassportLoginInfo(manager_mobile,manager_email,null);
        String loginName = jsonUtilInfo.getString("loginName");
        Passport passport = null;
        RelThirdPassportXsyPassportId relThirdPassportXsyPassportId = getRelThirdPassportXsyPassportIdByInfo(weixinid,manager_mobile,manager_email);

        if(relThirdPassportXsyPassportId == null){
            passport =  commonService._doCreateNewPassport(loginName);
            passportId = passport.getId();
            //  创建第三方个人ID与销售易用户的id的对应关系
            createAndSaveRelThirdPassportXsyPassportIdForWx(weixinid, manager_mobile,manager_email, passportId);
        }else{
            passportId = relThirdPassportXsyPassportId.getPassportId();
            //  创建第三方个人ID与销售易用户的id的对应关系
            createAndSaveRelThirdPassportXsyPassportIdForWx(weixinid, manager_mobile,manager_email, passportId);
        }
        JSONObject executeResult = new JSONObject();
        executeResult.put("passportId",passportId);
        return executeResult;
    }

    public RelThirdPassportXsyPassportId getRelThirdPassportXsyPassportIdByInfo(String  weixinid,String  mobile ,String  email) throws ServiceException {
        RelThirdPassportXsyPassportId relThirdPassportXsyPassportId = null;
        if(relThirdPassportXsyPassportId == null  && StringUtils.isNotBlank(weixinid)) {
            relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(weixinid, Platform.WEIXIN, WXAppConstant.APP_ID);
        }
        if(relThirdPassportXsyPassportId == null  && StringUtils.isNotBlank(mobile)){
            relThirdPassportXsyPassportId = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(mobile, Platform.WEIXIN, WXAppConstant.APP_ID);

        }
        if(relThirdPassportXsyPassportId == null && StringUtils.isNotBlank(email)){
            relThirdPassportXsyPassportId = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(email, Platform.WEIXIN, WXAppConstant.APP_ID);
        }
        return relThirdPassportXsyPassportId;
    }

    /**
     * 微信接口，供给微信创建关联关系使用
     * 使用 weixinid，mobile，email分别建立一套映射关系
     * @param weixinid
     * @param mobile
     * @param email
     * @param passportId
     * @throws ServiceException
     */
    public void createAndSaveRelThirdPassportXsyPassportIdForWx(String weixinid,String mobile,String email,long passportId) throws ServiceException {
        RelThirdPassportXsyPassportId rel = null;
        boolean isBlank = false;

        if(  StringUtils.isNotBlank(weixinid)) {
            rel = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(weixinid, Platform.WEIXIN, WXAppConstant.APP_ID);
        }else {
            isBlank = true;
        }
        if(!isBlank){
            if(rel == null){
                rel = new RelThirdPassportXsyPassportId();
            }
            rel.setThirdSource(Platform.WEIXIN);//来源：微信
            rel.setPassportId(passportId);
            rel.setThirdPlatformId(weixinid);
            rel.setSuiteKey(WXAppConstant.APP_ID);
            if(rel.getId() == null ||rel.getId().longValue() ==0 ){
                relThirdPassportXsyPassportIdService.save(rel);
            }else {
                relThirdPassportXsyPassportIdService.update(rel);
            }
        }

        rel = null;
        isBlank = false;
        if(  StringUtils.isNotBlank(mobile)) {
            rel = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(mobile, Platform.WEIXIN, WXAppConstant.APP_ID);
        }else {
            isBlank = true;
        }
        if(!isBlank){
            if(rel == null){
                rel = new RelThirdPassportXsyPassportId();
            }
            rel.setThirdSource(Platform.WEIXIN);//来源：微信
            rel.setPassportId(passportId);
            rel.setThirdPlatformId(mobile);
            rel.setSuiteKey(WXAppConstant.APP_ID);
            if(rel.getId() == null ||rel.getId().longValue() ==0 ){
                relThirdPassportXsyPassportIdService.save(rel);
            }else {
                relThirdPassportXsyPassportIdService.update(rel);
            }
        }


        rel = null;
        isBlank = false;
        if(  StringUtils.isNotBlank(email)) {
            rel = relThirdPassportXsyPassportIdService.getXsyPassportByThirdIdAndSource(email, Platform.WEIXIN, WXAppConstant.APP_ID);
        }else {
            isBlank = true;
        }
        if(!isBlank){
            if(rel == null){
                rel = new RelThirdPassportXsyPassportId();
            }
            rel.setThirdSource(Platform.WEIXIN);//来源：微信
            rel.setPassportId(passportId);
            rel.setThirdPlatformId(email);
            rel.setSuiteKey(WXAppConstant.APP_ID);
            if(rel.getId() == null ||rel.getId().longValue() ==0 ){
                relThirdPassportXsyPassportIdService.save(rel);
            }else {
                relThirdPassportXsyPassportIdService.update(rel);
            }
        }

    }
}
