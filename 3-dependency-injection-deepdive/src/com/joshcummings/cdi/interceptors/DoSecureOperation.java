package com.joshcummings.cdi.interceptors;

public class DoSecureOperation {
	/**
	 * This informs CDI to call the SecureInterceptor before calling the method.
	 */
	@Secure
	public void superSecureOperation() {
		System.out.println("Need auth to do this...");
	}
}
