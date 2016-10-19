package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiResultException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.HttpHelper;
import com.rkhd.ienterprise.apps.ingage.dingtalk.threads.SyncAddressBookeThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;

public class UserHelper {
	private static final Logger LOG = LoggerFactory.getLogger(UserHelper.class);
	//创建成员
	public static void createUser(String accessToken, User user) throws OApiException {
		String url = Env.OAPI_HOST + "/user/create?" +
				"access_token=" + accessToken;
		HttpHelper.httpPost(url, user);
	}
	
	
	//更新成员
	public static void updateUser(String accessToken, User user) throws OApiException {
		String url = Env.OAPI_HOST + "/user/update?" +
				"access_token=" + accessToken;
		HttpHelper.httpPost(url, user);
	}
	
	
	//删除成员
	public static void deleteUser(String accessToken, String userid) throws OApiException {
		String url = Env.OAPI_HOST + "/user/delete?" +
				"access_token=" + accessToken + "&userid=" + userid;
		HttpHelper.httpGet(url);
	}
	
	
	//获取成员
	public static User getUser(String accessToken, String userid) throws OApiException {
		String url = Env.OAPI_HOST + "/user/get?" +
				"access_token=" + accessToken + "&userid=" + userid;
		JSONObject json = HttpHelper.httpGet(url);
		LOG.info("userJson={}",json);
		return JSON.parseObject(json.toJSONString(), User.class);
	}
	
	
	//批量删除成员
	public static void batchDeleteUser(String accessToken, List<String> useridlist) 
			throws OApiException {
		String url = Env.OAPI_HOST + "/user/batchdelete?" +
				"access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("useridlist", useridlist);
		HttpHelper.httpPost(url, args);
	}
	
	
	//获取部门成员
	public static List<User> getDepartmentUser(String accessToken, long department_id) 
			throws OApiException {
		String url = Env.OAPI_HOST + "/user/simplelist?" +
				"access_token=" + accessToken + "&department_id=" + department_id;
		JSONObject response = HttpHelper.httpGet(url);
		if (response.containsKey("userlist")) {
			List<User> list = new ArrayList<User>();
			JSONArray arr = response.getJSONArray("userlist");
			for (int i = 0; i < arr.size(); i++) {
				list.add(arr.getObject(i, User.class));
			}
			return list;
		}
		else {
			throw new OApiResultException("userlist");
		}
	}
	
	
	//获取部门成员（详情）
	public static List<User> getUserDetails(String accessToken, long department_id) 
			throws OApiException {
		String url = Env.OAPI_HOST + "/user/list?" +
				"access_token=" + accessToken + "&department_id=" + department_id;
		JSONObject response = HttpHelper.httpGet(url);
		if (response.containsKey("userlist")) {
			JSONArray arr = response.getJSONArray("userlist");
			List<User> list = new ArrayList<User>();
			for (int i = 0; i < arr.size(); i++) {
				list.add(arr.getObject(i, User.class));
			}
			return list;
		}
		else {
			throw new OApiResultException("userlist");
		}
	}
	public static JSONObject getUserInfo(String accessToken, String code) throws OApiException{
		
		String url = Env.OAPI_HOST + "/user/getuserinfo?" + "access_token=" + accessToken + "&code=" + code;
		JSONObject response = HttpHelper.httpGet(url);
		return response;
	}
	
	public static JSONObject getAgentUserInfo(String ssoToken, String code) throws OApiException{
		
		String url = Env.OAPI_HOST + "/sso/getuserinfo?" + "access_token=" + ssoToken + "&code=" + code;
		JSONObject response = HttpHelper.httpGet(url);
		return response;
	}

}
