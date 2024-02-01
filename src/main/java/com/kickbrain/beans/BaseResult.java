package com.kickbrain.beans;

public class BaseResult {
	private int status;
	private String errorMessage;
	private String errorMessageAr;
	private String errorCode;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessageAr() {
		return errorMessageAr;
	}

	public void setErrorMessageAr(String errorMessageAr) {
		this.errorMessageAr = errorMessageAr;
	}

}
