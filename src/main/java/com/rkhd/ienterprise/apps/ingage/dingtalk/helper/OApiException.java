package com.rkhd.ienterprise.apps.ingage.dingtalk.helper;

public class OApiException extends Exception {


	private static final long serialVersionUID = 5419224346400905526L;

	private int errorCode = 0;
	private String errMsg = null;
	public OApiException(int errCode, String errMsg) {
		super("error code: " + errCode + ", error message: " + errMsg);
		this.errMsg = errMsg;
		this.errorCode = errorCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
