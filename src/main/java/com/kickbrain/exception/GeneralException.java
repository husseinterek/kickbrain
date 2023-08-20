package com.kickbrain.exception;

public class GeneralException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	
	public GeneralException(String code) {
		super();
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
