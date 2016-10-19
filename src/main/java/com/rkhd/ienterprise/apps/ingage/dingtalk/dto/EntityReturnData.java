package com.rkhd.ienterprise.apps.ingage.dingtalk.dto;

import java.io.Serializable;

public class EntityReturnData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3691383085130219034L;
	/**
	 * 成功与否
	 */
	private boolean success = false;
	/**
	 * 操作结果 
	 */
	private String msg;
	
	/**
	 * 前台标识 
	 */
	private String errortype;
	/**
	 * 返回对象
	 */
	private Object entity;

	/**
	 * 错误编码
	 */
	private String errorCode;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the entity
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public String getErrortype() {
		return errortype;
	}

	public void setErrortype(String errortype) {
		this.errortype = errortype;
	}


	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
