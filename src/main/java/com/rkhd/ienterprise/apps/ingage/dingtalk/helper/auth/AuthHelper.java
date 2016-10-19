package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.auth;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DingSessionUser;
import com.rkhd.ienterprise.apps.ingage.dingtalk.enums.Sessionkes;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiResultException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service.ServiceHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.HttpHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.ServiceUtil;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SessionUtils;
import com.rkhd.ienterprise.apps.ingage.dingtalk.util.SystemGlobals;
import com.rkhd.ienterprise.thirdparty.model.RelThirdToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Timer;

public class AuthHelper {
	private  static Logger LOG = LoggerFactory.getLogger(AuthHelper.class);
	public static String jsapiTicket = null;
	public static Timer timer = null;
	public static final Integer cacheTime = 1000 * 60 * 60 * 2;
	public static long currentTime = 0 + cacheTime +1;
	public static long lastTime = 0;


	public static String getAccessToken() throws OApiException {
		String ding_accessToken = null;
		if(lastTime != 0){
			currentTime = System.currentTimeMillis();
		}
		if(currentTime - lastTime >= cacheTime){
			String url = Env.OAPI_HOST + "/gettoken?" +
					"corpid=" + Env.CORP_ID + "&corpsecret=" + Env.SECRET;
			JSONObject response = HttpHelper.httpGet(url);
			if (response.containsKey("access_token")) {
				ding_accessToken = response.getString("access_token");

			}
			else {
				throw new OApiResultException("access_token");
			}

			String url_ticket = Env.OAPI_HOST + "/get_jsapi_ticket?" +
					"type=jsapi" + "&access_token=" + ding_accessToken;
			JSONObject response_ticket = HttpHelper.httpGet(url_ticket);
			if (response_ticket.containsKey("ticket")) {
				jsapiTicket = response_ticket.getString("ticket");
				currentTime = System.currentTimeMillis();
				lastTime = System.currentTimeMillis();
			}
			else {
				throw new OApiResultException("ticket");
			}

		}else{
			return ding_accessToken;
		}

		return ding_accessToken;
	}
	//正常的情况下，jsapi_ticket的有效期为7200秒，所以开发者需要在某个地方设计一个定时器，定期去更新jsapi_ticket

	/**
	 * 获取jsapi_ticket
	 * @param accessToken
	 * @return
	 * @throws OApiException
	 */
	public static String getJsapiTicket(String accessToken) throws OApiException {
		//if (jsapiTicket == null){
		String url = Env.OAPI_HOST + "/get_jsapi_ticket?" +
				"type=jsapi" + "&access_token=" + accessToken;
		JSONObject response = HttpHelper.httpGet(url);
		if (response.containsKey("ticket")) {
			jsapiTicket = response.getString("ticket");
			return jsapiTicket;
		}
		else {
			throw new OApiResultException("ticket");
		}
		//}else{
		//return jsapiTicket;
		//}
	}

	public static String sign(String ticket, String nonceStr, long timeStamp, String url)
			throws OApiException {
		String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr +
				"&timestamp=" + String.valueOf(timeStamp) + "&url=" + url;
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			sha1.reset();
			sha1.update(plain.getBytes("UTF-8"));
			return bytesToHex(sha1.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new OApiResultException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new OApiResultException(e.getMessage());
		}
	}

	private static String bytesToHex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash){
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String getConfig(HttpServletRequest request){
		LOG.info("authHelper getConfig start  ######################");
		long beginTime = System.currentTimeMillis();
		String dingConfig = null;
		String queryString = request.getQueryString();
		String corpId = null;
		DingSessionUser dingSessionUser  = (DingSessionUser) request.getSession().getAttribute(Sessionkes.SESSIONUSER.toString());
		if(SessionUtils.needReLogin(request)){
			corpId = request.getParameter("corpid");
			request.getSession().removeAttribute("agentId");
			request.getSession().removeAttribute("appId");
		}else{
			corpId = dingSessionUser.getThirdcorpid();
		}
		LOG.info("corpId={} ",corpId);
		String urlString = (String)request.getAttribute("requestUrl");
		LOG.info("urlString={} ",urlString);
		String queryStringEncode = null;
		String url;
		if(queryString != null){
			queryStringEncode = URLDecoder.decode(queryString);
			url = urlString + "?" + queryStringEncode;
		}
		else{
			url = urlString;
		}
//		LOG.info(url);
		String nonceStr = "xiaoshouyi";
		long timeStamp = System.currentTimeMillis()/1000;
		String signedUrl = url;
		String jsticket = null;
		String signature = null;
		String appId = null;
		String  agentId = null;
		try {
			RelThirdToken  relThirdToken = null;
			if(corpId == null){
				LOG.error("corpId is null , no autho to url:"+request.getRequestURI());
			}else {
				relThirdToken = ServiceUtil.getInstance().getRelThirdTokenByCorpId(corpId);
				if(relThirdToken == null){
					LOG.error("corpId["+corpId+"] Env.SUITE_KEY=["+Env.SUITE_KEY+"] can not find the relation by corpId between dingding and xiaoshouyi,"
							+"the reason may be data error or service down"
							+" or corpId not the real corp id .");
				}else{
					LOG.info("corpId["+corpId+"] get RelThirdToken by corpId and  result = "+(relThirdToken==null?"null": JSON.toJSONString(relThirdToken)));
					String permanentCode =  relThirdToken.getPermanentCode();

					String suite_access_token =  ServiceUtil.getInstance().getSuiteAccessToken();

					jsticket = relThirdToken.getJsapiTicket();
					signature = AuthHelper.sign(jsticket, nonceStr, timeStamp, signedUrl);

					agentId = (String) request.getSession().getAttribute("agentId");
 					appId = (String) request.getSession().getAttribute("appId");

					if(StringUtils.isBlank( agentId) || StringUtils.isBlank(appId)){
						JSONObject authInfoJson = ServiceHelper.getAuthInfo(suite_access_token,Env.SUITE_KEY,corpId,permanentCode);
						if(authInfoJson == null){
							LOG.error("corpId["+corpId+"] login get authInfoJson is null with suite_access_token:"+suite_access_token);
						}else {
							LOG.info("corpId["+corpId+"] login get authInfoJson is "+authInfoJson +" with suite_access_token:"+suite_access_token);
							JSONArray jsonArray = authInfoJson.getJSONObject("auth_info").getJSONArray("agent");

							for(int i=0;i<jsonArray.size();i++){
								agentId = jsonArray.getJSONObject(i).getString("agentid");
								appId = jsonArray.getJSONObject(i).getString("appid");
								LOG.info("agentId={} ,appId = {}",agentId,appId);
								break;
							}
							request.getSession().setAttribute("agentId",agentId);
							request.getSession().setAttribute("appId",appId);

						}
					}
					dingConfig = "{jsticket:'" + jsticket + "',signature:'"+signature
							+"',nonceStr:'"+nonceStr+"',timeStamp:'"+timeStamp
							+"',corpId:'"+corpId+"',agentId:'"+agentId+
							"',appId:'"+appId+"'}";
				}
			}
		} catch (Exception e) {
			LOG.error("msg={}",e.getMessage());
			e.printStackTrace();
		}

		LOG.info("dingConfig:"+dingConfig);
		long endTime = System.currentTimeMillis();

		return dingConfig;
	}

}
