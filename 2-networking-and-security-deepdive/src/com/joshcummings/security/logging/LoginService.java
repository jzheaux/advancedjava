package com.joshcummings.security.logging;

import org.apache.log4j.Logger;

public class LoginService {
	/**
	 * Change this out to use the ESAPI logger
	 */
	private static final Logger logger = Logger.getLogger(LoginService.class);
	
	public void login(String username, char[] password) {
		logger.error("Login [" + username + "]");
	}
	
	public static void main(String[] args) {
		new LoginService().login("dave", "dave".toCharArray());
		
		new LoginService().login("dave]\r\n2015-06-17 00:02:18 ERROR LoginService:9 - Login [harold", "dave".toCharArray());
		
		// looking at the logs, how many login events are there?
	}
}
