package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.eventchange;

import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiResultException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.HttpHelper;


import java.util.List;

public class EventChangeHelper {
	//注册事件回调接口
	public static String registerEventChange(String accessToken, List<String> callBackTag, String token, String aesKey, String url) throws OApiException {
		String signUpUrl = Env.OAPI_HOST + "/call_back/register_call_back?" +
				"access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("call_back_tag", callBackTag);
		args.put("token", token);
		args.put("aes_key", aesKey);
		args.put("url", url);

		JSONObject response = HttpHelper.httpPost(signUpUrl, args);
		if(response == null){
			throw new OApiException(0, "回调返回结果为空");
		}
		return response.toString();
	}
	//查询事件回调接口
	public static String getEventChange(String accessToken) throws OApiException{
		String url = Env.OAPI_HOST + "/call_back/get_call_back?" +
				"access_token=" + accessToken;
		JSONObject response = HttpHelper.httpGet(url);
		return response.toString();
	}
	//更新事件回调接口
	public static String updateEventChange(String accessToken, List<String> callBackTag, String token, String aesKey, String url) throws OApiException{
		String signUpUrl = Env.OAPI_HOST + "/call_back/update_call_back?" +
				"access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("call_back_tag", callBackTag);
		args.put("token", token);
		args.put("aes_key", aesKey);
		args.put("url", url);

		JSONObject response = HttpHelper.httpPost(signUpUrl, args);
		if (response.containsKey("errcode")) {
			return response.getString("errcode");
		}
		else {
			throw new OApiResultException("errcode");
		}
	}
	//删除事件回调接口
	public static String deleteEventChange(String accessToken) throws OApiException{
		String url = Env.OAPI_HOST + "/call_back/delete_call_back?" +
				"access_token=" + accessToken;
		JSONObject response = HttpHelper.httpGet(url);
		return response.toString();
	}

	
	public static String getFailedResult(String accessToken) throws OApiException{
		String url = Env.OAPI_HOST + "/call_back/get_call_back_failed_result?" +
				"access_token=" + accessToken;
		JSONObject response = HttpHelper.httpGet(url);
		return response.toString();
	}
	
	
}
