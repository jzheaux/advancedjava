package com.joshcummings.ws.auction;

public class User {
	private final Long id;
	private final String username;
	private final char[] password;
	
	public User(Long id, String username, char[] password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	/** 
	 * Potential security risk! We could accidentally expose the user's password
	 * across the wire.
	 * 
	 * @return
	 */
	public char[] getPassword() {
		return password;
	}
}
