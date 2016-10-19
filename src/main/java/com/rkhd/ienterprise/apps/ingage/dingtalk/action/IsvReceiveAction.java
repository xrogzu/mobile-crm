package com.rkhd.ienterprise.apps.ingage.dingtalk.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.aes.DingTalkEncryptException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.aes.DingTalkEncryptor;
import com.rkhd.ienterprise.apps.ingage.services.CommonService;
import com.rkhd.ienterprise.apps.ingage.services.DingService;
import com.rkhd.ienterprise.apps.ingage.services.IsvReceiveService;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.UpdateCorpTokenThread;
import com.rkhd.ienterprise.exception.ServiceException;
import com.rkhd.ienterprise.thirdparty.service.RelThirdTokenService;
import com.rkhd.platform.exception.PaasException;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by hougx on 2015/12/2.
 */
@Namespace("/dingtalk")
public class IsvReceiveAction extends BaseAction{
    private  static Logger LOG = LoggerFactory.getLogger(IsvReceiveAction.class);
    @Autowired
    private IsvReceiveService isvReceiveService;

    @Autowired
    private DingService dDingService;

    @Autowired
    private RelThirdTokenService relThirdTokenService;



    @Autowired
    @Qualifier("mwebCommonService")
    private CommonService commonService;

    @Autowired
    private DingService dingService;




    private String loginName;
   // private String captcha;
    private String password;

    //private String graphCode;

   // private String sessionID;
    private Map<String, String> application;
   // private Integer tenantType = CommonConstancts.TenantType.STANDARD;

    /**
     * 应用回调函数
     * @return
     * @throws Exception
     */
    @Action(value = "t", results = {@Result(name = SUCCESS, location = "/WEB-INF/pages/common/infoPage.jsp")})
    public String test1() throws Exception{
        String method = getRequest().getMethod();
        LOG.info("###############method:"+method);
        if("post".equalsIgnoreCase(method)){
            return doPost();
        }else{
            return doGet();
        }

    }

    protected String doGet() throws ServletException, IOException, OApiException,ServiceException {
        LOG.info("doGet" + ":let's rock!");
        return executeAction();
    }

    protected String doPost() throws ServletException, IOException, OApiException,ServiceException {
        // TODO Auto-generated method stub
        LOG.info("doPost" + ":let's rock!");
        return executeAction();
    }

    private long time1 = 0;
    public String executeAction( ) throws IOException, OApiException,ServiceException {
        /**url中的签名**/
        String msgSignature = getRequest().getParameter("signature");
        /**url中的时间戳**/
        String timeStamp = getRequest().getParameter("timestamp");
        /**url中的随机字符串**/
        String nonce = getRequest().getParameter("nonce");

        /**post数据包数据中的加密数据**/
        ServletInputStream sis =  getRequest().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(sis));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        JSONObject jsonEncrypt = JSONObject.parseObject(sb.toString());
        String encrypt = jsonEncrypt.getString("encrypt");

