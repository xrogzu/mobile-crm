package com.rkhd.ienterprise.apps.ingage.wx.dtos;

import java.io.Serializable;

public class WxEntityReturnData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3691383085130219034L;
	/**
	 * 成功与否
	 */
	private boolean success = false;

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


	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
