package com.kujatas.model;

public class KjataException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;
	private Throwable cause;
	
	public KjataException(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}
	
	@Override
	public Throwable getCause() {
		return this.cause;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