        /**对encrypt进行解密**/
        DingTalkEncryptor dingTalkEncryptor = null;
        String plainText = null;
        DingTalkEncryptException dingTalkEncryptException = null;
        boolean isOk = false;
        try {
            //对于DingTalkEncryptor的第三个参数，ISV进行配置的时候传对应套件的SUITE_KEY，普通企业传Corpid,由于此回调接口只有isv使用，所以传Env.SUITE_KEY
            dingTalkEncryptor = new DingTalkEncryptor(Env.TOKEN, Env.ENCODING_AES_KEY, Env.SUITE_KEY);
            plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
            isOk = true;
        } catch (DingTalkEncryptException e) {
            // TODO Auto-generated catch block
            dingTalkEncryptException = e;
//			LOG.info(e.getMessage());
//			e.printStackTrace();
        }finally{
            if(dingTalkEncryptException != null){
                if( dingTalkEncryptException.code == DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_CORPID_ERROR){
                    try {
                        dingTalkEncryptor = new DingTalkEncryptor(Env.TOKEN, Env.ENCODING_AES_KEY, Env.CREATE_SUITE_KEY);
                        plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
                        isOk = true;
                    } catch (DingTalkEncryptException e) {
                        // TODO Auto-generated catch block
                        LOG.error(e.getMessage());
//                        e.printStackTrace();
                    }
                }
                else{
                    LOG.error(dingTalkEncryptException.getMessage());
//                    dingTalkEncryptException.printStackTrace();
                }
            }
        }
        if(isOk){
        /**对从encrypt解密出来的明文进行处理**/
        JSONObject plainTextJson = JSONObject.parseObject(plainText);
        LOG.info("plainText:"+plainText);
        String eventType = plainTextJson.getString("EventType");
        LOG.info("eventType:" + eventType);
        String res = "success";
        /**
         *
         * 定时推送Ticket
         */
        if("suite_ticket".equalsIgnoreCase(eventType)) {
            //case "suite_ticket"://b: 定时推送Ticket
            String suiteTicket = plainTextJson.getString("SuiteTicket");//do something
            LOG.info("获取到新的suiteTicket={}",suiteTicket);
            //持久化到数据库,
            dDingService.setSuiteTicket(suiteTicket);
             dDingService.updatePersistSuite_access_token();
            //更新企业的accessToken和jsTicket ,利用Ticket的定时机制进行更新token
            UpdateCorpTokenThread updateCorpTokenThread = new UpdateCorpTokenThread(relThirdTokenService, commonService, dingService);
            updateCorpTokenThread.start();
        }else if("tmp_auth_code".equalsIgnoreCase(eventType)){
            // case "tmp_auth_code"://c:回调向ISV推送临时授权码

            String tmp_auth_code = plainTextJson.getString("AuthCode");//do something
            LOG.info("临时授权码" + tmp_auth_code);
            if(time1 == 0 ){
                time1 = System.currentTimeMillis();
            }else{
                LOG.info("间隔时间间隔时间为"+(System.currentTimeMillis() -time1));
            }
            try {
                String requestUrl = (String) getRequest().getAttribute("requestUrl");
                //企业授权后
                String  contextPath =  getRequest().getContextPath();
                isvReceiveService.onTmp_auth_code(tmp_auth_code,requestUrl,contextPath);


            } catch (ServiceException e) {
                e.printStackTrace();
            }catch (OApiException e){
                e.printStackTrace();
            } catch (PaasException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();;
            }
            // break;
        }else if("change_auth".equalsIgnoreCase(eventType)){
            // case "change_auth"://d:回调向ISV推送授权变更消息
//                String corpid = plainTextJson.getString("AuthCorpId");
//                String suiteKey = plainTextJson.getString("SuiteKey");
//                LOG.info("corpId:"+corpid);
//                LOG.info("suiteKey:"+suiteKey);
//
//                String permanentCode = SetSystemProp.getKeyValue("permanentCode_"+corpid);
//                LOG.info("suiteAccessToken="+ dDingService.getSuiteAccessToken());
//                LOG.info("permanentCode="+ permanentCode);
//                LOG.info("Env.SUITE_KEY="+ Env.SUITE_KEY);
//                LOG.info("corpid="+ corpid);
//            JSONObject responseJson =  ServiceHelper.getAgent(dDingService.getSuiteAccessToken(),suiteKey,corpid,permanentCode,"756");
//            LOG.info("change_auth responseJson = {}",responseJson);
//            String close =  responseJson.getString("close");
//            LOG.info("change_auth close = {}",close);

//               JSONObject jso =  ServiceHelper.getAuthInfo(dDingService.getSuiteAccessToken(), Env.SUITE_KEY, corpid,permanentCode);
//                LOG.info("json:"+jso.toString());
//
//                JSONArray jsonArray = jso.getJSONObject("auth_info").getJSONArray("agent");
//                for(int i=0;i<jsonArray.size();i++){
//                   // String agentNameTemp = jsonArray.getJSONObject(i).getString("agent_name");
//                       String  agentId = jsonArray.getJSONObject(i).getString("agentid");
//                    LOG.info("agentId:"+agentId);
//                      // String  appId = jsonArray.getJSONObject(i).getString("appid");
//                    JSONObject agentJson =  ServiceHelper.getAgent(dDingService.getSuiteAccessToken(),Env.SUITE_KEY,corpid,permanentCode,agentId);
//                    Integer closeTemp = agentJson.getInteger("close");
//                    if(closeTemp == 2){
//                        ServiceHelper.getActivateSuite(dDingService.getSuiteAccessToken(), Env.SUITE_KEY, corpid, permanentCode);
//                    }
//                }
            //由于以下操作需要从持久化存储中获得数据，而本demo并没有做持久化存储（因为部署环境千差万别），所以没有具体代码，只有操作指导。
            //1.根据corpid查询对应的permanent_code(永久授权码)
            //2.调用『企业授权的授权数据』接口（ServiceHelper.getAuthInfo方法），此接口返回数据具体详情请查看文档。
            //3.遍历从『企业授权的授权数据』接口中获取所有的微应用信息
            //4.对每一个微应用都调用『获取企业的应用信息接口』（ServiceHelper.getAgent方法）
			/*5.获取『获取企业的应用信息接口』接口返回值其中的"close"参数，才能得知微应用在企业用户做了授权变更之后的状态，有三种状态码，分别为0，1，2.含义如下：
				0:禁用（例如企业用户在OA后台禁用了微应用）
				1:正常 (例如企业用户在禁用之后又启用了微应用)
				2:待激活 (企业已经进行了授权，但是ISV还未为企业激活应用)
				再根据具体状态做具体操作。
				比如状态为0，可以不做任何操作，
				比如状态为2，就需要ISV为企业进行激活授权套件的操作。
			 */

            // break;
        }else if("check_create_suite_url".equalsIgnoreCase(eventType)) {
            //case "check_create_suite_url"://a.验证回调URL有效性事件
            res = plainTextJson.getString("Random");

            String testSuiteKey = plainTextJson.getString("TestSuiteKey");
            LOG.info("testSuiteKey:" + testSuiteKey);
            //  break;
        }else if("check_update_suite_url".equalsIgnoreCase(eventType)) {
            // case "check_update_suite_url"://e."套件信息更新"事件
            res = plainTextJson.getString("Random");
            // break;
        }else{
            // default : //do something
            // break;
        }

        /**对返回信息进行加密**/
        long timeStampLong = Long.parseLong(timeStamp);
        Map<String,String> jsonMap = null;
        try {
            jsonMap = dingTalkEncryptor.getEncryptedMap(res, timeStampLong, nonce);
        } catch (DingTalkEncryptException e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.putAll(jsonMap);
        // getResponse().getWriter().append(json.toString());
        LOG.info("返回结果为：{}", JSON.toJSONString(json));
        getRequest().setAttribute("returnStr",json.toJSONString());
        }
        return SUCCESS;
    }

}
