package com.ryaltech.util;

public class WrapperException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public WrapperException(Throwable th) {
		super(th instanceof WrapperException?((WrapperException)th).getCause():th);
	}
}
