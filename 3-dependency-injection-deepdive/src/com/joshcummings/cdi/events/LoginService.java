package com.joshcummings.cdi.events;

import java.time.Instant;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @Named makes the object a candidate for injection
 */
@Named
public class LoginService {
	/**
	 * Give me an instance of the thing that knows how to fire login events.
	 */
	@Inject
	Event<SuccessfulLoginEvent> loginSuccess;
	
	public void login(String username, char[] password) {
		//...
		loginSuccess.fire(new SuccessfulLoginEvent(username, Instant.now()));
	}
}
