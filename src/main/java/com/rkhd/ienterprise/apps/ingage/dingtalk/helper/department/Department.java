package com.rkhd.ienterprise.apps.ingage.dingtalk.helper.department;

import java.io.Serializable;

public class Department implements Serializable {



	public String id;
	public String name;
	public String parentid;
	private boolean deptHiding;
	private String  outerPermitDepts;
	private String  deptManagerUseridList;
	private long  errcode;
	private String outerPermitUsers;
	private boolean createDeptGroup;
	private long order;
	private boolean outerDept;
	private String orgDeptOwner;
	private boolean autoAddUser;
	private String  deptPerimits;
	private String userPerimits;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public boolean isDeptHiding() {
		return deptHiding;
	}

	public void setDeptHiding(boolean deptHiding) {
		this.deptHiding = deptHiding;
	}

	public String getOuterPermitDepts() {
		return outerPermitDepts;
	}

	public void setOuterPermitDepts(String outerPermitDepts) {
		this.outerPermitDepts = outerPermitDepts;
	}

	public String getDeptManagerUseridList() {
		return deptManagerUseridList;
	}

	public void setDeptManagerUseridList(String deptManagerUseridList) {
		this.deptManagerUseridList = deptManagerUseridList;
	}

	public long getErrcode() {
		return errcode;
	}

	public void setErrcode(long errcode) {
		this.errcode = errcode;
	}

	public String getOuterPermitUsers() {
		return outerPermitUsers;
	}

	public void setOuterPermitUsers(String outerPermitUsers) {
		this.outerPermitUsers = outerPermitUsers;
	}

	public boolean isCreateDeptGroup() {
		return createDeptGroup;
	}

	public void setCreateDeptGroup(boolean createDeptGroup) {
		this.createDeptGroup = createDeptGroup;
	}

	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}

	public boolean isOuterDept() {
		return outerDept;
	}

	public void setOuterDept(boolean outerDept) {
		this.outerDept = outerDept;
	}

	public String getOrgDeptOwner() {
		return orgDeptOwner;
	}

	public void setOrgDeptOwner(String orgDeptOwner) {
		this.orgDeptOwner = orgDeptOwner;
	}

	public boolean isAutoAddUser() {
		return autoAddUser;
	}

	public void setAutoAddUser(boolean autoAddUser) {
		this.autoAddUser = autoAddUser;
	}

	public String getDeptPerimits() {
		return deptPerimits;
	}

	public void setDeptPerimits(String deptPerimits) {
		this.deptPerimits = deptPerimits;
	}

	public String getUserPerimits() {
		return userPerimits;
	}

	public void setUserPerimits(String userPerimits) {
		this.userPerimits = userPerimits;
	}

	@Override
	public String toString() {
		return "Department[id:" + id + ", name:" + name + ", parentId:" + parentid + "]";
	}
}
