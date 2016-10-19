package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.service;


import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.dto.DSuiteAccessToken;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.auth.AuthHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceHelper {
	private  static Logger LOG = LoggerFactory.getLogger(ServiceHelper.class);
	/**
	 * 获取套件访问Token
	 * @param suite_key
	 * @param suite_secret
	 * @param suite_ticket
	 * @return
	 */
	public static DSuiteAccessToken getSuiteToken(String suite_key, String suite_secret, String suite_ticket) throws OApiException {
		String url = Env.OAPI_HOST + "/service/get_suite_token";
		JSONObject json = new JSONObject();
		json.put("suite_key", suite_key);
		json.put("suite_secret", suite_secret);
		json.put("suite_ticket", suite_ticket);
		JSONObject reponseJson = null;
		String suiteAccessToken = null;
		DSuiteAccessToken  dSuiteAccessToken = null;

		reponseJson = HttpHelper.httpPost(url, json);
		dSuiteAccessToken = new DSuiteAccessToken();
		dSuiteAccessToken.setExpires_in(reponseJson.getLong("expires_in"));
		dSuiteAccessToken.setSuite_access_token(reponseJson.getString("suite_access_token"));

		return dSuiteAccessToken;
	}

	/**
	 * 获取企业的永久授权码
	 * @param tmp_auth_cod
	 * @param suiteAccessToken
	 * @return
	 */
	public static JSONObject getPermanentCode(String tmp_auth_cod, String suiteAccessToken ) throws OApiException {
		String url = Env.OAPI_HOST + "/service/get_permanent_code?suite_access_token=" + suiteAccessToken ;
		JSONObject json = new JSONObject();
		json.put("tmp_auth_code", tmp_auth_cod);
		JSONObject reponseJson = null;
		reponseJson = HttpHelper.httpPost(url,json);

		return reponseJson;
	}

	/**
	 * 获取企业授权的access_token
	 * @param auth_corpid
	 * @param permanent_code
	 * @param suiteAccessToken
	 * @return
	 */
	public static String getCorpToken(String auth_corpid, String permanent_code, String suiteAccessToken ){
		String url = Env.OAPI_HOST + "/service/get_corp_token?suite_access_token=" + suiteAccessToken ;
		JSONObject json = new JSONObject();
		json.put("auth_corpid", auth_corpid);
		json.put("permanent_code", permanent_code);
		JSONObject reponseJson = null;
		String corpToken = null;
		try {
			reponseJson = HttpHelper.httpPost(url,json);
			corpToken = reponseJson.getString("access_token");

		} catch (OApiException e) {
			e.printStackTrace();
		}
		return corpToken;
	}

	/**
	 * 获取企业授权的授权数据
	 * @param suiteAccessToken
	 * @param suite_key
	 * @param auth_corpid
	 * @param permanent_code
	 * @return
	 */
	public static JSONObject getAuthInfo(String suiteAccessToken, String suite_key, String auth_corpid, String permanent_code){
		String url = Env.OAPI_HOST + "/service/get_auth_info?suite_access_token=" + suiteAccessToken ;
		JSONObject json = new JSONObject();
		json.put("suite_key", suite_key);
		json.put("auth_corpid", auth_corpid);
		json.put("permanent_code", permanent_code);

		JSONObject reponseJson = null;
		try {
			reponseJson = HttpHelper.httpPost(url,json);
		} catch (OApiException e) {
			LOG.error("getAuthInfo error ,msg="+e.getMessage()+";code:"+e.getErrorCode());
			e.printStackTrace();

		}
		return reponseJson;
	}

	/**
	 * 获取企业的应用信息
	 * @param suiteAccessToken
	 * @param suite_key
	 * @param auth_corpid
	 * @param permanent_code
	 * @param agentid
	 * @return
	 */
	public static JSONObject getAgent(String suiteAccessToken, String suite_key, String auth_corpid, String permanent_code, String agentid){
		String url = Env.OAPI_HOST + "/service/get_agent?suite_access_token=" + suiteAccessToken ;
		JSONObject json = new JSONObject();
		json.put("suite_key", suite_key);
		json.put("auth_corpid", auth_corpid);
		json.put("permanent_code", permanent_code);
		json.put("agentid", agentid);//agentid可以通过getAuthInfo返回的json中得到

		JSONObject reponseJson = null;
		try {
			reponseJson = HttpHelper.httpPost(url,json);
		} catch (OApiException e) {
			e.printStackTrace();
		}
		return reponseJson;
	}

	/**
	 * 激活授权套件
	 * @param suiteAccessToken
	 * @param suite_key
	 * @param auth_corpid
	 * @param permanent_code
	 * @return
	 */
	public static JSONObject getActivateSuite(String suiteAccessToken, String suite_key, String auth_corpid, String permanent_code ){
		String url = Env.OAPI_HOST + "/service/activate_suite?suite_access_token=" + suiteAccessToken ;
		JSONObject json = new JSONObject();
		json.put("suite_key", suite_key);
		json.put("auth_corpid", auth_corpid);
		json.put("permanent_code", permanent_code);

		JSONObject reponseJson = null;
		try {
			reponseJson = HttpHelper.httpPost(url,json);
		} catch (OApiException e) {
			e.printStackTrace();
		}
		return reponseJson;
	}


}
