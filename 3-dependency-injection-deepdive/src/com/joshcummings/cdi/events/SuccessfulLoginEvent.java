package com.joshcummings.cdi.events;

import java.time.Instant;

public class SuccessfulLoginEvent {
	private final String username;
	private final Instant when;
	
	public SuccessfulLoginEvent(String username, Instant when) {
		this.username = username;
		this.when = when;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Instant getWhen() {
		return when;
	}
}