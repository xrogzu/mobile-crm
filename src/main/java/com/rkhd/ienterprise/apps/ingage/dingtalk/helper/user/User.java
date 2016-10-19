package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.user;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class User {
	public String dingId; //只有激活了钉钉才会有
	public String userid;
	public String name;
	public boolean active;
	public String avatar;
	public List<Long> department;
	public String position;
	public String mobile;
	public String email;
	public String openId;
	public int status;
	private String isLeaderInDepts;
	private JSONObject extattr;
	private String  isAdmin;
	
	public User() {
	}
	
	public User(String userid, String name) {
		this.userid = userid;
		this.name = name;
	}

	public String getDingId() {
		return dingId;
	}

	public void setDingId(String dingId) {
		this.dingId = dingId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<Long> getDepartment() {
		return department;
	}

	public void setDepartment(List<Long> department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public JSONObject getExtattr() {
		return extattr;
	}

	public void setExtattr(JSONObject extattr) {
		this.extattr = extattr;
	}

	public String getIsLeaderInDepts() {
		return isLeaderInDepts;
	}

	public void setIsLeaderInDepts(String isLeaderInDepts) {
		this.isLeaderInDepts = isLeaderInDepts;
	}

	public String getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString() {
		List<User> users;
		return "User[dingId:"+ dingId + ", userid:" + userid + ", name:" + name + ", active:" + active + ", "
				+ "avatar:" + avatar + ", department:" + department +
				", position:" + position+ ", isLeaderInDepts:" + isLeaderInDepts + ", mobile:" + mobile + ", email:" + email +
				", openId:" + openId + ", status:" + status +", extattr:" + extattr +"]";
	}
}
