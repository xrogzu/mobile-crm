package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.Env;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.OApiResultException;
import com.rkhd.ienterprise.apps.ingage.dingtalk.helper.utils.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DepartmentHelper {
	private  static Logger LOG = LoggerFactory.getLogger(DepartmentHelper.class);
	public static long createDepartment(String accessToken, String name, 
			String parentId, String order, boolean createDeptGroup ) throws OApiException {
		String url = Env.OAPI_HOST + "/department/create?" +
				"access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("name", name);
		args.put("parentid", parentId);
		args.put("order", order);
		args.put("createDeptGroup", createDeptGroup);
		JSONObject response = HttpHelper.httpPost(url, args);
		if (response.containsKey("id")) {
			return response.getLong("id");
		}
		else {
			throw new OApiResultException("id");
		}
	}

	
	public static List<Department> listDepartments(String accessToken) 
			throws OApiException {
		String url = Env.OAPI_HOST + "/department/list?" +
				"access_token=" + accessToken;
		JSONObject response = HttpHelper.httpGet(url);
		LOG.info("查询到钉钉的部门列表为{}", response.toJSONString());
		if (response.containsKey("department")) {
			JSONArray arr = response.getJSONArray("department");
			List<Department> list  = new ArrayList<Department>();
			for (int i = 0; i < arr.size(); i++) {
				list.add(arr.getObject(i, Department.class));
			}
			return list;
		}
		else {
			throw new OApiResultException("department");
		}
	}
	
	
	public static void deleteDepartment(String accessToken, Long id) throws OApiException{
		String url = Env.OAPI_HOST  + "/department/delete?" +
				"access_token=" + accessToken + "&id=" + id;
		HttpHelper.httpGet(url);
	}
	
	
	public static void updateDepartment(String accessToken, String name, 
			String parentId, String order, long id,
			boolean autoAddUser, String deptManagerUseridList, boolean deptHiding, String deptPerimits) throws OApiException{
		String url = Env.OAPI_HOST  + "/department/update?" +
				"access_token=" + accessToken;
		JSONObject args = new JSONObject();
		args.put("name", name);
		args.put("parentid", parentId);
		args.put("order", order);
		args.put("id",id);
		args.put("autoAddUser",autoAddUser);
		args.put("deptManagerUseridList",deptManagerUseridList);
		args.put("deptHiding",deptHiding);
		args.put("deptPerimits",deptPerimits);

		HttpHelper.httpPost(url, args);
	}
	public static Department get(String accessToken,String dingDepartmentId)
			throws OApiException {
		String url = Env.OAPI_HOST + "/department/get?" +
				"access_token=" + accessToken +"&id="+dingDepartmentId;
		JSONObject response = HttpHelper.httpGet(url);
		LOG.info("ding return data is {}",response.toJSONString());
		if(response.getLong("errcode").longValue() == 0){

			Department department = new Department();
//			department.setName(response.getString("name"));
//			department.setParentid(response.getString("parentid"));
//			department.setId(dingDepartmentId);
			department = JSON.parseObject(response.toJSONString(),Department.class);
			// 其余的暂时不用，如果需要则可以补充
			return department;
		} else {
			throw new OApiResultException("department");
		}
	}
}
